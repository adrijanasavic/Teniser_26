package com.example.teniseri_26.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.teniseri_26.tools.ReviewerTools;


public class SimpleSyncTask extends AsyncTask<Integer, Void, Integer> {

    private Context context;

    public SimpleSyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        try {
            Thread.sleep( 500 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return integers[0];
    }


    @Override
    protected void onPostExecute(Integer integer) {
        Toast.makeText( context, "Uredjaj povezan na: " + ReviewerTools.getConnectionType( integer ), Toast.LENGTH_LONG ).show();

        Intent i = new Intent( "SYNC_DATA" );
        i.putExtra( "RESULT_CODE", integer );
        context.sendBroadcast( i );

    }
}
