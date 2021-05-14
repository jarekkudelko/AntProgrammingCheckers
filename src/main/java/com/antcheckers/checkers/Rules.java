package com.antcheckers.checkers;

public interface Rules {
    char EMPTY = '-';
    char BLACK_PAWN = 'b';
    char BLACK_QUEEN = 'B';
    char WHITE_PAWN = 'w';
    char WHITE_QUEEN = 'W';

    int BOT_RIGHT = 9;
    int TOP_LEFT = -9;
    int BOT_LEFT = 7;
    int TOP_RIGHT = -7;

    int TOP_EDGE_ID = 0;
    int LEFT_EDGE_ID = 0;
    int BOT_EDGE_ID = 7;
    int RIGHT_EDGE_ID = 7;

    int BOARD_EDGE_LENGTH = 8;
    int BOARD_SIZE = 64;

    int BASE_PREDICTION = 5;
    int MEDIUM_PREDICTION = 6;
    int HIGHEST_PREDICTION = 7;

    static boolean acceptableWhiteFigureKill(int killingPosition, int nextPosition, char[] boardState){
        return exist(killingPosition) &&
                blackFigureNotOnEdge(killingPosition, boardState) &&
                empty(nextPosition, boardState);
    }

    static boolean acceptableBlackFigureKill(int killingPosition, int nextPosition, char[] boardState){
        return exist(killingPosition) &&
                whiteFigureNotOnEdge(killingPosition, boardState) &&
                empty(nextPosition, boardState);
    }

    static boolean acceptableMove(int position, int nextPosition, char[] boardState) {
        return exist(nextPosition) &&
                empty(nextPosition, boardState) &&
                (acceptableMoveTop(position, nextPosition) ||
                        acceptableMoveBot(position, nextPosition)) &&
                (acceptableMoveRight(position, nextPosition) ||
                        acceptableMoveLeft(position, nextPosition));
    }

    static boolean acceptableMoveTop(int position, int nextPosition) {
        return position/BOARD_EDGE_LENGTH - 1 == nextPosition/BOARD_EDGE_LENGTH;
    }

    static boolean acceptableMoveBot(int position, int nextPosition) {
        return position/BOARD_EDGE_LENGTH + 1 == nextPosition/BOARD_EDGE_LENGTH;
    }

    static boolean acceptableMoveRight(int position, int nextPosition) {
        return position%BOARD_EDGE_LENGTH + 1 == nextPosition%BOARD_EDGE_LENGTH;
    }

    static boolean acceptableMoveLeft(int position, int nextPosition) {
        return position%BOARD_EDGE_LENGTH - 1 == nextPosition%BOARD_EDGE_LENGTH;
    }

    static boolean whiteFigureNotOnEdge(int position, char[] boardState) {
        return notEdge(position) && whiteFigure(position, boardState);
    }

    static boolean blackFigureNotOnEdge(int position, char[] boardState) {
        return notEdge(position) && blackFigure(position, boardState);
    }

    static boolean empty(int position, char[] boardState) {
        return boardState[position] == EMPTY;
    }

    static boolean promotableWhitePawn(int position, char[] boardState) {
        return topEdge(position) && whitePawn(position, boardState);
    }

    static boolean promotableBlackPawn(int position, char[] boardState) {
        return botEdge(position) && blackPawn(position, boardState);
    }

    static boolean blackPawn(int position, char[] boardState) {
        return boardState[position] == BLACK_PAWN;
    }

    static boolean blackQueen(int position, char[] boardState) {
        return boardState[position] == BLACK_QUEEN;
    }

    static boolean blackFigure(int position, char[] boardState) {
        return boardState[position] == BLACK_PAWN || boardState[position] == BLACK_QUEEN;
    }

    static boolean whitePawn(int position, char[] boardState) {
        return boardState[position] == WHITE_PAWN;
    }

    static boolean whiteQueen(int position, char[] boardState) {
        return boardState[position] == WHITE_QUEEN;
    }

    static boolean whiteFigure(int position, char[] boardState) {
        return boardState[position] == WHITE_PAWN || boardState[position] == WHITE_QUEEN;
    }

    static boolean notEdge(int position) {
        return !leftEdge(position) &&
                !rightEdge(position) &&
                !topEdge(position) &&
                !botEdge(position);
    }

    static boolean rightEdge(int position) {
        return position%BOARD_EDGE_LENGTH == RIGHT_EDGE_ID;
    }

    static boolean leftEdge(int position) {
        return position%BOARD_EDGE_LENGTH == LEFT_EDGE_ID;
    }

    static boolean botEdge(int position) {
        return position/BOARD_EDGE_LENGTH == BOT_EDGE_ID;
    }

    static boolean topEdge(int position) {
        return position/BOARD_EDGE_LENGTH == TOP_EDGE_ID;
    }

    static boolean exist(int position) {
        return position<BOARD_SIZE && position>=0;
    }
}
