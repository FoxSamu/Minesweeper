package net.rgsw.minesweeper.settings;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SettingsAdapter extends BaseAdapter {

    private final SettingsEntry[] entries;

    public SettingsAdapter( SettingsEntry... entries ) {
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.length;
    }

    @Override
    public Object getItem( int position ) {
        return entries[ position ];
    }

    @Override
    public long getItemId( int position ) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        return entries[ position ].getView();
    }

}
