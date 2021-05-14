package com.antcheckers.checkers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Blacks implements Rules {

    private char[] board;
    private Set<Game> gameNodes;
    private int depth;
    private boolean maximizing;

    public Blacks(char[] board, int depth, boolean maximizing) {
        this.board = board;
        this.gameNodes = new HashSet<>();
        this.depth = depth;
        this.maximizing = maximizing;
    }

    public Set<Game> getAllBlacksActions() {
        for (int position : GAME_FIELDS) {
            if (Rules.blackPawn(position, board)) {
                blackPawnAction(position, BOT_LEFT);
                blackPawnAction(position, BOT_RIGHT);
            } else if (Rules.blackQueen(position, board))
                for (int move : DIRECTIONS)
                    blackQueenAction(position, move);
        }
        return this.gameNodes;
    }

    private void blackPawnAction(int position, int move) {
        blackFigureMove(position, move, BLACK_PAWN);
        blackFigureKill(position, move, BLACK_PAWN, board);
    }

    private void blackQueenAction(int position, int move) {
        blackFigureMove(position, move, BLACK_QUEEN);
        blackFigureKill(position, move, BLACK_QUEEN, board);
    }

    private void blackFigureKill(int position, int move, char figure, char[] state) {
        char[] boardState = Arrays.copyOf(state,BOARD_SIZE);
        int killingPosition = position + move;
        int nextPosition = killingPosition + move;

        if (Rules.acceptableBlackFigureKill(killingPosition, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[killingPosition] = EMPTY;
            boardState[nextPosition] = figure;
            if (Rules.promotableBlackPawn(nextPosition, boardState))
                boardState[nextPosition] = BLACK_QUEEN;
            gameNodes.add(new Game(boardState, true, depth+1, !maximizing));

            for (int nextMove : DIRECTIONS)
                blackFigureKill(nextPosition, nextMove, figure, boardState);
        }
    }

    private void blackFigureMove(int position, int move, char figure) {
        char[] boardState = Arrays.copyOf(board,BOARD_SIZE);
        int nextPosition = position + move;

        if(Rules.acceptableMove(position, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[nextPosition] = figure;
            if (Rules.promotableBlackPawn(nextPosition, boardState))
                boardState[nextPosition] = BLACK_QUEEN;
            gameNodes.add(new Game(boardState, true, depth+1, !maximizing));
        }
    }
}
