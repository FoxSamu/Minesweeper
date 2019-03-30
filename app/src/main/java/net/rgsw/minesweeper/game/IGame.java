package net.rgsw.minesweeper.game;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Defines a basic minesweeper game. For a normal implementation, see {@link MinesweeperGame}.
 */
public interface IGame {
    /**
     * Returns the state of a cell at a given location.
     * @param x Location x, in cell coords
     * @param y Location y, in cell coords
     * @return The state of the cell. Must not be null.
     */
    @NonNull
    ECellState getState( int x, int y );

    /**
     * Returns the border mark of a cell at a given location.
     * @param x Location x, in cell coords
     * @param y Location y, in cell coords
     * @return The border mark of the cell, may be null for no mark
     */
    @Nullable
    default EMark getBorderMark( int x, int y ) {
        return null;
    }

    /**
     * Returns the background mark of a cell at a given location
     * @param x Location x, in cell coords
     * @param y Location y, in cell coords
     * @return The background mark of the cell, may be null for no mark
     */
    @Nullable
    default EMark getBackgroundMark( int x, int y ) {
        return null;
    }

    /**
     * Returns whether a cell at a given location could be flagged. Used in the canvas to render 'inferred flag' icons.
     * @param x Location x, in cell coords
     * @param y Location y, in cell coords
     * @return True whether the 'inferred flag' icon should be rendered at this cell
     */
    default boolean isInferredFlag( int x, int y ) {
        return false;
    }

    /**
     * Returns whether a cell at a given location could be dug. Used in the canvas to render 'inferred dig' icons.
     * @param x Location x, in cell coords
     * @param y Location y, in cell coords
     * @return True whether the 'inferred dig' icon should be rendered at this cell
     */
    default boolean isInferredDig( int x, int y ) {
        return false;
    }

    /**
     * Returns whether there is a cell at a given location
     * @param x Location x, in cell coords
     * @param y Location y, in cell coords
     * @return True when there is a cell at the given coords, false other wise
     */
    default boolean cellAt( int x, int y ) {
        return true;
    }

    /**
     * Checks whether the game is either won or lost
     * @return True when won or lost, false otherwise
     */
    boolean done();

    /**
     * Checks whether the game is won
     * @return True when won, false otherwise
     */
    boolean won();

    /**
     * @return The width of this game
     */
    int width();

    /**
     * @return The height of this game
     */
    int height();

    /**
     * @return The amount of mines in this game
     */
    int mines();

    /**
     * @return The amount of actually flagged cells in this game (not the inferred flags)
     */
    int totalFlags();
}
