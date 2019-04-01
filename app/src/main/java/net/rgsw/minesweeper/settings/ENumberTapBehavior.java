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
}
