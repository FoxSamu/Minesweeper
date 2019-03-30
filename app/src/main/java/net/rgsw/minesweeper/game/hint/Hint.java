package net.rgsw.minesweeper.game.hint;

import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import net.rgsw.minesweeper.game.EMark;
import net.rgsw.minesweeper.game.ICellInvalidator;
import net.rgsw.minesweeper.game.MinesweeperGame;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Hint instances are used by a {@link MinesweeperGame} instance to find and show specific hints. A hint is recycled
 * after usage, meaning that the same instance is used again later on. Every game has one instance of each hint that the
 * game will show. They are ordered on priority, so hints with lower index are searched first.
 */
public abstract class Hint {
    private final HashSet<MinesweeperGame.Location> inferredFlags = new HashSet<>();
    private final HashSet<MinesweeperGame.Location> inferredDigs = new HashSet<>();
    private final HashSet<MinesweeperGame.Location> wrongFlags = new HashSet<>();
    private final HashSet<MinesweeperGame.Location> usedNumbers = new HashSet<>();
    private final HashMap<MinesweeperGame.Location, EMark> marks = new HashMap<>();
    private MinesweeperGame.Location viewLocation;

    private boolean inUse = false;

    protected final void addInferredFlag( int x, int y ) {
        inferredFlags.add( new MinesweeperGame.Location( x, y ) );
    }

    protected final void addInferredDig( int x, int y ) {
        inferredDigs.add( new MinesweeperGame.Location( x, y ) );
    }

    protected final void addWrongFlag( int x, int y ) {
        wrongFlags.add( new MinesweeperGame.Location( x, y ) );
    }

    protected final void addUsedNumber( int x, int y ) {
        usedNumbers.add( new MinesweeperGame.Location( x, y ) );
    }

    protected final void setMark( int x, int y, EMark mark ) {
        marks.put( new MinesweeperGame.Location( x, y ), mark );
    }

    public final boolean isInferredFlag( int x, int y ) {
        return inferredFlags.contains( new MinesweeperGame.Location( x, y ) );
    }

    public final boolean isInferredDig( int x, int y ) {
        return inferredDigs.contains( new MinesweeperGame.Location( x, y ) );
    }

    public final boolean isWrongFlag( int x, int y ) {
        return wrongFlags.contains( new MinesweeperGame.Location( x, y ) );
    }

    public final boolean isUsedCell( int x, int y ) {
        return usedNumbers.contains( new MinesweeperGame.Location( x, y ) );
    }

    public final EMark getMark( int x, int y ) {
        return marks.get( new MinesweeperGame.Location( x, y ) );
    }

    public final void setViewLocation( int x, int y ) {
        this.viewLocation = new MinesweeperGame.Location( x, y );
    }

    public final void removeViewLocation() {
        this.viewLocation = null;
    }

    public final MinesweeperGame.Location getViewLocation() {
        return viewLocation;
    }

    public final void use() {
        inUse = true;
    }

    public final boolean isInUse() {
        return inUse;
    }

    public final void invalidate( ICellInvalidator invalidator ) {
        for( MinesweeperGame.Location l : inferredFlags ) invalidator.invalidateCell( l.x, l.y );
        for( MinesweeperGame.Location l : inferredDigs ) invalidator.invalidateCell( l.x, l.y );
        for( MinesweeperGame.Location l : wrongFlags ) invalidator.invalidateCell( l.x, l.y );
        for( MinesweeperGame.Location l : usedNumbers ) invalidator.invalidateCell( l.x, l.y );
        for( MinesweeperGame.Location l : marks.keySet() ) invalidator.invalidateCell( l.x, l.y );
    }

    @CallSuper
    public void reset() {
        wrongFlags.clear();
        usedNumbers.clear();
        inferredDigs.clear();
        inferredFlags.clear();
        marks.clear();
        viewLocation = null;
        inUse = false;
    }

    public abstract boolean findHint( MinesweeperGame game );

    @StringRes
    public abstract int getMessageResource();

    @Nullable
    @DrawableRes
    public Integer getMessageIcon() {
        return null;
    }
}
