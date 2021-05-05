package com.antcheckers;

import com.antcheckers.antcolony.AntColony;
import com.antcheckers.checkers.Game;

public class Main {
    public static void main(String[] args) {
//        AntColony antColony = new AntColony();
//        antColony.run();
        Game game = new Game();
        game.generateGameTree(2);
    }
}