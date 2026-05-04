package com.cannongame.entities;

import com.cannongame.audio.SoundManager;
import com.cannongame.core.GameConfig;
import com.cannongame.ui.InputHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A parameterized cannon that can face either LEFT or RIGHT.
 * 
 * WHY THIS EXISTS:
 * The original code had Cannon_1 (lines 150-368) and Cannon_2 (lines 371-592)
 * — two classes that were ~95% identical, differing only in:
 *   - Position coordinates (left vs right side)
 *   - Barrel direction (positive vs negative angle)
 *   - Ball spawn offset calculation
 *   - UI element positions (fire button, clear button, color selector)
 * 
 * This unified Cannon class takes a Side enum and position parameters,
 * eliminating ~220 lines of duplicated code. Fixing a bug or adding a
 * feature now requires changing only ONE class instead of TWO.
 * 
 * The generateSpaces(242) hack for right-side slider labels has been
 * replaced with proper coordinate-based label positioning.
 */
public class Cannon {

    /**
     * Determines barrel direction and angle sign.
     */
    public enum Side {
        LEFT(1, -2),    // Barrel points right, ball offset = -diameter - 2
        RIGHT(-1, 90);  // Barrel points left,  ball offset = -diameter + 90

        public final int directionMultiplier;
        public final int ballSpawnOffset;

        Side(int directionMultiplier, int ballSpawnOffset) {
            this.directionMultiplier = directionMultiplier;
            this.ballSpawnOffset = ballSpawnOffset;
        }
    }

    // ── Configuration (set at construction) ─────────────────
    private final Side side;
    private final int cannonX;
    private final int fireButtonX;
    private final int fireButtonY;
    private final int clearButtonX;
    private final int clearButtonY;
    private final int colorSelectionX;
    private final int colorSelectionY;

    // ── State ───────────────────────────────────────────────
    private int diameter = GameConfig.CANNON_BASE_DIAMETER;
    private final int width = GameConfig.CANNON_BARREL_WIDTH;
    private int ballX;
    private int ballY;
    private int angle;
    private int size;
    private int power;
    private Color colorSelected = GameConfig.BALL_COLORS[0]; // Default grey
    private final List<Ball> balls = new ArrayList<>();

    // ── Dependencies ────────────────────────────────────────
    private final SoundManager soundManager;
    private final InputHandler inputHandler;

    // ── Score ───────────────────────────────────────────────
    private int score = 0;

    public Cannon(Side side, int cannonX, int fireButtonX, int fireButtonY,
                  int clearButtonX, int clearButtonY,
                  int colorSelectionX, int colorSelectionY,
                  SoundManager soundManager, InputHandler inputHandler) {
        this.side = side;
        this.cannonX = cannonX;
        this.fireButtonX = fireButtonX;
        this.fireButtonY = fireButtonY;
        this.clearButtonX = clearButtonX;
        this.clearButtonY = clearButtonY;
        this.colorSelectionX = colorSelectionX;
        this.colorSelectionY = colorSelectionY;
        this.soundManager = soundManager;
        this.inputHandler = inputHandler;
    }

    /**
     * Main draw method — renders cannon, balls, buttons, and color picker.
     */
    public void draw(Graphics2D g, int angle, int size, int power) {
        this.angle = angle;
        this.size = size;
        this.power = power;

        drawBalls(g);
        drawCannon(g);
        drawButtons(g);
        drawColorSelection(g);
    }

    // ── Cannon Rendering ────────────────────────────────────

