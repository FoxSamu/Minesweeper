package net.rgsw.minesweeper.tutorial;

import net.rgsw.minesweeper.game.ECellState;
import net.rgsw.minesweeper.game.IGame;

public class TutorialGame implements IGame {

    private final int width;
    private final int height;
    private ECellState[] states;

    public TutorialGame( int width, int height, ECellState... states ) {
        this.width = width;
        this.height = height;
        this.states = states;
    }

    @Override
    public ECellState getState( int x, int y ) {
        ECellState state = states[ y * width + x ];
        return state == null ? ECellState.UNREVEALED : state;
    }

    public void setState( int x, int y, ECellState state ) {
        states[ y * width + x ] = state;
    }

    public void setStates( ECellState... states ) {
        this.states = states;
    }

    @Override
    public boolean done() {
        return false;
    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int mines() {
        return 0;
    }

    @Override
    public int totalFlags() {
        return 0;
    }
}
