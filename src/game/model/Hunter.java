package game.model;

/**
 * Represents a Hunter citizen in the game.
 * Hunters are responsible for providing meat to feed the population.
 */
public class Hunter extends Citizen {

    private static final long serialVersionUID = 1L;

    private static final int MIN_MEAT_PRODUCTION = 5;
    private static final int MAX_MEAT_PRODUCTION_BOUND = 8; // Exclusive (5 to 7)

    /**
     * Default constructor for Hunter.
     * Initializes the citizen with the job title "Hunter".
     */
    public Hunter() {
        super("Hunter");
    }

    /**
     * Calculates the daily meat production of a hunter.
     * Daily production ranges randomly between 5 and 7 units of meat.
     * If the hunter is sick, they cannot work and produce 0.
     *
     * @return The amount of meat produced today.
     */
    @Override
    public int produce() {
        if (isSick()) {
            return 0;
        }
        return random.nextInt(MIN_MEAT_PRODUCTION, MAX_MEAT_PRODUCTION_BOUND);
    }
}