package net.rgsw.minesweeper.game;

public interface ICellInvalidator {
    void invalidateCell( int x, int y );

    void invalidateAll();
}
