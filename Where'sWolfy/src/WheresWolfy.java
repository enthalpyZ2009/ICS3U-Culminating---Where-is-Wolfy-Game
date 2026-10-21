package src;
/*
Name: Angela Zhang, Kaitlyn Lee
Date: 12 Jan 2026
File Name: WheresWolfy.file
Description: Java code for ICS3U culminating project - Where's Wolfy? 
*/

/*
 * Here is a bulleted list of all the extra features used in this program that were not taught in class:
 * - Graphical User Interface (Java AWT and Java Swing - ActionEvent, ActionListener, JFrame, Timer, Dialog, etc.)
 * - Java Sound API (javax.sound.sampled)
 * - ImageIcon class
 * - ArrayLists
 * - Try-catch statements 
 * - Return
 * 
 * A description of writing to and reading from files: 
 * - Used for the leaderboard
 * - In this program, writing to and reading from files was used to save, store and retrieve the data of the players’ results 
 * - Each difficulty (Easy, Medium, Hard) has its own text file to store each leaderboard 
 * - Each file hold the players’ data: names, steps taken, and time
 * When the leaderboard is displayed, the program reads the corresponding file and stores the data in three ArrayLists for processing and sorting
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class WheresWolfy extends JFrame {
    static JFrame frame;
    static JButton[][] buttons;
    static int playerR = 0, playerC = 0, steps = 0, gridSize = 15, currentTurn = 0;
    static int wolfR, wolfC;
    static JLabel stepsLabel, hintLabel, timeLabel, wolfyLabel;
    static JPanel homePanel;
    static String playerName, difficulty;
    static int seconds;
    static Timer timer;
    static ImageIcon wolfIcon, playerIcon, blockIcon, wallIcon, bombIcon;
    static int[][] visitedTurn;
    static boolean[][] walls, bombs;
    static boolean bombExploding = false;
    static int bombR, bombC;
    static Clip musicClip;
    static Clip lobbyMusicClip;
    static double musicVolume = 0.3; // background music volume (0.0 to 1.0) default as 50%
    static double sfxVolume = 0.5;   // sound effects volume (0.0 to 1.0) default as 50%
    static boolean musicEnabled = true; // toggle for music
    static boolean sfxEnabled = true;   // toggle for sound effects

    public static void main(String[] args) {
        // Ask for player name first
        playerName = JOptionPane.showInputDialog(
            null,
            "Enter your name:",
            "WheresWolfy",
            JOptionPane.PLAIN_MESSAGE
        );

        // If user cancels or enters nothing, exit
        if (playerName == null || playerName.trim().isEmpty()) {
            System.exit(0);
        }

        showRules();
        // Create the main JFrame
        frame = new JFrame("WheresWolfy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(550, 550);
        frame.setLocationRelativeTo(null);

        // Show home page
        homePage();

        frame.setVisible(true);
    }

        /**
     * Sets up the home page of the game.
     * The home page displays a welcome message for the player, a start game button, a settings button,
     * a leaderboard button and a help button.
     * The settings button allows the player to adjust their music and sound settings.
     * The leaderboard button displays the scoreboard for the player.
     * The help button reminds the player of the game rules.
     */
    public static void homePage() {
        frame.setTitle("Where's Wolfy? - Welcome");
        frame.setLayout(new BorderLayout());

        //the Where's Wolfy title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(92, 170, 120)); // forest green
        headerPanel.setBorder(BorderFactory.createEmptyBorder(35, 20, 35, 20));

        JLabel titleLabel = new JLabel("Where’s Wolfy!", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(headerPanel, BorderLayout.NORTH);

        homePanel = new JPanel();
        homePanel.setBackground(Color.WHITE);
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton startButton = new JButton("Start Game");
        startButton.setBackground(new Color(60, 180, 90)); 
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setFont(new Font("SansSerif", Font.BOLD, 25));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(260, 60));
        startButton.setMaximumSize(new Dimension(260, 60));


    
        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);
        Dimension buttonSize = new Dimension(220, 45);


        //Inside the setting Button, the user is able to adjust their music and sound settings. They does it by sliding the volume bar or switching on and off
        JButton settingsButton = new JButton("Settings");
        settingsButton.setBackground(new Color(70, 160, 200)); // blue
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setFocusPainted(true);
        settingsButton.setBorderPainted(false);
        settingsButton.setFont(buttonFont);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setPreferredSize(buttonSize);
        settingsButton.setMaximumSize(buttonSize);

        settingsButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                frame.remove(homePanel);

                titleLabel.setText("Settings");

                JPanel settingsPanel = new JPanel();
                settingsPanel.setBackground(Color.WHITE);
                settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
                settingsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

                // Music Panel
                JPanel musicPanel = new JPanel();
                musicPanel.setLayout(new BoxLayout(musicPanel, BoxLayout.Y_AXIS));
                musicPanel.setBackground(Color.WHITE);
                musicPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                    "Background Music",
                    javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("SansSerif", Font.BOLD, 16),
                    new Color(60, 60, 60)
                ));

                JCheckBox musicToggle = new JCheckBox("Enable Music", musicEnabled);
                musicToggle.setBackground(Color.WHITE);
                musicToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
                musicToggle.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        musicEnabled = musicToggle.isSelected();
                        if (musicEnabled) {
                            try { 
                                playLobbyMusic(); 
                            } catch (Exception ex) {}
                        } else {
                            stopMusic();
                            stopLobbyMusic();
                        }
                    }
                });

                JLabel musicVolLabel = new JLabel("Music Volume");
                musicVolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JSlider musicSlider = new JSlider(0, 100, (int)(musicVolume * 100));
                musicSlider.setMajorTickSpacing(25);
                musicSlider.setPaintTicks(true);
                musicSlider.setPaintLabels(true);
                musicSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
                musicSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent ev) {
                        musicVolume = musicSlider.getValue() / 100.0;
                        if (musicClip != null && musicClip.isRunning()){
                            setClipVolume(musicClip, musicVolume);
                        } 
                        if (lobbyMusicClip != null && lobbyMusicClip.isRunning()) {
                            setClipVolume(lobbyMusicClip, musicVolume);
                        }
                    }
                });

                musicPanel.add(Box.createVerticalStrut(10));
                musicPanel.add(musicToggle);
                musicPanel.add(Box.createVerticalStrut(10));
                musicPanel.add(musicVolLabel);
                musicPanel.add(musicSlider);

                settingsPanel.add(musicPanel);
                settingsPanel.add(Box.createVerticalStrut(20));

                // SFX Panel
                JPanel sfxPanel = new JPanel();
                sfxPanel.setLayout(new BoxLayout(sfxPanel, BoxLayout.Y_AXIS));
                sfxPanel.setBackground(Color.WHITE);
                sfxPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                    "Sound Effects",
                    javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("SansSerif", Font.BOLD, 16),
                    new Color(60, 60, 60)
                ));

                JCheckBox sfxToggle = new JCheckBox("Enable Sound Effects", sfxEnabled);
                sfxToggle.setBackground(Color.WHITE);
                sfxToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
                sfxToggle.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        sfxEnabled = sfxToggle.isSelected();
                    }
                });

                JLabel sfxVolLabel = new JLabel("SFX Volume");
                sfxVolLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                JSlider sfxSlider = new JSlider(0, 100, (int)(sfxVolume * 100));
                sfxSlider.setMajorTickSpacing(25);
                sfxSlider.setPaintTicks(true);
                sfxSlider.setPaintLabels(true);
                sfxSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
                sfxSlider.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent ev) {
                        sfxVolume = sfxSlider.getValue() / 100.0;
                    }
                });

                sfxPanel.add(Box.createVerticalStrut(10));
                sfxPanel.add(sfxToggle);
                sfxPanel.add(Box.createVerticalStrut(10));
                sfxPanel.add(sfxVolLabel);
                sfxPanel.add(sfxSlider);

                settingsPanel.add(sfxPanel);
                settingsPanel.add(Box.createVerticalStrut(25));

                // Close Button
                JButton closeBtn = new JButton("Close");
                closeBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
                closeBtn.setBackground(new Color(70, 160, 200));
                closeBtn.setForeground(Color.WHITE);
                closeBtn.setFocusPainted(false);
                closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                closeBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ev) {
                        frame.remove(settingsPanel);
                        frame.add(homePanel, BorderLayout.CENTER);
                        frame.repaint();
                        titleLabel.setText("Where's Wolfy?");
                    }
                });

                settingsPanel.add(closeBtn);

                // Swap panels
                frame.remove(homePanel);
                frame.add(settingsPanel, BorderLayout.CENTER);
  

            }
        });



        //Leaderboard Button shows the scoreboard
        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setBackground(new Color(255, 179, 0)); // Orange
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setOpaque(true);
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.setFont(buttonFont);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.setPreferredSize(buttonSize);
        leaderboardButton.setMaximumSize(buttonSize);

        playLobbyMusic();


        leaderboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    displayLeaderboard(difficulty);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        //Help button reminds user of the game rules if they click it
        JButton helpButton = new JButton("Game Rules");
        helpButton.setBackground(new Color(120, 120, 120)); // grey
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        helpButton.setMaximumSize(buttonSize);
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRules(); 
            }
        });


        //Under the quick settings, we have the difficulty setting. It is a dropdown menu of easy, medium and hard
        JLabel quickSettingsLabel = new JLabel("Quick Settings");
        quickSettingsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        quickSettingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setOpaque(false);

        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        // Dropdown menu of easy, medium and hard
        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.setFont(new Font("SansSerif", Font.PLAIN, 16));

        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultyBox);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            
            difficulty = (String) difficultyBox.getSelectedItem();

            // set grid size based on difficulty
            if (difficulty.equals("Easy")) {
                gridSize = 10;
            } else if (difficulty.equals("Medium")) {
                gridSize = 12;
            } else if (difficulty.equals("Hard")) {
                gridSize = 15;
            }
        
            frame.remove(homePanel);    
            frame.remove(headerPanel);
            initializeGame();      
            
            }
        });

        //Welcome [username] message
        JLabel playerLabel = new JLabel("Welcome, " + playerName + "!");
        playerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        playerLabel.setForeground(new Color(92, 170, 120));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        homePanel.add(Box.createVerticalStrut(5));
        homePanel.add(playerLabel);

        homePanel.add(Box.createVerticalStrut(20));
        homePanel.add(startButton);

        homePanel.add(Box.createVerticalStrut(15));
        homePanel.add(settingsButton);

        homePanel.add(Box.createVerticalStrut(15));
        homePanel.add(leaderboardButton);

        homePanel.add(Box.createVerticalStrut(15)); 
        homePanel.add(helpButton);

        homePanel.add(Box.createVerticalStrut(20));
        homePanel.add(quickSettingsLabel);

        homePanel.add(Box.createVerticalStrut(10));
        homePanel.add(difficultyPanel);


        homePanel.add(Box.createVerticalGlue());

        frame.add(homePanel, BorderLayout.CENTER);
        frame.setSize(700,600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



    /**
     * Resets the game state when the game is started or restarted.
     * Resets player and wolf positions, steps, and time.
     * Resets arrays for buttons, walls, bombs, and visited turns.
     * Resets the game music and plays the in game music.
     * Resets labels that display information throughout the game.
     * Starts the timer and adds the info panel to the frame.
     */
    public static void initializeGame() {

        // Reset player and wolf
        playerR = 0;
        playerC = 0;
        currentTurn = 0;
        steps = 0;
        seconds = 0;

        // Reset arrays
        buttons = new JButton[gridSize][gridSize];
        walls = new boolean[gridSize][gridSize];
        bombs = new boolean[gridSize][gridSize];
        visitedTurn = new int[gridSize][gridSize];

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                visitedTurn[r][c] = -1000;
            }
        }

        stopLobbyMusic();
        
        playInGameMusic();

        /* Resets Wolfy's position
         * Allows player to interact with images that appear when an event happens
         * Including bombs and walls
         */
        do {
            wolfR = (int)(Math.random() * gridSize);
            wolfC = (int)(Math.random() * gridSize);
        } while (wolfR <= gridSize / 2 && wolfC <= gridSize / 2);

        ImageIcon originalWolf = new ImageIcon("assets/wolf.png");
        Image scaledWolf = originalWolf.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        wolfIcon = new ImageIcon(scaledWolf);

        ImageIcon originalBlock = new ImageIcon("assets/blockIcon.png");
        Image scaledBlock = originalBlock.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        blockIcon = new ImageIcon(scaledBlock);

        ImageIcon originalWall = new ImageIcon("assets/wall.png"); 
        Image scaledWall = originalWall.getImage().getScaledInstance(90, 70, Image.SCALE_SMOOTH);
        wallIcon = new ImageIcon(scaledWall);

        ImageIcon originalBomb = new ImageIcon("assets/bomb.png");
        Image scaledBomb = originalBomb.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        bombIcon = new ImageIcon(scaledBomb);

        ImageIcon originalPlayer = new ImageIcon("assets/player.png");
        Image scaledPlayer = originalPlayer.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        playerIcon = new ImageIcon(scaledPlayer);


        //This is the gird label. The top row labelded from 1 to gridSize and the left column labeled from A to gridSize
                
        // It added extra rows and columns for the gird Label
        JPanel gridPanel = new JPanel(new GridLayout(gridSize + 1, gridSize + 1)) {
            
            public Dimension getPreferredSize() {
                int size = Math.min(getWidth(), getHeight());
                return new Dimension(size, size);
            }
        };

        //The top left panel is empty
        gridPanel.add(new JLabel(""));

        for (int j = 0; j < gridSize; j++) {
            JLabel label = new JLabel(String.valueOf(j + 1), SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            gridPanel.add(label);
        }

        for (int r = 0; r < gridSize; r++) {
            JLabel rowLabel = new JLabel(String.valueOf((char) ('A' + r)), SwingConstants.CENTER);
            rowLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            gridPanel.add(rowLabel);

            for (int c = 0; c < gridSize; c++) {
                JButton btn = new JButton();
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
                btn.setBorderPainted(true);
                btn.setBorder(BorderFactory.createLineBorder(new Color(34, 139, 34)));
                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        frame.add(gridPanel, BorderLayout.CENTER);

        // Initialize labels that display information throughout the game
        stepsLabel = new JLabel("Steps: 0");
        hintLabel = new JLabel("Find Wolfy! 🐺");
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hintLabel.setFont(new Font("SansSerif", Font.BOLD, 25));

        wolfyLabel = new JLabel("");
        wolfyLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        wolfyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        seconds = 0;
        startTimer();

        // InfoPanel contains the player name, steps and time. with name steps on the left, time on the right
        JPanel infoPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridLayout(2,1));
        leftPanel.add(new JLabel("Player: " + playerName));
        
        leftPanel.add(stepsLabel); 
        infoPanel.add(leftPanel, BorderLayout.WEST);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wolfyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(hintLabel);
        centerPanel.add(wolfyLabel);
        infoPanel.add(centerPanel, BorderLayout.CENTER);


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(timeLabel);
        infoPanel.add(rightPanel, BorderLayout.EAST);
        frame.add(infoPanel, BorderLayout.NORTH);
    
        //in the bottom Panel, we added the cheat button, give up button and game rule button
        //cheat button tells user where wolfy current location is but add 50 to step as penalty
        //give up button shows wolfy's location and end the game
        //game rule button reminds user of the game rule
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton cheatButton = new JButton("Cheat");
        cheatButton.setPreferredSize(new Dimension(120, 40));
        cheatButton.setBackground(new Color(255, 179, 0));
        cheatButton.setForeground(Color.WHITE);
        cheatButton.setFocusPainted(false);
        cheatButton.setToolTipText("Reveal Wolfy's location (adds 50 steps!)");
        cheatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons[wolfR][wolfC].setIcon(wolfIcon); 
                steps += 50; 
                stepsLabel.setText("Steps: " + steps);
                hintLabel.setText("You clicked the cheat button! 50 steps added.");
                hintLabel.setForeground(Color.decode("#FF7900"));
            }
        });


        UIManager.put("ToolTip.font", new Font("SansSerif", Font.BOLD, 16));

        // Allows user to choose to forfiet the game.
        // Score will not be recorded in the scoreboard
        JButton giveUpButton = new JButton("Give Up");
        giveUpButton.setPreferredSize(new Dimension(120, 40));
        giveUpButton.setBackground(new Color(229, 58, 53)); // red
        giveUpButton.setForeground(Color.WHITE);
        giveUpButton.setFocusPainted(false);
        giveUpButton.setToolTipText("Ends the game; result will not be recorded. ");
        
        giveUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buttons[wolfR][wolfC].setIcon(wolfIcon);

                hintLabel.setText("You gave up! Game Over");
                hintLabel.setForeground(Color.RED);
                
                endGame("You gave up the hunt.\nScore will NOT be recorded.","Game Over",JOptionPane.ERROR_MESSAGE);
            }
        });

        /*
         * Displays the game rules when button is cicked, just in case user needs help
         */
        JButton helpButton = new JButton("Game Rules");
        helpButton.setPreferredSize(new Dimension(120, 40));
        helpButton.setBackground(new Color(120, 120, 120)); // grey
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRules(); // call showRules method
            }
        });

        ;
        bottomPanel.add(cheatButton);
        bottomPanel.add(giveUpButton);
        bottomPanel.add(helpButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(750,750);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);//center the frame
        
        visitedTurn[playerR][playerC] = currentTurn;

        if (difficulty.equals("Easy")) {
            createWalls(0);
        } else if(difficulty.equals("Medium"))  {
            createWalls(5);
        } 
        else {
            createWalls(10);
        }
        
        if(difficulty.equals("Hard")){
            createBombs(5);
        }
        else{
            createBombs(0);
        }


        updateGrid();
        initializeKey();
    }
    /*
     * Allow player to move with W, A, S, D movement keybinds
     */
    public static void initializeKey(){
        InputMap im = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);//WHEN_IN_FOCUSED_WINDOW ensures that keys work everywhere inside the window no need to worry about with component has focus
        ActionMap am = frame.getRootPane().getActionMap();
        
        im.put(KeyStroke.getKeyStroke('w'), "up");
        am.put("up", new AbstractAction() { 
            public void actionPerformed(ActionEvent e) { 
                movePlayer(-1, 0); 
                
            } 
        });
        im.put(KeyStroke.getKeyStroke('s'), "down");
        am.put("down", new AbstractAction() { 
            public void actionPerformed(ActionEvent e) { 
                movePlayer(1, 0);
            } 
            
        });
        im.put(KeyStroke.getKeyStroke('a'), "left");
        am.put("left", new AbstractAction() { 
            public void actionPerformed(ActionEvent e) { 
                    movePlayer(0, -1); 
                
            } 
        });
        im.put(KeyStroke.getKeyStroke('d'), "right");
        am.put("right", new AbstractAction() { 
            public void actionPerformed(ActionEvent e) { 
                movePlayer(0, 1); 
                
            } 
        });
    }
     /**
     * This method updates the player's position when buttons are clicked
     * The player's new position is calculated based on the current position and the direction the player chooses
     * If the new position is within the grid, the player's position is updated and the steps label is increases by intervals of 1
     * The hint label is updated based on the new position and the grid updates to reflect the new position
     * Then checkWin method is called to check if the player has found Wolfy
     */
    public static void movePlayer(int dRow, int dCol) {// dRow is the change in row, dCol is the change in column
        // Calculate the new position of the player
        if (bombExploding) {
            return; 
        }
        int newR = playerR + dRow;
        int newC = playerC + dCol;
        
        
        playWalkSound();
        
        // Check if the new position is within the grid
        if (newR >= 0 && newR < gridSize && newC >= 0 && newC < gridSize) {
            if(!difficulty.equals("Easy")){
                if (currentTurn - visitedTurn[newR][newC] <= 5) {
                    hintLabel.setText("That path is blocked! 🚫");
                    hintLabel.setForeground(Color.decode("#FF7900"));
                    playReverseStepSound();
                    return;
                }
            }

            if (walls[newR][newC]) {
                hintLabel.setText("You bumped your head on a wall!");
                hintLabel.setForeground(Color.decode("#FF7900"));

                playWallSound(); 
                return;
            }

            currentTurn++;

            // Update the player's position
            playerR = newR;
            playerC = newC;

            visitedTurn[playerR][playerC] = currentTurn;
        
            if (bombs[playerR][playerC]) {
                triggerBomb();
                return;
            }

            steps++;
            stepsLabel.setText("Steps: " + steps);
            
            moveWolfy();

            // Update the grid
            updateGrid();
            
            checkLose();
            
            // Check if the player has found Wolfy
            checkWin();
        }
    }

    /**
     * Starts the timer.
     * The timer increments the seconds variable every second and updates the time label
     * The timer is able to inrement the minutes counter when seconds reach over 60s, and present it accordingly
     */
    public static void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seconds++;
                int min = seconds / 60;
                int sec = seconds % 60;
                String minStr = "";
                String secStr = "";

                if (min < 10) {
                    minStr = "0" + min;
                } else {
                    minStr = "" + min;
                }

                if (sec < 10) {
                    secStr = "0" + sec;
                } else {
                    secStr = "" + sec;
                }

                timeLabel.setText("Time: " + minStr + ":" + secStr);
            }
        });
        timer.start();
    }

    /**
     * The hint label is updated, based on the distance between Wolfy and the player
     * If the player is at the same spot as Wolfy, the hint label will say "You found Wolfy!"
     * If the distance is between 1 and 2, the hint label will say "Very hot!"
     * If the distance is between 3 and 4, the hint label will say "Getting warmer..."
     * Otherwise the hint label is set to "Cold"
     */
    public static void updateHint() {
        int dist = Math.abs(playerR - wolfR) + Math.abs(playerC - wolfC);
        if (dist == 0) {
            hintLabel.setText("");
        } else if (dist <= 2) {
            hintLabel.setText("Very hot! ♨️");
            hintLabel.setForeground(Color.RED);

        } else if (dist <= 4) {
            hintLabel.setText("Getting warmer... 🌡️");
            hintLabel.setForeground(Color.decode("#FF7900"));

        } else {
            hintLabel.setText("Cold... ❄️");
            hintLabel.setForeground(Color.BLUE);
        }
        
    }

    /**
     * The grid is updayed to reflect the state of the game
     * It displays the path which the player has walked
     * If the difficulty is medium or hard, movement of the player is restricted such that 
     * the player cannot reverse their steps for 5 moves
     * If the player moves into a wall or a bomb, the user interface will reflect these events.
     */
    public static void updateGrid() {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                buttons[r][c].setIcon(null);
                buttons[r][c].setContentAreaFilled(true);
                buttons[r][c].setOpaque(true);
                if (walls[r][c]) {
                    buttons[r][c].setIcon(wallIcon);
                    buttons[r][c].setBackground(Color.LIGHT_GRAY);
                    continue;
                }
                if (bombs[r][c]) {
                    if (bombExploding && r == bombR && c == bombC) {
                        buttons[r][c].setIcon(bombIcon);
                        buttons[r][c].setBackground(Color.RED);
                    } else {
                        buttons[r][c].setIcon(null);
                        buttons[r][c].setBackground(new Color(50, 159, 91)); // grass

                    }
                    continue;
                }
                if (r == playerR && c == playerC) {
                    buttons[r][c].setIcon(playerIcon);
                    buttons[r][c].setBackground(new Color(255, 255, 180));
                    continue;
                }
                if (!difficulty.equals("Easy") && currentTurn - visitedTurn[r][c] <= 5) {
                    buttons[r][c].setIcon(blockIcon);       // blocked icon
                    buttons[r][c].setBackground(Color.GRAY);
                }
                else if(visitedTurn[r][c] != -1000){
                    buttons[r][c].setBackground(Color.GRAY);
                } else {
                    buttons[r][c].setBackground(new Color(50, 159, 91)); // grass
                }
            }
        }

    }

    /*
     * Creates and plots walls onto random positions throughout the grid. 
     * For medium and hard difficulty, there will be 5 walls created.  
     * Prevents programme from placing walls at the start position, duplicate walls, or Wolfy's position
     * Prevents programme from generating adjacent walls which may trap the player or Wolfy
    */
    public static void createWalls(int wallAmount){
        walls = new boolean[gridSize][gridSize];

        int wallCount = 0;
        while(wallCount < wallAmount){
            int wallR = (int)(Math.random() * gridSize);
            int wallC = (int)(Math.random() * gridSize);

            if((wallR == 0 && wallC == 0) || walls[wallR][wallC] || (wallR == wolfR && wallC == wolfC)){
                continue;
            }

            if (
                (wallR > 0 && wallC > 0 && walls[wallR - 1][wallC] && walls[wallR][wallC - 1]) || (wallR > 0 && wallC < gridSize - 1 && walls[wallR - 1][wallC] && walls[wallR][wallC + 1]) || (wallR < gridSize - 1 && wallC > 0 && walls[wallR + 1][wallC] && walls[wallR][wallC - 1]) || (wallR < gridSize - 1 && wallC < gridSize - 1 && walls[wallR + 1][wallC] && walls[wallR][wallC + 1])) {
                continue;
            }

            walls[wallR][wallC] = true;
            wallCount++;

        }
    }

    /* 
     * Creates and plots bombs on random throughout the grid
     * For medium and hard difficulty, there will be 3 bombs created.  
     * Prevents programme from placing walls at the start position, on top of walls, or Wolfy's position
     */
    public static void createBombs(int bombAmount){
        bombs = new boolean[gridSize][gridSize];
        int bombCount = 0;
        while(bombCount < bombAmount){
            int bombR = (int)(Math.random() * gridSize);
            int bombC = (int)(Math.random() * gridSize);
            if ((bombR == 0 && bombC == 0) || walls[bombR][bombC] || (bombR == wolfR && bombC == wolfC)){
                continue;
            }

            bombs[bombR][bombC] = true;
            bombCount++;
        }
    }

    /* Method is called when player steps on a bomb (player's position = bomb position). 
     * Player is sent back to the starting position.
     */
    public static void triggerBomb() {
        bombExploding = true;
        bombR = playerR;
        bombC = playerC;

        hintLabel.setText("You stepped on a bomb! Back to start.");
        hintLabel.setForeground(Color.decode("#FF7900"));

        playBombSound();

        updateGrid();
        bombs[playerR][playerC] = false;
        new javax.swing.Timer(2000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    bombExploding = false;

                    playerR = 0;
                    playerC = 0;
                    currentTurn = 0;

                    for (int r = 0; r < gridSize; r++) {
                        for (int c = 0; c < gridSize; c++) {
                            visitedTurn[r][c] = -1000;
                        }
                    }
                    visitedTurn[playerR][playerC] = currentTurn;


                    updateGrid();
                    ((Timer)e.getSource()).stop();
                }
        }).start();
    }

    /* 
     * Updates Wolfy's position as player moves throughout the grid
     * Wolfy's trajectory is randomised to 0 - 3 steps
     */
    public static void moveWolfy() {
        if (!difficulty.equals("Hard")) {
            updateHint();
            wolfyLabel.setText("");
            return;
        }
        int startR = wolfR;
        int startC = wolfC;

        int wolfSteps = (int)(Math.random() * 4); // 0–3 steps

        for (int i = 0; i < wolfSteps; i++) {
            int dir = (int)(Math.random() * 4);

            int tempR = wolfR;
            int tempC = wolfC;

            if (dir == 0) {
                tempR--;
            } // up
            else if (dir == 1){
                tempR++;
            } // down
            else if (dir == 2){
                tempC--;
            } // left
            else{
                tempC++; // right
            }
            
            // only update if in bounds and not on the wall
            if (tempR >= 0 && tempR < gridSize && tempC >= 0 && tempC < gridSize && !walls[tempR][tempC]) {
                wolfR = tempR;
                wolfC = tempC;
            }

        }

        int distance = Math.abs(wolfR - startR) + Math.abs(wolfC - startC);

        updateHint();
        wolfyLabel.setText("Wolfy moved " + distance + " spaces 🐾");
    }



    /**
     * Checks if the player has found Wolfy.
     * If the player has found Wolfy, a dialog box will pop up to challenge the player to a game or rock paper scissors.
     * The timer is paused once the player finds Wolfy.
     * Calls the playRPS method.
     */
    public static void checkWin() {
        if (playerR == wolfR && playerC == wolfC) {
            timer.stop(); // pause timer during RPS
            buttons[wolfR][wolfC].setIcon(wolfIcon);

            hintLabel.setText("You found Wolfy! Beat him in RPS to catch.");
            hintLabel.setForeground(new Color(218, 165, 32)); // golden

            playRPS();//start Rock Paper Scissors
        }
    }

    /* 
     * Checks to see if the player has trapped themselves by walls or restricted pathways.
     * Displays a message to the user that they have lost. 
     */
    public static void checkLose() {  
        if (isPlayerTrapped()) {
            hintLabel.setText("You trapped yourself! 💀");
            hintLabel.setForeground(Color.RED);

            endGame("You trapped yourself with walls and blocked paths.\nScore will NOT be recorded.","Game Over",JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
     * When the player wins / loses, this method is called
     * The game will be ended and the time is stopped
     * The player will have the option to choose whether to see a playback of their game
     * Otherwise they will be returned to the home page
     */
    public static void endGame(String message, String title, int messageType) {
        timer.stop();

        stopMusic();

        // Show a dialog with ONE button labeled "Return to Home Page"
        JOptionPane.showOptionDialog(
            frame,
            message,
            title,
            JOptionPane.DEFAULT_OPTION, 
            messageType,
            null,          
            new Object[]{"Return to Home Page"},  
            "Return to Home Page"
        );

        // Go back to home page
        frame.getContentPane().removeAll();

        homePage();
    }


    


    /*
     * Checks to see if the player has trapped themselves by walls or other restricted paths
     * If user chooses medium or hard difficulty, they cannot reverse their moves for up to 5 steps. 
     * However, only applies to medium and hard difficulties
     */
    public static boolean isPlayerTrapped() {
        int[][] directions = {
            {-1, 0}, // up
            {1, 0},  // down
            {0, -1}, // left
            {0, 1}   // right
        };

        for (int i = 0; i < 4; i++) {
            int r = playerR + directions[i][0];
            int c = playerC + directions[i][1];
            if (r < 0 || r >= gridSize || c < 0 || c >= gridSize)
                continue;

            if (walls[r][c])
                continue;

            if (!difficulty.equals("Easy") && currentTurn - visitedTurn[r][c] <= 5)
                continue;

            // at least one possible move
            return false;
        }

        return true; // no valid moves
    }


    /*
     * Creates a dialog box for when the player catches Wolfy
     * Presents a rock paper scissors game for the player to successfully catch him
     * Player must beat Wolfy (the randomly generated Computer choice) otherwise Wolfy's location will be randomised again
     * The player will input through buttons 
     * If the player beats Wolfy, the game will display a message and record the steps taken and time
     * The player results are then saved
     * If the player loses, the game will continue and Wolfy's location will be re randomised
     * The timer is resumed
     */
    public static void playRPS() {
        timer.stop(); // pause timer during RPS

        String[] options = {"rock", "paper", "scissors"};

        JDialog playRPSDialog = new JDialog(frame, "Rock Paper Scissors", true);
        playRPSDialog.setSize(500, 300);
        playRPSDialog.setLayout(new BorderLayout());
        playRPSDialog.setLocationRelativeTo(frame);

        // top panel(title + subtitle)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JLabel titleLabel = new JLabel("You have found Wolfy! ");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(218, 165, 32)); // golden

        JLabel subtitleLabel = new JLabel("Beat him in Rock, Paper, Scissors to catch him!");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(subtitleLabel);

        playRPSDialog.add(topPanel, BorderLayout.NORTH);

        //Center Panel, the rock paper scissors images
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));

        ImageIcon rockIcon = new ImageIcon(new ImageIcon("assets/rock.png").getImage().getScaledInstance(90, 70, Image.SCALE_SMOOTH));
        ImageIcon paperIcon = new ImageIcon(new ImageIcon("assets/paper.png").getImage().getScaledInstance(90, 70, Image.SCALE_SMOOTH));
        ImageIcon scissorsIcon = new ImageIcon(new ImageIcon("assets/scissors.png").getImage().getScaledInstance(90, 70, Image.SCALE_SMOOTH));

        //make the images clickable
        JButton rockButton = new JButton(rockIcon);
        JButton paperButton = new JButton(paperIcon);
        JButton scissorsButton = new JButton(scissorsIcon);

        rockButton.setActionCommand("rock");
        paperButton.setActionCommand("paper");
        scissorsButton.setActionCommand("scissors");

        rockButton.setBorderPainted(false);
        rockButton.setContentAreaFilled(false);
        rockButton.setFocusPainted(false);

        paperButton.setBorderPainted(false);
        paperButton.setContentAreaFilled(false);
        paperButton.setFocusPainted(false);

        scissorsButton.setBorderPainted(false);
        scissorsButton.setContentAreaFilled(false);
        scissorsButton.setFocusPainted(false);
        //Opt border painted, content area filled and focus painted. Ttherwise it shows a border with blue background as default

        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);

        playRPSDialog.add(buttonPanel, BorderLayout.CENTER);

        //bottom panel instruct the user to make a move
        JLabel resultsLabel = new JLabel("Make your move!", SwingConstants.CENTER);
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        resultsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        playRPSDialog.add(resultsLabel, BorderLayout.SOUTH);

        ActionListener rpsListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerPick = e.getActionCommand();
                String wolfyPick = options[(int)(Math.random()*3)];
                String result = getRPSResults(playerPick, wolfyPick);
                resultsLabel.setText(result);

                // Player Wins
                if (result.contains("You win")) {
                    int min = seconds / 60;
                    int sec = seconds % 60;

                    playWinSound();//play winning sound effect

                    endGame("You beat Wolfy! 🎉\n\nYou caught Wolfy in " + steps +" steps and " + min + ":" + sec + ".","Congratulations!",JOptionPane.INFORMATION_MESSAGE);
                    try {
                        savePlayer(difficulty, playerName, steps, seconds);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    playRPSDialog.dispose();//close dialog
                }

                // Player loses. Show messages and relocate wolfy
                else if (result.contains("You lose")) {
                    Object[] options = {"Continue"};
                    JOptionPane.showOptionDialog(
                            frame,
                            "Wolfy beats you! \nHe is running back into the field. You have to find him again!",
                            "Wolfy Escaped",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    relocateWolfy();
                    timer.start(); // resume timer
                    playRPSDialog.dispose();
                }
            }
        };

        rockButton.addActionListener(rpsListener);
        paperButton.addActionListener(rpsListener);
        scissorsButton.addActionListener(rpsListener);

        playRPSDialog.setVisible(true);
    }

    /*
     * Runs through the logic of rock-paper-scissors.
     * If the game is tied, the player plays RPS again. 
     * If the player wins, they catch Wolfy and the game ends.
     * If the player loses, Wolfy is relocated and the game continues.
     */
    public static String getRPSResults(String playerPick, String wolfyPick) {
        if (playerPick.equals(wolfyPick)) {
            return "Wolfy chose " + wolfyPick + ". It's a tie!";
        } else if ((playerPick.equals("rock") && wolfyPick.equals("scissors")) ||(playerPick.equals("paper") && wolfyPick.equals("rock")) ||(playerPick.equals("scissors") && wolfyPick.equals("paper"))) {
            return "Wolfy chose " + wolfyPick + ". You win!";
        } else {
            return "Wolfy chose " + wolfyPick + ". You lose! Better luck next time!";
        }
    }

    /* 
     * Logic to relocate Wolfy to a random position in the grid after the player has caught Wolfy but lose the RPS game
     * Ensures Wolfy is not placed on the player, walls, or bombs
     * Ensures Wolfy is not blocked and is at least 5 units away from its original position
     */
    public static void relocateWolfy() {
        int oldR = wolfR;
        int oldC = wolfC;

        do {
            wolfR = (int)(Math.random() * gridSize);
            wolfC = (int)(Math.random() * gridSize);
        } while (walls[wolfR][wolfC] || bombs[wolfR][wolfC] || (wolfR == playerR && wolfC == playerC) || (!difficulty.equals("Easy") && currentTurn - visitedTurn[wolfR][wolfC] <= 5) ||(Math.abs(wolfR - oldR) + Math.abs(wolfC - oldC) < 5));
        //make sure the new wolf location is not on walls, not on bombs, not on on player, not blocked, and at least 5 spaces away

        // Calls the methods to update the hint message to Wolfy's new position
        // And to update the grid to the new events
        updateHint();
        updateGrid();
    }

    /*
     * When the game ends, saves the player's username, steps, and time taken to a file depending on the difficulty
     * 3 different text files are referred to for memory allocation.
     */
    public static void savePlayer(String difficulty, String playerName, int steps, int seconds)throws Exception {

        String fileName = "";

        if (difficulty.equals("Easy")) {
            fileName = "levels/easy.txt";
        } else if (difficulty.equals("Medium")) {
            fileName = "levels/medium.txt";
        } else if (difficulty.equals("Hard")) {
            fileName = "levels/hard.txt";
        }
        File file = new File(fileName);

        PrintWriter output = new PrintWriter(new FileWriter(file, true)); 
        output.println(playerName + " " + steps + " " + seconds);
        output.close();
    }

    /*
     * GUI to display the leaderboard for the user
     * Leaderboard is formatted in a table
     * Format: Username , Steps, Time
     */
    public static void displayLeaderboard(String initialDifficulty) throws Exception {//We displayed the leaderboard in easy mode by default
        JFrame leaderboardFrame = new JFrame("WheresWolfy Leaderboard");
        leaderboardFrame.setSize(500, 500);
        leaderboardFrame.setLocationRelativeTo(frame);
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        leaderboardFrame.setLayout(new BorderLayout());

        // Top panel (title + difficulty)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(92, 170, 120));

        // Title
        JLabel titleLabel = new JLabel("Scoreboard");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Difficulty panel
        JPanel difficultyPanel = new JPanel(new FlowLayout());
        difficultyPanel.setBackground(new Color(92, 170, 120));

        JLabel scoreboardLabel = new JLabel("Select Difficulty:");
        scoreboardLabel.setFont(new Font("Arial", Font.BOLD, 20));
        scoreboardLabel.setForeground(Color.WHITE);

        String[] difficulties = {"Easy", "Medium", "Hard"};
        JComboBox<String> difficultyBox = new JComboBox<>(difficulties);
        difficultyBox.setFont(new Font("Arial", Font.PLAIN, 18));
        difficultyBox.setSelectedItem("Easy");

        difficultyPanel.add(scoreboardLabel);
        difficultyPanel.add(difficultyBox);

        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(difficultyPanel);
        topPanel.add(Box.createVerticalStrut(10));

        leaderboardFrame.add(topPanel, BorderLayout.NORTH);

        // Table with Rank , user name, steps, and time column
        String[] columns = {"Rank", "User Name", "Steps", "Time"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setEnabled(false);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        table.getTableHeader().setBackground(Color.decode("#56A6A9"));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scroll = new JScrollPane(table);
        leaderboardFrame.add(scroll, BorderLayout.CENTER);

        updateTableData(tableModel, (String) difficultyBox.getSelectedItem());

        // Update table when difficulty changes
        difficultyBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedDifficulty = (String) difficultyBox.getSelectedItem();
                updateTableData(tableModel, selectedDifficulty);
            }
        });
        leaderboardFrame.setVisible(true);
    }

    /*
     * Leaderboard is displayed depending on chosen difficulty
     * 3 array lists are created to enter new players' names, step count and times
     * 3 text files can be referred to and saved data is recalled from the files
     * The player is able to choose to view leaderboards of different difficulties by choosing from a drop-down list
     * The data is ordered by the number of steps the players have taken
     * If the number of steps is equal, the players are ordered by the time (less = higher rank)
     * The data is ordered through bubblesort
     */
    private static void updateTableData(DefaultTableModel tableModel, String difficulty) {
        tableModel.setRowCount(0);

        if (difficulty == null){
            difficulty = "Easy";
        }

        String fileName;
        if (difficulty.equals("Medium")) {
            fileName = "levels/medium.txt";
        } else if (difficulty.equals("Hard")) {
            fileName = "levels/hard.txt";
        } else {
            fileName = "levels/easy.txt";
        }

        File file = new File(fileName);
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> steps = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();

        try {
            if (file.exists()) {
                Scanner input = new Scanner(file);
                while (input.hasNext()) {
                    names.add(input.next());
                    steps.add(input.nextInt());
                    times.add(input.nextInt());
                }
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Bubble sort by steps, then time
        for (int i = 0; i < names.size() - 1; i++) {
            for (int j = 0; j < names.size() - i - 1; j++) {
                if (steps.get(j) > steps.get(j + 1) || (steps.get(j).equals(steps.get(j + 1)) && times.get(j) > times.get(j + 1))) {

                    // Swap steps
                    int tempSteps = steps.get(j);
                    steps.set(j, steps.get(j + 1));
                    steps.set(j + 1, tempSteps);

                    // Swap times
                    int tempTimes = times.get(j);
                    times.set(j, times.get(j + 1));
                    times.set(j + 1, tempTimes);

                    // Swap names
                    String tempName = names.get(j);
                    names.set(j, names.get(j + 1));
                    names.set(j + 1, tempName);
                }
            }
        }

        if (names.isEmpty()) {
            tableModel.addRow(new Object[]{"", "No scores yet", "", ""});
            return;
        }

        for (int i = 0; i < names.size(); i++) {
            int min = times.get(i) / 60;
            int sec = times.get(i) % 60;
            tableModel.addRow(new Object[]{i + 1, names.get(i), steps.get(i), min + " min " + sec + " sec"});
        }
    }

    /*
     * Displays the rules and description when the method is called 
     * Rules displayed when the programme is opened or when the player clicks into Help button
     */
    public static void showRules() {
        // Welcome label
        JLabel welcomeLabel = new JLabel("Dear " + playerName + ", welcome to Where's Wolfy!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(92, 170, 120)); // forest green
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Rules text
        JTextArea rulesArea = new JTextArea(
            "DESCRIPTION:\n" +
            "You and Wolfy are playing hide-and-seek, and it’s YOUR turn to find Wolfy! Wolfy is hiding on a spot on the field, " +
            "but the grass is so tall that you can’t see Wolfy! Enter the field to try and find Wolfy using the least number of steps " +
            "and with the fastest possible time!\n\n" +

            "INSTRUCTIONS:\n" +
            "Press Start Game to begin playing.\n" +
            "Press Settings to modify your sound and music settings.\n" +
            "Press Leaderboard to view past players’ scores.\n" +
            "Under Quick Settings, select your Difficulty in the drop down bar.\n" +
            "Press the close tab button to exit the game.\n\n" +
            "Use “WASD” to move your character:\n" +
            "W - move up\n" +
            "A - move left\n" +
            "S - move down\n" +
            "D - move right\n\n" +
            "The step count is displayed on the top left corner of the screen.\n" +
            "The timer is displayed on the top right of the screen. It will start once the game has begun.\n" +
            "The top centre of the screen will display notes relative to your game, including location hints and obstacles messages.\n\n" +

            "RULES:\n" +
            "You can take as many steps as needed. However, the more number of steps will result in a lower score on the leaderboard.\n" +
            "A header at the top of the interface will advise you by displaying hints about your relative location to Wolfy:\n" +
            "    “Getting warmer…” - You are within 4 grid spots of Wolfy.\n" +
            "    “Very hot!” - You are within 2 spots of Wolfy.\n" +
            "    “Cold…” - You are not close to Wolfy.\n\n" +
            "To catch Wolfy, you must beat him in a game of Rock, Paper, Scissors. If you win, you will successfully beat Wolfy and win the game. " +
            "If you lose, Wolfy will escape back into the field and you must catch him again. Your time will continue.\n\n" +
            "Easy - 10 x 10 field\n" +
            "Medium - 12 x 12 field + 5 walls\n" +
            "Hard - 15 x 15 field + 10 walls + 5 bombs + moving Wolfy\n\n" +
            "For Medium and Hard difficulties, you will not be able to retrace your steps for up to 5 steps and you may run into obstacles as you travel through the field.\n" +
            "On medium difficulty, you may encounter walls, which you will need to create alternate paths to avoid being blocked.\n" +
            "On hard difficulty, you may encounter walls (similar to medium difficulty), and bombs, which will explode and reset your location to the starting position. Wolfy will also continue moving as the player moves.\n\n" +
            "You cannot go out of bounds of the displayed grid size (field borders).\n\n" +
            "If you are completely trapped against the field borders / the retraced steps / a wall, it will result in an automatic loss and scores will not be recorded.\n\n" +
            "You can reveal Wolfy’s location by pressing “Cheat” on the bottom of the screen. However you will be penalised by an additional 50 steps added to your step score.\n" +
            "You can choose to forfeit the game by pressing the “Give Up” on the bottom of the screen. Your score will not be recorded.\n\n" +

            "LEADERBOARD:\n" +
            "Players will be organised depending on the difficulty of the game.\n" +
            "The number of steps is the primary factor that determines the leaderboard order. Time is factored to organise between players with the same number of steps taken.\n" +
            "You may select the difficulty level to view the player rankings across different difficulty levels.\n\n" +
            "Enjoy the game!"
        );

        rulesArea.setEditable(false);
        rulesArea.setLineWrap(true);
        rulesArea.setWrapStyleWord(true);
        rulesArea.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(rulesArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
            frame,
            panel,
            "Game Rules",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    /*
     * This is the music played in game after user entered start
     * Method called to play music from wav files
     * Music is continuously looped and can be turned on or off in the settings 
     */
    public static void playInGameMusic() {
        if (!musicEnabled){ 
            return;
        }

        try {
            File inGameMusic = new File("assets/Where'sWolfy.wav");
            AudioInputStream stream = AudioSystem.getAudioInputStream(inGameMusic);
            musicClip = AudioSystem.getClip();
            musicClip.open(stream);
            setClipVolume(musicClip, musicVolume);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.err.println("Could not play in-game music: " + e.getMessage());
        }
    }
    public static void stopMusic() {
        if (musicClip != null && musicClip.isRunning()) {
            musicClip.stop();
            musicClip.setFramePosition(0);
        }
    }

    public static void playLobbyMusic() {
        if (!musicEnabled) {
            return;
        }

        try {
            File lobbyMusic = new File("assets/wolfyStart.wav");
            AudioInputStream stream = AudioSystem.getAudioInputStream(lobbyMusic);
            lobbyMusicClip = AudioSystem.getClip();
            lobbyMusicClip.open(stream);
            setClipVolume(lobbyMusicClip, musicVolume);
            lobbyMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            lobbyMusicClip.start();
        } catch (Exception e) {
            System.err.println("Could not play lobby music: " + e.getMessage());
        }
    }

    //stop the lobby music when the game starts
    public static void stopLobbyMusic() {
        if (lobbyMusicClip != null && lobbyMusicClip.isRunning()) {
            lobbyMusicClip.stop();
            lobbyMusicClip.setFramePosition(0);
        }
    }

    // set clip volume from 0.0 to 1.0
   public static void setClipVolume(Clip clip, double volume) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        // Make sure volume is not zero to avoid log10(0) because log10(0) is undefined
        double actualVolume;
        if (volume == 0) {
            actualVolume = 0.0001; 
        } else {
            actualVolume = volume;
        }

        // Convert volume (0.0 - 1.0) to decibels. In java, we have to use decibels to set the volume.
        double dB = Math.log10(actualVolume) * 20;

        // Set the volume 
        gainControl.setValue((float) dB);
    }


    /**
     * Plays a sound effect given by the filename.
     * If sound effects are disabled, this method does nothing.
     */
    public static void playSFX(String filename) {
        if (!sfxEnabled){
            return;
        } 

        try {
            File file = new File(filename);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            setClipVolume(clip, sfxVolume);
            clip.start();
        } catch (Exception e) {
            System.err.println("Could not play SFX (" + filename + "): " + e.getMessage());
        }
    }

    public static void playWalkSound() { 
        playSFX("assets/walkSound.wav"); 
    }
    public static void playWallSound() { 
        playSFX("assets/wallSound.wav"); 
    }
    public static void playBombSound() { 
        playSFX("assets/cherryBombSound.wav"); 
    }
    public static void playWinSound() { 
        playSFX("assets/win.wav"); 
    }
    public static void playReverseStepSound() { 
        playSFX("assets/reverseStepSound.wav"); 
    }

}