package com.surefiz.screens.dashboard;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.HIGradient;
import com.highsoft.highcharts.common.HIStop;
import com.highsoft.highcharts.common.hichartsclasses.HIArea;
import com.highsoft.highcharts.common.hichartsclasses.HIBackground;
import com.highsoft.highcharts.common.hichartsclasses.HICSSObject;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HIColumn;
import com.highsoft.highcharts.common.hichartsclasses.HICondition;
import com.highsoft.highcharts.common.hichartsclasses.HIDataLabels;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HIGauge;
import com.highsoft.highcharts.common.hichartsclasses.HIHover;
import com.highsoft.highcharts.common.hichartsclasses.HILabel;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPane;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotBands;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
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
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.dashboard.adapter.ContactListAdapter;
import com.surefiz.screens.dashboard.model.DashboardModel;
import com.surefiz.screens.dashboard.model.Guagechart;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.screens.users.model.UserListItem;
import com.surefiz.screens.users.model.UserListModel;
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
    public String id = "";
    public int row_user = -1;
    PieChart chartViewLoss, pieChatBodyComposition;
    HIChartView chartView, chartViewBmi, chartViewGoals, chartViewSubGoals,
            chartViewAchiGoals, chartGauge;
    TextView tv_name, tv_mac, tv_weight_dynamic, tv_height_dynamic, tv_recorded, tv_fat_dynamic, tv_bone_dynamic, tv_muscle_dynamic, tv_bmi_dynamic, tv_water_dynamic, tv_protein_dynamic;
    Button btn_fat, btn_bone, btn_muscle, btn_bmi, btn_water, btn_protein;
    CardView cv_weight, cv_weight_loss, cv_bmi, cv_user_body_composition, cv_goals, cv_sub_goals, cv_achi_goals, cv_gauge;
    RecyclerView rv_items;
    HIOptions options, optionsLoss, optionsBMI, optionsGoals, optionsSubGoals, optionsAchiGoals;
    List<UserListItem> contactLists = new ArrayList<>();
    ContactListAdapter adapter;
    private LoadingData loader;
    private LinearLayoutManager mLayoutManager;
    private TextView tv_battery_low_status;
    private RelativeLayout rl_battery_image;
    private ImageView img_battery_status;
    private CardView cv_body_composition;

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

            if (!getIntent().getBooleanExtra("isFromMenu", false)) {
                callUpdateUserDeviceInfoApi();
            }

            //getAppVersion();
        }

        setRecyclerViewItem();
        //callContactsApi();
    }

    private String getAppVersion() {
        String osVersion = android.os.Build.VERSION.RELEASE;

        String appVersion = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("versionName: " + osVersion + " " + appVersion);

        return appVersion;
    }


    private String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    private void callUpdateUserDeviceInfoApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> userListModelCall = apiInterface.call_updateUserDeviceInfoApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(),
                "2", LoginShared.getDeviceToken(DashBoardActivity.this), getOSVersion(), getAppVersion());

        userListModelCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {

                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.d("@@updateUserDevice : ", jsonObject.toString());


                } catch (Exception e) {
                    MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                MethodUtils.errorMsg(DashBoardActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
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
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
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

                    Log.d("@@UserListInfo : ", response.body().getData().getUserList().toString());


                    if (response.body().getStatus() == 1) {
                        contactLists.clear();
                        //      Collections.reverse(response.body().getData().getUserList());
                        //contactLists.addAll(response.body().getData().getUserList());
                        contactLists.addAll(checkMainUserVisibility(response.body().getData().getUserList()));
                        //contactLists.addAll(checkSubUserAccount(contactLists));
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


    // This method is to check wheather any sub-user wants to be displayed on Main users family
    private List<UserListItem> checkMainUserVisibility(List<UserListItem> userList) {

        List<UserListItem> tempUserList = new ArrayList<>();

        if (LoginShared.getRegistrationDataModel(DashBoardActivity.this).getData().getUser().get(0).getScaleUserId().equals("1")) {

            tempUserList.add(userList.get(0));

            for (int i = 1; i < userList.size(); i++) {
                if (userList.get(i).getMainuservisibility() == 1) {
                    tempUserList.add(userList.get(i));
                }

                System.out.println("userDataUpdated: " + userList.get(i).getUserName() + " " + userList.get(i).getScaleUserId() + " " + userList.get(i).getMainuservisibility());
            }
        } else {


            for (int i = 0; i < userList.size(); i++) {
                if (String.valueOf(userList.get(i).getScaleUserId()).equalsIgnoreCase(LoginShared.getRegistrationDataModel(DashBoardActivity.this).getData().getUser().get(0).getScaleUserId())) {
                    tempUserList.add(userList.get(i));
                }

                System.out.println("userDataUpdated: " + userList.get(i).getUserName() + " " + userList.get(i).getScaleUserId() + " " + userList.get(i).getMainuservisibility());
            }
            //tempUserList.addAll(userList);
        }


        // This is to check if it is a Sub Users Account. Then Main User will be removed from the user list.
        /*if (!LoginShared.getRegistrationDataModel(DashBoardActivity.this).getData().getUser().get(0).getScaleUserId().equals("1")) {
            if (tempUserList.get(0).getScaleUserId() == 1) {
                tempUserList.remove(0);
            }
        }*/

        return tempUserList;
    }


    // This method is to check wheather any sub-user wants to be displayed on Main users family
    private List<UserListItem> checkSubUserAccount(List<UserListItem> userList) {

        List<UserListItem> tempUserList = new ArrayList<>();

        if (!LoginShared.getRegistrationDataModel(DashBoardActivity.this).getData().getUser().get(0).getScaleUserId().equals("1")) {
            tempUserList.addAll(userList);
            tempUserList.remove(0);
        } else {
            tempUserList.addAll(userList);
        }

        return tempUserList;
    }


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

                    Log.d("@@DashboardData : ", jsonObject.toString());

                    if (jsonObject.optInt("status") == 1) {
                        dashboardModel = gson.fromJson(responseString, DashboardModel.class);
                        LoginShared.setDashBoardDataModel(DashBoardActivity.this, dashboardModel);
//                        checkForShowView();
                        runOnUiThread(new Thread(new Runnable() {
                            @Override
                            public void run() {


                                /*if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().size() > 0) {
                                    for (int i = 0; i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().size(); i++) {
                                        showCharts(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().get(i));
                                    }
                                } else {

                                    setWeightChart();
                                    setWeightLossChart();
                                    setPieChatBodyComposition();
                                    setBMIChart();
                                    cv_sub_goals.setVisibility(View.VISIBLE);
                                    setSubGoalsChart();
                                    showGoalsChart();
                                }*/


                                if (getIntent().hasExtra("Performance")) {
                                    if (getIntent().getStringExtra("Performance").equalsIgnoreCase("1") && LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().size() > 0) {

                                        setWeightChart();
                                        setWeightLossChart();
                                        setPieChatBodyComposition();
                                        setBMIChart();
                                        cv_sub_goals.setVisibility(View.VISIBLE);
                                        setSubGoalsChart();
                                        showGoalsChart();
                                        setOtherOptions();

                                        checkForShowView();
                                    } else {
                                        MethodUtils.errorMsg(DashBoardActivity.this, "Sorry, Charts are private. We can change the message later");
                                    }
                                } else {
                                    setWeightChart();
                                    setWeightLossChart();
                                    setPieChatBodyComposition();
                                    setBMIChart();
                                    cv_sub_goals.setVisibility(View.VISIBLE);
                                    setSubGoalsChart();
                                    showGoalsChart();
                                    setOtherOptions();
                                }

                            }
                        }));

                        if (dashboardModel.getData().getProgress() == 0) {
                            // Create new Meter
                            runOnUiThread(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cv_gauge.setVisibility(View.VISIBLE);
                                    //cv_sub_goals.setVisibility(View.GONE);
                                    cv_achi_goals.setVisibility(View.GONE);
                                    cv_body_composition.setVisibility(View.GONE);
                                    implementHighChart(dashboardModel.getData().getChartList().getGuagechart());
                                }
                            }));

                        } else {
                            runOnUiThread(new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    cv_gauge.setVisibility(View.GONE);
                                    //cv_sub_goals.setVisibility(View.VISIBLE);
                                    cv_achi_goals.setVisibility(View.VISIBLE);
                                    cv_body_composition.setVisibility(View.VISIBLE);
                                    //setSubGoalsChart();
                                    showGoalsAndAcheivementsChart();

                                }
                            }));
                        }
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

    private void showCharts(String chartName) {
        switch (chartName) {
            case "weightProgress":
                setWeightChart();
                break;
            case "weightLoss":
                setWeightLossChart();
                break;
            case "composition":
                setPieChatBodyComposition();
                break;
            case "BMI":
                setBMIChart();
                break;
            case "nextSubGoal":
                break;
            case "subGoalsProgress":
                setSubGoalsChart();
                break;
            case "goals":
                showGoalsChart();
                break;

        }
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


        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("composition")) {
            cv_body_composition.setVisibility(View.VISIBLE);
        } else {
            cv_body_composition.setVisibility(View.GONE);
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

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("guagechart")) {
            cv_gauge.setVisibility(View.VISIBLE);
        } else {
            cv_gauge.setVisibility(View.GONE);
        }

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getVisibleCharts().contains("bodycomposition")) {
            cv_user_body_composition.setVisibility(View.VISIBLE);
        } else {
            cv_user_body_composition.setVisibility(View.GONE);
        }
    }

    private void setOtherOptions() {


        String nameText = "<font color=#788394> Name: " + "</font>" + "<font color=#B5B7BF>" + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getUserName() + "</font>";
        String scaleText = "<font color=#788394> Scale ID: " + "</font>" + "<font color=#B5B7BF>" + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getScaleMacAddress() + "</font>";

        tv_name.setText(Html.fromHtml(nameText));
        tv_mac.setText(Html.fromHtml(scaleText));
        //tv_name.setText("Name: " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getUserName());
        //tv_mac.setText("Scale ID: " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getScaleMacAddress());
        tv_weight_dynamic.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWeight());
        tv_height_dynamic.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getHeight());

        try {
            if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBodyFat().getValue() > 0.0) {
                tv_fat_dynamic.setText(String.valueOf(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBodyFat().getValue()));
                tv_fat_dynamic.setVisibility(View.VISIBLE);
            } else {
                tv_fat_dynamic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_fat_dynamic.setVisibility(View.GONE);
        }
        btn_fat.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBodyFat().getStatus());
        btn_fat.setBackgroundColor(Color.parseColor(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBodyFat().getColourCode()));

        try {
            if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBoneKg().getValue() > 0.0) {
                tv_bone_dynamic.setText(String.valueOf(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBoneKg().getValue()));
                tv_bone_dynamic.setVisibility(View.VISIBLE);
            } else {
                tv_bone_dynamic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_bone_dynamic.setVisibility(View.GONE);
        }
        btn_bone.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBoneKg().getStatus());
        btn_bone.setBackgroundColor(Color.parseColor(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBoneKg().getColourCode()));

        try {
            if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getMuscle().getValue() > 0.0) {
                tv_muscle_dynamic.setText(String.valueOf(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getMuscle().getValue()));
                tv_muscle_dynamic.setVisibility(View.VISIBLE);
            } else {
                tv_muscle_dynamic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_muscle_dynamic.setVisibility(View.GONE);
        }
        btn_muscle.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getMuscle().getStatus());

        try {
            btn_muscle.setBackgroundColor(Color.parseColor(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getMuscle().getColourCode()));
        } catch (Exception e) {
            e.printStackTrace();
            btn_muscle.setBackgroundColor(Color.parseColor("#05B085"));
        }

        try {
            if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getbMI().getValue() > 0.0) {
                tv_bmi_dynamic.setText(String.valueOf(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getbMI().getValue()));
                tv_bmi_dynamic.setVisibility(View.VISIBLE);
            } else {
                tv_bmi_dynamic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_bmi_dynamic.setVisibility(View.GONE);
        }
        btn_bmi.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getbMI().getStatus());
        btn_bmi.setBackgroundColor(Color.parseColor(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getbMI().getColourCode()));

        try {
            if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWater().getValue() > 0.0) {
                tv_water_dynamic.setText(String.valueOf(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWater().getValue()));
                tv_water_dynamic.setVisibility(View.VISIBLE);
            } else {
                tv_water_dynamic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_water_dynamic.setVisibility(View.GONE);
        }
        btn_water.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWater().getStatus());
        btn_water.setBackgroundColor(Color.parseColor(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getWater().getColourCode()));

        try {
            if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getProtein().getValue() > 0.0) {
                tv_protein_dynamic.setText(String.valueOf(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getProtein().getValue()));
                tv_protein_dynamic.setVisibility(View.VISIBLE);
            } else {
                tv_protein_dynamic.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tv_protein_dynamic.setVisibility(View.GONE);
        }
        btn_protein.setText(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getProtein().getStatus());
        btn_protein.setBackgroundColor(Color.parseColor(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getProtein().getColourCode()));

        tv_recorded.setText("Recorded on " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getRecordedOn());

        System.out.println("BatteryStatus: " + LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBattery());
        showBatteryStatus(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getCurrentCompositions().getBattery());
    }

    private void showBatteryStatus(int batteryStatus) {

        int imageDrawable = 0;

        switch (batteryStatus) {
            case 0:
                imageDrawable = R.drawable.battery0;
                MethodUtils.showInfoDialog(DashBoardActivity.this, getString(R.string.battery_status_low));
                break;
            case 1:
                //tv_battery_low_status.setVisibility(View.VISIBLE);
                //rl_battery_image.setVisibility(View.GONE);
                imageDrawable = R.drawable.battery1;
                MethodUtils.showInfoDialog(DashBoardActivity.this, getString(R.string.battery_status_low));
                break;
            case 2:
                imageDrawable = R.drawable.battery2;
                break;

            case 3:
                imageDrawable = R.drawable.battery3;
                break;

            case 4:
                imageDrawable = R.drawable.battery4;
                break;
        }

        if (imageDrawable != 0) {
            showBatteryImage(imageDrawable);
        }
    }

    private void showBatteryImage(int imageDrawable) {
        if (imageDrawable != 0) {
            tv_battery_low_status.setVisibility(View.GONE);
            rl_battery_image.setVisibility(View.VISIBLE);
            img_battery_status.setImageResource(imageDrawable);
        }
    }

    private void viewBind() {
        chartView = findViewById(R.id.hc_weight);
        chartView.setOptions(options);
        chartViewLoss = findViewById(R.id.hc_weight_loss);
        pieChatBodyComposition = findViewById(R.id.pieChatBodyComposition);
//        chartViewLoss.setOptions(optionsLoss);
        chartViewBmi = findViewById(R.id.hc_bmi);
        chartViewBmi.setOptions(optionsBMI);
        chartViewGoals = findViewById(R.id.hc_goals);
        chartViewGoals.setOptions(optionsGoals);
        chartViewSubGoals = findViewById(R.id.hc_sub_goals);
        chartViewSubGoals.setOptions(optionsSubGoals);
        chartViewAchiGoals = findViewById(R.id.hc_achi_goals);
        chartViewAchiGoals.setOptions(optionsAchiGoals);
        chartGauge = findViewById(R.id.hc_gauge);
        chartGauge.setOptions(optionsAchiGoals);
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

        tv_fat_dynamic = findViewById(R.id.tv_fat_dynamic);
        tv_bone_dynamic = findViewById(R.id.tv_bone_dynamic);
        tv_muscle_dynamic = findViewById(R.id.tv_muscle_dynamic);
        tv_bmi_dynamic = findViewById(R.id.tv_bmi_dynamic);
        tv_water_dynamic = findViewById(R.id.tv_water_dynamic);
        tv_protein_dynamic = findViewById(R.id.tv_protein_dynamic);

        cv_weight = findViewById(R.id.cv_weight);
        //cv_weight.setVisibility(View.GONE);


        cv_body_composition = findViewById(R.id.cv_body_composition);
        //cv_body_composition.setVisibility(View.GONE);

        cv_weight_loss = findViewById(R.id.cv_weight_loss);
        //cv_weight_loss.setVisibility(View.GONE);

        cv_bmi = findViewById(R.id.cv_bmi);
        //cv_bmi.setVisibility(View.GONE);

        cv_goals = findViewById(R.id.cv_goals);
        cv_user_body_composition = findViewById(R.id.cv_user_body_composition);
        //cv_goals.setVisibility(View.GONE);

        cv_sub_goals = findViewById(R.id.cv_sub_goals);
        //cv_sub_goals.setVisibility(View.GONE);

        cv_achi_goals = findViewById(R.id.cv_achi_goals);
        cv_gauge = findViewById(R.id.cv_gauge);
        rv_items = findViewById(R.id.rv_items);

        tv_battery_low_status = findViewById(R.id.tv_battery_low_status);
        rl_battery_image = findViewById(R.id.rl_battery_image);
        img_battery_status = findViewById(R.id.img_battery_status);



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

        HIGradient gradient = new HIGradient(0, 0, 0, 1);

        LinkedList<HIStop> stops = new LinkedList<>();
        stops.add(new HIStop(0, HIColor.initWithRGB(65, 71, 85)));
        stops.add(new HIStop(1, HIColor.initWithRGB(65, 71, 85)));

        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        chart.setType("column");
        HILegend hiLegend = new HILegend();
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");  //Removed
        hiLegend.setItemStyle(hicssObject);
        optionsSubGoals.setLegend(hiLegend);


        chart.setRenderTo("container");
        /*chart.setOptions3d(new HIOptions3d());
        chart.getOptions3d().setEnabled(true);
        chart.getOptions3d().setAlpha(15);
        chart.getOptions3d().setBeta(15);
        chart.getOptions3d().setDepth(50);
        chart.getOptions3d().setViewDistance(25);*/

        /*HIOptions3d hiOptions3d = new HIOptions3d();
        hiOptions3d.setEnabled(true);
        hiOptions3d.setAlpha(0);
        hiOptions3d.setBeta(10);
        hiOptions3d.setViewDistance(25);
        hiOptions3d.setDepth(100);
        chart.setOptions3d(hiOptions3d);*/

        optionsSubGoals.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Your Sub Goals Progress</p>");
        optionsSubGoals.setTitle(title);

        HIXAxis xAxis = new HIXAxis();
        HITitle hiXTitle = new HITitle();
        hiXTitle.setText("<p style='color: #ffffff; '>Weeks</p>");
        xAxis.setTitle(hiXTitle);
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
        hiTitle.setText("<p style='color: #ffffff; '>Weight</p>");
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
        hiColumn.setDepth(25);
        plotOptions.setColumn(hiColumn);
        plotOptions.getColumn().setBorderWidth(0);
        //hiColumn.setStacking("normal");
        //hiColumn.setDepth(40);
        optionsSubGoals.setPlotOptions(plotOptions);

        HIColumn series1 = new HIColumn();
        series1.setColorByPoint(true);
        series1.setName("Expected Weight To Go");
//        series1.setColor(HIColor.initWithRGB(73,183,130));
//        series1.setColor(HIColor.initWithHexValue("#49b782"));
        Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getSubGoalsProgress().getExpectedWeightToGo().toArray(new Number[0]);


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
        optionsGoals.setLegend(hiLegend);
        chart.setType("column");
        optionsGoals.setChart(chart);

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Next Sub Goals</p>");
        optionsGoals.setTitle(title);

        HIXAxis xAxis = new HIXAxis();
        HITitle hiXTitle = new HITitle();
        hiXTitle.setText("<p style='color: #ffffff; '>Weeks</p>");
        xAxis.setTitle(hiXTitle);
        xAxis.setAllowDecimals(false);

        xAxis.setCategories((ArrayList<String>)
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getNextSubGoal().getLabel());


        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");

        HILabels hiXLabels = new HILabels();
        hiXLabels.setStyle(hicssObject);
        xAxis.setLabels(hiXLabels);

        optionsGoals.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});
        xAxis.setVisible(true);

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        HILabels hiLabels = new HILabels();
        hiTitle.setText("<p style='color: #ffffff; '>Weight</p>");
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

        HIColumn series1 = new HIColumn();
        series1.setName("Brands");

        Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getNextSubGoal().
                getData().toArray(new Number[0]);

        Number[] numbers = new Number[series1_data.length];
        ArrayList<HIColor> hiColors = new ArrayList<>();
        ArrayList<String> colors = new ArrayList<>();
        for (int i = 0; i < series1_data.length; i++) {
            numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
            colors.add("#77D48B");
        }

        series1.setData(new ArrayList<>(Arrays.asList(numbers)));
        optionsGoals.setColors(colors);
        optionsGoals.setSeries(new ArrayList<>(Arrays.asList(series1)));

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
        title.setText("<p style='color: #ffffff; text-align: center;'>BMI Progress</p>");
        optionsBMI.setTitle(title);

        /*HISubtitle subtitle = new HISubtitle();
        subtitle.setText("<p style='color: #ffffff; text-align: center;'>Source: WorldClimate.com</p>");
        options.setSubtitle(subtitle);*/

        HIXAxis xAxis = new HIXAxis();
        HILabels labels = new HILabels();
        HITitle hiTitleX = new HITitle();
        hiTitleX.setText("<p style='color: #ffffff; '></p>");
        xAxis.setLineWidth(1);
        xAxis.setAllowDecimals(false);

        HILabels hiXLabels = new HILabels();
        hiXLabels.setStyle(hicssObject);
        xAxis.setLabels(hiXLabels);


        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().
                getLabel() != null ||
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                        getBMI().getLabel().size() > 0) {
            xAxis.setCategories((ArrayList<String>)
                    LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().
                            getBMI().getLabel());
        }

        optionsBMI.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});
        xAxis.setTitle(hiTitleX);
        xAxis.setVisible(true);

        HIYAxis yAxis = new HIYAxis();
        HITitle hiTitle = new HITitle();
        HILabels hiLabels = new HILabels();
        hiTitle.setText("<p style='color: #ffffff; '>BMI</p>");
        //hiTitle.setText("<p style='color: #ffffff; '>Values</p>");
        hiLabels.setStyle(hicssObject);
        yAxis.setLineWidth(1);
        yAxis.setTitle(hiTitle);
        yAxis.setLabels(hiLabels);

        optionsBMI.setYAxis(new ArrayList<HIYAxis>() {{
            add(yAxis);
        }});

        yAxis.setVisible(true);

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
        series1.setName("BMI");
        series1.setColor(HIColor.initWithRGB(255, 255, 255));

        Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getBMI().getData().toArray(new Number[0]);
        Number[] numbers = new Number[series1_data.length];
        for (int i = 0; i < series1_data.length; i++) {
            numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
        }
        series1.setData(new ArrayList<>(Arrays.asList(numbers)));

        optionsBMI.setSeries(new ArrayList<>(Arrays.asList(series1/*, series2*/)));


        chartViewBmi.setOptions(optionsBMI);
        chartViewBmi.reload();
    }


    private void setWeightLossChart() {

        chartViewLoss.setUsePercentValues(true);
        chartViewLoss.getDescription().setEnabled(false);
        chartViewLoss.setExtraOffsets(5, 10, 5, 5);

        chartViewLoss.setDragDecelerationFrictionCoef(0.95f);

//        chartViewLoss.setCenterText(generateCenterSpannableText());

        chartViewLoss.setDrawHoleEnabled(true);
        chartViewLoss.setHoleColor(Color.TRANSPARENT);

        chartViewLoss.setTransparentCircleColor(Color.WHITE);
        chartViewLoss.setTransparentCircleAlpha(110);

        chartViewLoss.setHoleRadius(58f);
        chartViewLoss.setTransparentCircleRadius(61f);

        chartViewLoss.setDrawCenterText(true);

        chartViewLoss.setRotationAngle(250);
        // enable rotation of the chartViewLoss by touch
        chartViewLoss.setRotationEnabled(false);
        chartViewLoss.setHighlightPerTapEnabled(false);


        chartViewLoss.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chartViewLoss.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(-20f);
        l.setYEntrySpace(-10f);
        l.setYOffset(0f);
        l.setCustom(new ArrayList<>());

        // entry label styling
        chartViewLoss.setEntryLabelColor(Color.TRANSPARENT);
        chartViewLoss.setEntryLabelTextSize(12f);

        // calculations
        ArrayList<PieEntry> entries = new ArrayList<>();
        double achieved = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getWeightLoss().getAchieved();
        double toGo = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getWeightLoss().getToGo();
        double percentage = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getWeightLoss().getPercentage();

        double total = achieved + toGo;
        float toGoChart = (float) ((total / 100) * toGo);
        float achievedChart = (float) ((total / 100) * achieved);
        PieEntry pieEntry = new PieEntry(toGoChart, "To Go: " + toGo);
        entries.add(pieEntry);
        pieEntry = new PieEntry(achievedChart, "Achieved: " + achieved);
        entries.add(pieEntry);

        //chartViewLoss.setCenterText(percentage + "%");
        chartViewLoss.setCenterText(Math.round(percentage) + "%");
        chartViewLoss.setCenterTextColor(getResources().getColor(R.color.textBlue));
        chartViewLoss.setCenterTextSize(35f);

        PieDataSet dataSet = new PieDataSet(entries, "Weight Loss Achievement");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(250, 66, 82));
        colors.add(Color.rgb(40, 181, 233));

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chartViewLoss));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        chartViewLoss.setData(data);

        // undo all highlights
        chartViewLoss.highlightValues(null);

        chartViewLoss.invalidate();



