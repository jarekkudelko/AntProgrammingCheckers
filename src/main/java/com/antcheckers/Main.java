package com.antcheckers;

import com.antcheckers.checkers.Tournament;

public class Main {
    public static void main(String[] args) {
//        AntColony antColony = new AntColony();
//        antColony.run();
        Tournament tournament = new Tournament();
        tournament.getWinningWeights();
    }
}