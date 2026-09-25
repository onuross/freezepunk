package game.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.except.GameOver;
import game.model.City;

/**
 * JUnit 5 test suite for verifying the core business logic of the {@link City}
 * class.
 * Ensures population initialization, worker allocation, resource consumption,
 * and game-over conditions work as expected.
 */
public class CityTest {

    private City testCity;

    /**
     * Sets up a fresh City instance before each test execution.
     */
    @BeforeEach
    public void setUp() {
        testCity = new City();
    }

    /**
     * Verifies that the default population is initialized to 50 healthy citizens.
     */
    @Test
    public void testInitialPopulation() {
        assertEquals(50, testCity.getPopulation().size(), "Initial population should be exactly 50.");
        assertEquals(50, testCity.getActiveWorker(), "All 50 workers should be healthy initially.");
    }

    /**
     * Tests the allocateWorkers method to ensure healthy citizens are reassigned
     * accurately.
     */
    @Test
    public void testWorkerAllocation() {
        // Allocate 20 Miners, 15 Lumberjacks, 15 Hunters
        testCity.allocateWorkers(20, 15, 15);

        assertEquals(20, testCity.getMinerCount(), "Miner count should be 20.");
        assertEquals(15, testCity.getLumberjackCount(), "Lumberjack count should be 15.");
        assertEquals(15, testCity.getHunterCount(), "Hunter count should be 15.");
    }

    /**
     * Verifies that advancing a day consumes coal as expected without prematurely
     * ending the game.
     */
    @Test
    public void testNextDayCoalConsumption() {
        double initialCoal = testCity.getStock().get("Coal").getQuantity();

        try {
            testCity.allocateWorkers(10, 20, 20);
            testCity.nextDay();
            double newCoal = testCity.getStock().get("Coal").getQuantity();
            // Coal should decrease after a day
            assertTrue(initialCoal > newCoal, "Coal quantity should decrease after passing a day.");
        } catch (GameOver e) {
            fail("Game should not be over on the first day with default resources.");
        }
    }

    /**
     * Verifies that a GameOver exception is thrown when the city runs out of coal.
     */
    @Test
    public void testGameOverWhenCoalDepleted() {
        testCity.getStock().get("Coal").setQuantity(0.0);
        testCity.allocateWorkers(0, 25, 25); // 0 Miners so no new coal is produced

        assertThrows(GameOver.class, () -> testCity.nextDay(),
                "GameOver exception must be thrown when coal drops to or below zero.");
    }
}