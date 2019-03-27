package net.rgsw.minesweeper.game;

/**
 * Used in {@link IGame}s to mark specific cells, either using a border or a background
 */
public enum EMark {
    RED,
    GREEN,
    BLUE,
    YELLOW,
    ORANGE,
    PURPLE,
    INVERSE // White on dark theme, black on light theme
}
