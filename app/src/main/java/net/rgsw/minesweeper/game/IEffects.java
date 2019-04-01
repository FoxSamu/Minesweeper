package net.rgsw.minesweeper.game;

public interface IEffects {
    void fxNumber( boolean dig );

    void fxExplode();

    void fxFlag();

    void fxSoftMark();

    void fxWin();

    void fxDig();
}
