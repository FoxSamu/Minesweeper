package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class EndedGameHint extends Hint {
    private boolean lost;

    @Override
    public boolean findHint( MinesweeperGame game ) {
        if( game.done() ) {
            lost = !game.won();
            return true;
        }
        return false;
    }

    @Override
    public int getMessageResource() {
        return lost ? R.string.hint_lost : R.string.hint_won;
    }
}
