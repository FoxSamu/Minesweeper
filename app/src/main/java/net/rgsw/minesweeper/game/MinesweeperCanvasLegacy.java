package net.rgsw.minesweeper.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import net.rgsw.minesweeper.R;

/**
 * @attr ref R.color#MinesweeperCanvas.iconSize
 */
public class MinesweeperCanvasLegacy extends View {

    private static final int COLOR_REVEALED = 0xffeeeeee;
    private static final int COLOR_UNREVEALED = 0xff9e9e9e;
    private static final int COLOR_GRID_OVERLAY = 0x15000000;
    private static final int COLOR_REVEALED_DARK = 0xff424242;
    private static final int COLOR_UNREVEALED_DARK = 0xff757575;

    private IGame game = new EditModeGame();

    private int scale = 32;
    private int cellSize = 40;
    private int padding = 0;
    private int cr = 4;
    private boolean grid = true;
    private boolean dark = true;
    private Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
    private Paint paint_noalias = new Paint();

    private OnCellClickListener cellClickListener;
    private OnCellLongClickListener cellLongClickListener;

    private float[] lastTouchDownXY = new float[ 2 ];

    // the purpose of the touch listener is just to store the touch X,Y coordinates
    @SuppressLint( "ClickableViewAccessibility" )
    View.OnTouchListener touchListener = ( v, event ) -> {

        // save the X,Y coordinates
        if( event.getActionMasked() == MotionEvent.ACTION_DOWN ) {
            lastTouchDownXY[ 0 ] = toDp( event.getX() );
            lastTouchDownXY[ 1 ] = toDp( event.getY() );
        }

        // let the touch event pass on to whoever needs it
        return false;
    };

    View.OnClickListener clickListener = v -> {
        float x = lastTouchDownXY[ 0 ];
        float y = lastTouchDownXY[ 1 ];

        x -= padding;
        y -= padding;

        x /= cellSize;
        y /= cellSize;

        int ix = ( int ) x;
        int iy = ( int ) y;

        if( cellClickListener != null ) { cellClickListener.onCellClick( this, ix, iy ); }
    };

    View.OnLongClickListener longClickListener = v -> {
        float x = lastTouchDownXY[ 0 ];
        float y = lastTouchDownXY[ 1 ];

        x -= padding;
        y -= padding;

        x /= cellSize;
        y /= cellSize;

        int ix = ( int ) x;
        int iy = ( int ) y;

        if( cellLongClickListener != null ) { cellLongClickListener.onCellLongClick( this, ix, iy ); }

        return true;
    };


    public MinesweeperCanvasLegacy( Context context ) {
        super( context );
        init( context );
    }

    public MinesweeperCanvasLegacy( Context context, AttributeSet attrs ) {
        super( context, attrs );
        init( context );
    }

    public MinesweeperCanvasLegacy( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        init( context );
    }

    public MinesweeperCanvasLegacy( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
        init( context );
    }

    public void setGame( IGame game ) {
        this.game = game;
        invalidate();
    }

    public IGame getGame() {
        return game;
    }

    public boolean isDark() {
        return dark;
    }

    public void setDark( boolean dark ) {
        this.dark = dark;
    }