    private void drawCannon(Graphics2D g) {
        diameter = size + 50;
        int y = GameConfig.WINDOW_HEIGHT - diameter - 50;

        // Build barrel polygon
        int[] xPoly, yPoly;
        if (side == Side.LEFT) {
            xPoly = new int[]{cannonX, cannonX + width, cannonX + width, cannonX};
        } else {
            xPoly = new int[]{cannonX, cannonX - width, cannonX - width, cannonX};
        }
        yPoly = new int[]{y, y, y + diameter, y + diameter};

        // Rotate barrel around pivot point
        int effectiveAngle = angle * side.directionMultiplier;
        for (int i = 0; i < xPoly.length; i++) {
            int[] rotated = rotateXY(xPoly[i], yPoly[i], effectiveAngle, cannonX, y + diameter);
            xPoly[i] = rotated[0];
            yPoly[i] = rotated[1];
        }

        // Keep cannon fixed to ground
        for (int i = 0; i < xPoly.length; i++) {
            yPoly[i] = yPoly[i] + y + 100 - yPoly[3];
        }

        // Calculate ball spawn position
        ballX = xPoly[1];
        ballY = yPoly[1];
        ballX += xPoly[2] - xPoly[1] + side.ballSpawnOffset;

        // Draw cannon body
        g.setColor(Color.BLACK);
        g.fillPolygon(new Polygon(xPoly, yPoly, xPoly.length));

        // Draw wheel
        g.setColor(GameConfig.CANNON_WHEEL_COLOR);
        g.fillOval(cannonX - 25, GameConfig.WINDOW_HEIGHT - 100,
                   GameConfig.CANNON_WHEEL_SIZE, GameConfig.CANNON_WHEEL_SIZE);
    }

    // ── Ball Management ─────────────────────────────────────

    private void drawBalls(Graphics2D g) {
        // Fire button click detection
        if (inputHandler.isClick() &&
            inputHandler.getCursorX() > fireButtonX &&
            inputHandler.getCursorX() < fireButtonX + GameConfig.FIRE_BUTTON_WIDTH &&
            inputHandler.getCursorY() > fireButtonY &&
            inputHandler.getCursorY() < fireButtonY + GameConfig.FIRE_BUTTON_HEIGHT) {

            fireBall();
            inputHandler.consumeClick();
        }

        // Clear button click detection
        if (inputHandler.isClick() &&
            inputHandler.getCursorX() > clearButtonX &&
            inputHandler.getCursorX() < clearButtonX + GameConfig.CLEAR_BUTTON_WIDTH &&
            inputHandler.getCursorY() > clearButtonY &&
            inputHandler.getCursorY() < clearButtonY + GameConfig.CLEAR_BUTTON_HEIGHT) {

            balls.clear();
            inputHandler.consumeClick();
        }

        // Render all balls
        for (Ball ball : balls) {
            g.setColor(ball.getColor());
            g.fillOval(ball.getX(), ball.getY(), ball.getDiameter(), ball.getDiameter());
        }
    }

    private void fireBall() {
        int effectivePower;
        int angleCalc;

        if (side == Side.LEFT) {
            effectivePower = power * -1;
            int divisor = 157;
            int speedX = (int) (effectivePower - ((double) effectivePower / divisor) * (angle * -1));
            int speedY = (int) (((double) effectivePower / divisor) * (angle * -1));
            balls.add(new Ball(ballX, ballY, diameter, speedX, speedY,
                              colorSelected, size, power, GameConfig.SPEED_DIVISOR));
        } else {
            effectivePower = power;
            int divisor = 203;
            int speedX = (int) (effectivePower - ((double) effectivePower / divisor) * (angle * -1));
            int speedY = (int) (((double) effectivePower / divisor) * angle);
            balls.add(new Ball(ballX, ballY, diameter, speedX, speedY,
                              colorSelected, size, power, GameConfig.SPEED_DIVISOR));
        }

        soundManager.play("cannonfire");
        soundManager.play("metal");
    }

    // ── UI Elements ─────────────────────────────────────────

