package com.surefiz.screens.userconfirmation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserConfirmationActivity extends BaseActivity implements View.OnClickListener {

    public View view;
    EditText et_weight, et_time_loss;
    Button btn_accept, btn_provide;
    String isnotification;
    String units = "";
    private LoadingData loader;
    private List<String> weightList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private UniversalPopup weightPopup, timePopup;
    private int maintainWeightByServer = 0;
    private int savedType = 1;
    private int savedUnits = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_user_confirmation, null);
        addContentView(view);
        isnotification = LoginShared.getWeightFromNotification(this);

        LoginShared.setWeightFromNotification(this, "0");

        loader = new LoadingData(this);
        initializeView(view);
        showViewMode();
        addTimeListAndCall();
        setClickEvent();

        if (!ConnectionDetector.isConnectingToInternet(UserConfirmationActivity.this)) {
            MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.no_internet));
        } else {
            getWeightManagementApi();
        }
    }

    private void setClickEvent() {
        btn_accept.setOnClickListener(this);
        btn_provide.setOnClickListener(this);
        et_weight.setOnClickListener(this);
        et_time_loss.setOnClickListener(this);
    }

    private void showViewMode() {
        et_weight.setEnabled(false);
        et_time_loss.setEnabled(false);
        btn_accept.setEnabled(true);
        btn_provide.setEnabled(true);
    }

    private void initializeView(View view) {
        setHeaderView();
        et_weight = view.findViewById(R.id.et_weight);
        et_time_loss = view.findViewById(R.id.et_time_loss);
        btn_accept = view.findViewById(R.id.btn_accept);
        btn_provide = view.findViewById(R.id.btn_provide);

    }

    private void getWeightManagementApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> call_weight_management = apiInterface.call_getWeightManagement(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserMac());
        call_weight_management.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    System.out.println("getWeightManagementApiConfirm: " + jsonObject.toString());
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        JSONObject jsnObject = jsObject.getJSONObject("WeightDetails");
                        setData(jsnObject);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(UserConfirmationActivity.this);
                        LoginShared.destroySessionTypePreference(UserConfirmationActivity.this);
                        LoginShared.setDeviceToken(UserConfirmationActivity.this, deviceToken);
                        Intent loginIntent = new Intent(UserConfirmationActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(UserConfirmationActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void sendWeightManagementDetails() {
        String weight = "";
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);


        weight = et_weight.getText().toString().trim();
        if (!weight.equalsIgnoreCase("")) {
            String[] splittedWeights = weight.split(" ");
            weight = splittedWeights[0].trim();
        } else {
            loader.dismiss();
            MethodUtils.errorMsg(UserConfirmationActivity.this, "Enter desired weight.");
            return;
        }

        String type = "2";
        String maintainWeightByServer = "0";

        String time = et_time_loss.getText().toString().trim();

        if (!time.equalsIgnoreCase("") && !time.equalsIgnoreCase("TBD")) {
            String[] splittedTime = time.split(" ");
            time = splittedTime[0].trim();
        } else {
            loader.dismiss();
            MethodUtils.errorMsg(UserConfirmationActivity.this, "Enter time to lose weight.");
            return;
        }


        /*Call<ResponseBody> call_sendWeightManagementApi = apiInterface.call_sendWeightManagement(LoginShared.getRegistrationDataModel(UserConfirmationActivity.this).getData().getToken(),
                LoginShared.getRegistrationDataModel(UserConfirmationActivity.this).getData().getUser().get(0).getUserId(),
                weight, et_time_loss.getText().toString().trim(), units);*/


        Call<ResponseBody> call_sendWeightManagementApi = apiInterface.call_sendWeightManagementForWeight(LoginShared.getRegistrationDataModel(UserConfirmationActivity.this).getData().getToken(),
                LoginShared.getRegistrationDataModel(UserConfirmationActivity.this).getData().getUser().get(0).getUserId(),
                weight,
                time,
                String.valueOf(savedUnits),
                type,
                maintainWeightByServer);


        call_sendWeightManagementApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    System.out.println("sendWeightManagementDetails: " + jsonObject.toString());

                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(UserConfirmationActivity.this, jsObject.getString("message"));
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent;
                                if (isnotification.equals("7")) {
                                    loginIntent = new Intent(UserConfirmationActivity.this, DashBoardActivity.class);
                                } else
                                    loginIntent = new Intent(UserConfirmationActivity.this, SettingsActivity.class);

                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(UserConfirmationActivity.this);
                        LoginShared.destroySessionTypePreference(UserConfirmationActivity.this);
                        LoginShared.setDeviceToken(UserConfirmationActivity.this, deviceToken);
                        Intent loginIntent = new Intent(UserConfirmationActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(UserConfirmationActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.error_occurred));
            }
        });

    }

    private void setData(JSONObject jsnObject) {
        et_weight.setText(jsnObject.optString("desiredWeight"));

        if (jsnObject.optString("timeToLoseWeight").equalsIgnoreCase("0 Weeks")) {
            et_time_loss.setText("TBD");
        } else {
            et_time_loss.setText(jsnObject.optString("timeToLoseWeight"));
        }

        maintainWeightByServer = jsnObject.optInt("maintain_Weight_By_Server");
        savedType = jsnObject.optInt("type");
        savedUnits = jsnObject.optInt("preferredUnits");

        /*if (units.equalsIgnoreCase("LB")) {
            addWeightListAndCall("LB");
        } else {
            addWeightListAndCall("KG");
        }*/


        if (savedUnits == 1) {
            addWeightListAndCall("KG");
        } else {
            addWeightListAndCall("LB");
        }
    }

    private void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LB")) {
            for (int i = 5; i < 301; i++) {
                weightList.add(i + " " + change);
            }

        } else {
            for (int i = 5; i < 151; i++) {
                weightList.add(i + " " + change);
            }
        }
        weightPopup = new UniversalPopup(UserConfirmationActivity.this, weightList, et_weight);/*, new OnWeightCallback() {
            @Override
            public void onSuccess(String value) {
                splited = value.split(" ");

//                if (splited[1].equals("LB")) {
                if (et_units.getText().toString().equals("KG/CM")) {
                    et_weight.setText(String.valueOf(Double.parseDouble(splited[0]) * 0.45359237) + "KG");
                } else {

                }
//                }
//                if (splited[1].equals("KG")) {
                if (et_units.getText().toString().equals("LB/INCH")) {
                    et_weight.setText(String.valueOf(Double.parseDouble(splited[0]) * 2.2046226218) + "LB");
                } else {

                }
//                }
            }
        });*/
    }

    private void callApiforweightUpdate() {
        if (btn_accept.getText().equals("Update")) {
            sendWeightManagementDetails();

        } else {
            loader.show_with_label("Loading");
            Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
            final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            /*Call<ResponseBody> callApifor_server_weight = apiInterface.call_Apiforserver_weight(LoginShared.getRegistrationDataModel(this).getData().getToken(), "application/json",
                    LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), "1");*/

            Call<ResponseBody> callApifor_server_weight = apiInterface.call_Apiforserver_weight(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                    LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), "1");
            callApifor_server_weight.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (loader != null && loader.isShowing())
                        loader.dismiss();
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.optInt("status") == 1) {
                            JSONObject jsObject = jsonObject.getJSONObject("data");
                            showSuccessMessage(jsObject.getString("message"));
                        } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                            String deviceToken = LoginShared.getDeviceToken(UserConfirmationActivity.this);
                            LoginShared.destroySessionTypePreference(UserConfirmationActivity.this);
                            LoginShared.setDeviceToken(UserConfirmationActivity.this, deviceToken);
                            Intent loginIntent = new Intent(UserConfirmationActivity.this, LoginActivity.class);
                            startActivity(loginIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finishAffinity();
                        } else {
                            JSONObject jsObject = jsonObject.getJSONObject("data");
                            MethodUtils.errorMsg(UserConfirmationActivity.this, jsObject.getString("message"));
                        }

                        /*Intent dashboard;
                        //  if (isnotification.equals("7")){
                        dashboard = new Intent(UserConfirmationActivity.this, DashBoardActivity.class);
                        // }else
                        //  dashboard = new Intent(WeightManagementActivity.this, SettingsActivity.class);

                        startActivity(dashboard);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();*/


                    } catch (Exception e) {
                        MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.error_occurred));
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (loader != null && loader.isShowing())
                        loader.dismiss();
                    MethodUtils.errorMsg(UserConfirmationActivity.this, getString(R.string.error_occurred));


                }
            });
        }
    }

    private void setHeaderView() {
        tv_universal_header.setText("User Confirmation");
        rl_back.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        img_topbar_menu.setVisibility(View.GONE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void showConfirmDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserConfirmationActivity.this);
        alertDialog.setTitle(R.string.app_name_otp);
        alertDialog.setMessage("Please note, if you decline you will need to provide the desired weight and time to lose weight. You can provide your input in User Confirmation Screen. Would you like to decline?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                et_weight.setEnabled(true);
                et_time_loss.setEnabled(true);
                btn_provide.setEnabled(false);
                btn_provide.setBackgroundResource(R.drawable.rounded_corner_provide);
                btn_accept.setText("Update");
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create();

        alertDialog.show();
    }


    public void showSuccessMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserConfirmationActivity.this);
        alertDialog.setTitle(R.string.app_name_otp);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent dashboard = new Intent(UserConfirmationActivity.this, DashBoardActivity.class);
                startActivity(dashboard);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finishAffinity();
            }
        });


        alertDialog.create();

        alertDialog.show();
    }

    private void showAndDismissWeightPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                weightPopup.showAsDropDown(et_weight);
            }
        }, 100);
    }

    private void showAndDismissTimePopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timePopup.showAsDropDown(et_time_loss);
            }
        }, 100);
    }

    private void addTimeListAndCall() {
        //for (int i = 1; i < 261; i++) {
        for (int i = 1; i <= 104; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(UserConfirmationActivity.this, timeList, et_time_loss);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_accept:
                callApiforweightUpdate();
                break;
            case R.id.btn_provide:
                showConfirmDialog();
                break;
            case R.id.et_weight:
                if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissWeightPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.et_time_loss:
                if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissTimePopup();
                        }
                    }, 100);
                }
                break;
        }
    }
}
