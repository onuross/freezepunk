package game.model;

/**
 * Represents a Miner citizen in the game.
 * Miners are responsible for extracting coal to keep the city warm.
 */
public class Miner extends Citizen {

    private static final long serialVersionUID = 1L;

    private static final int MIN_COAL_PRODUCTION = 11;
    private static final int MAX_COAL_PRODUCTION_BOUND = 18; // Exclusive [11, 18)
    private static final double EXTRA_FOOD_PORTION = 0.5;

    /**
     * Default constructor for Miner.
     * Initializes the citizen with the job title "Miner".
     */
    public Miner() {
        super("Miner");
    }

    /**
     * Calculates the daily coal production of a miner.
     * Daily production ranges randomly between 11 and 17 units of coal.
     * If the miner is sick, they cannot work and produce 0.
     *
     * @return The amount of coal produced today.
     */
    @Override
    public int produce() {
        if (isSick()) {
            return 0;
        }
        return random.nextInt(MIN_COAL_PRODUCTION, MAX_COAL_PRODUCTION_BOUND);
    }

    /**
     * Calculates the daily food consumption of a miner.
     * Miners consume an additional 0.5 portion compared to standard citizens.
     *
     * @return The total meat portions consumed by the miner today.
     */
    @Override
    public double eat() {
        return super.eat() + EXTRA_FOOD_PORTION;
    }
}