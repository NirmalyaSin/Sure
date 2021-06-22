package com.surefiz.screens.setupPreparation;

import android.app.Activity;
import android.content.Intent;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SendWifiLog {

    String action;
    Activity mActivity;
    LoadingData loader;
    OnUiEventClick onUiEventClick;

    public SendWifiLog(String action, Activity activity, OnUiEventClick onUiEventClick) {
        this.action = action;
        this.mActivity = activity;
        this.onUiEventClick = onUiEventClick;
        loader = new LoadingData(activity);
        sendLog();
    }

    private void sendLog() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<ResponseBody> sendWifiLog = apiInterface.sendWifiLog(LoginShared.getRegistrationDataModel(mActivity).getData().getToken(),
                LoginShared.getRegistrationDataModel(mActivity).getData().getUser().get(0).getScaleUserId(), action);

        sendWifiLog.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                if (response.isSuccessful()) {
                    Intent intent = new Intent();
                    intent.putExtra("success", true);
                    onUiEventClick.onUiClick(intent, response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(mActivity, mActivity.getResources().getString(R.string.error_occurred));
            }
        });
    }
}
