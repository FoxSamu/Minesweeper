package net.rgsw.minesweeper.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import net.rgsw.minesweeper.R;

public abstract class SettingsEntry <T extends SettingsEntry> {
    final String key;
    final Context ctx;
    private IChangeListener<T> changeListener;


    SettingsEntry( String key, Context ctx ) {
        this.key = key;
        this.ctx = ctx;
    }

    public abstract View getView();

    public void save( SharedPreferences.Editor editor ) {

    }

    public void load( SharedPreferences prefs ) {

    }

    public SettingsEntry setChangeListener( IChangeListener<T> changeListener ) {
        this.changeListener = changeListener;
        return this;
    }

    @SuppressWarnings( "unchecked" )
    void onChange() {
        if( changeListener != null ) changeListener.changed( ( T ) this );
    }

    public interface IChangeListener <T extends SettingsEntry> {
        void changed( T entry );
    }

    public static class BooleanEntry extends SettingsEntry<BooleanEntry> {
        private boolean value;
        private CharSequence title;
        private CharSequence desc = "";
        private final View view;
        private final CheckBox checkBox;
        private final TextView descView;

        public BooleanEntry( String key, Context ctx ) {
            super( key, ctx );
            LayoutInflater inflater = LayoutInflater.from( ctx );
            view = inflater.inflate( R.layout.settings_boolean, null, false );

            checkBox = view.findViewById( R.id.booleanValue );
            descView = view.findViewById( R.id.desc );
            checkBox.setChecked( value );
            updateDesc( desc );
            checkBox.setText( title );
            checkBox.setOnCheckedChangeListener( this::onChange );

            view.setOnClickListener( this::clickEntry );
        }

        private void updateDesc( CharSequence desc ) {
            if( desc == null || desc.length() == 0 ) {
                descView.setVisibility( View.GONE );
            } else {
                descView.setVisibility( View.VISIBLE );
                descView.setText( desc );
            }
        }

        protected void onChange( CompoundButton btn, boolean checked ) {
            value = checked;
            onChange();
        }

        private void clickEntry( View v ) {
            checkBox.toggle();
        }

        @Override
        public View getView() {
            return view;
        }

        public BooleanEntry setValue( boolean value ) {
            this.value = value;
            checkBox.setChecked( value );
            return this;
        }

        public boolean getValue() {
            return value;
        }

        public BooleanEntry setTitle( CharSequence title ) {
            this.title = title;
            checkBox.setText( title );
            return this;
        }

        public BooleanEntry setTitle( int resTitle ) {
            return setTitle( ctx.getString( resTitle ) );
        }

        public CharSequence getTitle() {
            return title;
        }

        public BooleanEntry setDesc( CharSequence desc ) {
            this.desc = desc;
            updateDesc( desc );
            return this;
        }

        public CharSequence getDesc() {
            return desc;
        }

        public BooleanEntry setDesc( int resDesc ) {
            return setDesc( ctx.getString( resDesc ) );
        }

        public BooleanEntry setEnabled( boolean enabled ) {
            checkBox.setEnabled( enabled );
            return this;
        }

        public boolean isEnabled() {
            return checkBox.isEnabled();
        }

        @Override
        public void save( SharedPreferences.Editor editor ) {
            editor.putBoolean( key, value );
        }

        @Override
        public void load( SharedPreferences prefs ) {
            setValue( prefs.getBoolean( key, false ) );
        }
    }

    public static BooleanEntry bool( Context ctx, String key, int resTitle, Integer resDesc, boolean defaultValue, IChangeListener<BooleanEntry> listener ) {
        BooleanEntry entry = new BooleanEntry( key, ctx ).setTitle( resTitle ).setValue( defaultValue );
        if( resDesc != null ) entry.setDesc( resDesc );
        entry.setChangeListener( listener );
        return entry;
    }


    public static class SwitchEntry extends SettingsEntry<SwitchEntry> {
        private boolean value;
        private CharSequence title;
        private CharSequence desc = "";
        private final View view;
        private final Switch switchView;
        private final TextView descView;

        public SwitchEntry( String key, Context ctx ) {
            super( key, ctx );
            LayoutInflater inflater = LayoutInflater.from( ctx );
            view = inflater.inflate( R.layout.settings_switch, null, false );

            switchView = view.findViewById( R.id.switchValue );
            descView = view.findViewById( R.id.desc );
            switchView.setChecked( value );
            updateDesc( desc );
            switchView.setText( title );
            switchView.setOnCheckedChangeListener( this::onChange );

            view.setOnClickListener( this::clickEntry );
        }

