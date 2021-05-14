package com.antcheckers.checkers;

import java.util.*;

public class Game implements Rules{

    private static int lastTurn;
    private static int lookupDepth;
    private static float[] whitesWeights;
    private static float[] blacksWeights;

    private Set<Game> gameNodes = new HashSet<>();
    private float value = 0;
    private boolean maximizing;
    private char[] board;
    private boolean white;
    private int depth;

    public Game() {
        this.board = Arrays.copyOf(INITIAL_BOARD, BOARD_SIZE);
        this.maximizing = true;
        this.white = true;
        this.depth = 0;
    }

    public Game(char[] board, boolean white, int depth, boolean maximizing) {
        this.maximizing = maximizing;
        this.board = board;
        this.white = white;
        this.depth = depth;
    }

    public static void setGameLimits(int turnLimit, int lookupLimit) {
        lastTurn = turnLimit;
        lookupDepth = lookupLimit;
    }

    public static void setPlayerWeights(float[] whitePlayer, float[] blackPlayer) {
        whitesWeights = whitePlayer;
        blacksWeights = blackPlayer;
    }

    public static float[] playMatch() {
        Game match = new Game();
        for (int i=0; i<lastTurn; i++){
            if (Rules.bothColorsOnBoard(match.board)){
                match.generateTree(lookupDepth);
                match.evaluateTreeLeaves(match);
                match.passMaxToCore(match, MIN_FLOAT, MAX_FLOAT);
                char[] nextBoard = match.getBestMove();
                boolean nextWhite = !match.white;
                if (nextBoard.length == 0) {
                    if (nextWhite)
                        return whitesWeights;
                    else
                        return blacksWeights;
                }
                match = new Game(nextBoard, nextWhite,0,true);
            }
        }
        if (match.strengthBalance() >= 0)
            return whitesWeights;
        else
            return blacksWeights;
    }

    private void generateTree(int maxDepth) {
        if (depth < maxDepth) {
            if (white) {
                Whites whites = new Whites(board, depth, maximizing);
                gameNodes = whites.getAllWhitesActions();
            } else {
                Blacks blacks = new Blacks(board, depth, maximizing);
                gameNodes = blacks.getAllBlacksActions();
            }
            if (!gameNodes.isEmpty())
                for (Game node : gameNodes)
                    node.generateTree(maxDepth);
        }
    }

    private void evaluateTreeLeaves(Game game) {
        for (Game node : game.gameNodes) {
            if(node.gameNodes.isEmpty()){
                if(white)
                    node.value = new Evaluation(node.board, whitesWeights).get();
                else
                    node.value = new Evaluation(node.board, blacksWeights).get();
            }
            evaluateTreeLeaves(node);
        }
    }

    private void passMinToCore(Game game, float alpha, float beta) {
        float min = MAX_FLOAT;
        for (Game node : game.gameNodes) {
            if(!node.gameNodes.isEmpty()) {
                if(node.maximizing)
                    passMaxToCore(node, alpha, beta);
                else
                    passMinToCore(node, alpha, beta);
            }
            if (min > node.value) {
                min = node.value;
                beta = node.value;
            }
            if(beta <= alpha)
                break;
        }
        game.value = min;
    }

    private void passMaxToCore(Game game, float alpha, float beta) {
        float max = MIN_FLOAT;
        for (Game node : game.gameNodes) {
            if(!node.gameNodes.isEmpty()) {
                if(node.maximizing)
                    passMaxToCore(node, alpha, beta);
                else
                    passMinToCore(node, alpha, beta);
            }
            if (max < node.value) {
                max = node.value;
                alpha = node.value;
            }
            if (beta <= alpha)
                break;
        }
        game.value = max;
    }

    private char[] getBestMove() {
        for (Game node : gameNodes) {
            if (node.value == value)
                return Arrays.copyOf(node.board, BOARD_SIZE);
        }
        return new char[0];
    }

    private int strengthBalance() {
        int whitePoints = 0;
        for (int i : GAME_FIELDS) {
            if(board[i] == WHITE_PAWN)
                whitePoints += 1;
            if(board[i] == WHITE_QUEEN)
                whitePoints += 4;
            if(board[i] == BLACK_PAWN)
                whitePoints -= 1;
            if(board[i] == BLACK_QUEEN)
                whitePoints -= 4;
        }
        return whitePoints;
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
