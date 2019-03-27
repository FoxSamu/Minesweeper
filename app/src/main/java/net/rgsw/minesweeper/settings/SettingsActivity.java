package net.rgsw.minesweeper.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import net.rgsw.minesweeper.R;

public class SettingsActivity extends AppCompatActivity {

    private SettingsAdapter settings;
    private SharedPreferences prefs;

    private Configuration.ISettingChangeListener<Boolean> darkThemeChangeListener = setting -> restartApp();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        Configuration.useDarkTheme.addChangeListener( darkThemeChangeListener );
        initialize();
    }

    public void initialize() {
        setTheme( Configuration.useDarkTheme.getValue() ? R.style.AppTheme_Dark : R.style.AppTheme );
        setContentView( R.layout.activity_settings );

        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setDisplayShowHomeEnabled( true );
            actionBar.setHomeButtonEnabled( true );
        }

        ListView listView = findViewById( R.id.settings );

        settings = new SettingsAdapter(
                SettingsEntry.header( this, "", R.string.settings_header_appearance ),
                SettingsEntry.toggle( this, "", R.string.setting_dark_theme, R.string.setting_dark_theme_desc, Configuration.useDarkTheme.getValue(), ( entry ) -> Configuration.useDarkTheme.setValue( entry.getValue() ) ),
                SettingsEntry.slider( this, "", R.string.setting_cell_size, R.string.setting_cell_size_desc, R.string.simple_decimal_format, 30, 80, Configuration.gameCellSize.getValue(), ( entry ) -> Configuration.gameCellSize.setValue( entry.getValue() ) ),
                SettingsEntry.slider( this, "", R.string.setting_icon_size, R.string.setting_icon_size_desc, R.string.simple_decimal_format, 20, 80, Configuration.gameIconSize.getValue(), ( entry ) -> Configuration.gameIconSize.setValue( entry.getValue() ) ),
                SettingsEntry.slider( this, "", R.string.setting_corner_radius, R.string.setting_corner_radius_desc, R.string.simple_decimal_format, 0, 10, Configuration.gameCornerRadius.getValue(), ( entry ) -> Configuration.gameCornerRadius.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_checkerboard, R.string.setting_checkerboard_desc, Configuration.checkerboardGrid.getValue(), ( entry ) -> Configuration.checkerboardGrid.setValue( entry.getValue() ) ),
                SettingsEntry.header( this, "", R.string.settings_header_behavior ),
                SettingsEntry.spinner( this, "", R.string.setting_mode_button_position, R.string.setting_mode_button_position_desc, Configuration.modeButtonPos.getValue(), ( entry ) -> Configuration.modeButtonPos.setValue( entry.getValue() ), new EModeButtonPos.Values() ),
                SettingsEntry.toggle( this, "", R.string.setting_mark_tapped_mine, R.string.setting_mark_tapped_mine_desc, Configuration.markTappedMine.getValue(), ( entry ) -> Configuration.markTappedMine.setValue( entry.getValue() ) ),
                SettingsEntry.header( this, "", R.string.settings_header_hints ),
                SettingsEntry.toggle( this, "", R.string.setting_inferred_flags, R.string.setting_inferred_flags_desc, Configuration.showInferredFlags.getValue(), ( entry ) -> Configuration.showInferredFlags.setValue( entry.getValue() ) ),
                SettingsEntry.header( this, "", R.string.settings_header_advanced ),
                SettingsEntry.slider( this, "", R.string.setting_chunk_size, R.string.setting_chunk_size_desc, R.string.simple_decimal_format, 1, 30, Configuration.gameChunkSize.getValue(), ( entry ) -> Configuration.gameChunkSize.setValue( entry.getValue() ) )
        );

        listView.setAdapter( settings );
    }

    public void restartApp() {
        System.exit( 0 );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Configuration.useDarkTheme.removeChangeListener( darkThemeChangeListener );
    }
}
