package com.surefiz.screens.weightManagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.AddUserDialogForOTP;
import com.surefiz.dialog.universalpopup.UniversalPopup;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.screens.wificonfig.WifiConfigActivity;
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

public class WeightManagementActivity extends BaseActivity implements View.OnClickListener {
    public View view;
    EditText et_weight, et_time_loss, et_units;
    Button btn_submit;
    private LoadingData loader;
    private List<String> weightList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private List<String> prefferedList = new ArrayList<>();
    private UniversalPopup weightPopup, timePopup, prefferedPopup;
    private String weight_value = "", time_value = "", units_value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_weight_management, null);
        addContentView(view);
        loader = new LoadingData(this);
        initializeView();
        setClickEvent();
        addTimeListAndCall();
        addPrefferedListAndCall();
        if (!ConnectionDetector.isConnectingToInternet(WeightManagementActivity.this)) {
            MethodUtils.errorMsg(WeightManagementActivity.this,
                    getString(R.string.no_internet));
        } else {
            getWeightManagementApi();
        }

        et_units.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                weightList.clear();

                if (et_units.getText().toString().equals("KG/CM")) {
                    addWeightListAndCall("KG");
                } else {
                    addWeightListAndCall("LB");
                }
            }
        });
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
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        JSONObject jsnObject = jsObject.getJSONObject("WeightDetails");
                        setData(jsnObject);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(WeightManagementActivity.this);
                        LoginShared.destroySessionTypePreference();
                        LoginShared.setDeviceToken(WeightManagementActivity.this, deviceToken);
                        Intent loginIntent = new Intent(WeightManagementActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(WeightManagementActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void setData(JSONObject jsnObject) {
        et_weight.setText(jsnObject.optString("desiredWeight"));
        et_time_loss.setText(jsnObject.optString("timeToLoseWeight"));
        String units = jsnObject.optString("preferredUnits");
        if (units.equalsIgnoreCase("LB")) {
            addWeightListAndCall("LB");
        } else {
            addWeightListAndCall("KG");
        }
        if (jsnObject.optString("preferredUnits").equals("LB")) {
            et_units.setText("LB/INCH");
        } else {
            et_units.setText("KG/CM");
        }
        if (units.equalsIgnoreCase("LB")) {
            units_value = "LB/INCH";
        }else{
            units_value = "KG/CM";
        }
        weight_value = jsnObject.optString("desiredWeight");
        time_value = jsnObject.optString("timeToLoseWeight");
    }

    private void addPrefferedListAndCall() {
        prefferedList.add("KG/CM");
        prefferedList.add("LB/INCH");

        prefferedPopup = new UniversalPopup(WeightManagementActivity.this, prefferedList, et_units);
    }

    private void addTimeListAndCall() {
        for (int i = 1; i < 261; i++) {
            timeList.add(i + " " + "Weeks");
        }
        timePopup = new UniversalPopup(WeightManagementActivity.this, timeList, et_time_loss);
    }

    private void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LB")) {
            for (int i = 5; i < 1001; i++) {
                weightList.add(i + " " + change);
            }
        } else {
            for (int i = 5; i < 455; i++) {
                weightList.add(i + " " + change);
            }
        }
        weightPopup = new UniversalPopup(WeightManagementActivity.this, weightList, et_weight);
    }

    private void setClickEvent() {
        rl_back.setOnClickListener(this);
        et_weight.setOnClickListener(this);
        et_time_loss.setOnClickListener(this);
        et_units.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    private void initializeView() {
        setHeaderView();
        et_weight = findViewById(R.id.et_weight);
        et_time_loss = findViewById(R.id.et_time_loss);
        et_units = findViewById(R.id.et_units);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Weight Management");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        img_topbar_menu.setVisibility(View.GONE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.rl_back:
                Intent loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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
            case R.id.et_units:
                if (weightPopup != null && weightPopup.isShowing()) {
                    weightPopup.dismiss();
                } else if (timePopup != null && timePopup.isShowing()) {
                    timePopup.dismiss();
                } else if (prefferedPopup != null && prefferedPopup.isShowing()) {
                    prefferedPopup.dismiss();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAndDismissPrefferedPopup();
                        }
                    }, 100);
                }
                break;
            case R.id.btn_submit:
                showWeightUpdateDialog();
                break;
        }
    }

    private void showAndDismissPrefferedPopup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                prefferedPopup.showAsDropDown(et_units);
            }
        }, 100);
    }

    private void sendWeightManagementDetails() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ResponseBody> call_sendWeightManagementApi = apiInterface.call_sendWeightManagement(LoginShared.getRegistrationDataModel(WeightManagementActivity.this).getData().getToken(),
                LoginShared.getRegistrationDataModel(WeightManagementActivity.this).getData().getUser().get(0).getUserId(),
                et_weight.getText().toString().trim(), et_time_loss.getText().toString().trim());
        call_sendWeightManagementApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        MethodUtils.errorMsg(WeightManagementActivity.this, jsObject.getString("message"));
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);


                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(WeightManagementActivity.this);
                        LoginShared.destroySessionTypePreference();
                        LoginShared.setDeviceToken(WeightManagementActivity.this, deviceToken);
                        Intent loginIntent = new Intent(WeightManagementActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(WeightManagementActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.error_occurred));
            }
        });

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    public void showWeightUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WeightManagementActivity.this);
        alertDialog.setTitle(R.string.app_name_otp);
        alertDialog.setMessage("Changing these parameters will reassign the subgoal progress. Current progress will get replaced. Are you sure want to change?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (et_weight.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(WeightManagementActivity.this, "Enter desired weight");
                } else if (et_time_loss.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(WeightManagementActivity.this, "Enter time to lose weight");
                } else if (!ConnectionDetector.isConnectingToInternet(WeightManagementActivity.this)) {
                    MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.no_internet));
                } else {
                    sendWeightManagementDetails();
                }
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                et_weight.setText(weight_value);
                et_time_loss.setText(time_value);
                et_units.setText(units_value);
            }
        });

        alertDialog.create();

        alertDialog.show();
    }
}
