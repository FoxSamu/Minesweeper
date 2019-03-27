package net.rgsw.minesweeper.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

public class SettingsSubMenu extends SettingsMenu implements SubMenu {

    private final Context ctx;
    private CharSequence headerTitle;
    private Drawable headerIcon;
    private View headerView;
    private Drawable icon;
    private final MenuItem item;

    public SettingsSubMenu( Context ctx, MenuItem item ) {
        super( ctx );
        this.ctx = ctx;
        this.item = item;
    }

    public SettingsSubMenu( Context ctx, MenuItem item, CharSequence title ) {
        super( ctx );
        this.ctx = ctx;
        headerTitle = title;
        this.item = item;
    }

    public SettingsSubMenu( Context ctx, MenuItem item, int title ) {
        super( ctx );
        this.ctx = ctx;
        headerTitle = ctx.getString( title );
        this.item = item;
    }

    @Override
    public SubMenu setHeaderTitle( int titleRes ) {
        headerTitle = ctx.getString( titleRes );
        return this;
    }

    @Override
    public SubMenu setHeaderTitle( CharSequence title ) {
        headerTitle = title;
        return this;
    }

    @Override
    public SubMenu setHeaderIcon( int iconRes ) {
        return setHeaderIcon( ctx.getDrawable( iconRes ) );
    }

    @Override
    public SubMenu setHeaderIcon( Drawable icon ) {
        this.headerIcon = icon;
        return this;
    }

    @Override
    public SubMenu setHeaderView( View view ) {
        this.headerView = view;
        return this;
    }

    @Override
    public void clearHeader() {
        headerTitle = null;
        headerIcon = null;
        headerView = null;
    }

    @Override
    public SubMenu setIcon( int iconRes ) {
        return setIcon( ctx.getDrawable( iconRes ) );
    }

    @Override
    public SubMenu setIcon( Drawable icon ) {
        this.icon = icon;
        return this;
    }

    @Override
    public MenuItem getItem() {
        return item;
    }
}
