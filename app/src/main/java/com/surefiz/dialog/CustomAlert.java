package com.surefiz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surefiz.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomAlert extends Dialog {
    protected Activity activity;

    @BindView(R.id.cancel_block)
    public LinearLayout cancel_block;
    @BindView(R.id.tv_header)
    public TextView tv_header;
    @BindView(R.id.tv_sub)
    public TextView tv_sub;
    @BindView(R.id.btn_cancel)
    public TextView btn_cancel;
    @BindView(R.id.btn_ok)
    public TextView btn_ok;

    public CustomAlert(@NonNull Activity activity) {
        super(activity, R.style.DialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        View view = View.inflate(getContext(), R.layout.custom_alert_view, null);
        setContentView(view);

        ButterKnife.bind(this, view);
        ButterKnife.bind(activity);

        this.activity=activity;

    }
    public void setHeaderText(String s){
        tv_header.setText(s);
    }
    public void setSubText(String s){
        tv_sub.setText(s);
    }
    public void setKeyName(String s,String s2){
        btn_cancel.setText(s);
        btn_ok.setText(s2);
    }

    public void setCancelVisible(){
        cancel_block.setVisibility(View.VISIBLE);
    }
}
