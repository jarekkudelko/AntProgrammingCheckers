package com.antcheckers.antcolony.move;

import com.antcheckers.antcolony.Ant;
import com.antcheckers.antcolony.Edge;
import com.antcheckers.antcolony.Node;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AntMove {

    private Ant ant;
    private boolean localPheromones;
    private float vaporizeFactor;
    private float initialPheromones;
    private float exploitationFactor;

    private List<Edge> candidateEdges;
    private float [] edgesAttractiveness;

    public AntMove(Ant ant, boolean localPheromones, float vaporizeFactor, float initialPheromones, float exploitationFactor) {
        this.ant = ant;
        this.localPheromones = localPheromones;
        this.vaporizeFactor = vaporizeFactor;
        this.initialPheromones = initialPheromones;
        this.exploitationFactor = exploitationFactor;
    }

    public void antUpdate() {
        getCandidateEdges();
        setEdgesAttractiveness();
        int edgeIndex = getChosenEdgeId();
        addEdgeCopy(edgeIndex);
    }

    private void getCandidateEdges() {
        List<Node> antNodes = ant.getVisitedNodes();
        Node startNode = antNodes.get(antNodes.size() - 1);
        candidateEdges = startNode.getCandidateEdges();
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

    private float getNodeVisibility(int equationPower) {
        return (float) 1 / (2 + equationPower);
    }

    private int getChosenEdgeId() {
        float q = ThreadLocalRandom.current().nextFloat();
        if(q <= exploitationFactor) {
            Exploitation exploitation = new Exploitation(edgesAttractiveness);
            return exploitation.getEdgeIndex();
        } else {
            Exploration exploration = new Exploration(edgesAttractiveness);
            return exploration.getEdgeIndex();
        }
    }

    private void addEdgeCopy(int edgeIndex) {
        Edge edge = candidateEdges.get(edgeIndex);

        Node endNode = edge.getEndNode();
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
