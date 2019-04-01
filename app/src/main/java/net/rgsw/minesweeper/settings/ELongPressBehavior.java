package net.rgsw.minesweeper.settings;

import net.rgsw.minesweeper.R;

public enum ELongPressBehavior {
    OFF( R.string.setting_long_press_off ),
    FLAG_DIG( R.string.setting_long_press_inverse ),
    SOFTMARK_ONLY( R.string.setting_long_press_all_soft_mark ),
    FLAG_SOFTMARK( R.string.setting_long_press_soft_mark );

    public final int stringRes;

    ELongPressBehavior( int stringRes ) {
        this.stringRes = stringRes;
    }

    public static class Values implements ISettingsEnum<ELongPressBehavior> {

        @Override
        public int getDisplayNameRes( int index ) {
            return getValue( index ).stringRes;
        }

        @Override
        public ELongPressBehavior getValue( int index ) {
            return ELongPressBehavior.values()[ index ];
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getIndex( ELongPressBehavior value ) {
            return value.ordinal();
        }
    }
}
