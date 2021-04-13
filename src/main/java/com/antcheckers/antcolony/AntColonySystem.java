package com.antcheckers.antcolony;

import com.antcheckers.antcolony.move.AntMove;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntColonySystem {

    private final String[] operators = {"+", "-", "*", "/"};

    private int numberOfAnts = 3;
    private int numberOfCycles = 3;
    private int maxIterationsInCycle = 12;

    private int minNodeValue = -1;
    private int maxNodeValue = 2;

    private float initialFeromones = 0.001f;
    private boolean localPheromones = true;

    private float vaporizeFactor = 0.1f;
    private float exploitationFactor = 0.2f;

    private List<Node> nodes = new ArrayList<>();
    private List<Ant> ants = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public void run() {
        setColonyLists();
        colonyLifecycle();
    }

    private void setColonyLists() {
        fillNodes();
        fillAnts();
        fillEdgesAndAssignThemToStartNodes();
    }

    private void fillNodes() {
        addOperatorNodes();
        addOperandNodes();
    }

    private void addOperatorNodes() {
        Node node;
        for (String operator : operators) {
            node = new Node(operator,1);
            nodes.add(node);
        }
    }

    private void addOperandNodes() {
        Node node;
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
                edge = new Edge(startNode, endNode, initialFeromones);
                edges.add(edge);
                startNode.addEdge(edge);
            }
        }
    }

    private void colonyLifecycle(){
        for(int i=0; i<numberOfCycles; i++){
            prepareAnts();
            antsUpdate();
            balanceAntsEquations();

            System.out.println("PO BALANSIE:");
            for (Ant ant : ants) {
                System.out.print("Ant: " + ant.getId() + " Power: " + ant.getEquationPower() + " Eqation: " );
                for (Node eqationNode : ant.getVisitedNodes()) {
                    System.out.print(eqationNode.getData() + " ");
                }
                System.out.println();
            }

            int bestAntId = getBestSolutionAntId();
            globalPheromoneUpdate(bestAntId,1);
        }
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

    private void antsUpdate() {
        for (Ant ant : ants){
            int i=0;
            while (i < maxIterationsInCycle && ant.getEquationPower() > 0) {
                AntMove antMove = new AntMove(ant, localPheromones, vaporizeFactor, initialFeromones, exploitationFactor);
                antMove.antUpdate();
                i++;
            }
        }
    }

    private void balanceAntsEquations(){
        for (Ant ant : ants){
            ant.balanceEquation();
        }
    }

    private int getBestSolutionAntId() {
        PrefixCalculator prefixCalculator = new PrefixCalculator();
        int bestAntIndex = -1;
        float bestAntQuality = -100f;

        for (Ant ant : ants) {
            float antSolutionQuality = prefixCalculator.evaluate(ant.getVisitedNodes());
            if(antSolutionQuality > bestAntQuality){
                bestAntQuality = antSolutionQuality;
                bestAntIndex = ant.getId();
            }
        }
        System.out.println("Winner: Ant " + bestAntIndex + " Score: " + bestAntQuality);
        return bestAntIndex;
    }

    private void globalPheromoneUpdate(int bestAntIndex, float victoryRate) {
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