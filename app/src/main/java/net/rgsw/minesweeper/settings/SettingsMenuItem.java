package net.rgsw.minesweeper.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.*;

public class SettingsMenuItem implements MenuItem {

    private CharSequence title;
    private CharSequence titleCondensed;
    private final Context ctx;
    private int id;
    private int groupId;
    private int order;
    private Drawable icon;
    private Intent intent;
    private final char[] shortcut = new char[ 2 ];
    private boolean checkable;
    private boolean exclusiveCheckable;
    private boolean checked;
    private boolean visible;
    private boolean enabled;
    private boolean actionViewExpanded;
    private SubMenu subMenu;
    private OnMenuItemClickListener menuItemClickListener;
    private OnActionExpandListener actionExpandListener;
    private int showAsActionFlags;
    private View actionView;
    private ActionProvider actionProvider;

    public SettingsMenuItem( Context context ) {
        this.ctx = context;
    }

    public SettingsMenuItem( Context ctx, CharSequence title ) {
        this( ctx );
        this.title = title;
    }

    public SettingsMenuItem( Context ctx, int title ) {
        this( ctx );
        this.title = ctx.getString( title );
    }

    @Override
    public int getItemId() {
        return id;
    }

    @Override
    public int getGroupId() {
        return groupId;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public MenuItem setTitle( CharSequence title ) {
        this.title = title;
        return this;
    }

    @Override
    public MenuItem setTitle( int title ) {
        this.title = ctx.getString( title );
        return this;
    }

    @Override
    public CharSequence getTitle() {
        return this.title;
    }

    @Override
    public MenuItem setTitleCondensed( CharSequence title ) {
        this.titleCondensed = title;
        return this;
    }

    @Override
    public CharSequence getTitleCondensed() {
        return titleCondensed;
    }

    @Override
    public MenuItem setIcon( Drawable icon ) {
        this.icon = icon;
        return this;
    }

    @Override
    public MenuItem setIcon( int iconRes ) {
        return setIcon( ctx.getDrawable( iconRes ) );
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public MenuItem setIntent( Intent intent ) {
        this.intent = intent;
        return this;
    }

    @Override
    public Intent getIntent() {
        return this.intent;
    }

    @Override
    public MenuItem setShortcut( char numericChar, char alphaChar ) {
        shortcut[ 0 ] = numericChar;
        shortcut[ 1 ] = alphaChar;
        return this;
    }

    @Override
    public MenuItem setNumericShortcut( char numericChar ) {
        shortcut[ 0 ] = numericChar;
        return this;
    }

    @Override
    public char getNumericShortcut() {
        return shortcut[ 0 ];
    }

    @Override
    public MenuItem setAlphabeticShortcut( char alphaChar ) {
        shortcut[ 1 ] = alphaChar;
        return this;
    }

    @Override
    public char getAlphabeticShortcut() {
        return shortcut[ 1 ];
    }

    @Override
    public MenuItem setCheckable( boolean checkable ) {
        this.checkable = checkable;
        return this;
    }

    @Override
    public boolean isCheckable() {
        return checkable;
    }

    @Override
    public MenuItem setChecked( boolean checked ) {
        this.checked = checked;
        return this;
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public MenuItem setVisible( boolean visible ) {
        this.visible = visible;
        return this;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public MenuItem setEnabled( boolean enabled ) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean hasSubMenu() {
        return subMenu != null;
    }

    @Override
    public SubMenu getSubMenu() {
        return subMenu;
    }

    @Override
    public MenuItem setOnMenuItemClickListener( OnMenuItemClickListener menuItemClickListener ) {
        this.menuItemClickListener = menuItemClickListener;
        return this;
    }

    @Override
    public ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override
    public void setShowAsAction( int actionEnum ) {
        showAsActionFlags = actionEnum;
    }

    @Override
    public MenuItem setShowAsActionFlags( int actionEnum ) {
        showAsActionFlags = actionEnum;
        return this;
    }

    @Override
    public MenuItem setActionView( View view ) {
        actionView = view;
        return this;
    }

    @Override
    public MenuItem setActionView( int resId ) {
        LayoutInflater inflater = LayoutInflater.from( ctx );
        setActionView( inflater.inflate( resId, null ) );
        return this;
    }

    @Override
    public View getActionView() {
        return actionView;
    }

    @Override
    public MenuItem setActionProvider( ActionProvider actionProvider ) {
        this.actionProvider = actionProvider;
        return this;
    }

    @Override
    public ActionProvider getActionProvider() {
        return this.actionProvider;
    }

    @Override
    public boolean expandActionView() {
        actionViewExpanded = true;
        if( actionExpandListener != null ) return actionExpandListener.onMenuItemActionExpand( this );
        return true;
    }

    @Override
    public boolean collapseActionView() {
        actionViewExpanded = false;
        if( actionExpandListener != null ) return actionExpandListener.onMenuItemActionCollapse( this );
        return true;
    }

    @Override
    public boolean isActionViewExpanded() {
        return actionViewExpanded;
    }

    @Override
    public MenuItem setOnActionExpandListener( OnActionExpandListener listener ) {
        actionExpandListener = listener;
        return this;
    }

    public MenuItem setExclusiveCheckable( boolean exclusiveCheckable ) {
        this.exclusiveCheckable = exclusiveCheckable;
        return this;
    }

    public boolean isExclusiveCheckable() {
        return exclusiveCheckable;
    }

    SettingsMenuItem setOrder( int order ) {
        this.order = order;
        return this;
    }

    SettingsMenuItem setId( int id ) {
        this.id = id;
        return this;
    }

    SettingsMenuItem setGroupId( int groupId ) {
        this.groupId = groupId;
        return this;
    }

    SettingsMenuItem setSubMenu( SubMenu menu ) {
        this.subMenu = menu;
        return this;
    }
}
