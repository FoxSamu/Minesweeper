package net.rgsw.minesweeper.game;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.rgsw.ctable.io.CTableDecoder;
import net.rgsw.ctable.io.CTableEncoder;
import net.rgsw.ctable.io.CTableReader;
import net.rgsw.ctable.io.CTableWriter;
import net.rgsw.ctable.tag.TagStringCompound;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.main.Mode;
import net.rgsw.minesweeper.settings.Configuration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class GameActivityLgc extends AppCompatActivity {

    private Mode mode = null;
    private MinesweeperGame game;
    private MinesweeperCanvasLegacy gameCanvas;
    private TextView timeView;
    private TextView minesView;
    private Toolbar toolbar;
    private Handler handler;
    private FloatingActionButton modeButton;
    private MenuItem pauseIcon;
    private MenuItem faceIcon;
    private boolean flagMode;
    private boolean finalUpdate;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setTheme( Configuration.useDarkTheme.getValue() ? R.style.AppTheme_Dark_NoActionBar : R.style.AppTheme_NoActionBar );
        setContentView( R.layout.activity_game_lgc );

        // Create action bar
        setSupportActionBar( toolbar = findViewById( R.id.toolbar2 ) );
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setDisplayShowHomeEnabled( true );

        if( savedInstanceState != null && savedInstanceState.getBoolean( "playing", false ) ) {
            // Activity was killed, resume...
            resume();
        } else if( getIntent().getBooleanExtra( "resuming", false ) ) {
            // Resume button was pressed
            resume();
        } else {
            // Start a new game since no one told us to resume saved game
            init();
        }

        handler = new Handler();

        // Find some views
        timeView = findViewById( R.id.game_time );
        minesView = findViewById( R.id.game_remaining_mines );
        gameCanvas = findViewById( R.id.canvas );
        modeButton = findViewById( R.id.mode_button );

        // Set FAB listener
        modeButton.setOnClickListener( this::modeButtonPress );


        // Place a time update in the queue
        int stack = 0;
        while( !handler.postDelayed( this::updateTime, 200 ) ) {
            System.out.println( stack );
            stack++;
        }

        setResult( RESULT_CANCELED ); // Initial result

        gameCanvas.setDark( Configuration.useDarkTheme.getValue() );
        gameCanvas.setCellSize( Configuration.gameCellSize.getValue() );
        gameCanvas.setScale( Configuration.gameIconSize.getValue() );
        gameCanvas.setGrid( Configuration.checkerboardGrid.getValue() );
        gameCanvas.setCr( Configuration.gameCornerRadius.getValue() );
        gameCanvas.invalidate();

        CardView gameCard = findViewById( R.id.game_cardview );
        gameCard.setRadius( Configuration.gameCornerRadius.getValue() );
        gameCard.invalidate();

        // Start updating game state
        updateGameState();

        LinearLayout layout = findViewById( R.id.mode_button_layout );
        switch( Configuration.modeButtonPos.getValue() ) {
            case LEFT:
                layout.setGravity( Gravity.LEFT ); break;
            case MIDDLE:
                layout.setGravity( Gravity.CENTER_HORIZONTAL ); break;
            case RIGHT:
                layout.setGravity( Gravity.RIGHT ); break;
        }

    }

    // Animates the FAB color
    public void setFABColor( int old, int now ) {
        ValueAnimator animator = ValueAnimator.ofObject( new ArgbEvaluator(), old, now );
        animator.addUpdateListener( animation -> {
            int val = ( int ) animation.getAnimatedValue();
            modeButton.setSupportBackgroundTintList( ColorStateList.valueOf( val ) );
        } );
        animator.setDuration( 300 );
        animator.start();
    }

    // Called on FAB click
    public void modeButtonPress( View v ) {
        if( !game.isInitialized() ) return;
        if( flagMode ) {
            flagMode = false; // Switch to reveal mode
            setFABColor( 0xff00e676, 0xffff5252 );
            modeButton.setImageResource( R.drawable.ic_mode_reveal );
        } else {
            flagMode = true; // Switch to flag mode
            setFABColor( 0xffff5252, 0xff00e676 );
            modeButton.setImageResource( R.drawable.ic_mode_flag );
        }
    }

    @SuppressLint( "DefaultLocale" )
    public void updateTime() {
        long timeMS = game.getTimeMS();

        String formatted = String.format(
                "%d:%02d:%02d",
                ( ( timeMS / ( 1000 * 60 * 60 ) ) % 24 ),
                ( ( timeMS / ( 1000 * 60 ) ) % 60 ),
                ( ( timeMS / ( 1000 ) ) % 60 )
        );

        timeView.setText( formatted );
        timeView.invalidate();

        // Wait until a message is placed
        int stack = 0;
        while( !handler.postDelayed( this::updateTime, 200 ) ) {
            System.out.println( stack );
            stack++;
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate( R.menu.menu_game, menu );
        pauseIcon = menu.findItem( R.id.menu_pause );
        faceIcon = menu.findItem( R.id.menu_face );

        // Update game state again so that icons are updated too
        updateGameState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();

        if( id == R.id.menu_pause ) { // Pause/play
            if( !game.done() ) {
                if( game.isPaused() ) {
                    game.resume(); // Resume button
                    pauseIcon.setIcon( R.drawable.ic_game_pause );
                } else {
                    game.pause(); // Pause button
                    pauseIcon.setIcon( R.drawable.ic_game_play );
                }
            }

            // Re-render
            toolbar.invalidate();
            gameCanvas.invalidate();
            return true;
        }

        if( id == R.id.menu_face ) { // Face: only play new game when done
            if( game.done() ) {
                newGame();
            }
            return true;
        }

        if( id == R.id.menu_new_game ) { // New game, even if we are still playing
            newGame();
            return true;
        }

        if( id == R.id.menu_main_menu ) { // Main menu, just finish the activity
            finish();
            return true;
        }

        if( id == R.id.menu_discard ) { // Main menu, and do not save game
            Intent intent = new Intent();
            TagStringCompound cpd = new TagStringCompound();
            mode.save( cpd ); // Serialize mode in ctable format
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            CTableEncoder encoder = new CTableEncoder( stream );
            try {
                encoder.write( cpd );
            } catch( IOException e ) {
                e.printStackTrace();
                setResult( RESULT_CANCELED );
                finish();
                return true;
            }
            intent.putExtra( "mode", stream.toByteArray() );
            intent.putExtra( "undone", false ); // Set as done, we don't need to restore game state again
            setResult( RESULT_OK, intent );
            finish();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    // Initializes a new game
    public void init() {
        byte[] bytes = getIntent().getByteArrayExtra( "mode" );
        if( bytes == null ) {
            finish();
            return;
        }
        ByteArrayInputStream stream = new ByteArrayInputStream( bytes );
        CTableDecoder decoder = new CTableDecoder( stream );
        try {
            TagStringCompound cpd = decoder.readTagStringCompound();
            mode = new Mode( cpd );
        } catch( IOException e ) {
            e.printStackTrace();
            finish();
            return;
        }

        this.game = new MinesweeperGame( mode );

        MinesweeperCanvasLegacy canvas = findViewById( R.id.canvas );
        canvas.setGame( this.game );
        initCanvas( canvas );

        // Updates the played games counter
        mode.started();
    }

    // Loads from savedState.dat
    public void resume() {
        File filesDir = getFilesDir();
        try( CTableReader reader = new CTableReader( new File( filesDir, "savedState.dat" ), false, false ) ) {
            reader.init();
            TagStringCompound cpd = reader.readTagStringCompound();
            game = new MinesweeperGame( null );
            game.load( cpd );
            mode = game.getMode();
        } catch( Exception e ) {
            e.printStackTrace();
            init();
            return;
        }

        MinesweeperCanvasLegacy canvas = findViewById( R.id.canvas );
        canvas.setGame( this.game );
        initCanvas( canvas );
    }

    // Saves the game state
    public void save() {
        File filesDir = getFilesDir();
        try( CTableWriter writer = new CTableWriter( new File( filesDir, "savedState.dat" ), false, false ) ) {
            TagStringCompound cpd = new TagStringCompound();
            game.save( cpd );
            writer.write( cpd );
        } catch( Exception exc ) {
            exc.printStackTrace();
            // Finish... Something went wrong...
            finish();
        }
    }

    // Store game states on stop
    @Override
    protected void onStop() {
        super.onStop();
        save();
    }

    // Store game states on pause
    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    public void initCanvas( MinesweeperCanvasLegacy canvas ) {
        // Init cell click listeners
        canvas.setOnCellClickListener( this::onClick );
        canvas.setOnCellLongClickListener( this::onLongClick );
    }

    public void updateGameState() {
        if( finalUpdate ) return;
        int remaining = game.mines() - game.totalFlags();
        minesView.setText( getString( R.string.simple_decimal_format, remaining ) );


        if( faceIcon != null ) faceIcon.setIcon( R.drawable.ic_face_alive );
        if( game.isPaused() ) {
            if( pauseIcon != null ) pauseIcon.setIcon( R.drawable.ic_game_play );
        } else {
            if( pauseIcon != null ) pauseIcon.setIcon( R.drawable.ic_game_pause );
        }

        if( game.done() ) {
            if( pauseIcon != null ) pauseIcon.setIcon( R.drawable.ic_game_stopped );
            if( game.won() ) {
                // Won!
                if( faceIcon != null ) faceIcon.setIcon( R.drawable.ic_face_happy );
                if( minesView != null ) minesView.setText( getString( R.string.simple_decimal_format, 0 ) );

                // Show a dialog, TODO: only on setting
//                WinDialog dialog = new WinDialog( this );
//                long bestTimeMS = mode.getBestTime();
//                long timeMS = game.getTimeMS();
//                dialog.show( game.mines(), game.getTimeMS(), bestTimeMS < 0 || timeMS < bestTimeMS );
            } else {
                // Lost!
                if( faceIcon != null ) faceIcon.setIcon( R.drawable.ic_face_dead );

                // Show a dialog, TODO: only on setting
//                LoseDialog dialog = new LoseDialog( this );
//                dialog.show( game.getFlaggedMines(), game.mines(), game.getRevealedRelative() );
            }

            finalUpdate = true;

            // Update stats
            mode.played( game.won(), game.getTimeMS() );
        }

        // Re-render
        toolbar.invalidate();
        gameCanvas.invalidate();


        // Save mode and stats to activity result, so that stats get updated when exiting the activity
        Intent intent = new Intent();
        TagStringCompound cpd = new TagStringCompound();
        mode.save( cpd ); // Serialize mode in ctable format
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CTableEncoder encoder = new CTableEncoder( stream );
        try {
            encoder.write( cpd );
        } catch( IOException e ) {
            e.printStackTrace();
            setResult( RESULT_CANCELED );
            return;
        }
        intent.putExtra( "mode", stream.toByteArray() );
        intent.putExtra( "undone", !game.done() );
        setResult( RESULT_OK, intent );
    }

    // Called when a cell is pressed
    public void onClick( MinesweeperCanvasLegacy canvas, int x, int y ) {
        game.doInput( x, y, flagMode ? MinesweeperGame.Flag.FLAG : null );
        canvas.invalidate();
        updateGameState();
    }

    // Called when a cell is long-pressed
    public void onLongClick( MinesweeperCanvasLegacy canvas, int x, int y ) {
        game.doInput( x, y, flagMode ? MinesweeperGame.Flag.SOFT_MARK : MinesweeperGame.Flag.FLAG );
        canvas.invalidate();
        updateGameState();
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );

        // Save the game, so that we can load it again on activity start
        save();

        // Store that we were playing the game, and that it should be reloaded when the activity opens again...
        outState.putBoolean( "playing", true );
    }

    public void doneAndFinish() {
        Intent intent = new Intent();
        TagStringCompound cpd = new TagStringCompound();
        mode.save( cpd ); // Serialize mode in ctable format
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CTableEncoder encoder = new CTableEncoder( stream );
        try {
            encoder.write( cpd );
        } catch( IOException e ) {
            e.printStackTrace();
            setResult( RESULT_CANCELED );
            return;
        }
        intent.putExtra( "mode", stream.toByteArray() );
        intent.putExtra( "undone", false );
        setResult( RESULT_OK, intent );
        finish();
    }

    @Override
    public void onMultiWindowModeChanged( boolean isInMultiWindowMode ) {
        super.onMultiWindowModeChanged( isInMultiWindowMode );
        gameCanvas.invalidate();
    }

    @Override
    public void onPictureInPictureModeChanged( boolean isInPictureInPictureMode ) {
        super.onPictureInPictureModeChanged( isInPictureInPictureMode );
        gameCanvas.invalidate();
    }

    // Do a new game
    public void newGame() {
        faceIcon.setIcon( R.drawable.ic_face_alive );
        pauseIcon.setIcon( R.drawable.ic_game_pause );
        finalUpdate = false;
        game.reset();
        if( flagMode ) {
            setFABColor( 0xff00e676, 0xffff5252 );
            modeButton.setImageResource( R.drawable.ic_mode_reveal );
            flagMode = false;
        }
        mode.started();
        updateGameState();
        toolbar.invalidate();
        gameCanvas.invalidate();
    }
}
