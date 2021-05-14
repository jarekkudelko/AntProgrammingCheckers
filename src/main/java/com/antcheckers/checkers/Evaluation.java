package com.antcheckers.checkers;

public class Evaluation implements Rules{

    private char[] board;
    private float[] weights;

    public Evaluation(char[] board, float[] weights) {
        this.board = board;
        this.weights = weights;
    }

    public float get() {
        return  //pawns
                weights[0] * countFigures(WHITE_PAWN) +
                weights[1] * countFigures(BLACK_PAWN) +

                //queens
                weights[2] * countFigures(WHITE_QUEEN) +
                weights[3] * countFigures(BLACK_QUEEN) +

                //own territory
                weights[4] * locationBonus(WHITE_PAWN, WHITE_SECTOR) +
                weights[5] * locationBonus(BLACK_PAWN, BLACK_SECTOR) +

                //mid territory
                weights[6] * locationBonus(WHITE_PAWN, MID_SECTOR) +
                weights[7] * locationBonus(BLACK_PAWN, MID_SECTOR) +

                //opponent territory
                weights[8] * locationBonus(WHITE_PAWN, BLACK_SECTOR) +
                weights[9] * locationBonus(BLACK_PAWN, WHITE_SECTOR) +

                //hold edges
                weights[10] * locationBonus(WHITE_PAWN, EDGES) +
                weights[11] * locationBonus(BLACK_PAWN, EDGES) +

                //formation
                weights[12] * formationBonus(WHITE_PAWN, WHITE_QUEEN) +
                weights[13] * formationBonus(BLACK_PAWN, BLACK_QUEEN) +

                //stalk enemies
                weights[14] * stalkingBonus(WHITE_QUEEN, BLACK_PAWN, BLACK_QUEEN) +
                weights[15] * stalkingBonus(BLACK_QUEEN, WHITE_PAWN, WHITE_QUEEN);
    }

    private int countFigures(char figure) {
        int summary = 0;
        for (int position : GAME_FIELDS)
            if (board[position] == figure)
                summary += 1;
        return summary;
    }

    private int locationBonus(char figure, int[] positions) {
        int summary = 0;
        for (int position : positions)
            if (board[position] == figure)
                summary += 1;
        return summary;
    }

    private int formationBonus(char pawn, char queen) {
        int summary = 0;
        for (int position : GAME_FIELDS)
            if(board[position] == pawn || board[position] == queen)
                for (int direction : DIRECTIONS)
                    summary += covered(position, direction, pawn, queen);
        return summary;
    }

    private int covered(int position, int direction, char pawn, char queen) {
        int cover = position + direction;
        if(Rules.acceptableCover(position, cover) && (board[cover] == pawn || board[cover] == queen))
            return 1;
        return 0;
    }

    private int stalkingBonus(char stalkerQueen, char stalkedPawn, char stalkedQueen){
        int summary = 0;
        for (int position : GAME_FIELDS)
            if(board[position] == stalkerQueen)
                for (int direction : STALK_DIRECTIONS)
                    summary += stalked(position, direction, stalkedPawn, stalkedQueen);
        return summary;
    }

    private int stalked(int position, int direction, char stalkedPawn, char stalkedQueen) {
        int stalk = position + direction;
        if(Rules.acceptableStalk(position, stalk) && (board[stalk] == stalkedPawn || board[stalk] == stalkedQueen))
            return 1;
        return 0;
    }
}
