package com.surefiz.screens.login;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.AddLoginUserDetails;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.forgotpassword.ForgotPasswordActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.MembershipActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
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
    private FirebaseAuth mAuth;
    private TwitterAuthClient mTwitterAuthClient;

    public LoginClickEvent(LoginActivity activity) {
        this.mLoginActivity = activity;
        loader = new LoadingData(mLoginActivity);
/*
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                mLoginActivity.getResources().getString(R.string.twitter_consumer_key),
                mLoginActivity.getResources().getString(R.string.twitter_consumer_secret));
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Twitter Auth-Client
        mTwitterAuthClient = new TwitterAuthClient();*/

        // Check if user is signed in (non-null) and update UI accordingly.
        //   FirebaseUser currentUser = mAuth.getCurrentUser();
        //    updateUI(currentUser);

        //    hideSoftKeyBoard();
        setClickEvent();
    }


    private void setClickEvent() {
        mLoginActivity.btnLogin.setOnClickListener(this);
        mLoginActivity.tv_register.setOnClickListener(this);
        //mLoginActivity.iv_facebook.setOnClickListener(this);
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

            case R.id.iv_twiter:
                MethodUtils.errorMsg(mLoginActivity, "Under Development");
                //Perform Twitter login
                //     performTwiterLogin();

                break;

            case R.id.tv_register:
                Intent regIntent = new Intent(mLoginActivity, MembershipActivity.class);
                regIntent.putExtra("completeStatus", "1");
                mLoginActivity.startActivity(regIntent);
                mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private void performTwiterLogin() {
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
//        {"status":1,"data":{"status":1,"message":"You have successfully logged in.","token":"29c41fc29c0ec161708b345901f2dbf6","user":[{"user_id":"101","user_name":"John101  John","user_email":"john101@surefiz.com","user_password":"123456","user_photo":"https:\/\/www.surefiz.com\/img\/profiles\/user-101\/1542293673.jpg","user_mac":"1000000010","user_permission":"ReadOnly","user_LastLogin":"2019-02-14 05:11:55","user_Profile_Complete_Status":1}]}}
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
                        LoginShared.setUserPhoto(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserPhoto());
                        LoginShared.setUserName(mLoginActivity, LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getUserName());
                        LoginShared.setScaleUserId(Integer.parseInt(LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).getScaleUserId()));
                        if (LoginShared.getRegistrationDataModel(mLoginActivity).getData().getUser().get(0).
                                getUserProfileCompleteStatus() == 0) {
//                            new AddLoginUserDetails(mLoginActivity).show();
                            Intent regIntent = new Intent(mLoginActivity, MembershipActivity.class);
                            regIntent.putExtra("completeStatus", "0");
                            mLoginActivity.startActivity(regIntent);
                            mLoginActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
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
