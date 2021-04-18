package com.antcheckers.antcolony;

import com.antcheckers.antcolony.move.AntMove;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntSystem {

    private final static String[] operators = {"+", "-", "*", "/"};

    private int numberOfAnts;
    private int maxIterationsInCycle;

    private float initialPheromones;
    private boolean localPheromones ;

    private float vaporizeFactor;
    private float exploitationFactor;

    private List<Node> nodes = new ArrayList<>();
    private List<Ant> ants = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public AntSystem(int numberOfAnts, int maxIterationsInCycle, float initialPheromones, boolean localPheromones, float vaporizeFactor, float exploitationFactor) {
        this.numberOfAnts = numberOfAnts;
        this.maxIterationsInCycle = maxIterationsInCycle;
        this.initialPheromones = initialPheromones;
        this.localPheromones = localPheromones;
        this.vaporizeFactor = vaporizeFactor;
        this.exploitationFactor = exploitationFactor;
    }

    public void setLists(int minNodeValue, int maxNodeValue) {
        fillNodes(minNodeValue, maxNodeValue);
        fillAnts();
        fillEdgesAndAssignThemToStartNodes();
    }

    private void fillNodes(int minNodeValue, int maxNodeValue) {
        Node node;
        for (String operator : operators) {
            node = new Node(operator,1);
            nodes.add(node);
        }
        for(int i = minNodeValue; i <= maxNodeValue; i++) {
            node = new Node(Integer.toString(i),-1);
            nodes.add(node);
        }
    }

    private void fillAnts() {
        Ant ant;
        for (int i=0; i<numberOfAnts; i++) {
            ant = new Ant(i);
            ants.add(ant);
        }
    }

    private void fillEdgesAndAssignThemToStartNodes() {
        Edge edge;
        for (Node startNode : nodes) {
            for (Node endNode : nodes) {
                edge = new Edge(startNode, endNode, initialPheromones);
                edges.add(edge);
                startNode.addEdge(edge);
            }
        }
    }

    public float[] antsUpdate(){
            prepareAnts();
            runAntsCycle();
            return getEstimatedAntsEquations();
    }

    private void prepareAnts() {
        for (Ant ant : ants) {
            resetAnt(ant);
            setAntStartNode(ant);
        }
    }

    private void resetAnt(Ant ant) {
        ant.clearEdges();
        ant.clearVisitedNodes();
        int initialPower = 2;
        ant.setEquationPower(initialPower);
    }

    private void setAntStartNode(Ant ant) {
        int startNodeId = getRandomOperatorNodeId();
        Node startNode = nodes.get(startNodeId);
        ant.addVisitedNode(startNode);
    }

    private int getRandomOperatorNodeId() {
        int maxRandom = operators.length;
        return ThreadLocalRandom.current().nextInt(0, maxRandom);
    }

    private void runAntsCycle() {
        for (Ant ant : ants){
            int i=0;
            while (i < maxIterationsInCycle && ant.getEquationPower() > 0) {
                AntMove antMove = new AntMove(ant, localPheromones, vaporizeFactor, initialPheromones, exploitationFactor);
                antMove.antUpdate();
                i++;
            }
        }
    }

    private float[] getEstimatedAntsEquations(){
        float[] antsValues = new float[ants.size()];
        PrefixCalculator prefixCalculator = new PrefixCalculator();
        for (int i=0; i<ants.size(); i++){
            ants.get(i).balanceEquation();
            antsValues[i] = prefixCalculator.evaluate(ants.get(i).getVisitedNodes());
        }
        return antsValues;
    }

    public void globalPheromoneUpdate(int bestAntIndex, float victoryRate) {
        Ant bestAnt = ants.get(bestAntIndex);
        Edge originalEdge;
        float currentPheromones;
        for (Edge antEdge : bestAnt.getEdges()){
            originalEdge = getOriginalEdge(antEdge);
            currentPheromones = originalEdge.getPheromones();
            currentPheromones = (1 - vaporizeFactor) * currentPheromones + vaporizeFactor * victoryRate;
            originalEdge.setPheromones(currentPheromones);
        }
    }

    private Edge getOriginalEdge(Edge edgeCopy) {
        for (Edge edge : edges) {
            if (edgeCopy.isCopyOf(edge)){
                return edge;
            }
        }
        return null;
    }
}