/*

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
        //tooltip.setPointFormat("{series.name}: <b>{point.percentage:.1f}%</b>");
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
        *//*map2.put("sliced", true);
        map2.put("selected", true);*//*


         *//*HashMap<String, Object> map3 = new HashMap<>();
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
        map6.put("y", 0.2);*//*

        pie.setData(new ArrayList<>(Arrays.asList(map1, map2*//*, map3, map4, map5, map6*//*)));

        optionsLoss.setSeries(new ArrayList<>(Collections.singletonList(pie)));

        chartViewLoss.setOptions(optionsLoss);
        chartViewLoss.reload();*/
    }


    private void setPieChatBodyComposition() {
        pieChatBodyComposition.setUsePercentValues(true);
        pieChatBodyComposition.getDescription().setEnabled(false);
        pieChatBodyComposition.setExtraOffsets(5, 10, 5, 5);
        pieChatBodyComposition.setDragDecelerationFrictionCoef(0.95f);
        pieChatBodyComposition.setDrawHoleEnabled(true);
        pieChatBodyComposition.setHoleColor(Color.TRANSPARENT);
        pieChatBodyComposition.setTransparentCircleColor(Color.WHITE);
        pieChatBodyComposition.setTransparentCircleAlpha(110);
        pieChatBodyComposition.setHoleRadius(58f);
        pieChatBodyComposition.setTransparentCircleRadius(61f);
        pieChatBodyComposition.setDrawCenterText(true);
        pieChatBodyComposition.setRotationAngle(270);
        pieChatBodyComposition.setRotationEnabled(false);
        pieChatBodyComposition.setHighlightPerTapEnabled(false);


        pieChatBodyComposition.animateY(1400, Easing.EaseInOutQuad);

        Legend l = pieChatBodyComposition.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(-20f);
        l.setYEntrySpace(-10f);
        l.setYOffset(0f);
        l.setCustom(new ArrayList<>());
        //l.setCustom(new ArrayList<>());


        pieChatBodyComposition.setEntryLabelColor(Color.WHITE);
        pieChatBodyComposition.setEntryLabelTextSize(12f);

        // calculation of values to be displayed
        ArrayList<PieEntry> entries = new ArrayList<>();

        double muscelValue = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getComposition().getMuscle();
        PieEntry muscelEntry = new PieEntry((float) muscelValue);
        entries.add(muscelEntry);

        Double boneValue = 0.0;
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getComposition().getOverallData().size() > 2) {
            try {
                boneValue = Double.parseDouble(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getComposition().getOverallData().get(1));

                if (Double.isNaN(boneValue)) {
                    boneValue = 0.0;
                }

            } catch (Exception e) {
                e.printStackTrace();
                boneValue = 0.0;
            }
        }
        PieEntry boneEntry = new PieEntry(boneValue.floatValue());
        entries.add(boneEntry);

        double fatValue = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getComposition().getFat();
        PieEntry fatEntry = new PieEntry((float) fatValue);
        entries.add(fatEntry);


        String centerText = "<font color=#fd7d04><b> Muscle " + muscelValue + "%</b></font>" + "<br>" + "<font color=#fa4251><b> Fat " + fatValue + "%<b></font>"
                + "<br>" + "<font color=#24F91D><b> Bone " + boneValue + "%<b></font>";
        //String centerText = "<font color=#fd7d04><b> Muscle " + muscelValue + "%</b></font>" + "<br>" + "<font color=#fa4251><b> Fat " + fatValue + "%<b></font>";
        pieChatBodyComposition.setCenterText(Html.fromHtml(centerText));
        //pieChatBodyComposition.setCenterText("Muscle " + muscelValue + "%" + "\nFat " + fatValue + "%");
        pieChatBodyComposition.setCenterTextColor(Color.WHITE);
        pieChatBodyComposition.setCenterTextSize(12f);


        PieDataSet dataSet = new PieDataSet(entries, "");


        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(1f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);


        // Setting the color code for the slices
        //ArrayList<Integer> colors = setColorForChart(bmiPercentage);
        ArrayList<Integer> colors = new ArrayList<>();
        //colors.add(Color.rgb(151, 125, 6));
        colors.add(Color.rgb(253, 125, 4));
        colors.add(Color.rgb(41, 178, 7));
        colors.add(Color.rgb(250, 66, 82));


        dataSet.setColors(colors);


        /*ArrayList<LegendEntry> legendEntries = new ArrayList<>();
        LegendEntry legendEntry = new LegendEntry();
        legendEntry.label = "Muscle: " + muscelValue + "%";
        legendEntries.add(legendEntry);

        LegendEntry legendEntryFat = new LegendEntry();
        legendEntryFat.label = "Fat: " + fatValue + "%";
        legendEntries.add(legendEntryFat);

        l.setCustom(legendEntries);*/

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChatBodyComposition));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        pieChatBodyComposition.setData(data);

        // undo all highlights
        pieChatBodyComposition.highlightValues(null);
        pieChatBodyComposition.invalidate();
    }

    private void setWeightChart() {

        HIChart chart = new HIChart();
        chart.setType("area");
        //Required for gradient Background
        HIGradient gradient = new HIGradient(0, 0, 0, 1);
        LinkedList<HIStop> stops = new LinkedList<>();
        /*stops.add(new HIStop(0, HIColor.initWithRGB(255, 51, 0)));
        stops.add(new HIStop(1, HIColor.initWithRGB(51, 153, 255)));*/

        stops.add(new HIStop(0, HIColor.initWithRGB(250, 69, 107)));
        stops.add(new HIStop(1, HIColor.initWithRGB(64, 93, 249)));
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

            series1.setData(new ArrayList<>(Arrays.asList(numbers)));

            options.setSeries(new ArrayList<>(Arrays.asList(series1/*, series2*/)));
        }

        chartView.setOptions(options);
        chartView.reload();

    }

    private void showGoalsAndAcheivementsChart() {


        HIChart chart = new HIChart();
        chart.setType("line");

        chart.setBackgroundColor(HIColor.initWithHexValue("#554755"));

        HIGradient gradient = new HIGradient(0, 0, 0, 1);

        LinkedList<HIStop> stops = new LinkedList<>();

        stops.add(new HIStop(0, HIColor.initWithRGB(65, 71, 85)));
        stops.add(new HIStop(1, HIColor.initWithRGB(65, 71, 85)));

        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        optionsAchiGoals.setChart(chart);
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Your Goals</p>");
        optionsAchiGoals.setTitle(title);


        HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(true);
        HITitle xtitle1 = new HITitle();
        HILabels xlabels = new HILabels();
        xlabels.setStyle(hicssObject);
        xtitle1.setText("<p style='color: #ffffff; '>Weeks</p>");
        xAxis.setLabels(xlabels);
        xAxis.setTitle(xtitle1);

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson() != null
            /*LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson().size() > 0*/) {


            ArrayList<String> xAxisWeeks = new ArrayList<>();
            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMs1();
                 i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMs2(); i++) {

                if (i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson().size()) {
                    xAxisWeeks.add(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson().get(i));
                }
            }

            System.out.println("xAxisWeeks: " + xAxisWeeks.toString());
            xAxis.setCategories(xAxisWeeks);

            //xAxis.setCategories((ArrayList<String>) LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson());
        }
        optionsAchiGoals.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        //xAxis.setVisible(true);

        HIYAxis yaxis = new HIYAxis();
        HITitle title1 = new HITitle();
        HILabels labels = new HILabels();
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext() != null ||
                !LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext().
                        equalsIgnoreCase("null") ||
                !LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext().isEmpty()) {

            String s = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext();
//            title1.setText("<p style='color: #ffffff; '>" + s + "p>");
            title1.setText("<p style='color: #ffffff; '>Weight</p>");
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
        hiCondition.setMaxWidth(700);
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

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMinigoalsjson() != null &&
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMinigoalsjson().size() > 0) {

            /*Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson().toArray(new Number[0]);*/

            List<String> series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson();

            //Number[] numbers = new Number[series1_data.length];
            Number[] numbers = new Number[LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                    .getData().getChartList().getGoals().getMs2()];

            System.out.println("numbers: " + numbers.length);

            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                    .getData().getChartList().getGoals().getMs1();
                 i < LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                         .getData().getChartList().getGoals().getMs2(); i++) {
                //numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
                if (!series1_data.get(i).equals("")) {
                    numbers[i] = Double.parseDouble(series1_data.get(i));
                }
            }


            line1.setColor(HIColor.initWithRGB(143, 236, 126));
            line1.setData(new ArrayList<>(Arrays.asList(numbers)));

            /*Number[] series2_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson().toArray(new Number[0]);*/


            List<String> series2_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson();

            //Number[] numbers2 = new Number[series2_data.length];
            /*Number[] numbers2 = new Number[(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2() -
                    LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe1()) + 1];*/

            //System.out.println("numbers2: " + numbers2.length);

            ArrayList<Double> numberList2 = new ArrayList<>();

            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe1();
                 i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2(); i++) {

                if (series2_data.get(i) != null && !series2_data.get(i).equals("")) {
                    numberList2.add(Double.parseDouble(series2_data.get(i)));
                }
            }

            System.out.println("numbers2: " + numberList2.toString());

            HILine line2 = new HILine();
            line2.setName("Achived Sub Goals");
            line2.setColor(HIColor.initWithRGB(233, 150, 43));
            //line2.setData(new ArrayList<>(Arrays.asList(numbers2)));
            line2.setData(numberList2);
            optionsAchiGoals.setSeries(new ArrayList<>(Arrays.asList(line1, line2)));
        }

        chartViewAchiGoals.setOptions(optionsAchiGoals);
        chartViewAchiGoals.reload();
    }


    /*private void showGoalsAndAcheivementsChart() {


        HIChart chart = new HIChart();
        chart.setType("line");

        chart.setBackgroundColor(HIColor.initWithHexValue("#554755"));

        HIGradient gradient = new HIGradient(0, 0, 0, 1);

        LinkedList<HIStop> stops = new LinkedList<>();

        stops.add(new HIStop(0, HIColor.initWithRGB(65, 71, 85)));
        stops.add(new HIStop(1, HIColor.initWithRGB(65, 71, 85)));

        chart.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));

        optionsAchiGoals.setChart(chart);
        HICSSObject hicssObject = new HICSSObject();
        hicssObject.setColor("#ffffff");

        HITitle title = new HITitle();
        title.setUseHTML(true);
        title.setText("<p style='color: #ffffff; text-align: center;'>Your Goals</p>");
        optionsAchiGoals.setTitle(title);


        HIXAxis xAxis = new HIXAxis();
        xAxis.setAllowDecimals(true);
        HITitle xtitle1 = new HITitle();
        HILabels xlabels = new HILabels();
        xlabels.setStyle(hicssObject);
        xtitle1.setText("<p style='color: #ffffff; '>Weeks</p>");
        xAxis.setLabels(xlabels);
        xAxis.setTitle(xtitle1);

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson() != null
            *//*LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson().size() > 0*//*) {


            ArrayList<String> xAxisWeeks = new ArrayList<>();
            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMs1();
                 i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMs2(); i++) {
                xAxisWeeks.add(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson().get(i));
            }

            System.out.println("xAxisWeeks: " + xAxisWeeks.toString());
            xAxis.setCategories(xAxisWeeks);

            //xAxis.setCategories((ArrayList<String>) LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweeksjson());
        }
        optionsAchiGoals.setXAxis(new ArrayList<HIXAxis>() {{
            add(xAxis);
        }});

        //xAxis.setVisible(true);

        HIYAxis yaxis = new HIYAxis();
        HITitle title1 = new HITitle();
        HILabels labels = new HILabels();
        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext() != null ||
                !LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext().
                        equalsIgnoreCase("null") ||
                !LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext().isEmpty()) {

            String s = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMiniweighttext();
//            title1.setText("<p style='color: #ffffff; '>" + s + "p>");
            title1.setText("<p style='color: #ffffff; '>Weight</p>");
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
        hiCondition.setMaxWidth(700);
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

        if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMinigoalsjson() != null &&
                LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMinigoalsjson().size() > 0) {

            Number[] series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson().toArray(new Number[0]);

            *//*List<String> series1_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson();*//*

            //Number[] numbers = new Number[series1_data.length];
            Number[] numbers = new Number[LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                    .getData().getChartList().getGoals().getMs2()];

            System.out.println("numbers: " + numbers.length);

            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                    .getData().getChartList().getGoals().getMs1();
                 i < LoginShared.getDashBoardDataModel(DashBoardActivity.this)
                         .getData().getChartList().getGoals().getMs2(); i++) {
                numbers[i] = Double.parseDouble(String.valueOf(series1_data[i]));
                *//*if (!series1_data.get(i).equals("")) {
                    numbers[i] = Double.parseDouble(series1_data.get(i));
                }*//*
            }


            line1.setColor(HIColor.initWithRGB(143, 236, 126));
            line1.setData(new ArrayList<>(Arrays.asList(numbers)));

            Number[] series2_data = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().
                    getMinigoalsjson().toArray(new Number[0]);

            //Number[] numbers2 = new Number[series2_data.length];
            Number[] numbers2 = new Number[(LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2() -
                    LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe1()) + 1];

            System.out.println("numbers2: " + numbers2.length);

            for (int i = LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe1();
                 i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2(); i++) {
                //numbers2[i] = series2_data[i] == null ? 0 : Double.parseDouble(String.valueOf(series2_data[i]));

                int localIndex = i - LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe1();

                if (series2_data[i] == null) {
                    if (LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2() == 1) {
                        numbers2[localIndex] = 0;
                    } else {
                        if (i < LoginShared.getDashBoardDataModel(DashBoardActivity.this).getData().getChartList().getGoals().getMe2() - 1) {
                            numbers2[localIndex] = (Double.parseDouble(String.valueOf(series2_data[i - 1])) + Double.parseDouble(String.valueOf(series2_data[i + 1]))) / 2;
                        } else {
                            numbers2[localIndex] = Double.parseDouble(String.valueOf(series2_data[i - 1]));
                        }
                    }
                } else {
                    numbers2[localIndex] = Double.parseDouble(String.valueOf(series2_data[i]));
                }
            }

            HILine line2 = new HILine();
            line2.setName("Achived Sub Goals");
            line2.setColor(HIColor.initWithRGB(233, 150, 43));
            line2.setData(new ArrayList<>(Arrays.asList(numbers2)));
            optionsAchiGoals.setSeries(new ArrayList<>(Arrays.asList(line1, line2)));
        }

        chartViewAchiGoals.setOptions(optionsAchiGoals);
        chartViewAchiGoals.reload();
    }*/


    private void setHeaderView() {
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
        iv_friend_request.setVisibility(View.VISIBLE);
        iv_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, NotificationActivity.class);
                intent.putExtra("fromDashboard", true);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onViewClick(int position) {
        if (!ConnectionDetector.isConnectingToInternet(DashBoardActivity.this)) {
            MethodUtils.errorMsg(DashBoardActivity.this, DashBoardActivity.this.getString(R.string.no_internet));
        } else {
            callDashBoardApi("" + contactLists.get(position).getServerUserId());
        }
    }

    private void implementHighChart(Guagechart gaugeChart) {
        if (gaugeChart != null) {

            HIOptions options = new HIOptions();

            HIChart chart = new HIChart();
            chart.setType("gauge");
            chart.setPlotBorderWidth(0);
            chart.setPlotShadow(false);
            options.setChart(chart);

            HITitle title = new HITitle();
            title.setText("Maintenance Mode");
            options.setTitle(title);

            HIPane pane = new HIPane();
            pane.setStartAngle(-150);
            pane.setEndAngle(150);
            HIBackground background1 = new HIBackground();
            HIGradient gradient = new HIGradient();
            LinkedList<HIStop> stops = new LinkedList<>();
            stops.add(new HIStop(0, HIColor.initWithHexValue("FFF")));
            stops.add(new HIStop(1, HIColor.initWithHexValue("333")));
            background1.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));
            background1.setBorderWidth(0);
            background1.setOuterRadius("109%");
            HIBackground background2 = new HIBackground();
            background2.setBackgroundColor(HIColor.initWithLinearGradient(gradient, stops));
            background2.setBorderWidth(1);
            background2.setOuterRadius("107%");
            HIBackground background3 = new HIBackground();
            HIBackground background4 = new HIBackground();
            background4.setBackgroundColor(HIColor.initWithHexValue("DDD"));
            background4.setBorderWidth(0);
            background4.setOuterRadius("105%");
            background4.setInnerRadius("103%");
            pane.setBackground(new ArrayList<>(Arrays.asList(background1, background2, background3, background4)));
            options.setPane(pane);

            HIYAxis yaxis = new HIYAxis();
            yaxis.setMin(Double.parseDouble(gaugeChart.getGtw()));
            yaxis.setMax(Double.parseDouble(gaugeChart.getMaxweight()));
            yaxis.setMinorTickWidth(1);
            yaxis.setMinorTickLength(10);
            yaxis.setMinorTickPosition("inside");
            yaxis.setMinorTickColor(HIColor.initWithHexValue("666"));
            yaxis.setTickPixelInterval(30);
            yaxis.setTickWidth(2);
            yaxis.setTickPosition("inside");
            yaxis.setTickLength(10);
            yaxis.setTickColor(HIColor.initWithHexValue("666"));
            yaxis.setLabels(new HILabels());
            yaxis.getLabels().setStep(2);
            yaxis.setTitle(new HITitle());
            //yaxis.getTitle().setText("Latest Weight");
            yaxis.getTitle().setText("<p style='color: #104c52; '>Latest Weight</p>");


            // calculations
            double greenStart = Double.parseDouble(gaugeChart.getGtw());
            double greenEnd = (Double.parseDouble(gaugeChart.getGtw()) + Double.parseDouble(gaugeChart.getDiff()));

            double yellowEnd = (Double.parseDouble(gaugeChart.getGtw()) + (2 * Double.parseDouble(gaugeChart.getDiff())));

            double redEnd = Double.parseDouble(gaugeChart.getMaxweight());

            HIPlotBands plotband1 = new HIPlotBands();
            plotband1.setFrom(greenStart);
            plotband1.setTo(greenEnd);
            plotband1.setColor(HIColor.initWithHexValue("55BF3B"));

            HIPlotBands plotband2 = new HIPlotBands();
            plotband2.setFrom(greenEnd);
            plotband2.setTo(yellowEnd);
            plotband2.setColor(HIColor.initWithHexValue("DDDF0D"));

            HIPlotBands plotband3 = new HIPlotBands();
            plotband3.setFrom(yellowEnd);
            plotband3.setTo(redEnd);
            plotband3.setColor(HIColor.initWithHexValue("DF5353"));

            yaxis.setPlotBands(new ArrayList<>(Arrays.asList(plotband1, plotband2, plotband3)));
            options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis)));

            HIGauge gauge = new HIGauge();
            gauge.setName("Latest Weight");
            gauge.setTooltip(new HITooltip());
            gauge.getTooltip().setValueSuffix(" lbs");
            gauge.setData(new ArrayList<>(Collections.singletonList(Integer.valueOf(gaugeChart.getGtw()))));

            options.setSeries(new ArrayList<>(Collections.singletonList(gauge)));

            chartGauge.setOptions(options);
            chartGauge.reload();
        }
    }
}
