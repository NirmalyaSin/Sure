package com.surefiz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.surefiz.R;
import com.surefiz.interfaces.OnUiEventClick;

public class ChooseOptionDialog extends Dialog {
    private Activity activity;
    private OnUiEventClick onUiEventClick;


    public ChooseOptionDialog(Activity activity, OnUiEventClick onUiEventClick) {
        super(activity, android.R.style.Theme_Material_NoActionBar_TranslucentDecor);
        this.activity = activity;
        this.onUiEventClick = onUiEventClick;
        setContentView(R.layout.popup_please_choose_any_option);
        init();
    }

    private void init() {
        findViewById(R.id.btn_option_adduser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("userDialog", true);
                onUiEventClick.onUiClick(intent, 101);
                dismiss();
            }
        });
        findViewById(R.id.btn_option_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("userDialog", false);
                onUiEventClick.onUiClick(intent, 101);
                dismiss();
            }
        });
    }
}
