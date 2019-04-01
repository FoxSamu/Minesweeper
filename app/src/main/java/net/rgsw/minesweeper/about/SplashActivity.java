package net.rgsw.minesweeper.about;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.settings.Configuration;
import net.rgsw.minesweeper.util.SignatureUtil;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTheme( Configuration.useDarkTheme.getValue() ? R.style.AppTheme_Dark_NoActionBar : R.style.AppTheme_NoActionBar );
        setContentView( R.layout.activity_splash );

        Handler handler = new Handler();

        // Check for signature problems...
        int valid = SignatureUtil.checkAppSignature( this );
        if( valid == SignatureUtil.INVALID ) {
            boolean playstore = SignatureUtil.verifyInstaller( this );
            AlertDialog.Builder dialog = new AlertDialog.Builder( this );

            // Inform the user about possible malware
            dialog.setTitle( R.string.signature_msg_title );
            if( playstore ) {
                // We're still safe: this app is from the play store and possibly my fault
                dialog.setMessage( R.string.signature_msg_text );
            } else {
                // We're not safe: this app is installed manually, inform about possible malware
                dialog.setMessage( R.string.signature_msg_text_error );
            }

            dialog.setPositiveButton( R.string.ok, ( dialog1, which ) -> handler.postDelayed( this::finish, 2000 ) );

            dialog.show();
        } else if( valid == SignatureUtil.ERROR ) {
            Toast.makeText( this, R.string.signature_error, Toast.LENGTH_LONG ).show();
            handler.postDelayed( this::finish, 5000 );
        } else {
            handler.postDelayed( this::finish, 2000 );
        }
    }
}
