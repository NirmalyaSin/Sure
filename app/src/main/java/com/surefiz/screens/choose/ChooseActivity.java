package com.surefiz.screens.choose;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.amazon.AmazonDialog;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.barcodescanner.BarCodeScanner;
import com.surefiz.screens.choose.response.VerifyResponse;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.signup.SignUpActivity;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

//***AVIK
public class ChooseActivity extends ChooseActivityView {

    public static final int CAMERA = 1;
    private static final int REQUEST_BAR_CODE = 101;
    private AmazonDialog amazonDialog;
    private int step=1;
    private String orderId="";
    private String scaleId="";
    private LoadingData loader;
    private boolean isScanner=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_layout);
        ButterKnife.bind(this);
        loader = new LoadingData(this);

        amazonDialog=new AmazonDialog(this,step);
        chooseOnClick=new ChooseOnClick(ChooseActivity.this);

    }

    protected void callAmazon(){
        amazonDialog.show();
        amazonDialog.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step==1) {
                    if (amazonDialog.isShowing())
                        amazonDialog.dismiss();
                }else if(step==2) {

                    Intent intent = new Intent(ChooseActivity.this, SignUpActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("scaleId", scaleId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else if(step==3){
                    step=2;
                    amazonDialog.stepTwoView();
                }
            }
        });

        amazonDialog.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(step==1) {
                    if(amazonDialog.et_orderId.getText().toString().trim().equals("")){
                        MethodUtils.errorMsg(ChooseActivity.this, "Please Enter Your Amazon Order ID");

                    }else{
                        callVerifyApi(amazonDialog.et_orderId.getText().toString(),true);
                    }

                }else if(step==2) {
                    amazonDialog.stepThreeView();
                    step=3;
                }else if(step==3) {
                    if(isScanner) {
                        if (amazonDialog.et_scaleId.getText().toString().trim().equals("")) {
                            MethodUtils.errorMsg(ChooseActivity.this, "Please enter Scale ID");

                        } else {
                            callVerifyApi(amazonDialog.et_scaleId.getText().toString().replaceAll("-",""), false);
                        }
                    }else{
                        if (amazonDialog.et_scaleId.getText().toString().trim().equals("")) {
                            MethodUtils.errorMsg(ChooseActivity.this, "Please enter Scale ID");

                        }else if (amazonDialog.et_con_scaleId.getText().toString().trim().equals("")) {
                            MethodUtils.errorMsg(ChooseActivity.this, "Please Retype Scale ID");

                        }else if (!amazonDialog.et_con_scaleId.getText().toString().trim().equals(amazonDialog.et_scaleId.getText().toString().trim())) {
                            MethodUtils.errorMsg(ChooseActivity.this, "Scale ID and Retype Scale ID mismatch");

                        }else {
                            callVerifyApi(amazonDialog.et_scaleId.getText().toString().replaceAll("-",""), false);
                        }
                    }
                }

            }
        });

        amazonDialog.et_orderId.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 3)
                    sb.insert(3, "-");
                if (sb.length() > 11)
                    sb.insert(11, "-");
                if (sb.length() > 19)
                    sb.delete(19, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });

        amazonDialog.et_scaleId.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 2)
                    sb.insert(2, "-");
                if (sb.length() > 7)
                    sb.insert(7, "-");
                if (sb.length() > 12)
                    sb.delete(12, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });

        amazonDialog.et_con_scaleId.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 2)
                    sb.insert(2, "-");
                if (sb.length() > 7)
                    sb.insert(7, "-");
                if (sb.length() > 12)
                    sb.delete(12, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });

        amazonDialog.btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callScanner();
            }
        });

        amazonDialog.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanner=false;
                amazonDialog.et_scaleId.setText("");
                amazonDialog.et_con_scaleId.setText("");
                amazonDialog.btn_scan.setVisibility(View.VISIBLE);
                amazonDialog.tv_or.setVisibility(View.VISIBLE);
                amazonDialog.et_con_scaleId.setVisibility(View.VISIBLE);
                amazonDialog.btn_remove.setVisibility(View.GONE);

            }
        });

    }

    private void callVerifyApi(String orderID,boolean b) {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<VerifyResponse> call;
        if(b)
            call= apiInterface.call_amazon(orderID);
        else
            call= apiInterface.call_scale(orderID);


        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {

                    if (response.body().getStatus() == 1) {

                        if(step==1) {
                            step = 2;
                            orderId = orderID;
                            MethodUtils.showInfoDialog(ChooseActivity.this, response.body().getData().getMessage());
                            amazonDialog.stepTwoView();
                        }else if(step==3){
                            scaleId=orderID;

                            showInfoDialog(response.body().getData().getMessage());
                        }

                    } else {
                        MethodUtils.errorMsg(ChooseActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(ChooseActivity.this, ChooseActivity.this.getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(ChooseActivity.this, getString(R.string.error_occurred));
            }
        });
    }


    public void showInfoDialog(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name_splash));
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    dialog.dismiss();

                    Intent intent = new Intent(ChooseActivity.this, SignUpActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("scaleId", scaleId);

                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
        alertDialog.show();
    }



    protected void callScanner(){

        if (checkStoragePermission()) {
            Intent intent=new Intent(this, BarCodeScanner.class);
            startActivityForResult(intent,REQUEST_BAR_CODE);
        } else {
            requestStoragePermission();
        }
    }

    protected boolean checkStoragePermission() {
        return (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case REQUEST_BAR_CODE:
                if ((resultCode == RESULT_OK)) {
                    if(amazonDialog!=null)
                        isScanner=true;
                    amazonDialog.et_scaleId.setText(data.getStringExtra("barCode"));
                    amazonDialog.et_con_scaleId.setVisibility(View.GONE);
                    amazonDialog.btn_scan.setVisibility(View.GONE);
                    amazonDialog.btn_remove.setVisibility(View.VISIBLE);
                    amazonDialog.tv_or.setVisibility(View.GONE);

                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (amazonDialog.isShowing())
            amazonDialog.dismiss();
    }
}
