package game.model;

/**
 * Represents the Wood resource in the game.
 * Extends the abstract class {@link Resource}.
 */
public class Wood extends Resource {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor for Wood.
     * Initializes with a default quantity of 200.0.
     */
    public Wood() {
        super("Wood", 200.0);
    }

    /**
     * Overloaded constructor for Wood.
     * 
     * @param quantity The desired quantity of Wood to start with.
     */
    public Wood(double quantity) {
        super("Wood", quantity);
    }
}