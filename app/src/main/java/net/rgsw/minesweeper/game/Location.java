package net.rgsw.minesweeper.game;

public class Location {
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

    @Override
    public int hashCode() {
        return x << 16 + y;
    }
}
