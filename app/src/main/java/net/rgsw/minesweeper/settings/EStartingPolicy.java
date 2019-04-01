package net.rgsw.minesweeper.settings;

import net.rgsw.minesweeper.R;

public enum EStartingPolicy {
    NO_ENSURANCE( R.string.setting_start_policy_no_ensuring ),
    BASIC_ENSURANCE( R.string.setting_start_policy_basic ),
    FULL_ENSURANCE( R.string.setting_start_policy_advanced );

    public final int stringRes;

    EStartingPolicy( int stringRes ) {
        this.stringRes = stringRes;
    }

    public static class Values implements ISettingsEnum<EStartingPolicy> {

        @Override
        public int getDisplayNameRes( int index ) {
            return getValue( index ).stringRes;
        }

        @Override
        public EStartingPolicy getValue( int index ) {
            return EStartingPolicy.values()[ index ];
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public int getIndex( EStartingPolicy value ) {
            return value.ordinal();
        }
    }
}
