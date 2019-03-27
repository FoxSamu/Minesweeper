package net.rgsw.minesweeper.settings;

public interface ISettingsEnum <T> {
    int getDisplayNameRes( int index );

    T getValue( int index );

    int getCount();

    int getIndex( T value );
}
