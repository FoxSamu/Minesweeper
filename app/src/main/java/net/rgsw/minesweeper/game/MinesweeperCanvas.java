package net.rgsw.minesweeper.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.util.Utils;

public class MinesweeperCanvas extends View {

    ////////////////////////////////////////////////////////////////////////////
    // FIELDS                                                                 //
    ////////////////////////////////////////////////////////////////////////////

    private Drawable mineIcon;                              // Mine icon when dead
    private Drawable mineWinIcon;                           // Mine icon when won
    private Drawable mineFoundIcon;                         // Flagged mine icon
    private Drawable noMineIcon;                            // Wrongly placed flag icon
    private Drawable flagIcon;                              // Flag icon
    private Drawable softMarkIcon;                          // Question mark icon
    private Drawable oneIcon;                               // Number 1
    private Drawable twoIcon;                               // Number 2
    private Drawable threeIcon;                             // Number 3
    private Drawable fourIcon;                              // Number 4
    private Drawable fiveIcon;                              // Number 5
    private Drawable sixIcon;                               // Number 6
    private Drawable sevenIcon;                             // Number 7
    private Drawable eightIcon;                             // Number 8
    private Drawable inferredFlagIcon;                      // Inferred flag icon
    private Drawable inferredDigIcon;                       // Inferred dig icon

    private boolean gridEnabled;                            // Whether a grid should be rendered or not

    private int cellSize;                                   // Cell size (width and height)
    private int iconSize;                                   // Icon size
    private int cornerRounding;                             // Corner rounding

    private int redTint;                                    // Red tint for marks
    private int greenTint;                                  // Green tint for marks
    private int blueTint;                                   // Blue tint for marks
    private int yellowTint;                                 // Yellow tint for marks
    private int orangeTint;                                 // Orange tint for marks
    private int purpleTint;                                 // Purple tint for marks
    private int inverseTint;                                // White/Black tint for marks

    private int unrevColor;                                 // Color of unrevealed cells
    private int revColor;                                   // Color of revealed cells
    private int gridColor;                                  // Color of grid

    private IGame game;                                     // The game to render

    private IGameChunk chunk;                               // The chunk of the game to render

    private OnCellClickListener cellClickListener;          // Handler for cell click events
    private OnCellLongClickListener cellLongClickListener;  // Handler for cell long press events

    /** The coordinates of the last touch-down event, used in click listening */
    private float[] lastTouchDownXY = new float[ 2 ];

    private int iconPadding;                                // Padding of an icon compared to the cell size
    private int iconDimen;                                  // Actual icon dimensions based on icon padding

