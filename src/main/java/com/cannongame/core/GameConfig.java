package com.cannongame.core;

import java.awt.Color;

/**
 * Centralized configuration constants for the entire game.
 * 
 * WHY THIS EXISTS:
 * In the original code, magic numbers like gravity (0.4), air resistance (0.996),
 * window dimensions (1550x794), and bounce damping (0.55) were scattered across
 * Ball, Cannon_1, Cannon_2, and GamePanel. Changing any physics value required
 * hunting through 957 lines. This class makes tuning trivial and documents
 * every constant's purpose.
 */
public final class GameConfig {

    private GameConfig() {} // Prevent instantiation

    // ── Window ──────────────────────────────────────────────
    public static final int WINDOW_WIDTH = 1550;
    public static final int WINDOW_HEIGHT = 794;
    public static final String WINDOW_TITLE = "Cannon Battle — 2 Player";

    // ── Game Loop ───────────────────────────────────────────
    public static final int TARGET_FPS = 60;
    public static final long NANOS_PER_SECOND = 1_000_000_000L;
    public static final long TARGET_FRAME_TIME_NS = NANOS_PER_SECOND / TARGET_FPS;

    // ── Physics ─────────────────────────────────────────────
    public static final double GRAVITY = 0.4;
    public static final double AIR_RESISTANCE_X = 0.996;
    public static final double AIR_RESISTANCE_Y = 0.999;
    public static final double BOUNCE_DAMPING = 0.55;
    public static final double SPEED_THRESHOLD = 0.1;
    public static final double GROUND_FRICTION = 0.996;
    public static final double SPEED_DIVISOR = 4.5;

    // ── World ───────────────────────────────────────────────
    public static final int GROUND_HEIGHT = 50;
    public static final int GROUND_Y = WINDOW_HEIGHT - GROUND_HEIGHT;

    // ── Cannon ──────────────────────────────────────────────
    public static final int CANNON_BARREL_WIDTH = 300;
    public static final int CANNON_BASE_DIAMETER = 100;
    public static final int CANNON_WHEEL_SIZE = 100;
    public static final Color CANNON_WHEEL_COLOR = new Color(139, 69, 19);

    // ── Cannon 1 (Left) Positions ───────────────────────────
    public static final int C1_X = 90;
    public static final int C1_FIRE_BTN_X = 105;
    public static final int C1_FIRE_BTN_Y = 350;
    public static final int C1_CLEAR_BTN_X = 320;
    public static final int C1_CLEAR_BTN_Y = 25;
    public static final int C1_COLOR_SEL_X = 50;
    public static final int C1_COLOR_SEL_Y = 75;
    public static final int C1_SLIDER_X = 50;

    // ── Cannon 2 (Right) Positions ──────────────────────────
    public static final int C2_X = 1400;
    public static final int C2_FIRE_BTN_X = 1275;
    public static final int C2_FIRE_BTN_Y = 350;
    public static final int C2_CLEAR_BTN_X = 1075;
    public static final int C2_CLEAR_BTN_Y = 25;
    public static final int C2_COLOR_SEL_X = 1200;
    public static final int C2_COLOR_SEL_Y = 75;
    public static final int C2_SLIDER_X = 1200;

    // ── UI ──────────────────────────────────────────────────
    public static final int FIRE_BUTTON_WIDTH = 100;
    public static final int FIRE_BUTTON_HEIGHT = 50;
    public static final int CLEAR_BUTTON_WIDTH = 100;
    public static final int CLEAR_BUTTON_HEIGHT = 40;
    public static final int COLOR_BOX_WIDTH = 20;
    public static final int SLIDER_WIDTH = 250;

    // ── Colors ──────────────────────────────────────────────
    public static final Color SKY_COLOR = new Color(197, 244, 243);
    public static final Color GROUND_COLOR = new Color(228, 222, 109);
    public static final Color CLOUD_COLOR = new Color(204, 204, 204);
    public static final Color SLIDER_TRACK_COLOR = Color.GRAY;
    public static final Color MENU_BOX_COLOR = new Color(200, 200, 200);

    public static final Color[] BALL_COLORS = {
        new Color(85, 85, 85),
        new Color(3, 61, 180),
        new Color(255, 0, 0),
        new Color(27, 137, 60),
        new Color(255, 177, 14),
        new Color(164, 73, 164)
    };

    // ── Fire Effect ─────────────────────────────────────────
    public static final int FIRE_EFFECT_DURATION = 40;
    public static final Color[] FIRE_COLORS = {
        Color.ORANGE, Color.RED, Color.YELLOW, Color.BLUE, Color.WHITE
    };

    // ── Scoring ─────────────────────────────────────────────
    public static final int SCORE_DESTROY = 10;
    public static final int SCORE_MUTUAL = 5;

    // ── Cloud ───────────────────────────────────────────────
    public static final int CLOUD_COUNT = 4;
    public static final int CLOUD_MIN_SIZE = 30;
    public static final int CLOUD_MAX_SIZE = 60;
    public static final int CLOUD_MAX_Y = 400;
    public static final double CLOUD_MIN_SPEED = 0.25;
    public static final double CLOUD_MAX_SPEED = 0.75;
}
