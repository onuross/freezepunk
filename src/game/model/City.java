package game.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.except.GameOver;

/**
 * Represents the City in the game, managing resources, population,
 * and daily progression.
 * Implements {@link Comparable} for sorting by days survived and
 * {@link Serializable} for saving/loading game state.
 */
public class City implements Comparable<City>, Serializable {

    private static final long serialVersionUID = 1L;

    // --- Game Constants (Magic Numbers Cleaned Up) ---
    private static final double BASE_SICK_CHANCE = 6.0;
    private static final double STARVATION_SICK_PENALTY = 30.0;
    private static final double HOMELESS_SICK_CHANCE = 25.0;
    private static final int SURVIVOR_ARRIVAL_INTERVAL = 10;
    private static final int REPAIR_START_DAY = 5;
    private static final int BASE_COAL_PER_CITIZEN = 4;
    private static final int DAILY_COAL_MULTIPLIER = 12;

    // --- City Attributes ---
    private int day = 1;
    private int hutCapacity = 0;
    private int hutCount = 0;
    private int woodPerHut = 15;
    private int citPerHut = 4;
    private int repairPerHut = 4;

    private Map<String, Resource> stock = new HashMap<>();
    private List<Citizen> population = new ArrayList<>();

    private final Random random = new Random();

    /**
     * Default no-argument constructor for City.
     * Initializes default starting resources and citizens.
     */
    public City() {
        initializeStock();

        for (int i = 0; i < 20; i++) {
            population.add(new Miner()); // 20 Miners
            if (i % 2 == 0) {
                population.add(new Lumberjack()); // 10 Lumberjacks
            }
            population.add(new Hunter()); // 20 Hunters
        }
    }

    /**
     * Overloaded constructor to start a city with custom starting workers.
     * Resource quantities are set to default.
     * 
     * @param miner      Number of starting miners.
     * @param lumberjack Number of starting lumberjacks.
     * @param hunter     Number of starting hunters.
     */
    public City(int miner, int lumberjack, int hunter) {
        initializeStock();

        for (int i = 0; i < miner; i++)
            population.add(new Miner());
        for (int i = 0; i < lumberjack; i++)
            population.add(new Lumberjack());
        for (int i = 0; i < hunter; i++)
            population.add(new Hunter());
    }

    /**
     * Helper method to initialize the default resource stock.
     */
    private void initializeStock() {
        stock.put("Coal", new Coal());
        stock.put("Wood", new Wood());
        stock.put("Meat", new Meat());
    }

    // --- Getters & Setters ---

    public int getHutCapacity() {
        return hutCapacity;
    }

    public void setHutCapacity(int hutCapacity) {
        this.hutCapacity = hutCapacity;
    }

    public int getHutCount() {
        return hutCount;
    }

    public void setHutCount(int hutCount) {
        this.hutCount = hutCount;
    }

    public int getWoodPerHut() {
        return woodPerHut;
    }

    public void setWoodPerHut(int woodPerHut) {
        this.woodPerHut = woodPerHut;
    }

    public int getCitPerHut() {
        return citPerHut;
    }

    public void setCitPerHut(int citPerHut) {
        this.citPerHut = citPerHut;
    }

    public int getRepairPerHut() {
        return repairPerHut;
    }

    public void setRepairPerHut(int repairPerHut) {
        this.repairPerHut = repairPerHut;
    }

    public Map<String, Resource> getStock() {
        return stock;
    }

    public void setStock(Map<String, Resource> stock) {
        this.stock = stock;
    }

    public List<Citizen> getPopulation() {
        return population;
    }

