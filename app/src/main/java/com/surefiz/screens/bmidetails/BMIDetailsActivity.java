package com.surefiz.screens.bmidetails;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
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

import java.util.ArrayList;
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
    /*@BindView(R.id.donut_progress)
    DonutProgress donut_progress;*/
    @BindView(R.id.img_bmi_progress)
    ImageView img_bmi_progress;
    @BindView(R.id.img_bmi_battery)
    ImageView img_bmi_battery;
    @BindView(R.id.bmiProgressChart)
    PieChart bmiProgressChart;
    private LoadingData loader;
    private int numberOfSlices = 40;
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

        //setBMIProgressChart("10.0");
        //showBMIProgressReport();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this, DashBoardActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finishAffinity();
    }

    /*private void showBMIProgressReport() {
        txt_bmi_subgoal1.setText("150");
        txt_bmi_subgoal2.setText("155");

        int imageDrawable = 0;

        switch ("progress-empty") {
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
    }*/

    private void setBMIProgressChart(String bmiPercentage) {
        bmiProgressChart.setUsePercentValues(true);
        bmiProgressChart.getDescription().setEnabled(false);
        bmiProgressChart.setExtraOffsets(5, 10, 5, 5);
        bmiProgressChart.setDragDecelerationFrictionCoef(0.95f);
        bmiProgressChart.setDrawHoleEnabled(true);
        bmiProgressChart.setHoleColor(Color.TRANSPARENT);
        bmiProgressChart.setTransparentCircleColor(Color.WHITE);
        bmiProgressChart.setTransparentCircleAlpha(110);
        bmiProgressChart.setHoleRadius(58f);
        bmiProgressChart.setTransparentCircleRadius(61f);
        bmiProgressChart.setDrawCenterText(true);
        bmiProgressChart.setRotationAngle(270);
        bmiProgressChart.setRotationEnabled(false);
        bmiProgressChart.setHighlightPerTapEnabled(false);


        bmiProgressChart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = bmiProgressChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(-20f);
        l.setYEntrySpace(-10f);
        l.setYOffset(0f);
        l.setCustom(new ArrayList<>());

        bmiProgressChart.setEntryLabelColor(Color.WHITE);
        bmiProgressChart.setEntryLabelTextSize(12f);

        // calculation of values to be displayed
        ArrayList<PieEntry> entries = setChartValues();

        int bmiValue = Math.round(Float.parseFloat(bmiPercentage));

        //bmiProgressChart.setCenterText(bmiPercentage + "%");
        bmiProgressChart.setCenterText(bmiValue + "%");
        bmiProgressChart.setCenterTextColor(Color.WHITE);
        bmiProgressChart.setCenterTextSize(15f);


        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);


        // Setting the color code for the slices
        //ArrayList<Integer> colors = setColorForChart(bmiPercentage);


        ArrayList<Integer> colors = setColorForChart(bmiValue);

        if (bmiValue > 0) {
            dataSet.setColors(colors);
        }else {
            dataSet.setColor(Color.TRANSPARENT);
        }

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(bmiProgressChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        bmiProgressChart.setData(data);

        // undo all highlights
        bmiProgressChart.highlightValues(null);
        bmiProgressChart.invalidate();
    }

    private ArrayList<Integer> setColorForChart(int bmiPercentage) {


        ArrayList<Integer> colors = new ArrayList<>();

        if (bmiPercentage < 1) {
            return colors;
        }

        //float bmiValue = Float.parseFloat(bmiPercentage);
        //float val = (numberOfSlices/100.0f)*bmiValue;
        int achievedSlices = (int) ((numberOfSlices / 100.0f) * bmiPercentage);
        int leftOverSlices = numberOfSlices - achievedSlices;


        // Achieved steps should be displayed in White color
        for (int i = 0; i < achievedSlices; i++) {
            colors.add(Color.rgb(255, 255, 255));
        }


        // Leftover steps will be displayed in Transparent color
        for (int i = 0; i < leftOverSlices; i++) {
            colors.add(Color.argb(40, 0, 0, 0));
        }

        return colors;

    }


    private ArrayList<PieEntry> setChartValues() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Making the Progress bar basis on total number of steps
        // numberOfSlices= 40 for time being

        for (int i = 0; i < numberOfSlices; i++) {
            PieEntry pieEntry = new PieEntry((float) 100 / numberOfSlices);
            entries.add(pieEntry);
        }

        return entries;

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

        /*final Call<BMIResponse> call_BMIDetailsApi = apiInterface
                .call_BMIDetailsApi(token, "9222","1000000099");*/

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

                    //donut_progress.setProgress(Float.parseFloat(dmBmiDetails.getPercentage()));

                    setBMIProgressChart(dmBmiDetails.getPercentage());

                    if (dmBmiDetails.getWeight().contains(" ")) {
                        String[] split = dmBmiDetails.getWeight().split(Pattern.quote(" "));
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
                        case "progress-bar2.jpg":
                            imageDrawable = R.drawable.progress_bar2;
                            break;
                        case "progress-bar4.jpg":
                            imageDrawable = R.drawable.progress_bar4;
                            break;
                        case "progress-bar6.jpg":
                            imageDrawable = R.drawable.progress_bar6;
                            break;
                        case "progress-bar8.jpg":
                            imageDrawable = R.drawable.progress_bar8;
                            break;
                        case "progress-bar10.jpg":
                            imageDrawable = R.drawable.progress_bar10;
                            break;
                        case "progress-bar12.jpg":
                            imageDrawable = R.drawable.progress_bar12;
                            break;
                        case "progress-bar14.jpg":
                            imageDrawable = R.drawable.progress_bar14;
                            break;
                        case "progress-bar16.jpg":
                            imageDrawable = R.drawable.progress_bar16;
                            break;
                        case "progress-bar18.jpg":
                            imageDrawable = R.drawable.progress_bar18;
                            break;
                        case "progress-bar20.jpg":
                            imageDrawable = R.drawable.progress_bar20;
                            break;
                        case "progress-bar22.jpg":
                            imageDrawable = R.drawable.progress_bar22;
                            break;
                        case "progress-bar24.jpg":
                            imageDrawable = R.drawable.progress_bar24;
                            break;
                        case "progress-bar26.jpg":
                            imageDrawable = R.drawable.progress_bar26;
                            break;
                        case "progress-bar28.jpg":
                            imageDrawable = R.drawable.progress_bar28;
                            break;
                        case "progress-full.jpg":
                            imageDrawable = R.drawable.progress_full;
                            break;
                        case "progress-empty.jpg":
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
}
