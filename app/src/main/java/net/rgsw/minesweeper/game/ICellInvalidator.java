package net.rgsw.minesweeper.game;

public interface ICellInvalidator {
    void invalidateCell( int x, int y );

    void invalidateAll();

    default void invalidateArea( int x1, int y1, int x2, int y2 ) {
        for( int x = x1; x <= x2; x++ ) {
            for( int y = y1; y <= y2; y++ ) {
                invalidateCell( x, y );
            }
        }
    }
}
