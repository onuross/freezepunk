package game.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Taskbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import game.model.City;

/**
 * Represents the initial start window of the Freezepunk game.
 * This class handles the initial allocation of the 50 survivors
 * and displays the high score dashboard from previous games.
 */
public class StartWindow {

    private static final String SAVE_FILE = "fpGameSave.dat";
    private static final String HIGHSCORE_FILE = "highscore.dat";

    private JFrame frame;
    private JPanel allocPanel;
    private JTextField miner, hunter, lumber;
    private JLabel infoLabel;
    private JButton startButton, loadButton;
    private List<Integer> scores;
    private JTextArea dashArea;

    /**
     * Default constructor for StartWindow.
     * Automatically initializes and displays the GUI.
     */
    public StartWindow() {
        startGUI();
    }

    /**
     * Reads the high score data from the "highscore.dat" file and formats it.
     * Displays up to the top 8 scores. If no file exists,
     * returns a welcome message.
     * 
     * @return A string containing the high score dashboard as numbered list.
     */
    @SuppressWarnings("unchecked")
    private String getDashboard() {
        String dashString = "=== Highscores ===\n";
        int limit = 8;

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE))) {
            scores = (ArrayList<Integer>) inputStream.readObject();

            if (limit > scores.size()) {
                limit = scores.size();
            }

            for (int i = 1; i < limit + 1; i++) {
                dashString += i + ". " + scores.get(i - 1) + " Days\n";
            }

        } catch (Exception e) {
            dashString += "No scores yet! \nYou are the first player yet!";
        }

        return dashString;
    }

    /**
     * Initializes the GUI components.
     * Sets up the layout, text fields, labels, and the start/load game buttons.
     * Validates that the user allocates exactly 50 Workers.
     */
    private void startGUI() {
        frame = new JFrame("Freezepunk!");

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
        frame.setSize(610, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 0));

        dashArea = new JTextArea(getDashboard());
        dashArea.setEditable(false);
        dashArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dashArea.setBackground(frame.getBackground());
        frame.add(dashArea, BorderLayout.WEST);

        infoLabel = new JLabel(
                "<html>You have <b>50 survivors</b>." +
                        "<br>Assign their starting jobs.<br>" +
                        "<br>Reallocate workers daily." +
                        "<br>Watch your resources.<br>" +
                        "<small><i>(if you want to survive…)</i></small>" +
                        "<br><br><i>- OST</i>" +
                        "</html>");
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        frame.add(infoLabel, BorderLayout.CENTER);

        allocPanel = new JPanel();
        allocPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        allocPanel.setLayout(new GridLayout(4, 2, 5, 5));

        miner = new JTextField();
        lumber = new JTextField();
        hunter = new JTextField();

        // Allocation Panel
        allocPanel.add(new JLabel("<html><b>Miner:</b></html>"));
        allocPanel.add(miner);
        allocPanel.add(new JLabel("<html><b>Lumberjack:</b></html>"));
        allocPanel.add(lumber);
        allocPanel.add(new JLabel("<html><b>Hunter:</b></html>"));
        allocPanel.add(hunter);

        // Buttons at the right corner
        loadButton = new JButton("<html><i>Load Game</i></html>");
        allocPanel.add(loadButton);
        startButton = new JButton("<html><b><i>Start Game</i></b></html>");
        allocPanel.add(startButton);

        frame.add(allocPanel, BorderLayout.EAST);

        // Button Actions
        startButton.addActionListener(e -> handleStartGame());
        loadButton.addActionListener(e -> handleLoadGame());

        frame.setVisible(true);
    }

    /**
     * Allocates the workers according to inputs and validates that exactly 50
     * workers are assigned.
     */
    private void handleStartGame() {
        try {
            int min = Integer.parseInt(miner.getText());
            int lum = Integer.parseInt(lumber.getText());
            int hun = Integer.parseInt(hunter.getText());

            if (min < 0 || lum < 0 || hun < 0) {
                JOptionPane.showMessageDialog(frame, "Negative Workers... How?");
                return;
            }

            if (min + lum + hun > 50) {
                JOptionPane.showMessageDialog(frame, "You only have 50 Workers, read the left side!");
                return;
            }
            if (min + lum + hun < 50) {
                JOptionPane.showMessageDialog(frame, "You have to allocate all 50 Workers to start!");
                return;
            }

            new GameWindow(min, lum, hun);
            frame.dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please Enter a Number to allocate the workers!");
        }
    }

    /**
     * Loads a saved game state from file and launches the main GameWindow.
     */
    private void handleLoadGame() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            City loadCity = (City) inputStream.readObject();
            new GameWindow(loadCity);
            frame.dispose();
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(frame, "Error during loading: " + exc.getMessage());
        }
    }
}