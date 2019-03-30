package net.rgsw.minesweeper.about;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import net.rgsw.minesweeper.BuildConfig;
import net.rgsw.minesweeper.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_about );

        if( !BuildConfig.DEBUG ) {
            // Hide the debug text, this is a stable release.
            findViewById( R.id.debug_text ).setVisibility( View.GONE );
        }
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
}
