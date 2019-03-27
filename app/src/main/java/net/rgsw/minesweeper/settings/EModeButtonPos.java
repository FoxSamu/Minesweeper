package net.rgsw.minesweeper.settings;

import net.rgsw.minesweeper.R;

public enum EModeButtonPos {
    LEFT( R.string.setting_mode_button_pos_left ),
    MIDDLE( R.string.setting_mode_button_pos_center ),
    RIGHT( R.string.setting_mode_button_pos_right );

    private final int nameRes;

    EModeButtonPos( int nameRes ) {
        this.nameRes = nameRes;
    }

    public static class Values implements ISettingsEnum<EModeButtonPos> {

        @Override
        public int getDisplayNameRes( int index ) {
            return getValue( index ).nameRes;
        }

        @Override
        public EModeButtonPos getValue( int index ) {
            return EModeButtonPos.values()[ index ];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public int getIndex( EModeButtonPos value ) {
            return value.ordinal();
        }
    }
}
