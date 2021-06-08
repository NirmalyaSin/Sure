package com.surefiz.screens.forgotpassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.btn_link)
    Button btn_link;
    LoadingData loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        loader = new LoadingData(this);
        setClickEvent();

    }

    private void setClickEvent() {
        btn_link.setOnClickListener(this);
        rl_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_link:
                setValidationAndApiCall();
                break;
            case R.id.rl_back:
                onBackPressed();
                break;
        }
    }

    private void setValidationAndApiCall() {
        if (et_email.getText().toString().equals("")) {
            MethodUtils.errorMsg(this, "Please enter your email.");
        } else if (!MethodUtils.isValidEmail(et_email.getText().toString())) {
            MethodUtils.errorMsg(this, "Please enter a valid email ID.");
        } else {
            callForgotApi();
        }
    }

    private void callForgotApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> call_forgot = apiInterface.call_forgotApi(et_email.getText().toString().trim());
        call_forgot.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(ForgotPasswordActivity.this, jsObject.getString("message"));
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);
                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(ForgotPasswordActivity.this);
                        LoginShared.destroySessionTypePreference(ForgotPasswordActivity.this);
                        LoginShared.setDeviceToken(ForgotPasswordActivity.this, deviceToken);
                        Intent loginIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(ForgotPasswordActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(ForgotPasswordActivity.this, getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(ForgotPasswordActivity.this, getString(R.string.error_occurred));
            }
        });
    }
}
