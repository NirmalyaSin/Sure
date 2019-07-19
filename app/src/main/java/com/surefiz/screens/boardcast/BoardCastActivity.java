package com.surefiz.screens.boardcast;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.mydevice.MyDeviceActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BoardCastActivity extends BaseActivity implements View.OnClickListener {

    public View view;
    EditText et_message;
    Button btn_boardcast;
    private LoadingData loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_board_cast, null);
        addContentView(view);
        loader = new LoadingData(BoardCastActivity.this);
        setHeaderView();
        viewBind();
        clickEventFunc();

    }

    private void viewBind() {
        et_message = findViewById(R.id.et_message);
        btn_boardcast = findViewById(R.id.btn_boardcast);
    }

    private void clickEventFunc() {
        btn_boardcast.setOnClickListener(this);
    }

    private void callBoardCastApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<ResponseBody> call_boardcastApi = apiInterface.call_boardcastApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), et_message.getText().toString().trim());

        call_boardcastApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(BoardCastActivity.this, jsObject.getString("message"));
                        et_message.setText("");

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(BoardCastActivity.this);
                        LoginShared.destroySessionTypePreference(BoardCastActivity.this);
                        LoginShared.setDeviceToken(BoardCastActivity.this, deviceToken);
                        Intent loginIntent = new Intent(BoardCastActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(BoardCastActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(BoardCastActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(BoardCastActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void setHeaderView() {
        tv_universal_header.setText("Message Board");
        img_topbar_menu.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_boardcast:
                if (et_message.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(BoardCastActivity.this, "Please enter some text to BoardCast");
                } else if (!ConnectionDetector.isConnectingToInternet(BoardCastActivity.this)) {
                    MethodUtils.errorMsg(BoardCastActivity.this, BoardCastActivity.this.getString(R.string.no_internet));
                } else {
                    callBoardCastApi();
                }
                break;

        }

    }
}
