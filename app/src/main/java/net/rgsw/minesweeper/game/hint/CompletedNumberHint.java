package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.EMark;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class CompletedNumberHint extends CellIteratingHint {

    @Override
    public boolean findHintAt( MinesweeperGame game, int x, int y ) {
        if( !game.isRevealed( x, y ) ) return false;
        int num = game.getNumber( x, y );
        int amount = game.amountOfUnrevealedAdjacentTiles( x, y );
        if( num > 0 && num == game.findAdjacentFlags( x, y ) && amount > num ) {
            for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
                for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                    if( x1 == x && y1 == y ) continue;
                    if( game.outOfRange( x1, y1 ) ) continue;
                    if( game.isRevealed( x1, y1 ) ) continue;
                    if( game.isFlagged( x1, y1 ) ) continue;
                    addInferredDig( x1, y1 );
                }
            }
            setMark( x, y, EMark.GREEN );
            return true;
        }
        return false;
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_enough_flags;
    }
}
