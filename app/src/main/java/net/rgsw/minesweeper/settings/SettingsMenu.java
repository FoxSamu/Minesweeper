package net.rgsw.minesweeper.settings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu implements Menu {

    private final ArrayList<MenuItem> items = new ArrayList<>();
    private final ArrayList<SubMenu> subMenus = new ArrayList<>();

    private final Context ctx;

    public SettingsMenu( Context ctx ) {
        this.ctx = ctx;
    }

    @Override
    public MenuItem add( CharSequence title ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, title );
        items.add( item );
        return item;
    }

    @Override
    public MenuItem add( int titleRes ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, titleRes );
        items.add( item );
        return item;
    }

    @Override
    public MenuItem add( int groupId, int itemId, int order, CharSequence title ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, title ).setGroupId( groupId ).setId( itemId ).setOrder( order );
        items.add( item );
        return item;
    }

    @Override
    public MenuItem add( int groupId, int itemId, int order, int titleRes ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, titleRes ).setGroupId( groupId ).setId( itemId ).setOrder( order );
        items.add( item );
        return item;
    }

    @Override
    public SubMenu addSubMenu( CharSequence title ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, title );
        items.add( item );
        SettingsSubMenu subMenu = new SettingsSubMenu( ctx, item, title );
        subMenus.add( subMenu );
        return subMenu;
    }

    @Override
    public SubMenu addSubMenu( int titleRes ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, titleRes );
        items.add( item );
        SettingsSubMenu subMenu = new SettingsSubMenu( ctx, item, titleRes );
        subMenus.add( subMenu );
        return subMenu;
    }

    @Override
    public SubMenu addSubMenu( int groupId, int itemId, int order, CharSequence title ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, title ).setGroupId( groupId ).setId( itemId ).setOrder( order );
        items.add( item );
        SettingsSubMenu subMenu = new SettingsSubMenu( ctx, item, title );
        subMenus.add( subMenu );
        return subMenu;
    }

    @Override
    public SubMenu addSubMenu( int groupId, int itemId, int order, int titleRes ) {
        SettingsMenuItem item = new SettingsMenuItem( ctx, titleRes ).setGroupId( groupId ).setId( itemId ).setOrder( order );
        items.add( item );
        SettingsSubMenu subMenu = new SettingsSubMenu( ctx, item, titleRes );
        subMenus.add( subMenu );
        return subMenu;
    }

    @Override
    public int addIntentOptions( int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems ) {
        PackageManager pm = this.ctx.getPackageManager();
        List<ResolveInfo> lri = pm.queryIntentActivityOptions( caller, specifics, intent, 0 );
        int N = lri != null ? lri.size() : 0;
        if( ( flags & 1 ) == 0 ) {
            this.removeGroup( groupId );
        }

        for( int i = 0; i < N; ++i ) {
            ResolveInfo ri = lri.get( i );
            Intent rintent = new Intent( ri.specificIndex < 0 ? intent : specifics[ ri.specificIndex ] );
            rintent.setComponent( new ComponentName( ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name ) );
            MenuItem item = this.add( groupId, itemId, order, ri.loadLabel( pm ) ).setIcon( ri.loadIcon( pm ) ).setIntent( rintent );
            if( outSpecificItems != null && ri.specificIndex >= 0 ) {
                outSpecificItems[ ri.specificIndex ] = item;
            }
        }

        return N;
    }

    private void remove( MenuItem item ) {
        if( item.hasSubMenu() ) {
            this.subMenus.remove( item.getSubMenu() );
        }
        this.items.remove( item );
    }

    @Override
    public void removeItem( int id ) {
        if( id < 0 ) return;
        for( MenuItem item : this.items ) {
            if( item.getItemId() == id ) {
                remove( item );
            }
        }
    }

    @Override
    public void removeGroup( int id ) {
        if( id < 0 ) return;
        for( MenuItem item : this.items ) {
            if( item.getGroupId() == id ) {
                remove( item );
            }
        }
    }

    @Override
    public void clear() {
        items.clear();
        subMenus.clear();
    }

    @Override
    public void setGroupCheckable( int id, boolean checkable, boolean exclusive ) {
        if( id < 0 ) return;
        for( MenuItem item : this.items ) {
            if( item.getGroupId() == id ) {
                item.setCheckable( checkable );
                ( ( SettingsMenuItem ) item ).setExclusiveCheckable( checkable );
            }
        }
    }

    @Override
    public void setGroupVisible( int id, boolean visible ) {
        if( id < 0 ) return;
        for( MenuItem item : this.items ) {
            if( item.getGroupId() == id ) {
                item.setVisible( visible );
            }
        }
    }

    @Override
    public void setGroupEnabled( int id, boolean enabled ) {
        if( id < 0 ) return;
        for( MenuItem item : this.items ) {
            if( item.getGroupId() == id ) {
                item.setEnabled( enabled );
            }
        }
    }

    @Override
    public boolean hasVisibleItems() {
        for( MenuItem item : this.items ) {
            if( item.isVisible() ) return true;
        }
        return false;
    }

    @Override
    public MenuItem findItem( int id ) {
        if( id < 0 ) return null;
        for( MenuItem item : this.items ) {
            if( item.getItemId() == id ) {
                return item;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public MenuItem getItem( int index ) {
        return items.get( index );
    }

    @Override
    public void close() {

    }


    public boolean performShortcut( int keyCode, KeyEvent event, int flags ) {
        return false;
    }

    @Override
    public boolean isShortcutKey( int keyCode, KeyEvent event ) {
        return false;
    }

    @Override
    public boolean performIdentifierAction( int id, int flags ) {
        return false;
    }

    @Override
    public void setQwertyMode( boolean isQwerty ) {

    }

    public Context getContext() {
        return ctx;
    }
}
