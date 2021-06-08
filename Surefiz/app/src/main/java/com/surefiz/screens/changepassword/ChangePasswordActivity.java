package com.surefiz.screens.changepassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.drawerlayout.widget.DrawerLayout;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.mydevice.MyDeviceActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    public View view;
    EditText et_old, et_new, et_retype;
    Button btn_submit;
    private LoadingData loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_change_password, null);
        addContentView(view);
        loader = new LoadingData(this);
        viewBind();
        setHeaderView();
        setClickEvent();
    }

    private void setClickEvent() {
        btn_submit.setOnClickListener(this);
        rl_back.setOnClickListener(this);
    }

    private void viewBind() {
        et_old = findViewById(R.id.et_old);
        et_new = findViewById(R.id.et_new);
        et_retype = findViewById(R.id.et_retype);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Change Password");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        img_topbar_menu.setVisibility(View.GONE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_submit:
                if (et_old.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(this, "Please enter your old password.");
                } else if (et_new.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(this, "Please enter your new password.");
                } else if (et_retype.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(this, "Please retype your new password.");
                } else if (!et_new.getText().toString().trim().equals(et_retype.getText().toString().trim())) {
                    MethodUtils.errorMsg(this, "“Entered passwords are not matching.");
                } else if (!(et_new.getText().toString().trim().length() >= 8
                        && et_new.getText().toString().trim().length() <= 16)) {
                    MethodUtils.errorMsg(this, "Password must be 8 characters or more.");
                } else if (!ConnectionDetector.isConnectingToInternet(this)) {
                    MethodUtils.errorMsg(this, getString(R.string.no_internet));
                } else {
                    callPasswordChangeApi();
                }
                break;
            case R.id.rl_back:
                Intent loginIntent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private void callPasswordChangeApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_changePasswordApi = apiInterface.call_changePasswordApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), et_old.getText().toString().trim(),
                et_new.getText().toString().trim());
        call_changePasswordApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(ChangePasswordActivity.this, jsObject.getString("message"));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                RegistrationModel registrationModel = LoginShared.getRegistrationDataModel(ChangePasswordActivity.this);
                                registrationModel.getData().getUser().get(0).setUserPassword(et_new.getText().toString().trim());
                                LoginShared.setRegistrationDataModel(ChangePasswordActivity.this, registrationModel);
                                /*String deviceToken = LoginShared.getDeviceToken(ChangePasswordActivity.this);
                                LoginShared.destroySessionTypePreference(ChangePasswordActivity.this);
                                LoginShared.setDeviceToken(ChangePasswordActivity.this, deviceToken);*/
                                Intent loginIntent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(ChangePasswordActivity.this);
                        LoginShared.destroySessionTypePreference(ChangePasswordActivity.this);
                        LoginShared.setDeviceToken(ChangePasswordActivity.this, deviceToken);
                        Intent loginIntent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(ChangePasswordActivity.this, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(ChangePasswordActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(ChangePasswordActivity.this, getString(R.string.error_occurred));
            }
        });

    }
}
