package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private String data;
    private int nodePower;
    private List<Edge> candidateEdges;

    public Node(String data, int nodePower) {
        this.data = data;
        this.nodePower = nodePower;
        candidateEdges = new ArrayList<>();
    }

    public String getData() {
        return data;
    }

    public int getNodePower() {
        return nodePower;
    }

    public void addEdge(Edge edge) {
        candidateEdges.add(edge);
    }

    public List<Edge> getCandidateEdges() {
        return candidateEdges;
    }

    public boolean isOperand(){
        return nodePower == -1;
    }

    public boolean isOperator(){
        return nodePower == 1;
    }

    @Override
    public String toString() {
        return "Node{" +
                "data='" + data + '\'' +
                ", nodePower=" + nodePower +
                ", edgesConected=" + candidateEdges.size() +
                '}';
    }
}
