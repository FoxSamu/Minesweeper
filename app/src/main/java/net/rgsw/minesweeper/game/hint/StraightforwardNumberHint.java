package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.EMark;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class StraightforwardNumberHint extends CellIteratingHint {
    @Override
    public boolean findHintAt( MinesweeperGame game, int x, int y ) {
        int num = game.getRemainingMinesAround( x, y );
        if( num < 1 || num == 9 ) return false;
        int actual = game.amountOfUnrevealedAdjacentTiles( x, y ) - game.findAdjacentFlags( x, y );
        if( actual == num ) {
            setMark( x, y, EMark.BLUE );
            for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
                for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                    if( x1 == x && y1 == y ) continue;
                    if( game.outOfRange( x1, y1 ) ) continue;
                    if( game.isRevealed( x1, y1 ) ) continue;
                    if( game.isFlagged( x1, y1 ) ) continue;
                    addInferredFlag( x1, y1 );
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_inferred_flag;
    }
}
