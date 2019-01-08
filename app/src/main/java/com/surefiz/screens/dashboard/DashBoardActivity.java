package com.surefiz.screens.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIArea;
import com.highsoft.highcharts.common.hichartsclasses.HICSSObject;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIColumn;
import com.highsoft.highcharts.common.hichartsclasses.HICondition;
import com.highsoft.highcharts.common.hichartsclasses.HIData;
import com.highsoft.highcharts.common.hichartsclasses.HIDataLabels;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HIHover;
import com.highsoft.highcharts.common.hichartsclasses.HILabel;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions3d;
import com.highsoft.highcharts.common.hichartsclasses.HIPie;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPoint;
import com.highsoft.highcharts.common.hichartsclasses.HIResponsive;
import com.highsoft.highcharts.common.hichartsclasses.HIRules;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HISpline;
import com.highsoft.highcharts.common.hichartsclasses.HIStates;
import com.highsoft.highcharts.common.hichartsclasses.HIStyle;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.ErrorMessageDialog;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.dashboard.adapter.ContactListAdapter;
import com.surefiz.screens.dashboard.model.DashboardModel;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.progressstatus.ProgressStatusActivity;
import com.surefiz.screens.singlechart.SingleChartActivity;
import com.surefiz.screens.users.model.UserList;
import com.surefiz.screens.users.model.UserListModel;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DashBoardActivity extends BaseActivity implements ContactListAdapter.OnCircleViewClickListener {

    public View view;
    HIChartView chartView, chartViewLoss, chartViewBmi, chartViewGoals, chartViewSubGoals, chartViewAchiGoals;
    TextView tv_name, tv_mac, tv_weight_dynamic, tv_height_dynamic, tv_recorded;
    Button btn_fat, btn_bone, btn_muscle, btn_bmi, btn_water, btn_protein;
    CardView cv_weight, cv_weight_loss, cv_bmi, cv_goals, cv_sub_goals, cv_achi_goals;
    RecyclerView rv_items;
    private LoadingData loader;
    HIOptions options, optionsLoss, optionsBMI, optionsGoals, optionsSubGoals, optionsAchiGoals;
    List<UserList> contactLists = new ArrayList<>();
    ContactListAdapter adapter;
    public String id = "";
    public int row_user = -1;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_dash_board, null);
        addContentView(view);
        loader = new LoadingData(this);
        options = new HIOptions();
        optionsLoss = new HIOptions();
        optionsBMI = new HIOptions();
        optionsGoals = new HIOptions();
        optionsSubGoals = new HIOptions();
        optionsAchiGoals = new HIOptions();
        viewBind();
        setHeaderView();
        LoginShared.setWeightPageFrom(DashBoardActivity.this, "0");

        if (getIntent().getStringExtra("expired") != null) {
            if (getIntent().getStringExtra("expired").equals("1")) {
                MethodUtils.errorMsg(DashBoardActivity.this, "Your weight details notification has been expired.");
            }
        }

        if (getIntent().getStringExtra("id") != null) {
            id = getIntent().getStringExtra("id");
            if (getIntent().getStringExtra("page").equals("1")) {
                rl_back.setVisibility(View.VISIBLE);
                tv_universal_header.setText("Performance");
                img_topbar_menu.setVisibility(View.GONE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                rl_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DashBoardActivity.this, AcountabilityActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                });
                rv_items.setVisibility(View.GONE);
            } else {
                rl_back.setVisibility(View.GONE);
                img_topbar_menu.setVisibility(View.VISIBLE);
                tv_universal_header.setText("Dashboard");
                rv_items.setVisibility(View.VISIBLE);
                row_user = 1;
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        } else {
            id = LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId();
            rv_items.setVisibility(View.VISIBLE);
        }

        if (!ConnectionDetector.isConnectingToInternet(DashBoardActivity.this)) {
            MethodUtils.errorMsg(DashBoardActivity.this, DashBoardActivity.this.getString(R.string.no_internet));
        } else {
            callDashBoardApi(id);
            callUserListApi();
        }

        setRecyclerViewItem();
        //callContactsApi();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getStringExtra("id") != null) {
            if (getIntent().getStringExtra("page").equals("1")) {
                Intent intent = new Intent(DashBoardActivity.this, AcountabilityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        }
    }

    private void setRecyclerViewItem() {
        adapter = new ContactListAdapter(this, contactLists, this, row_user);
        rv_items.setAdapter(adapter);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        rv_items.addItemDecoration(decoration);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_items.setLayoutManager(mLayoutManager);
    }

    private void callUserListApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<UserListModel> userListModelCall = apiInterface.call_userListApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());

        userListModelCall.enqueue(new Callback<UserListModel>() {
            @Override
            public void onResponse(Call<UserListModel> call, Response<UserListModel> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        contactLists.clear();
                        //      Collections.reverse(response.body().getData().getUserList());
                        contactLists.addAll(response.body().getData().getUserList());
                        adapter.notifyDataSetChanged();

                        if (id != null && !id.equals("")) {
                            for (int i = 0; i < contactLists.size(); i++) {
                                if (String.valueOf(contactLists.get(i).getServerUserId()).equals(id)) {
                                    //  rv_items.smoothScrollToPosition(i);
                                    mLayoutManager.scrollToPositionWithOffset(i, 0);
                                    break;
                                }
                            }
                        } else {
                            // rv_items.smoothScrollToPosition(0);
                            mLayoutManager.scrollToPositionWithOffset(0, 0);
                        }

                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(DashBoardActivity.this);
                        LoginShared.destroySessionTypePreference(DashBoardActivity.this);
                        LoginShared.setDeviceToken(DashBoardActivity.this, deviceToken);
                        Intent loginIntent = new Intent(DashBoardActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        MethodUtils.errorMsg(DashBoardActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<UserListModel> call, Throwable t) {
                MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    /*private void callContactsApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ContactListModel> call_contactApi = apiInterface.call_contactListApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());

        call_contactApi.enqueue(new Callback<ContactListModel>() {
            @Override
            public void onResponse(Call<ContactListModel> call, Response<ContactListModel> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        contactLists.clear();
                        contactLists.addAll(response.body().getData().getUserList());
                        adapter.notifyDataSetChanged();
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(DashBoardActivity.this);
                        LoginShared.destroySessionTypePreference();
                        LoginShared.setDeviceToken(DashBoardActivity.this, deviceToken);
                        Intent loginIntent = new Intent(DashBoardActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        MethodUtils.errorMsg(DashBoardActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ContactListModel> call, Throwable t) {
                MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
            }
        });

    }*//*8502,8658*/


    private void callDashBoardApi(String id) {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> call_dashboardApi = apiInterface.call_dashboardApi(
                LoginShared.getRegistrationDataModel(this).getData().getToken(),
                id);
        call_dashboardApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {
                    String responseString = response.body().string();
                    Gson gson = new Gson();
                    DashboardModel dashboardModel;
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.optInt("status") == 1) {
                        dashboardModel = gson.fromJson(responseString, DashboardModel.class);
                        LoginShared.setDashBoardDataModel(DashBoardActivity.this, dashboardModel);
//                        checkForShowView();

                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {
                                setWeightChart();
                                setWeightLossChart();
                                setBMIChart();
                                showGoalsChart();
                                setSubGoalsChart();
                                showGoalsAndAcheivementsChart();
                                setOtherOptions();
                            }
                        }));

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(DashBoardActivity.this);
                        LoginShared.destroySessionTypePreference(DashBoardActivity.this);
                        LoginShared.setDeviceToken(DashBoardActivity.this, deviceToken);
                        Intent loginIntent = new Intent(DashBoardActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(DashBoardActivity.this, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void checkForShowView() {
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("weightProgress")) {
            cv_weight.setVisibility(View.VISIBLE);
        } else {
            cv_weight.setVisibility(View.GONE);
        }

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("weightLoss")) {
            cv_weight_loss.setVisibility(View.VISIBLE);
        } else {
            cv_weight_loss.setVisibility(View.GONE);
        }

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("BMI")) {
            cv_bmi.setVisibility(View.VISIBLE);
        } else {
            cv_bmi.setVisibility(View.GONE);
        }

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("nextSubGoal")) {
            cv_goals.setVisibility(View.VISIBLE);
        } else {
            cv_goals.setVisibility(View.GONE);
        }

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("subGoalsProgress")) {
            cv_sub_goals.setVisibility(View.VISIBLE);
        } else {
            cv_sub_goals.setVisibility(View.GONE);
        }

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("goals")) {
            cv_achi_goals.setVisibility(View.VISIBLE);
        } else {
            cv_achi_goals.setVisibility(View.GONE);
        }
    }

    private void setOtherOptions() {
        tv_name.setText("Name: " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getUserName());
        tv_mac.setText("Scale Mac Address: " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getScaleMacAddress());
        tv_weight_dynamic.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWeight());
        tv_height_dynamic.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getHeight());
        btn_fat.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBodyFat());
        btn_bone.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBoneKg());
        btn_muscle.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getMuscle());
        btn_bmi.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBMI());
        btn_water.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWater());
        btn_protein.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getProtein());
        tv_recorded.setText("Recorded on " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getRecordedOn());
    }

    private void viewBind() {
        chartView = (HIChartView) findViewById(R.id.hc_weight);
        chartView.setOptions(options);
        chartViewLoss = (HIChartView) findViewById(R.id.hc_weight_loss);
        chartViewLoss.setOptions(optionsLoss);
        chartViewBmi = (HIChartView) findViewById(R.id.hc_bmi);
        chartViewBmi.setOptions(optionsBMI);
        chartViewGoals = (HIChartView) findViewById(R.id.hc_goals);
        chartViewGoals.setOptions(optionsGoals);
        chartViewSubGoals = (HIChartView) findViewById(R.id.hc_sub_goals);
        chartViewSubGoals.setOptions(optionsSubGoals);
        chartViewAchiGoals = (HIChartView) findViewById(R.id.hc_achi_goals);
        chartViewAchiGoals.setOptions(optionsAchiGoals);
        tv_name = findViewById(R.id.tv_name);
        tv_mac = findViewById(R.id.tv_mac);
        tv_weight_dynamic = findViewById(R.id.tv_weight_dynamic);
        tv_height_dynamic = findViewById(R.id.tv_height_dynamic);
        btn_fat = findViewById(R.id.btn_fat);
        btn_bone = findViewById(R.id.btn_bone);
        btn_muscle = findViewById(R.id.btn_muscle);
        btn_bmi = findViewById(R.id.btn_bmi);
        btn_water = findViewById(R.id.btn_water);
        btn_protein = findViewById(R.id.btn_protein);
        tv_recorded = findViewById(R.id.tv_recorded);
        cv_weight = findViewById(R.id.cv_weight);
        cv_weight_loss = findViewById(R.id.cv_weight_loss);
        cv_bmi = findViewById(R.id.cv_bmi);
        cv_goals = findViewById(R.id.cv_goals);
        cv_sub_goals = findViewById(R.id.cv_sub_goals);
        cv_achi_goals = findViewById(R.id.cv_achi_goals);
        rv_items = findViewById(R.id.rv_items);


       /* cv_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this,
                        SingleChartActivity.class);
                startActivity(intent);
            }
        });*/
    }

    private void setSubGoalsChart() {

        HIChart chart = new HIChart();
        //    chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));

        HIGradient gradient = new HIGradient(1, 1, 1, 1);

        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithHexValue("#ff3300")));
        stops.add(new HIStop(1, HIColor.initWithHexValue("#3399ff")));
        //    stops.add(new HIStop(1, HIColor.initWithRGB(60, 60, 60)));

        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        chart.setType("column");
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        hiLegend.setItemStyle(hicssObject);
        optionsSubGoals.setLegend(hiLegend);
        HIOptions3d hiOptions3d = new HIOptions3d();
        hiOptions3d.setEnabled(true);
        hiOptions3d.setAlpha(15);
        hiOptions3d.setBeta(15);
        hiOptions3d.setViewDistance(25);
        hiOptions3d.setDepth(40);
        optionsSubGoals.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Sub Goals Progress</p>");
        optionsSubGoals.setTitle(title);

        HIXAxis xAxis = new HIXAxis();
        String[] categoriesList = new String[]{"Apples", "Oranges", "Pears", "Grapes", "Bananas"};
        xAxis.setCategories((ArrayList<String>)
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getSubGoalsProgress().getWeeks());
        HILabels hiLabels = new HILabels();
        hiLabels.setStyle(hicssObject);
        xAxis.setLabels(hiLabels);
        hiLabels.setSkew3d(true);
        HIStyle hiStyle = new HIStyle();
        hiStyle.setFontSize("16px");
        optionsSubGoals.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        optionsSubGoals.setExporting(exporting);

        HIYAxis yAxis = new HIYAxis();
        yAxis.setAllowDecimals(false);
        yAxis.setMin(0);
        HITitle hiTitle = new HITitle();
        hiTitle.setText("<p style='color: #ffffff; '>Values</p>");
        hiTitle.setSkew3d(true);
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        optionsSubGoals.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITooltip tooltip = new HITooltip();
        tooltip.setHeaderFormat("<b>{point.key}</b><br>");
        tooltip.setPointFormat("<span style=\"color:{series.color}\"></span> {series.name}: {point.y} / {point.stackTotal}");
        optionsSubGoals.setTooltip(tooltip);

        HIPlotOptions plotOptions = new HIPlotOptions();
        HIColumn hiColumn = new HIColumn();
        hiColumn.setStacking("normal");
        hiColumn.setDepth(40);
        optionsSubGoals.setPlotOptions(plotOptions);

        HIColumn series1 = new HIColumn();
        series1.setColorByPoint(true);
        series1.setName("Expected Weight To Go");
