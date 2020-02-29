package com.example.teniseri_26.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class AboutDialog extends AlertDialog.Builder {
    public AboutDialog(Context context) {
        super( context );
        setTitle( "About" );
        setMessage( "Ime aplikacije: Teniseri\nKreator: Adrijana Savic" );
        setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );
        setNegativeButton( "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        } );
    }

    public AlertDialog prepareDialog() {
        AlertDialog dialog = create();
        dialog.setCanceledOnTouchOutside( false );
        return dialog;
    }
}

