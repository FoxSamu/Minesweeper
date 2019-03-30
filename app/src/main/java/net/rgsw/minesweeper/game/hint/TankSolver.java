package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.game.Location;
import net.rgsw.minesweeper.game.MinesweeperGame;

import java.util.ArrayList;

/**
 * Tank solver algorithm implementation as described
 * <a href="https://luckytoilet.wordpress.com/2012/12/23/2125/">here</a>. Guess algorithm not included
 */
public class TankSolver {
    private final MinesweeperGame game;
    private final ArrayList<Location> locs = new ArrayList<>();

    private int width;
    private int height;
    private boolean[] knownMines;
    private boolean[] knownEmpty;
    private final ArrayList<boolean[]> solutions = new ArrayList<>();
    private Boolean[] known;

    public TankSolver( MinesweeperGame game ) {
        this.game = game;
    }

    public void addLocation( int x, int y ) {
        if( game.outOfRange( x, y ) ) return;
        Location loc = new Location( x, y );
        if( !locs.contains( loc ) ) { locs.add( loc ); }
    }

    public void addAll( int x, int y, int w, int h ) {
        for( int x1 = x; x1 < x + w; x1++ ) {
            for( int y1 = y; y1 < y + h; y1++ ) {
                addLocation( x1, y1 );
            }
        }
    }

    public void removeLocation( int x, int y ) {
        if( game.outOfRange( x, y ) ) return;
        locs.remove( new Location( x, y ) );
    }

    public void removeAll( int x, int y, int w, int h ) {
        for( int x1 = x; x1 < x + w; x1++ ) {
            for( int y1 = y; y1 < y + h; y1++ ) {
                removeLocation( x1, y1 );
            }
        }
    }

    public boolean isKnownMine( int x, int y ) {
        Boolean b = known[ x * height + y ];
        return b != null && b;
    }

    public boolean isKnownEmpty( int x, int y ) {
        Boolean b = known[ x * height + y ];
        return b != null && !b;
    }

    public void clearLocations() {
        locs.clear();
    }

    public void solve( boolean opt ) {
        width = game.width();
        height = game.height();
        int size = width * height;

        knownMines = new boolean[ size ];
        knownEmpty = new boolean[ size ];

        solutions.clear();

        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                knownEmpty[ x * height + y ] = game.isRevealed( x, y );
                knownMines[ x * height + y ] = game.isFlagged( x, y );
            }
        }
        recurse( 0, opt );


        known = new Boolean[ size ];

        if( !solutions.isEmpty() ) {
            for( int i = 0; i < locs.size(); i++ ) {
                Location loc = locs.get( i );

                boolean mine = true;
                boolean empty = true;

                for( boolean[] b : solutions ) {
                    boolean bool = b[ i ];

                    if( bool ) empty = false;
                    if( !bool ) mine = false;
                }

                known[ loc.x * height + loc.y ] = null;
                if( mine ) known[ loc.x * height + loc.y ] = true;
                if( empty ) known[ loc.x * height + loc.y ] = false;
            }
        }
    }

    private int knownMinesAround( int x, int y ) {
        int mines = 0;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue;
                if( game.outOfRange( x1, y1 ) ) continue;
                if( knownMines[ x1 * height + y1 ] ) mines++;
            }
        }

        return mines;
    }

    private int knownEmptyAround( int x, int y ) {
        int empty = 0;
        for( int x1 = x - 1; x1 <= x + 1; x1++ ) {
            for( int y1 = y - 1; y1 <= y + 1; y1++ ) {
                if( x1 == x && y1 == y ) continue;
                if( game.outOfRange( x1, y1 ) ) continue;
                if( knownEmpty[ x1 * height + y1 ] ) empty++;
            }
        }

        return empty;
    }

    private void recurse( int k, boolean optimize ) {
        int flagCount = 0;
        for( int x = 0; x < width; x++ ) {
            for( int y = 0; y < height; y++ ) {
                int i = x * height + y;
                if( knownMines[ i ] ) flagCount++;

                if( !game.isRevealed( x, y ) ) continue;

                int num = game.getNumber( x, y );
                if( num < 0 ) continue;

                int surround;
                if( ( x == 0 && y == 0 ) || ( y == height - 1 && x == width - 1 ) || ( y == 0 && x == width - 1 ) || ( y == height - 1 && x == 0 ) ) {
                    surround = 3;
                } else if( x == 0 || y == 0 || y == height - 1 || x == width - 1 ) {
                    surround = 5;
                } else {
                    surround = 8;
                }

                int numFlags = knownMinesAround( x, y );
                int numFree = knownEmptyAround( x, y );

                // Scenario 1: too many mines
                if( numFlags > num ) return;

                // Scenario 2: too few mines
                if( surround - numFree < num ) return;
            }
        }

        // We have too many flags
        if( flagCount > game.mines() ) { return; }

        // Solution found!
        if( k == locs.size() ) {

            if( !optimize && flagCount < game.mines() ) return;

            boolean[] solution = new boolean[ locs.size() ];
            for( int i = 0; i < locs.size(); i++ ) {
                Location s = locs.get( i );
                int sx = s.x;
                int sy = s.y;
                solution[ i ] = knownMines[ sx * height + sy ];
            }
            solutions.add( solution );
            return;
        }



        Location q = locs.get( k );

        // Recurse two positions: mine and no mine
        knownMines[ q.x * height + q.y ] = true;
        recurse( k + 1, optimize );
        knownMines[ q.x * height + q.y ] = false;

        knownEmpty[ q.x * height + q.y ] = true;
        recurse( k + 1, optimize );
        knownEmpty[ q.x * height + q.y ] = false;
    }
}
