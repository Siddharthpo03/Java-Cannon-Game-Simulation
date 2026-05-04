package com.cannongame.ui;

import com.cannongame.core.GameConfig;
import com.cannongame.core.GameState;

import java.awt.*;

/**
 * Heads-Up Display — renders scores, game state overlays, and controls info.
 * 
 * WHY THIS EXISTS:
 * The original game had no scoring, no pause screen, and no game state
 * feedback. The HUD is a dedicated rendering class for all UI overlays
 * that aren't part of the game world. This keeps score rendering out of
 * GamePanel and entity classes, following Single Responsibility Principle.
 */
public class HUD {

    private static final Font SCORE_FONT = new Font("Calibri", Font.BOLD, 28);
    private static final Font TITLE_FONT = new Font("Calibri", Font.BOLD, 64);
    private static final Font SUBTITLE_FONT = new Font("Calibri", Font.BOLD, 28);
    private static final Font INFO_FONT = new Font("Calibri", Font.PLAIN, 20);
    private static final Color OVERLAY_COLOR = new Color(0, 0, 0, 150);
    private static final Color ACCENT_COLOR = new Color(255, 200, 50);

    /**
     * Renders player scores at the top center of the screen.
     */
    public void drawScores(Graphics2D g, int score1, int score2) {
        g.setFont(SCORE_FONT);

        // Player 1 score (left)
        String p1Text = "P1: " + score1;
        g.setColor(new Color(3, 61, 180));
        g.drawString(p1Text, GameConfig.WINDOW_WIDTH / 2 - 150, 35);

        // Separator
        g.setColor(Color.BLACK);
        g.drawString(" | ", GameConfig.WINDOW_WIDTH / 2 - 30, 35);

        // Player 2 score (right)
        String p2Text = "P2: " + score2;
        g.setColor(new Color(180, 30, 30));
        g.drawString(p2Text, GameConfig.WINDOW_WIDTH / 2 + 30, 35);
    }

    /**
     * Renders the pause overlay with semi-transparent background.
     */
    public void drawPauseOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Title
        g.setColor(Color.WHITE);
        g.setFont(TITLE_FONT);
        String pauseText = "PAUSED";
        FontMetrics fm = g.getFontMetrics();
        int textX = (GameConfig.WINDOW_WIDTH - fm.stringWidth(pauseText)) / 2;
        g.drawString(pauseText, textX, GameConfig.WINDOW_HEIGHT / 2 - 30);

        // Instructions
        g.setFont(SUBTITLE_FONT);
        g.setColor(ACCENT_COLOR);
        String resumeText = "Press P to Resume";
        fm = g.getFontMetrics();
        textX = (GameConfig.WINDOW_WIDTH - fm.stringWidth(resumeText)) / 2;
        g.drawString(resumeText, textX, GameConfig.WINDOW_HEIGHT / 2 + 20);
    }

    /**
     * Renders game over screen showing the winner.
     */
    public void drawGameOverOverlay(Graphics2D g, int score1, int score2) {
        // Semi-transparent overlay
        g.setColor(OVERLAY_COLOR);
        g.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Winner announcement
        g.setFont(TITLE_FONT);
        String winner;
        if (score1 > score2) {
            g.setColor(new Color(50, 150, 255));
            winner = "PLAYER 1 WINS!";
        } else if (score2 > score1) {
            g.setColor(new Color(255, 80, 80));
            winner = "PLAYER 2 WINS!";
        } else {
            g.setColor(Color.WHITE);
            winner = "IT'S A TIE!";
        }
        FontMetrics fm = g.getFontMetrics();
        int textX = (GameConfig.WINDOW_WIDTH - fm.stringWidth(winner)) / 2;
        g.drawString(winner, textX, GameConfig.WINDOW_HEIGHT / 2 - 40);

        // Final scores
        g.setFont(SUBTITLE_FONT);
        g.setColor(Color.WHITE);
        String scoreText = "Final Score — P1: " + score1 + " | P2: " + score2;
        fm = g.getFontMetrics();
        textX = (GameConfig.WINDOW_WIDTH - fm.stringWidth(scoreText)) / 2;
        g.drawString(scoreText, textX, GameConfig.WINDOW_HEIGHT / 2 + 10);

        // Restart instruction
        g.setColor(ACCENT_COLOR);
        String restartText = "Press R to Restart";
        fm = g.getFontMetrics();
        textX = (GameConfig.WINDOW_WIDTH - fm.stringWidth(restartText)) / 2;
        g.drawString(restartText, textX, GameConfig.WINDOW_HEIGHT / 2 + 50);
    }

    /**
     * Draws control hints showing keyboard bindings for both players.
     */
    public void drawControlHints(Graphics2D g) {
        Font hintFont = new Font("Calibri", Font.PLAIN, 16);
        g.setFont(hintFont);
        FontMetrics fm = g.getFontMetrics();

        int y = GameConfig.WINDOW_HEIGHT - 8;

        // Player 1 controls (left side)
        g.setColor(new Color(3, 61, 180));
        String p1Hints = "P1: W/S Angle | A/D Power | Q/E Size | Space Fire | C Clear | 1-6 Color";
        g.drawString(p1Hints, 20, y);

        // Player 2 controls (right side)
        g.setColor(new Color(180, 30, 30));
        String p2Hints = "P2: \u2191/\u2193 Angle | \u2190/\u2192 Power | ,/. Size | Enter Fire | Bksp Clear | Num1-6 Color";
        int p2Width = fm.stringWidth(p2Hints);
        g.drawString(p2Hints, GameConfig.WINDOW_WIDTH - p2Width - 20, y);

        // Global controls (center)
        g.setColor(new Color(80, 80, 80));
        String global = "P = Pause | R = Restart";
        int gWidth = fm.stringWidth(global);
        g.drawString(global, (GameConfig.WINDOW_WIDTH - gWidth) / 2, y - 18);
    }
}