//        series1.setColor(HIColor.initWithRGB(73,183,130));
//        series1.setColor(HIColor.initWithHexValue("#49b782"));
        Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getSubGoalsProgress().getExpectedWeightToGo().toArray(new Number[0]);
        /*Object[] series1_data = new Object[]{
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().getData()};*/

        Number[] numbers = new Number[series1_data.length];
        ArrayList<String> colors1 = new ArrayList<>();
        ArrayList<String> colors2 = new ArrayList<>();

        for (int i = 0; i < series1_data.length; i++) {
            numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
            colors1.add("#49b782");
        }
        series1.setShowInLegend(true);
        series1.setColor(HIColor.initWithRGB(73, 183, 130));
        series1.setColors(colors1);
        series1.setData(new ArrayList<>(Arrays.asList(numbers)));
//        series1.setStack("male");
        HIColumn series2 = new HIColumn();
        series2.setColorByPoint(true);
        series2.setName("Acheived Weight");
//        series2.setColor(HIColor.initWithRGB(255,175,68));
//        series2.setColor(HIColor.initWithHexValue("#FFAF44"));
        Number[] series2_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getSubGoalsProgress().getAcheivedWeight().toArray(new Number[0]);
        Number[] numbers1 = new Number[series2_data.length];
        for (int i = 0; i < series2_data.length; i++) {
            numbers1[i] = Double.parseDouble(String.valueOf(series2_data[i]));
            colors2.add("#FFAF44");
        }
