package game.model;

import java.io.Serializable;
import java.util.Random;

/**
 * Represents an abstract Citizen in the game.
 * All specific job roles (Miner, Hunter, Lumberjack) extend this class.
 * Implements {@link Comparable} for sorting and {@link Serializable} for
 * saving/loading game state.
 */
public abstract class Citizen implements Comparable<Citizen>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MIN_RECOVERY_DAYS = 1;
    private static final int MAX_RECOVERY_DAYS_BOUND = 4; // Exclusive [1, 4)
    private static final double BASE_PORTION = 1.0;

    protected final Random random = new Random();

    private String job;
    private boolean isSick = false;
    private int daysLeft = 0;

    /**
     * Default no-argument constructor.
     */
    protected Citizen() {
    }

    /**
     * Constructor to initialize a citizen with a specific job title.
     *
     * @param job The initial job title of the citizen.
     */
    protected Citizen(String job) {
        this.job = job;
    }

    /**
     * Gets the job title of the citizen.
     *
     * @return The current job of the citizen.
     */
    public String getJob() {
        return job;
    }

    /**
     * Sets the job title of the citizen.
     *
     * @param job The new job title for the citizen.
     */
    public void setJob(String job) {
        this.job = job;
    }

    /**
     * Gets the remaining days for the citizen to recover from sickness.
     *
     * @return The number of recovery days left.
     */
    public int getDaysLeft() {
        return daysLeft;
    }

    /**
     * Sets the remaining days for the citizen to recover.
     *
     * @param daysLeft The number of days left to recover.
     */
    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    /**
     * Checks if the citizen is currently sick.
     *
     * @return {@code true} if the citizen is sick, {@code false} otherwise.
     */
    public boolean isSick() {
        return isSick;
    }

    /**
     * Sets the sickness status of the citizen.
     *
     * @param isSick {@code true} to make the citizen sick, {@code false} to heal
     *               them.
     */
    public void setSick(boolean isSick) {
        this.isSick = isSick;
    }

    /**
     * Makes the citizen fall sick and assigns a random recovery period between 1
     * and 3 days.
     */
    public void fallSick() {
        this.isSick = true;
        this.daysLeft = random.nextInt(MIN_RECOVERY_DAYS, MAX_RECOVERY_DAYS_BOUND);
    }

    /**
     * Processes the daily recovery of the citizen.
     * Decreases the remaining recovery days by 1.
     * If no recovery days remain, the citizen becomes healthy again.
     */
    public void recover() {
        if (this.isSick) {
            daysLeft -= 1;
            if (daysLeft <= 0) {
                daysLeft = 0;
                isSick = false;
            }
        }
    }

    /**
     * Calculates the daily resource production of the citizen.
     * Must be implemented by job subclasses (Miner, Lumberjack, Hunter).
     *
     * @return The amount of resources produced today.
     */
    public abstract int produce();

    /**
     * Calculates the daily food consumption of the citizen.
     * Sick citizens consume between 1.0 and 2.0 portions.
     * Healthy citizens consume between 0.7 and 1.3 portions.
     *
     * @return The amount of meat portions consumed today.
     */
    public double eat() {
        if (isSick) {
            // Sick citizens eat between 1.0 and 2.0 portions (1.5 ± 0.5)
            return BASE_PORTION + random.nextDouble(0.0, 1.00000001);
        }

        // Healthy citizens eat between 0.7 and 1.3 portions (1.0 ± 0.3)
        return BASE_PORTION + random.nextDouble(-0.3, 0.30000001);
    }

    /**
     * Calculates the daily food consumption with a multiplier applied.
     *
     * @param bonusFactor A multiplier applied to the standard food consumption.
     * @return The adjusted amount of meat portions consumed today.
     */
    public double eat(double bonusFactor) {
        return eat() * bonusFactor;
    }

    /**
     * Returns a string representation of the citizen, including their job,
     * health status, and body temperature if sick.
     *
     * @return A formatted string representing the citizen's state.
     */
    @Override
    public String toString() {
        if (isSick) {
            return job + " (Sick, " + daysLeft + " days to recover, Current Body Temp: " + random.nextInt(37, 43)
                    + "°C)";
        }
        return job + " (Feeling good enough to work)";
    }

    /**
     * Compares this citizen with another citizen for sorting.
     * Sorting is performed primarily alphabetically by job title,
     * and secondarily by sickness status (healthy citizens precede sick ones).
     *
     * @param otherCitizen The other citizen to compare against.
     * @return A negative integer, zero, or a positive integer based on comparison.
     */
    @Override
    public int compareTo(Citizen otherCitizen) {
        int jobComp = this.job.compareTo(otherCitizen.getJob());

        if (jobComp != 0) {
            return jobComp;
        }

        return Boolean.compare(this.isSick(), otherCitizen.isSick());
    }
}