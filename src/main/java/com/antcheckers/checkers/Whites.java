package com.antcheckers.checkers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Whites implements Rules{

    private char[] board;
    private Set<Game> gameNodes;
    private int depth;
    private boolean maximizing;

    public Whites(char[] board, int depth, boolean maximizing) {
        this.board = board;
        this.gameNodes = new HashSet<>();
        this.depth = depth;
        this.maximizing = maximizing;
    }

    public Set<Game> getAllWhitesActions() {
        for (int position : GAME_FIELDS) {
            if (Rules.whitePawn(position, board)) {
                whitePawnAction(position, TOP_LEFT);
                whitePawnAction(position, TOP_RIGHT);
            } else if(Rules.whiteQueen(position, board))
                for (int move : DIRECTIONS)
                    whiteQueenAction(position, move);
        }
        return this.gameNodes;
    }

    private void whitePawnAction(int position, int move) {
        whiteFigureMove(position, move, WHITE_PAWN);
        whiteFigureKill(position, move, WHITE_PAWN, board);
    }

    private void whiteQueenAction(int position, int move) {
        whiteFigureMove(position, move, WHITE_QUEEN);
        whiteFigureKill(position, move, WHITE_QUEEN, board);
    }

    private void whiteFigureKill(int position, int move, char figure, char[] state) {
        char[] boardState = Arrays.copyOf(state,BOARD_SIZE);
        int killingPosition = position + move;
        int nextPosition = killingPosition + move;

        if (Rules.acceptableWhiteFigureKill(killingPosition, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[killingPosition] = EMPTY;
            boardState[nextPosition] = figure;
            if (Rules.promotableWhitePawn(nextPosition, boardState))
                boardState[nextPosition] = WHITE_QUEEN;
            gameNodes.add(new Game(boardState, false, depth+1, !maximizing));

            for (int nextMove : DIRECTIONS)
                whiteFigureKill(nextPosition, nextMove, figure, boardState);
        }
    }

    private void whiteFigureMove(int position, int move, char figure) {
        char[] boardState = Arrays.copyOf(board,BOARD_SIZE);
        int nextPosition = position + move;

        if (Rules.acceptableMove(position, nextPosition, boardState)) {
            boardState[position] = EMPTY;
            boardState[nextPosition] = figure;
            if (Rules.promotableWhitePawn(nextPosition, boardState))
                boardState[nextPosition] = WHITE_QUEEN;
            gameNodes.add(new Game(boardState, false, depth+1, !maximizing));
        }
    }
}
