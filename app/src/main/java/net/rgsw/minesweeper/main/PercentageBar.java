package net.rgsw.minesweeper.main;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

public class PercentageBar extends View {
    private final int negativeColor = 0xFFFF5252;
    private final int positiveColor = 0xFF00E676;

    private final Paint negativePaint = new Paint( Paint.ANTI_ALIAS_FLAG );
    private final Paint positivePaint = new Paint( Paint.ANTI_ALIAS_FLAG );

    {
        negativePaint.setColor( negativeColor );
        positivePaint.setColor( positiveColor );
    }

    private int valueNegative;
    private int valuePositive;

    public PercentageBar( Context context ) {
        super( context );
    }

    public PercentageBar( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
    }

    public PercentageBar( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
    }

    public PercentageBar( Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes ) {
        super( context, attrs, defStyleAttr, defStyleRes );
    }

    public void setValueNegative( int valueNegative ) {
        this.valueNegative = valueNegative;
    }

    public void setValuePositive( int valuePositive ) {
        this.valuePositive = valuePositive;
    }

    public int getValueNegative() {
        return valueNegative;
    }

    public int getValuePositive() {
        return valuePositive;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );

        int width = getWidth();
        int height = getHeight();

        double ratio = valuePositive / ( double ) ( valuePositive + valueNegative );

        if( isInEditMode() ) {
            ratio = 0.3;
        }

        canvas.drawRect( 0, 0, width, height, negativePaint );
        canvas.drawRect( 0, 0, width * ( float ) ratio, height, positivePaint );
    }

    private int toPx( int dp ) {
        return Math.round( dp * ( getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    private float toDp( float px ) {
        return ( px / ( Resources.getSystem().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        int width = toPx( 100 );
        if( getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT ) {
        } else if( ( getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT ) ) {
            width = MeasureSpec.getSize( widthMeasureSpec );
        } else { width = getLayoutParams().width; }


        int height = toPx( 4 );
        if( getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT ) {
        } else if( ( getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT ) ) {
            height = MeasureSpec.getSize( heightMeasureSpec );
        } else { height = getLayoutParams().height; }


        setMeasuredDimension( width | MeasureSpec.EXACTLY, height | MeasureSpec.EXACTLY );
    }
}
