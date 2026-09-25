package game.model;

/**
 * Represents a Lumberjack citizen in the game.
 * Lumberjacks are responsible for gathering wood to build and repair shelters.
 */
public class Lumberjack extends Citizen {

    private static final long serialVersionUID = 1L;

    private static final int MIN_WOOD_PRODUCTION = 5;
    private static final int MAX_WOOD_PRODUCTION_BOUND = 16; // Exclusive (5 to 15)

    /**
     * Default constructor for Lumberjack.
     * Initializes the citizen with the job title "Lumberjack".
     */
    public Lumberjack() {
        super("Lumberjack");
    }

    /**
     * Calculates the daily wood production of a lumberjack.
     * Daily production ranges randomly between 5 and 15 units of wood.
     * If the lumberjack is sick, they cannot work and produce 0.
     *
     * @return The amount of wood produced today.
     */
    @Override
    public int produce() {
        if (isSick()) {
            return 0;
        }
        return random.nextInt(MIN_WOOD_PRODUCTION, MAX_WOOD_PRODUCTION_BOUND);
    }
}