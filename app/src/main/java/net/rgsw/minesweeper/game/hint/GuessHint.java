package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class GuessHint extends Hint {

    @Override
    public boolean findHint( MinesweeperGame game ) {
        return true;
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_guess;
    }
}
