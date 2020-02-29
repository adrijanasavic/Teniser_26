package com.example.teniseri_26.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.teniseri_26.R;
import com.example.teniseri_26.tools.ReviewerTools;


public class SimpleReceiver extends BroadcastReceiver {

    private static int notificationID = 1;

    private void readFileAndFillList(Context context) {
        String[] lista = ReviewerTools.readFromFile( context, "myfile.txt" ).split( "\n" );

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );

        boolean showMessage = sharedPreferences.getBoolean( context.getString( R.string.allow_message ), false );

        if (showMessage) {
            if (intent.getAction().equals( "SYNC_DATA" )) {
                int resultCode = intent.getExtras().getInt( "RESULT_CODE" );
                prepareNotification( resultCode, context, 0, null );
                readFileAndFillList( context );

            } else if (intent.getAction().equals( "MY_COMMENT" )) {
                int resultCode = intent.getExtras().getInt( "RESULT_CODE" );
                prepareNotification( resultCode, context, 2, intent.getExtras() );

            }

        }
    }

    private void prepareNotification(int resultCode, Context context, int notifID, Bundle bundle) {
        NotificationManager notifManager = (NotificationManager) context.getSystemService( Context.NOTIFICATION_SERVICE );
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder( context );

        Bitmap bm = BitmapFactory.decodeResource( context.getResources(), R.mipmap.ic_teniseri_logo );

        if (bundle == null) {
            if (resultCode == ReviewerTools.TYPE_NOT_CONNECTED) {
                notifBuilder.setSmallIcon( R.drawable.ic_not_conected );
                notifBuilder.setContentTitle( context.getString( R.string.autosync_warning ) );
                notifBuilder.setContentText( context.getString( R.string.connect_to_wifi ) );
            } else if (resultCode == ReviewerTools.TYPE_MOBILE) {
                notifBuilder.setSmallIcon( R.drawable.ic_mobile );
                notifBuilder.setContentTitle( context.getString( R.string.autosync_warning ) );
                notifBuilder.setContentText( context.getString( R.string.connect_to_wifi ) );
            } else {
                notifBuilder.setSmallIcon( R.drawable.ic_wifi );
                notifBuilder.setContentTitle( context.getString( R.string.autosync ) );
                notifBuilder.setContentText( context.getString( R.string.good_news_sync ) );
            }
        } else {
            String title = bundle.getString( "title" );
            String comment = bundle.getString( "comment" );

            notifBuilder.setSmallIcon( R.drawable.ic_comment );
            notifBuilder.setContentTitle( title );
            notifBuilder.setContentText( comment );
        }


        notifBuilder.setLargeIcon( bm );
        notifManager.notify( notifID, notifBuilder.build() );

    }
}