    public void setCellSize( int cellSize ) {
        this.cellSize = cellSize;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setScale( int scale ) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    public void setGrid( boolean grid ) {
        this.grid = grid;
    }

    public boolean hasGrid() {
        return grid;
    }

    public void setCr( int cr ) {
        this.cr = cr;
    }

    public int getCr() {
        return cr;
    }

    private Drawable mine;
    private Drawable no_mine;
    private Drawable win_mine;
    private Drawable flagged_mine;
    private Drawable flag;
    private Drawable soft_mark;
    private Drawable found1;
    private Drawable found2;
    private Drawable found3;
    private Drawable found4;
    private Drawable found5;
    private Drawable found6;
    private Drawable found7;
    private Drawable found7inv;
    private Drawable found8;

    @SuppressLint( "ClickableViewAccessibility" )
    private void init( Context ctx ) {
        mine = ctx.getDrawable( R.drawable.ic_mine );
        no_mine = ctx.getDrawable( R.drawable.ic_no_mine );
        win_mine = ctx.getDrawable( R.drawable.ic_mine_win );
        flagged_mine = ctx.getDrawable( R.drawable.ic_flagged_mine );
        flag = ctx.getDrawable( R.drawable.ic_flag );
        soft_mark = ctx.getDrawable( R.drawable.ic_maybe );
        found1 = ctx.getDrawable( R.drawable.ic_found_1 );
        found2 = ctx.getDrawable( R.drawable.ic_found_2 );
        found3 = ctx.getDrawable( R.drawable.ic_found_3 );
        found4 = ctx.getDrawable( R.drawable.ic_found_4 );
        found5 = ctx.getDrawable( R.drawable.ic_found_5 );
        found6 = ctx.getDrawable( R.drawable.ic_found_6 );
        found7 = ctx.getDrawable( R.drawable.ic_found_7 );
        found7inv = ctx.getDrawable( R.drawable.ic_found_7_inv );
        found8 = ctx.getDrawable( R.drawable.ic_found_8 );

        if( this.isInEditMode() ) {
            game = new EditModeGame();
        }

        this.setOnClickListener( clickListener );
        this.setOnLongClickListener( longClickListener );
        this.setOnTouchListener( touchListener );
    }

    private int toPx( int dp ) {
        return Math.round( dp * ( getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    private float toDp( float px ) {
        return ( px / ( Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    private double width;
    private double height;
    private double xoff;
    private double yoff;
    private int[] locs = new int[ 2 ];

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );

        double s = getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT;

        height = toDp( getResources().getDisplayMetrics().heightPixels );
        width = toDp( getResources().getDisplayMetrics().widthPixels );

        getLocationOnScreen( locs );

        xoff = toDp( locs[ 0 ] );
        yoff = toDp( locs[ 1 ] );

        canvas.save();
        canvas.scale( ( float ) s, ( float ) s );

        paint.setColor( dark ? COLOR_REVEALED_DARK : COLOR_REVEALED );
        canvas.drawRoundRect( padding, padding, getGame().width() * cellSize + padding, getGame().height() * cellSize + padding, cr, cr, paint );

        for( int x = 0; x < getGame().width(); x++ ) {
            for( int y = 0; y < getGame().height(); y++ ) {
                drawCell( canvas, x, y );
            }
        }

        canvas.restore();
    }

    private Path path = new Path();

    public void drawCellBG( Canvas canvas, int x, int y, boolean[] corners, int col, float outset ) {
        path.reset();
        path.addRoundRect( x - outset, y - outset, x + cellSize + outset, y + cellSize + outset, new float[] {
                corners[ 0 ] ? 0 : cr,
                corners[ 0 ] ? 0 : cr,
                corners[ 1 ] ? 0 : cr,
                corners[ 1 ] ? 0 : cr,
                corners[ 2 ] ? 0 : cr,
                corners[ 2 ] ? 0 : cr,
                corners[ 3 ] ? 0 : cr,
                corners[ 3 ] ? 0 : cr
        }, Path.Direction.CW );

        paint.setColor( col );

        canvas.drawPath( path, paint );
    }

    public void drawIcon( Canvas canvas, int x, int y, Drawable drawable ) {
        int off = ( cellSize - scale ) / 2;
        drawable.setBounds( x + off, y + off, x + scale + off, y + scale + off );
        drawable.draw( canvas );
    }

    public Drawable getIcon( ECellState state ) {
        switch( state ) {
            case MINE:
                return mine;
            case FLAGGED_MINE:
                return flagged_mine;
            case NO_MINE:
                return no_mine;
            case MINE_WIN:
                return win_mine;
            case FLAGGED:
                return flag;
            case SOFT_MARKED:
                return soft_mark;
            case FOUND_1:
                return found1;
            case FOUND_2:
                return found2;
            case FOUND_3:
                return found3;
            case FOUND_4:
                return found4;
            case FOUND_5:
                return found5;
            case FOUND_6:
                return found6;
            case FOUND_7:
                return dark ? found7inv : found7; // Use white seven in dark theme
            case FOUND_8:
                return found8;
        }
        return null;
    }

    public void drawCell( Canvas canvas, ECellState state, int x, int y, int cx, int cy, boolean[] corners, boolean[] unrevCorners ) {
        if( !state.revealed ) {
            drawCellBG( canvas, x, y, unrevCorners, dark ? COLOR_UNREVEALED_DARK : COLOR_UNREVEALED, toDp( 1F ) );
        }
        if( grid && ( ( cx + cy ) & 1 ) == 0 ) drawCellBG( canvas, x, y, corners, COLOR_GRID_OVERLAY, 0 );

        Drawable icon = getIcon( state );
        if( icon != null ) drawIcon( canvas, x, y, icon );
    }

    public void drawCell( Canvas canvas, int cx, int cy ) {
        ECellState thisState = getGame().getState( cx, cy );

        boolean[] corners = new boolean[] { true, true, true, true };
        if( cx == 0 && cy == 0 ) corners[ 0 ] = false;
        if( cx == getGame().width() - 1 && cy == 0 ) corners[ 1 ] = false;
        if( cx == getGame().width() - 1 && cy == getGame().height() - 1 ) corners[ 2 ] = false;
        if( cx == 0 && cy == getGame().height() - 1 ) corners[ 3 ] = false;

        boolean[] unrevcorners = new boolean[] { false, false, false, false };

        if( cx > 0 && !getGame().getState( cx - 1, cy ).revealed ) {
            unrevcorners[ 0 ] = true;
            unrevcorners[ 3 ] = true;
        }
        if( cx < getGame().width() - 1 && !getGame().getState( cx + 1, cy ).revealed ) {
            unrevcorners[ 1 ] = true;
            unrevcorners[ 2 ] = true;
        }
        if( cy > 0 && !getGame().getState( cx, cy - 1 ).revealed ) {
            unrevcorners[ 0 ] = true;
            unrevcorners[ 1 ] = true;
        }
        if( cy < getGame().height() - 1 && !getGame().getState( cx, cy + 1 ).revealed ) {
            unrevcorners[ 2 ] = true;
            unrevcorners[ 3 ] = true;
        }

        if( cx == 0 && cy == 0 ) unrevcorners[ 0 ] = false;
        if( cx == getGame().width() - 1 && cy == 0 ) unrevcorners[ 1 ] = false;
        if( cx == getGame().width() - 1 && cy == getGame().height() - 1 ) unrevcorners[ 2 ] = false;
        if( cx == 0 && cy == getGame().height() - 1 ) unrevcorners[ 3 ] = false;

        drawCell( canvas, thisState, cx * cellSize + padding, cy * cellSize + padding, cx, cy, corners, unrevcorners );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        int width = toPx( getGame().width() * cellSize + padding * 2 );
        if( getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT ) {
        } else if( ( getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT ) ) {
            width = MeasureSpec.getSize( widthMeasureSpec );
        } else { width = getLayoutParams().width; }


        int height = toPx( getGame().height() * cellSize + padding * 2 );
        if( getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT ) {
        } else if( ( getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT ) ) {
            height = MeasureSpec.getSize( heightMeasureSpec );
        } else { height = getLayoutParams().height; }


        setMeasuredDimension( width | MeasureSpec.EXACTLY, height | MeasureSpec.EXACTLY );
    }

    public void setOnCellClickListener( OnCellClickListener cellClickListener ) {
        this.cellClickListener = cellClickListener;
    }

    public void setOnCellLongClickListener( OnCellLongClickListener cellLongClickListener ) {
        this.cellLongClickListener = cellLongClickListener;
    }

    public interface OnCellClickListener {
        void onCellClick( MinesweeperCanvasLegacy canvas, int x, int y );
    }

    public interface OnCellLongClickListener {
        void onCellLongClick( MinesweeperCanvasLegacy canvas, int x, int y );
    }
}
