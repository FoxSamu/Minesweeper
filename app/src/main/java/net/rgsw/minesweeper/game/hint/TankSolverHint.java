package net.rgsw.minesweeper.game.hint;

import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.game.Location;
import net.rgsw.minesweeper.game.MinesweeperGame;

import java.util.ArrayList;
import java.util.LinkedList;

public class TankSolverHint extends Hint {
    private final TankSolver solver;

    ArrayList<Location> borderTiles = new ArrayList<>();
    ArrayList<Location> allEmptyTiles = new ArrayList<>();
    private int width, height;
    private final int maxHints;

    private static final int BF_LIMIT = 8;

    public TankSolverHint( MinesweeperGame game, int maxHints ) {
        solver = new TankSolver( game );
        this.maxHints = maxHints;
    }

    @Override
    public boolean findHint( MinesweeperGame game ) {
        borderTiles = new ArrayList<>();
        allEmptyTiles = new ArrayList<>();

        width = game.width();
        height = game.height();

        int width = game.width();
        int height = game.height();
        boolean borderOptimization = false;
        for( int y = 0; y < height; y++ ) {
            for( int x = 0; x < width; x++ ) {
                if( game.getNumber( x, y ) == -1 && !game.isFlagged( x, y ) ) {
                    allEmptyTiles.add( new Location( x, y ) );
                }
            }
        }


        for( int y = 0; y < height; y++ ) {
            for( int x = 0; x < width; x++ ) {
                if( game.hasTileInformation( x, y ) && !game.isFlagged( x, y ) ) {
                    borderTiles.add( new Location( x, y ) );
                }
            }
        }

        int numOutSquares = allEmptyTiles.size() - borderTiles.size();
        if( numOutSquares > BF_LIMIT ) {
            borderOptimization = true;
        } else {
            borderTiles = allEmptyTiles;
        }


        if( borderTiles.size() == 0 ) { return false; }

        ArrayList<ArrayList<Location>> segregated;
        if( !borderOptimization ) {
            segregated = new ArrayList<>();
            segregated.add( borderTiles );
        } else { segregated = tankSegregate( borderTiles, game ); }

        int found = 0;
        int xtot = 0; // Add up all x and y positions to take average of that for computing the point the
        int ytot = 0; // scrollview needs to scroll to

        boolean first = false;
        int xfirst = 0;
        int yfirst = 0;

        Iterator:
        for( ArrayList<Location> list : segregated ) {
            solver.clearLocations();
            for( Location l : list ) solver.addLocation( l.x, l.y );
            solver.solve( borderOptimization );
            for( Location l : list ) {
                if( solver.isKnownMine( l.x, l.y ) && !game.isFlagged( l.x, l.y ) ) {
                    if( !first ) {
                        xfirst = l.x;
                        yfirst = l.y;
                        addInferredFlag( l.x, l.y );
                        found++;
                        first = true;
                    } else {
                        if( Math.abs( l.x - xfirst ) < 2 && Math.abs( l.y - yfirst ) < 2 ) {
                            addInferredFlag( l.x, l.y );
                            found++;
                        }
                    }
                }
                if( solver.isKnownEmpty( l.x, l.y ) && !game.isRevealed( l.x, l.y ) ) {
                    if( !first ) {
                        xfirst = l.x;
                        yfirst = l.y;
                        addInferredDig( l.x, l.y );
                        found++;
                        first = true;
                    } else {
                        if( Math.abs( l.x - xfirst ) < 2 && Math.abs( l.y - yfirst ) < 2 ) {
                            addInferredDig( l.x, l.y );
                            found++;
                        }
                    }
                }
                if( found > maxHints ) break Iterator; // Keep it a hint, not a solution
            }
        }

        if( found <= 0 ) return false; // Break here to prevent division by zero

        setViewLocation( xfirst, yfirst );

        return true;
    }

    ArrayList<ArrayList<Location>> tankSegregate( ArrayList<Location> borderTiles, MinesweeperGame game ) {

        ArrayList<ArrayList<Location>> allRegions = new ArrayList<>();
        ArrayList<Location> covered = new ArrayList<>();

        while( true ) {

            LinkedList<Location> queue = new LinkedList<>();
            ArrayList<Location> finishedRegion = new ArrayList<>();

            // Find a suitable starting point
            for( Location firstT : borderTiles ) {
                if( !covered.contains( firstT ) ) {
                    queue.add( firstT );
                    break;
                }
            }

            if( queue.isEmpty() ) { break; }

            while( !queue.isEmpty() ) {

                Location curTile = queue.poll();
                int ci = curTile.y;
                int cj = curTile.x;

                finishedRegion.add( curTile );
                covered.add( curTile );

                // Find all connecting tiles
                for( Location tile : borderTiles ) {
                    int ti = tile.y;
                    int tj = tile.x;

                    boolean isConnected = false;

                    if( finishedRegion.contains( tile ) ) { continue; }

                    if( Math.abs( ci - ti ) > 2 || Math.abs( cj - tj ) > 2 ) { isConnected = false; } else {
                        // Perform a search on all the tiles
                        tilesearch:
                        for( int y = 0; y < height; y++ ) {
                            for( int x = 0; x < width; x++ ) {
                                if( game.getNumber( x, y ) > 0 ) {
                                    if( Math.abs( ci - y ) <= 1 && Math.abs( cj - x ) <= 1 && Math.abs( ti - y ) <= 1 && Math.abs( tj - x ) <= 1 ) {
                                        isConnected = true;
                                        break tilesearch;
                                    }
                                }
                            }
                        }
                    }

                    if( !isConnected ) continue;

                    if( !queue.contains( tile ) ) { queue.add( tile ); }

                }
            }

            allRegions.add( finishedRegion );

        }

        return allRegions;

    }

    @Override
    public int getMessageResource() {
        return R.string.hint_algorithm;
    }
}
