package net.rgsw.minesweeper.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.settings.Configuration;

public class LoseDialog extends AlertDialog.Builder {
    private final GameActivity activity;

    public LoseDialog( GameActivity context ) {
        super( context );
        activity = context;
        init();
    }

    public LoseDialog( GameActivity context, int themeResId ) {
        super( context, themeResId );
        activity = context;
        init();
    }

    private void init() {
        setTitle( R.string.dialog_lose_title );
        setView( R.layout.dialog_lose );
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
        AlertDialog dialog = super.show();

        TextView msg = dialog.findViewById( R.id.msg );
        msg.setText( getContext().getString( R.string.dialog_lose_found, found, total ) );

        TextView revealed = dialog.findViewById( R.id.revealed );
        revealed.setText( getContext().getString( R.string.dialog_lose_percentage, ( int ) ( revealedPercent * 100 ) ) );

        ProgressBar bar = dialog.findViewById( R.id.revealed_bar );
        bar.setMax( 1000 );

        if( Build.VERSION.SDK_INT > 24 ) {
            bar.setProgress( ( int ) ( revealedPercent * 1000 ), true );
        } else {
            bar.setProgress( ( int ) ( revealedPercent * 1000 ) );
        }

        return dialog;
    }
}