//        series2.setColor(HIColor.initWithRGB(255,175,68));
        series2.setShowInLegend(true);
        series2.setColor(HIColor.initWithRGB(255, 175, 68));
        series2.setColors(colors2);
        series2.setData(new ArrayList<>(Arrays.asList(numbers1)));

        optionsSubGoals.setSeries(new ArrayList<>(Arrays.asList(series1, series2/*, series3, series4*/)));

        chartViewSubGoals.setOptions(optionsSubGoals);
        chartViewSubGoals.reload();
    }

    private void showGoalsChart() {
        chartViewGoals.plugins = new ArrayList<>(Arrays.asList("drilldown"));

        HIChart chart = new HIChart();
        //Required for gradient Background
        HIGradient gradient = new HIGradient(0, 0, 0, 1);
        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(255, 204, 102)));
        stops.add(new HIStop(1, HIColor.initWithRGB(51, 204, 51)));
        //Set Color
        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
//        hicssObject.setColor("#ffffff");
        optionsGoals.setLegend(hiLegend);
        chart.setType("column");
        optionsGoals.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Next Sub Goals</p>");
        optionsGoals.setTitle(title);

//        HISubtitle subtitle = new HISubtitle();
//        subtitle.setText("Click the columns to view versions.'Source': <a href=\"http://netmarketshare.com\">netmarketshare.com</a>.");
//        options.setSubtitle(subtitle);

       /* HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(false);
        xAxis.setCategories((ArrayList<String>)
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                        getWeightProgress().getLabel());
        *//*HILabels labels = new HILabels();
        HIFunction hiFunction = new HIFunction("function () { return this.value; }");
        labels.setFormatter(hiFunction);
        labels.setStyle(hicssObject);
        xAxis.setLabels(labels);*//*
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});*/

        HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(false);
        xAxis.setType("category");
        xAxis.setCategories((ArrayList<String>)
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getNextSubGoal().getLabel());
        /*HILabels labels = new HILabels();
        labels.setStyle(hicssObject);
        xAxis.setLabels(labels);*/
        optionsGoals.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});
        xAxis.setVisible(false);

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        HILabels hiLabels = new HILabels();
        hiTitle.setText("<p style='color: #ffffff; '></p>");
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        yAxis.setGridLineColor(HIColor.initWithRGBA(0, 0, 0, 0.3));
        optionsGoals.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HIPlotOptions hiPlotOptions = new HIPlotOptions();
        HIColumn hiColumn = new HIColumn();
        HIDataLabels hiDataLabels = new HIDataLabels();
        hiDataLabels.setEnabled(true);
        hiDataLabels.setColor(HIColor.initWithRGB(30, 72, 179));
        hiColumn.setDataLabels(hiDataLabels);
        hiPlotOptions.setColumn(hiColumn);
        optionsGoals.setPlotOptions(hiPlotOptions);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        optionsGoals.setLegend(legend);


        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        optionsGoals.setExporting(exporting);

