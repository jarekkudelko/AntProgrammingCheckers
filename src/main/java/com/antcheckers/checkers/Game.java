package com.antcheckers.checkers;

import java.util.*;

public class Game implements Rules{

    private static final char[] INITIAL_BOARD =
            {'-', 'b', '-', 'b', '-', 'b', '-', 'b',
             'b', '-', 'b', '-', 'b', '-', 'b', '-',
             '-', 'b', '-', 'b', '-', 'b', '-', 'b',
             '-', '-', '-', '-', '-', '-', '-', '-',
             '-', '-', '-', '-', '-', '-', '-', '-',
             'w', '-', 'w', '-', 'w', '-', 'w', '-',
             '-', 'w', '-', 'w', '-', 'w', '-', 'w',
             'w', '-', 'w', '-', 'w', '-', 'w', '-'};

    private Set<Game> gameNodes = new HashSet<>();
    private int value = 0;

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

    public void playMatch() {
        int turn = 0;
        Game match = new Game();
        System.out.println(match + " Start board");

        while (match.whitesOnBoard() && match.blacksOnBoard() && turn<80) {
            match.generateTree(BASE_PREDICTION);
            match.evaluateTreeLeaves(match);
            match.passMaxToCore(match);

            char[] nextBoard = match.makeOptimalMove();
            boolean nextWhite = !match.white;

            match = new Game(nextBoard, nextWhite,0,true);

            System.out.println("Turn: " + turn);
            System.out.println(match);

            turn++;
        }
    }

    private boolean blacksOnBoard() {
        for (char field : board)
            if (field == BLACK_PAWN || field == BLACK_QUEEN)
                return true;
        return false;
    }

    private boolean whitesOnBoard() {
        for (char field : board)
            if (field == WHITE_PAWN || field == WHITE_QUEEN)
                return true;
        return false;
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
                    node.value = evaluateForWhites(node.board);
                else
                    node.value = evaluateForBlacks(node.board);
            }
            evaluateTreeLeaves(node);
        }
    }

    private void passMinToCore(Game game) {
        int min = Integer.MAX_VALUE;
        for (Game node : game.gameNodes) {
            if(!node.gameNodes.isEmpty()) {
                if(node.maximizing)
                    passMaxToCore(node);
                else
                    passMinToCore(node);
            }
            if(min > node.value)
                min = node.value;
        }
        game.value = min;
    }

    private void passMaxToCore(Game game) {
        int max = Integer.MIN_VALUE;
        for (Game node : game.gameNodes) {
            if(!node.gameNodes.isEmpty()) {
                if(node.maximizing)
                    passMaxToCore(node);
                else
                    passMinToCore(node);
            }
            if(max < node.value)
                max = node.value;
        }
        game.value = max;
    }

    private char[] makeOptimalMove() {
        for (Game node : gameNodes) {
            if (node.value == value)
                return Arrays.copyOf(node.board, BOARD_SIZE);
        }
        return new char[0];
    }

    private int evaluateForWhites(char[] state) {
        int summary = 0;
        for (char field : state) {
            if (field == 'b')
                summary--;
            if (field == 'w')
                summary++;
            if (field == 'B')
                summary = summary-4;
            if (field == 'W')
                summary = summary+4;
        }
        return summary;
    }

    private int evaluateForBlacks(char[] state) {
        int summary = 0;
        for (char field : state) {
            if (field == 'b')
                summary++;
            if (field == 'w')
                summary--;
            if (field == 'B')
                summary = summary+4;
            if (field == 'W')
                summary = summary-4;
        }
        return summary;
    }

    @Override
    public String toString() {
        return "Lvl:" + depth + " " + String.valueOf(board) + " Val: " + value + ", White:" + white + ", Maxing:" + maximizing;
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
