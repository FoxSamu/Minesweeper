package net.rgsw.minesweeper.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import net.rgsw.minesweeper.R;

public class EditDialogController {

    @SuppressLint( { "CutPasteId", "SetTextI18n" } )
    public static void show( int titleres, Context ctx, ModeListAdapter listAdapter, String name, Integer initW, Integer initH, Integer initMines, int pos ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
        builder.setTitle( titleres );
        builder.setPositiveButton( R.string.save, ( idialog, which ) -> {
            AlertDialog dialog = ( AlertDialog ) idialog;

            EditText titleInput = dialog.findViewById( R.id.edit_mode_name );
            EditText widthInput = dialog.findViewById( R.id.edit_mode_width );
            EditText heightInput = dialog.findViewById( R.id.edit_mode_height );
            EditText minesInput = dialog.findViewById( R.id.edit_mode_mines );

            String title = titleInput.getText().toString();

            if( title.length() > 0 && title.charAt( 0 ) == '@' ) return;

            int width = 9;
            int height = 9;
            int mines = 10;

            try {
                width = Integer.parseInt( widthInput.getText().toString() );
            } catch( NumberFormatException ignored ) {}

            try {
                height = Integer.parseInt( heightInput.getText().toString() );
            } catch( NumberFormatException ignored ) {}

            try {
                mines = Integer.parseInt( minesInput.getText().toString() );
            } catch( NumberFormatException ignored ) {}

            if( pos < 0 ) { listAdapter.addItem( new Mode( title, mines, width, height ) ); } else {
                Mode m = ( Mode ) listAdapter.getItem( pos );
                m.setTitle( title );
                m.setSize( width, height );
                m.setMines( mines );
            }
            listAdapter.notifyDataSetChanged();
        } );
        builder.setNegativeButton( R.string.cancel, null );
        builder.setView( R.layout.add_mode_dialog );
        AlertDialog dialog = builder.show();


        // Title
        TextInputLayout titleLayout = dialog.findViewById( R.id.edit_mode_name_layout );
        TextInputLayout widthLayout = dialog.findViewById( R.id.edit_mode_width_layout );
        TextInputLayout heightLayout = dialog.findViewById( R.id.edit_mode_height_layout );
        TextInputLayout minesLayout = dialog.findViewById( R.id.edit_mode_mines_layout );

        EditText mines = dialog.findViewById( R.id.edit_mode_mines );
        SeekBar minesBar = dialog.findViewById( R.id.edit_mode_mines_bar );

        EditText title = dialog.findViewById( R.id.edit_mode_name );
        title.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                titleLayout.setError( null );
                if( s.length() > 0 && s.charAt( 0 ) == '@' ) {
                    titleLayout.setError( ctx.getString( R.string.edit_mode_error_invalid_name ) );
                }

                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void afterTextChanged( Editable s ) {

            }
        } );
        if( name != null ) title.setText( name );



        // Width
        EditText width = dialog.findViewById( R.id.edit_mode_width );
        SeekBar widthBar = dialog.findViewById( R.id.edit_mode_width_bar );
        EditText height = dialog.findViewById( R.id.edit_mode_height );
        SeekBar heightBar = dialog.findViewById( R.id.edit_mode_height_bar );
        if( initW != null ) {
            width.setText( initW.toString() );
            widthBar.setProgress( initW - 5 );
        }
        width.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                widthLayout.setError( null );
                try {
                    int w = Integer.parseInt( s.toString() );
                    if( w < 5 || w > 100 ) {
                        widthLayout.setError( ctx.getString( R.string.error_range_5_100 ) );
                        updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
                        return;
                    }
                    widthBar.setProgress( w - 5 );

                    int w1 = 9;
                    try {
                        w1 = Integer.parseInt( width.getText().toString() );
                    } catch( NumberFormatException ignored ) {}

                    int h1 = 9;
                    try {
                        h1 = Integer.parseInt( height.getText().toString() );
                    } catch( NumberFormatException ignored ) {}

                    int max = w1 * h1 - 1;
                    minesBar.setMax( max );
                    mines.setText( mines.getText() );
                } catch( NumberFormatException exc ) {
                    widthLayout.setError( ctx.getString( R.string.error_nan ) );
                }

                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void afterTextChanged( Editable s ) {

            }
        } );

        widthBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint( "SetTextI18n" )
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                if( fromUser ) width.setText( ( progress + 5 ) + "" );
                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        } );



        // Height
        if( initH != null ) {
            height.setText( initH.toString() );
            heightBar.setProgress( initH - 5 );
        }
        height.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                heightLayout.setError( null );
                try {
                    int w = Integer.parseInt( s.toString() );
                    if( w < 5 || w > 100 ) {
                        heightLayout.setError( ctx.getString( R.string.error_range_5_100 ) );
                        updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
                        return;
                    }
                    heightBar.setProgress( w - 5 );

                    int w1 = 9;
                    try {
                        w1 = Integer.parseInt( width.getText().toString() );
                    } catch( NumberFormatException ignored ) {}

                    int h1 = 9;
                    try {
                        h1 = Integer.parseInt( height.getText().toString() );
                    } catch( NumberFormatException ignored ) {}

                    int max = w1 * h1 - 1;
                    minesBar.setMax( max );
                    mines.setText( mines.getText() );
                } catch( NumberFormatException exc ) {
                    heightLayout.setError( ctx.getString( R.string.error_nan ) );
                }

                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void afterTextChanged( Editable s ) {

            }
        } );

        heightBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint( "SetTextI18n" )
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                if( fromUser ) height.setText( ( progress + 5 ) + "" );
                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        } );


        // Mines
        if( initMines != null && initW != null && initH != null ) {
            mines.setText( initMines.toString() );
            minesBar.setProgress( initMines - 1 );
            minesBar.setMax( initW * initH - 2 );
        }
        mines.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

            }

            @Override
            public void onTextChanged( CharSequence s, int start, int before, int count ) {
                minesLayout.setError( null );
                try {
                    int m = Integer.parseInt( s.toString() );

                    int w = 9;
                    try {
                        w = Integer.parseInt( width.getText().toString() );
                    } catch( NumberFormatException ignored ) {}

                    int h = 9;
                    try {
                        h = Integer.parseInt( height.getText().toString() );
                    } catch( NumberFormatException ignored ) {}

                    int max = w * h - 1;

                    if( m < 1 ) {
                        minesLayout.setError( ctx.getString( R.string.error_negative_mines ) );
                        updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
                        return;
                    }

                    if( m > max ) {
                        minesLayout.setError( ctx.getString( R.string.error_range_mines ) );
                        updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
                        return;
                    }

                    minesBar.setProgress( m - 1 );
                    minesBar.setMax( max - 1 );
                } catch( NumberFormatException exc ) {
                    minesLayout.setError( ctx.getString( R.string.error_nan ) );
                }

                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void afterTextChanged( Editable s ) {

            }
        } );

        minesBar.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint( "SetTextI18n" )
            @Override
            public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                if( fromUser ) mines.setText( ( progress + 1 ) + "" );
                updateButton( dialog, minesLayout, widthLayout, heightLayout, titleLayout );
            }

            @Override
            public void onStartTrackingTouch( SeekBar seekBar ) {

            }

            @Override
            public void onStopTrackingTouch( SeekBar seekBar ) {

            }
        } );
    }

    private static void updateButton( AlertDialog dialog, TextInputLayout minesL, TextInputLayout widthL, TextInputLayout heightL, TextInputLayout titleL ) {
        Button button = dialog.getButton( AlertDialog.BUTTON_POSITIVE );
        button.setEnabled(
                titleL.getError() == null && widthL.getError() == null && heightL.getError() == null && minesL.getError() == null
        );
        button.invalidate();
    }
}
