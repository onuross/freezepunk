package game.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Taskbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import game.except.GameOver;
import game.model.Citizen;
import game.model.City;

/**
 * Main game window of Freezepunk.
 * Handles daily worker allocation, status updates, saving/loading, and
 * highscores.
 */
public class GameWindow {

    private static final String SAVE_FILE = "fpGameSave.dat";
    private static final String HIGHSCORE_FILE = "highscore.dat";

    private City theCity;
    private int min, lum, hun; // Daily allocation inputs
    private List<Integer> dashboard; // Highscore list at the end

    private JFrame frame;
    private JButton nextDayButton, saveGameButton, loadGameButton, sortButton, restartButton;
    private JLabel dayLabel;
    private JPanel allocPanel, bottomButtonPanel, eastPanel;
    private JTextArea leftStatusArea, logArea;
    private JTextField miner, lumber, hunter;
    private String minInput, lumInput, hunInput, dailyLog, avgInfo;
    private JScrollPane leftScroll;

    /**
     * Constructor for starting a new game with custom worker allocation.
     * 
     * @param startMiner  Initial number of miners.
     * @param startLumber Initial number of lumberjacks.
     * @param startHunter Initial number of hunters.
     */
    public GameWindow(int startMiner, int startLumber, int startHunter) {
        theCity = new City(startMiner, startLumber, startHunter);
        startGUI();
    }

    /**
     * Constructor for loading an existing game.
     * 
     * @param loadCity The city object loaded from the save file.
     */
    public GameWindow(City loadCity) {
        this.theCity = loadCity;
        startGUI();
    }

