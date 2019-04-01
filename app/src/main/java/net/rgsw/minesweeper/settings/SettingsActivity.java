package net.rgsw.minesweeper.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.about.AboutActivity;

public class SettingsActivity extends AppCompatActivity {

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

        SettingsAdapter settings = new SettingsAdapter(
                // APPEARANCE
                SettingsEntry.header( this, "", R.string.settings_header_appearance ),
                SettingsEntry.toggle( this, "", R.string.setting_dark_theme, R.string.setting_dark_theme_desc, Configuration.useDarkTheme.getValue(), ( entry ) -> Configuration.useDarkTheme.setValue( entry.getValue() ) ),
                SettingsEntry.slider( this, "", R.string.setting_cell_size, R.string.setting_cell_size_desc, R.string.simple_decimal_format, 30, 80, Configuration.gameCellSize.getValue(), ( entry ) -> Configuration.gameCellSize.setValue( entry.getValue() ) ),
                SettingsEntry.slider( this, "", R.string.setting_icon_size, R.string.setting_icon_size_desc, R.string.simple_decimal_format, 20, 80, Configuration.gameIconSize.getValue(), ( entry ) -> Configuration.gameIconSize.setValue( entry.getValue() ) ),
                SettingsEntry.slider( this, "", R.string.setting_corner_radius, R.string.setting_corner_radius_desc, R.string.simple_decimal_format, 0, 10, Configuration.gameCornerRadius.getValue(), ( entry ) -> Configuration.gameCornerRadius.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_checkerboard, R.string.setting_checkerboard_desc, Configuration.checkerboardGrid.getValue(), ( entry ) -> Configuration.checkerboardGrid.setValue( entry.getValue() ) ),

                // BEHAVIOR
                SettingsEntry.header( this, "", R.string.settings_header_behavior ),
                SettingsEntry.spinner( this, "", R.string.setting_mode_button_position, R.string.setting_mode_button_position_desc, Configuration.modeButtonPos.getValue(), ( entry ) -> Configuration.modeButtonPos.setValue( entry.getValue() ), new EModeButtonPos.Values() ),
                SettingsEntry.toggle( this, "", R.string.setting_mark_tapped_mine, R.string.setting_mark_tapped_mine_desc, Configuration.markTappedMine.getValue(), ( entry ) -> Configuration.markTappedMine.setValue( entry.getValue() ) ),
                SettingsEntry.spinner( this, "", R.string.setting_chording, R.string.setting_chording_desc, Configuration.numberTapBehavior.getValue(), ( entry ) -> Configuration.numberTapBehavior.setValue( entry.getValue() ), new ENumberTapBehavior.Values() ),
                SettingsEntry.spinner( this, "", R.string.setting_long_press, R.string.setting_long_press_desc, Configuration.longPressBehavior.getValue(), ( entry ) -> Configuration.longPressBehavior.setValue( entry.getValue() ), new ELongPressBehavior.Values() ),
                SettingsEntry.spinner( this, "", R.string.setting_win_condition, R.string.setting_win_condition_desc, Configuration.winCondition.getValue(), ( entry ) -> Configuration.winCondition.setValue( entry.getValue() ), new EWinPolicy.Values() ),
                SettingsEntry.spinner( this, "", R.string.setting_start_policy, R.string.setting_start_policy_desc, Configuration.startingPolicy.getValue(), ( entry ) -> Configuration.startingPolicy.setValue( entry.getValue() ), new EStartingPolicy.Values() ),
                SettingsEntry.spinner( this, "", R.string.setting_end_dialog, R.string.setting_end_dialog_desc, Configuration.endDialogBehavior.getValue(), ( entry ) -> Configuration.endDialogBehavior.setValue( entry.getValue() ), new EShowDialogOnEndBehavior.Values() ),
                SettingsEntry.toggle( this, "", R.string.setting_vibration, R.string.setting_vibration_desc, Configuration.vibration.getValue(), ( entry ) -> Configuration.vibration.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_keep_screen_on, R.string.setting_keep_screen_on_desc, Configuration.keepScreenOn.getValue(), ( entry ) -> Configuration.keepScreenOn.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_disable_flags_around_completed, null, Configuration.disableFlagOverflowAroundNums.getValue(), ( entry ) -> Configuration.disableFlagOverflowAroundNums.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_disable_flags_over_limit, null, Configuration.disableFlagOverflowTotal.getValue(), ( entry ) -> Configuration.disableFlagOverflowTotal.setValue( entry.getValue() ) ),

                // HINTS
                SettingsEntry.header( this, "", R.string.settings_header_hints ),
                SettingsEntry.toggle( this, "", R.string.setting_hint_button, R.string.setting_hint_button_desc, Configuration.showHintOption.getValue(), ( entry ) -> Configuration.showHintOption.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_inferred_flags, R.string.setting_inferred_flags_desc, Configuration.showInferredFlags.getValue(), ( entry ) -> Configuration.showInferredFlags.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_inferred_digs, R.string.setting_inferred_digs_desc, Configuration.showInferredDigs.getValue(), ( entry ) -> Configuration.showInferredDigs.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_mark_too_many_flags, null, Configuration.markTooManyFlags.getValue(), ( entry ) -> Configuration.markTooManyFlags.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_mark_infer_numbers, null, Configuration.markCompletableNumbers.getValue(), ( entry ) -> Configuration.markCompletableNumbers.setValue( entry.getValue() ) ),
                SettingsEntry.toggle( this, "", R.string.setting_mark_enough_flags, null, Configuration.markCompletedNumbers.getValue(), ( entry ) -> Configuration.markCompletedNumbers.setValue( entry.getValue() ) ),

                // ADVANCED
                SettingsEntry.header( this, "", R.string.settings_header_advanced ),
                SettingsEntry.slider( this, "", R.string.setting_chunk_size, R.string.setting_chunk_size_desc, R.string.simple_decimal_format, 1, 30, Configuration.gameChunkSize.getValue(), ( entry ) -> Configuration.gameChunkSize.setValue( entry.getValue() ) ),

                // ABOUT
                SettingsEntry.header( this, "", R.string.settings_header_about ),
                SettingsEntry.action( this, "", R.string.setting_about, null, this::openAbout ),
                SettingsEntry.action( this, "", R.string.setting_rate, R.string.setting_rate_desc, this::rateApp ),
                SettingsEntry.action( this, "", R.string.setting_open_github, R.string.setting_open_github_desc, this::openGithub ),
                SettingsEntry.action( this, "", R.string.setting_report_bug, R.string.setting_report_bug_desc, this::openIssueTracker ),
                SettingsEntry.action( this, "", R.string.setting_license, R.string.setting_license_desc, this::openLicense )
        );

        listView.setAdapter( settings );
    }

    public void restartApp() {
        System.exit( 0 );
    }

    public void openGithub( SettingsEntry.ActionEntry entry ) {

    }

    public void openIssueTracker( SettingsEntry.ActionEntry entry ) {

    }

    public void rateApp( SettingsEntry.ActionEntry entry ) {

    }

    public void openAbout( SettingsEntry.ActionEntry entry ) {
        Intent intent = new Intent( this, AboutActivity.class );
        startActivity( intent );
    }

    public void openLicense( SettingsEntry.ActionEntry entry ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle( R.string.menu_license );
        builder.setPositiveButton( R.string.ok, null );
        builder.setView( R.layout.license );
        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Configuration.useDarkTheme.removeChangeListener( darkThemeChangeListener );
    }
}
