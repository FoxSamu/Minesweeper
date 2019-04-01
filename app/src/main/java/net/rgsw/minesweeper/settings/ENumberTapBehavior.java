package net.rgsw.minesweeper.settings;

import net.rgsw.minesweeper.R;

public enum ENumberTapBehavior {
    NOTHING( R.string.setting_chording_off ),
    PLACE_FLAGS( R.string.setting_chording_flags ),
    REVEAL( R.string.setting_chording_reveal ),
    BOTH( R.string.setting_chording_both );

    public final int stringRes;

    ENumberTapBehavior( int stringRes ) {
        this.stringRes = stringRes;
    }

    public static class Values implements ISettingsEnum<ENumberTapBehavior> {

        @Override
        public int getDisplayNameRes( int index ) {
            return getValue( index ).stringRes;
        }

        @Override
        public ENumberTapBehavior getValue( int index ) {
            return ENumberTapBehavior.values()[ index ];
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getIndex( ENumberTapBehavior value ) {
            return value.ordinal();
        }
    }
}
