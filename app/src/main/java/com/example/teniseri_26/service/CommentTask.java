package com.example.teniseri_26.service;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class CommentTask extends AsyncTask<String, Void, String[]> {

    private Context context;

    public CommentTask(Context context) {
        this.context = context;
    }

    @Override
    protected String[] doInBackground(String... strings) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return strings;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        Intent ints = new Intent("MY_COMMENT");
        ints.putExtra("title", strings[0]);
        ints.putExtra("comment", strings[1]);
        context.sendBroadcast(ints);
    }
}
