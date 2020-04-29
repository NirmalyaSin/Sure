package com.surefiz.dialog.amazon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.surefiz.R;
import com.surefiz.interfaces.OnUiEventClick;

public class AmazonDialog extends Dialog {

    Activity activity;

    public AmazonDialog(Activity activity) {
        super(activity, android.R.style.Theme_Material_NoActionBar_TranslucentDecor);
        this.activity = activity;
        setContentView(R.layout.popup_please_choose_any_option);
    }
}
