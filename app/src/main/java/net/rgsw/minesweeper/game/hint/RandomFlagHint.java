package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.game.MinesweeperGame;

public class RandomFlagHint extends CellIteratingHint {
    @Override
    public boolean findHintAt( MinesweeperGame game, int x, int y ) {
        return false;
    }

    @Override
    public int getMessageResource() {
        return 0;
    }
}
