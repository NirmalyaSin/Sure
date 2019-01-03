package com.surefiz.screens.progressstatus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProgressStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private LoadingData loader;
    private String userId = "";
    @BindView(R.id.tv_desc)
    TextView tv_desc;
    @BindView(R.id.btn_dashboard)
    Button btn_dashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_status);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("userId") != null) {
            userId = getIntent().getStringExtra("userId");
        }

        tv_desc.setMovementMethod(new ScrollingMovementMethod());
        loader = new LoadingData(ProgressStatusActivity.this);

       /* if (!ConnectionDetector.isConnectingToInternet(ProgressStatusActivity.this)) {
            MethodUtils.errorMsg(ProgressStatusActivity.this, ProgressStatusActivity.this.getString(R.string.no_internet));
        } else {
            getProgressStatus();
        }*/
        setClickEvent();
    }

    private void setClickEvent() {
        btn_dashboard.setOnClickListener(this);
    }

    private void getProgressStatus() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<ResponseBody> progress = apiInterface.call_ProgressDetailsApi(
                LoginShared.getRegistrationDataModel(ProgressStatusActivity.this).getData().getToken(),
                userId);
        progress.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dashboard:
                Intent intent = new Intent(this, DashBoardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }
}
