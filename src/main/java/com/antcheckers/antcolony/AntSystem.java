package com.antcheckers.antcolony;

import com.antcheckers.antcolony.move.AntMove;
import com.antcheckers.utility.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntSystem {

    private final static String[] OPERATORS = {"+", "-", "*", "/"};
    private final static int OPERATOR_POWER = 1;
    private final static int OPERAND_POWER = -1;
    private final static int INITIAL_POWER = 2;

    private List<Node> nodes = new ArrayList<>();
    private List<Ant> ants = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public AntSystem(int min, int max) {
        setNodes(min, max);
        setAnts();
        setEdges();
    }

    private void setNodes(int min, int max) {
        for (String operator : OPERATORS)
            nodes.add(new Node(operator, OPERATOR_POWER));
        for (int i=min; i<=max; i++)
            nodes.add(new Node(Integer.toString(i), OPERAND_POWER));
    }

    private void setAnts() {
        for (int i = 0; i<Parameters.players; i++)
            ants.add(new Ant());
    }

    private void setEdges() {
        for (Node startNode : nodes)
            for (Node endNode : nodes) {
                Edge edge = new Edge(startNode, endNode, Parameters.initialPheromone);
                edges.add(edge);
                startNode.addEdge(edge);
            }
    }

    public float[] antsUpdate(){
            prepareAnts();
            visitNodes();
            return getResults();
    }

    private void prepareAnts() {
        for (Ant ant : ants) {
            resetAnt(ant);
            setStartNode(ant);
        }
    }

    private void resetAnt(Ant ant) {
        ant.clearEdges();
        ant.clearVisitedNodes();
        ant.setEquationPower(INITIAL_POWER);
    }

    private void setStartNode(Ant ant) {
        int startNodeId = getRandomOperatorId();
        Node startNode = nodes.get(startNodeId);
        ant.addVisitedNode(startNode);
    }

    private int getRandomOperatorId() {
        return ThreadLocalRandom.current().nextInt(0, OPERATORS.length);
    }

    private void visitNodes() {
        for (Ant ant : ants) {
            for (int i=0; i<Parameters.iterations; i++)
                if (ant.getEquationPower() > 0)
                    new AntMove(ant).update();
        }
    }

    private float[] getResults() {
        float[] results = new float[ants.size()];
        for (int i=0; i<ants.size(); i++) {
            Ant ant = ants.get(i);
            ant.balanceEquation();
            List<Node> equation = ant.getVisitedNodes();
            results[i] = PrefixCalculator.evaluate(equation);
        }
        return results;
    }

    public void globalPheromoneUpdate(int id) {
        List<Edge> winnerPath = ants.get(id).getEdges();
        for (Edge edge : winnerPath) {
            Edge originalEdge = getOriginalEdge(edge);
            float currentPheromones = originalEdge.getPheromones();
            currentPheromones = (1 - Parameters.vaporizeFactor) * currentPheromones + Parameters.vaporizeFactor;
            originalEdge.setPheromones(currentPheromones);
        }
    }

    private Edge getOriginalEdge(Edge copy) {
        for (Edge edge : edges)
            if (copy.isCopyOf(edge))
                return edge;
        return null;
    }
}
