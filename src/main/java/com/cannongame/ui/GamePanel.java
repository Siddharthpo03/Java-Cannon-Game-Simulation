package com.cannongame.ui;

import com.cannongame.audio.SoundManager;
import com.cannongame.core.GameConfig;
import com.cannongame.core.GameState;
import com.cannongame.engine.CollisionEngine;
import com.cannongame.engine.PhysicsEngine;
import com.cannongame.entities.Ball;
import com.cannongame.entities.Cannon;
import com.cannongame.entities.Cloud;
import com.cannongame.entities.FireEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Main game rendering panel — the central coordinator.
 * 
 * WHY THIS CHANGED:
 * The original GamePanel (lines 633-877) was a god class responsible for:
 * - Game loop (now in GameLoop)
 * - Input handling (now in InputHandler)
 * - Sound playback (now in SoundManager)
 * - Collision detection (now in CollisionEngine)
 * - Score management (now in HUD + Cannon)
 * - All rendering
 * 
 * This refactored GamePanel is now purely a coordinator: it owns the
 * double-buffered image, calls update/render methods on the appropriate
 * components, and manages game state transitions. Each responsibility
 * has been delegated to a focused, single-purpose class.
 */
public class GamePanel extends JPanel {

    // ── Keyboard slider step (pixels per frame while key is held) ────
    private static final int KEYBOARD_SLIDER_STEP = 3;

    // ── Rendering ───────────────────────────────────────────
    private BufferedImage image;
    private Graphics2D g;

    // ── Game State ──────────────────────────────────────────
    private GameState state = GameState.RUNNING;

    // ── Input ───────────────────────────────────────────────
    private final InputHandler inputHandler = new InputHandler();

    // ── Audio ───────────────────────────────────────────────
    private final SoundManager soundManager = new SoundManager();

    // ── Engines ─────────────────────────────────────────────
    private final PhysicsEngine physicsEngine = new PhysicsEngine();
    private final CollisionEngine collisionEngine = new CollisionEngine();

    // ── Entities ────────────────────────────────────────────
    private final Cannon cannon1;
    private final Cannon cannon2;

    private final SliderInput angleSlider1;
    private final SliderInput sizeSlider1;
    private final SliderInput powerSlider1;

    private final SliderInput angleSlider2;
    private final SliderInput sizeSlider2;
    private final SliderInput powerSlider2;

    private final List<Cloud> clouds = new ArrayList<>();
    private final List<FireEffect> fireEffects = new ArrayList<>();

    // ── UI ──────────────────────────────────────────────────
    private final HUD hud = new HUD();

    // ── Constructor ─────────────────────────────────────────

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        setFocusable(true);
        requestFocus();

        // Register input handler
        addMouseListener(inputHandler);
        addMouseMotionListener(inputHandler);
        addKeyListener(inputHandler);

        // Initialize cannons (unified class, parameterized by side)
        cannon1 = new Cannon(
            Cannon.Side.LEFT,
            GameConfig.C1_X,
            GameConfig.C1_FIRE_BTN_X, GameConfig.C1_FIRE_BTN_Y,
            GameConfig.C1_CLEAR_BTN_X, GameConfig.C1_CLEAR_BTN_Y,
            GameConfig.C1_COLOR_SEL_X, GameConfig.C1_COLOR_SEL_Y,
            soundManager, inputHandler
        );

        cannon2 = new Cannon(
            Cannon.Side.RIGHT,
            GameConfig.C2_X,
            GameConfig.C2_FIRE_BTN_X, GameConfig.C2_FIRE_BTN_Y,
            GameConfig.C2_CLEAR_BTN_X, GameConfig.C2_CLEAR_BTN_Y,
            GameConfig.C2_COLOR_SEL_X, GameConfig.C2_COLOR_SEL_Y,
            soundManager, inputHandler
        );

        // Initialize sliders (with proper label positioning, no space-padding hack)
        angleSlider1 = new SliderInput(GameConfig.C1_SLIDER_X, 155, 157, 0, "Angle", inputHandler);
        sizeSlider1 = new SliderInput(GameConfig.C1_SLIDER_X, 225, 0, 75, "Size", inputHandler);
        powerSlider1 = new SliderInput(GameConfig.C1_SLIDER_X, 295, 150, 0, "Power", inputHandler);

        angleSlider2 = new SliderInput(GameConfig.C2_SLIDER_X, 155, 157, 0, "Angle", inputHandler);
        sizeSlider2 = new SliderInput(GameConfig.C2_SLIDER_X, 225, 0, 75, "Size", inputHandler);
        powerSlider2 = new SliderInput(GameConfig.C2_SLIDER_X, 295, 150, 0, "Power", inputHandler);

        // Initialize clouds
        for (int i = 0; i < GameConfig.CLOUD_COUNT; i++) {
            clouds.add(new Cloud());
        }

