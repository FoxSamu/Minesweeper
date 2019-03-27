package net.rgsw.minesweeper.main;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import net.rgsw.ctable.tag.TagStringCompound;
import net.rgsw.minesweeper.R;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Mode {

    private String title;
    private int played;
    private int completed;
    private int wins;
    private long bestTimeMS;
    private int mines;
    private int width;
    private int height;
    private boolean isNative;

    private View view;

    public Mode( String title, int mines, int width, int height ) {
        this.title = title;
        this.mines = mines;
        this.width = width;
        this.height = height;
        this.played = 0;
        this.wins = 0;
        this.bestTimeMS = -1;
    }

    public Mode( TagStringCompound compound ) {
        this.title = compound.getString( "title" );
        this.completed = compound.optInteger( "completed", 0 );
        this.played = compound.optInteger( "played", 0 );
        this.wins = compound.optInteger( "wins", 0 );
        this.bestTimeMS = compound.optLong( "best", -1 );
        this.mines = compound.optInteger( "mines", 0 );
        this.width = compound.optInteger( "width", 0 );
        this.height = compound.optInteger( "height", 0 );
        this.isNative = compound.optBoolean( "native", false );
    }

    public Mode( TagStringCompound compound, Mode old ) {
        this.title = old.title;
        this.mines = old.mines;
        this.width = old.width;
        this.height = old.height;
        this.completed = compound.optInteger( "completed", 0 );
        this.played = compound.optInteger( "played", 0 );
        this.wins = compound.optInteger( "wins", 0 );
        this.bestTimeMS = compound.optLong( "best", -1 );
        this.isNative = compound.optBoolean( "native", false );
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setSize( int w, int h ) {
        this.width = w;
        this.height = h;
        updateView();
    }

    public int getMines() {
        return mines;
    }

    public void setMines( int mines ) {
        this.mines = mines;
        updateView();
    }

    public void resetStats() {
        this.played = 0;
        this.bestTimeMS = -1;
        this.wins = 0;
        this.completed = 0;
        updateView();
    }

    private void updateBestTime( long timeMS ) {
        if( timeMS < 0 ) return;
        if( bestTimeMS < 0 || timeMS < bestTimeMS ) {
            bestTimeMS = timeMS;
        }
        updateView();
    }

    public long getBestTime() {
        return bestTimeMS;
    }

    public int getWins() {
        return wins;
    }

    public void played( boolean won, long timeMS ) {
        this.completed += 1;
        if( won ) {
            this.wins += 1;
            updateBestTime( timeMS );
        }
        updateView();
    }

    public void started() {
        this.played += 1;
    }

    public void save( TagStringCompound compound ) {
        compound.set( "title", title );
        compound.set( "completed", completed );
        compound.set( "played", played );
        compound.set( "best", bestTimeMS );
        compound.set( "wins", wins );
        compound.set( "mines", mines );
        compound.set( "width", width );
        compound.set( "height", height );
        compound.set( "native", isNative );
    }

    public void setView( View v ) {
        view = v;
        updateView();
    }

    public int getCompleted() {
        return completed;
    }

    public int getPlayed() {
        return played;
    }

    private void updateView() {
        if( view == null ) return;
        Context ctx = view.getContext();

        TextView titleView = view.findViewById( R.id.mode_title );
        titleView.setText( this.getLocalizedName() );

        TextView sizeView = view.findViewById( R.id.mode_size );
        sizeView.setText( ctx.getString( R.string.mode_size_format, this.width, this.height ) );

        TextView minesView = view.findViewById( R.id.mode_mines );
        minesView.setText( ctx.getString( R.string.mode_mines_format, this.mines ) );

        TextView playedView = view.findViewById( R.id.mode_total_played );
        playedView.setText( ctx.getString( R.string.mode_played_format, this.played ) );

        double winrate = this.wins / ( double ) this.completed * 100;

        TextView winRateView = view.findViewById( R.id.mode_win_rate );
        winRateView.setText( ctx.getString( R.string.mode_win_rate_format, ( int ) winrate ) );

        String formatted;
        if( bestTimeMS >= 0 ) {
            Date date = new Date( this.bestTimeMS );
            DateFormat formatter = new SimpleDateFormat( "HH:mm:ss.SSS", Locale.US );
            formatter.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
            formatted = formatter.format( date );
        } else {
            formatted = ctx.getString( R.string.inapplicable );
        }

        TextView timeView = view.findViewById( R.id.mode_best_time );
        timeView.setText( ctx.getString( R.string.mode_best_time_format, formatted ) );
    }

    public View getView() {
        return view;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public boolean isNative() {
        return isNative;
    }


    public String formatBestTime( Context ctx ) {
        String formatted;
        if( bestTimeMS >= 0 ) {
            Date date = new Date( this.bestTimeMS );
            DateFormat formatter = new SimpleDateFormat( "HH:mm:ss.SSS", Locale.US );
            formatter.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
            formatted = formatter.format( date );
        } else {
            formatted = ctx.getString( R.string.inapplicable );
        }
        return formatted;
    }

    public String getLocalizedName() {
        if( title.length() > 0 && title.charAt( 0 ) == '@' && view != null ) {
            Context ctx = view.getContext();
            return ctx.getString( getResId( "mode_name_" + title.substring( 1 ), R.string.class ) );
        } else {
            return title;
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

    public Mode makeNative() {
        isNative = true;
        return this;
    }
}
