package com.antcheckers.antcolony;

import com.antcheckers.checkers.Tournament;
import com.antcheckers.utility.Parameters;

import java.util.Arrays;

public class AntColony {

    private AntSystem[] antSystems = new AntSystem[Parameters.attributes];
    private float[][] systemsResults = new float[Parameters.attributes][Parameters.players];
    private float[][] weights = new float[Parameters.players][Parameters.attributes];

    public void run() {
        System.out.println("Setting systems!");
        setAntSystems();
        for(int i=0; i<Parameters.cycles; i++) {
            getSystemsResults();
            System.out.println("Got system results!");
            setWeights();
            System.out.println("Tournament started!");
            Tournament tournament = new Tournament(weights);
            int winnerId = tournament.getWinner();
            updateWinnerPath(winnerId);
            System.out.println("Winner weights: " + Arrays.toString(weights[winnerId]));
        }
    }

    private void setAntSystems() {
        for (int i=0; i<Parameters.attributes; i++)
            antSystems[i] = new AntSystem(Parameters.minNodes[i], Parameters.maxNodes[i]);
    }

    private void getSystemsResults() {
        for(int i=0; i<Parameters.attributes; i++)
            systemsResults[i] = antSystems[i].antsUpdate();
    }

    private void setWeights() {
        for (int i=0; i<Parameters.attributes; i++)
            for (int j = 0; j<Parameters.players; j++)
                weights[j][i] = systemsResults[i][j];
    }

    private void updateWinnerPath(int id) {
        for (AntSystem system : antSystems)
            system.globalPheromoneUpdate(id);
    }
}
