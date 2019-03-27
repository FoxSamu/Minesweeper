package net.rgsw.minesweeper.tutorial;

import android.content.Context;
import android.util.AttributeSet;
import net.rgsw.minesweeper.game.ECellState;
import net.rgsw.minesweeper.game.IGame;
import net.rgsw.minesweeper.game.MinesweeperCanvasLegacy;

public class TutorialCanvasLegacy extends MinesweeperCanvasLegacy {

    private final IGame game = new TutorialGame( 5, 5, new ECellState[ 5 * 5 ] );

    public TutorialCanvasLegacy( Context context ) {
        super( context );
    }

    public TutorialCanvasLegacy( Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    public TutorialCanvasLegacy( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }

    public TutorialCanvasLegacy( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
    }

    @Override
    public IGame getGame() {
        return game;
    }
}
