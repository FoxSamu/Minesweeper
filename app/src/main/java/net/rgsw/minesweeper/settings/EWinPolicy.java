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
}
