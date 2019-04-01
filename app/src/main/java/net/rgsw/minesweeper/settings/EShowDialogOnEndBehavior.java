package net.rgsw.minesweeper.settings;

import net.rgsw.minesweeper.R;

public enum EShowDialogOnEndBehavior {
    NEVER( R.string.setting_end_dialog_never ),
    ON_WIN( R.string.setting_end_dialog_win ),
    ON_LOSE( R.string.setting_end_dialog_lose ),
    ALWAYS( R.string.setting_end_dialog_always );

    public final int stringRes;

    EShowDialogOnEndBehavior( int stringRes ) {
        this.stringRes = stringRes;
    }

    public static class Values implements ISettingsEnum<EShowDialogOnEndBehavior> {

        @Override
        public int getDisplayNameRes( int index ) {
            return getValue( index ).stringRes;
        }

        @Override
        public EShowDialogOnEndBehavior getValue( int index ) {
            return EShowDialogOnEndBehavior.values()[ index ];
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public int getIndex( EShowDialogOnEndBehavior value ) {
            return value.ordinal();
        }
    }
}