    private void drawButtons(Graphics2D g) {
        // Fire button
        g.setColor(Color.RED);
        g.fillRect(fireButtonX, fireButtonY, GameConfig.FIRE_BUTTON_WIDTH, GameConfig.FIRE_BUTTON_HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Calibri", Font.BOLD, 48));
        g.drawString("FIRE", fireButtonX + 7, fireButtonY + GameConfig.FIRE_BUTTON_HEIGHT - 10);

        // Clear button
        g.setColor(Color.CYAN);
        g.fillRect(clearButtonX, clearButtonY, GameConfig.CLEAR_BUTTON_WIDTH, GameConfig.CLEAR_BUTTON_HEIGHT);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Calibri", Font.BOLD, 32));
        g.drawString("CLEAR", clearButtonX + 7, clearButtonY + GameConfig.CLEAR_BUTTON_HEIGHT - 10);
    }

    private void drawColorSelection(Graphics2D g) {
        Color[] boxColors = GameConfig.BALL_COLORS;
        int boxWidth = GameConfig.COLOR_BOX_WIDTH;
        int padding = 10;
        int menuBoxWidth = (boxWidth * 2 * boxColors.length) + padding * 2;
        int menuBoxHeight = boxWidth + 40;

        // Menu background
        g.setColor(GameConfig.MENU_BOX_COLOR);
        g.fillRect(colorSelectionX - padding, colorSelectionY - padding - 24,
                   menuBoxWidth, menuBoxHeight);

        // Label
        g.setColor(Color.BLACK);
        g.setFont(new Font("Calibri", Font.BOLD, 24));
        g.drawString("Color", colorSelectionX, colorSelectionY - 12);

        // Color boxes
        for (int i = 0; i < boxColors.length; i++) {
            int boxX = colorSelectionX + boxWidth * i * 2;

            // Selection highlight
            if (boxColors[i].getRGB() == colorSelected.getRGB()) {
                g.setColor(Color.BLACK);
                g.fillRect(boxX - 4, colorSelectionY - 4, boxWidth + 8, boxWidth + 8);
            }

            // Color box
            g.setColor(boxColors[i]);
            g.fillRect(boxX, colorSelectionY, boxWidth, boxWidth);

            // Click detection
            if (inputHandler.isClick() &&
                inputHandler.getCursorX() > boxX &&
                inputHandler.getCursorX() < boxX + boxWidth &&
                inputHandler.getCursorY() > colorSelectionY &&
                inputHandler.getCursorY() < colorSelectionY + boxWidth) {
                colorSelected = boxColors[i];
                inputHandler.consumeClick();
            }
        }
    }

    // ── Keyboard Actions ─────────────────────────────────────

    /**
     * Fires a ball from the keyboard (no click detection needed).
     * Called by GamePanel when the player presses their fire key.
     */
    public void fireFromKeyboard() {
        fireBall();
    }

    /**
     * Clears all balls from this cannon (keyboard triggered).
     */
    public void clearBalls() {
        balls.clear();
    }

    /**
     * Selects a color by index from the BALL_COLORS palette.
     * Used for keyboard-driven color selection (number keys).
     */
    public void selectColor(int colorIndex) {
        if (colorIndex >= 0 && colorIndex < GameConfig.BALL_COLORS.length) {
            colorSelected = GameConfig.BALL_COLORS[colorIndex];
        }
    }

    // ── Geometry Utility ────────────────────────────────────

    private int[] rotateXY(int x, int y, int angle, int cx, int cy) {
        double tempX = x - cx;
        double tempY = y - cy;
        double rad = angle / 100.0;

        double rotatedX = tempX * Math.cos(rad) - tempY * Math.sin(rad);
        double rotatedY = tempX * Math.sin(rad) + tempY * Math.cos(rad);

        return new int[]{(int) (rotatedX + cx), (int) (rotatedY + cy)};
    }

    // ── Accessors ───────────────────────────────────────────

    public List<Ball> getBalls() { return balls; }
    public Side getSide() { return side; }
    public int getScore() { return score; }
    public void addScore(int points) { this.score += points; }
    public void resetScore() { this.score = 0; }

    /**
     * Resets cannon state for a new game.
     */
    public void reset() {
        balls.clear();
        score = 0;
        colorSelected = GameConfig.BALL_COLORS[0];
    }
}
