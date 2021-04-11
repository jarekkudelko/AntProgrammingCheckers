package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntMove {

    private Ant ant;
    private List<Node> nodes;
    private boolean localPheromones;
    private float vaporizeFactor;
    private float initialPheromones;
    private float exploitationFactor;

    private List<Edge> candidateEdges = new ArrayList<>();
    private float edgesAttractivenessSum = 0f;
    private float [] edgesAttractiveness;

    public AntMove(Ant ant, List<Node> nodes, boolean localPheromones, float vaporizeFactor, float initialPheromones, float exploitationFactor) {
        this.ant = ant;
        this.nodes = nodes;
        this.localPheromones = localPheromones;
        this.vaporizeFactor = vaporizeFactor;
        this.initialPheromones = initialPheromones;
        this.exploitationFactor = exploitationFactor;
    }

    public void antUpdate() {
        getCandidateEdges();
        setEdgesAttractiveness();
        setEdgesAttractivenessSum();

        int edgeIndex = getChosenEdgeId();
        addEdgeCopy(edgeIndex);
    }

    private void getCandidateEdges() {
        int startNodeId = ant.getCurrentNodeId();
        Node startNode = nodes.get(startNodeId);
        candidateEdges = startNode.getEdges();
    }

    private void setEdgesAttractiveness(){
        initializeEdgesAttractiveness();
        fillEdgesAttractiveness();
    }

    private void initializeEdgesAttractiveness() {
        edgesAttractiveness = new float[candidateEdges.size()];
    }

    private void fillEdgesAttractiveness() {
        for (Edge candidateEdge : candidateEdges) {
            float candidateEdgePheromones = candidateEdge.getPheromones();
            float nodeVisibility = getNodeVisibility(candidateEdge.getEndNode().getNodePower() + ant.getEquationPower());

            if(localPheromones)
                candidateEdgePheromones = checkForLocalPheromones(candidateEdge, candidateEdgePheromones);

            float probability = candidateEdgePheromones * nodeVisibility;
            int i = candidateEdges.indexOf(candidateEdge);
            edgesAttractiveness[i] = probability;
        }
    }

    private float checkForLocalPheromones(Edge candidateEdge, float candidateEdgePheromones) {
        List<Edge> antEdges = ant.getEdges();
        for (Edge antEdge : antEdges)
            if(antEdge.isCopyOf(candidateEdge) && antEdge.pheromonesStrongerThan(candidateEdgePheromones))
                candidateEdgePheromones = antEdge.getPheromones();
        return candidateEdgePheromones;
    }

    private void setEdgesAttractivenessSum() {
        for (float probability : edgesAttractiveness) {
            edgesAttractivenessSum += probability;
        }
    }

    private float getNodeVisibility(int equationPower) {
        return (float) 1 / (2 + equationPower);
    }

    private int getChosenEdgeId() {
        float q = ThreadLocalRandom.current().nextFloat();
        int index;
        if(q <= exploitationFactor)
            index = getIndexByExploitation();
        else
            index = getIndexByExploration();
        return index;
    }

    private int getIndexByExploitation() {
        float maxValue=0;
        int maxIndex=-1;
        int amount = 0;

        //poszukiwanie maksymalnej wartości prawdopodobieństwa
        for (int i = 0; i< edgesAttractiveness.length; i++) {
            if(edgesAttractiveness[i]>maxValue){
                maxValue = edgesAttractiveness[i];
                amount = 1;
                maxIndex = i;
            } else if(edgesAttractiveness[i] == maxValue){
                amount++;
            }
        }

        //jeżeli kilka maksymalnych wartości przeprowadź losowanie
        if(amount>1){
            int [] maxIndexArr = new int[amount];
            for (int i = 0; i< edgesAttractiveness.length; i++){
                if(edgesAttractiveness[i] == maxValue){
                    maxIndexArr[--amount] = i;
                }
            }
            maxIndex = maxIndexArr[ThreadLocalRandom.current().nextInt(0, maxIndexArr.length)];
        }
        return maxIndex;
    }

    private int getIndexByExploration() {
        float[] edgesPickProbabilities = getPickProbabilities();
        int index = getRandomEdgeIndex(edgesPickProbabilities);
        return index;
    }

    private float[] getPickProbabilities() {
        float [] probabilities = edgesAttractiveness.clone();
        calculatePickProbabilities(probabilities);
        return probabilities;
    }

    private void calculatePickProbabilities(float[] probabilities) {
        for (int i=0; i<probabilities.length; i++) {
            probabilities[i] /= edgesAttractivenessSum;
        }
    }

    private int getRandomEdgeIndex(float [] probabilities){
        calculateProbabilitiesRanges(probabilities);
        int index = getOccupiedRangeIndex(probabilities);
        return index;
    }

    private void calculateProbabilitiesRanges(float[] probabilities) {
        for (int i=1; i<probabilities.length; i++){
            probabilities[i] += probabilities[i-1];
        }
    }

    private int getOccupiedRangeIndex(float[] probabilities) {
        int index = -1;
        float randomValue = ThreadLocalRandom.current().nextFloat();
        for (int i=0; i<probabilities.length; i++) {
            if(randomValue < probabilities[i])
                index = i;
        }
        return index;
    }

    private void addEdgeCopy(int edgeIndex) {
        Edge edge = candidateEdges.get(edgeIndex);

        Node endNode = edge.getEndNode();
        int endNodeId = endNode.getId();
        ant.setCurrentNodeId(endNodeId);
        ant.addVisitedNode(endNode);

        int currentPower = ant.getEquationPower();
        int selectedNodePower = endNode.getNodePower();
        ant.setEquationPower(currentPower + selectedNodePower);

        Edge edgeCopy = new Edge(edge);
        if(localPheromones)
            localPheromoneUpdate(edgeCopy);
        ant.addEdge(edgeCopy);
    }

    private void localPheromoneUpdate(Edge edgeCopy) {
        float newLocalPheromones = (1f - vaporizeFactor) * edgeCopy.getPheromones() + vaporizeFactor * initialPheromones;
        edgeCopy.setPheromones(newLocalPheromones);
    }

}
