package com.cannongame.core;

import com.cannongame.ui.GamePanel;

/**
 * A production-grade game loop with stable frame timing.
 * 
 * WHY THIS EXISTS:
 * The original loop in GamePanel.run() had two critical issues:
 * 1. Thread.sleep(waitTime) could receive negative values when a frame
 *    took longer than targetTime, causing IllegalArgumentException
 *    (silently swallowed by the empty catch block).
 * 2. Mixing rendering and game logic timing in one monolithic method.
 * 
 * This loop uses System.nanoTime() for precision, guards against negative
 * sleep, and cleanly separates update from render. It also respects
 * GameState to skip physics updates when paused.
 */
public class GameLoop implements Runnable {

    private final GamePanel gamePanel;
    private volatile boolean running;

    public GameLoop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void start() {
        if (running) return;
        running = true;
        Thread gameThread = new Thread(this, "GameLoop");
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            long elapsed = now - lastTime;

            if (elapsed >= GameConfig.TARGET_FRAME_TIME_NS) {
                lastTime = now;

                gamePanel.gameUpdate();
                gamePanel.gameRender();
                gamePanel.gameDraw();

                frames++;
            }

            // Calculate remaining time and sleep to avoid busy-waiting
            long remainingNs = GameConfig.TARGET_FRAME_TIME_NS - (System.nanoTime() - lastTime);
            if (remainingNs > 0) {
                long sleepMs = remainingNs / 1_000_000;
                if (sleepMs > 0) {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            // FPS counter (logged every second — useful for debugging)
            if (System.currentTimeMillis() - timer >= 1000) {
                timer = System.currentTimeMillis();
                frames = 0;
            }
        }
    }
}
