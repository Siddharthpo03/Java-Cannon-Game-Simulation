package com.cannongame.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized sound manager with resource-based loading and caching.
 * 
 * WHY THIS EXISTS:
 * The original code had THREE identical copies of playSound() — one in
 * Cannon_1, one in Cannon_2, and one in GamePanel. Each used hardcoded
 * absolute paths like "E:\\Sounds\\cannonfire.wav", which meant:
 * 
 * 1. The game was IMPOSSIBLE to run on any other machine
 * 2. The game couldn't be distributed as a JAR file
 * 3. Every sound call opened and parsed the file from disk (no caching)
 * 4. Error handling was inconsistent across the three copies
 * 
 * This SoundManager:
 * 1. Loads sounds from the classpath (bundled in JAR) via getResource()
 * 2. Caches URL references for fast repeated playback
 * 3. Plays sounds on background threads to avoid blocking the game loop
 * 4. Handles errors gracefully with a single, consistent error path
 * 5. Is a single instance shared by all components (no duplication)
 */
public class SoundManager {

    private final Map<String, URL> soundCache = new HashMap<>();

    public SoundManager() {
        // Pre-load all game sounds from classpath resources
        loadSound("cannonfire", "/sounds/cannonfire.wav");
        loadSound("metal", "/sounds/metal.wav");
        loadSound("metal1", "/sounds/metal1.wav");
        loadSound("gamestart", "/sounds/gamestart.wav");
        loadSound("explosion", "/sounds/pixel_burst.wav");
        loadSound("explosion1", "/sounds/pixel_burst1.wav");
    }

    /**
     * Registers a sound resource by name.
     * Uses getClass().getResource() which searches the classpath —
     * this works whether running from IDE, Maven, or a JAR file.
     */
    private void loadSound(String name, String resourcePath) {
        URL url = getClass().getResource(resourcePath);
        if (url != null) {
            soundCache.put(name, url);
        } else {
            System.err.println("[SoundManager] WARNING: Sound not found: " + resourcePath);
        }
    }

    /**
     * Plays a named sound on a background thread.
     * 
     * Each call creates a new Clip so multiple overlapping sounds
     * can play simultaneously (e.g., two cannons firing at once).
     * The clip auto-closes when playback finishes.
     */
    public void play(String name) {
        URL url = soundCache.get(name);
        if (url == null) {
            System.err.println("[SoundManager] Sound not loaded: " + name);
            return;
        }

        new Thread(() -> {
            try {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);

                // Auto-close clip when playback finishes to prevent resource leaks
                clip.addLineListener(event -> {
                    if (event.getType() == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.start();
            } catch (Exception e) {
                System.err.println("[SoundManager] Error playing sound '" + name + "': " + e.getMessage());
            }
        }, "SoundThread-" + name).start();
    }

    /**
     * Checks if a sound is loaded and available for playback.
     */
    public boolean isLoaded(String name) {
        return soundCache.containsKey(name);
    }
}
