package com.cannongame.entities;

import com.cannongame.core.GameConfig;

import java.awt.*;
import java.util.Random;

/**
 * Short-lived particle effect displayed at collision points.
 * 
 * WHY THIS CHANGED:
 * Minimal refactoring — already simple and focused. Changes:
 * - Moved to its own file (was in the monolith)
 * - Uses Random instance instead of Math.random() (consistent API)
 * - Uses GameConfig for fire colors and duration
 */
public class FireEffect {

    private final int x;
    private final int y;
    private int life;
    private final Random rand = new Random();

    public FireEffect(int x, int y) {
        this.x = x;
        this.y = y;
        this.life = GameConfig.FIRE_EFFECT_DURATION;
    }

    public void draw(Graphics2D g) {
        if (life > 0) {
            g.setColor(GameConfig.FIRE_COLORS[rand.nextInt(GameConfig.FIRE_COLORS.length)]);
            int size = rand.nextInt(30) + 20;
            g.fillOval(x - size / 2, y - size / 2, size, size);
            life--;
        }
    }

    public boolean isDone() {
        return life <= 0;
    }
}
