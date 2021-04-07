package com.antcheckers.antcolony;

public class Edge {
    private Node startNode;
    private Node endNode;
    private float pheromones;

    public Edge() {
    }

    public Edge(Node startNode, Node endNode, float pheromones) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.pheromones = pheromones;
    }

    //deep copy constructor
    public Edge(Edge that) {
        this(that.getStartNode(),that.getEndNode(),that.getPheromones());
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public float getPheromones() {
        return pheromones;
    }

    public void setPheromones(float pheromones) {
        this.pheromones = pheromones;
    }

    @Override
    public String toString() {
        return "Edge{" + startNode.getData() + " -> " + endNode.getData() + "} P=" + pheromones;
    }
}