//        optionsGoals.setPlotOptions(plotOptions);

        HIColumn series1 = new HIColumn();
        series1.setName("Brands");
//        series1.setColor(HIColor.initWithHexValue("#77D48B"));
//        series1.setColorByPoint(false);

        /*HIPlotOptions plotOptions = new HIPlotOptions();
        HISeries hiSeries = new HISeries();
        hiSeries.setBorderWidth(hiSeries);
        HIDataLabels hiDataLabels = new HIDataLabels();
        hiDataLabels.setEnabled(true);
        hiDataLabels.setFormat("{point.y:.1f}%");
        optionsGoals.setPlotOptions(plotOptions);

        HITooltip tooltip = new HITooltip();
        tooltip.setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>");
        tooltip.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>");
        optionsGoals.setTooltip(tooltip);*/

        Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getNextSubGoal().
                getData().toArray(new Number[0]);
        /*Object[] series1_data = new Object[]{
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().getData()};*/
//        Number[] numbers = new Number[series1_data.length];
        Number[] numbers = new Number[series1_data.length];
        ArrayList<HIColor> hiColors = new ArrayList<>();
        ArrayList<String> colors = new ArrayList<>();
        for (int i = 0; i < series1_data.length; i++) {
            numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
            colors.add("#77D48B");
            /*HashMap<String, Object> map1 = new HashMap<>();
            map1.put("y", Double.parseDouble(String.valueOf(series1_data[i])));
            map1.put("color", "#77D48B");
            numbers[i].put("map1", map1);*/
        }

        series1.setData(new ArrayList<>(Arrays.asList(numbers)));
        optionsGoals.setColors(colors);

//        HashMap[] series1_data = new HashMap[] { map1, map2, map3, map4, map5, map6 };
//        series1.data = new ArrayList<>(Arrays.asList(series1_data));
//        options.series = new ArrayList<>(Arrays.asList(series1));

        /*HashMap<String, Object> map1 = new HashMap<>();
        map1.put("name", "Microsoft Internet Explorer");
        map1.put("y", 56.33);
        map1.put("drilldown", "Microsoft Internet Explorer");

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("name", "Chrome");
        map2.put("y", 24.03);
        map2.put("drilldown", "Chrome");

        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("name", "Firefox");
        map3.put("y", 10.38);
        map3.put("drilldown", "Firefox");

        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("name", "Safari");
        map4.put("y", 4.77);
        map4.put("drilldown", "Safari");

        HashMap<String, Object> map5 = new HashMap<>();
        map5.put("name", "Opera");
        map5.put("y", 0.91);
        map5.put("drilldown", "Opera");

        HashMap<String, Object> map6 = new HashMap<>();
        map6.put("name", "Proprietary or Undetectable");
        map6.put("y", 0.2);
        map6.put("drilldown", null);*/

        optionsGoals.setSeries(new ArrayList<>(Arrays.asList(series1)));

        /*HIDrilldown drilldown = new HIDrilldown();

        HIColumn series2 = new HIColumn();
        series2.setName("Microsoft Internet Explorer");
        series2.setId("Microsoft Internet Explorer");

        Object[] object1 = new Object[]{"v11.0", 24.13};
        Object[] object2 = new Object[]{"v8.0", 17.2};
        Object[] object3 = new Object[]{"v9.0", 8.11};
        Object[] object4 = new Object[]{"v10.0", 5.33};
        Object[] object5 = new Object[]{"v6.0", 1.06};
        Object[] object6 = new Object[]{"v7.0", 0.5};

        series2.setData(new ArrayList<>(Arrays.asList(object1, object2, object3, object4, object5, object6)));

        HIColumn series3 = new HIColumn();
        series3.setName("Chrome");
        series3.setId("Chrome");

        Object[] object7 = new Object[]{"v41.0", 4.32};
        Object[] object8 = new Object[]{"v42.0", 3.68};
        Object[] object9 = new Object[]{"v39.0", 2.96};
        Object[] object10 = new Object[]{"v36.0", 2.53};
        Object[] object11 = new Object[]{"v43.0", 1.45};
        Object[] object12 = new Object[]{"v31.0", 1.24};
        Object[] object13 = new Object[]{"v35.0", 0.85};
        Object[] object14 = new Object[]{"v38.0", 0.6};
        Object[] object15 = new Object[]{"v32.0", 0.55};
        Object[] object16 = new Object[]{"v37.0", 0.38};
        Object[] object17 = new Object[]{"v33.0", 0.19};
        Object[] object18 = new Object[]{"v34.0", 0.14};
        Object[] object19 = new Object[]{"v30.0", 0.14};
        Object[] object20 = new Object[]{"v40.0", 5};

        series3.setData(new ArrayList<>(Arrays.asList(object7, object8, object9, object10, object11, object12, object13, object14, object15, object16, object17, object18, object19, object20)));

        HIColumn series4 = new HIColumn();
        series4.setName("Firefox");
        series4.setId("Firefox");

        Object[] object21 = new Object[]{"v35", 2.76};
        Object[] object22 = new Object[]{"v36", 2.32};
        Object[] object23 = new Object[]{"v37", 2.31};
        Object[] object24 = new Object[]{"v34", 1.27};
        Object[] object25 = new Object[]{"v38", 1.02};
        Object[] object26 = new Object[]{"v31", 0.33};
        Object[] object27 = new Object[]{"v33", 0.22};
        Object[] object28 = new Object[]{"v32", 0.15};

        series4.setData(new ArrayList<>(Arrays.asList(object21, object22, object23, object24, object25, object26, object27, object28)));

        HIColumn series5 = new HIColumn();
        series5.setName("Safari");
        series5.setId("Safari");

        Object[] object29 = new Object[]{"v8.0", 2.56};
        Object[] object30 = new Object[]{"v7.1", 0.77};
        Object[] object31 = new Object[]{"v5.1", 0.42};
        Object[] object32 = new Object[]{"v5.0", 0.3};
        Object[] object33 = new Object[]{"v6.1", 0.29};
        Object[] object34 = new Object[]{"v7.0", 0.26};
        Object[] object35 = new Object[]{"v6.2", 0.17};

        series5.setData(new ArrayList<>(Arrays.asList(object29, object30, object31, object32, object33, object34, object35)));

        HIColumn series6 = new HIColumn();
        series6.setName("Opera");
        series6.setId("Opera");

        Object[] object36 = new Object[]{"v12.x", 0.34};
        Object[] object37 = new Object[]{"v28", 0.24};
        Object[] object38 = new Object[]{"v27", 0.17};
        Object[] object39 = new Object[]{"v29", 0.16};

        series6.setData(new ArrayList<>(Arrays.asList(object36, object37, object38, object39)));*/

