package com.surefiz.dialog.amazon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.screens.choose.ChooseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmazonDialog extends Dialog {

    ChooseActivity activity;
    int step;

    @BindView(R.id.tv_sub)
    public TextView tv_sub;
    @BindView(R.id.btn_cancel)
    public TextView btn_cancel;
    @BindView(R.id.btn_ok)
    public TextView btn_ok;

    @BindView(R.id.btn_yes)
    public TextView btn_yes;
    @BindView(R.id.btn_no)
    public TextView btn_no;
    @BindView(R.id.btn_cancel2)
    public TextView btn_cancel2;

    @BindView(R.id.second_view)
    public LinearLayout second_view;

    @BindView(R.id.third_view)
    public LinearLayout third_view;

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
    @BindView(R.id.btn_remove)
    public TextView btn_remove;
    @BindView(R.id.tv_or)
    public View tv_or;


    public AmazonDialog(ChooseActivity activity,int step) {
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
           stepOneView(activity.isAmazon);
        }
    }

    public void stepOneView(boolean isAmazon){
        if(isAmazon)
            tv_sub.setText("Please enter your Amazon Order ID.");
        else
            tv_sub.setText("Please enter your Order ID.");

        et_orderId.setText("");
        et_orderId.setVisibility(View.VISIBLE);
        scan_block.setVisibility(View.GONE);
        third_view.setVisibility(View.VISIBLE);
        second_view.setVisibility(View.GONE);

        btn_cancel.setText("Cancel");
        btn_ok.setText("Verify");

    }

    public void stepTwoView(){
        tv_sub.setText("Have you received the scale?");
        et_orderId.setVisibility(View.GONE);
        scan_block.setVisibility(View.GONE);

        third_view.setVisibility(View.GONE);
        second_view.setVisibility(View.VISIBLE);

    }

    public void stepThreeView(){
        tv_sub.setText("Please scan the scale ID barcode on the back your scale or enter it manually.");
        et_scaleId.setText("");
        et_con_scaleId.setText("");
        et_orderId.setVisibility(View.GONE);
        scan_block.setVisibility(View.VISIBLE);
        third_view.setVisibility(View.VISIBLE);
        second_view.setVisibility(View.GONE);
        btn_remove.setPaintFlags(btn_remove.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        btn_cancel.setText("Cancel");
        btn_ok.setText("Submit");

    }

}
