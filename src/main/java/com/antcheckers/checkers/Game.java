package com.antcheckers.checkers;

import java.util.*;

public class Game implements Rules{

    private static final char[] INITIAL_BOARD =
            {'-', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', '-', '-', '-', '-', '-', '-',
             'b', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', 'w', '-', '-', '-', '-', '-'};

    private char[] board;
    private int boardValue;
    private Set<Game> gameNodes = new HashSet<>();
    private boolean playerWhite;
    private boolean maximizing;
    private int depth;

    public Game() {
        this.board = Arrays.copyOf(INITIAL_BOARD,BOARD_SIZE);
        this.playerWhite = true;
        this.depth = 0;
    }

    public Game(char[] board, int boardValue, boolean playerWhite, int depth) {
        this.board = board;
        this.boardValue = boardValue;
        this.playerWhite = playerWhite;
        this.depth = depth;
    }

    public void generateGameTree(int maxDepth) {
        if (depth < maxDepth) {
            if (playerWhite) {
                System.out.println("white turn, depth: " + depth);
                getAllWhiteActions();
                if (!gameNodes.isEmpty()){
                    boardStateInfoPrint();
                    for (Game gameNode : gameNodes) {
                        gameNode.generateGameTree(maxDepth);
                    }
                }
            } else {
                System.out.println("black turn, depth: " + depth);
                getAllBlackActions();
                if (!gameNodes.isEmpty()){
                    boardStateInfoPrint();
                    for (Game gameNode : gameNodes) {
                        gameNode.generateGameTree(maxDepth);
                    }
                }
            }
        }
    }

    public int evaluateBoardValue(char[] state) {
        int summary = 0;
        for (char field : state) {
            if (field == 'b')
                summary++;
            if (field == 'w')
                summary--;
        }
        return summary;
    }

    private void getAllBlackActions() {
        for (int position=0; position<BOARD_SIZE; position++){
            if (Rules.blackPawn(position, board)) {
                blackPawnAction(position, BOT_LEFT);
                blackPawnAction(position, BOT_RIGHT);
            }
        }
    }

    private void getAllWhiteActions() {
        for (int position=0; position<BOARD_SIZE; position++){
            if(Rules.whitePawn(position, board)) {
                whitePawnAction(position, TOP_LEFT);
                whitePawnAction(position, TOP_RIGHT);
            }
        }
    }

    private void blackPawnAction(int position, int move) {
        blackPawnMove(position, move);
        blackPawnKill(position, move, board);
    }

    private void whitePawnAction(int position, int move) {
        whitePawnMove(position, move);
        whitePawnKill(position, move, board);
    }

    private void blackPawnKill(int position, int move, char[] state) {
        char[] boardState = Arrays.copyOf(state,BOARD_SIZE);
        int killingPosition = position + move;
        int nextPosition = killingPosition + move;

        if (Rules.acceptableBlackPawnKill(killingPosition, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[killingPosition] = EMPTY;
            boardState[nextPosition] = BLACK_PAWN;

            blackPawnKill(nextPosition, BOT_LEFT, boardState);
            blackPawnKill(nextPosition, BOT_RIGHT, boardState);
            blackPawnKill(nextPosition, TOP_RIGHT, boardState);
            blackPawnKill(nextPosition, TOP_LEFT, boardState);
        }

        if (!state.equals(board)){
            gameNodes.add(new Game(boardState, evaluateBoardValue(boardState), true, depth+1));
        }
    }

    private void whitePawnKill(int position, int move, char[] state) {
        char[] boardState = Arrays.copyOf(state,BOARD_SIZE);
        int killingPosition = position + move;
        int nextPosition = killingPosition + move;

        if (Rules.acceptableWhitePawnKill(killingPosition, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[killingPosition] = EMPTY;
            boardState[nextPosition] = WHITE_PAWN;

            whitePawnKill(nextPosition, BOT_LEFT, boardState);
            whitePawnKill(nextPosition, BOT_RIGHT, boardState);
            whitePawnKill(nextPosition, TOP_RIGHT, boardState);
            whitePawnKill(nextPosition, TOP_LEFT, boardState);
        }

        if (!state.equals(board)){
            gameNodes.add(new Game(boardState, evaluateBoardValue(boardState), false, depth+1));
        }
    }

    private void blackPawnMove(int position, int move) {
        char[] boardState = Arrays.copyOf(board,BOARD_SIZE);
        int nextPosition = position + move;
        if(Rules.acceptableBlackPawnMove(position, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[nextPosition] = BLACK_PAWN;
            gameNodes.add(new Game(boardState, evaluateBoardValue(boardState), true, depth+1));
        }
    }

    private void whitePawnMove(int position, int move) {
        char[] boardState = Arrays.copyOf(board,BOARD_SIZE);
        int nextPosition = position + move;
        if(Rules.acceptableWhitePawnMove(position, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[nextPosition] = WHITE_PAWN;
            gameNodes.add(new Game(boardState, evaluateBoardValue(boardState), false, depth+1));
        }
    }

    public void boardStateInfoPrint() {
        printMe(board,"Start");
        System.out.println("Possible moves: " + gameNodes.size());
        for (Game game : gameNodes) {
            printMe(game.board, "Board state:");
            System.out.println("Board value: " + game.boardValue);
        }
    }

    private void printMe(char[] arr, String from) {
        System.out.println(from);
        for (int i = 0; i< arr.length; i++){
            System.out.print(arr[i]+" ");
            if (i% BOARD_EDGE_LENGTH ==7)
                System.out.println();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Arrays.equals(board, game.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
