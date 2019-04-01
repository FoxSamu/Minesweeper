package net.rgsw.minesweeper.settings;

import net.rgsw.minesweeper.R;

public enum EWinPolicy {
    ALL_FLAG( R.string.setting_win_condition_all_flags ),
    ALL_REVEALED( R.string.setting_win_condition_all_revealed ),
    ALL_INFERRED( R.string.setting_win_condition_all_inferred ),
    FLAG_OR_REVEALED( R.string.setting_win_condition_flag_revealed );

    public final int stringRes;

    EWinPolicy( int stringRes ) {
        this.stringRes = stringRes;
    }

    public static class Values implements ISettingsEnum<EWinPolicy> {

        @Override
        public int getDisplayNameRes( int index ) {
            return getValue( index ).stringRes;
        }

        @Override
        public EWinPolicy getValue( int index ) {
            return EWinPolicy.values()[ index ];
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getIndex( EWinPolicy value ) {
            return value.ordinal();
        }
    }
}
