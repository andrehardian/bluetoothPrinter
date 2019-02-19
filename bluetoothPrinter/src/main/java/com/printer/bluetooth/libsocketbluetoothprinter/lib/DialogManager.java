package com.printer.bluetooth.libsocketbluetoothprinter.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogManager {
    protected final Context context;

    public DialogManager(Context context) {
        this.context = context;
    }

    public AlertDialog errorDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                    }
                });

        final AlertDialog alert = builder.create();
        try {
            if (context instanceof Activity && !((Activity) context).isFinishing()) {
                alert.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alert;
    }

}
