package net.rgsw.minesweeper.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.settings.Configuration;

public class WinDialog extends AlertDialog.Builder {
    private final GameActivity activity;

    public WinDialog( GameActivity context ) {
        super( context );
        activity = context;
        init();
    }

    public WinDialog( GameActivity context, int themeResId ) {
        super( context, themeResId );
        activity = context;
        init();
    }

    private void init() {
        setTitle( R.string.dialog_win_title );
        setView( R.layout.dialog_win );
        setIcon( Configuration.useDarkTheme.getValue() ? R.drawable.ic_face_happy : R.drawable.ic_dialog_win );
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

    public AlertDialog show( int total, long timeMS, boolean highscore ) {
        AlertDialog dialog = super.show();

        TextView msg = dialog.findViewById( R.id.msg );
        msg.setText( getContext().getString( R.string.dialog_win_found, total ) );

        TextView time = dialog.findViewById( R.id.time );

        time.setText( getContext().getString( R.string.dialog_win_time_format,
                ( ( timeMS / ( 1000 * 60 * 60 ) ) % 24 ),
                ( ( timeMS / ( 1000 * 60 ) ) % 60 ),
                ( ( timeMS / ( 1000 ) ) % 60 ),
                ( timeMS % 1000 )
        ) );

        TextView newRecord = dialog.findViewById( R.id.highscore );
        newRecord.setVisibility( highscore ? View.VISIBLE : View.GONE );

        return dialog;
    }
}
