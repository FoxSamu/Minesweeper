package net.rgsw.minesweeper.game;

/**
 * Implements a basic squared game chunk
 */
public class BasicGameChunk implements IGameChunk {
    private final int x, y;
    private final int size;

    /**
     * Constructs a game chunk
     * @param x    The chunk x
     * @param y    The chunk y
     * @param size The size of a chunk square
     */
    public BasicGameChunk( int x, int y, int size ) {
        this.x = x;
        this.y = y;
        this.size = size;
    }


    @Override
    public int getX() {
        return x * size;
    }

    @Override
    public int getY() {
        return y * size;
    }

    @Override
    public int getW() {
        return size;
    }

    @Override
    public int getH() {
        return size;
    }
}
