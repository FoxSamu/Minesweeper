package net.rgsw.minesweeper.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

public class SignatureUtil {
    public static final int VALID = 0;
    public static final int INVALID = 1;
    public static final int ERROR = 2;

    private static final String SIGNATURE = "BNo0kCB4e9nst035TGZs9Yt5zc8=";


    @SuppressLint( "PackageManagerGetSignatures" )
    public static int checkAppSignature( Context context ) {

        try {

            // There was a security leak here until android 4.4, it's solved in android 5 so we can safely do this
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo( context.getPackageName(), PackageManager.GET_SIGNATURES );

            for( Signature signature : packageInfo.signatures ) {

                MessageDigest md = MessageDigest.getInstance( "SHA" );

                md.update( signature.toByteArray() );

                final String currentSignature = Base64.encodeToString( md.digest(), Base64.DEFAULT );

                Log.i( "SweeperSignatureUtil", "App has signature " + currentSignature + ", needs " + SIGNATURE );

                // Compare signatures, trim because some whitespaces are added somehow
                if( currentSignature != null && currentSignature.trim().equals( SIGNATURE ) ) {
                    Log.i( "SweeperSignatureUtil", "Signature is valid... We're safe!" );
                    return VALID;
                }

            }

        } catch( Exception e ) {
            Log.e( "SweeperSignatureUtil", "Failed checking signatures", e );
            return ERROR;
        }

        return INVALID;

    }



    private static final String PLAY_STORE_APP_ID = "com.android.vending";

    public static boolean verifyInstaller( final Context context ) {

        final String installer = context.getPackageManager().getInstallerPackageName( context.getPackageName() );

        Log.i( "SweeperSignatureUtil", "Installed from " + installer );

        return installer != null && installer.startsWith( PLAY_STORE_APP_ID );

    }
}

/*

I/SweeperSignatureUtil: App has needs signature BNo0kCB4e9nst035TGZs9Yt5zc8=
I/SweeperSignatureUtil: App has signature       BNo0kCB4e9nst035TGZs9Yt5zc8=
 */