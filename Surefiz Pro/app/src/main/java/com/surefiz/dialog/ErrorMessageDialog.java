package com.surefiz.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

public class ErrorMessageDialog {

    private static ErrorMessageDialog errorMessageDialog;
    private static Context prevContext;
    private Activity context;
    private AlertDialog dialog = null;

    private ErrorMessageDialog(Activity context) {
        this.context = context;
    }

    public static ErrorMessageDialog getInstant(Activity context) {
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
        CustomAlert customAlert=new CustomAlert(context);
        customAlert.setSubText(msg);
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                return;
            }
        });

        try {
            customAlert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