    /**
     * Initializes the main game GUI components and layout.
     */
    private void startGUI() {
        // Main Frame
        frame = new JFrame("Freezepunk by OST!");
        // Set Application & Window Icon
        try {
            File iconFile = new File("assets/logo.png");
            if (iconFile.exists()) {
                Image appIcon = ImageIO.read(iconFile);
                frame.setIconImage(appIcon);
                if (Taskbar.isTaskbarSupported()) {
                    Taskbar taskbar = Taskbar.getTaskbar();
                    if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                        taskbar.setIconImage(appIcon);
                    }
                }
            } else {
                System.err.println("Icon file not found at: " + iconFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
        }
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Day - NORTH
        dayLabel = new JLabel();
        dayLabel.setText("<html><b>Day: " + theCity.getDay() + "</b></html>");
        dayLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dayLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        frame.add(dayLabel, BorderLayout.NORTH);

        // Daily Logs - CENTER
        logArea = new JTextArea(10, 30);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        logArea.setText("Press Next Day to Begin!\n");
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Daily Log"));
        frame.add(logScroll, BorderLayout.CENTER);

        // Bottom - Load/Save Buttons - SOUTH
        saveGameButton = new JButton("Save");
        loadGameButton = new JButton("Load");
        sortButton = new JButton("Sort Citizens");
        restartButton = new JButton("Restart");

        bottomButtonPanel = new JPanel(new FlowLayout());
        bottomButtonPanel.add(saveGameButton);
        bottomButtonPanel.add(loadGameButton);
        bottomButtonPanel.add(sortButton);
        bottomButtonPanel.add(restartButton);
        frame.add(bottomButtonPanel, BorderLayout.SOUTH);

        // City Status - WEST
        leftStatusArea = new JTextArea();
        leftStatusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        leftStatusArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftStatusArea.setEditable(false);
        leftScroll = new JScrollPane(leftStatusArea);
        leftScroll.setBorder(BorderFactory.createTitledBorder("City Status"));
        frame.add(leftScroll, BorderLayout.WEST);

        // EAST Panel
        eastPanel = new JPanel(new BorderLayout(10, 10));

        // Allocation Panel, NORTH-EAST
        allocPanel = new JPanel(new GridLayout(3, 2, 8, 12));
        allocPanel.setBorder(BorderFactory.createTitledBorder("Worker Allocation"));
        allocPanel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        miner = new JTextField();
        lumber = new JTextField();
        hunter = new JTextField();

        allocPanel.add(new JLabel("Miner"));
        allocPanel.add(miner);
        allocPanel.add(new JLabel("Lumberjack"));
        allocPanel.add(lumber);
        allocPanel.add(new JLabel("Hunter"));
        allocPanel.add(hunter);

        eastPanel.add(allocPanel, BorderLayout.NORTH);

        // Next Day Button, SOUTH-EAST
        nextDayButton = new JButton("Next Day");
        JPanel nextDayWrapper = new JPanel(new FlowLayout());
        nextDayWrapper.add(nextDayButton);
        eastPanel.add(nextDayWrapper, BorderLayout.SOUTH);

        frame.add(eastPanel, BorderLayout.EAST);

        // Button Listeners
        saveGameButton.addActionListener(e -> handleSaveGame());
        loadGameButton.addActionListener(e -> handleLoadGame());
        sortButton.addActionListener(e -> handleSortCitizens());
        restartButton.addActionListener(e -> handleRestartGame());
        nextDayButton.addActionListener(e -> handleNextDay());

        // Start with yesterday's allocation + healed citizens
        refreshCityStatusUI();

        frame.setVisible(true);
    }

    /**
     * Updates the city status area, day label, and worker text fields.
     */
    private void refreshCityStatusUI() {
        avgInfo = getAvgInfo();
        leftStatusArea.setText(theCity.toString() + avgInfo);
        dayLabel.setText("<html><b>Day: " + theCity.getDay() + "</b></html>");
        miner.setText(String.valueOf(theCity.getMinerCount()));
        lumber.setText(String.valueOf(theCity.getLumberjackCount()));
        hunter.setText(String.valueOf(theCity.getHunterCount()));
    }

    /**
     * Saves the current City state to a file.
     */
    private void handleSaveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(theCity);
            JOptionPane.showMessageDialog(frame, "Game Saved!");
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(frame, "Error during save game: " + exc.getMessage());
        }
    }

    /**
     * Loads a saved City state from a file and refreshes the GUI.
     */
    private void handleLoadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            theCity = (City) in.readObject();
            JOptionPane.showMessageDialog(frame, "Game Loaded!");
            refreshCityStatusUI();
            nextDayButton.setEnabled(true);
            saveGameButton.setEnabled(true);
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(frame, "Error during loading: " + exc.getMessage());
        }
    }

    /**
     * Sorts the city's population and appends the sorted list to the daily log.
     */
    private void handleSortCitizens() {
        try {
            List<Citizen> popList = theCity.getPopulation();
            Collections.sort(popList); // Uses compareTo method of Citizen Class
            logArea.append("\n   ======== Sorted Citizen List ========\n");
            int i = 1;
            for (Citizen c : popList) {
                logArea.append(String.format("%3d. %s\n", i, c.toString()));
                i++;
            }
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(frame, "Error during sorting the citizens: " + exc.getMessage());
        }
    }

    /**
     * Disposes the current window and opens the StartWindow.
     */
    private void handleRestartGame() {
        try {
            frame.dispose();
            new StartWindow();
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(frame, "Error during restart: " + exc.getMessage());
        }
    }

    /**
     * Validates worker inputs, advances the game by one day, and handles GameOver
     * state.
     */
    private void handleNextDay() {
        try {
            dailyLog = "Good Morning, its cold today!\n";

            minInput = miner.getText();
            hunInput = hunter.getText();
            lumInput = lumber.getText();

            if (minInput.isBlank() || lumInput.isBlank() || hunInput.isBlank()) {
                JOptionPane.showMessageDialog(frame,
                        "Please fill in all worker allocations!\nTo assign no workers, enter 0.");
                return;
            }

            min = Integer.parseInt(minInput);
            hun = Integer.parseInt(hunInput);
            lum = Integer.parseInt(lumInput);

            if (min < 0 || lum < 0 || hun < 0) {
                JOptionPane.showMessageDialog(frame, "Negative Workers... How?");
                return;
            }

            if (min + hun + lum != theCity.getActiveWorker()) {
                JOptionPane.showMessageDialog(frame, "Can't assign more/less workers than available!");
                return;
            }

            theCity.allocateWorkers(min, lum, hun);

            dailyLog += theCity.nextDay();
            logArea.setText(dailyLog);
            refreshCityStatusUI();

        } catch (GameOver gg) {
            JOptionPane.showMessageDialog(frame, gg.getMessage());
            nextDayButton.setEnabled(false);
            saveGameButton.setEnabled(false);
            updateAndDisplayHighscores();
        } catch (NumberFormatException num) {
            JOptionPane.showMessageDialog(frame, "Please Enter a Number to allocate the workers!");
        }
    }

    /**
     * Loads, updates, saves, and displays the highscore list upon Game Over.
     */
    @SuppressWarnings("unchecked")
    private void updateAndDisplayHighscores() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE))) {
            dashboard = (ArrayList<Integer>) inputStream.readObject();
        } catch (Exception gg_e) {
            JOptionPane.showMessageDialog(frame, "This is the first time this game being played, thanks!");
            dashboard = new ArrayList<>();
        }

        dashboard.add(theCity.getDay());
        dashboard.sort(Collections.reverseOrder());

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE))) {
            outputStream.writeObject(dashboard);
        } catch (Exception gg_e) {
            JOptionPane.showMessageDialog(frame, "Highscore couldn't be saved!");
        }

        String dashString = "\n\n=== Highscores ===\n";
        int limit = 8;
        if (limit > dashboard.size()) {
            limit = dashboard.size();
        }
        for (int i = 1; i < limit + 1; i++) {
            dashString += i + ". " + dashboard.get(i - 1) + " Days\n";
        }

        logArea.append(dashString);
    }

    /**
     * Creates a String of daily average consumption & production per worker
     * information to help the player.
     * 
     * @return String of avg. consumption & production per worker.
     */
    private String getAvgInfo() {
        return "\nAvg. Production Per Worker:" +
                "\n~14 Coal/day" +
                "\n~10 Wood/day" +
                "\n~6  Meat/day" +
                "\n------------------------\nConsumption" +
                "\nWood Per Hut: " + theCity.getWoodPerHut() +
                "\nWood needed to maintain Huts: " + (theCity.getRepairPerHut() * theCity.getHutCount()) +
                "\nDaily Meat Portions Needed: ~" + theCity.getPopulation().size() +
                "\nDaily Coal Consumption: ~" + (theCity.getPopulation().size() * 4 + theCity.getDay() * 12);
    }
}