        private void updateDesc( CharSequence desc ) {
            if( desc == null || desc.length() == 0 ) {
                descView.setVisibility( View.GONE );
            } else {
                descView.setVisibility( View.VISIBLE );
                descView.setText( desc );
            }
        }

        protected void onChange( CompoundButton btn, boolean checked ) {
            value = checked;
            onChange();
        }

        private void clickEntry( View v ) {
            switchView.toggle();
        }

        @Override
        public View getView() {
            return view;
        }

        public SwitchEntry setValue( boolean value ) {
            this.value = value;
            switchView.setChecked( value );
            return this;
        }

        public boolean getValue() {
            return value;
        }

        public SwitchEntry setTitle( CharSequence title ) {
            this.title = title;
            switchView.setText( title );
            return this;
        }

        public SwitchEntry setTitle( int resTitle ) {
            return setTitle( ctx.getString( resTitle ) );
        }

        public CharSequence getTitle() {
            return title;
        }

        public SwitchEntry setDesc( CharSequence desc ) {
            this.desc = desc;
            updateDesc( desc );
            return this;
        }

        public CharSequence getDesc() {
            return desc;
        }

        public SwitchEntry setDesc( int resDesc ) {
            return setDesc( ctx.getString( resDesc ) );
        }

        public SwitchEntry setEnabled( boolean enabled ) {
            switchView.setEnabled( enabled );
            return this;
        }

        public boolean isEnabled() {
            return switchView.isEnabled();
        }

        @Override
        public void save( SharedPreferences.Editor editor ) {
            editor.putBoolean( key, value );
        }

        @Override
        public void load( SharedPreferences prefs ) {
            setValue( prefs.getBoolean( key, false ) );
        }
    }

    public static SwitchEntry toggle( Context ctx, String key, int resTitle, Integer resDesc, boolean defaultValue, IChangeListener<SwitchEntry> listener ) {
        SwitchEntry entry = new SwitchEntry( key, ctx ).setTitle( resTitle ).setValue( defaultValue );
        if( resDesc != null ) entry.setDesc( resDesc );
        entry.setChangeListener( listener );
        return entry;
    }


    public static class HeaderEntry extends SettingsEntry<HeaderEntry> {
        private CharSequence title;
        private final View view;
        private final TextView headerView;

        public HeaderEntry( String key, Context ctx ) {
            super( key, ctx );
            LayoutInflater inflater = LayoutInflater.from( ctx );
            view = inflater.inflate( R.layout.setting_header, null, false );

            headerView = view.findViewById( R.id.header );
            headerView.setText( title );
        }

        @Override
        public View getView() {
            return view;
        }

        public HeaderEntry setTitle( CharSequence title ) {
            this.title = title;
            headerView.setText( title );
            return this;
        }

        public HeaderEntry setTitle( int resTitle ) {
            return setTitle( ctx.getString( resTitle ) );
        }

        public CharSequence getTitle() {
            return title;
        }
    }

    public static HeaderEntry header( Context ctx, String key, int resTitle ) {
        return new HeaderEntry( key, ctx ).setTitle( resTitle );
    }


    public static class SpinnerEntry <T> extends SettingsEntry<SpinnerEntry<T>> {
        private int value;
        private CharSequence title;
        private CharSequence desc = "";
        private final View view;
        private final Spinner spinner;
        private final TextView descView;
        private final TextView titleView;
        private final ISettingsEnum<T> values;

        public SpinnerEntry( String key, Context ctx, ISettingsEnum<T> values ) {
            super( key, ctx );
            this.values = values;
            LayoutInflater inflater = LayoutInflater.from( ctx );
            view = inflater.inflate( R.layout.settings_enum, null, false );

            spinner = view.findViewById( R.id.spinner );
            descView = view.findViewById( R.id.desc );
            titleView = view.findViewById( R.id.title );
            spinner.setAdapter( new Adapter() );
            updateDesc( desc );
            titleView.setText( title );
            spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected( AdapterView<?> parent, View view, int position, long id ) {
                    onChange( parent, view, position, id );
                }

                @Override
                public void onNothingSelected( AdapterView<?> parent ) {
                    setValue( 0 );
                }
            } );

