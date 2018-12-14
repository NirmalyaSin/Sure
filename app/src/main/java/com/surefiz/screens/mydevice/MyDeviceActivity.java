package com.surefiz.screens.mydevice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
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

public class MyDeviceActivity extends BaseActivity implements View.OnClickListener {
    public View view;
    Button btn_update;
    EditText et_id;
    TextView tv_scale_id;
    private LoadingData loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_my_device, null);
        addContentView(view);
        setViewBind();
        setHeaderView();
        setClickEvent();
        loader = new LoadingData(this);
//        showButton();

    }

    private void setClickEvent() {
        btn_update.setOnClickListener(this);
        rl_back.setOnClickListener(this);
    }

    private void setViewBind() {
        btn_update = findViewById(R.id.btn_update);
        et_id = findViewById(R.id.et_id);
        tv_scale_id = findViewById(R.id.tv_scale_id);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Device Binding");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        img_topbar_menu.setVisibility(View.GONE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (LoginShared.getRegistrationDataModel(this) != null) {
            tv_scale_id.setText("Your Scale ID: " + LoginShared.getRegistrationDataModel(MyDeviceActivity.this).getData().getUser().get(0).getUserMac());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(MyDeviceActivity.this, SettingsActivity.class);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_update:
                if (!ConnectionDetector.isConnectingToInternet(this)) {
                    MethodUtils.errorMsg(this, getString(R.string.no_internet));
                } else if (et_id.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(this, "Please enter Scale ID");
                } else {
                    changeScaleIdToServer();
                }
                break;
            case R.id.rl_back:
                Intent loginIntent = new Intent(MyDeviceActivity.this, SettingsActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }

    private void changeScaleIdToServer() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_addDeviceApi = apiInterface.call_addDeviceApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), et_id.getText().toString().trim());
        call_addDeviceApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        RegistrationModel registrationModel = LoginShared.getRegistrationDataModel(MyDeviceActivity.this);
                        registrationModel.getData().getUser().get(0).setUserMac(et_id.getText().toString().trim());
                        LoginShared.setRegistrationDataModel(MyDeviceActivity.this, registrationModel);

                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(MyDeviceActivity.this, jsObject.getString("message"));

                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(MyDeviceActivity.this, InstructionActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(MyDeviceActivity.this);
                        LoginShared.destroySessionTypePreference();
                        LoginShared.setDeviceToken(MyDeviceActivity.this, deviceToken);
                        Intent loginIntent = new Intent(MyDeviceActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(MyDeviceActivity.this, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(MyDeviceActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(MyDeviceActivity.this, getString(R.string.error_occurred));
            }
        });
    }
}
