package com.cannongame.core;

import com.cannongame.ui.GamePanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Application entry point.
 * 
 * WHY THIS EXISTS:
 * Separates bootstrap/frame setup from game logic. The original main()
 * was inside CannonGameFinal and directly created the JFrame — mixing
 * infrastructure concerns with game concerns.
 * 
 * Also ensures Swing initialization happens on the Event Dispatch Thread
 * (EDT) via SwingUtilities.invokeLater, which is the correct way to
 * start Swing applications.
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame(GameConfig.WINDOW_TITLE);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            GamePanel gamePanel = new GamePanel();
            window.setContentPane(gamePanel);
            window.pack();
            window.setLocationRelativeTo(null); // Center on screen
            window.setVisible(true);

            // Start the game loop on a separate thread
            GameLoop gameLoop = new GameLoop(gamePanel);
            gameLoop.start();
        });
    }
}
