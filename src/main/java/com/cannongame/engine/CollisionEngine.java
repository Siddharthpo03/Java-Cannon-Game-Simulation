package com.cannongame.engine;

import com.cannongame.core.GameConfig;
import com.cannongame.entities.Ball;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles collision detection and resolution between projectiles.
 * 
 * WHY THIS EXISTS:
 * The original collision logic was embedded in GamePanel.checkCollisions() and
 * GamePanel.handleCollision() (lines 823-875). This mixed rendering concerns
 * (fire effects, sound) with pure algorithmic logic (distance checks, size
 * comparisons). Extracting it means:
 * 
 * 1. Collision detection can be tested with pure unit tests (no GUI needed)
 * 2. Detection is separated from resolution (Open/Closed Principle)
 * 3. New collision responses (power-ups, score bonuses) can be added
 *    without modifying the detection algorithm
 */
public class CollisionEngine {

    /**
     * Represents the result of a single collision between two balls.
     */
    public static class CollisionResult {
        public final Ball ball1;
        public final Ball ball2;
        public final int collisionX;
        public final int collisionY;
        /** +1 = ball1 wins, -1 = ball2 wins, 0 = mutual destruction */
        public final int winner;

        public CollisionResult(Ball ball1, Ball ball2, int collisionX, int collisionY, int winner) {
            this.ball1 = ball1;
            this.ball2 = ball2;
            this.collisionX = collisionX;
            this.collisionY = collisionY;
            this.winner = winner;
        }
    }

    /**
     * Detects and resolves all collisions between two groups of balls.
     * Returns a list of collision results for the caller to handle
     * (fire effects, sounds, scoring).
     * 
     * The original code used removeAll() inside nested loops, which is
     * O(n*m*k) — we collect results first, then batch-remove.
     */
    public List<CollisionResult> processCollisions(List<Ball> groupA, List<Ball> groupB) {
        List<CollisionResult> results = new ArrayList<>();
        List<Ball> toRemoveA = new ArrayList<>();
        List<Ball> toRemoveB = new ArrayList<>();

        for (Ball a : groupA) {
            for (Ball b : groupB) {
                if (toRemoveA.contains(a) || toRemoveB.contains(b)) continue;

                if (isColliding(a, b)) {
                    int winner = determineWinner(a, b);
                    int cx = (a.getX() + b.getX()) / 2;
                    int cy = (a.getY() + b.getY()) / 2;

                    results.add(new CollisionResult(a, b, cx, cy, winner));

                    if (winner >= 0) toRemoveB.add(b);  // ball1 wins or mutual
                    if (winner <= 0) toRemoveA.add(a);   // ball2 wins or mutual
                }
            }
        }

        groupA.removeAll(toRemoveA);
        groupB.removeAll(toRemoveB);

        return results;
    }

    /**
     * Circle-circle collision detection using distance formula.
     * Two circles collide when the distance between their centers
     * is less than or equal to the sum of their radii.
     */
    private boolean isColliding(Ball a, Ball b) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        double radiusSum = a.getRadius() + b.getRadius();
        return distance <= radiusSum;
    }

    /**
     * Determines collision winner based on size, then power.
     * 
     * Rules (preserved from original):
     * - Larger ball wins (+1 or -1)
     * - If same size, higher power wins
     * - If both equal, mutual destruction (0)
     * 
     * Note: The original code had a subtle bug where the power comparison
     * was inverted (ball1 lower power caused ball2 removal). This has been
     * fixed to be consistent: the ball with MORE power/size wins.
     */
    private int determineWinner(Ball a, Ball b) {
        if (a.getSize() > b.getSize()) return 1;
        if (a.getSize() < b.getSize()) return -1;
        if (a.getPower() > b.getPower()) return 1;
        if (a.getPower() < b.getPower()) return -1;
        return 0; // mutual destruction
    }
}
