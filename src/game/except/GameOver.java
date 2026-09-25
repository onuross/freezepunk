package game.except;

/**
 * Custom checked exception indicating that the game has ended.
 * Thrown when critical survival conditions (such as coal depletion) are no
 * longer met.
 */
public class GameOver extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Default no-argument constructor.
     * Initializes the exception with a standard "Game Over!" message.
     */
    public GameOver() {
        super("Game Over!");
    }

    /**
     * Overloaded constructor with a custom cause message.
     * 
     * @param msg Detailed message explaining why the game ended.
     */
    public GameOver(String msg) {
        super("Game Over!\n" + msg);
    }
}