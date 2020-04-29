package com.surefiz.screens.choose;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.amazon.AmazonDialog;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.barcodescanner.BarCodeScanner;
import com.surefiz.screens.choose.response.VerifyResponse;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
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
    private LoadingData loader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_layout);
        ButterKnife.bind(this);
        loader = new LoadingData(this);

        chooseOnClick=new ChooseOnClick(ChooseActivity.this);

    }

    protected void callAmazon(){

        amazonDialog=new AmazonDialog(this,step);
        amazonDialog.show();

        amazonDialog.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step==1) {
                    if (amazonDialog.isShowing())
                        amazonDialog.dismiss();
                }else if(step==2) {

                    Intent regIntent = new Intent(ChooseActivity.this, RegistrationActivity.class);
                    regIntent.putExtra("completeStatus", "1");
                    //regIntent.putExtra("registrationModelData", responseString);
                    startActivity(regIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finishAffinity();
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
                        callAmazonVerifyApi(amazonDialog.et_orderId.getText().toString());
                    }

                }else if(step==2) {
                    amazonDialog.stepThreeView();
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

        amazonDialog.btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callScanner();
            }
        });

    }

    private void callAmazonVerifyApi(String orderID) {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<VerifyResponse> call = apiInterface.call_amazon(orderID);

        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {

                    if (response.body().getStatus() == 1) {
                        step=2;
                        orderId=orderID;
                        MethodUtils.showInfoDialog(ChooseActivity.this, response.body().getData().getMessage());

                        amazonDialog.stepTwoView();


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
                        amazonDialog.et_scaleId.setText(data.getStringExtra("barCode"));
                }
                break;
        }
    }
}
