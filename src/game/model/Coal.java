package game.model;

/**
 * Represents the Coal resource in the game.
 * Extends the abstract class {@link Resource}.
 */
public class Coal extends Resource {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor for Coal.
     * Initializes with a default quantity of 400.0.
     */
    public Coal() {
        super("Coal", 400.0);
    }

    /**
     * Overloaded constructor for Coal.
     * 
     * @param quantity The desired quantity of Coal to start with.
     */
    public Coal(double quantity) {
        super("Coal", quantity);
    }
}