package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.MinesweeperGame;

public class NotJetStartedHint extends Hint {
    @Override
    public boolean findHint( MinesweeperGame game ) {
        return !game.isInitialized();
    }

    @Override
    public boolean isDone( MinesweeperGame game ) {
        return game.isInitialized();
    }

    @Override
    public int getMessageResource() {
        return R.string.hint_not_started;
    }
}
