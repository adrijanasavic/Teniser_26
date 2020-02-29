package com.example.teniseri_26.tools;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.teniseri_26.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ReviewerTools {

    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectionType(Integer type) {

        switch (type) {
            case 1:
                return "WI-FI";
            case 2:
                return "Mobilni internet";
            default:
                return "Niste konektovani na internet";

        }
    }

    public static int calculateTimeTillNextSync(int minutes) {
        return 1000 * 60 * minutes;
    }

    public static void writeToFile(String data, Context context, String filename) {
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context, String file) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static void fillAdapter(String[] products, Context context) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.master_fragment, products);
        ListView listView = ((Activity) context).findViewById(R.id.lvList);

        listView.setAdapter(adapter);
    }

    public static void readFile(Context context) {
        String text = ReviewerTools.readFromFile(context, "myfile.txt");
        String[] data = text.split("\n");

        fillAdapter(data, context);
    }

    public static boolean isFileExists(Context context, String filename) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
