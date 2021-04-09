package com.antcheckers.antcolony;

public class Edge {
    private Node startNode;
    private Node endNode;
    private float pheromones;

    public Edge(Node startNode, Node endNode, float pheromones) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.pheromones = pheromones;
    }

    //deep copy constructor
    public Edge(Edge that) {
        this(that.getStartNode(),that.getEndNode(),that.getPheromones());
    }

    boolean isCopyOf(Edge original) {
        return getStartNode() == original.getStartNode() && getEndNode() == original.getEndNode();
    }

    boolean pheromonesStrongerThan(float originalEdgePheromones) {
        return originalEdgePheromones <= getPheromones();
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
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
