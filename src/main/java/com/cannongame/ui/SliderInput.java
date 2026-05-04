package com.cannongame.ui;

import com.cannongame.core.GameConfig;

import java.awt.*;

/**
 * Custom slider control for adjusting cannon parameters (angle, size, power).
 * 
 * WHY THIS CHANGED:
 * The original SliderInput (lines 880-943) read input from GamePanel static
 * fields and used a hack — generateSpaces(242) — to position the label for
 * right-side sliders. This refactored version:
 * 
 * 1. Takes an InputHandler reference instead of reading statics
 * 2. Uses proper x-coordinate math for label centering (no space-padding hack)
 * 3. Extracts constants to GameConfig
 */
public class SliderInput {

    private final int min;
    private final int max;
    private final int x;
    private final int y;
    private final String label;

    private final int width = GameConfig.SLIDER_WIDTH;
    private final int height = 10;
    private final int sliderWidth = 10;
    private final int sliderHeight = 30;

    private int sliderX;
    private boolean sliderGrabbed = false;

    private final InputHandler inputHandler;

    public SliderInput(int x, int y, int min, int max, String label, InputHandler inputHandler) {
        this.x = x;
        this.y = y;
        this.min = min;
        this.max = max;
        this.label = label;
        this.inputHandler = inputHandler;
        this.sliderX = (width / 2) - (sliderWidth / 2);
    }

    public void draw(Graphics2D g) {
        // Handle dragging
        if (inputHandler.isDragging() &&
            inputHandler.getCursorX() > (sliderX - 10) + x &&
            inputHandler.getCursorX() < sliderX + (sliderWidth + 10) + x &&
            inputHandler.getCursorY() > y &&
            inputHandler.getCursorY() < y + height) {
            sliderGrabbed = true;
        }

        if (!inputHandler.isDragging()) {
            sliderGrabbed = false;
        }

        if (sliderGrabbed &&
            inputHandler.getCursorX() > x + (sliderWidth / 2) &&
            inputHandler.getCursorX() < x + width - 1) {
            sliderX = inputHandler.getCursorX() - x - (sliderWidth / 2);
        }

        // Draw track
        g.setColor(GameConfig.SLIDER_TRACK_COLOR);
        g.fillRect(x, y, width, height);

        // Draw thumb
        g.setColor(Color.BLACK);
        g.fillRect(sliderX + x, y - (sliderHeight / 3), sliderWidth, sliderHeight);

        // Draw label — centered above the slider using font metrics
        g.setColor(Color.BLACK);
        Font f = new Font("Calibri", Font.BOLD, 24);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics(f);
        int labelWidth = fm.stringWidth(label);
        int labelX = x + (width - labelWidth) / 2;
        g.drawString(label, labelX, y - 12);
    }

    /**
     * Returns the current slider value mapped to the [min, max] range.
     */
    public int getValue() {
        return (int) ((double) (sliderX + (sliderWidth / 2)) / (double) width * (max - min));
    }

    /**
     * Adjusts the slider position by a delta amount (positive = right, negative = left).
     * Used for keyboard-driven control. Clamped to valid slider range.
     */
    public void adjustValue(int delta) {
        sliderX = Math.max(0, Math.min(width - sliderWidth, sliderX + delta));
    }

    /**
     * Resets slider to center position.
     */
    public void reset() {
        sliderX = (width / 2) - (sliderWidth / 2);
    }
}
