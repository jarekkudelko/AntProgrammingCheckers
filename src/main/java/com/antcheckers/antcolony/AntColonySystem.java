package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntColonySystem {

    private static String[] operators = {"+", "-", "*", "/"};

    private int numberOfAnts = 3;
    private int numberOfCycles = 3;
    private int maxIterationsInCycle = 12;

    private int minNodeValue = 1;
    private int maxNodeValue = 2;

    private float initialFeromones = 0.01f;
    private boolean localPheromones = true;

    private float vaporizeFactor = 0.1f;
    private float exploitationFactor = 0.5f;

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
        int nodeId;
        for (String operator : operators) {
            nodeId = nodes.size();
            node = new Node(nodeId, operator,1);
            nodes.add(node);
        }
    }

    private void addOperandNodes() {
        Node node;
        int nodeId;
        for(int i = minNodeValue; i <= maxNodeValue; i++) {
            nodeId = nodes.size();
            node = new Node(nodeId, Integer.toString(i),-1);
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
            balanceEquation();

            //------------------------------------------wyswietlanie danych----------------------------------------
            System.out.println("PO BALANSIE:");
            for (Ant ant : ants) {
                System.out.print("Ant: " + ant.getId() + " Power: " + ant.getEquationPower() + " Eqation: " );
                for (Node eqationNode : ant.getVisitedNodes()) {
                    System.out.print(eqationNode.getData() + " ");
                }
                System.out.println();
            }
            //-------------------------------------------------------------------------------------------------------

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
        ant.setCurrentNodeId(startNodeId);
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
            while (i < maxIterationsInCycle && ant.getEquationPower() > 0){
                antUpdate(ant);
                i++;
            }
        }
    }

    private void antUpdate(Ant ant) {
        int startNodeId;
        int endNodeId;
        Node startNode;
        Node endNode;
        Edge edge;

        startNodeId = ant.getCurrentNodeId();
        startNode = nodes.get(startNodeId);

        List<Edge> candidateEdges = startNode.getEdges();
        float [] pickProbabilities = new float[candidateEdges.size()];
        float pickProbabilitiesSum = 0f;

        //dla kazdej krawedzi wyznacza prawdopodobienstwo wyboru i dodaje je do sumy
        for (Edge candidateEdge : candidateEdges) {
            float candidateEdgePheromones = candidateEdge.getPheromones();

            float nodeVisibility = getNodeVisibility(candidateEdge.getEndNode().getNodePower() + ant.getEquationPower());

            if(localPheromones) {
                List<Edge> antEdges = ant.getEdges();
                for (Edge antEdge : antEdges) {
                    if(antEdge.isCopyOf(candidateEdge) && antEdge.pheromonesStrongerThan(candidateEdgePheromones)) {
                        candidateEdgePheromones = antEdge.getPheromones();
                    }
                }

            }

            float probability = candidateEdgePheromones * nodeVisibility;
            int i = candidateEdges.indexOf(candidateEdge);
            pickProbabilities[i] = probability;
            pickProbabilitiesSum += probability;
        }


        int edgeIndex = chooseEdgeToGo(pickProbabilities, pickProbabilitiesSum);
        edge = candidateEdges.get(edgeIndex);

        // pobranie i zapis węzła, w którym mrówka się będzie znajdować
        endNode = edge.getEndNode();
        endNodeId = endNode.getId();
        ant.setCurrentNodeId(endNodeId);

        // dodaj nowy węzeł do listy odwiedzonych węzłów
        ant.addVisitedNode(endNode);

        // aktualziacja siły wyrażenia
        int currentPower = ant.getEquationPower();
        int selectedNodePower = endNode.getNodePower();
        ant.setEquationPower(currentPower + selectedNodePower);

        Edge edgeCopy = new Edge(edge);

        if(localPheromones)
            localPheromoneUpdate(edgeCopy);

        // zapisz kopię wybranej krawędzi do tablicy krawędzi mrówki
        ant.addEdge(edgeCopy);
    }

    private void localPheromoneUpdate(Edge edgeCopy) {
        float newLocalPheromones = (1f - vaporizeFactor) * edgeCopy.getPheromones() + vaporizeFactor * initialFeromones;
        edgeCopy.setPheromones(newLocalPheromones);
    }

    private float getNodeVisibility(int equationPower) {
        return (float) 1 / (2 + equationPower);
    }

    private int chooseEdgeToGo(float[] pickProbability, float pickProbabilitySum) {
        float q = ThreadLocalRandom.current().nextFloat();
        int index;
        if(q <= exploitationFactor)
            index = getEdgeExploitation(pickProbability);
        else
            index = getEdgeExploration(pickProbability, pickProbabilitySum);
        return index;
    }

    private int getEdgeExploitation(float [] probabilities) {
        float maxValue=0;
        int maxIndex=-1;
        int amount = 0;

        //poszukiwanie maksymalnej wartości prawdopodobieństwa
        for (int i=0; i<probabilities.length; i++) {
            if(probabilities[i]>maxValue){
                maxValue = probabilities[i];
                amount = 1;
                maxIndex = i;
            } else if(probabilities[i] == maxValue){
                amount++;
            }
        }

        //jeżeli kilka maksymalnych wartości przeprowadź losowanie
        if(amount>1){
            int [] maxIndexArr = new int[amount];
            for (int i=0; i<probabilities.length; i++){
                if(probabilities[i] == maxValue){
                    maxIndexArr[--amount] = i;
                }
            }
            maxIndex = maxIndexArr[ThreadLocalRandom.current().nextInt(0, maxIndexArr.length)];
        }
        return maxIndex;
    }

    private int getEdgeExploration(float [] probabilities, float probabilitiesSum){
        for (int i=0; i<probabilities.length; i++) {
            probabilities[i] /= probabilitiesSum;
        }
        int index = getRandomIntWithProbabilities(probabilities);
        return index;
    }

    private int getRandomIntWithProbabilities(float [] probabilities){
        float [] probabilitiesSum = probabilities.clone();

        // obliczenie kolejnych sum prawdopodobieństw
        for (int i=1; i<probabilitiesSum.length; i++){
            probabilitiesSum[i] += probabilitiesSum[i-1];
        }

        // wygenerowanie liczby losowej z przedziału [0; 1)
        float randomValue = ThreadLocalRandom.current().nextFloat();

        // sprawdzenie jakiej ona odpowiada liczbie całkowitej z przedziału [0; 1)
        for (int i=0; i<probabilitiesSum.length; i++){
            if(randomValue < probabilitiesSum[i]){
                return i;
            }
        }
        return -1;
    }

    private void balanceEquation(){
        //jeżeli wyrażenie z samych znaków -> wstaw emergencyNode do listy węzłów mrówki
        Node emergencyNode = new Node(-1,"0",-1);

        for (Ant ant : ants){
            int operatorsToRemove = ant.getEquationPower();
            for(int i=0; i<operatorsToRemove; i++){
                boolean operatorToRemove = true;

                //przejrzyj listę od prawej do lewej
                for(int index = ant.getVisitedNodes().size() - 1; index>=0; index--){

                    //jeżeli trafisz na operator, usuń go i zmniejsz siłę wyrażenia o 1
                    if (operatorToRemove && ant.getVisitedNodes().get(index).getNodePower() == 1){

                        //jeżeli operator do usunięcia znajduje się na końcu listy
                        if (index == ant.getVisitedNodes().size() - 1){
                            //jeżeli mrówki stworzyły wyrażenie składające się z samych znaków
                            if(index - 1 == -1){
                                ant.addVisitedNode(emergencyNode);
                            } else {
                                ant.getEdges().remove(index - 1);
                            }
                        } else if(index < ant.getVisitedNodes().size() - 1 && index != 0){
                            // w wyniku podmieniania jedej z wewnetrznych krawedzi moze dojsc do sytuacji
                            // ze startNode i endNode są takie same, w tym wypadku należy obsłużyć takie
                            // zdarzenie w globalnej aktualizacji sladu feromonowego pomijając taką krawędź

                            Node startNode = ant.getEdges().get(index - 1).getStartNode();
                            Node endNode = ant.getEdges().get(index).getEndNode();
                            Edge dummyEdge = new Edge(startNode,endNode,-100f);
                            ant.getEdges().remove(index);
                            ant.getEdges().remove(index-1);
                            ant.addEdgeToPosition(index - 1,dummyEdge);
                        }
                        //jeżeli operator do usunięcia znajduje się na początku listy
                        else if (index == 0){
                            // moze dojsc do sytuacji, ze w liscie visitedNodes bedzie tylko jeden wezel
                            // a lista krawedzi mrowki bedzie pusta - taką sytuację też trzeba obsłużyć w aktualziacji sladu feromonowego
                            ant.getEdges().remove(index);
                        }

                        operatorToRemove = false;
                        ant.getVisitedNodes().remove(index);
                        ant.setEquationPower((ant.getEquationPower() - 1));
                    }
                }
            }
        }
    }

    private int getBestSolutionAntId() {
        PrefixExpressionHandler prefixExpressionHandler = new PrefixExpressionHandler();
        int bestAntIndex = -1;
        float bestAntQuality = -100f;

        for (Ant ant : ants) {
            float antSolutionQuality = prefixExpressionHandler.evaluatePrefix(ant.getVisitedNodes());
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