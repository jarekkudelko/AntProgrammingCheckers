package com.antcheckers;


import com.antcheckers.antcolony.AntColonySystem;

public class Main {
    boolean localUpdatePheromones;  //czy używać lokalnej aktualizacji śladu feromonowego

    public static void main(String[] args) {
//        int c0Min = -3;
//        int c0Max = 3;
//        int numberOfAnts = 3;               //ilość mrówek (graczy)
//        int numberOfCycles = 1;             //liczba cykli
//        int numberOfIterationsInCycle = 5;  //maksymalna liczba iteracji w cyklu
//        float ro;       //współczynnik wyparowania śladu feromonowego (Ro)
//        float initialFeromonesValue;    //początkowa wartość feromonu
//        float q0 = 0.5f;                       //prawdopodobienstwo wyboru ekspoatacji
        AntColonySystem antColonySystem = new AntColonySystem();
        antColonySystem.run();
    }
}