    public void setPopulation(List<Citizen> population) {
        this.population = population;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    // --- Business Logic Methods ---

    /**
     * Counts the number of healthy miners.
     * 
     * @return Number of healthy miners.
     */
    public int getMinerCount() {
        return (int) population.stream()
                .filter(c -> c.getJob().equals("Miner") && !c.isSick())
                .count();
    }

    /**
     * Counts the number of healthy hunters.
     * 
     * @return Number of healthy hunters.
     */
    public int getHunterCount() {
        return (int) population.stream()
                .filter(c -> c.getJob().equals("Hunter") && !c.isSick())
                .count();
    }

    /**
     * Counts the number of healthy lumberjacks.
     * 
     * @return Number of healthy lumberjacks.
     */
    public int getLumberjackCount() {
        return (int) population.stream()
                .filter(c -> c.getJob().equals("Lumberjack") && !c.isSick())
                .count();
    }

    /**
     * Calculates the total number of healthy, active workers available.
     * 
     * @return The count of citizens who are not sick.
     */
    public int getActiveWorker() {
        return (int) population.stream().filter(c -> !c.isSick()).count();
    }

    /**
     * Allocates the healthy workers to specific jobs.
     * 
     * @param min The number of workers to assign as Miners.
     * @param lum The number of workers to assign as Lumberjacks.
     * @param hun The number of workers to assign as Hunters.
     */
    public void allocateWorkers(int min, int lum, int hun) {
        int minCount = min;
        int lumCount = lum;
        int hunCount = hun;

        for (int i = 0; i < population.size(); i++) {
            Citizen c = population.get(i);
            if (c.isSick()) {
                continue; // Cannot reallocate sick citizens
            }

            if (minCount > 0) {
                population.set(i, new Miner());
                minCount--;
            } else if (lumCount > 0) {
                population.set(i, new Lumberjack());
                lumCount--;
            } else if (hunCount > 0) {
                population.set(i, new Hunter());
                hunCount--;
            }
        }
    }

    /**
     * Processes the end of the current day and simulates the events of the next
     * day.
     * Handles resource production, consumption, sickness, and population growth.
     * 
     * @return Daily log string summarizing the day's events.
     * @throws GameOver If the city runs out of coal.
     */
    public String nextDay() throws GameOver {
        StringBuilder dailyLog = new StringBuilder("=== Daily Report ===");

        double sickPercent = BASE_SICK_CHANCE;
        int starvingPeople = 0;

        double consumedMeat = 0;
        double consumedWood = 0;
        double repairCost = 0;

        int dailyCoal = 0;
        int dailyWood = 0;
        int dailyMeat = 0;

        double oldCoal = stock.get("Coal").getQuantity();
        double oldMeat = stock.get("Meat").getQuantity();
        double oldWood = stock.get("Wood").getQuantity();

        // 1. Starvation Check
        if (oldMeat <= 0) {
            sickPercent += STARVATION_SICK_PENALTY;
            oldMeat = 0;
            dailyLog.append("\nWarning: People are starving!.. \n⇒ Sickness chance is highly increased!");
        }

        day++;

        // 2. Survivor Arrivals
        if (day % SURVIVOR_ARRIVAL_INTERVAL == 0) {
            int newCitizens = random.nextInt(8, 17);
            dailyLog.append("\n A group (+").append(newCitizens).append(") of survivors arrived!\n");
            for (int i = 0; i < newCitizens; i++) {
                Citizen newGuy = new Miner();
                newGuy.fallSick();
                population.add(newGuy);
            }
        }

        // 3. Accommodation & Building
        int currentSize = population.size();
        while (currentSize > hutCapacity && oldWood >= woodPerHut) {
            hutCapacity += citPerHut;
            hutCount++;
            consumedWood += woodPerHut;
            oldWood -= woodPerHut;
        }

        if (day > REPAIR_START_DAY) {
            repairCost = (hutCount * repairPerHut);
        }

        if (currentSize > hutCapacity) {
            dailyLog.append("\nNot enough Huts for every Citizen...\nChances of falling sick is increased heavily.");
            sickPercent = HOMELESS_SICK_CHANCE;
        }

        // 4. Daily Citizen Loop (Production, Consumption, Sickness)
        for (Citizen citizen : population) {
            // Health mechanics
            if (citizen.isSick()) {
                citizen.recover();
            } else {
                if (sickPercent > random.nextInt(1, 101)) {
                    citizen.fallSick();
                }
            }

            // Production mechanics
            int production = citizen.produce();
            switch (citizen.getJob()) {
                case "Miner":
                    dailyCoal += production;
                    break;
                case "Hunter":
                    dailyMeat += production;
                    break;
                case "Lumberjack":
                    dailyWood += production;
                    break;
            }

            // Eating mechanics
            double portion = citizen.eat();
            if (oldMeat - consumedMeat >= portion) {
                consumedMeat += portion;
            } else {
                starvingPeople++;
                consumedMeat = oldMeat;
                citizen.fallSick();
            }

        }

        if (starvingPeople > 0) {
            dailyLog.append("\nWarning: Not enough food portions!\nSo ")
                    .append(starvingPeople).append(" citizens became sick!");
        }

        // 5. Coal Consumption & Game Over Check
        double consumedCoal = (currentSize * BASE_COAL_PER_CITIZEN)
                + random.nextInt(-10, 11)
                + (DAILY_COAL_MULTIPLIER * day);

        if (oldWood + dailyWood < repairCost) {
            hutCount = Math.max(0, hutCount - 1);
            hutCapacity = hutCount * citPerHut;
            dailyLog.append("\nWarning: Not enough wood for repairs! A hut collapsed!");
        }

        double newCoalQuantity = oldCoal + dailyCoal - consumedCoal;
        double newMeatQuantity = oldMeat + dailyMeat - consumedMeat;
        double newWoodQuantity = Math.max(0, oldWood + dailyWood - repairCost);

        if (newCoalQuantity <= 0) {
            throw new GameOver("No Coal, People died freezing!…");
        }

        // 6. Update Resources
        stock.get("Coal").setQuantity(newCoalQuantity);
        stock.get("Wood").setQuantity(newWoodQuantity);
        stock.get("Meat").setQuantity(newMeatQuantity);

        // 7. Compile Report
        dailyLog.append("\n=========================")
                .append("\nMining Report:\n")
                .append(String.format("%-25s %d\n", "Miner Production:", dailyCoal))
                .append(String.format("%-25s %.1f\n", "Daily Coal Usage:", consumedCoal))
                .append(String.format("%-25s %.1f\n", "Coal Left:", newCoalQuantity))
                .append("\n=========================")
                .append("\nLogging Report:\n")
                .append(String.format("%-25s %d\n", "Lumberjack Production:", dailyWood))
                .append(String.format("%-25s %.1f\n", "Daily Wood Usage:", consumedWood + repairCost))
                .append(String.format("%-25s %.1f\n", "Wood Left:", newWoodQuantity))
                .append("\n=========================")
                .append("\nHunting Report:\n")
                .append(String.format("%-25s %d\n", "Hunter Production:", dailyMeat))
                .append(String.format("%-25s %.1f\n", "Daily Meat Consumed:", consumedMeat))
                .append(String.format("%-25s %.1f\n", "Meat Left:", newMeatQuantity));

        return dailyLog.toString();
    }

    /**
     * Compares this city to another based on the days survived.
     * 
     * @param anoCity The other city to compare.
     * @return Result of the integer comparison.
     */
    @Override
    public int compareTo(City anoCity) {
        return Integer.compare(this.day, anoCity.day);
    }

    /**
     * Returns a string representation of the City's current state.
     * 
     * @return A formatted string representation.
     */
    @Override
    public String toString() {
        double coal = stock.get("Coal").getQuantity();
        double wood = stock.get("Wood").getQuantity();
        double meat = stock.get("Meat").getQuantity();
        long sick = population.stream().filter(Citizen::isSick).count();

        return "\n----People----\n" +
                String.format("%-20s %d\n", "Population: ", population.size()) +
                String.format("%-20s %d\n", "Active Workers: ", getActiveWorker()) +
                String.format("%-20s %d\n", "Sick Citizens: ", sick) +
                "\n----Workers----\n" +
                String.format("%-20s %d\n", "Miner: ", getMinerCount()) +
                String.format("%-20s %d\n", "Lumberjack: ", getLumberjackCount()) +
                String.format("%-20s %d\n", "Hunter: ", getHunterCount()) +
                "\n---Accommodation---\n" +
                String.format("%-20s %d\n", "Huts: ", hutCount) +
                String.format("%-20s %d\n", "Hut Capacity: ", hutCapacity) +
                "\n----Resources----\n" +
                String.format("%-20s %.1f\n", "Coal: ", coal) +
                String.format("%-20s %.1f\n", "Wood: ", wood) +
                String.format("%-20s %.1f\n", "Meat: ", meat);
    }
}
