package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class RandomFlagHint extends CellIteratingHint {
    @Override
    public boolean findHintAt( MinesweeperGame game, int x, int y ) {
        if( game.isFlagged( x, y ) ) {
            if( game.amountOfUnrevealedAdjacentTiles( x, y ) == 8 ) {
                addWrongFlag( x, y );
                return true;
            }
        }
        return false;
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_random_flag;
    }
}
