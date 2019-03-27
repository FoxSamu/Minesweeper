package net.rgsw.minesweeper.game;

/**
 * Used in games to determine the basic rendering of a cell (e.g. if it is revealed and which icon should be rendered).
 */
public enum ECellState {
    NOTHING( true, false, false, false, 0 ),
    FOUND_1( true, false, false, false, 1 ),
    FOUND_2( true, false, false, false, 2 ),
    FOUND_3( true, false, false, false, 3 ),
    FOUND_4( true, false, false, false, 4 ),
    FOUND_5( true, false, false, false, 5 ),
    FOUND_6( true, false, false, false, 6 ),
    FOUND_7( true, false, false, false, 7 ),
    FOUND_8( true, false, false, false, 8 ),
    MINE( true, true, false, false, -1 ),
    MINE_WIN( true, true, false, false, -1 ),
    NO_MINE( true, false, true, false, -1 ),
    FLAGGED_MINE( true, true, true, false, -1 ),
    UNREVEALED( false, false, false, false, -1 ),
    FLAGGED( false, false, true, false, -1 ),
    SOFT_MARKED( false, false, false, true, -1 );

    public final boolean revealed;
    public final boolean mine;
    public final boolean flagged;
    public final boolean softMarked;
    public final int found;

    ECellState( boolean revealed, boolean mine, boolean flagged, boolean softMarked, int found ) {
        this.revealed = revealed;
        this.mine = mine;
        this.flagged = flagged;
        this.softMarked = softMarked;
        this.found = found;
    }
}
