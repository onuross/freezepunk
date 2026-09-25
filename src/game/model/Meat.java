package game.model;

/**
 * Represents the Meat resource in the game.
 * Extends the abstract class {@link Resource}.
 */
public class Meat extends Resource {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor for Meat.
     * Initializes with a default quantity of 140.0.
     */
    public Meat() {
        super("Meat", 140.0);
    }

    /**
     * Overloaded constructor for Meat.
     * 
     * @param quantity The desired quantity of Meat to start with.
     */
    public Meat(double quantity) {
        super("Meat", quantity);
    }
}