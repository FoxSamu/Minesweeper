package net.rgsw.minesweeper.game;

public class PatternDetector {

    public interface IPattern {
        boolean detect( Area area );

        int width();

        int height();
    }

    public static class Area {
        private int x;
        private int y;
        private int width;
        private int height;
        private Rotation rot = Rotation.UP;
    }

    public enum Rotation {
        UP, RIGHT, DOWN, LEFT;

    }
}
