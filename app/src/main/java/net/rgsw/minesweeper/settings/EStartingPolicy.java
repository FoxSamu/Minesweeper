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
}
