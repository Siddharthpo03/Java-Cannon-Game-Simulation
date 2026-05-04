package com.cannongame.engine;

import com.cannongame.core.GameConfig;
import com.cannongame.entities.Ball;

/**
 * Handles all physics simulation: gravity, air resistance, bouncing, and friction.
 * 
 * WHY THIS EXISTS:
 * In the original code, all physics was baked into Ball.update() (lines 104-143).
 * This violated the Single Responsibility Principle — Ball was both a data entity
 * AND a physics simulator. Extracting physics into its own engine class means:
 * 
 * 1. Physics can be unit-tested independently (no rendering required)
 * 2. Physics parameters can be tuned from GameConfig without touching entity code
 * 3. New entity types can reuse the same physics engine
 * 4. You can swap physics models (e.g., add wind) without modifying entities
 */
public class PhysicsEngine {

    /**
     * Performs a full physics update on a ball for one frame.
     * Called once per game tick for each active ball.
     */
    public void update(Ball ball) {
        applyHorizontalMovement(ball);
        applyAirResistanceX(ball);
        applyWallBounce(ball);
        applySpeedThreshold(ball);
        applyGroundFriction(ball);
        applyVerticalMovement(ball);
        applyGroundBounce(ball);
    }

    /**
     * Moves the ball horizontally based on its speed and velocity.
     */
    private void applyHorizontalMovement(Ball ball) {
        ball.setX(ball.getXDouble() + ball.getVelocityX() + ball.getSpeedX());
    }

    /**
     * Applies air resistance to horizontal speed, gradually slowing the ball.
     * Uses a multiplicative decay factor (0.996) — each frame the ball retains
     * 99.6% of its horizontal speed, simulating drag.
     */
    private void applyAirResistanceX(Ball ball) {
        ball.setSpeedX(ball.getSpeedX() * GameConfig.AIR_RESISTANCE_X);
    }

    /**
     * Reverses horizontal direction when the ball hits left/right walls.
     * A simple elastic reflection — speed magnitude stays the same,
     * direction flips.
     */
    private void applyWallBounce(Ball ball) {
        double x = ball.getXDouble();
        double diameter = ball.getDiameter();

        if (x > GameConfig.WINDOW_WIDTH - diameter || x < 0) {
            ball.setSpeedX(ball.getSpeedX() * -1);
        }
    }

    /**
     * Stops horizontal movement when speed is negligibly small.
     * Prevents floating-point drift where a ball appears to creep
     * along the ground forever at 0.0001 pixels per frame.
     */
    private void applySpeedThreshold(Ball ball) {
        double speedX = ball.getSpeedX();
        if ((speedX < 0 && speedX > -GameConfig.SPEED_THRESHOLD) ||
            (speedX > 0 && speedX < GameConfig.SPEED_THRESHOLD)) {
            ball.setSpeedX(0);
        }
    }

    /**
     * Applies extra friction when the ball has stopped bouncing and
     * is resting on the ground. This makes balls eventually stop
     * instead of sliding infinitely.
     */
    private void applyGroundFriction(Ball ball) {
        double velocity = ball.getVelocity();
        double y = ball.getYDouble();
        double diameter = ball.getDiameter();

        if (velocity < GameConfig.SPEED_THRESHOLD && y >= GameConfig.WINDOW_HEIGHT - diameter) {
            ball.setSpeedX(ball.getSpeedX() * GameConfig.GROUND_FRICTION);
        }
    }

    /**
     * Applies gravity and air resistance to vertical movement.
     * Gravity is a constant additive acceleration (0.4 per frame).
     * Air resistance is multiplicative decay (0.999 per frame).
     */
    private void applyVerticalMovement(Ball ball) {
        double velocity = ball.getVelocity();
        velocity *= GameConfig.AIR_RESISTANCE_Y;     // Air resistance
        velocity += GameConfig.GRAVITY;               // Gravity pulls down
        ball.setVelocity(velocity);

        ball.setY(ball.getYDouble() + ball.getVelocityY() + velocity);
    }

    /**
     * Handles ground collision with energy-loss bouncing.
     * When the ball hits the bottom of the screen, its vertical velocity
     * is reversed with damping (loses 0.55 units per bounce), making
     * each bounce lower than the last until the ball comes to rest.
     */
    private void applyGroundBounce(Ball ball) {
        double y = ball.getYDouble();
        double diameter = ball.getDiameter();
        double velocity = ball.getVelocity();

        if (y + diameter >= GameConfig.WINDOW_HEIGHT) {
            velocity = velocity * -1 + GameConfig.BOUNCE_DAMPING;
            ball.setVelocity(velocity);

            // Prevent ball from sinking below ground
            if (velocity + GameConfig.GRAVITY + y + diameter >= GameConfig.WINDOW_HEIGHT) {
                ball.setY(GameConfig.WINDOW_HEIGHT - diameter);
            }
        }
    }
}
