package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.EMark;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class TooManyFlagsHint extends CellIteratingHint {

    private int num, x, y;

    @Override
    public boolean findHintAt( MinesweeperGame game, int x, int y ) {
        if( !game.isRevealed( x, y ) ) return false;
        int num = game.getNumber( x, y );
        if( num > 0 && num < game.findAdjacentFlags( x, y ) ) {
            setMark( x, y, EMark.RED );
            this.num = num;
            this.x = x;
            this.y = y;
            return true;
        }
        return false;
    }

    @Override
    public boolean isDone( MinesweeperGame game ) {
        if( num < game.findAdjacentFlags( x, y ) ) return false;
        return super.isDone( game );
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_too_many_flags;
    }
}
