package com.surefiz.screens.bmidetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.bmidetails.model.BMIResponse;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BMIDetailsActivity extends AppCompatActivity {

    @BindView(R.id.btn_kg)
    Button btn_kg;
    @BindView(R.id.btnSkipWeight)
    Button btnSkipWeight;
    @BindView(R.id.btn_lbs)
    Button btn_lbs;
    @BindView(R.id.btn_go_next)
    Button btn_go_next;
    @BindView(R.id.tv_kg_lb_value)
    TextView tv_kg_lb_value;
    @BindView(R.id.textBMI)
    TextView textBMI;
    @BindView(R.id.textSubGoal1)
    TextView textSubGoal1;
    @BindView(R.id.textSubGoal2)
    TextView textSubGoal2;
    @BindView(R.id.textPercentage)
    TextView textPercentage;

    private BMIDetailsOnclick mBMIDetailsOnclick;
    private LoadingData loader;

    //Weight Measurement Units
    public double capturedBMIWeight = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_details);
        //Bind ButterKnife to the view
        ButterKnife.bind(this);
        //Initialize Loader
        loader = new LoadingData(this);

        Intent intent = getIntent();
        if (intent != null) {
            String serverUserId = intent.getStringExtra("serverUserId");
            String scaleUserId = intent.getStringExtra("ScaleUserId");
            //Call Api to list the BMI-Data
            callBMIApi(serverUserId, scaleUserId);
        }
        //Set onClickListener here
        mBMIDetailsOnclick = new BMIDetailsOnclick(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register Receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(myBMIReceiver,
                new IntentFilter("new_bmi_data"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Un-Register Receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBMIReceiver);
    }

    //Api to get the BMI data
    private void callBMIApi(final String serverUserId, final String scaleUserId) {
        loader.show();
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();

        Log.d("@@Sent-BMI-Data : ", "token = " + token + "\nserverUserId =" + serverUserId
                + "\nscaleUserId = " + scaleUserId);

        final Call<BMIResponse> call_BMIDetailsApi = apiInterface
                .call_BMIDetailsApi(token, serverUserId, scaleUserId);

        call_BMIDetailsApi.enqueue(new Callback<BMIResponse>() {
            @Override
            public void onResponse(Call<BMIResponse> call, Response<BMIResponse> response) {
                if (loader != null && loader.isShowing()) {
                    loader.dismiss();
                }
                Log.d("@@BMI-Data : ", response.body().toString());

                if (response.body().getStatus() == 1) {
                    //Set the BMI Data
                    capturedBMIWeight = response.body().getData().getBMIDetails().getWeight();
                    //Convert to lbs
                    mBMIDetailsOnclick.onClick(btn_lbs);
                    //Set values in View
                    textBMI.setText(String.valueOf(response.body().getData().getBMIDetails().getBMI()));
                    textSubGoal1.setText(String.valueOf(response.body().getData().getBMIDetails().getSubgoal1()));
                    textSubGoal2.setText(String.valueOf(response.body().getData().getBMIDetails().getSubgoal2()));
                    textPercentage.setText(String.valueOf(response.body().getData().getBMIDetails().getPercentage()));

                } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                    LoginShared.destroySessionTypePreference(BMIDetailsActivity.this);
                    startActivity(new Intent(BMIDetailsActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    MethodUtils.errorMsg(BMIDetailsActivity.this,
                            response.body().getData().getMessage());
                }
            }

            @Override
            public void onFailure(Call<BMIResponse> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(BMIDetailsActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    public void goToDashboard() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private BroadcastReceiver myBMIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("@@BMI-Broadcast : ", "Received on BMI-Page");

            String serverUserId = intent.getStringExtra("serverUserId");
            String scaleUserId = intent.getStringExtra("ScaleUserId");

            //Call Api for BMI
            callBMIApi(serverUserId, scaleUserId);
        }
    };
}
