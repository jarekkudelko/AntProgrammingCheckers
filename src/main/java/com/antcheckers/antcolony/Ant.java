package com.antcheckers.antcolony;

import java.util.ArrayList;
import java.util.List;

public class Ant {

    private int id;
    private int equationPower;
    private List<Edge> edges;
    private List<Node> visitedNodes;

    public Ant(int id) {
        this.id = id;
        equationPower = 2;
        edges = new ArrayList<>();
        visitedNodes = new ArrayList<>();
    }

    public int getId() {
        return id;
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

    public void balanceEquation() {
        Node emergencyNode = new Node("0",-1);
        int operatorsToRemove = equationPower;
        for(int i=0; i<operatorsToRemove; i++){
            boolean operatorToRemove = true;
            for(int nodeId = visitedNodes.size() - 1; nodeId>=0; nodeId--){
                if (operatorToRemove && isOperator(nodeId)) {
                    if (operatorLast(nodeId)){
                        if(isTheOnlyNode(nodeId)){
                            addVisitedNode(emergencyNode);
                        } else
                            edges.remove(nodeId - 1);
                    } else if(operatorInside(nodeId)){
                        Node startNode = edges.get(nodeId - 1).getStartNode();
                        Node endNode = edges.get(nodeId).getEndNode();
                        Edge newEdge = new Edge(startNode,endNode,0f);
                        edges.remove(nodeId);
                        edges.remove(nodeId-1);
                        addEdgeToPosition(nodeId - 1,newEdge);
                    }
                    else if (operatorFirst(nodeId)){
                        edges.remove(nodeId);
                    }
                    operatorToRemove = false;
                    visitedNodes.remove(nodeId);
                    equationPower--;
                }
            }
        }
    }

    private boolean isOperator(int nodeId) {
        return visitedNodes.get(nodeId).isOperator();
    }

    private boolean operatorLast(int nodeId) {
        return nodeId == visitedNodes.size()-1;
    }

    private boolean isTheOnlyNode(int nodeId) {
        return nodeId-1 == -1;
    }

    private boolean operatorInside(int nodeId) {
        return nodeId < visitedNodes.size()-1 && nodeId != 0;
    }

    private boolean operatorFirst(int nodeId) {
        return nodeId == 0;
    }

    @Override
    public String toString() {
        return "Ant{" +
                "id=" + id +
                ", eqationPower=" + equationPower +
                ", edges=" + "\r\n" + edges +
                ", visitedNodes=" + visitedNodes +
                '}';
    }
}