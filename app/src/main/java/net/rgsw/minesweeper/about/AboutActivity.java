package net.rgsw.minesweeper.about;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import net.rgsw.minesweeper.BuildConfig;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.util.SignatureUtil;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.about_view );

        Toolbar t = findViewById( R.id.toolbar );
        setSupportActionBar( t );
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );
        getSupportActionBar().setTitle( R.string.about_title );

        t.setNavigationOnClickListener( v -> finish() );

        if( !BuildConfig.DEBUG ) {
            // Hide the debug text, this is a stable release.
            findViewById( R.id.debug_text ).setVisibility( View.GONE );
        }

        if( SignatureUtil.checkAppSignature( this ) == 0 ) {
            // Hide the security leak text, this is app has a valid signature.
            findViewById( R.id.signed_text ).setVisibility( View.GONE );
        } else {
            if( SignatureUtil.verifyInstaller( this ) ) {
                ( ( TextView ) findViewById( R.id.signed_text ) ).setText( R.string.about_signature_violation_play_store );
            }
        }

        ( ( TextView ) findViewById( R.id.version_text ) ).setText( getString( R.string.about_version, BuildConfig.VERSION_NAME ) );
    }

    public void openLicense( View v ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( R.string.menu_license );
        builder.setPositiveButton( R.string.ok, null );
        builder.setView( R.layout.license );
        builder.show();
    }

    public void rateApp( View v ) {

    }

    public void reportBug( View v ) {

    }

    public void stackoverflow( View v ) {

    }

    public void github( View v ) {

    }

    public void discord( View v ) {

    }

    public void youtube( View v ) {

    }

    public void openOnGithub( View v ) {

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch( item.getItemId() ) {
            case android.R.id.home: // This is the back button on the left
                finish();
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }
}