        // Initialize double-buffer
        image = new BufferedImage(
            GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT,
            BufferedImage.TYPE_INT_RGB
        );
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Play startup sound
        soundManager.play("gamestart");
    }

    // ── Game Loop Methods (called by GameLoop) ──────────────

    /**
     * Updates game logic: physics, collisions, state transitions.
     * Skipped when game is paused.
     */
    public void gameUpdate() {
        // Handle keyboard input for state changes
        handleStateInput();

        if (state != GameState.RUNNING) return;

        // Handle keyboard controls for both players
        handleKeyboardControls();

        // Update physics for all balls
        for (Ball ball : cannon1.getBalls()) {
            physicsEngine.update(ball);
        }
        for (Ball ball : cannon2.getBalls()) {
            physicsEngine.update(ball);
        }

        // Check collisions between the two players' projectiles
        List<CollisionEngine.CollisionResult> collisions =
            collisionEngine.processCollisions(cannon1.getBalls(), cannon2.getBalls());

        // Process collision results
        for (CollisionEngine.CollisionResult result : collisions) {
            fireEffects.add(new FireEffect(result.collisionX, result.collisionY));
            soundManager.play("explosion");

            // Scoring
            if (result.winner > 0) {
                cannon1.addScore(GameConfig.SCORE_DESTROY);
            } else if (result.winner < 0) {
                cannon2.addScore(GameConfig.SCORE_DESTROY);
            } else {
                cannon1.addScore(GameConfig.SCORE_MUTUAL);
                cannon2.addScore(GameConfig.SCORE_MUTUAL);
            }
        }
    }

    /**
     * Renders the entire game scene to the off-screen buffer.
     */
    public void gameRender() {
        // Background
        g.setColor(GameConfig.SKY_COLOR);
        g.fillRect(0, 0, GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT);

        // Ground
        g.setColor(GameConfig.GROUND_COLOR);
        g.fillRect(0, GameConfig.WINDOW_HEIGHT - GameConfig.GROUND_HEIGHT,
                   GameConfig.WINDOW_WIDTH, GameConfig.GROUND_HEIGHT);

        // Clouds
        for (Cloud cloud : clouds) {
            cloud.draw(g);
        }

        // Cannons with their balls
        cannon1.draw(g, angleSlider1.getValue(), sizeSlider1.getValue(), powerSlider1.getValue());
        cannon2.draw(g, angleSlider2.getValue(), sizeSlider2.getValue(), powerSlider2.getValue());

        // Fire effects
        for (int i = fireEffects.size() - 1; i >= 0; i--) {
            FireEffect fire = fireEffects.get(i);
            fire.draw(g);
            if (fire.isDone()) {
                fireEffects.remove(i);
            }
        }

        // Sliders
        angleSlider1.draw(g);
        sizeSlider1.draw(g);
        powerSlider1.draw(g);
        angleSlider2.draw(g);
        sizeSlider2.draw(g);
        powerSlider2.draw(g);

        // HUD (scores, control hints)
        hud.drawScores(g, cannon1.getScore(), cannon2.getScore());
        hud.drawControlHints(g);

        // State overlays (drawn last, on top of everything)
        if (state == GameState.PAUSED) {
            hud.drawPauseOverlay(g);
        } else if (state == GameState.GAME_OVER) {
            hud.drawGameOverOverlay(g, cannon1.getScore(), cannon2.getScore());
        }
    }

    /**
     * Blits the off-screen buffer to the screen (double buffering).
     */
    public void gameDraw() {
        Graphics g2 = this.getGraphics();
        if (g2 != null) {
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
        }
    }

    // ── Keyboard Controls ────────────────────────────────────

    /**
     * Processes keyboard input for both players each frame.
     * Slider adjustments are continuous (runs every frame while held).
     * Fire/clear/color are one-shot (consumed on read).
     */
    private void handleKeyboardControls() {
        // ── Player 1: WASD + QE + Space/F + C + 1-6 ──
        if (inputHandler.isP1AngleUp())   angleSlider1.adjustValue(KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP1AngleDown()) angleSlider1.adjustValue(-KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP1PowerUp())   powerSlider1.adjustValue(KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP1PowerDown()) powerSlider1.adjustValue(-KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP1SizeUp())    sizeSlider1.adjustValue(KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP1SizeDown())  sizeSlider1.adjustValue(-KEYBOARD_SLIDER_STEP);

        if (inputHandler.isP1FirePressed())  cannon1.fireFromKeyboard();
        if (inputHandler.isP1ClearPressed()) cannon1.clearBalls();

        int p1Color = inputHandler.getP1ColorSelect();
        if (p1Color >= 0) cannon1.selectColor(p1Color);

        // ── Player 2: Arrows + <> + Enter + Backspace + Numpad 1-6 ──
        if (inputHandler.isP2AngleUp())   angleSlider2.adjustValue(KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP2AngleDown()) angleSlider2.adjustValue(-KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP2PowerUp())   powerSlider2.adjustValue(KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP2PowerDown()) powerSlider2.adjustValue(-KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP2SizeUp())    sizeSlider2.adjustValue(KEYBOARD_SLIDER_STEP);
        if (inputHandler.isP2SizeDown())  sizeSlider2.adjustValue(-KEYBOARD_SLIDER_STEP);

        if (inputHandler.isP2FirePressed())  cannon2.fireFromKeyboard();
        if (inputHandler.isP2ClearPressed()) cannon2.clearBalls();

        int p2Color = inputHandler.getP2ColorSelect();
        if (p2Color >= 0) cannon2.selectColor(p2Color);
    }

    // ── State Management ────────────────────────────────────

    private void handleStateInput() {
        if (inputHandler.isPausePressed()) {
            if (state == GameState.RUNNING) {
                state = GameState.PAUSED;
            } else if (state == GameState.PAUSED) {
                state = GameState.RUNNING;
            }
        }

        if (inputHandler.isRestartPressed()) {
            restartGame();
        }
    }

    private void restartGame() {
        cannon1.reset();
        cannon2.reset();
        fireEffects.clear();
        state = GameState.RUNNING;

        // Reset sliders
        angleSlider1.reset();
        sizeSlider1.reset();
        powerSlider1.reset();
        angleSlider2.reset();
        sizeSlider2.reset();
        powerSlider2.reset();
    }
}
