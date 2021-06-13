package com.antcheckers.checkers;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Tournament {

    private float[][] weights;
    private int[] weightsScores;
    private int[][] roundRobin;
    int playOffsCounter = 0;

    public Tournament(float[][] weights) {
        this.weights = weights;
    }

    public int getWinner() {
        setRoundRobin();
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
