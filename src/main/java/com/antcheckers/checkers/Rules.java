package com.antcheckers.checkers;

public interface Rules {
    float MAX_FLOAT = 999999999;
    float MIN_FLOAT = -999999999;
    char[] INITIAL_BOARD =
            {'-', 'b', '-', 'b', '-', 'b', '-', 'b',
            'b', '-', 'b', '-', 'b', '-', 'b', '-',
            '-', 'b', '-', 'b', '-', 'b', '-', 'b',
            '-', '-', '-', '-', '-', '-', '-', '-',
            '-', '-', '-', '-', '-', '-', '-', '-',
            'w', '-', 'w', '-', 'w', '-', 'w', '-',
            '-', 'w', '-', 'w', '-', 'w', '-', 'w',
            'w', '-', 'w', '-', 'w', '-', 'w', '-'};

    int[] EDGES = {1,3,5,7,8,23,24,39,40,55,56,58,60,62};
    int[] WHITE_SECTOR = {1,3,5,7,8,10,12,14,17,19,21,23};
    int[] MID_SECTOR = {24,26,28,30,33,35,37,39};
    int[] BLACK_SECTOR = {40,42,44,46,49,51,53,55,56,58,60,62};
    int[] GAME_FIELDS = {1,3,5,7,8,10,12,14,17,19,21,23,24,26,
            28,30,33,35,37,39,40,42,44,46,49,51,53,55,56,58,60,62};

    char EMPTY = '-';
    char BLACK_PAWN = 'b';
    char BLACK_QUEEN = 'B';
    char WHITE_PAWN = 'w';
    char WHITE_QUEEN = 'W';

    int BOT_RIGHT = 9;
    int TOP_LEFT = -9;
    int BOT_LEFT = 7;
    int TOP_RIGHT = -7;
    int[] DIRECTIONS = {BOT_RIGHT, TOP_LEFT, BOT_LEFT, TOP_RIGHT};

    int STALK_TOP = -16;
    int STALK_TOP_RIGHT = -14;
    int STALK_RIGHT = 2;
    int STALK_BOT_RIGHT = 18;
    int STALK_BOT = 16;
    int STALK_BOT_LEFT = 14;
    int STALK_LEFT = -2;
    int STALK_TOP_LEFT = -18;
    int[] STALK_DIRECTIONS = {STALK_TOP, STALK_TOP_RIGHT, STALK_RIGHT,
            STALK_BOT_RIGHT, STALK_BOT, STALK_BOT_LEFT, STALK_LEFT, STALK_TOP_LEFT};

    int TOP_EDGE_ID = 0;
    int LEFT_EDGE_ID = 0;
    int BOT_EDGE_ID = 7;
    int RIGHT_EDGE_ID = 7;

    int BOARD_EDGE_LENGTH = 8;
    int BOARD_SIZE = 64;

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

    static boolean acceptableCover(int position, int nextPosition) {
        return exist(nextPosition) &&
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

    static boolean acceptableStalk(int position, int nextPosition) {
        return exist(nextPosition) &&
                (acceptableStalkTop(position, nextPosition) ||
                        acceptableStalkBot(position, nextPosition) ||
                        acceptableStalkVerticallyStatic(position, nextPosition)) &&
                (acceptableStalkRight(position, nextPosition) ||
                        acceptableStalkLeft(position, nextPosition) ||
                        acceptableStalkHorizontallyStatic(position, nextPosition));
    }

    static boolean acceptableStalkTop(int position, int nextPosition) {
        return position/BOARD_EDGE_LENGTH - 2 == nextPosition/BOARD_EDGE_LENGTH;
    }

    static boolean acceptableStalkBot(int position, int nextPosition) {
        return position/BOARD_EDGE_LENGTH + 2 == nextPosition/BOARD_EDGE_LENGTH;
    }

    static boolean acceptableStalkVerticallyStatic(int position, int nextPosition) {
        return position/BOARD_EDGE_LENGTH == nextPosition/BOARD_EDGE_LENGTH;
    }

    static boolean acceptableStalkRight(int position, int nextPosition) {
        return position%BOARD_EDGE_LENGTH + 2 == nextPosition%BOARD_EDGE_LENGTH;
    }

    static boolean acceptableStalkLeft(int position, int nextPosition) {
        return position%BOARD_EDGE_LENGTH - 2 == nextPosition%BOARD_EDGE_LENGTH;
    }

    static boolean acceptableStalkHorizontallyStatic(int position, int nextPosition) {
        return position%BOARD_EDGE_LENGTH  == nextPosition%BOARD_EDGE_LENGTH;
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

    static boolean bothColorsOnBoard(char[] boardState) {
        return whitesOnBoard(boardState) && blacksOnBoard(boardState);
    }

    static boolean blacksOnBoard(char[] boardState) {
        for (char field : boardState)
            if (field == BLACK_PAWN || field == BLACK_QUEEN)
                return true;
        return false;
    }

    static boolean whitesOnBoard(char[] boardState) {
        for (char field : boardState)
            if (field == WHITE_PAWN || field == WHITE_QUEEN)
                return true;
        return false;
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
