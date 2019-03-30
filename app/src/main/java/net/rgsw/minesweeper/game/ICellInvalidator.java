package net.rgsw.minesweeper.game;

/**
 * Object that could be attached to a game to listen to cell updates, specifically for rendering. The game activity
 * uses and implements this to update render chunks for improved performance.
 */
public interface ICellInvalidator {
    /**
     * Invalidates a cell: marks the cell for being rerendered. A typical implementation would rerender all not-diagonal
     * adjacent cells for updating the rounded corner connections.
     * @param x The x coordinate of the cell to update
     * @param y The y coordinate of the cell to update
     */
    void invalidateCell( int x, int y );

    /**
     * Invalidates all cells: marks every cell for being rerendered. For higher performance, loop over all rendering
     * regions and invalidate each one once, instead of looping over cells and invalidating them manually.
     */
    void invalidateAll();

    /**
     * Invalidates an area: marks every cell in the specified area for being rerendered. This calls
     * {@link #invalidateCell(int, int)} for each cell in the specified area.
     * @param x1 The lowest x coordinate of the area
     * @param y1 The lowest y coordinate of the area
     * @param x2 The highest x coordinate of the area
     * @param y2 The highest y coordinate of the area
     */
    default void invalidateArea( int x1, int y1, int x2, int y2 ) {
        for( int x = x1; x <= x2; x++ ) {
            for( int y = y1; y <= y2; y++ ) {
                invalidateCell( x, y );
            }
        }
    }
}
