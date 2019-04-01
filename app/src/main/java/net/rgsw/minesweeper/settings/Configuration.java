package net.rgsw.minesweeper.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class Configuration {
    // Appearance
    public static final Setting<Boolean> useDarkTheme = new Setting<>( false, false );
    public static final Setting<Integer> gameCellSize = new Setting<>( false, 40 );
    public static final Setting<Integer> gameIconSize = new Setting<>( false, 32 );
    public static final Setting<Integer> gameCornerRadius = new Setting<>( false, 4 );
    public static final Setting<Boolean> checkerboardGrid = new Setting<>( false, true );

    // Behavior & Controls
    public static final Setting<EModeButtonPos> modeButtonPos = new Setting<>( false, EModeButtonPos.RIGHT );
    public static final Setting<Boolean> markTappedMine = new Setting<>( false, true );
    public static final Setting<ENumberTapBehavior> numberTapBehavior = new Setting<>( false, ENumberTapBehavior.REVEAL );
    public static final Setting<ELongPressBehavior> longPressBehavior = new Setting<>( false, ELongPressBehavior.FLAG_DIG );
    public static final Setting<EWinPolicy> winCondition = new Setting<>( false, EWinPolicy.FLAG_OR_REVEALED );
    public static final Setting<EStartingPolicy> startingPolicy = new Setting<>( false, EStartingPolicy.FULL_ENSURANCE );
    public static final Setting<Boolean> vibration = new Setting<>( false, false );
    public static final Setting<EShowDialogOnEndBehavior> endDialogBehavior = new Setting<>( false, EShowDialogOnEndBehavior.ALWAYS );
    public static final Setting<Boolean> keepScreenOn = new Setting<>( false, false );
    public static final Setting<Boolean> disableFlagOverflowAroundNums = new Setting<>( false, false );
    public static final Setting<Boolean> disableFlagOverflowTotal = new Setting<>( false, true );

    // Hints
    public static final Setting<Boolean> showHintOption = new Setting<>( false, true );
    public static final Setting<Boolean> showInferredFlags = new Setting<>( false, false );
    public static final Setting<Boolean> showInferredDigs = new Setting<>( false, false );
    public static final Setting<Boolean> markTooManyFlags = new Setting<>( false, false );
    public static final Setting<Boolean> markCompletableNumbers = new Setting<>( false, false );
    public static final Setting<Boolean> markCompletedNumbers = new Setting<>( false, false );

    // Advanced
    public static final Setting<Integer> gameChunkSize = new Setting<>( false, 4 );

    private static final ArrayList<IChangeListener> globalListeners = new ArrayList<>();

    private static SharedPreferences prefs;
    private static boolean initialized;

    public static boolean isInitialized() {
        return initialized;
    }

    public static void init( Context ctx ) {
        if( initialized ) return;

        prefs = ctx.getSharedPreferences( "settings", Context.MODE_PRIVATE );
        load();
        initialized = true;
    }

    @SuppressLint( "ApplySharedPref" )
    private static void save() {
        prefs
                .edit()

                // Appearance
                .putBoolean( "useDarkTheme", useDarkTheme.getValue() )
                .putInt( "gameCellSize", gameCellSize.getValue() )
                .putInt( "gameIconSize", gameIconSize.getValue() )
                .putInt( "gameCornerRadius", gameCornerRadius.getValue() )
                .putBoolean( "checkerboardGrid", checkerboardGrid.getValue() )

                // Behavior & Controls
                .putInt( "modeButtonPos", modeButtonPos.getValue().ordinal() )
                .putBoolean( "markTappedMine", markTappedMine.getValue() )
                .putInt( "numberTapBehavior", numberTapBehavior.getValue().ordinal() )
                .putInt( "longPressBehavior", longPressBehavior.getValue().ordinal() )
                .putInt( "winCondition", winCondition.getValue().ordinal() )
                .putInt( "startingPolicy", startingPolicy.getValue().ordinal() )
                .putInt( "endDialogBehavior", endDialogBehavior.getValue().ordinal() )
                .putBoolean( "vibration", vibration.getValue() )
                .putBoolean( "keepScreenOn", keepScreenOn.getValue() )
                .putBoolean( "disableFlagOverflowAroundNums", disableFlagOverflowAroundNums.getValue() )
                .putBoolean( "disableFlagOverflowTotal", disableFlagOverflowTotal.getValue() )

                // Hints
                .putBoolean( "showHintOption", showHintOption.getValue() )
                .putBoolean( "showInferredFlags", showInferredFlags.getValue() )
                .putBoolean( "showInferredDigs", showInferredDigs.getValue() )
                .putBoolean( "markTooManyFlags", markTooManyFlags.getValue() )
                .putBoolean( "markCompletableNumbers", markCompletableNumbers.getValue() )
                .putBoolean( "markCompletedNumbers", markCompletedNumbers.getValue() )

                // Advanced
                .putInt( "gameChunkSize", gameChunkSize.getValue() )
                .commit()
        ;

    }

    private static void load() {
        // Appearance
        useDarkTheme.setValue( prefs.getBoolean( "useDarkTheme", false ) );
        gameCellSize.setValue( prefs.getInt( "gameCellSize", 40 ) );
        gameIconSize.setValue( prefs.getInt( "gameIconSize", 32 ) );
        gameCornerRadius.setValue( prefs.getInt( "gameCornerRadius", 4 ) );
        checkerboardGrid.setValue( prefs.getBoolean( "checkerboardGrid", true ) );

        // Behavior & Controls
        modeButtonPos.setValue( EModeButtonPos.values()[ prefs.getInt( "modeButtonPos", EModeButtonPos.RIGHT.ordinal() ) ] );
        markTappedMine.setValue( prefs.getBoolean( "markTappedMine", true ) );
        numberTapBehavior.setValue( ENumberTapBehavior.values()[ prefs.getInt( "numberTapBehavior", ENumberTapBehavior.REVEAL.ordinal() ) ] );
        longPressBehavior.setValue( ELongPressBehavior.values()[ prefs.getInt( "longPressBehavior", ELongPressBehavior.FLAG_DIG.ordinal() ) ] );
        winCondition.setValue( EWinPolicy.values()[ prefs.getInt( "winCondition", EWinPolicy.FLAG_OR_REVEALED.ordinal() ) ] );
        startingPolicy.setValue( EStartingPolicy.values()[ prefs.getInt( "startingPolicy", EStartingPolicy.FULL_ENSURANCE.ordinal() ) ] );
        endDialogBehavior.setValue( EShowDialogOnEndBehavior.values()[ prefs.getInt( "endDialogBehavior", EShowDialogOnEndBehavior.ALWAYS.ordinal() ) ] );
        vibration.setValue( prefs.getBoolean( "vibration", false ) );
        keepScreenOn.setValue( prefs.getBoolean( "keepScreenOn", false ) );
        disableFlagOverflowAroundNums.setValue( prefs.getBoolean( "disableFlagOverflowAroundNums", false ) );
        disableFlagOverflowTotal.setValue( prefs.getBoolean( "disableFlagOverflowTotal", true ) );

        // Hints
        showHintOption.setValue( prefs.getBoolean( "showHintOption", true ) );
        showInferredFlags.setValue( prefs.getBoolean( "showInferredFlags", false ) );
        showInferredDigs.setValue( prefs.getBoolean( "showInferredDigs", false ) );
        markTooManyFlags.setValue( prefs.getBoolean( "markTooManyFlags", false ) );
        markCompletableNumbers.setValue( prefs.getBoolean( "markCompletableNumbers", false ) );
        markCompletedNumbers.setValue( prefs.getBoolean( "markCompletedNumbers", false ) );

        // Advanced
        gameChunkSize.setValue( prefs.getInt( "gameChunkSize", 4 ) );
    }

    private static void onChange( Setting setting ) {
        if( initialized ) save();
        for( IChangeListener listener : globalListeners ) {
            listener.onSettingsChange( setting );
        }
    }

    public static void addSettingsChangeListener( IChangeListener listener ) {
        globalListeners.add( listener );
    }

    public static void removeSettingsChangeListener( IChangeListener listener ) {
        globalListeners.remove( listener );
    }

    public static class Setting <T> {
        private T value;
        private final boolean nullable;
        private final ArrayList<ISettingChangeListener<T>> listeners = new ArrayList<>();

        public Setting( boolean nullable, T value ) {
            if( !nullable && value == null ) throw new NullPointerException( "Value may not be null" );
            this.nullable = nullable;
            this.value = value;
        }

        public void setValue( T value ) {
            System.out.println( "Set value to " + value );
            if( !nullable && value == null ) throw new NullPointerException( "Value may not be null" );
            this.value = value;
            Configuration.onChange( this );
            this.onChange();
        }

        public T getValue() {
            return value;
        }

        public void addChangeListener( ISettingChangeListener<T> listener ) {
            listeners.add( listener );
        }

        public void removeChangeListener( ISettingChangeListener<T> listener ) {
            listeners.remove( listener );
        }

        private void onChange() {
            for( ISettingChangeListener<T> listener : listeners ) {
                listener.onSettingChange( this );
            }
        }
    }

    public interface IChangeListener {
        void onSettingsChange( Setting setting );
    }

    public interface ISettingChangeListener <T> {
        void onSettingChange( Setting<T> setting );
    }
}
