package com.surefiz.screens.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.forgotpassword.ForgotPasswordActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginClickEvent implements View.OnClickListener {
    LoginActivity mLoginActivity;
    private LoadingData loader;

    public LoginClickEvent(LoginActivity activity) {
        this.mLoginActivity = activity;
        loader = new LoadingData(mLoginActivity);
        //    hideSoftKeyBoard();
        setClickEvent();
    }


    private void setClickEvent() {
        mLoginActivity.btnLogin.setOnClickListener(this);
        mLoginActivity.tv_register.setOnClickListener(this);
        mLoginActivity.iv_facebook.setOnClickListener(this);
        mLoginActivity.iv_twiter.setOnClickListener(this);
        mLoginActivity.tv_forgetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_forgetPassword:
                mLoginActivity.startActivity(new Intent(mLoginActivity, ForgotPasswordActivity.class));
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.btnLogin:
                blankvalidationandapicall();
                break;

            case R.id.iv_facebook:
                MethodUtils.errorMsg(mLoginActivity, "Under Development");
                break;

            case R.id.iv_twiter:
                MethodUtils.errorMsg(mLoginActivity, "Under Development");
                break;

            case R.id.tv_register:
                mLoginActivity.startActivity(new Intent(mLoginActivity, RegistrationActivity.class));
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private void blankvalidationandapicall() {
        if (!mLoginActivity.editEmail.getText().toString().equals("")) {
            if (!mLoginActivity.editPassword.getText().toString().equals("")) {
                callapiforlogin();
            } else
                Toast.makeText(mLoginActivity, "pass not blank", Toast.LENGTH_LONG).show();

        } else
            Toast.makeText(mLoginActivity, "Email not blank", Toast.LENGTH_LONG).show();

    }

    private void callapiforlogin() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> loginapicall = apiInterface.call_loginApi(mLoginActivity.editEmail.getText().toString().trim(),
                mLoginActivity.editPassword.getText().toString(), "2", LoginShared.getDeviceToken(mLoginActivity));
        loginapicall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    RegistrationModel registrationModel;

                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.d("@@LoginData : ", jsonObject.toString());
                    if (jsonObject.optInt("status") == 1) {
                        LoginShared.setstatusforOtpvarification(mLoginActivity, true);
                        LoginShared.setWeightPageFrom(mLoginActivity, "0");
                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(mLoginActivity, registrationModel);
                        if (!LoginShared.getstatusforwifivarification(mLoginActivity)) {
                            Intent intent = new Intent(mLoginActivity, WifiConfigActivity.class);
                            mLoginActivity.startActivity(intent);
                            mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            mLoginActivity.finish();
                        } else {
                            Intent intent = new Intent(mLoginActivity, DashBoardActivity.class);
                            mLoginActivity.startActivity(intent);
                            mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            mLoginActivity.finish();
                        }
                    } else if (jsonObject.optInt("status") == 4) {
                        registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                        LoginShared.setRegistrationDataModel(mLoginActivity, registrationModel);
                        Intent intent = new Intent(mLoginActivity, OtpActivity.class);
                        mLoginActivity.startActivity(intent);
                        mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        mLoginActivity.finish();
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(mLoginActivity, jsObject.getString("message"));

                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(mLoginActivity, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(mLoginActivity, mLoginActivity.getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(mLoginActivity, mLoginActivity.getString(R.string.error_occurred));
            }
        });
    }

    private void hideSoftKeyBoard() {
        View view = mLoginActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mLoginActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