    private final float[] roundings = new float[ 8 ];
    private Path path = new Path();
    private Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );

    private static final int DP2 = Math.round( 2 * ( Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );

    private boolean longPressing;





    ////////////////////////////////////////////////////////////////////////////
    // INITIALIZATION                                                         //
    ////////////////////////////////////////////////////////////////////////////

    public MinesweeperCanvas( Context context ) {
        super( context );
        readAttrs( null, 0, 0 );
    }

    public MinesweeperCanvas( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        readAttrs( attrs, 0, 0 );
    }

    public MinesweeperCanvas( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        readAttrs( attrs, defStyleAttr, 0 );
    }

    public MinesweeperCanvas( Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
        readAttrs( attrs, defStyleAttr, defStyleRes );
    }

    private void readAttrs( AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes( attrs, R.styleable.MinesweeperCanvas, defStyleAttr, defStyleRes );

        // Read attributes
        mineIcon = a.getDrawable( R.styleable.MinesweeperCanvas_mineIcon );
        mineWinIcon = a.getDrawable( R.styleable.MinesweeperCanvas_mineWinIcon );
        mineFoundIcon = a.getDrawable( R.styleable.MinesweeperCanvas_mineFoundIcon );
        noMineIcon = a.getDrawable( R.styleable.MinesweeperCanvas_noMineIcon );
        flagIcon = a.getDrawable( R.styleable.MinesweeperCanvas_flagIcon );
        softMarkIcon = a.getDrawable( R.styleable.MinesweeperCanvas_softMarkIcon );
        oneIcon = a.getDrawable( R.styleable.MinesweeperCanvas_oneIcon );
        twoIcon = a.getDrawable( R.styleable.MinesweeperCanvas_twoIcon );
        threeIcon = a.getDrawable( R.styleable.MinesweeperCanvas_threeIcon );
        fourIcon = a.getDrawable( R.styleable.MinesweeperCanvas_fourIcon );
        fiveIcon = a.getDrawable( R.styleable.MinesweeperCanvas_fiveIcon );
        sixIcon = a.getDrawable( R.styleable.MinesweeperCanvas_sixIcon );
        sevenIcon = a.getDrawable( R.styleable.MinesweeperCanvas_sevenIcon );
        eightIcon = a.getDrawable( R.styleable.MinesweeperCanvas_eightIcon );
        inferredFlagIcon = a.getDrawable( R.styleable.MinesweeperCanvas_inferredFlagIcon );
        inferredDigIcon = a.getDrawable( R.styleable.MinesweeperCanvas_inferredDigIcon );

        gridEnabled = a.getBoolean( R.styleable.MinesweeperCanvas_gridCheckerboard, true );

        cellSize = a.getDimensionPixelSize( R.styleable.MinesweeperCanvas_cellSize, toPx( 40 ) );
        iconSize = a.getDimensionPixelSize( R.styleable.MinesweeperCanvas_iconSize, toPx( 32 ) );
        cornerRounding = a.getDimensionPixelSize( R.styleable.MinesweeperCanvas_cornerRounding, toPx( 4 ) );

        redTint = a.getColor( R.styleable.MinesweeperCanvas_redTint, getResources().getColor( R.color.red_tint ) );
        greenTint = a.getColor( R.styleable.MinesweeperCanvas_greenTint, getResources().getColor( R.color.green_tint ) );
        blueTint = a.getColor( R.styleable.MinesweeperCanvas_blueTint, getResources().getColor( R.color.blue_tint ) );
        yellowTint = a.getColor( R.styleable.MinesweeperCanvas_yellowTint, getResources().getColor( R.color.yellow_tint ) );
        orangeTint = a.getColor( R.styleable.MinesweeperCanvas_orangeTint, getResources().getColor( R.color.orange_tint ) );
        purpleTint = a.getColor( R.styleable.MinesweeperCanvas_purpleTint, getResources().getColor( R.color.purple_tint ) );
        inverseTint = a.getColor( R.styleable.MinesweeperCanvas_inverseTint, getResources().getColor( R.color.black_tint ) );

        unrevColor = a.getColor( R.styleable.MinesweeperCanvas_unrevealedColor, getResources().getColor( R.color.unrevealed_color ) );
        revColor = a.getColor( R.styleable.MinesweeperCanvas_revealedColor, getResources().getColor( R.color.revealed_color ) );
        gridColor = a.getColor( R.styleable.MinesweeperCanvas_gridColor, getResources().getColor( R.color.grid_overlay_color ) );

        // Default values for drawables, since unspecified drawables are just null
        mineIcon = Utils.orDefault( mineIcon, getContext().getDrawable( R.drawable.ic_mine ) );
        mineWinIcon = Utils.orDefault( mineWinIcon, getContext().getDrawable( R.drawable.ic_mine_win ) );
        mineFoundIcon = Utils.orDefault( mineFoundIcon, getContext().getDrawable( R.drawable.ic_flagged_mine ) );
        noMineIcon = Utils.orDefault( noMineIcon, getContext().getDrawable( R.drawable.ic_no_mine ) );
        flagIcon = Utils.orDefault( flagIcon, getContext().getDrawable( R.drawable.ic_flag ) );
        softMarkIcon = Utils.orDefault( softMarkIcon, getContext().getDrawable( R.drawable.ic_maybe ) );
        oneIcon = Utils.orDefault( oneIcon, getContext().getDrawable( R.drawable.ic_found_1 ) );
        twoIcon = Utils.orDefault( twoIcon, getContext().getDrawable( R.drawable.ic_found_2 ) );
        threeIcon = Utils.orDefault( threeIcon, getContext().getDrawable( R.drawable.ic_found_3 ) );
        fourIcon = Utils.orDefault( fourIcon, getContext().getDrawable( R.drawable.ic_found_4 ) );
        fiveIcon = Utils.orDefault( fiveIcon, getContext().getDrawable( R.drawable.ic_found_5 ) );
        sixIcon = Utils.orDefault( sixIcon, getContext().getDrawable( R.drawable.ic_found_6 ) );
        sevenIcon = Utils.orDefault( sevenIcon, getContext().getDrawable( R.drawable.ic_found_7 ) );
        eightIcon = Utils.orDefault( eightIcon, getContext().getDrawable( R.drawable.ic_found_8 ) );
        inferredFlagIcon = Utils.orDefault( inferredFlagIcon, getContext().getDrawable( R.drawable.ic_inferred_flag ) );
        inferredDigIcon = Utils.orDefault( inferredDigIcon, getContext().getDrawable( R.drawable.ic_inferred_dig ) );

        if( isInEditMode() ) { // Use a basic game view in the editor, to show that there is a minesweeper canvas
            game = new EditModeGame();
        }

        recomputeIconPadding();

        a.recycle(); // Free native memory used by the TypedArray


        // Do this to receive click events
        setClickable( true );
        setFocusable( true );
        setLongClickable( true );
    }





    ////////////////////////////////////////////////////////////////////////////
    // SOME UTILITY METHODS                                                   //
    ////////////////////////////////////////////////////////////////////////////

    private boolean isInChunk( int x, int y ) {
        if( chunk == null ) return true;
        int cx = chunk.getX(), cy = chunk.getY();
        if( x >= cx && x < cx + chunk.getW() ) return true;
        return y >= cy && y < cy + chunk.getH();
    }

    private int getChunkX() {
        return chunk == null ? 0 : chunk.getX();
    }

    private int getChunkY() {
        return chunk == null ? 0 : chunk.getY();
    }

    private int getGameWidth() {
        IGame game = getGame();
        return game == null ? 5 : game.width();
    }

    private int getGameHeight() {
        IGame game = getGame();
        return game == null ? 5 : game.height();
    }

    private int getChunkWidth() {
        IGameChunk chunk = getChunk();
        return chunk == null ? getGameWidth() : chunk.getW();
    }

    private int getChunkHeight() {
        IGameChunk chunk = getChunk();
        return chunk == null ? getGameHeight() : chunk.getH();
    }

    private int getActualWidth() {
        if( chunk == null ) return getGameWidth();
        return Math.min( getGameWidth() - getChunkX(), getChunkWidth() );
    }

    private int getActualHeight() {
        if( chunk == null ) return getGameHeight();
        return Math.min( getGameHeight() - getChunkY(), getChunkHeight() );
    }

    /**
     * Converts dp to px
     * @param dp The dp value
     * @return The px value
     */
    private int toPx( int dp ) {
        return Math.round( dp * ( getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    /**
     * Checks whether there is an unrevealed cell at specific location, taking the game bounds into account.
     */
    private boolean unrevealedAt( IGame game, int x, int y ) {
        if( x < 0 || y < 0 || x >= game.width() || y >= game.height() ) return false;
        return !game.getState( x, y ).revealed;
    }

    /**
     * Recomputes icon padding and icon dimen
     */
    private void recomputeIconPadding() {
        iconPadding = ( cellSize - iconSize );
        iconDimen = cellSize - iconPadding;
        iconPadding /= 2;
    }





    ////////////////////////////////////////////////////////////////////////////
    // INPUT HANDLING                                                         //
    ////////////////////////////////////////////////////////////////////////////


    @Override
    public boolean hasOnClickListeners() {
        return true;
    }

    @SuppressLint( "ClickableViewAccessibility" )
    @Override // Handle touching to save touch coordinates for click handling
    public boolean onTouchEvent( MotionEvent event ) {
        if( event.getActionMasked() == MotionEvent.ACTION_DOWN ) {
            lastTouchDownXY[ 0 ] = event.getX();
            lastTouchDownXY[ 1 ] = event.getY();

            longPressing = false;
        }

        return super.onTouchEvent( event );
    }

    @Override // Handle clicking to activate cell clicking
    public boolean performClick() {

        if( longPressing ) return super.performClick();

        float x = lastTouchDownXY[ 0 ]; // Use the coordinates saved in 'onTouchEvent'
        float y = lastTouchDownXY[ 1 ];

        x -= getPaddingTop();
        y -= getPaddingLeft();

        x /= cellSize;
        y /= cellSize;

        int ix = ( int ) x + getChunkX();
        int iy = ( int ) y + getChunkY();

        if( cellClickListener != null && isInChunk( ix, iy ) ) {
            cellClickListener.onCellClick( this, ix, iy );
        }

        return super.performClick();
    }

    @Override // Handle long pressing to activate long cell pressing
    public boolean performLongClick( float fx, float fy ) {
        longPressing = true;
        float x = fx - getPaddingTop();
        float y = fy - getPaddingLeft();

        x /= cellSize;
        y /= cellSize;

        int ix = ( int ) x + getChunkX();
        int iy = ( int ) y + getChunkY();

        if( cellLongClickListener != null && isInChunk( ix, iy ) ) {
            cellLongClickListener.onCellLongClick( this, ix, iy );
        }

        return super.performLongClick( fx, fy );
    }

    private int getColor( EMark mark ) {
        switch( mark ) {
            case RED:
                return redTint;
            case GREEN:
                return greenTint;
            case BLUE:
                return blueTint;
            case YELLOW:
                return yellowTint;
            case ORANGE:
                return orangeTint;
            case PURPLE:
                return purpleTint;
            default:
                return inverseTint;
        }
    }



    ////////////////////////////////////////////////////////////////////////////
    // RENDERING                                                              //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw( Canvas canvas ) {
        IGame game = getGame();
        if( game == null ) {
            return; // No rendering needed. There is no game to render
        }

        canvas.save();
        canvas.translate( getPaddingLeft(), getPaddingTop() );

        // Load chunk bounds
        int width = getActualWidth(), height = getActualHeight();
        int cx = getChunkX(), cy = getChunkY();

        // Find out which corners of the canvas are rounded
        boolean leftEdge = cx == 0;
        boolean topEdge = cy == 0;
        boolean rightEdge = cx + width == game.width();
        boolean bottomEdge = cy + height == game.height();

        float tl = leftEdge && topEdge ? cornerRounding : 0;
        float tr = rightEdge && topEdge ? cornerRounding : 0;
        float br = rightEdge && bottomEdge ? cornerRounding : 0;
        float bl = leftEdge && bottomEdge ? cornerRounding : 0;

        roundings[ 0 ] = tl;
        roundings[ 1 ] = tl;
        roundings[ 2 ] = tr;
        roundings[ 3 ] = tr;
        roundings[ 4 ] = br;
        roundings[ 5 ] = br;
        roundings[ 6 ] = bl;
        roundings[ 7 ] = bl;

        // Draw main background
        path.reset();
        path.addRoundRect( 0, 0, width * cellSize, height * cellSize, roundings, Path.Direction.CW );
        paint.setColor( revColor );
        canvas.drawPath( path, paint );


        // Draw cells that are in chunk bounds
        for( int x = cx; x < cx + width; x++ ) {
            for( int y = cy; y < cy + height; y++ ) {
                drawCell( canvas, game, x, y, x - cx, y - cy );
            }
        }

        canvas.restore();
    }

    private void drawCell( Canvas canvas, IGame game, int x, int y, int rx, int ry ) {
        ECellState state = game.getState( x, y );
        EMark bgMark = game.getBackgroundMark( x, y );
        EMark mark = game.getBorderMark( x, y );
        boolean infFlag = game.isInferredFlag( x, y );
        boolean infDig = game.isInferredDig( x, y );

        // Draw unrevealed background
        if( !state.revealed ) {
            float tl = cornerRounding, tr = cornerRounding, br = cornerRounding, bl = cornerRounding;

            if( unrevealedAt( game, x, y - 1 ) ) {
                tl = 0; tr = 0;
            }

            if( unrevealedAt( game, x, y + 1 ) ) {
                bl = 0; br = 0;
            }

            if( unrevealedAt( game, x - 1, y ) ) {
                tl = 0; bl = 0;
            }

            if( unrevealedAt( game, x + 1, y ) ) {
                tr = 0; br = 0;
            }

            roundings[ 0 ] = tl;
            roundings[ 1 ] = tl;
            roundings[ 2 ] = tr;
            roundings[ 3 ] = tr;
            roundings[ 4 ] = br;
            roundings[ 5 ] = br;
            roundings[ 6 ] = bl;
            roundings[ 7 ] = bl;

            path.reset();
            path.addRoundRect( rx * cellSize, ry * cellSize, ( rx + 1 ) * cellSize, ( ry + 1 ) * cellSize, roundings, Path.Direction.CW );
            paint.setColor( unrevColor );
            canvas.drawPath( path, paint );
        }

        if( isGridEnabled() ) {
            if( ( ( x + y ) & 1 ) == 0 ) {

                roundings[ 0 ] = x == 0 && y == 0 ? cornerRounding : 0;
                roundings[ 1 ] = x == 0 && y == 0 ? cornerRounding : 0;
                roundings[ 2 ] = x == game.width() - 1 && y == 0 ? cornerRounding : 0;
                roundings[ 3 ] = x == game.width() - 1 && y == 0 ? cornerRounding : 0;
                roundings[ 4 ] = x == game.width() - 1 && y == game.height() - 1 ? cornerRounding : 0;
                roundings[ 5 ] = x == game.width() - 1 && y == game.height() - 1 ? cornerRounding : 0;
                roundings[ 6 ] = x == 0 && y == game.height() - 1 ? cornerRounding : 0;
                roundings[ 7 ] = x == 0 && y == game.height() - 1 ? cornerRounding : 0;

                path.reset();
                path.addRoundRect( rx * cellSize, ry * cellSize, ( rx + 1 ) * cellSize, ( ry + 1 ) * cellSize, roundings, Path.Direction.CW );
                paint.setColor( gridColor );
                canvas.drawPath( path, paint );
            }
        }

        // Draw background mark
        if( bgMark != null ) {
            int color = getColor( bgMark );

            paint.setColor( color );
            paint.setAlpha( 0x45 );
            canvas.drawRoundRect( rx * cellSize, ry * cellSize, ( rx + 1 ) * cellSize, ( ry + 1 ) * cellSize, cornerRounding, cornerRounding, paint );
        }

        // Draw border mark
        if( mark != null ) {
            int color = getColor( mark );

            paint.setColor( color );

            path.reset();
            path.setFillType( Path.FillType.EVEN_ODD );
            path.addRoundRect( rx * cellSize, ry * cellSize, ( rx + 1 ) * cellSize, ( ry + 1 ) * cellSize, cornerRounding, cornerRounding, Path.Direction.CW );
            float rounding1 = Math.max( cornerRounding - DP2, 0 );
            path.addRoundRect( rx * cellSize + DP2, ry * cellSize + DP2, ( rx + 1 ) * cellSize - DP2, ( ry + 1 ) * cellSize - DP2, rounding1, rounding1, Path.Direction.CW );
            paint.setColor( color );
            canvas.drawPath( path, paint );
        }

        switch( state ) {
            case FLAGGED:
                drawIconInCell( canvas, flagIcon, rx, ry );
                break;
            case SOFT_MARKED:
                drawIconInCell( canvas, softMarkIcon, rx, ry );
                break;
            case MINE_WIN:
                drawIconInCell( canvas, mineWinIcon, rx, ry );
                break;
            case MINE:
                drawIconInCell( canvas, mineIcon, rx, ry );
                break;
            case NO_MINE:
                drawIconInCell( canvas, noMineIcon, rx, ry );
                break;
            case FLAGGED_MINE:
                drawIconInCell( canvas, mineFoundIcon, rx, ry );
                break;
            case FOUND_1:
                drawIconInCell( canvas, oneIcon, rx, ry );
                break;
            case FOUND_2:
                drawIconInCell( canvas, twoIcon, rx, ry );
                break;
            case FOUND_3:
                drawIconInCell( canvas, threeIcon, rx, ry );
                break;
            case FOUND_4:
                drawIconInCell( canvas, fourIcon, rx, ry );
                break;
            case FOUND_5:
                drawIconInCell( canvas, fiveIcon, rx, ry );
                break;
            case FOUND_6:
                drawIconInCell( canvas, sixIcon, rx, ry );
                break;
            case FOUND_7:
                drawIconInCell( canvas, sevenIcon, rx, ry );
                break;
            case FOUND_8:
                drawIconInCell( canvas, eightIcon, rx, ry );
                break;
            default:
                if( infFlag ) {
                    drawIconInCell( canvas, inferredFlagIcon, rx, ry );
                } else if( infDig ) {
                    drawIconInCell( canvas, inferredDigIcon, rx, ry );
                }
                break;
        }
    }

    /**
     * Draws an icon in a cell, if the icon is not null
     */
    private void drawIconInCell( Canvas canvas, Drawable icon, int x, int y ) {
        drawDrawableIfNotNull(
                canvas, icon,
                x * cellSize + iconPadding,
                y * cellSize + iconPadding,
                iconDimen,
                iconDimen,
                null, 255
        );
    }

    /**
     * Draws the specified drawable when it is not null.
     * and corner rounding is applied automatically.
     * @param canvas   The canvas to draw on
     * @param drawable The drawable to draw, or null
     * @param x        The x location in pixels
     * @param y        The y location in pixels
     * @param w        The drawable width in pixels
     * @param h        The drawable height in pixels
     * @param tint     The drawable tint, or null if no tint should be used
     * @param alpha    The drawable alpha
     */
    private void drawDrawableIfNotNull( Canvas canvas, Drawable drawable, int x, int y, int w, int h, Integer tint, Integer alpha ) {
        if( drawable == null ) {
            return;
        }

        drawable.setBounds( x, y, x + w, y + h );
        if( tint != null ) drawable.setTint( tint );
        if( alpha != null ) drawable.setAlpha( alpha );

        drawable.draw( canvas );
    }



    ////////////////////////////////////////////////////////////////////////////
    // MEASURING                                                              //
    ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        int width = getActualWidth() * cellSize;
        width += getPaddingLeft() + getPaddingRight();
        if( getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT ) {
        } else if( ( getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT ) ) {
            width = MeasureSpec.getSize( widthMeasureSpec );
        } else { width = getLayoutParams().width; }


        int height = getActualHeight() * cellSize;
        height += getPaddingTop() + getPaddingBottom();
        if( getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT ) {
        } else if( ( getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT ) ) {
            height = MeasureSpec.getSize( heightMeasureSpec );
        } else { height = getLayoutParams().height; }


        setMeasuredDimension( width | MeasureSpec.EXACTLY, height | MeasureSpec.EXACTLY );
    }





    ////////////////////////////////////////////////////////////////////////////
    // SETTERS AND GETTERS                                                    //
    ////////////////////////////////////////////////////////////////////////////



    /**
     * Enables grid pattern rendering on this canvas, to show where each separate cell is. This is usually done with a
     * checkerboard, but a line or point grid could be used by specifying a custom grid drawable and disabling grid
     * checkerboard mode.
     * @param gridEnabled Whether the grid overlay should be rendered or not.
     * @see #isGridEnabled()
     */
    public void setGridEnabled( boolean gridEnabled ) {
        this.gridEnabled = gridEnabled;
        invalidate();
    }

    /**
     * Returns whether the grid overlay is enabled or not.
     * @return True when grid overlay is rendered.
     * @see #setGridEnabled(boolean)
     */
    public boolean isGridEnabled() {
        return gridEnabled;
    }



    public void setMineIcon( Drawable drawable ) {
        this.mineIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setMineIcon( @DrawableRes int drawableRes ) {
        setMineIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getMineIcon() {
        return mineIcon;
    }



    public void setMineFoundIcon( Drawable drawable ) {
        this.mineFoundIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setMineFoundIcon( @DrawableRes int drawableRes ) {
        setMineFoundIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getMineFoundIcon() {
        return mineFoundIcon;
    }



    public void setMineWinIcon( Drawable drawable ) {
        this.mineWinIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setMineWinIcon( @DrawableRes int drawableRes ) {
        setMineWinIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getMineWinIcon() {
        return mineWinIcon;
    }



    public void setNoMineIcon( Drawable drawable ) {
        this.noMineIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setNoMineIcon( @DrawableRes int drawableRes ) {
        setNoMineIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getNoMineIcon() {
        return noMineIcon;
    }



    public void setFlagIcon( Drawable drawable ) {
        this.flagIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setFlagIcon( @DrawableRes int drawableRes ) {
        setFlagIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getFlagIcon() {
        return flagIcon;
    }



    public void setSoftMarkIcon( Drawable drawable ) {
        this.softMarkIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setSoftMarkIcon( @DrawableRes int drawableRes ) {
        setSoftMarkIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getSoftMarkIcon() {
        return softMarkIcon;
    }



    public void setOneIcon( Drawable drawable ) {
        this.oneIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setOneIcon( @DrawableRes int drawableRes ) {
        setOneIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getOneIcon() {
        return oneIcon;
    }



    public void setTwoIcon( Drawable drawable ) {
        this.twoIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setTwoIcon( @DrawableRes int drawableRes ) {
        setTwoIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getTwoIcon() {
        return twoIcon;
    }



    public void setThreeIcon( Drawable drawable ) {
        this.threeIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setThreeIcon( @DrawableRes int drawableRes ) {
        setThreeIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getThreeIcon() {
        return threeIcon;
    }



    public void setFourIcon( Drawable drawable ) {
        this.fourIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setFourIcon( @DrawableRes int drawableRes ) {
        setFourIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getFourIcon() {
        return fourIcon;
    }



    public void setFiveIcon( Drawable drawable ) {
        this.fiveIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setFiveIcon( @DrawableRes int drawableRes ) {
        setFiveIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getFiveIcon() {
        return fiveIcon;
    }



    public void setSixIcon( Drawable drawable ) {
        this.sixIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setSixIcon( @DrawableRes int drawableRes ) {
        setSixIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getSixIcon() {
        return sixIcon;
    }



    public void setSevenIcon( Drawable drawable ) {
        this.sevenIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setSevenIcon( @DrawableRes int drawableRes ) {
        setSevenIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getSevenIcon() {
        return sevenIcon;
    }



    public void setEightIcon( Drawable drawable ) {
        this.eightIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setEightIcon( @DrawableRes int drawableRes ) {
        setEightIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getEightIcon() {
        return eightIcon;
    }



    public void setInferredFlagIcon( Drawable drawable ) {
        this.inferredFlagIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setInferredFlagIcon( @DrawableRes int drawableRes ) {
        setInferredFlagIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getInferredFlagIcon() {
        return inferredFlagIcon;
    }



    public void setInferredDigIcon( Drawable drawable ) {
        this.inferredDigIcon = drawable;
        invalidate(); // Mark for re-render
    }

    public void setInferredDigIcon( @DrawableRes int drawableRes ) {
        setInferredDigIcon( getContext().getDrawable( drawableRes ) );
    }

    public Drawable getInferredDigIcon() {
        return inferredDigIcon;
    }



    public void setCellSize( int dimen ) {
        this.cellSize = dimen;
        invalidate(); // Mark for re-render
        recomputeIconPadding();
    }

    public int getCellSize() {
        return cellSize;
    }



    public void setIconSize( int dimen ) {
        this.iconSize = dimen;
        invalidate(); // Mark for re-render
        recomputeIconPadding();
    }

    public int getIconSize() {
        return iconSize;
    }



    public void setCornerRounding( int dimen ) {
        this.cornerRounding = dimen;
        invalidate(); // Mark for re-render
    }

    public int getCornerRounding() {
        return cornerRounding;
    }



    public void setRedTint( @ColorInt int color ) {
        this.redTint = color;
        invalidate(); // Mark for re-render
    }

    public int getRedTint() {
        return redTint;
    }



    public void setGreenTint( @ColorInt int color ) {
        this.greenTint = color;
        invalidate(); // Mark for re-render
    }

    public int getGreenTint() {
        return greenTint;
    }



    public void setBlueTint( @ColorInt int color ) {
        this.blueTint = color;
        invalidate(); // Mark for re-render
    }

    public int getBlueTint() {
        return blueTint;
    }



    public void setYellowTint( @ColorInt int color ) {
        this.yellowTint = color;
        invalidate(); // Mark for re-render
    }

    public int getYellowTint() {
        return yellowTint;
    }



    public void setOrangeTint( @ColorInt int color ) {
        this.orangeTint = color;
        invalidate(); // Mark for re-render
    }

    public int getOrangeTint() {
        return orangeTint;
    }



    public void setPurpleTint( @ColorInt int color ) {
        this.purpleTint = color;
        invalidate(); // Mark for re-render
    }

    public int getPurpleTint() {
        return purpleTint;
    }



    public void setInverseTint( @ColorInt int color ) {
        this.inverseTint = color;
        invalidate(); // Mark for re-render
    }

    public int getInverseTint() {
        return inverseTint;
    }



    public void setUnrevealedColor( @ColorInt int color ) {
        this.unrevColor = color;
        invalidate(); // Mark for re-render
    }

    public int getUnrevealedColor() {
        return unrevColor;
    }



    public void setRevealedColor( @ColorInt int color ) {
        this.revColor = color;
        invalidate(); // Mark for re-render
    }

    public int getRevealedColor() {
        return revColor;
    }



    public void setGridColor( @ColorInt int color ) {
        this.gridColor = color;
        invalidate(); // Mark for re-render
    }

    public int getGridColor() {
        return gridColor;
    }



    public void setGame( IGame game ) {
        this.game = game;
        invalidate(); // Mark for re-render
    }

    public IGame getGame() {
        return game;
    }



    public void setChunk( IGameChunk chunk ) {
        this.chunk = chunk;
        invalidate();
    }

    public IGameChunk getChunk() {
        return chunk;
    }




    ////////////////////////////////////////////////////////////////////////////
    // LISTENERS                                                              //
    ////////////////////////////////////////////////////////////////////////////

    public void setOnCellClickListener( OnCellClickListener cellClickListener ) {
        this.cellClickListener = cellClickListener;
    }

    public void setOnCellLongClickListener( OnCellLongClickListener cellLongClickListener ) {
        this.cellLongClickListener = cellLongClickListener;
    }

    public interface OnCellClickListener {
        void onCellClick( MinesweeperCanvas canvas, int x, int y );
    }

    public interface OnCellLongClickListener {
        void onCellLongClick( MinesweeperCanvas canvas, int x, int y );
    }
}
