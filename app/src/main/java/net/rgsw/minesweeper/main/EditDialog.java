package net.rgsw.minesweeper.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import net.rgsw.minesweeper.R;
import net.rgsw.minesweeper.settings.Configuration;
import org.intellij.lang.annotations.MagicConstant;

public class EditDialog extends AlertDialog.Builder implements TextWatcher {
    // These are just initial values used when showing
    private final String title;
    private final int width;
    private final int height;
    private final int mines;
    private final int updateModeIndex;
    private final ModeListAdapter listAdapter;

    private EditText titleEditor;
    private EditText widthEditor;
    private EditText heightEditor;
    private EditText minesEditor;
    private TextView error;
    private Button saveButton;
    private boolean hasError;

    public static final int ADD = 0;
    public static final int EDIT = 1;
    public static final int COPY = 2;

    private static final int[] OPERATION_TITLES = {
            R.string.edit_mode_add, R.string.edit_mode_edit, R.string.edit_mode_copy
    };


    private static final int[] OPERATION_ICONS = {
            R.drawable.ic_action_add_dark,
            R.drawable.ic_action_copy,
            R.drawable.ic_action_delete_mode
    };

    private static final int[] OPERATION_ICONS_DARK = {
            R.drawable.ic_action_add,
            R.drawable.ic_action_copy_inv,
            R.drawable.ic_action_delete_mode_inv
    };

    public EditDialog( Context context, @MagicConstant( intValues = { ADD, EDIT, COPY } ) int operation, String title, int width, int height, int mines, int updateModeIndex, ModeListAdapter listAdapter ) {
        super( context );
        this.title = title;
        this.width = width;
        this.height = height;
        this.mines = mines;
        this.updateModeIndex = updateModeIndex;
        this.listAdapter = listAdapter;

        setTitle( OPERATION_TITLES[ operation ] );
        setIcon(
                Configuration.useDarkTheme.getValue() ?
                        OPERATION_ICONS_DARK[ operation ] :
                        OPERATION_ICONS[ operation ]
        );

        setView( R.layout.dialog_edit );

        setPositiveButton( R.string.save, this::save );
        setNegativeButton( R.string.cancel, null );
    }

    private void save( DialogInterface dialog, int which ) {
        if( hasError ) return;

        if( updateModeIndex < 0 ) {
            listAdapter.addItem( new Mode( getTitle(), getMines(), getWidth(), getHeight() ) );
        } else {
            Mode m = listAdapter.getItem( updateModeIndex );
            m.setTitle( getTitle() );
            m.setMines( getMines() );
            m.setSize( getWidth(), getHeight() );
        }

        listAdapter.notifyDataSetChanged();
    }

    public String getTitle() {
        return titleEditor.getText().toString();
    }

    public int getWidth() {
        return Integer.parseInt( widthEditor.getText().toString() );
    }

    public int getHeight() {
        return Integer.parseInt( heightEditor.getText().toString() );
    }

    public int getMines() {
        return Integer.parseInt( minesEditor.getText().toString() );
    }

    private String textFor( EditText txt ) {
        return txt.getText().toString();
    }

    private boolean error( int error ) {
        this.error.setText( error );
        saveButton.setEnabled( false );
        hasError = true;
        return true;
    }

    private void noError() {
        this.error.setText( "" );
        saveButton.setEnabled( true );
        hasError = false;
    }

    private boolean checkError() {
        noError();

        String title = textFor( titleEditor );
        if( title.length() != 0 && title.charAt( 0 ) == '@' ) {
            return error( R.string.edit_error_name_char );
        }

        String widthS = textFor( widthEditor );
        String heightS = textFor( heightEditor );

        int width, height;

        try {
            width = Integer.parseInt( widthS );
        } catch( NumberFormatException exc ) {
            return error( R.string.error_nan );
        }

        if( width < 5 || width > 100 ) return error( R.string.edit_error_size );

        try {
            height = Integer.parseInt( heightS );
        } catch( NumberFormatException exc ) {
            return error( R.string.error_nan );
        }

        if( height < 5 || height > 100 ) return error( R.string.edit_error_size );

        int minesMax = width * height - 1;


        String minesS = textFor( minesEditor );
        int mines;

        try {
            mines = Integer.parseInt( minesS );
        } catch( NumberFormatException exc ) {
            return error( R.string.error_nan );
        }

        if( mines < 1 ) return error( R.string.edit_error_mines_low );
        if( mines > minesMax ) return error( R.string.edit_error_mines_high );

        return false;
    }

    public AlertDialog show() {
        AlertDialog dialog = super.show();

        hasError = false;

        titleEditor = dialog.findViewById( R.id.edit_mode_title );
        widthEditor = dialog.findViewById( R.id.edit_mode_width );
        heightEditor = dialog.findViewById( R.id.edit_mode_height );
        minesEditor = dialog.findViewById( R.id.edit_mode_mines );
        error = dialog.findViewById( R.id.edit_mode_error );

        titleEditor.setText( title );
        widthEditor.setText( getContext().getString( R.string.simple_decimal_format, width ) );
        heightEditor.setText( getContext().getString( R.string.simple_decimal_format, height ) );
        minesEditor.setText( getContext().getString( R.string.simple_decimal_format, mines ) );



        saveButton = dialog.getButton( Dialog.BUTTON_POSITIVE );

        titleEditor.addTextChangedListener( this );
        widthEditor.addTextChangedListener( this );
        heightEditor.addTextChangedListener( this );
        minesEditor.addTextChangedListener( this );

        checkError();

        return dialog;
    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
        checkError();
    }

    @Override
    public void afterTextChanged( Editable s ) {

    }
}
