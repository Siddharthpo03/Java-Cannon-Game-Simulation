package com.cannongame.entities;

import com.cannongame.core.GameConfig;

import java.awt.*;
import java.util.Random;

/**
 * Animated background cloud that drifts across the screen.
 * 
 * WHY THIS CHANGED:
 * Minimal refactoring — the original Cloud class was already well-structured.
 * Changes: extracted to its own file, uses GameConfig constants instead of
 * magic numbers, and uses a single Random instance (thread-safe for game loop).
 */
public class Cloud {

    private int size;
    private double x;
    private int y;
    private double speed;
    private final Random rand = new Random();

    public Cloud() {
        resetCloud();
        // Start at a random x position so clouds don't all appear at once
        x = rand.nextInt(GameConfig.WINDOW_WIDTH);
    }

    public void draw(Graphics2D g) {
        update();
        g.setColor(GameConfig.CLOUD_COLOR);
        g.fillOval((int) x, y, size, size);
        g.fillOval((int) x + (size / 2) + (size / 6), y - (size / 3), size, size);
        g.fillOval((int) x + size + (size / 3), y, size, size);
    }

    private void update() {
        x += speed;
        if (x > GameConfig.WINDOW_WIDTH) {
            resetCloud();
        }
    }

    private void resetCloud() {
        y = rand.nextInt(GameConfig.CLOUD_MAX_Y + 1);
        size = rand.nextInt(GameConfig.CLOUD_MAX_SIZE - GameConfig.CLOUD_MIN_SIZE) + GameConfig.CLOUD_MIN_SIZE;
        // Start off-screen left so cloud doesn't pop in
        x = ((size * 2.5) + rand.nextInt(400)) * -1;
        speed = Math.random() * (GameConfig.CLOUD_MAX_SPEED - GameConfig.CLOUD_MIN_SPEED) + GameConfig.CLOUD_MIN_SPEED;
    }
}