//        HIColumn[] seriesList = new HIColumn[]{series2, series3, series4, series5, series6};
//        drilldown.setSeries(new ArrayList<>(Arrays.asList(seriesList)));
//        optionsGoals.setDrilldown(drilldown);


        chartViewGoals.setOptions(optionsGoals);
        chartViewGoals.reload();
    }

    private void setBMIChart() {
        chartView.plugins = new ArrayList<>(Arrays.asList("series-label"));

        HIChart chart = new HIChart();
        //Required for gradient Background
        HIGradient gradient = new HIGradient(0, 0, 0, 1);
        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(56, 239, 125)));
        stops.add(new HIStop(1, HIColor.initWithRGB(17, 153, 142)));
        //Set Color
        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        chart.setType("spline");
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        hiLegend.setItemStyle(hicssObject);
        optionsBMI.setLegend(hiLegend);
        optionsBMI.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>BMI</p>");
        optionsBMI.setTitle(title);

        /*HISubtitle subtitle = new HISubtitle();
        subtitle.setText("<p style='color: #ffffff; text-align: center;'>Source: WorldClimate.com</p>");
        options.setSubtitle(subtitle);*/

        HIXAxis xAxis = new HIXAxis();
        HILabels labels = new HILabels();
        xAxis.setAllowDecimals(false);
        // String[] categoriesList = new String[]{LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().getLabel()};
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().
                getLabel() != null ||
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                        getBMI().getLabel().size() > 0) {
            xAxis.setCategories((ArrayList<String>)
                    LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                            getBMI().getLabel());
        }
        xAxis.setVisible(false);
//        labels.setStyle(hicssObject);
//        xAxis.setLabels(labels);
        optionsBMI.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        HILabels hiLabels = new HILabels();
        hiTitle.setText("<p style='color: #ffffff; '>Values</p>");
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);
        optionsBMI.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});
        yAxis.setVisible(false);

        HITooltip tooltip = new HITooltip();
        tooltip.setShared(true);
        optionsBMI.setTooltip(tooltip);

        /*HIPlotOptions plotOptions = new HIPlotOptions();
        HIArea hiArea = new HIArea();
        // hiArea.setPointStart(1940);
        hiArea.setPointStart(0);
        HIMarker marker = new HIMarker();
        marker.setEnabled(true);
        marker.setSymbol("circle");
        marker.setFillColor(HIColor.initWithRGB(255,255,255));
        marker.setRadius(3);
        HIStates hiStates = new HIStates();
        HIHover hiHover = new HIHover();
        hiStates.setHover(hiHover);
        hiArea.setMarker(marker);
        hiHover.setEnabled(true);
        hiStates.setHover(hiHover);
        plotOptions.setArea(hiArea);
        options.setPlotOptions(plotOptions);*/

        HIPlotOptions plotOptions = new HIPlotOptions();
        HISpline hiSpline = new HISpline();
        HIMarker hiMarker = new HIMarker();
        HIArea hiArea = new HIArea();
        hiMarker.setEnabled(true);
        hiMarker.setSymbol("circle");
        hiMarker.setFillColor(HIColor.initWithHexValue("#ffffff"));
        hiMarker.setRadius(4);
        hiMarker.setLineColor(HIColor.initWithRGB(102, 102, 102));
        hiMarker.setLineWidth(1);
        hiArea.setMarker(hiMarker);
        plotOptions.setArea(hiArea);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        optionsBMI.setExporting(exporting);

        optionsBMI.setPlotOptions(plotOptions);

        HISpline series1 = new HISpline();
        series1.setName("Weight");
        series1.setColor(HIColor.initWithRGB(255, 255, 255));
