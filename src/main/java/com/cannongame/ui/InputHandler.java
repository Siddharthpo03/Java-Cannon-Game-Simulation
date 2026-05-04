package com.cannongame.ui;

import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Centralized input state manager for mouse AND keyboard.
 * 
 * Supports full 2-player keyboard controls:
 * 
 * Player 1 (Left cannon):
 *   W/S  = Angle up/down
 *   A/D  = Power down/up
 *   Q/E  = Size down/up
 *   F/Space = Fire
 *   C    = Clear balls
 *   1-6  = Select color
 * 
 * Player 2 (Right cannon):
 *   ↑/↓  = Angle up/down
 *   ←/→  = Power down/up
 *   ,/.  (< >) = Size down/up
 *   Enter = Fire
 *   Backspace = Clear balls
 *   Numpad 1-6 = Select color
 * 
 * Global:
 *   P = Pause/Resume
 *   R = Restart
 */
public class InputHandler implements MouseListener, MouseMotionListener, KeyListener {

    private volatile boolean click;
    private volatile boolean dragging;
    private volatile int cursorX;
    private volatile int cursorY;

    // Game state keys (auto-consumed on read)
    private volatile boolean pausePressed;
    private volatile boolean restartPressed;

    // Held-key tracking for continuous movement (sliders)
    private final Set<Integer> heldKeys = new HashSet<>();

    // Player 1 fire/clear (one-shot, consumed on read)
    private volatile boolean p1FirePressed;
    private volatile boolean p1ClearPressed;
    private volatile int p1ColorSelect = -1; // -1 = no selection

    // Player 2 fire/clear (one-shot, consumed on read)
    private volatile boolean p2FirePressed;
    private volatile boolean p2ClearPressed;
    private volatile int p2ColorSelect = -1;

    // ── Mouse State Accessors ───────────────────────────────

    public boolean isClick() { return click; }
    public boolean isDragging() { return dragging; }
    public int getCursorX() { return cursorX; }
    public int getCursorY() { return cursorY; }

    /**
     * Consumes the current click so it isn't processed by multiple handlers.
     */
    public void consumeClick() { click = false; }

    // ── Keyboard State (auto-consumed) ──────────────────────

    public boolean isPausePressed() {
        boolean val = pausePressed;
        pausePressed = false;
        return val;
    }

    public boolean isRestartPressed() {
        boolean val = restartPressed;
        restartPressed = false;
        return val;
    }

    // ── Player 1 Keyboard Controls ──────────────────────────

    public boolean isP1FirePressed() {
        boolean val = p1FirePressed;
        p1FirePressed = false;
        return val;
    }

    public boolean isP1ClearPressed() {
        boolean val = p1ClearPressed;
        p1ClearPressed = false;
        return val;
    }

    /** Returns color index (0-5) or -1 if none selected. Auto-consumed. */
    public int getP1ColorSelect() {
        int val = p1ColorSelect;
        p1ColorSelect = -1;
        return val;
    }

    /** Checks if a key for P1 slider adjustment is currently held down. */
    public boolean isP1AngleUp()   { return heldKeys.contains(KeyEvent.VK_W); }
    public boolean isP1AngleDown() { return heldKeys.contains(KeyEvent.VK_S); }
    public boolean isP1PowerUp()   { return heldKeys.contains(KeyEvent.VK_D); }
    public boolean isP1PowerDown() { return heldKeys.contains(KeyEvent.VK_A); }
    public boolean isP1SizeUp()    { return heldKeys.contains(KeyEvent.VK_E); }
    public boolean isP1SizeDown()  { return heldKeys.contains(KeyEvent.VK_Q); }

    // ── Player 2 Keyboard Controls ──────────────────────────

    public boolean isP2FirePressed() {
        boolean val = p2FirePressed;
        p2FirePressed = false;
        return val;
    }

    public boolean isP2ClearPressed() {
        boolean val = p2ClearPressed;
        p2ClearPressed = false;
        return val;
    }

    /** Returns color index (0-5) or -1 if none selected. Auto-consumed. */
    public int getP2ColorSelect() {
        int val = p2ColorSelect;
        p2ColorSelect = -1;
        return val;
    }

    /** Checks if a key for P2 slider adjustment is currently held down. */
    public boolean isP2AngleUp()   { return heldKeys.contains(KeyEvent.VK_UP); }
    public boolean isP2AngleDown() { return heldKeys.contains(KeyEvent.VK_DOWN); }
    public boolean isP2PowerUp()   { return heldKeys.contains(KeyEvent.VK_RIGHT); }
    public boolean isP2PowerDown() { return heldKeys.contains(KeyEvent.VK_LEFT); }
    public boolean isP2SizeUp()    { return heldKeys.contains(KeyEvent.VK_PERIOD); }
    public boolean isP2SizeDown()  { return heldKeys.contains(KeyEvent.VK_COMMA); }

    // ── MouseListener ───────────────────────────────────────

    @Override
    public void mouseClicked(MouseEvent e) { click = true; }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    // ── MouseMotionListener ─────────────────────────────────

    @Override
    public void mouseDragged(MouseEvent e) {
        dragging = true;
        cursorX = e.getX();
        cursorY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        dragging = false;
        click = false;
        cursorX = e.getX();
        cursorY = e.getY();
    }

    // ── KeyListener ─────────────────────────────────────────

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        heldKeys.add(code);

        switch (code) {
            // ── Global ──
            case KeyEvent.VK_P -> pausePressed = true;
            case KeyEvent.VK_R -> restartPressed = true;

            // ── Player 1 Fire/Clear ──
            case KeyEvent.VK_F, KeyEvent.VK_SPACE -> p1FirePressed = true;
            case KeyEvent.VK_C -> p1ClearPressed = true;

            // ── Player 1 Color Selection (number keys 1-6) ──
            case KeyEvent.VK_1 -> p1ColorSelect = 0;
            case KeyEvent.VK_2 -> p1ColorSelect = 1;
            case KeyEvent.VK_3 -> p1ColorSelect = 2;
            case KeyEvent.VK_4 -> p1ColorSelect = 3;
            case KeyEvent.VK_5 -> p1ColorSelect = 4;
            case KeyEvent.VK_6 -> p1ColorSelect = 5;

            // ── Player 2 Fire/Clear ──
            case KeyEvent.VK_ENTER -> p2FirePressed = true;
            case KeyEvent.VK_BACK_SPACE -> p2ClearPressed = true;

            // ── Player 2 Color Selection (numpad 1-6) ──
            case KeyEvent.VK_NUMPAD1 -> p2ColorSelect = 0;
            case KeyEvent.VK_NUMPAD2 -> p2ColorSelect = 1;
            case KeyEvent.VK_NUMPAD3 -> p2ColorSelect = 2;
            case KeyEvent.VK_NUMPAD4 -> p2ColorSelect = 3;
            case KeyEvent.VK_NUMPAD5 -> p2ColorSelect = 4;
            case KeyEvent.VK_NUMPAD6 -> p2ColorSelect = 5;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
    }
}
