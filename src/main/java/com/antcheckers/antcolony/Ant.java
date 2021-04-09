package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;

public class Ant {

    private int id;
    private int currentNodeId;
    private int equationPower;
    private List<Edge> edges;
    private List<Node> visitedNodes;

    public Ant(int id) {
        this.id = id;
        currentNodeId = -1;
        equationPower = 2;
        edges = new ArrayList<Edge>();
        visitedNodes = new ArrayList<Node>();
    }

    public int getId() {
        return id;
    }

    public int getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(int currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void addEdgeToPosition(int index, Edge edge) {
        edges.add(index, edge);
    }

    public void clearEdges() {
        edges.clear();
    }

    public void addVisitedNode(Node node) {
        visitedNodes.add(node);
    }

    public void clearVisitedNodes() {
        visitedNodes.clear();
    }

    public int getEquationPower() {
        return equationPower;
    }

    public void setEquationPower(int equationPower) {
        this.equationPower = equationPower;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getVisitedNodes() {
        return visitedNodes;
    }

    @Override
    public String toString() {
        return "Ant{" +
                "id=" + id +
                ", currentNode=" + currentNodeId +
                ", eqationPower=" + equationPower +
                ", edges=" + "\r\n" + edges +
                ", visitedNodes=" + visitedNodes +
                '}';
    }
}