//        series1.setLineColor(HIColor.initWithRGB(255,255,255));
        /*HIMarker hiMarker1 = new HIMarker();
        hiMarker1.setSymbol("square");
        HIData data1 = new HIData();
        data1.setY(26.5);
        HIMarker hiMarker2 = new HIMarker();
        hiMarker2.setSymbol("url(https://www.highcharts.com/samples/graphics/sun.png)");*/

       /* CharSequence[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getWeightProgress().getData().toArray(new CharSequence[0]);
        Number[] numbers = new Number[series1_data.length];
        for (int i = 0; i < series1_data.length; i++) {
            numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
        }
        //Number[] series1_data = new Number[]{null, null, null, null, null, 6, 11, 32, 110, 235, 369, 640, 1005, 1436, 2063, 3057, 4618, 6444, 9822, 15468, 20434, 24126, 27387, 29459, 31056, 31982, 32040, 31233, 29224, 27342, 26662, 26956, 27912, 28999, 28965, 27826, 25579, 25722, 24826, 24605, 24304, 23464, 23708, 24099, 24357, 24237, 24401, 24344, 23586, 22380, 21004, 17287, 14747, 13076, 12555, 12144, 11009, 10950, 10871, 0};
        series1.setData(new ArrayList<>(Arrays.asList(numbers)));*/

        Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().getData().toArray(new Number[0]);
        /*Object[] series1_data = new Object[]{
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().getData()};*/
        Number[] numbers = new Number[series1_data.length];
        for (int i = 0; i < series1_data.length; i++) {
            numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
        }
        series1.setData(new ArrayList<>(Arrays.asList(numbers)));

        /*HISpline series2 = new HISpline();
        series2.setName("London");
        HIMarker hiMarker3 = new HIMarker();
        hiMarker3.setSymbol("diamond");
        HIData data2 = new HIData();
        data2.setY(3.9);
        HIMarker hiMarker4 = new HIMarker();
        hiMarker4.setSymbol("url(https://www.highcharts.com/samples/graphics/snow.png)");

        Object[] series2_data = new Object[]{data2, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8};
        series2.setData(new ArrayList<>(Arrays.asList(series2_data)));*/

        optionsBMI.setSeries(new ArrayList<>(Arrays.asList(series1/*, series2*/)));


        chartViewBmi.setOptions(optionsBMI);
        chartViewBmi.reload();
    }

    private void setWeightLossChart() {

        HIChart chart = new HIChart();

        //Required for gradient Background
        HIGradient gradient = new HIGradient(0, 0, 0, 1);
        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(220, 227, 91)));
        stops.add(new HIStop(1, HIColor.initWithRGB(69, 182, 73)));
        //Set Color
        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        chart.setPlotShadow(false);
        chart.setType("pie");
        optionsLoss.setChart(chart);

        ArrayList<String> colors = new ArrayList<>();
        HIGradient hiGradient = new HIGradient((float) 0.5, (float) 0.3, (float) 0.7);

        HashMap<String, Number> radialGradient = new HashMap<>();
        radialGradient.put("cx", 0.5);
        radialGradient.put("cy", 0.3);
        radialGradient.put("r", 0.7);

        colors.add("#40CDDE");
        colors.add("#FF3E42");
        //colors.add("#90ed7d");
        //colors.add("#8085e9");

        optionsLoss.setColors(colors);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        optionsLoss.setExporting(exporting);

        HITitle title = new HITitle();
        title.setText("Weight Loss");
        optionsLoss.setTitle(title);

        HITooltip tooltip = new HITooltip();
        tooltip.setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");
        optionsLoss.setTooltip(tooltip);

        HIPlotOptions plotoptions = new HIPlotOptions();
        HIPie hiPie = new HIPie();
        hiPie.setAllowPointSelect(true);
        hiPie.setCursor("pointer");
        HIDataLabels hiDataLabels = new HIDataLabels();
        hiDataLabels.setEnabled(true);
        hiDataLabels.setFormat("<b>{point.name}</b>: {point.percentage:.1f} %");
        HIStyle hiStyle = new HIStyle();
        hiStyle.setColor("black");
        hiDataLabels.setConnectorColor(HIColor.initWithHexValue("#808080"));
        optionsLoss.setPlotOptions(plotoptions);

        HIPie pie = new HIPie();
        pie.setName("Brands");
        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("name", "Acheived");
        map1.put("y", LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getWeightLoss().getAchieved());

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("name", "ToGo");
        map2.put("y", LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getWeightLoss().getToGo());
        map2.put("sliced", true);
        map2.put("selected", true);

        /*HashMap<String, Object> map3 = new HashMap<>();
        map3.put("name", "Firefox");
        map3.put("y", 10.38);

        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("name", "Safari");
        map4.put("y", 4.77);

        HashMap<String, Object> map5 = new HashMap<>();
        map5.put("name", "Opera");
        map5.put("y", 0.91);

        HashMap<String, Object> map6 = new HashMap<>();
        map6.put("name", "Proprietary or Undetectable");
        map6.put("y", 0.2);*/

        pie.setData(new ArrayList<>(Arrays.asList(map1, map2/*, map3, map4, map5, map6*/)));

        optionsLoss.setSeries(new ArrayList<>(Collections.singletonList(pie)));

        chartViewLoss.setOptions(optionsLoss);
        chartViewLoss.reload();
    }

    private void setWeightChart() {

        HIChart chart = new HIChart();
        chart.setType("area");
        //Required for gradient Background
        HIGradient gradient = new HIGradient(0, 0, 0, 1);
        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(255, 51, 0)));
        stops.add(new HIStop(1, HIColor.initWithRGB(51, 153, 255)));
        //Set Color
        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        options.setChart(chart);
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");
        hiLegend.setItemStyle(hicssObject);
        options.setLegend(hiLegend);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Weight Progress</p>");
        options.setTitle(title);

