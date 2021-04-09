package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private int id;
    private String data;
    private int nodePower;
    private List<Edge> edges;

    public Node(int id, String data, int nodePower) {
        this.id = id;
        this.data = data;
        this.nodePower = nodePower;
        edges = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public int getNodePower() {
        return nodePower;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", nodePower=" + nodePower +
                ", edgesConected=" + edges.size() +
                '}';
    }
}
