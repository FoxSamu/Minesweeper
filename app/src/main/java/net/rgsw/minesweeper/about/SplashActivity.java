package net.rgsw.minesweeper.about;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.settings.Configuration;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTheme( Configuration.useDarkTheme.getValue() ? R.style.AppTheme_Dark_NoActionBar : R.style.AppTheme_NoActionBar );
        setContentView( R.layout.activity_splash );

        Handler handler = new Handler();
        handler.postDelayed( this::finish, 2000 );
    }
}
