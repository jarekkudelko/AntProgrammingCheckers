package com.antcheckers.antcolony;

import java.util.Arrays;

public class AntColony {

    private final int ATTRIBUTES = 3;

    private int numberOfAnts = 3;
    private int numberOfCycles = 3;
    private int maxIterationsInCycle = 12;

    private float initialPheromones = 0.001f;
    private boolean localPheromones = true;

    private float vaporizeFactor = 0.1f;
    private float exploitationFactor = 0.2f;

    private AntSystem[] antSystems = new AntSystem[ATTRIBUTES];
    private int[] minNodes = {-2,-2,-2};
    private int[] maxNodes = {2,2,2};

    private float[][] antSystemsResults = new float[ATTRIBUTES][numberOfAnts];
    private float[][] playerFunctionsWeights = new float[numberOfAnts][ATTRIBUTES];

    public void run() {
        initializeSystems();
        for(int i=0; i<numberOfCycles; i++) {
            getSystemsResults();
            setPlayerFunctionsWeights();
            int winnerId = mockCompareSystemValues();
            globalPheromoneUpdate(winnerId);
        }
    }

    private void initializeSystems() {
        AntSystem antSystem;
        for (int i=0; i<ATTRIBUTES; i++) {
            antSystem = new AntSystem(numberOfAnts,maxIterationsInCycle,initialPheromones,localPheromones,vaporizeFactor,exploitationFactor);
            antSystem.setLists(minNodes[i],maxNodes[i]);
            antSystems[i] = antSystem;
        }
    }

    private void getSystemsResults() {
        for(int i=0; i<ATTRIBUTES; i++) {
            float[] antSystemResult = antSystems[i].antsUpdate();
            antSystemsResults[i] = antSystemResult;
        }
    }

    private void setPlayerFunctionsWeights() {
        for (int i=0; i<ATTRIBUTES; i++){
            for (int j=0; j<numberOfAnts; j++) {
                playerFunctionsWeights[j][i] = antSystemsResults[i][j];
            }
        }
        System.out.println("Player functions: " + Arrays.deepToString(playerFunctionsWeights));
    }

    private int mockCompareSystemValues() {
        int winnerId = 0;
        float winningVal = sumPlayerArr(0);
        for (int i=0; i<playerFunctionsWeights.length; i++){
            float currentValue = sumPlayerArr(i);
            if(winningVal < currentValue){
                winnerId = i;
                winningVal = currentValue;
            }
        }
        System.out.println("Winner Id:" + winnerId + ", Val: " + winningVal);
        return winnerId;
    }

    private float sumPlayerArr(int id) {
        float sum = 0;
        for (float f : playerFunctionsWeights[id]) {
            sum += f;
        }
        return sum;
    }

    private void globalPheromoneUpdate(int winnerId) {
        for (AntSystem antSystem : antSystems) {
            antSystem.globalPheromoneUpdate(winnerId,1);
        }
    }
}
