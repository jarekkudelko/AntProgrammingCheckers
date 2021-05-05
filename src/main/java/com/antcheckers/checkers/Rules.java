package com.antcheckers.checkers;

public interface Rules {
    char EMPTY = '-';
    char BLACK_PAWN = 'b';
    char WHITE_PAWN = 'w';

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

    static boolean acceptableWhitePawnKill(int killingPosition, int nextPosition, char[] boardState){
        return exist(killingPosition) &&
                blackPawnNotOnEdge(killingPosition, boardState) &&
                empty(nextPosition, boardState);
    }

    static boolean acceptableBlackPawnKill(int killingPosition, int nextPosition, char[] boardState){
        return exist(killingPosition) &&
                whitePawnNotOnEdge(killingPosition, boardState) &&
                empty(nextPosition, boardState);
    }

    static boolean acceptableBlackPawnMove(int position, int nextPosition, char[] boardState) {
        return exist(nextPosition) &&
                empty(nextPosition, boardState) &&
                acceptableMoveBot(position, nextPosition) &&
                (acceptableMoveRight(position, nextPosition) ||
                        acceptableMoveLeft(position, nextPosition));
    }

    static boolean acceptableWhitePawnMove(int position, int nextPosition, char[] boardState) {
        return exist(nextPosition) &&
                empty(nextPosition, boardState) &&
                acceptableMoveTop(position, nextPosition) &&
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

    static boolean whitePawnNotOnEdge(int position, char[] boardState) {
        return notEdge(position) && whitePawn(position, boardState);
    }

    static boolean blackPawnNotOnEdge(int position, char[] boardState) {
        return notEdge(position) && blackPawn(position, boardState);
    }

    static boolean empty(int position, char[] boardState) {
        return boardState[position] == EMPTY;
    }

    static boolean blackPawn(int position, char[] boardState) {
        return boardState[position] == BLACK_PAWN;
    }

    static boolean whitePawn(int position, char[] boardState) {
        return boardState[position] == WHITE_PAWN;
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
