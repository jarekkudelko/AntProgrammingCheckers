package com.antcheckers.antcolony;

import com.antcheckers.checkers.Tournament;
import com.antcheckers.utility.Parameters;

public class AntColony {

    private AntSystem[] antSystems = new AntSystem[Parameters.ATTRIBUTES];
    private float[][] systemsResults = new float[Parameters.ATTRIBUTES][Parameters.players];
    private float[][] weights = new float[Parameters.players][Parameters.ATTRIBUTES];

    public float[] run() {
        int winnerId = -1;
        setAntSystems();
        for(int i=0; i<Parameters.cycles; i++) {
            getSystemsResults();
            setWeights();
            Tournament tournament = new Tournament(weights);
            winnerId = tournament.getWinner();
            updateWinnerPath(winnerId);
        }
        return weights[winnerId];
    }

    private void setAntSystems() {
        for (int i = 0; i<Parameters.ATTRIBUTES; i++)
            antSystems[i] = new AntSystem(Parameters.minNodes[i], Parameters.maxNodes[i]);
    }

    private void getSystemsResults() {
        for(int i = 0; i<Parameters.ATTRIBUTES; i++)
            systemsResults[i] = antSystems[i].antsUpdate();
    }

    private void setWeights() {
        for (int i = 0; i<Parameters.ATTRIBUTES; i++)
            for (int j = 0; j<Parameters.players; j++)
                weights[j][i] = systemsResults[i][j];
    }

    private void updateWinnerPath(int id) {
        for (AntSystem system : antSystems)
            system.globalPheromoneUpdate(id);
    }
}
