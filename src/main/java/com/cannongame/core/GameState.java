package com.cannongame.core;

/**
 * Represents the possible states of the game.
 * 
 * WHY THIS EXISTS:
 * The original game had no concept of state — it just ran forever in an
 * infinite loop. Adding an explicit state enum enables pause/resume,
 * restart, and future menu/game-over screens without spaghetti boolean flags.
 */
public enum GameState {
    /** Game is actively running and accepting input. */
    RUNNING,

    /** Game is paused — rendering continues but physics/input are frozen. */
    PAUSED,

    /** A player has won or lost — show final scores, offer restart. */
    GAME_OVER
}
