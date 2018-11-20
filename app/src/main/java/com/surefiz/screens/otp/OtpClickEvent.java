package com.surefiz.screens.otp;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OtpClickEvent implements View.OnClickListener {
    OtpActivity otpActivity;
    private LoadingData loader;

    public OtpClickEvent(OtpActivity otpActivity) {
        this.otpActivity = otpActivity;
        loader = new LoadingData(otpActivity);

        setEnterEditext();
        setClickEvent();
    }

    private void setEnterEditext() {
        otpActivity.et_first.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (otpActivity.et_first.getText().toString().length() == 1)     //size as per your requirement
                {
                    otpActivity.et_second.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        otpActivity.et_second.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (otpActivity.et_second.getText().toString().length() == 1)     //size as per your requirement
                {
                    otpActivity.et_third.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        otpActivity.et_third.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (otpActivity.et_third.getText().toString().length() == 1)     //size as per your requirement
                {
                    otpActivity.et_fourth.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

        otpActivity.et_fourth.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (otpActivity.et_fourth.getText().toString().length() == 1)     //size as per your requirement
                {
                    hideSoftKeyBoard();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
    }

    private void setClickEvent() {
        otpActivity.btn_submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (otpActivity.et_first.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(otpActivity, "Enter full four digit OTP");

                } else if (otpActivity.et_second.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(otpActivity, "Enter full four digit OTP");

                } else if (otpActivity.et_third.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(otpActivity, "Enter full four digit OTP");

                } else if (otpActivity.et_fourth.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(otpActivity, "Enter full four digit OTP");
                } else if (!ConnectionDetector.isConnectingToInternet(otpActivity)) {
                    MethodUtils.errorMsg(otpActivity, otpActivity.getString(R.string.no_internet));
                } else {
                    sendOtpApi();
                }
                break;
        }

    }

    private void sendOtpApi() {
        String otpValue = otpActivity.et_first.getText().toString() + otpActivity.et_second.getText().toString() + otpActivity.et_third.getText().toString() +
                otpActivity.et_fourth.getText().toString();
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<ResponseBody> otp = apiInterface.call_otpApi("application/json",
                LoginShared.getRegistrationDataModel(otpActivity).getData().getToken(),
                LoginShared.getRegistrationDataModel(otpActivity).getData().getUser().get(0).getUserId(), otpValue);
        otp.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {

                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(otpActivity, jsObject.getString("message"));
                        LoginShared.setstatusforOtpvarification(true);
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(otpActivity, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(otpActivity, otpActivity.getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(otpActivity, otpActivity.getString(R.string.error_occurred));
            }
        });

    }

    private void hideSoftKeyBoard() {
        View view = otpActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) otpActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
