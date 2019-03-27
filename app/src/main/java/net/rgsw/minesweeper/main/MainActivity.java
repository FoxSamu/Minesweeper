package net.rgsw.minesweeper.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import net.rgsw.ctable.io.CTableDecoder;
import net.rgsw.ctable.io.CTableEncoder;
import net.rgsw.ctable.io.CTableReader;
import net.rgsw.ctable.io.CTableWriter;
import net.rgsw.ctable.tag.TagBoolean;
import net.rgsw.ctable.tag.TagInteger;
import net.rgsw.ctable.tag.TagList;
import net.rgsw.ctable.tag.TagStringCompound;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.about.SplashActivity;
import net.rgsw.minesweeper.game.GameActivity;
import net.rgsw.minesweeper.settings.Configuration;
import net.rgsw.minesweeper.settings.SettingsActivity;
import net.rgsw.minesweeper.tutorial.TutorialActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private File filesDir;

    private ModeListAdapter listAdapter;
    private ListView list;
    private LinearLayout optionsBg;
    private MenuItem resumeItem;
    private Toolbar toolbar;

    BottomSheetBehavior bottomSheet;

    private boolean restored;
    private boolean restoredFromSettings;
    private int currentGame = -1;

    private boolean optionsOpen;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        if( savedInstanceState == null ) {
            // Show splash screen
            startActivity( new Intent( this, SplashActivity.class ) );
        }

        Configuration.init( this );
        setTheme( Configuration.useDarkTheme.getValue() ? R.style.AppTheme_Dark_NoActionBar : R.style.AppTheme_NoActionBar );

        setContentView( R.layout.activity_main );

        filesDir = getFilesDir();

        // Set toolbar
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        FloatingActionButton fab = findViewById( R.id.add_button );
        fab.setOnClickListener( this::onAddMode );
        fab.setOnLongClickListener( this::showTooltip );

        // List
        list = findViewById( R.id.mode_list );
        listAdapter = new ModeListAdapter( this, list );
        list.setAdapter( listAdapter );
        listAdapter.setOnOptionsListener( this::onModeOptions );
        listAdapter.setOnPressListener( this::onModeInfo );

        ( optionsBg = findViewById( R.id.options_bg ) ).setOnClickListener( this::closeOptions );
        findViewById( R.id.options_cancel ).setOnClickListener( this::closeOptions );
        findViewById( R.id.options_clear_history ).setOnClickListener( this::onClearHistoryClick );
        findViewById( R.id.options_remove ).setOnClickListener( this::onDeleteClick );
        findViewById( R.id.options_edit ).setOnClickListener( this::onEditClick );
        findViewById( R.id.options_copy ).setOnClickListener( this::onCopyClick );
        findViewById( R.id.options_play ).setOnClickListener( this::onPlayClick );
        findViewById( R.id.options_info ).setOnClickListener( this::onInfoClick );

        findViewById( R.id.options_cancel ).setOnLongClickListener( this::showTooltip );
        findViewById( R.id.options_clear_history ).setOnLongClickListener( this::showTooltip );
        findViewById( R.id.options_remove ).setOnLongClickListener( this::showTooltip );
        findViewById( R.id.options_edit ).setOnLongClickListener( this::showTooltip );
        findViewById( R.id.options_copy ).setOnLongClickListener( this::showTooltip );
        findViewById( R.id.options_info ).setOnLongClickListener( this::showTooltip );
        findViewById( R.id.options_play ).setOnLongClickListener( this::showTooltip );

        restored = savedInstanceState != null;

        BottomSheetBehavior bsb = BottomSheetBehavior.from( findViewById( R.id.bottom_sheet ) );
        Toolbar bst = findViewById( R.id.bottom_sheet_title );
        LinearLayout bsi = findViewById( R.id.bottom_sheet_info );
        bst.measure( View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED );
        bsi.measure( View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED );
        bsb.setPeekHeight( bst.getMeasuredHeight() + convertDpToPx( 4 ) + bsi.getMeasuredHeight() );
        bsb.setFitToContents( true );
        bsb.setHideable( true );
        bsb.setState( BottomSheetBehavior.STATE_HIDDEN );

        bottomSheet = bsb;

        // Load modes
        loadModes();
    }

    @Override
    public void onBackPressed() {
        if( optionsOpen ) {
            closeOptions( null );
        } else if( bottomSheet.getState() != BottomSheetBehavior.STATE_HIDDEN ) {
            bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
        } else {
            super.onBackPressed();
        }
    }

    public boolean showTooltip( View v ) {
        Toast t = new Toast( this );

        t.setDuration( Toast.LENGTH_SHORT );

        int[] locs = new int[ 2 ];
        v.getLocationInWindow( locs );
        View view = getLayoutInflater().inflate( R.layout.tooltip_layout, null );
        TextView txt = view.findViewById( R.id.tooltip_txt );
        txt.setText( v.getContentDescription() );
        t.setView( view );
        t.setMargin( 0, 0 );
        t.setGravity(
                Gravity.LEFT | Gravity.TOP,
                locs[ 0 ],
                locs[ 1 ] - v.getMeasuredHeight() * 3 / 2
        );
        t.show();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveModes();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveModes();
    }

    public void saveModes() {
        TagList list = new TagList();
        this.listAdapter.save( list );
        File file = new File( filesDir, "modes.dat" );
        try( CTableWriter writer = new CTableWriter( file, true, true ) ) {
            writer.write( new TagBoolean( restoredFromSettings ) );
            writer.write( list );
            writer.write( new TagInteger( this.currentGame ) );
        } catch( IOException exc ) {
            exc.printStackTrace();
        }
    }

    public void setCurrentGame( int currentGame ) {
        if( currentGame < 0 ) {
            if( resumeItem != null ) resumeItem.setVisible( false );
        } else {
            if( resumeItem != null ) resumeItem.setVisible( true );
        }
        toolbar.invalidate();
        this.currentGame = currentGame;
    }

    public void loadModes() {
        File file = new File( filesDir, "modes.dat" );
        TagList list = null;
        if( file.exists() ) {
            try( CTableReader reader = new CTableReader( file, true, true ) ) {
                restoredFromSettings = reader.readTagBoolean().getValue();
                list = reader.readTagList();
                if( restoredFromSettings || !restored ) setCurrentGame( reader.readTagInteger().getValue() );
                System.out.println( "Reloading modes from FS. Restored activity: " + restored );
                restoredFromSettings = false;
            } catch( Exception exc ) {
                exc.printStackTrace();
            }
        }
        boolean recreateNatives = false;
        if( list == null ) {
            list = new TagList();
            recreateNatives = true;
        }

        listAdapter.load( list );
        listAdapter.notifyDataSetChanged();

        if( recreateNatives ) {
            createNativesAndShowTutorial();
        }
    }

    public static int getResId( String resName, Class<?> c ) {

        try {
            Field idField = c.getDeclaredField( resName );
            return idField.getInt( idField );
        } catch( Exception e ) {
            e.printStackTrace();
            return -1;
        }
    }

    public void createNativesAndShowTutorial() {
        listAdapter.addItem( new Mode( "@very_easy", 4, 6, 6 ).makeNative() );
        listAdapter.addItem( new Mode( "@easy", 10, 9, 9 ).makeNative() );
        listAdapter.addItem( new Mode( "@normal", 40, 16, 16 ).makeNative() );
        listAdapter.addItem( new Mode( "@hard", 99, 16, 30 ).makeNative() );
        listAdapter.addItem( new Mode( "@very_hard", 200, 35, 25 ).makeNative() );
        listAdapter.addItem( new Mode( "@longstretched", 99, 100, 8 ).makeNative() );
        listAdapter.addItem( new Mode( "@tiny", 8, 5, 5 ).makeNative() );

        Intent intent = new Intent( this, TutorialActivity.class );
        startActivityForResult( intent, 0 );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        resumeItem = menu.findItem( R.id.menu_resume );
        resumeItem.setVisible( currentGame >= 0 );
        toolbar.invalidate();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.menu_settings ) {
            restoredFromSettings = true;
            saveModes();
            Intent intent = new Intent( this, SettingsActivity.class );
            startActivityForResult( intent, 0xffff );
            return true;
        }

        if( id == R.id.menu_tutorial ) {
            Intent intent = new Intent( this, TutorialActivity.class );
            startActivityForResult( intent, 0 );
            return true;
        }

        if( id == R.id.menu_resume ) {
            resumeGame( listAdapter.getItem( currentGame ), currentGame );
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    public void closeOptions( View v ) {
        if( !optionsOpen ) return;
        optionsOpen = false;
        findViewById( R.id.options_cancel ).setOnClickListener( null );
        findViewById( R.id.options_bg ).setOnClickListener( null );
        Animation anim = new AlphaAnimation( 1, 0 );
        anim.setDuration( 300 );
        anim.setAnimationListener( new Animation.AnimationListener() {
            @Override
            public void onAnimationStart( Animation animation ) {

            }

            @Override
            public void onAnimationEnd( Animation animation ) {
                optionsBg.setVisibility( View.GONE );
            }

            @Override
            public void onAnimationRepeat( Animation animation ) {

            }
        } );
        optionsBg.startAnimation( anim );
    }

    private int[] locs = new int[ 2 ];

    private int currentOptionsPos = -1;

    public void doPlayGame() {
        if( currentGame < 0 ) {
            playGame( listAdapter.getItem( currentOptionsPos ), currentOptionsPos );
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( R.string.msg_other_game )
                .setNegativeButton( R.string.action_resume, ( dialog, which ) -> {
                    resumeGame( listAdapter.getItem( currentGame ), currentGame );
                } )
                .setPositiveButton( R.string.action_startnew, ( dialog, which ) -> {
                    playGame( listAdapter.getItem( currentOptionsPos ), currentOptionsPos );
                } )
                .show();
    }

    public void playGame( Mode mode, int index ) {
        Intent intent = new Intent( this, GameActivity.class );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TagStringCompound cpd = new TagStringCompound();
        mode.save( cpd );
        CTableEncoder encoder = new CTableEncoder( stream );
        try {
            encoder.write( cpd );
            encoder.close();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        intent.putExtra( "mode", stream.toByteArray() );
        startActivityForResult( intent, index + 1 );
    }


    // This method is invoked when target activity return result data back.
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent intent ) {
        super.onActivityResult( requestCode, resultCode, intent );

        restoredFromSettings = false;
        if( requestCode == 0 ) {
            if( resultCode == RESULT_OK ) {
                playGame( listAdapter.getItem( 0 ), 0 );
            }
        } else if( requestCode == 0xffff ) {
            restoredFromSettings = true;
        } else if( resultCode == RESULT_OK ) {
            ByteArrayInputStream stream = new ByteArrayInputStream( intent.getByteArrayExtra( "mode" ) );
            CTableDecoder decoder = new CTableDecoder( stream );
            try {
                TagStringCompound cpd = decoder.readTagStringCompound();
                Mode m = new Mode( cpd, listAdapter.getItem( requestCode - 1 ) );
                listAdapter.setItem( m, requestCode - 1 );
                listAdapter.notifyDataSetChanged();
            } catch( IOException e ) {
                e.printStackTrace();
            }

            if( intent.getBooleanExtra( "undone", false ) ) {
                System.out.println( "Received result from game " + ( requestCode - 1 ) );
                setCurrentGame( requestCode - 1 );
            } else {
                System.out.println( "Received result from game which is done now" );
                setCurrentGame( -1 );
            }
        }
    }

    public void resumeGame( Mode mode, int idx ) {
        Intent intent = new Intent( this, GameActivity.class );
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TagStringCompound cpd = new TagStringCompound();
        mode.save( cpd );
        CTableEncoder encoder = new CTableEncoder( stream );
        try {
            encoder.write( cpd );
            encoder.close();
        } catch( IOException e ) {
            e.printStackTrace();
        }
        intent.putExtra( "mode", stream.toByteArray() );
        intent.putExtra( "resuming", true );
        startActivityForResult( intent, idx + 1 );
    }

    public void onModeInfo( View btn, View entry, int pos, ModeListAdapter adapter ) {
        currentOptionsPos = pos;
        onInfoClick( btn );
    }

    public void onModeOptions( View btn, View entry, int pos, ModeListAdapter adapter, boolean play ) {
        optionsOpen = true;
        findViewById( R.id.options_cancel ).setOnClickListener( this::closeOptions );
        findViewById( R.id.options_bg ).setOnClickListener( this::closeOptions );
        currentOptionsPos = pos;
        if( play ) {
            doPlayGame();
            return;
        }
        btn.getLocationInWindow( locs );

        int px24 = convertDpToPx( 24 );

        CardView optionsView = findViewById( R.id.options_menu );
        LinearLayout.LayoutParams pars = ( LinearLayout.LayoutParams ) optionsView.getLayoutParams();
        pars.topMargin = locs[ 1 ] - px24;

//        int h = optionsView.getMeasuredHeight();
//
//        LinearLayout parent = (LinearLayout) optionsView.getParent();
//
//        if( pars.topMargin + h > parent.getHeight() - px24 ) {
//            pars.topMargin = parent.getHeight() - h - px24;
//        }


        optionsBg.setVisibility( View.VISIBLE );
        Animation anim = new AlphaAnimation( 0, 1 );
        anim.setDuration( 300 );
        optionsBg.startAnimation( anim );

        Mode m = listAdapter.getItem( pos );

        findViewById( R.id.options_remove ).setVisibility( m.isNative() ? View.GONE : View.VISIBLE );
        findViewById( R.id.options_edit ).setVisibility( m.isNative() ? View.GONE : View.VISIBLE );
    }

    public void onClearHistoryClick( View v ) {
        bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
        closeOptions( v );
        Mode m = listAdapter.getItem( currentOptionsPos );
        m.resetStats();
        listAdapter.notifyDataSetChanged();
    }

    public void delete() {
        Mode m = listAdapter.getItem( currentOptionsPos );
        listAdapter.removeItem( m );
        listAdapter.notifyDataSetChanged();

        // Decrease current game since when one entry is removed, all next entries decrease in index
        if( currentGame >= currentOptionsPos ) currentGame--;

        Snackbar.make( optionsBg, getString( R.string.msg_removed ), Snackbar.LENGTH_LONG )
                .setAction(
                        R.string.action_undo, v1 -> {
                            listAdapter.addItem( m, currentOptionsPos );
                            listAdapter.notifyDataSetChanged();

                            // Reincrease
                            if( currentGame >= currentOptionsPos ) currentGame++;
                        }
                )
                .show();
    }

    public void onDeleteClick( View v ) {
        bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
        closeOptions( v );

        if( currentOptionsPos == currentGame ) {
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setPositiveButton( R.string.delete_error_delete, ( dialog, which ) -> {
                setCurrentGame( -1 );
                delete();
            } );
            builder.setNegativeButton( R.string.delete_error_cancel, null );
            builder.setIcon( Configuration.useDarkTheme.getValue() ? R.drawable.ic_warning_inv : R.drawable.ic_dialog_warning );
            builder.setMessage( R.string.delete_error_msg );
            builder.setTitle( R.string.delete_error_title );
            builder.show();
        } else {
            delete();
        }
    }

    public void onEditClick( View v ) {
        bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
        closeOptions( v );
        Mode m = listAdapter.getItem( currentOptionsPos );
        new EditDialog(
                this, EditDialog.EDIT,
                m.getLocalizedName(),
                m.getWidth(), m.getHeight(), m.getMines(),
                currentOptionsPos, listAdapter
        ).show();
    }

    public void onCopyClick( View v ) {
        bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
        closeOptions( v );
        Mode m = listAdapter.getItem( currentOptionsPos );
        new EditDialog(
                this, EditDialog.COPY,
                m.getLocalizedName(),
                m.getWidth(), m.getHeight(), m.getMines(),
                -1, listAdapter
        ).show();
    }

    public void onPlayClick( View v ) {
        bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
        closeOptions( v );
        doPlayGame();
    }

    private void setTextViewText( int textViewID, String text ) {
        TextView v = findViewById( textViewID );
        v.setText( text );
    }

    public void onInfoClick( View v ) {
        closeOptions( v );
        Mode m = listAdapter.getItem( currentOptionsPos );

        setTextViewText( R.id.info_title, m.getLocalizedName() );
        setTextViewText( R.id.info_dimensions, getString( R.string.mode_size_format, m.getWidth(), m.getHeight() ) );
        setTextViewText( R.id.info_mines, getString( R.string.simple_decimal_format, m.getMines() ) );
        setTextViewText( R.id.info_win_rate, getString( R.string.mode_win_rate_format, ( int ) ( m.getWins() / ( double ) m.getCompleted() * 100 ) ) );
        setTextViewText( R.id.info_games_played, getString( R.string.simple_decimal_format, m.getPlayed() ) );
        setTextViewText( R.id.info_games_won, getString( R.string.simple_decimal_format, m.getWins() ) );
        setTextViewText( R.id.info_games_lost, getString( R.string.simple_decimal_format, m.getCompleted() - m.getWins() ) );
        setTextViewText( R.id.info_games_discarded, getString( R.string.simple_decimal_format, m.getPlayed() - m.getCompleted() - ( currentOptionsPos == currentGame ? 1 : 0 ) ) );
        setTextViewText( R.id.info_best_time, m.formatBestTime( this ) );

        findViewById( R.id.info_delete_btn ).setVisibility( m.isNative() ? View.GONE : View.VISIBLE );
        findViewById( R.id.info_edit_btn ).setVisibility( m.isNative() ? View.GONE : View.VISIBLE );

        PercentageBar bar = findViewById( R.id.winBar );
        bar.setValueNegative( m.getCompleted() - m.getWins() );
        bar.setValuePositive( m.getWins() );
        bar.invalidate();

        ScrollView sv = findViewById( R.id.bottom_sheet_sv );
        sv.fullScroll( View.FOCUS_UP );

        Button button = findViewById( R.id.info_play );
        button.setOnClickListener( ( view ) -> {
            bottomSheet.setState( BottomSheetBehavior.STATE_HIDDEN );
            doPlayGame();
        } );

        bottomSheet.setState( BottomSheetBehavior.STATE_COLLAPSED );
    }

    private int convertDpToPx( int dp ) {
        return Math.round( dp * ( getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    private int convertPxToDp( int px ) {
        return Math.round( px / ( Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    public void onAddMode( View view ) {
        new EditDialog(
                this, EditDialog.ADD,
                getString( R.string.txt_custom ),
                9, 9, 10,
                -1, listAdapter
        ).show();
    }
}
