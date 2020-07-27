package com.surefiz.screens.login;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.forgotpassword.ForgotPasswordActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.MembershipActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.welcome.WelcomeActivity;
import com.surefiz.screens.wificonfig.SetUpPreparation;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginClickEvent implements View.OnClickListener {
    LoginActivity mLoginActivity;
    private LoadingData loader;
    //private FirebaseAuth mAuth;
    //private TwitterAuthClient mTwitterAuthClient;

    public LoginClickEvent(LoginActivity activity) {
        this.mLoginActivity = activity;
        loader = new LoadingData(mLoginActivity);
        setClickEvent();
    }


    private void setClickEvent() {
        mLoginActivity.btnLogin.setOnClickListener(this);
        mLoginActivity.tv_register.setOnClickListener(this);
        mLoginActivity.iv_twiter.setOnClickListener(this);
        mLoginActivity.tv_forgetPassword.setOnClickListener(this);
        mLoginActivity.iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_forgetPassword:
                mLoginActivity.startActivity(new Intent(mLoginActivity, ForgotPasswordActivity.class));
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.iv_back:
                //callWelcome();
                mLoginActivity.onBackPressed();
                break;

                case R.id.btnLogin:
                blankvalidationandapicall();
                break;

            case R.id.iv_twiter:
                //performTwiterLogin();
                break;

            case R.id.tv_register:
                Intent regIntent = new Intent(mLoginActivity, MembershipActivity.class);
                regIntent.putExtra("completeStatus", "1");
                mLoginActivity.startActivity(regIntent);
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private void callWelcome(){
        Intent loginIntent = new Intent(mLoginActivity, WelcomeActivity.class);
        mLoginActivity.startActivity(loginIntent);
        mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        mLoginActivity.finish();
    }

    /*private void performTwiterLogin() {
        mTwitterAuthClient.authorize(mLoginActivity,
                new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        // Success
                        Log.d("@@TwiterLogin: ",
                                "user = " + twitterSessionResult.data.getUserName()
                                        + "\nResponse = " + twitterSessionResult.response.body());
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });
    }*/

    private void blankvalidationandapicall() {
        if (!mLoginActivity.editEmail.getText().toString().equals("")) {
            if (!mLoginActivity.editPassword.getText().toString().equals("")) {
                callapiforlogin();
            } else {
                MethodUtils.errorMsg(mLoginActivity, "Please enter your password");
            }
        } else {
            MethodUtils.errorMsg(mLoginActivity, "Please enter your email");
        }


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
                    navigateAfterLogin(responseString, false);

                } catch (Exception e) {
                    e.printStackTrace();
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


    public void navigateAfterLogin(String responseString, boolean isSocialLogin) {

        try {
            JSONObject jsonObject = new JSONObject(responseString);
            Gson gson = new Gson();
            RegistrationModel registrationModel;

            Log.d("@@LoginData : ", jsonObject.toString());

            if (jsonObject.optInt("status") == 1) {
                LoginShared.setstatusforOtpvarification(mLoginActivity, true);
                LoginShared.setWeightPageFrom(mLoginActivity, "0");

                registrationModel = gson.fromJson(responseString, RegistrationModel.class);

                if (isSocialLogin) {
                    registrationModel.getData().getUser().get(0).setUserProfileCompleteStatus(1);
                }

                LoginShared.setRegistrationDataModel(mLoginActivity, registrationModel);
                LoginShared.setUserPhoto(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserPhoto());
                LoginShared.setUserName(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserName());
                LoginShared.setScaleUserId(Integer.parseInt(LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getScaleUserId()));

                LoginShared.setAccessToken(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getToken());

                if (!LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).
                        getUserMac().equals("")) {
                    LoginShared.setUserMacId(Long.parseLong(LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserMac()));
                }

                if (LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getScaleUserId().equals("1")) {
                    if (LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).
                            getUserMac() == null || LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).
                            getUserMac().equals("")) {

                        LoginShared.setRegistrationDataModel(mLoginActivity, null);


                        Intent regIntent = new Intent(mLoginActivity, RegistrationActivity.class);
                        regIntent.putExtra("completeStatus", "0");
                        regIntent.putExtra("registrationModelData", responseString);
                        mLoginActivity.startActivity(regIntent);
                        mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //mLoginActivity.finishAffinity();
                    } else {
                        //if (!LoginShared.getstatusforwifivarification(mLoginActivity)) {
                            //AVIK
                            //Intent intent = new Intent(mLoginActivity, WifiConfigActivity.class);
                        if (!new InstructionSharedPreference(mLoginActivity).isInstructionShown(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserId())) {

                            Intent intent = new Intent(mLoginActivity, SetUpPreparation.class);
                            intent.putExtra("fromLogin",true);
                            mLoginActivity.startActivity(intent);
                            mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            Intent intent = new Intent(mLoginActivity, DashBoardActivity.class);
                            mLoginActivity.startActivity(intent);
                            mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            mLoginActivity.finishAffinity();
                        }
                    }
                } else {
                    if (LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserProfileCompleteStatus() == 0) {

                        LoginShared.setRegistrationDataModel(mLoginActivity, null);

                        Intent regIntent = new Intent(mLoginActivity, RegistrationActivity.class);
                        regIntent.putExtra("completeStatus", "0");
                        regIntent.putExtra("registrationModelData", responseString);
                        mLoginActivity.startActivity(regIntent);
                        mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        //mLoginActivity.finishAffinity();

                    } else {

                        if (LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).
                                getUserMac().equals("")) {

                            String deviceToken = LoginShared.getDeviceToken(mLoginActivity);
                            LoginShared.setRegistrationDataModel(mLoginActivity, null);
                            LoginShared.destroySessionTypePreference(mLoginActivity);
                            LoginShared.setDeviceToken(mLoginActivity, deviceToken);

                            //JSONObject jsObject = jsonObject.getJSONObject("data");
                            MethodUtils.errorMsg(mLoginActivity, mLoginActivity.getString(R.string.mac_id_not_found));
                        } else {

                            if (!new InstructionSharedPreference(mLoginActivity).isInstructionShown(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserId())) {
                                Intent instruc = new Intent(mLoginActivity, InstructionActivity.class);
                                mLoginActivity.startActivity(instruc);
                                mLoginActivity.finish();
                            }else {
                                LoginShared.setstatusforwifivarification(mLoginActivity, true);
                                Intent intent = new Intent(mLoginActivity, DashBoardActivity.class);
                                mLoginActivity.startActivity(intent);
                                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                mLoginActivity.finishAffinity();
                            }



                        }
                    }
                }
            } else if (jsonObject.optInt("status") == 4) {
                registrationModel = gson.fromJson(responseString, RegistrationModel.class);
                LoginShared.setRegistrationDataModel(mLoginActivity, registrationModel);
                Intent intent = new Intent(mLoginActivity, OtpActivity.class);
                mLoginActivity.startActivity(intent);
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //mLoginActivity.finish();
            } else {
                JSONObject jsObject = jsonObject.getJSONObject("data");
                MethodUtils.errorMsg(mLoginActivity, jsObject.getString("message"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            MethodUtils.errorMsg(mLoginActivity, mLoginActivity.getString(R.string.error_occurred));
        }
    }
}
