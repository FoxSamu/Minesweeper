package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.EMark;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class FlagNextToZeroHint extends CellIteratingHint {
    @Override
    public boolean findHintAt( MinesweeperGame game, int x, int y ) {
        if( !game.isFlagged( x, y ) ) return false;
        boolean found = false;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue;
                if( game.outOfRange( x1, y1 ) ) continue;
                if( !game.isRevealed( x1, y1 ) ) continue;
                int num = game.getNumber( x1, y1 );
                if( num == 0 ) {
                    setMark( x, y, EMark.RED );
                    setMark( x1, y1, EMark.YELLOW );
                    found = true;
                }
            }
        }
        return found;
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_flag_zero_conflict;
    }
}
