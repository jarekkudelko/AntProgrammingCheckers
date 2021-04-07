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

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNodePower() {
        return nodePower;
    }

    public void setNodePower(int nodePower) {
        this.nodePower = nodePower;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void clearEdges() {
        edges.clear();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
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
