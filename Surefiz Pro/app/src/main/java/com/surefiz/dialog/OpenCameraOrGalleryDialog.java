package com.surefiz.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.surefiz.R;
import com.surefiz.interfaces.OnImageSet;
import com.surefiz.screens.registration.RegistrationActivity;


public class OpenCameraOrGalleryDialog extends AppCompatDialog implements View.OnClickListener {
    private Activity mActivity;
    private TextView tv_header, tv_openCamera, tv_openGallery, tvCancel;
    private OnImageSet onImageSet;
    String choose;

    public OpenCameraOrGalleryDialog(Activity mActivity, OnImageSet onImageSet, String choose) {
        super(mActivity);
        this.mActivity = mActivity;
        this.onImageSet = onImageSet;
        this.choose = choose;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = mActivity.getLayoutInflater().inflate(R.layout.open_camera_open_gallery_dialog, null);
        setContentView(view);
        setCanceledOnTouchOutside(true);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wmlp.height = ViewGroup.LayoutParams.MATCH_PARENT;

        wmlp.gravity = Gravity.BOTTOM;

        wmlp.windowAnimations = R.style.DialogAnimation;

        initView(view);

        clickEvent();

    }

    private void initView(View view) {
        tv_header = view.findViewById(R.id.tv_header);
        tv_openCamera = view.findViewById(R.id.tv_openCamera);
        tv_openGallery = view.findViewById(R.id.tv_openGallery);
        tvCancel = view.findViewById(R.id.tvCancel);
    }

    private void clickEvent() {
        tv_openCamera.setOnClickListener(this);
        tv_openGallery.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_openCamera:
                if (choose.equals("0")) {
                    ((RegistrationActivity) mActivity).choiceMedia(RegistrationActivity.CAMERA, onImageSet);
                }

                dismiss();
                break;
            case R.id.tv_openGallery:
                if (choose.equals("0")) {
                    ((RegistrationActivity) mActivity).choiceMedia(RegistrationActivity.GALLERY, onImageSet);
                }
                dismiss();
                break;
            case R.id.tvCancel:
                dismiss();
                break;
        }
    }
}