//        HISubtitle subtitle = new HISubtitle();
//        subtitle.setText("<p style='color: #ffffff;'>Source: <a style='color: #ffffff;' href=\"http://thebulletin.metapress.com/content/c4120650912x74k7/fulltext.pdf\">thebulletin.metapress.com</a></p>");
//        options.setSubtitle(subtitle);

        HIXAxis xAxis = new HIXAxis();
        xAxis.setVisible(false);
        xAxis.setMin(0);
        xAxis.setAllowDecimals(true);
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                getWeightProgress().getLabel() != null ||
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                        getWeightProgress().getLabel().size() > 0) {
            xAxis.setCategories((ArrayList<String>)
                    LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                            getWeightProgress().getLabel());
        }
        /*HILabels labels = new HILabels();
        HIFunction hiFunction = new HIFunction("function () { return this.value; }");
        labels.setFormatter(hiFunction);
        labels.setStyle(hicssObject);
        xAxis.setLabels(labels);*/
        options.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});
        Number minY = null;
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                getWeightProgress().getData().size() > 0) {

            int minIndex = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                    getWeightProgress().getData().indexOf(Collections.min(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                    getWeightProgress().getData()));

            minY = Math.round(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                    getWeightProgress().getData().get(minIndex));
        } else {
            minY = 0;
        }

        /*Number maxY = Math.round(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                getWeightProgress().getData().get(0));*/

        HIYAxis yAxis = new HIYAxis();
        //   yAxis.setMax(maxY);
        yAxis.setMin(minY);

        yAxis.setVisible(false);
       /* HITitle hiTitle = new HITitle();
        hiTitle.setUseHTML(true);
    //    hiTitle.setText("<p style='color: #ffffff; '>Nuclear weapon states</p>");
        HILabels hiLabels = new HILabels();
        HIFunction hiFunction1 = new HIFunction("function () { return this.value ; }");
        hiLabels.setFormatter(hiFunction1);
        hiLabels.setStyle(hicssObject);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);*/
        options.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        HITooltip tooltip = new HITooltip();
        tooltip.setPointFormat("{series.name} produced <b>{point.y:,100.0f}</b><br/>warheads in {point.x}");
        options.setTooltip(tooltip);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);

        HIPlotOptions plotOptions = new HIPlotOptions();
        HIArea hiArea = new HIArea();
        // hiArea.setPointStart(1940);
        hiArea.setPointStart(0);
        HIMarker marker = new HIMarker();
        marker.setEnabled(true);
        marker.setSymbol("circle");
        marker.setFillColor(HIColor.initWithRGB(255, 255, 255));
        marker.setRadius(3);
        HIStates hiStates = new HIStates();
        HIHover hiHover = new HIHover();
        hiStates.setHover(hiHover);
        hiArea.setMarker(marker);
        hiHover.setEnabled(true);
        hiStates.setHover(hiHover);
        plotOptions.setArea(hiArea);
        options.setPlotOptions(plotOptions);

        HIArea series1 = new HIArea();
        series1.setName("Weight");
        series1.setColor(HIColor.initWithRGB(255, 255, 255));
        series1.setLineColor(HIColor.initWithRGB(255, 255, 255));
        series1.setFillColor(HIColor.initWithRGBA(255, 255, 255, 0.4));
        //   series1.setNegativeFillColor(HIColor.initWithHexValue("#07FFFCFC"));
        //    series1.setNegativeFillColor(HIColor.initWithHexValue("#ffffff"));
        //    series1.setFillColor(HIColor.initWithHexValue("#ffffff"));

//        series1.setFillOpacity(0.2);
        // series1.setPointStart(0);
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData()
                .getChartList().getWeightProgress().getData() != null ||
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData()
                        .getChartList().getWeightProgress().getData().size() > 0) {

            Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                    .getData().getChartList().getWeightProgress().getData().toArray(new Number[0]);
            Number[] numbers = new Number[series1_data.length];
            for (int i = 0; i < series1_data.length; i++) {
                numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
            }
            //Number[] series1_data = new Number[]{null, null, null, null, null, 6, 11, 32, 110, 235, 369, 640, 1005, 1436, 2063, 3057, 4618, 6444, 9822, 15468, 20434, 24126, 27387, 29459, 31056, 31982, 32040, 31233, 29224, 27342, 26662, 26956, 27912, 28999, 28965, 27826, 25579, 25722, 24826, 24605, 24304, 23464, 23708, 24099, 24357, 24237, 24401, 24344, 23586, 22380, 21004, 17287, 14747, 13076, 12555, 12144, 11009, 10950, 10871, 0};
            series1.setData(new ArrayList<>(Arrays.asList(numbers)));
        /*HIArea series2 = new HIArea();
        series2.setName("USSR/Russia");
        Number[] series2_data = new Number[]{null, null, null, null, null, null, null, null, null, null, 5, 25, 50, 120, 150, 200, 426, 660, 869, 1060, 1605, 2471, 3322, 4238, 5221, 6129, 7089, 8339, 9399, 10538, 11643, 13092, 14478, 15915, 17385, 19055, 21205, 23044, 25393, 27935, 30062, 32049, 33952, 35804, 37431, 39197, 45000, 43000, 41000, 39000, 37000, 35000, 33000, 31000, 29000, 27000, 25000, 24000, 23000, 22000, 21000, 20000, 19000, 18000, 18000, 17000, 16000};
        series2.setData(new ArrayList<>(Arrays.asList(series2_data)));*/
//        series1.setData(new ArrayList<>(Arrays.asList(series1_data)));
            options.setSeries(new ArrayList<>(Arrays.asList(series1/*, series2*/)));
        }

        chartView.setOptions(options);
        chartView.reload();

    }

    private void showGoalsAndAcheivementsChart() {


        HIChart chart = new HIChart();
        chart.setType("line");
        //       chart.setBackgroundColor(HIColor.initWithHexValue("#000000"));

        HIGradient gradient = new HIGradient(1, 1, 1, 1);

        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithHexValue("#ff3300")));
        stops.add(new HIStop(1, HIColor.initWithHexValue("#3399ff")));
        //    stops.add(new HIStop(1, HIColor.initWithRGB(60, 60, 60)));

        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        optionsAchiGoals.setChart(chart);
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Goals</p>");
        optionsAchiGoals.setTitle(title);

