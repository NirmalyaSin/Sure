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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.bmidetails.model.BMIDetails;
import com.surefiz.screens.bmidetails.model.BMIResponse;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BMIDetailsActivity extends AppCompatActivity {

    @BindView(R.id.img_bmi_back)
    ImageView img_bmi_back;
    @BindView(R.id.txt_bmi_weight)
    TextView txt_bmi_weight;
    @BindView(R.id.txt_bmi_bmiversion)
    TextView txt_bmi_bmiversion;
    @BindView(R.id.txt_bmi_weight_value)
    TextView txt_bmi_weight_value;
    @BindView(R.id.txt_bmi_weighttolose)
    TextView txt_bmi_weighttolose;
    @BindView(R.id.txt_bmi_subgoal1)
    TextView txt_bmi_subgoal1;
    @BindView(R.id.txt_bmi_subgoal2)
    TextView txt_bmi_subgoal2;
    @BindView(R.id.donut_progress)
    DonutProgress donut_progress;
    @BindView(R.id.img_bmi_progress)
    ImageView img_bmi_progress;
    @BindView(R.id.img_bmi_battery)
    ImageView img_bmi_battery;
    private LoadingData loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_details);
        //Bind ButterKnife to the view
        ButterKnife.bind(this);
        //Initialize Loader
        loader = new LoadingData(this);
        LoginShared.setWeightFromNotification(this, "0");
        Intent intent = getIntent();
        if (intent != null) {
            String serverUserId = intent.getStringExtra("serverUserId");
            String scaleUserId = intent.getStringExtra("ScaleUserId");
            Log.d("@@BMI-page : ", "Received-Data" + "\nserverUserId = " + serverUserId
                    + "\nscaleUserId = " + scaleUserId);
            //Call Api to list the BMI-Data
            callBMIApi(serverUserId, scaleUserId);
        }

        img_bmi_back.setOnClickListener(view -> {
            startActivity(new Intent(this, DashBoardActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finishAffinity();
        });

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
                    //Set values in View
                    BMIDetails dmBmiDetails = response.body().getData().getBMIDetails();
                    txt_bmi_bmiversion.setText("BMI " + dmBmiDetails.getBMI());

                    donut_progress.setProgress(Float.parseFloat(dmBmiDetails.getPercentage()));

                    if (dmBmiDetails.getWeight().contains(" ")) {
                        String split[] = dmBmiDetails.getWeight().split(Pattern.quote(" "));
                        if (split.length > 0) {
                            txt_bmi_weight.setText(split[0]);
                        }
                    } else
                        txt_bmi_weight.setText(dmBmiDetails.getWeight());
                    txt_bmi_weight_value.setText(dmBmiDetails.getPreferredUnit().equals("1") ? "KG" : "LBS");

                    txt_bmi_weighttolose.setText(dmBmiDetails.getWeighttolose());

                    txt_bmi_subgoal1.setText(dmBmiDetails.getSubgoal1());
                    txt_bmi_subgoal2.setText(dmBmiDetails.getSubgoal2());

                    int imageDrawable = 0;

                    switch (dmBmiDetails.getSubgoalImageName()) {
                        case "progress-bar2":
                            imageDrawable = R.drawable.progress_bar2;
                            break;
                        case "progress-bar4":
                            imageDrawable = R.drawable.progress_bar4;
                            break;
                        case "progress-bar6":
                            imageDrawable = R.drawable.progress_bar6;
                            break;
                        case "progress-bar8":
                            imageDrawable = R.drawable.progress_bar8;
                            break;
                        case "progress-bar10":
                            imageDrawable = R.drawable.progress_bar10;
                            break;
                        case "progress-bar12":
                            imageDrawable = R.drawable.progress_bar12;
                            break;
                        case "progress-bar14":
                            imageDrawable = R.drawable.progress_bar14;
                            break;
                        case "progress-bar16":
                            imageDrawable = R.drawable.progress_bar16;
                            break;
                        case "progress-bar18":
                            imageDrawable = R.drawable.progress_bar18;
                            break;
                        case "progress-bar20":
                            imageDrawable = R.drawable.progress_bar20;
                            break;
                        case "progress-bar22":
                            imageDrawable = R.drawable.progress_bar22;
                            break;
                        case "progress-bar24":
                            imageDrawable = R.drawable.progress_bar24;
                            break;
                        case "progress-bar26":
                            imageDrawable = R.drawable.progress_bar26;
                            break;
                        case "progress-bar28":
                            imageDrawable = R.drawable.progress_bar28;
                            break;
                        case "progress-full":
                            imageDrawable = R.drawable.progress_full;
                            break;
                        case "progress-empty":
                            imageDrawable = R.drawable.progress_empty;
                            break;
                    }

                    if (imageDrawable != 0)
                        img_bmi_progress.setImageResource(imageDrawable);

                    switch (dmBmiDetails.getBatteryimage()) {
                        case "battery0.png":
                            img_bmi_battery.setImageResource(R.drawable.battery0);
                            break;
                        case "battery1.png":
                            img_bmi_battery.setImageResource(R.drawable.battery1);
                            break;
                        case "battery2.png":
                            img_bmi_battery.setImageResource(R.drawable.battery2);
                            break;
                        case "battery3.png":
                            img_bmi_battery.setImageResource(R.drawable.battery3);
                            break;
                        case "battery4.png":
                            img_bmi_battery.setImageResource(R.drawable.battery4);
                            break;
                    }


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

    private BroadcastReceiver myBMIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String serverUserId = intent.getStringExtra("serverUserId");
            String scaleUserId = intent.getStringExtra("ScaleUserId");
            Log.d("@@BMI-Broadcast : ", "Received on BMI-Page" + "\nserverUserId = " + serverUserId
                    + "\nscaleUserId = " + scaleUserId);
            //Call Api for BMI
            callBMIApi(serverUserId, scaleUserId);
        }
    };
}
