package com.cannongame.entities;

import java.awt.Color;

/**
 * Represents a cannonball projectile in the game world.
 * 
 * WHY THIS CHANGED:
 * The original Ball class (lines 40-148) mixed data storage with physics
 * simulation. Ball.update() contained gravity, air resistance, wall bouncing,
 * and ground collision — all of which are physics engine concerns.
 * 
 * This refactored Ball is a pure data entity: it holds position, velocity,
 * size, and color. The PhysicsEngine operates on Ball instances externally.
 * This follows the Entity-Component-System pattern and makes both the
 * entity and physics independently testable.
 * 
 * BUG FIX: The original setVelocityY() set this.velocity instead of
 * this.velocityY. Both fields are now properly accessible.
 */
public class Ball {

    private double x;
    private double y;
    private double diameter;

    private double speedX;
    private double speedY;
    private double velocity;

    private double velocityX;
    private double velocityY;

    private int size;
    private int power;
    private Color color;

    public Ball(int x, int y, int diameter, int speedX, int speedY,
                Color color, int size, int power, double speedDivisor) {
        this.x = x;
        this.y = y;
        this.diameter = diameter;
        this.speedX = speedX / speedDivisor;
        this.speedY = (speedY / speedDivisor) * -1;
        this.color = color;
        this.size = size;
        this.power = power;
        this.velocity = this.speedY;
    }

    // ── Position ────────────────────────────────────────────

    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    public double getXDouble() { return x; }
    public double getYDouble() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    // ── Diameter / Radius ───────────────────────────────────

    public int getDiameter() { return (int) diameter; }
    public double getRadius() { return diameter / 2.0; }

    // ── Horizontal speed (launch speed with decay) ──────────

    public double getSpeedX() { return speedX; }
    public void setSpeedX(double speedX) { this.speedX = speedX; }

    // ── Velocity components (from collision response, etc.) ─

    public double getVelocityX() { return velocityX; }
    public void setVelocityX(double velocityX) { this.velocityX = velocityX; }

    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double velocityY) { this.velocityY = velocityY; }

    // ── Vertical velocity (gravity-affected) ────────────────

    public double getVelocity() { return velocity; }
    public void setVelocity(double velocity) { this.velocity = velocity; }

    // ── Properties ──────────────────────────────────────────

    public int getSize() { return size; }
    public int getPower() { return power; }
    public Color getColor() { return color; }
}