//        HISubtitle subtitle = new HISubtitle();
//        subtitle.setText("Source: thesolarfoundation.com");
//        options.setSubtitle(subtitle);

        HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(false);
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson() != null ||
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson().size() > 0) {
            xAxis.setCategories((ArrayList<String>)
                    LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson());
        }
        optionsAchiGoals.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        HIYAxis yaxis = new HIYAxis();
        HITitle title1 = new HITitle();
        HILabels labels = new HILabels();
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext() != null ||
                !LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext().
                        equalsIgnoreCase("null") ||
                !LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext().isEmpty()) {

            String s = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext();
//            title1.setText("<p style='color: #ffffff; '>" + s + "p>");
            title1.setText("<p style='color: #ffffff; '>Values</p>");
        } else {
            title1.setText("<p style='color: #ffffff; '>Sub Goals Progress</p>");
        }
        labels.setStyle(hicssObject);
        yaxis.setLabels(labels);
        yaxis.setTitle(title1);
        optionsAchiGoals.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

        HILegend legend = new HILegend();
        legend.setLayout("vertical");
        legend.setAlign("right");
        legend.setVerticalAlign("middle");
        legend.setItemStyle(hicssObject);
        optionsAchiGoals.setLegend(legend);

        HIPlotOptions plotoptions = new HIPlotOptions();
        HISeries hiSeries = new HISeries();
        hiSeries.setPointStart(2010);
        HILabel hiLabel = new HILabel();
        hiSeries.setLabel(hiLabel);
        hiLabel.setConnectorAllowed(false);
        plotoptions.setSeries(hiSeries);

        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        optionsAchiGoals.setExporting(exporting);

        HIResponsive responsive = new HIResponsive();

        HIRules rules1 = new HIRules();
        HICondition hiCondition = new HICondition();
        hiCondition.setMaxWidth(500);
        rules1.setCondition(hiCondition);
        HashMap<String, HashMap> chartLegend = new HashMap<>();
        HashMap<String, String> legendOptions = new HashMap<>();
        legendOptions.put("layout", "horizontal");
        legendOptions.put("align", "center");
        legendOptions.put("verticalAlign", "bottom");
        chartLegend.put("legend", legendOptions);
        rules1.setChartOptions(chartLegend);
        responsive.setRules(new ArrayList<>(Collections.singletonList(rules1)));
        optionsAchiGoals.setResponsive(responsive);
//ms1 to ms2 Line1
        HILine line1 = new HILine();
        line1.setName("Current Sub Goals");
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMinigoalsjson() != null ||
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMinigoalsjson().size() > 0) {
            Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson().toArray(new Number[0]);
            Number[] numbers = new Number[series1_data.length];
            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMs1(); i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMs2(); i++) {
                numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
            }
            //Number[] series1_data = new Number[]{null, null, null, null, null, 6, 11, 32, 110, 235, 369, 640, 1005, 1436, 2063, 3057, 4618, 6444, 9822, 15468, 20434, 24126, 27387, 29459, 31056, 31982, 32040, 31233, 29224, 27342, 26662, 26956, 27912, 28999, 28965, 27826, 25579, 25722, 24826, 24605, 24304, 23464, 23708, 24099, 24357, 24237, 24401, 24344, 23586, 22380, 21004, 17287, 14747, 13076, 12555, 12144, 11009, 10950, 10871, 0};
            line1.setColor(HIColor.initWithRGB(143, 236, 126));
            line1.setData(new ArrayList<>(Arrays.asList(numbers)));

            Number[] series2_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson().toArray(new Number[0]);
            Number[] numbers2 = new Number[series1_data.length];
            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe1(); i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2(); i++) {
                numbers2[i] = Double.parseDouble(String.valueOf(series2_data[i]));
            }

            HILine line2 = new HILine();
            line2.setName("Achived Sub Goals");
            line2.setColor(HIColor.initWithRGB(233, 150, 43));
            line2.setData(new ArrayList<>(Arrays.asList(numbers2)));

        /*HILine line2 = new HILine();
        line2.setName("Manufacturing");
        line2.setData(new ArrayList<>(Arrays.asList(24916, 24064, 29742, 29851, 32490, 30282, 38121, 404340)));

        HILine line3 = new HILine();
        line3.setName("Sales & Distribution");
        line3.setData(new ArrayList<>(Arrays.asList(11744, 17722, 16005, 19771, 20185, 24377, 32147, 39387)));

        HILine line4 = new HILine();
        line4.setName("Project Development");
        line4.setData(new ArrayList<>(Arrays.asList(null, null, 7988, 12169, 15112, 22452, 34400, 34227)));

        HILine line5 = new HILine();
        line5.setName("Other");
        line5.setData(new ArrayList<>(Arrays.asList(12908, 5948, 8105, 11248, 8989, 11816, 18274, 18111)));*/

            optionsAchiGoals.setSeries(new ArrayList<>(Arrays.asList(line1, line2/*, line2, line3, line4, line5*/)));
        }

        chartViewAchiGoals.setOptions(optionsAchiGoals);
        chartViewAchiGoals.reload();

        /*HIChart chart = new HIChart();
        chart.setType("column");
        options.setChart(chart);

        HITitle title = new HITitle();
        title.setText("Demo chart");

        options.setTitle(title);

        HIColumn series = new HIColumn();
        series.setData(new ArrayList<>(Arrays.asList(49.9, 71.5, 106.4, 129.2, 144, 176, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4)));
        options.setSeries(new ArrayList<HISeries>(Collections.singletonList(series)));

        chartView.setOptions(options);*/
    }


    private void setHeaderView() {
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewClick(int position) {
        if (!ConnectionDetector.isConnectingToInternet(DashBoardActivity.this)) {
            MethodUtils.errorMsg(DashBoardActivity.this, DashBoardActivity.this.getString(R.string.no_internet));
        } else {
            callDashBoardApi(contactLists.get(position).getServerUserId().toString());
        }
    }
}
