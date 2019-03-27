package net.rgsw.minesweeper.game;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A game implementation shown in the visual editor.
 */
public class EditModeGame implements IGame {

    @NonNull
    @Override
    public ECellState getState( int x, int y ) {
        if( y == 0 || y == 1 ) {
            return ECellState.UNREVEALED;
        }
        if( y == 2 || y == 3 ) {
            return ECellState.NOTHING;
        }
        if( y == 4 ) {
            if( x == 1 ) return ECellState.FLAGGED;
            if( x == 2 ) return ECellState.SOFT_MARKED;
            return ECellState.UNREVEALED;
        }
        if( y == 5 ) {
            if( x == 0 ) return ECellState.FOUND_1;
            if( x == 1 ) return ECellState.FOUND_2;
            if( x == 2 ) return ECellState.FOUND_3;
            if( x == 3 ) return ECellState.FOUND_4;
            if( x == 4 ) return ECellState.FOUND_5;
            if( x == 5 ) return ECellState.FOUND_6;
            if( x == 6 ) return ECellState.FOUND_7;
            if( x == 7 ) return ECellState.FOUND_8;
        }
        if( y == 6 ) {
            if( x == 0 ) return ECellState.MINE;
            if( x == 1 ) return ECellState.MINE_WIN;
            if( x == 2 ) return ECellState.NO_MINE;
            if( x == 3 ) return ECellState.FLAGGED_MINE;
            return ECellState.NOTHING;
        }
        return ECellState.NOTHING;
    }

    @Override
    public boolean isInferredFlag( int x, int y ) {
        return x == 0 && y == 4;
    }

    @Nullable
    @Override
    public EMark getBackgroundMark( int x, int y ) {
        if( y != 0 && y != 2 ) return null;
        return EMark.values()[ x % 7 ];

    }

    @Nullable
    @Override
    public EMark getBorderMark( int x, int y ) {
        if( y != 1 && y != 3 ) return null;
        return EMark.values()[ x % 7 ];
    }

    @Override
    public boolean done() {
        return false;
    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public int width() {
        return 10;
    }

    @Override
    public int height() {
        return 10;
    }

    @Override
    public int mines() {
        return 0;
    }

    @Override
    public int totalFlags() {
        return 0;
    }
}
