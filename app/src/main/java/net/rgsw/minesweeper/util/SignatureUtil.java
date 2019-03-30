package net.rgsw.minesweeper.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

public class SignatureUtil {
    private static final int VALID = 0;

    private static final int INVALID = 1;

    private static final String SIGNATURE = "tMs4s+ylx5lA7m5N2PknZn9OMfg=";


    public static int checkAppSignature( Context context ) {

        try {

            PackageInfo packageInfo = context.getPackageManager().getPackageInfo( context.getPackageName(), PackageManager.GET_SIGNATURES );

            for( Signature signature : packageInfo.signatures ) {

                byte[] signatureBytes = signature.toByteArray();

                MessageDigest md = MessageDigest.getInstance( "SHA" );

                md.update( signature.toByteArray() );

                final String currentSignature = Base64.encodeToString( md.digest(), Base64.DEFAULT );

                Log.d( "REMOVE_ME", "Include this string as a value for SIGNATURE:" + currentSignature );

                // Compare signatures
                if( SIGNATURE.equals( currentSignature ) ) {
                    return VALID;
                }

            }

        } catch( Exception e ) {
        }

        return INVALID;

    }



    private static final String PLAY_STORE_APP_ID = "com.android.vending";

    public static boolean verifyInstaller( final Context context ) {

        final String installer = context.getPackageManager().getInstallerPackageName( context.getPackageName() );

        return installer != null && installer.startsWith( PLAY_STORE_APP_ID );

    }
}
