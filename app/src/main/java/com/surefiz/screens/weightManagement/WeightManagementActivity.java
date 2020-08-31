package com.surefiz.screens.weightManagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bigkoo.pickerview.MyOptionsPickerView;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.interfaces.OnWeightCallback;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.sharedhandler.LoginShared;
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
    EditText et_weight, et_time_loss, et_units, et_weight_managment, et_desired_weight_selection;
    Button btn_submit, btn_accept, btn_decline;
    String units = "", weight = "";
    String[] splited;
    String isnotification = "";
    private LoadingData loader;
    private ArrayList<String> weightList = new ArrayList<>();
    private ArrayList<String> timeList = new ArrayList<>();
    private ArrayList<String> prefferedList = new ArrayList<>();
    private ArrayList<String> managementList = new ArrayList<>();
    private ArrayList<String> desiredWeightSelectionList = new ArrayList<>();
    private String weight_value = "", time_value = "", units_value = "";

    private MyOptionsPickerView managementPopup, selectionPopup,weightPopup,timePopup,weigtUniversalPopupPreffered;
    private int selectedWeightManagmentGoal = 0;
    private int selectedDesiredWeightSelection = 0;
    //private boolean isFirstTimeUpdate = true;
    //private int maintain_Weight_By_Server = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_weight_management, null);
        //isnotification = LoginShared.getWeightFromNotification(this);

        addContentView(view);
        loader = new LoadingData(this);
        initializeView();
        setClickEvent();
        addTimeListAndCall();

        addManagementListAndCall();
        addSelectionListAndCall();

        addPrefferedListAndCall();
        if (!ConnectionDetector.isConnectingToInternet(WeightManagementActivity.this)) {
            MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.no_internet));
        } else {
            getWeightManagementApi();
        }

        if (isnotification.equals("7")) {
            et_time_loss.setEnabled(false);
            et_units.setEnabled(false);
            et_weight.setEnabled(false);
            btn_submit.setVisibility(View.GONE);
            btn_accept.setVisibility(View.VISIBLE);
            btn_decline.setVisibility(View.VISIBLE);
            rl_back.setVisibility(View.VISIBLE);

            LoginShared.setWeightFromNotification(WeightManagementActivity.this, "0");
        }
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApiforweightUpdate();
            }
        });
        btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_time_loss.setEnabled(true);
                et_units.setEnabled(true);
                et_weight.setEnabled(true);

                //  btn_submit.setVisibility(View.VISIBLE);
                btn_accept.setText("Update");
                btn_decline.setOnClickListener(null);
                btn_decline.setAlpha((float) 0.4);
                //  btn_decline.setBackgroundColor(getResources().getColor(R.color.hintColor));

            }
        });

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
                    addWeightListAndCall("LBS");
                }
            }
        });
    }

    private void addManagementListAndCall() {

        managementList.add("Lose And Maintain Weight");
        managementList.add("Maintain Current Weight");

        managementPopup=new MyOptionsPickerView(this);
        managementPopup.setPicker(managementList);
        managementPopup.setCyclic(false);
        managementPopup.setSelectOptions(0);


        managementPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int option1, int option2, int option3) {

                if (option1==0) {
                    et_weight_managment.setText(managementList.get(0));

                    findViewById(R.id.ll_desired_weight_selection).setVisibility(View.VISIBLE);
                    findViewById(R.id.rl_desired_weight_selection).setVisibility(View.VISIBLE);
                    et_desired_weight_selection.setText("");

                    findViewById(R.id.tv_weight).setVisibility(View.GONE);
                    findViewById(R.id.rl_weight).setVisibility(View.GONE);
                    findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                    findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

                    selectedWeightManagmentGoal = 0;
                    selectedDesiredWeightSelection = -1;
                } else {
                    et_weight_managment.setText(managementList.get(1));


                    findViewById(R.id.ll_desired_weight_selection).setVisibility(View.GONE);
                    findViewById(R.id.rl_desired_weight_selection).setVisibility(View.GONE);
                    findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                    findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

                    findViewById(R.id.tv_weight).setVisibility(View.GONE);
                    findViewById(R.id.rl_weight).setVisibility(View.GONE);

                    selectedWeightManagmentGoal = 1;
                    selectedDesiredWeightSelection = -1;
                }
            }
        });

    }

    private void addSelectionListAndCall() {
        desiredWeightSelectionList.add("I Will Provide The Info");
        desiredWeightSelectionList.add("I Want " + getResources().getString(R.string.app_name_splash) + " To Suggest");

        selectionPopup=new MyOptionsPickerView(this);
        selectionPopup.setPicker(desiredWeightSelectionList);
        selectionPopup.setCyclic(false);
        selectionPopup.setSelectOptions(0);

        selectionPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                if (options1==0) {
                    et_desired_weight_selection.setText(desiredWeightSelectionList.get(0));

                    if (selectedWeightManagmentGoal == 1) {
                        findViewById(R.id.tv_weight).setVisibility(View.GONE);
                        findViewById(R.id.rl_weight).setVisibility(View.GONE);
                        findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                        findViewById(R.id.rl_time_loss).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.tv_weight).setVisibility(View.VISIBLE);
                        findViewById(R.id.rl_weight).setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_time_loss).setVisibility(View.VISIBLE);
                        findViewById(R.id.rl_time_loss).setVisibility(View.VISIBLE);
                        et_time_loss.setHint("Please Select");
                        et_time_loss.setText("");
                    }


                    selectedDesiredWeightSelection = 0;
                    et_time_loss.setEnabled(true);
                    et_weight.setEnabled(true);
                } else {
                    et_desired_weight_selection.setText(desiredWeightSelectionList.get(1));

                    findViewById(R.id.tv_weight).setVisibility(View.GONE);
                    findViewById(R.id.rl_weight).setVisibility(View.GONE);
                    findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                    findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

                    selectedDesiredWeightSelection = 1;
                }
            }
        });

    }

    private void showAndDismissSelectionPopup() {

        selectionPopup.show();

    }

    private void showAndDismissManagementPopup() {
        managementPopup.show();

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

                        System.out.println("weightdata: " + jsonObject.toString());
                        if (jsonObject.optInt("status") == 1) {
                            JSONObject jsObject = jsonObject.getJSONObject("data");
                        }

                        Intent dashboard;
                        //  if (isnotification.equals("7")){
                        dashboard = new Intent(WeightManagementActivity.this, DashBoardActivity.class);
                        // }else
                        //  dashboard = new Intent(WeightManagementActivity.this, SettingsActivity.class);

                        startActivity(dashboard);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();

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

                    System.out.println("getWeightManagementApi: " + jsonObject.toString());
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        JSONObject jsnObject = jsObject.getJSONObject("WeightDetails");
                        showData(jsnObject);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(WeightManagementActivity.this);
                        LoginShared.destroySessionTypePreference(WeightManagementActivity.this);
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

    private void showData(JSONObject jsnObject) {

        units = jsnObject.optString("preferredUnits");
        weight = et_weight.getText().toString().trim();
        splited = weight.split(" ");

        if (units.equalsIgnoreCase("0")) {
            addWeightListAndCall("LBS");
        } else {
            addWeightListAndCall("KG");
        }

        if (units.equals("0")) {
            et_units.setText("LBS/INCH");
        } else {
            et_units.setText("KG/CM");
        }

        if (units.equalsIgnoreCase("0")) {
            units_value = "LBS/INCH";
        } else {
            units_value = "KG/CM";
        }

        weight_value = jsnObject.optString("desiredWeight");
        time_value = jsnObject.optString("timeToLoseWeight");


        if (jsnObject.optString("type").equalsIgnoreCase("1")) {

            et_weight_managment.setText(managementList.get(1));

            findViewById(R.id.ll_desired_weight_selection).setVisibility(View.GONE);
            findViewById(R.id.rl_desired_weight_selection).setVisibility(View.GONE);
            findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
            findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

            findViewById(R.id.tv_weight).setVisibility(View.GONE);
            findViewById(R.id.rl_weight).setVisibility(View.GONE);

            selectedWeightManagmentGoal = 1;
            selectedDesiredWeightSelection = -1;

        } else if (jsnObject.optString("type").equalsIgnoreCase("2")) {

            et_weight_managment.setText(managementList.get(0));

            findViewById(R.id.ll_desired_weight_selection).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_desired_weight_selection).setVisibility(View.VISIBLE);
            et_desired_weight_selection.setText("");

            findViewById(R.id.tv_weight).setVisibility(View.GONE);
            findViewById(R.id.rl_weight).setVisibility(View.GONE);
            findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
            findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

            selectedWeightManagmentGoal = 0;
            selectedDesiredWeightSelection = -1;
        }


        if (jsnObject.optInt("maintain_Weight_By_Server") == 1) {

            et_desired_weight_selection.setText(desiredWeightSelectionList.get(1));

            //et_weight_managment.setText(managementList.get(1));
            findViewById(R.id.ll_desired_weight_selection).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_desired_weight_selection).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
            findViewById(R.id.rl_time_loss).setVisibility(View.GONE);

            findViewById(R.id.tv_weight).setVisibility(View.GONE);
            findViewById(R.id.rl_weight).setVisibility(View.GONE);

            selectedWeightManagmentGoal = 1;
            selectedDesiredWeightSelection = -1;

        } else if (jsnObject.optInt("maintain_Weight_By_Server") == 0) {

            et_desired_weight_selection.setText(desiredWeightSelectionList.get(0));

            if (selectedWeightManagmentGoal == 1) {
                findViewById(R.id.tv_weight).setVisibility(View.GONE);
                findViewById(R.id.rl_weight).setVisibility(View.GONE);
                findViewById(R.id.tv_time_loss).setVisibility(View.GONE);
                findViewById(R.id.rl_time_loss).setVisibility(View.GONE);
            } else {
                findViewById(R.id.tv_weight).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_weight).setVisibility(View.VISIBLE);
                findViewById(R.id.tv_time_loss).setVisibility(View.VISIBLE);
                findViewById(R.id.rl_time_loss).setVisibility(View.VISIBLE);
                et_time_loss.setHint("Please Select");
                et_time_loss.setText("");
            }


            selectedDesiredWeightSelection = 0;
            et_time_loss.setEnabled(true);
            et_weight.setEnabled(true);
        }


        if (jsnObject.optInt("maintain_Weight_By_Server") == 1) {
            findViewById(R.id.tv_time_loss).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_time_loss).setVisibility(View.VISIBLE);
            et_time_loss.setText("TBD");
        } else if (jsnObject.optInt("type") == 2 && jsnObject.optInt("maintain_Weight_By_Server") == 0) {
            findViewById(R.id.tv_weight).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_weight).setVisibility(View.VISIBLE);

            if (!weight_value.equalsIgnoreCase("0.0 LBS") &&
                    !weight_value.equalsIgnoreCase("0 LBS") &&
                    !weight_value.equalsIgnoreCase("0.00 LBS") &&
                    !weight_value.equalsIgnoreCase("0.0 KG") &&
                    !weight_value.equalsIgnoreCase("0 KG") &&
                    !weight_value.equalsIgnoreCase("0.00 KG") &&
                    !weight_value.equalsIgnoreCase("0")) {
                et_weight.setText(weight_value);
            } else {
                et_weight.setText("TBD");
            }
        }


        if (isNonZeroValue(weight_value) &&
                !jsnObject.optString("type").equalsIgnoreCase("1")) {

            findViewById(R.id.tv_weight).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_weight).setVisibility(View.VISIBLE);
            et_weight.setText(weight_value);
        }

        et_weight.setText(jsnObject.optString("desiredWeight"));

        if (jsnObject.optString("timeToLoseWeight").equals("0 Weeks")) {
            et_time_loss.setText("");
        } else {
            et_time_loss.setText(jsnObject.optString("timeToLoseWeight"));
        }


        if (jsnObject.optInt("type") == 2 && jsnObject.optInt("maintain_Weight_By_Server") == 1) {
            if (jsnObject.optString("timeToLoseWeight").equals("0 Weeks")) {
                et_time_loss.setText("TBD");
            }
            et_time_loss.setEnabled(false);
            et_weight.setEnabled(false);
        }
    }

    private boolean isNonZeroValue(String weight_value) {
        return !weight_value.equalsIgnoreCase("0.0 LBS") ||
                !weight_value.equalsIgnoreCase("0 LBS") ||
                !weight_value.equalsIgnoreCase("0.00 LBS") ||
                !weight_value.equalsIgnoreCase("0.0 KG") ||
                !weight_value.equalsIgnoreCase("0 KG") ||
                !weight_value.equalsIgnoreCase("0.00 KG") ||
                !weight_value.equalsIgnoreCase("0");
    }

    private void addPrefferedListAndCall() {

        prefferedList.add("LBS/INCH");
        prefferedList.add("KG/CM");

        weigtUniversalPopupPreffered=new MyOptionsPickerView(this);
        weigtUniversalPopupPreffered.setPicker(prefferedList);
        weigtUniversalPopupPreffered.setCyclic(false);
        weigtUniversalPopupPreffered.setSelectOptions(0);

        weigtUniversalPopupPreffered.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                weight = et_weight.getText().toString().trim();
                splited = weight.split(" ");

                String value=prefferedList.get(options1);

                if (value.equals("KG/CM")) {
                    units = "KG";
                } else {
                    units = "LB";
                }
                if (units.equals(splited[1])) {
                } else {
                    if (value.equals("KG/CM")) {
                        et_weight.setText(Math.round(Double.parseDouble(splited[0]) * 0.45359237) + " KG");
                        units = "KG";
                    }
                    if (value.equals("LBS/INCH")) {
                        units = "LBS";
                        et_weight.setText((Math.round(Double.parseDouble(splited[0]) / 0.45359237)) + " LB");
                    }
                }
            }
        });


    }

    private void addTimeListAndCall() {
        for (int i = 1; i <=30; i++) {
            timeList.add(i + " " + "Weeks");
        }

        timePopup=new MyOptionsPickerView(this);
        timePopup.setPicker(timeList);
        timePopup.setCyclic(false);
        timePopup.setSelectOptions(0);
        timePopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                et_time_loss.setText(timeList.get(options1));

            }
        });

    }

    private void addWeightListAndCall(String change) {
        weightList.clear();
        if (change.equals("LBS")) {
            for (int i = 35; i <= 400; i++) {
                weightList.add(i + " " + change);
            }

        } else {
            for (int i = 20; i <= 180; i++) {
                weightList.add(i + " " + change);
            }
        }


        weightPopup=new MyOptionsPickerView(this);
        weightPopup.setPicker(weightList);
        weightPopup.setCyclic(false);
        weightPopup.setSelectOptions(0);


        weightPopup.setOnoptionsSelectListener(new MyOptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {

                et_weight.setText(weightList.get(options1));
            }
        });

    }

    private void setClickEvent() {
        rl_back.setOnClickListener(this);
        et_weight.setOnClickListener(this);
        et_time_loss.setOnClickListener(this);
        et_units.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        et_weight_managment.setOnClickListener(this);
        et_desired_weight_selection.setOnClickListener(this);
    }

    private void initializeView() {
        setHeaderView();
        btn_accept = findViewById(R.id.btn_accept);
        btn_decline = findViewById(R.id.btn_decline);
        et_weight = findViewById(R.id.et_weight);
        et_time_loss = findViewById(R.id.et_time_loss);
        et_units = findViewById(R.id.et_units);
        et_desired_weight_selection = findViewById(R.id.et_desired_weight_selection);
        et_weight_managment = findViewById(R.id.et_weight_managment);
        btn_submit = findViewById(R.id.btn_submit);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Weight Management");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
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
                if (getIntent().getBooleanExtra("isInitiatedFromProfile", false)) {
                    finish();
                } else {
                    Intent loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
                break;
            case R.id.et_weight:
                showAndDismissWeightPopup();
                break;

            case R.id.et_time_loss:
                showAndDismissTimePopup();
                break;

            case R.id.et_units:
                showAndDismissPrefferedPopup();
                break;

            case R.id.btn_submit:
                showWeightUpdateDialog();
                break;

            case R.id.et_weight_managment:
                showAndDismissManagementPopup();
                break;

            case R.id.et_desired_weight_selection:
                showAndDismissSelectionPopup();
                break;
        }
    }

    private void showAndDismissPrefferedPopup() {
        weigtUniversalPopupPreffered.show();

    }

    private void sendWeightManagementDetails() {

        loader.show_with_label("Loading");

        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);


        String type = "";
        String userselectionbody = "";

        String weight = et_weight.getText().toString().trim();
        String time = et_time_loss.getText().toString().trim();

        if (!weight.equalsIgnoreCase("")) {
            String[] weights = weight.split(" ");
            weight = weights[0];
        }

        if (time.equalsIgnoreCase("") || time.equalsIgnoreCase("TBD")) {
            time = "0";
        } else {
            String[] splittedTime = time.split(" ");
            time = splittedTime[0].trim();
        }


        if (selectedWeightManagmentGoal == 0) {
            type = "2";
            if (selectedDesiredWeightSelection == 0) {
                userselectionbody = "0";

                if (time.equalsIgnoreCase("0")) {
                    loader.dismiss();
                    MethodUtils.errorMsg(WeightManagementActivity.this, "Enter time to lose weight.");
                    return;
                }
                //weight = et_weight.getText().toString().trim();
                //time = et_time_loss.getText().toString().trim();
            } else if (selectedDesiredWeightSelection == 1) {
                userselectionbody = "1";
                weight = "";
                time = "";
            } else {
                loader.dismiss();
                MethodUtils.errorMsg(WeightManagementActivity.this, "Enter desired weight selection");
                return;
            }
        } else {
            type = "2";
            userselectionbody = "1";
            //weight = "";
            time = "";
        }


        Call<ResponseBody> call_sendWeightManagementApi = apiInterface.call_sendWeightManagementForWeight(LoginShared.getRegistrationDataModel(WeightManagementActivity.this).getData().getToken(),
                LoginShared.getRegistrationDataModel(WeightManagementActivity.this).getData().getUser().get(0).getUserId(),
                weight, time, units, type, userselectionbody);

        call_sendWeightManagementApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    System.out.println("sendWeightDetails: " + jsonObject.toString());
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        showResponseDialog(jsObject.getString("message"));
                        /*MethodUtils.errorMsg(WeightManagementActivity.this, jsObject.getString("message"));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent;
                                if (isnotification.equals("7")) {
                                    loginIntent = new Intent(WeightManagementActivity.this, DashBoardActivity.class);

                                } else
                                    loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);

                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);*/

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(WeightManagementActivity.this);
                        LoginShared.destroySessionTypePreference(WeightManagementActivity.this);
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

    public void showResponseDialog(String message) {

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText(message);
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                Intent loginIntent;
                if (isnotification.equals("7")) {
                    loginIntent = new Intent(WeightManagementActivity.this, DashBoardActivity.class);
                } else {
                    loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);
                }
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    private void showAndDismissWeightPopup() {

        weightPopup.show();

    }

    private void showAndDismissTimePopup() {

        timePopup.show();

    }

    @Override
    public void onBackPressed() {
        if (isnotification.equals("7")) {
            Intent loginIntent = new Intent(WeightManagementActivity.this, DashBoardActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (getIntent().getBooleanExtra("isInitiatedFromProfile", false)) {
            finish();
        } else {
            //super.onBackPressed();
            Intent loginIntent = new Intent(WeightManagementActivity.this, SettingsActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }
    }

    public void showWeightUpdateDialog() {

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText(getString(R.string.Changing));
        customAlert.setCancelVisible();
        customAlert.setKeyName("No","Yes");
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();

                if (selectedWeightManagmentGoal == 0 && selectedDesiredWeightSelection == 0) {
                    if (et_weight.getText().toString().trim().equals("")) {
                        MethodUtils.errorMsg(WeightManagementActivity.this, "Enter desired weight");
                    } else if (et_time_loss.getText().toString().trim().equals("")) {
                        MethodUtils.errorMsg(WeightManagementActivity.this, "Enter time to lose weight");
                    } else if (!ConnectionDetector.isConnectingToInternet(WeightManagementActivity.this)) {
                        MethodUtils.errorMsg(WeightManagementActivity.this, getString(R.string.no_internet));
                    } else {
                        sendWeightManagementDetails();
                    }

                } else {

                    sendWeightManagementDetails();

                }
            }
        });

        customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();

                et_weight.setText(weight_value);
                et_time_loss.setText(time_value);
                et_units.setText(units_value);
            }
        });
    }
}
