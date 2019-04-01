package net.rgsw.minesweeper.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.settings.Configuration;

public class AprilFoolsDialog extends AlertDialog.Builder {
    private final GameActivity activity;

    public AprilFoolsDialog( GameActivity context ) {
        super( context );
        activity = context;
        init();
    }

    public AprilFoolsDialog( GameActivity context, int themeResId ) {
        super( context, themeResId );
        activity = context;
        init();
    }

    private void init() {
        setTitle( "1 April!" );
        setView( R.layout.dialog_aprilfools );
        setIcon( Configuration.useDarkTheme.getValue() ? R.drawable.ic_face_dead : R.drawable.ic_dialog_lose );
        setNeutralButton( R.string.dialog_end_main_menu, this::menu );
        setPositiveButton( R.string.dialog_end_dismiss, null );
        setNegativeButton( R.string.dialog_end_new_game, this::newGame );
    }

    private void menu( DialogInterface dialog, int which ) {
        activity.doneAndFinish();
    }

    private void newGame( DialogInterface dialog, int which ) {
        activity.newGame();
    }

    public AlertDialog show( int found, int total, double revealedPercent ) {
        return super.show();
    }
}
