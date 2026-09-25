package game;

import game.gui.StartWindow;

/**
 * The entry point for the Freezepunk game.
 * Contains the main method which starts the game.
 */
public class Main {

    /**
     * The main method that launches the game.
     * It initializes the starting GUI (StartWindow).
     * 
     * @param args Default main arguments, not used.
     */
    public static void main(String[] args) {
        new StartWindow();
    }
}