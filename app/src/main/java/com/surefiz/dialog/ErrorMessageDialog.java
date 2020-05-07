package com.surefiz.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.surefiz.R;

public class ErrorMessageDialog {

    private static ErrorMessageDialog errorMessageDialog;
    private static Context prevContext;
    private Context context;
    private AlertDialog dialog = null;

    private ErrorMessageDialog(Context context) {
        this.context = context;
    }

    public static ErrorMessageDialog getInstant(Context context) {
        if (errorMessageDialog == null) {
            prevContext = context;
            errorMessageDialog = new ErrorMessageDialog(context);
        }

        if (prevContext != context) {
            errorMessageDialog = null;
            errorMessageDialog = new ErrorMessageDialog(context);
        }
        prevContext = context;
        return errorMessageDialog;
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void show(String msg) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(R.string.app_name_splash);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(msg);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.dismiss();
                return;
            }
        });

        try {
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
