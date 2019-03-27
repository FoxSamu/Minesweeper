package net.rgsw.minesweeper.game;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import net.rgsw.ctable.tag.TagStringCompound;
import net.rgsw.minesweeper.main.Mode;
import net.rgsw.minesweeper.settings.Configuration;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MinesweeperGame implements IGame {

    private boolean ended;
    private boolean won;
    private boolean initialized;
    private boolean paused;
    private int width;
    private int height;
    private int amountMines;
    private int amountFlags;
    private Flag[] flags;
    private boolean[] mines;
    private boolean[] revealed;
    private int[] adjacent;
    private long startTime;
    private long time;
    private ICellInvalidator invalidator;
    private Integer tappedMineX;
    private Integer tappedMineY;

    private final Stack<Location> processingStack = new Stack<>();

    private Random rand;

    private final ArrayList<Location> possibleMineLocations = new ArrayList<>();

    private Mode mode;

    public MinesweeperGame( Mode mode ) {
        this.mode = mode;
        if( mode != null ) reset();
    }

    private void invalidate( int x, int y ) {
        if( invalidator != null ) invalidator.invalidateCell( x, y );
    }

    private void invalidateAll() {
        if( invalidator != null ) invalidator.invalidateAll();
    }

    private void invalidateArea( int x1, int y1, int x2, int y2 ) {
        if( invalidator != null ) invalidator.invalidateArea( x1, y1, x2, y2 );
    }

    /**
     * Resets the game for reusing. This invalidates all cells if an invalidator is specified.
     */
    public void reset() {
        mines = new boolean[ mode.getWidth() * mode.getHeight() ];
        revealed = new boolean[ mode.getWidth() * mode.getHeight() ];
        flags = new Flag[ mode.getWidth() * mode.getHeight() ];
        adjacent = new int[ mode.getWidth() * mode.getHeight() ];
        amountMines = mode.getMines();
        width = mode.getWidth();
        height = mode.getHeight();
        possibleMineLocations.clear();
        rand = new Random();
        ended = false;
        paused = false;
        won = false;
        initialized = false;
        amountFlags = 0;
        time = 0;
        startTime = 0;
        tappedMineX = null;
        tappedMineY = null;

        invalidateAll();
    }

    /**
     * Handle the first tap. This is automatically called in {@link #doInput} when the game was not yet initialized.
     * @param x X coordinate of the tapped cell
     * @param y Y coordinate of the tapped cell
     */
    public void startAt( int x, int y ) {
        if( paused ) return;
        // Compute no-mine radius... Try to retain a 3x3 square of no mines, but fallback on 1x1 if not enough empty
        // space for that.
        int range = 2;
        if( width * height - 1 - amountMines < 9 ) range = 1;


        // Find possible mine locations
        for( int x1 = 0; x1 < width; x1++ ) {
            for( int y1 = 0; y1 < height; y1++ ) {
                int xrange = x1 - x;
                int yrange = y1 - y;
                if( yrange > -range && yrange < range && xrange > -range && xrange < range ) {
                    // This location must not be a mine...
                    continue;
                }

                possibleMineLocations.add( new Location( x1, y1 ) );
            }
        }

        // Put mines in grid
        int allMines = 0;
        for( int i = 0; i < amountMines; i++ ) {
            if( possibleMineLocations.isEmpty() ) break;
            allMines++;

            int index = rand.nextInt( possibleMineLocations.size() );
            Location loc = possibleMineLocations.remove( index );

            index = index( loc );

            mines[ index ] = true;
        }
        amountMines = allMines;

        // Compute adjacent-mines numbers
        for( int x1 = 0; x1 < width; x1++ ) {
            for( int y1 = 0; y1 < height; y1++ ) {
                int i = index( x1, y1 );
                adjacent[ i ] = findAdjacentMines( x1, y1 );
            }
        }

        startTime = System.currentTimeMillis();

        // Push something processable to the stack
        processingStack.push( new Location( x, y ) );

        process();

        checkWin();
    }

    /**
     * Try to reveal cells on stack. When a revealed cell is empty (no number) it will push it adjacents to the stack,
     * making this method iteratively reveal an empty area around pushed cells.
     */
    private void process() {
        while( !processingStack.empty() ) {
            Location l = processingStack.pop();
            boolean revealed = tryReveal( l.x, l.y );

            // We've found an empty cell: push adjacent to stack for being processed too
            if( revealed && adjacent[ index( l ) ] == 0 ) {
                pushAdjacent( l.x, l.y );
            }
        }
    }

    public void pushAdjacent( int x, int y ) {
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                if( isFlagOrSoftMark( x1, y1 ) ) continue; // Skip marked tiles
                if( isRevealed( x1, y1 ) ) continue; // Skip revealed tiles
                processingStack.push( new Location( x1, y1 ) );
            }
        }
    }

    /**
     * Checks whether the cell at specified coordinates has a mine.
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return True when there was a mine at specified coords.
     */
    public boolean isMine( int x, int y ) {
        return mines[ index( x, y ) ];
    }

    /**
     * Checks whether the cell at specified coordinates is revealed.
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return True when the cell at specified coords is revealed.
     */
    public boolean isRevealed( int x, int y ) {
        return revealed[ index( x, y ) ];
    }

    /**
     * Checks whether a cell at specified coordinates has either a flag or a soft mark
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return True when the cell had a flag or a soft mark
     */
    public boolean isFlagOrSoftMark( int x, int y ) {
        if( isRevealed( x, y ) ) return false;
        return flags[ index( x, y ) ] != null;
    }

    /**
     * Checks whether a cell has a flag
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return True when the cell had a flag
     */
    public boolean isFlagged( int x, int y ) {
        if( isRevealed( x, y ) ) return false;
        return flags[ index( x, y ) ] == Flag.FLAG;
    }

    /**
     * Checks whether a cell has a soft mark (question mark)
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return True when the cell had a soft mark
     */
    public boolean isSoftMarked( int x, int y ) {
        if( isRevealed( x, y ) ) return false;
        return flags[ index( x, y ) ] == Flag.SOFT_MARK;
    }

    /**
     * Ends the game. This invalidates all cells if an invalidator is specified.
     * @param won If the game is won
     */
    public void end( boolean won ) {
        ended = true;
        this.won = won;
        this.time += System.currentTimeMillis() - this.startTime;
        invalidateAll();
    }

    /**
     * Pauses the game. Paused games could not be interacted with.
     * @see #resume()
     */
    public void pause() {
        time += System.currentTimeMillis() - startTime;
        paused = true;
    }

    /**
     * Resumes the game from paused state.
     * @see #pause()
     */
    public void resume() {
        paused = false;
        startTime = System.currentTimeMillis();
    }

    /**
     * Reveals the cell at specified coordinates, or ends the game without winning when a mine was revealed. When the
     * specified cell is flagged or soft marked, this does nothing.
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     */
    public void revealOrLose( int x, int y ) {
        if( isFlagOrSoftMark( x, y ) ) return;
        if( isMine( x, y ) ) {
            tappedMineX = x; // Save tapped mine to mark that
            tappedMineY = y;
            end( false );
        }
        boolean revealed = tryReveal( x, y );
        if( revealed && adjacent[ index( x, y ) ] == 0 ) {
            pushAdjacent( x, y );
        }
        process();
    }

    /**
     * Calls {@link #revealOrLose} on each adjacent cell to the specified coordinates
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void revealOrLoseAdjacent( int x, int y ) {
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                if( isRevealed( x1, y1 ) ) continue; // Skip revealed tiles
                revealOrLose( x1, y1 );
            }
        }
    }

    /**
     * Places a flag on the specified cell, or removes it when already flagged
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     */
    public void doFlag( int x, int y ) {
        int i = index( x, y );

        Flag f = flags[ i ];
        if( f == null || f == Flag.SOFT_MARK ) {
            if( amountFlags < amountMines ) {
                flags[ i ] = Flag.FLAG;
                amountFlags++;
            }
        } else {
            flags[ i ] = null;
            amountFlags--;
        }

        invalidate( x, y );
    }

    /**
     * Places a soft mark on the specified cell, or removes it when already soft marked. Does nothing when the cell was
     * flagged.
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     */
    public void doSoftMark( int x, int y ) {
        int i = index( x, y );

        Flag f = flags[ i ];
        if( f == Flag.FLAG ) return;
        if( f == null ) { flags[ i ] = Flag.SOFT_MARK; } else flags[ i ] = null;

        invalidate( x, y );
    }

    /**
     * Performs one user-input action.<br>
     * - When paused or when the game is done, this does nothing.<br>
     * - When coordinate out of bounds, this does nothing either.<br>
     * - When not initialized, this initializes the game by calling 'startAt'.<br>
     * - When the specified cell is a visible, and completed number, this will reveal all adjacent cells<br>
     * - When the specified cell is not revealed, it will either flag, soft-mark or dig it, depending on input mode.<br>
     * In other conditions, it does nothing.
     * @param x    X coordinate of the input
     * @param y    Y coordinate of the input
     * @param flag The flagging/input mode. When {@code null} it will dig.
     */
    public void doInput( int x, int y, Flag flag ) {
        if( paused || ended ) return;
        if( outOfRange( x, y ) ) return;

        if( !initialized ) {
            startAt( x, y );
            initialized = true;
            return;
        }
        int i = index( x, y );
        if( isRevealed( x, y ) ) {
            int adj = adjacent[ i ];
            if( adj == 0 ) return;
            int flags = findAdjacentFlags( x, y );
            if( flags >= adj ) {
                revealOrLoseAdjacent( x, y );
            }
        } else {
            if( flag == null ) {
                revealOrLose( x, y );
            } else if( flag == Flag.FLAG ) {
                doFlag( x, y );
            } else {
                doSoftMark( x, y );
            }
        }

        process();
        checkWin();
    }

    /**
     * Reveals the specified cell unless it has a mine
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return True when the cell was revealed
     */
    public boolean tryReveal( int x, int y ) {
        if( isMine( x, y ) ) return false;
        revealed[ index( x, y ) ] = true;
        if( Configuration.showInferredFlags.getValue() ) {
            // Update surrounding area to invalidate inferred flag updates
            invalidateArea( x - 1, y - 1, x + 1, y + 1 );
        } else {
            // No inferred flagging, save performance and invalidate only this cell
            invalidate( x, y );
        }
        return true;
    }

    /**
     * Location to array index
     */
    private int index( Location loc ) {
        return loc.x * height + loc.y;
    }

    /**
     * Location to array index
     */
    private int index( int x, int y ) {
        return x * height + y;
    }

    /**
     * Counts the amount of mines adjacent to the specified cell. This computes the number that would be shown in the
     * specified cell.
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return The amount of adjacent mines
     */
    private int findAdjacentMines( int x, int y ) {
        int mines = 0;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                int i = index( x1, y1 );
                if( this.mines[ i ] ) mines++;
            }
        }
        return mines;
    }

    /**
     * Counts the amount of flags adjacent to the specified cell. Used in handling number taps, to check if a number
     * could be tapped.
     * @param x X coordinate of the cell
     * @param y Y coordinate of the cell
     * @return The amount of adjacent flags
     */
    private int findAdjacentFlags( int x, int y ) {
        int flags = 0;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                if( isRevealed( x1, y1 ) ) continue; // Skip revealed tiles
                if( isFlagged( x1, y1 ) ) flags++;
            }
        }
        return flags;
    }

    /**
     * Checks whether the specified coordinate lies in the game board, i.e. if they point to a valid cell.
     * @param x X coordinate
     * @param y Y coordinate
     * @return True when the specified coords point to a valid cell.
     */
    private boolean outOfRange( int x, int y ) {
        return x >= width || y >= height || x < 0 || y < 0;
    }

    @NonNull
    @Override
    public ECellState getState( int x, int y ) {
        ECellState state = ECellState.UNREVEALED;
        if( isRevealed( x, y ) ) {
            state = ECellState.values()[ adjacent[ index( x, y ) ] ];
        } else {
            if( isFlagged( x, y ) ) { state = ECellState.FLAGGED; } else if( isFlagOrSoftMark( x, y ) ) {
                state = ECellState.SOFT_MARKED;
            }
        }
        if( ended ) {
            if( isMine( x, y ) ) {
                if( isFlagged( x, y ) ) { state = ECellState.FLAGGED_MINE; } else {
                    state = won ? ECellState.MINE_WIN : ECellState.MINE;
                }
            } else if( isFlagged( x, y ) ) state = ECellState.NO_MINE;
        }
        return state;
    }

    @Nullable
    @Override
    public EMark getBackgroundMark( int x, int y ) {
        if( Configuration.markTappedMine.getValue() && ended && !won && tappedMineX == x && tappedMineY == y ) {
            return EMark.RED;
        }
        return null;
    }

    @Override
    public boolean isInferredFlag( int x, int y ) {
        if( !Configuration.showInferredFlags.getValue() ) return false;
        return inferFlag( x, y );
    }

    @Override
    public boolean done() {
        return ended;
    }

    @Override
    public boolean won() {
        return won;
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
        return amountMines;
    }

    @Override
    public int totalFlags() {
        return amountFlags;
    }

    /**
     * Whether the game is paused or not.
     * @return True if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Computes the time that this game is running. This is always 0 when not initialized.
     * @return The time in milliseconds
     */
    public long getTimeMS() {
        if( !initialized ) return 0;
        if( ended || paused ) return time;
        return time + ( System.currentTimeMillis() - startTime );
    }

    /**
     * Converts flag array to a byte array.
     */
    private byte[] flagsToByteArr() {
        byte[] bytes = new byte[ flags.length ];
        for( int i = 0; i < flags.length; i++ ) {
            Flag f = flags[ i ];
            if( f == null ) bytes[ i ] = 0;
            if( f == Flag.FLAG ) bytes[ i ] = 1;
            if( f == Flag.SOFT_MARK ) bytes[ i ] = 2;
        }
        return bytes;
    }

    /**
     * Converts mines array to a byte array
     */
    private byte[] minesToByteArr() {
        byte[] bytes = new byte[ mines.length ];
        for( int i = 0; i < mines.length; i++ ) {
            boolean f = mines[ i ];
            if( f ) bytes[ i ] = 1;
        }
        return bytes;
    }

    /**
     * Converts revealed array to a byte array
     */
    private byte[] revealedToByteArr() {
        byte[] bytes = new byte[ revealed.length ];
        for( int i = 0; i < revealed.length; i++ ) {
            boolean f = revealed[ i ];
            if( f ) bytes[ i ] = 1;
        }
        return bytes;
    }

    /**
     * Converts number array to a byte array
     */
    private byte[] adjToByteArr() {
        byte[] bytes = new byte[ adjacent.length ];
        for( int i = 0; i < adjacent.length; i++ ) {
            bytes[ i ] = ( byte ) adjacent[ i ];
        }
        return bytes;
    }

    /**
     * Reads number array from byte array
     */
    private void adjFromByteArray( byte[] arr ) {
        int[] adjs = new int[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            adjs[ i ] = arr[ i ];
        }
        adjacent = adjs;
    }

    /**
     * Reads mines array from byte array
     */
    private void minesFromByteArray( byte[] arr ) {
        boolean[] mines = new boolean[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            mines[ i ] = arr[ i ] > 0;
        }
        this.mines = mines;
    }

    /**
     * Reads revealed array from byte array
     */
    private void revealedFromByteArray( byte[] arr ) {
        boolean[] revealed = new boolean[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            revealed[ i ] = arr[ i ] > 0;
        }
        this.revealed = revealed;
    }

    /**
     * Reads flag array from byte array
     */
    private void flagsFromByteArray( byte[] arr ) {
        Flag[] flags = new Flag[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            byte b = arr[ i ];
            flags[ i ] = b == 0 ? null : b == 1 ? Flag.FLAG : Flag.SOFT_MARK;
        }
        this.flags = flags;
    }

    /**
     * Computes the amount of unrevealed cells adjacent to the specified coordinates. Used to infer flags.
     * @param x X coordinate
     * @param y Y coordinate
     */
    private int amountOfUnrevealedAdjacentTiles( int x, int y ) {
        int amount = 0;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                if( !isRevealed( x1, y1 ) ) amount++;
            }
        }
        return amount;
    }

    /**
     * Tries to infer a flag to the specified coordinates, by checking surrounding numbers if they could be completed
     * by flagging their neighbors.
     * @param x X coordinate
     * @param y Y coordinate
     * @return True if a flag could be inferred, false otherwise
     */
    private boolean inferFlag( int x, int y ) {
        if( isRevealed( x, y ) ) return false;
        if( isFlagged( x, y ) ) return false;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                if( !isRevealed( x1, y1 ) ) continue; // Skip revealed tiles

                int adj = adjacent[ index( x1, y1 ) ];

                int unrev = amountOfUnrevealedAdjacentTiles( x1, y1 );

                if( unrev <= adj ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Ends the game when won according to the win policy setting.
     */
    public void checkWin() {
        boolean allComplete;

        allComplete = true;
        for( int i = 0; i < mines.length; i++ ) {
            if( !mines[ i ] && !revealed[ i ] ) {
                allComplete = false;
                break;
            }
        }
        if( allComplete ) {
            end( true ); // All not-mines are revealed
            return;
        }

        allComplete = true;
        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                int i = index( x, y );

                boolean flag = flags[ i ] == Flag.FLAG;
                if( mines[ i ] != flag ) {
                    allComplete = false;
                    break;
                }
            }
        }
        if( allComplete ) {
            end( true ); // All mines are flagged
        }
    }

    /**
     * Saves the game to a binary compound
     * @param cpd The string-value compound
     */
    public void save( TagStringCompound cpd ) {
        cpd.set( "paused", this.paused );
        cpd.set( "ended", this.ended );
        cpd.set( "won", this.won );
        cpd.set( "initialized", this.initialized );
        cpd.set( "width", this.width );
        cpd.set( "height", this.height );
        cpd.set( "amountMines", this.amountMines );
        cpd.set( "amountFlags", this.amountFlags );
        cpd.set( "flags", flagsToByteArr() );
        cpd.set( "mines", minesToByteArr() );
        cpd.set( "revealed", revealedToByteArr() );
        cpd.set( "adjacent", adjToByteArr() );
        this.time += System.currentTimeMillis() - this.startTime;
        this.startTime = System.currentTimeMillis();
        cpd.set( "time", time );

        TagStringCompound mode = new TagStringCompound();
        this.mode.save( mode );
        cpd.set( "mode", mode );
    }

    /**
     * Loads the game from a binary compound
     * @param cpd The string-value compound
     */
    public void load( TagStringCompound cpd ) {
        this.initialized = cpd.getBoolean( "initialized" );
        if( !this.initialized ) {
            TagStringCompound mode = cpd.getTagStringCompound( "mode" );
            this.mode = new Mode( mode );
            this.reset(); // Just use reset... The game isn't initialized jet...
            this.paused = cpd.getBoolean( "paused" ); // Restore paused state since that can be changed before initializing
            return;
        }
        this.paused = cpd.getBoolean( "paused" );
        this.ended = cpd.getBoolean( "ended" );
        this.won = cpd.getBoolean( "won" );
        this.width = cpd.getInteger( "width" );
        this.height = cpd.getInteger( "height" );
        this.amountMines = cpd.getInteger( "amountMines" );
        this.amountFlags = cpd.getInteger( "amountFlags" );
        flagsFromByteArray( cpd.getByteArray( "flags" ) );
        minesFromByteArray( cpd.getByteArray( "mines" ) );
        revealedFromByteArray( cpd.getByteArray( "revealed" ) );
        adjFromByteArray( cpd.getByteArray( "adjacent" ) );
        this.time = cpd.getLong( "time" );
        this.startTime = System.currentTimeMillis();

        TagStringCompound mode = cpd.getTagStringCompound( "mode" );
        this.mode = new Mode( mode );
    }

    /**
     * Returns the mode of this game, including the latest stats
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Whether the game is initialized in it's current state
     * @return True if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the amount of mines that have a flag placed correctly on it.
     */
    public int getFlaggedMines() {
        int found = 0;
        for( int i = 0; i < mines.length; i++ ) {
            boolean mine = mines[ i ];
            Flag flag = flags[ i ];
            if( mine && flag == Flag.FLAG ) {
                found++;
            }
        }
        return found;
    }

    /**
     * Returns the revealed cell ratio, which is {@code revealed / all}.
     */
    public double getRevealedRelative() {
        int rev = 0;
        for( int i = 0; i < revealed.length; i++ ) {
            if( revealed[ i ] ) {
                rev++;
            }
        }
        return ( double ) rev / revealed.length;
    }

    /**
     * Set an invalidator to listen to cell updates
     * @param invalidator An invalidator, or null
     */
    public void setInvalidator( ICellInvalidator invalidator ) {
        this.invalidator = invalidator;
    }

    public static class Location {
        public final int x;
        public final int y;

        public Location( int x, int y ) {
            this.x = x;
            this.y = y;
        }

        public boolean equals( Object o ) {
            if( o == this ) return true;
            if( !( o instanceof Location ) ) return false;
            Location l = ( Location ) o;
            return l.x == x && l.y == y;
        }
    }

    public enum Flag {
        FLAG, SOFT_MARK
    }
}
