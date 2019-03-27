package net.rgsw.minesweeper.util;

import org.jetbrains.annotations.Contract;

public final class Utils {
    private Utils() {
    }

    /**
     * Returns a replacement value if the passed source value is null. Do not pass a null value as replacement,
     * since that would make this call useless.
     * @param source      The source value, may be {@code null}
     * @param replacement The replacement value. This may be {@code null} too, thought that would make a call to this
     *                    useless (replaces a null value with a null value).
     * @return The source value, or the replacement value if the source value was null.
     */
    @Contract( value = "!null, _ -> param1; null, _ -> param2", pure = true )
    public static <T> T orDefault( T source, T replacement ) {
        if( source != null ) {
            return source;
        } else {
            return replacement;
        }
    }
}
