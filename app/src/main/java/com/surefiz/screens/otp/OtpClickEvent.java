package com.surefiz.screens.otp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.AddUserDialogForOTP;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.wificonfig.SetUpPreparation;
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
                    otpActivity.et_second.setCursorVisible(true);
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
                    otpActivity.et_second.setCursorVisible(false);
                    otpActivity.et_third.setCursorVisible(true);
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
                    otpActivity.et_third.setCursorVisible(false);
                    otpActivity.et_fourth.setCursorVisible(true);
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
                    otpActivity.et_fourth.setCursorVisible(false);
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
        final Call<ResponseBody> otp = apiInterface.call_otpApi(LoginShared.getRegistrationDataModel(otpActivity).getData().getToken(),
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

                        LoginShared.setstatusforOtpvarification(otpActivity, true);

                        NotificationManager notificationManager = (NotificationManager) otpActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(100);

                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        showOTPSuccessDialog(jsObject.getString("message"));

                      //AVIK
                    }/* else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(otpActivity);
                        LoginShared.destroySessionTypePreference(otpActivity);
                        LoginShared.setDeviceToken(otpActivity, deviceToken);
                        Intent loginIntent = new Intent(otpActivity, LoginActivity.class);
                        otpActivity.startActivity(loginIntent);
                        otpActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        otpActivity.finish();
                    }*/ else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(otpActivity, jsObject.getString("message"));
                        otpActivity.et_first.setText("");
                        otpActivity.et_second.setText("");
                        otpActivity.et_third.setText("");
                        otpActivity.et_fourth.setText("");
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

    public void showOTPSuccessDialog(String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(otpActivity);
        alertDialog.setTitle(R.string.app_name_otp);
        alertDialog.setMessage(title);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (LoginShared.getRegistrationDataModel(otpActivity).getData().getUser().get(0).getUserProfileCompleteStatus() == 0 ||
                        LoginShared.getRegistrationDataModel(otpActivity).getData().getUser().get(0).getUserMac().equals("")) {
                    Intent regIntent = new Intent(otpActivity, RegistrationActivity.class);
                    regIntent.putExtra("completeStatus", "0");
                    otpActivity.startActivity(regIntent);
                    otpActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    otpActivity.finish();
                } else {
                    Intent intent = new Intent(otpActivity, SetUpPreparation.class);
                    intent.putExtra("fromLogin",true);
                    otpActivity.startActivity(intent);
                    otpActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    otpActivity.finish();
                }
            }
        });

        alertDialog.create();
        alertDialog.show();
    }


    public void showUserAddDialog(String title, String positive, String negative) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(otpActivity);
        alertDialog.setTitle(R.string.app_name_otp);
        alertDialog.setMessage(title);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new AddUserDialogForOTP(otpActivity).show();
            }
        });

        alertDialog.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent dashBoardIntent = new Intent(otpActivity, SetUpPreparation.class);
                otpActivity.startActivity(dashBoardIntent);
                otpActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                otpActivity.finish();
            }
        });

        alertDialog.create();

        alertDialog.show();
    }

    private void hideSoftKeyBoard() {
        View view = otpActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) otpActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
