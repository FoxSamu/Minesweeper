package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.game.MinesweeperGame;

public abstract class CellIteratingHint extends Hint {
    @Override
    public boolean findHint( MinesweeperGame game ) {
        int width = game.width();
        int height = game.height();
        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                if( findHintAt( game, x, y ) ) {
                    if( getViewLocation() == null ) {
                        setViewLocation( x, y );
                    }
                    return true;
                } else {
                    removeViewLocation();
                }
            }
        }
        return false;
    }

    public abstract boolean findHintAt( MinesweeperGame game, int x, int y );

}
