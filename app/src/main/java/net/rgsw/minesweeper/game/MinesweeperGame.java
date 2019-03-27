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

    /**
     * Resets the game for reusing
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

    public void startAt( int x, int y, Flag flag ) {
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

        processingStack.push( new Location( x, y ) );

        process();

        checkWin();
    }

    private void process() {
        while( !processingStack.empty() ) {
            Location l = processingStack.pop();
            boolean revealed = tryReveal( l.x, l.y );
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
                if( isFlagOrSM( x1, y1 ) ) continue; // Skip marked tiles
                if( isRevealed( x1, y1 ) ) continue; // Skip revealed tiles
                processingStack.push( new Location( x1, y1 ) );
            }
        }
    }

    public boolean isMine( int x, int y ) {
        return mines[ index( x, y ) ];
    }

    public boolean isRevealed( int x, int y ) {
        return revealed[ index( x, y ) ];
    }

    public boolean isFlagOrSM( int x, int y ) {
        return flags[ index( x, y ) ] != null;
    }

    public boolean isFlagged( int x, int y ) {
        return flags[ index( x, y ) ] == Flag.FLAG;
    }

    public void end( boolean won ) {
        ended = true;
        this.won = won;
        this.time += System.currentTimeMillis() - this.startTime;
        invalidateAll();
    }

    public void pause() {
        this.time += System.currentTimeMillis() - this.startTime;
        paused = true;
    }

    public void resume() {
        paused = false;
        this.startTime = System.currentTimeMillis();
    }


    public void revealOrLose( int x, int y ) {
        if( isFlagOrSM( x, y ) ) return;
        if( isMine( x, y ) ) {
            tappedMineX = x;
            tappedMineY = y;
            end( false );
        }
        boolean revealed = tryReveal( x, y );
        if( revealed && adjacent[ index( x, y ) ] == 0 ) {
            pushAdjacent( x, y );
        }
        process();
    }

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

    public void flag( int x, int y ) {
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

    public void softMark( int x, int y ) {
        int i = index( x, y );

        Flag f = flags[ i ];
        if( f == Flag.FLAG ) return;
        if( f == null ) { flags[ i ] = Flag.SOFT_MARK; } else flags[ i ] = null;

        invalidate( x, y );
    }

    public void click( int x, int y, Flag flag ) {
        if( paused || ended ) return;
        if( !initialized ) {
            startAt( x, y, flag );
            initialized = true;
            return;
        }
        if( outOfRange( x, y ) ) return;
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
                flag( x, y );
            } else {
                softMark( x, y );
            }
        }

        process();
        checkWin();
    }

    public boolean tryReveal( int x, int y ) {
        if( isMine( x, y ) ) return false;
        revealed[ index( x, y ) ] = true;
        invalidate( x, y );
        return true;
    }

    private int index( Location loc ) {
        return loc.x * height + loc.y;
    }

    private int index( int x, int y ) {
        return x * height + y;
    }

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

    private int findAdjacentFlags( int x, int y ) {
        int flags = 0;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue; // Skip center tile
                if( outOfRange( x1, y1 ) ) continue; // Skip out-of-range tiles
                if( isRevealed( x1, y1 ) ) continue; // Skip revealed tiles
                int i = index( x1, y1 );
                if( this.flags[ i ] == Flag.FLAG ) flags++;
            }
        }
        return flags;
    }

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
            if( isFlagged( x, y ) ) { state = ECellState.FLAGGED; } else if( isFlagOrSM( x, y ) ) {
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

    public boolean isPaused() {
        return paused;
    }

    public long getTimeMS() {
        if( !initialized ) return 0;
        if( ended || paused ) return time;
        return time + ( System.currentTimeMillis() - startTime );
    }

    public enum Flag {
        FLAG, SOFT_MARK
    }

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

    private byte[] minesToByteArr() {
        byte[] bytes = new byte[ mines.length ];
        for( int i = 0; i < mines.length; i++ ) {
            boolean f = mines[ i ];
            if( f ) bytes[ i ] = 1;
        }
        return bytes;
    }

    private byte[] revealedToByteArr() {
        byte[] bytes = new byte[ revealed.length ];
        for( int i = 0; i < revealed.length; i++ ) {
            boolean f = revealed[ i ];
            if( f ) bytes[ i ] = 1;
        }
        return bytes;
    }

    private byte[] adjToByteArr() {
        byte[] bytes = new byte[ adjacent.length ];
        for( int i = 0; i < adjacent.length; i++ ) {
            bytes[ i ] = ( byte ) adjacent[ i ];
        }
        return bytes;
    }

    private void adjFromByteArray( byte[] arr ) {
        int[] adjs = new int[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            adjs[ i ] = arr[ i ];
        }
        adjacent = adjs;
    }

    private void minesFromByteArray( byte[] arr ) {
        boolean[] mines = new boolean[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            mines[ i ] = arr[ i ] > 0;
        }
        this.mines = mines;
    }

    private void revealedFromByteArray( byte[] arr ) {
        boolean[] revealed = new boolean[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            revealed[ i ] = arr[ i ] > 0;
        }
        this.revealed = revealed;
    }

    private void flagsFromByteArray( byte[] arr ) {
        Flag[] flags = new Flag[ arr.length ];
        for( int i = 0; i < arr.length; i++ ) {
            byte b = arr[ i ];
            flags[ i ] = b == 0 ? null : b == 1 ? Flag.FLAG : Flag.SOFT_MARK;
        }
        this.flags = flags;
    }

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

    public Mode getMode() {
        return mode;
    }

    public boolean isInitialized() {
        return initialized;
    }

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

    public double getRevealedRelative() {
        int rev = 0;
        for( int i = 0; i < revealed.length; i++ ) {
            if( revealed[ i ] ) {
                rev++;
            }
        }
        return ( double ) rev / revealed.length;
    }

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
}
