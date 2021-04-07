package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AntColonySystem {

    private int numberOfAnts = 3;
    private int numberOfCycles = 3;
    private int numberOfIterationsInCycle = 12;
    private int minNodeValue = 1;
    private int maxNodeValue = 2;
    private float initialFeromonesValue = 0.01f;
    private float ro = 0.1f;
    private float q0 = 0.5f;
    private boolean localUpdatePheromones = true;

    private List<Node> nodes;
    private List<Ant> ants;
    private List<Edge> edges;

    public void run() {
        nodes = createNodesList(minNodeValue, maxNodeValue);
        ants = createAntsList(numberOfAnts);
        edges = createEdgesList(nodes,initialFeromonesValue);

        antsCycle(ants,nodes,edges,numberOfIterationsInCycle,numberOfCycles);
    }

    private List createNodesList(int minValue, int maxValue) {
        List<Node> nodesList = new ArrayList<>();
        Node node;
        int index = 0;
        node = new Node(index++, "+",1);
        nodesList.add(node);
        node = new Node(index++, "-",1);
        nodesList.add(node);
        node = new Node(index++, "*",1);
        nodesList.add(node);
        node = new Node(index++, "/",1);
        nodesList.add(node);
        while (minValue <= maxValue) {
            node = new Node(index, Integer.toString(minValue),-1);
            nodesList.add(node);
            index++;
            minValue++;
        }
        return nodesList;
    }

    private List createAntsList(int antsNumber) {
        List<Ant> antsList = new ArrayList<>();
        Ant ant;
        for (int index=0; index<antsNumber; index++) {
            ant = new Ant(index);
            antsList.add(ant);
        }
        return antsList;
    }

    private List createEdgesList(List<Node> nodesList, float initFeromones){
        List<Edge> edgesList = new ArrayList<>();
        Edge edge;
        for (Node startNode : nodesList) {
            for (Node endNode : nodesList) {
                if(startNode != endNode) {
                    edge = new Edge();
                    edge.setStartNode(startNode);
                    edge.setEndNode(endNode);
                    edge.setPheromones(initFeromones);
                    edgesList.add(edge);
                    startNode.addEdge(edge);
                }
            }
        }
        return edgesList;
    }

    private void setAntsStartingPosition(List<Ant> antsList) {
        for (Ant ant : antsList) {
            // zresetuj mrówkę przed rozpoczęciem cyklu
            ant.clearEdges();
            ant.clearVisitedNodes();
            ant.setEquationPower(2);
            int nodeId = ThreadLocalRandom.current().nextInt(0, 4);
            ant.setCurrentNode(nodeId);
        }
    }

    private Node findNodeById(List<Node> nodesList, int id){
        for (Node node : nodesList) {
            if(node.getId() == id){
                return node;
            }
        }
        return null;
    }

    private Edge getOriginalEdge(Edge edgeCopy, List<Edge> edgesList){
        Node startNode = edgeCopy.getStartNode();
        Node endNode = edgeCopy.getEndNode();
        for (Edge edge : edgesList) {
            if (edge.getStartNode() == startNode && edge.getEndNode() == endNode){
                return edge;
            }
        }
        return null;
    }

    private void antUpdate(Ant ant, List<Node> nodesList) {
        int startNodeId;
        int endNodeId;
        Node startNode;
        Node endNode;
        Edge edge;

        startNodeId = ant.getCurrentNode();
        startNode = findNodeById(nodesList,startNodeId);

        // dodaj węzeł startowy do listy odwiedzonych węzłów
        if(ant.getVisitedNodes().isEmpty()){
            ant.addVisitedNode(startNode);
        }

        List<Edge> candidateEdges = startNode.getEdges();
        float [] probabilities = new float[candidateEdges.size()];
        float probabilitiesSum = 0f;

        int i = 0;
        for (Edge candidateEdge : candidateEdges) {
            float probability;
            float candidateEdgePheromones = candidateEdge.getPheromones();

            // wg pracy magisterskiej studenta - pomijam wpływ długości wyrażenia
            float nodeVisibility = getNodeVisibilityValue(candidateEdge.getEndNode().getNodePower() + ant.getEquationPower());

            if(localUpdatePheromones){
            // sprawdz tablicę odwiedzonych krawędzi przez mrówkę
                for (Edge antEdge : ant.getEdges()) {

                    //jeżeli znajdziesz kopię krawędzi krawędź
                    if(antEdge.getStartNode() == candidateEdge.getStartNode() && antEdge.getEndNode() == candidateEdge.getEndNode()){

                        //wybierz najwyższą(ostatnią) wartość sładu feromonowego
                        if(candidateEdgePheromones <= antEdge.getPheromones()){
                            candidateEdgePheromones = antEdge.getPheromones();
                        }
                    }
                }
            }

            // wyznacz prawdopodobienstwo wybrania wezla i dodaj je do tablicy prawdopodobienstw
            probability = candidateEdgePheromones * nodeVisibility;
            probabilities[i++] = probability;
            probabilitiesSum += probability;
        }

        // wylosowanie q - wybór między eksploracją a eksploatacją
        float q = 0 + new Random().nextFloat() * (1 - 0);

        int edgeIndex;
        if(q <= q0) {
            edgeIndex = getEdgeExploitation(probabilities);
        } else {
            edgeIndex = getEdgeExploration(probabilities, probabilitiesSum);
        }

        edge = candidateEdges.get(edgeIndex);

        // pobranie i zapis węzła, w którym mrówka się będzie znajdować
        endNode = edge.getEndNode();
        endNodeId = endNode.getId();
        ant.setCurrentNode(endNodeId);

        // dodaj nowy węzeł do listy odwiedzonych węzłów
        ant.addVisitedNode(endNode);

        // aktualziacja siły wyrażenia
        int currentPower = ant.getEquationPower();
        int selectedNodePower = endNode.getNodePower();
        ant.setEquationPower(currentPower + selectedNodePower);

        // utwórz kopię krawędzi (wymagane ze względu na opcjonalną obsługę feromonu lokalnego)
        Edge edgeCopy = new Edge(edge);

        if(localUpdatePheromones){
            // aktualizuj lokalny ślad feromonowy
            float newLocalPheromones = (1f - ro) * edgeCopy.getPheromones() + ro * initialFeromonesValue;
            edgeCopy.setPheromones(newLocalPheromones);
        }

        // zapisz kopię wybranej krawędzi do tablicy krawędzi mrówki
        ant.addEdge(edgeCopy);
    }

    // funkcja widoczności węzła (mam wrażenie, że długość wyrażenia w tym wypadku nie ma żadnego znaczenia)
    private float getNodeVisibilityValue(int eqationPower){
        return (float) 1/(2+eqationPower);
    }

    private int getEdgeExploitation(float [] probs){
        float maxValue=0;
        int maxIndex=-1;
        int amount = 0;

        //poszukiwanie maksymalnej wartości prawdopodobieństwa
        for (int i=0; i<probs.length; i++) {
            if(probs[i]>maxValue){
                maxValue = probs[i];
                amount = 1;
                maxIndex = i;
            } else if(probs[i] == maxValue){
                amount++;
            }
        }

        //jeżeli kilka maksymalnych wartości przeprowadź losowanie
        if(amount>1){
            int [] maxIndexArr = new int[amount];
            for (int i=0; i<probs.length; i++){
                if(probs[i] == maxValue){
                    maxIndexArr[--amount] = i;
                }
            }
            maxIndex = maxIndexArr[ThreadLocalRandom.current().nextInt(0, maxIndexArr.length)];
        }
        return maxIndex;
    }

    private int getEdgeExploration(float [] probs, float probsSum){
        for (int i=0; i<probs.length; i++) {
            probs[i] /= probsSum;
        }
        int index = getRandomIntWithProbabilities(probs);
        return index;
    }

    private int getRandomIntWithProbabilities(float [] probs){
        float [] probabilitiesSum = probs.clone();

        // obliczenie kolejnych sum prawdopodobieństw
        for (int i=1; i<probabilitiesSum.length; i++){
            probabilitiesSum[i] += probabilitiesSum[i-1];
        }

        // wygenerowanie liczby losowej z przedziału [0; 1)
        float randomValue = 0 + new Random().nextFloat() * (1 - 0);

        // sprawdzenie jakiej ona odpowiada liczbie całkowitej z przedziału [0; 1)
        for (int i=0; i<probabilitiesSum.length; i++){
            if(randomValue < probabilitiesSum[i]){
                return i;
            }
        }
        return -1;
    }

    private void balanceEquation(List<Ant> antsList){
        //jeżeli wyrażenie z samych znaków -> wstaw emergencyNode do listy węzłów mrówki
        Node emergencyNode = new Node(-1,"0",-1);

        for (Ant ant : antsList){
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

    private void globalPheromoneUpdate(List<Ant> antsList, List<Edge> edgesList){
        PrefixExpressionHandler prefixExpressionHandler = new PrefixExpressionHandler();
        int bestAntIndex = -1;
        float bestAntQuality = -100f;
        for (Ant ant : antsList) {
            //float antSolutionQuality = calculateQualityFunctionOfSolution();  <=  rozegranie gier pomiędzy rozwiązaniami
            float antSolutionQuality = prefixExpressionHandler.evaluatePrefix(ant.getVisitedNodes());
            if(antSolutionQuality > bestAntQuality){
                bestAntQuality = antSolutionQuality;
                bestAntIndex = ant.getId();
            }
        }
        System.out.println("Winner: Ant " + bestAntIndex + " Score: " + bestAntQuality);

        Edge originalEdge;
        Ant bestAnt = antsList.get(bestAntIndex);

        for (Edge antEdge : bestAnt.getEdges()){
            originalEdge = getOriginalEdge(antEdge,edgesList);

            float currentPheromones = originalEdge.getPheromones();
            currentPheromones = (1 - ro) * currentPheromones + ro * bestAntQuality;

            originalEdge.setPheromones(currentPheromones);
        }

    }

    private void antsUpdate(List<Ant> antsList, int maxIterations, List<Node> nodesList){
        for (Ant ant : antsList){
            int i=0;
            while (i<maxIterations && ant.getEquationPower() > 0){
                antUpdate(ant,nodesList);
                i++;
            }
//            System.out.print("Ant: " + ant.getId() + " Power: " + ant.getEquationPower() + " Eqation: " );
//            for (Node eqationNode : ant.getVisitedNodes()) {
//                System.out.print(eqationNode.getData() + "");
//            }
//            System.out.println();
//            for (Edge visitedEdge : ant.getEdges()) {
//                System.out.print(visitedEdge.getStartNode().getData() + " -> " + visitedEdge.getEndNode().getData() + ", ");
//            }
//            System.out.println();
        }
    }

    private void antsCycle(List<Ant> antsList, List<Node> nodesList, List<Edge> edgesList, int maxIterations, int cycles){
        for(int cycle=0;cycle<cycles;cycle++){

            System.out.println("cycle " + cycle);

            setAntsStartingPosition(antsList);

            antsUpdate(antsList,maxIterations,nodesList);

            balanceEquation(antsList);

            System.out.println("PO BALANSIE:");
            for (Ant ant : antsList) {
                System.out.print("Ant: " + ant.getId() + " Power: " + ant.getEquationPower() + " Eqation: " );
                for (Node eqationNode : ant.getVisitedNodes()) {
                    System.out.print(eqationNode.getData() + " ");
                }
                System.out.println();
            }

            globalPheromoneUpdate(antsList,edgesList);

            for (Edge edge: edgesList) {
                System.out.println(edge.toString());
            }
        }
    }

}
