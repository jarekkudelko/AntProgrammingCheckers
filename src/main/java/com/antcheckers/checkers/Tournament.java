package com.antcheckers.checkers;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Tournament {

    private static final int LOW_PREDICTION = 4;
    private static final int BASE_PREDICTION = 5;
    private static final int MEDIUM_PREDICTION = 6;
    private static final int HIGH_PREDICTION = 7;
    private static final int LAST_TURN_ACF = 50;
    private static final int LAST_TURN_MEDIUM = 60;
    private static final int LAST_TURN_BEGINNER = 70;

    static final float[] BALANCE_ORIENTED = {25,-25,100,-100,2,-2,4,-4,8,-8,1,-1,1,-1,2,-2};
    static final float[] QUEENS_ORIENTED = {20,-20,150,-150,1,-1,7,-7,14,-14,1,-1,1,-1,5,-5};
    static final float[] POSITION_ORIENTED = {15,-15,60,-60,2,-2,6,-6,8,-8,2,-2,3,-3,1,-1};

    private float[][] weights = {BALANCE_ORIENTED, QUEENS_ORIENTED, POSITION_ORIENTED};
    private int[] weightsScores;
    private int[][] roundRobin;
    int playOffsCounter = 0;

    public Tournament() {
    }

    public Tournament(float[][] weights) {
        this.weights = weights;
    }

    public int getWinningWeights() {
        setRoundRobin();
        Game.setGameLimits(LAST_TURN_MEDIUM, LOW_PREDICTION);
        playSeries();
        return pickWinner();
    }

    private void setRoundRobin() {
        int matches = weights.length * (weights.length - 1) / 2;
        int twoPlayers = 2;
        roundRobin = new int[matches][twoPlayers];
        addMatchesFor(0);
        weightsScores = new int[weights.length];
    }

    private void addMatchesFor(int player){
        int nextPlayer = player + 1;
        for (int i = nextPlayer; i<weights.length; i++) {
            roundRobin[playOffsCounter] = new int[]{player, i};
            playOffsCounter++;
        }
        if(nextPlayer < weights.length)
            addMatchesFor(nextPlayer);
    }

    private void playSeries() {
        for (int[] players : roundRobin) {
            orderCoinFlip(players);
            int whiteId = players[0];
            int blackId = players[1];
            float[] whiteWeights = weights[whiteId];
            float[] blackWeights = getMappedCopy(weights[blackId]);
            Game.setPlayerWeights(whiteWeights, blackWeights);
            float[] winner = Game.playMatch();
            if (Arrays.equals(winner, whiteWeights))
                weightsScores[whiteId] += 1;
            else
                weightsScores[blackId] += 1;
        }
    }

    private void orderCoinFlip(int[] players) {
        boolean changeOrder = ThreadLocalRandom.current().nextBoolean();
        if(changeOrder) {
            int tmp = players[0];
            players[0] = players[1];
            players[1] = tmp;
        }
    }

    private float[] getMappedCopy(float[] weights){
        float[] copy = Arrays.copyOf(weights,weights.length);
        for(int i=0; i<weights.length; i+=2){
            float tmp = copy[i];
            copy[i] = copy[i+1];
            copy[i+1] = tmp;
        }
        return copy;
    }

    private int pickWinner() {
        int maxId = -1;
        int maxVal = Integer.MIN_VALUE;
        for (int i=0; i<weightsScores.length; i++)
            if(weightsScores[i] > maxVal) {
                maxVal = weightsScores[i];
                maxId = i;
            }
        return maxId;
    }
}
