package com.example.teniseri_26.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.teniseri_26.tools.ReviewerTools;


public class SimpleService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int status = ReviewerTools.getConnectivityStatus( getApplicationContext() );


        if (status == ReviewerTools.TYPE_WIFI) {
            new SimpleSyncTask( getApplicationContext() ).execute( status );
        } else if (status == ReviewerTools.TYPE_MOBILE) {
            new SimpleSyncTask( getApplicationContext() ).execute( status );
        } else {
            new SimpleSyncTask( getApplicationContext() ).execute( status );
        }


        stopSelf();

        return START_NOT_STICKY;
    }
}