            view.setOnClickListener( this::clickEntry );
        }

        private void updateDesc( CharSequence desc ) {
            if( desc == null || desc.length() == 0 ) {
                descView.setVisibility( View.GONE );
            } else {
                descView.setVisibility( View.VISIBLE );
                descView.setText( desc );
            }
        }

        protected void onChange( AdapterView<?> parent, View view, int position, long id ) {
            value = position;
            onChange();
        }

        private void clickEntry( View v ) {
            spinner.performClick();
        }

        @Override
        public View getView() {
            return view;
        }

        public SpinnerEntry<T> setValue( T value ) {
            this.value = values.getIndex( value );
            spinner.setSelection( this.value );
            return this;
        }

        public SpinnerEntry<T> setValue( int index ) {
            this.value = index;
            spinner.setSelection( this.value );
            return this;
        }

        public T getValue() {
            return values.getValue( value );
        }

        public SpinnerEntry<T> setTitle( CharSequence title ) {
            this.title = title;
            titleView.setText( title );
            return this;
        }

        public SpinnerEntry<T> setTitle( int resTitle ) {
            return setTitle( ctx.getString( resTitle ) );
        }

        public CharSequence getTitle() {
            return title;
        }

        public SpinnerEntry<T> setDesc( CharSequence desc ) {
            this.desc = desc;
            updateDesc( desc );
            return this;
        }

        public CharSequence getDesc() {
            return desc;
        }

        public SpinnerEntry<T> setDesc( int resDesc ) {
            return setDesc( ctx.getString( resDesc ) );
        }

        public SpinnerEntry<T> setEnabled( boolean enabled ) {
            spinner.setEnabled( enabled );
            return this;
        }

        public boolean isEnabled() {
            return spinner.isEnabled();
        }

        @Override
        public void save( SharedPreferences.Editor editor ) {
            editor.putInt( key, value );
        }

        @Override
        public void load( SharedPreferences prefs ) {
            setValue( prefs.getInt( key, 0 ) );
        }

        private class Adapter extends BaseAdapter {

            @Override
            public int getCount() {
                return values.getCount();
            }

            @Override
            public Object getItem( int position ) {
                return values.getValue( position );
            }

            @Override
            public long getItemId( int position ) {
                return position;
            }

            private int toPx( int dp ) {
                return Math.round( dp * ( ctx.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT ) );
            }

            @Override
            public View getView( int position, View convertView, ViewGroup parent ) {
                if( convertView != null ) return convertView;
                TextView t = new TextView( ctx );
                t.setText( values.getDisplayNameRes( position ) );
                int padding = toPx( 8 );
                t.setPadding( padding, padding, padding, padding );
                return t;
            }
        }
    }

    public static <T> SpinnerEntry<T> spinner( Context ctx, String key, int resTitle, Integer resDesc, T defaultValue, IChangeListener<SpinnerEntry<T>> listener, ISettingsEnum<T> values ) {
        SpinnerEntry<T> entry = new SpinnerEntry<>( key, ctx, values ).setTitle( resTitle ).setValue( defaultValue );
        if( resDesc != null ) entry.setDesc( resDesc );
        entry.setChangeListener( listener );
        return entry;
    }


    public static class SliderEntry extends SettingsEntry<SliderEntry> {
        private int value;
        private CharSequence title;
        private CharSequence desc = "";
        private final View view;
        private final SeekBar slider;
        private final TextView descView;
        private final TextView titleView;
        private final TextView valueView;
        private final int min;
        private final int max;
        private String format = "%d";

        public SliderEntry( String key, Context ctx, int min, int max ) {
            super( key, ctx );
            this.min = min;
            this.max = max;
            LayoutInflater inflater = LayoutInflater.from( ctx );
            view = inflater.inflate( R.layout.settings_slider, null, false );

            slider = view.findViewById( R.id.slider );
            descView = view.findViewById( R.id.desc );
            titleView = view.findViewById( R.id.title );
            valueView = view.findViewById( R.id.value );
            slider.setMax( max - min );
            slider.setProgress( value - min );
            updateDesc( desc );
            titleView.setText( title );
            valueView.setText( title );
            slider.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
                    onChange( progress );
                }

                @Override
                public void onStartTrackingTouch( SeekBar seekBar ) {

                }

                @Override
                public void onStopTrackingTouch( SeekBar seekBar ) {

                }
            } );
        }

        private void updateDesc( CharSequence desc ) {
            if( desc == null || desc.length() == 0 ) {
                descView.setVisibility( View.GONE );
            } else {
                descView.setVisibility( View.VISIBLE );
                descView.setText( desc );
            }
        }

        protected void onChange( int progress ) {
            value = progress + min;
            valueView.setText( String.format( format, value ) );
            onChange();
        }

        @Override
        public View getView() {
            return view;
        }

        public SliderEntry setValue( int value ) {
            this.value = Math.max( min, Math.min( max, value ) );
            slider.setProgress( value - min );
            valueView.setText( String.format( format, value ) );
            return this;
        }

        public int getValue() {
            return value;
        }

        public SliderEntry setTitle( CharSequence title ) {
            this.title = title;
            titleView.setText( title );
            return this;
        }

        public SliderEntry setTitle( int resTitle ) {
            return setTitle( ctx.getString( resTitle ) );
        }

        public CharSequence getTitle() {
            return title;
        }

        public SliderEntry setFormat( String format ) {
            this.format = format;
            valueView.setText( String.format( format, value ) );
            return this;
        }

        public String getFormat() {
            return format;
        }

        public SliderEntry setFormat( int resDesc ) {
            return setFormat( ctx.getString( resDesc ) );
        }

        public SliderEntry setDesc( CharSequence desc ) {
            this.desc = desc;
            updateDesc( desc );
            return this;
        }

        public CharSequence getDesc() {
            return desc;
        }

        public SliderEntry setDesc( int resDesc ) {
            return setDesc( ctx.getString( resDesc ) );
        }

        public SliderEntry setEnabled( boolean enabled ) {
            slider.setEnabled( enabled );
            return this;
        }

        public boolean isEnabled() {
            return slider.isEnabled();
        }
    }

    public static SliderEntry slider( Context ctx, String key, int resTitle, Integer resDesc, Integer resFormat, int min, int max, int defaultValue, IChangeListener<SliderEntry> listener ) {
        SliderEntry entry = new SliderEntry( key, ctx, min, max ).setTitle( resTitle ).setValue( defaultValue );
        if( resDesc != null ) entry.setDesc( resDesc );
        if( resFormat != null ) entry.setFormat( resFormat );
        entry.setChangeListener( listener );
        return entry;
    }


    public static class ActionEntry extends SettingsEntry<ActionEntry> {
        private CharSequence title;
        private CharSequence desc = "";
        private final View view;
        private final TextView descView;
        private final TextView titleView;

        public ActionEntry( String key, Context ctx ) {
            super( key, ctx );
            LayoutInflater inflater = LayoutInflater.from( ctx );
            view = inflater.inflate( R.layout.settings_action, null, false );

            descView = view.findViewById( R.id.desc );
            titleView = view.findViewById( R.id.title );
            updateDesc( desc );
            titleView.setText( title );

            view.setOnClickListener( this::click );
        }

        private void click( View v ) {
            onChange();
        }

        private void updateDesc( CharSequence desc ) {
            if( desc == null || desc.length() == 0 ) {
                descView.setVisibility( View.GONE );
            } else {
                descView.setVisibility( View.VISIBLE );
                descView.setText( desc );
            }
        }

        @Override
        public View getView() {
            return view;
        }

        public ActionEntry setTitle( CharSequence title ) {
            this.title = title;
            titleView.setText( title );
            return this;
        }

        public ActionEntry setTitle( int resTitle ) {
            return setTitle( ctx.getString( resTitle ) );
        }

        public CharSequence getTitle() {
            return title;
        }

        public ActionEntry setDesc( CharSequence desc ) {
            this.desc = desc;
            updateDesc( desc );
            return this;
        }

        public CharSequence getDesc() {
            return desc;
        }

        public ActionEntry setDesc( int resDesc ) {
            return setDesc( ctx.getString( resDesc ) );
        }
    }

    public static ActionEntry action( Context ctx, String key, int resTitle, Integer resDesc, IChangeListener<ActionEntry> listener ) {
        ActionEntry entry = new ActionEntry( key, ctx ).setTitle( resTitle );
        if( resDesc != null ) entry.setDesc( resDesc );
        entry.setChangeListener( listener );
        return entry;
    }

}
