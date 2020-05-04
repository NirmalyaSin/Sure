package com.surefiz.dialog.amazon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.interfaces.OnUiEventClick;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmazonDialog extends Dialog {

    Activity activity;
    int step;

    @BindView(R.id.tv_sub)
    public TextView tv_sub;
    @BindView(R.id.btn_cancel)
    public TextView btn_cancel;
    @BindView(R.id.btn_ok)
    public TextView btn_ok;

    @BindView(R.id.et_orderId)
    public EditText et_orderId;

    @BindView(R.id.scan_block)
    public LinearLayout scan_block;
    @BindView(R.id.et_scaleId)
    public EditText et_scaleId;
    @BindView(R.id.et_con_scaleId)
    public EditText et_con_scaleId;
    @BindView(R.id.btn_scan)
    public TextView btn_scan;


    public AmazonDialog(Activity activity,int step) {
        super(activity, R.style.DialogStyle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        View view = View.inflate(getContext(), R.layout.amazon_dialog_view, null);
        setContentView(view);

        ButterKnife.bind(this, view);
        ButterKnife.bind(activity);

        this.activity=activity;
        this.step=step;

        if(step==1){
           stepOneView();
        }
    }

    public void stepOneView(){
        tv_sub.setText("Please enter your Amazon Order Id.");
        et_orderId.setVisibility(View.VISIBLE);
        scan_block.setVisibility(View.GONE);

        btn_cancel.setText("Cancel");
        btn_ok.setText("Verify");

    }

    public void stepTwoView(){
        tv_sub.setText("Have you received the scale?");
        et_orderId.setVisibility(View.GONE);
        scan_block.setVisibility(View.GONE);

        btn_cancel.setText("No");
        btn_ok.setText("Yes");

    }

    public void stepThreeView(){
        tv_sub.setText("Please scan the scale ID barcode on the back your scale or enter it manually");
        et_orderId.setVisibility(View.GONE);
        scan_block.setVisibility(View.VISIBLE);

        btn_cancel.setText("Cancel");
        btn_ok.setText("Submit");

    }

}
