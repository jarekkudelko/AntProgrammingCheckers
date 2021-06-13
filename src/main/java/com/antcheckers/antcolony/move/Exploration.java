package com.antcheckers.antcolony.move;

import java.util.concurrent.ThreadLocalRandom;

public class Exploration {

    private float [] edgesAttractiveness;
    private float edgesAttractivenessSum;
    private float [] pickProbabilities;

    public Exploration(float[] edgesAttractiveness) {
        this.edgesAttractiveness = edgesAttractiveness;
    }

    public int getEdgeIndex() {
        setEdgesAttractivenessSum();
        createAndFillPickProbabilities();
        transformProbabilitiesToRanges();
        return getOccupiedRangeIndex();
    }

    private void setEdgesAttractivenessSum() {
        edgesAttractivenessSum = 0;
        for (float attractiveness : edgesAttractiveness)
            edgesAttractivenessSum += attractiveness;
    }

    private void createAndFillPickProbabilities() {
        pickProbabilities = edgesAttractiveness.clone();
        for (int i=0; i<pickProbabilities.length; i++)
            pickProbabilities[i] /= edgesAttractivenessSum;
    }

    private void transformProbabilitiesToRanges() {
        for (int i=1; i<pickProbabilities.length; i++)
            pickProbabilities[i] += pickProbabilities[i-1];
    }

    private int getOccupiedRangeIndex() {
        float randomValue = ThreadLocalRandom.current().nextFloat();
        for (int i=0; i<pickProbabilities.length; i++)
            if(randomValue < pickProbabilities[i])
                return i;
        return -1;
    }
}