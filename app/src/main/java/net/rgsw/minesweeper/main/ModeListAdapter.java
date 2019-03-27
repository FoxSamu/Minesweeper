package net.rgsw.minesweeper.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import net.rgsw.ctable.tag.TagList;
import net.rgsw.ctable.tag.TagStringCompound;
import net.rgsw.minesweeper.R;

import java.util.ArrayList;

public class ModeListAdapter extends BaseAdapter {
    private final ArrayList<Mode> entries = new ArrayList<>();
    private final LayoutInflater inflater;
    private final MainActivity ctx;
    private final ListView view;
    private OnOptionsListener optionsListener;
    private OnPressListener pressListener;

    public ModeListAdapter( MainActivity ctx, ListView view ) {
        this.ctx = ctx;
        inflater = ( LayoutInflater ) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.view = view;
    }

    public void addItem( Mode entry ) {
        entries.add( entry );
    }

    public void addItem( Mode entry, int idx ) {
        entries.add( idx, entry );
    }

    public void setItem( Mode entry, int idx ) {
        entries.set( idx, entry );
    }

    public void removeItem( Mode entry ) {
        entries.remove( entry );
    }

    public void setOnOptionsListener( OnOptionsListener listener ) {
        this.optionsListener = listener;
    }

    public void setOnPressListener( OnPressListener pressListener ) {
        this.pressListener = pressListener;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem( int position ) {
        return entries.get( position );
    }

    @Override
    public long getItemId( int position ) {
        return position;
    }

    @Override
    public View getView( final int position, final View convertView, final ViewGroup parent ) {
        View v;
        if( convertView == null ) {
            v = inflater.inflate( R.layout.mode_item, parent, false );
        } else {
            v = convertView;
        }
        this.entries.get( position ).setView( v );
        v.findViewById( R.id.mode_options ).setOnClickListener(
                view1 -> {if( optionsListener != null ) optionsListener.onOptions( view1, v, position, this, false );}
        );
        v.findViewById( R.id.mode_play ).setOnClickListener(
                view1 -> {if( optionsListener != null ) optionsListener.onOptions( view1, v, position, this, true );}
        );
        v.findViewById( R.id.mode_play ).setOnLongClickListener( ctx::showTooltip );
        v.findViewById( R.id.mode_options ).setOnLongClickListener( ctx::showTooltip );
        v.findViewById( R.id.clickable_view ).setOnClickListener(
                view1 -> {if( pressListener != null ) pressListener.onPress( view1, v, position, this );}
        );
        return v;
    }

    public void save( TagList list ) {
        for( Mode m : entries ) {
            TagStringCompound cpd = new TagStringCompound();
            m.save( cpd );
            list.add( cpd );
        }
    }

    public void load( TagList list ) {
        this.entries.clear();
        for( int i = 0; i < list.size(); i++ ) {
            TagStringCompound cpd = list.getTagStringCompound( i );
            this.entries.add( new Mode( cpd ) );
        }
    }

    public interface OnOptionsListener {
        void onOptions( View button, View list, int position, ModeListAdapter adapter, boolean play );
    }

    public interface OnPressListener {
        void onPress( View button, View list, int position, ModeListAdapter adapter );
    }
}
