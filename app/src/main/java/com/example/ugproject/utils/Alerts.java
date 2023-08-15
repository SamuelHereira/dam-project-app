package com.example.ugproject.utils;

import android.app.AlertDialog;
import android.content.Context;

public class Alerts {

    public static void showAlertDialogWithText(Context context, String title, String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }


    public static AlertDialog showLoadingDialog(Context context) {
        AlertDialog builder = new android.app.AlertDialog.Builder(context)
                .setMessage("Cargando...")
                .setCancelable(false)
                .create();

        return builder;
    }

}
