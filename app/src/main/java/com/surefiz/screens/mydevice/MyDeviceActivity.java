package com.surefiz.screens.mydevice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.apconfig.ApConfigActivity;
import com.surefiz.screens.barcodescanner.BarCodeScanner;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyDeviceActivity extends BaseActivity implements View.OnClickListener {
    public View view;
    Button btn_update;
    EditText et_id,et_confirm_scale_id;
    TextView tv_scale_id,btn_scan;
    private LoadingData loader;
    private String toolTipText;
    private static final int REQUEST_BAR_CODE = 101;
    private static final int CAMERA = 100;

    /*private String blockCharacterSet = "~*#^|$%&!/+(),;N.";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_my_device, null);
        addContentView(view);
        setViewBind();
        setHeaderView();
        setClickEvent();
        loader = new LoadingData(this);
//        showButton();

        toolTipText = "Scale ID is labeled on\nthe back of your scale";


    }

    private void setClickEvent() {
        btn_update.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        btn_scan.setOnClickListener(this);
    }

    private void setViewBind() {
        btn_update = findViewById(R.id.btn_update);
        et_id = findViewById(R.id.et_id);
        et_confirm_scale_id = findViewById(R.id.et_confirm_scale_id);
        tv_scale_id = findViewById(R.id.tv_scale_id);
        btn_scan = findViewById(R.id.btn_scan);

        setTextFormatter();
    }

    private void setTextFormatter() {
        et_id.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 2)
                    sb.insert(2, "-");
                if (sb.length() > 7)
                    sb.insert(7, "-");
                if (sb.length() > 12)
                    sb.delete(12, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });

        et_confirm_scale_id.addTextChangedListener(new TextWatcher() {

            private boolean isEdiging = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEdiging) return;
                isEdiging = true;
                // removing old dashes
                StringBuilder sb = new StringBuilder();
                sb.append(s.toString().trim().replace("-", ""));

                if (sb.length() > 2)
                    sb.insert(2, "-");
                if (sb.length() > 7)
                    sb.insert(7, "-");
                if (sb.length() > 12)
                    sb.delete(12, sb.length());

                s.replace(0, s.length(), sb.toString());
                isEdiging = false;
            }
        });
    }

    private void setHeaderView() {
        tv_universal_header.setText("Scale ID");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        img_topbar_menu.setVisibility(View.GONE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (LoginShared.getRegistrationDataModel(this) != null) {
            tv_scale_id.setText("Current scale ID: " + formatScaleId(LoginShared.getRegistrationDataModel(MyDeviceActivity.this).getData().getUser().get(0).getUserMac()));
        }
    }

    private String formatScaleId(String scaleId) {
        StringBuilder sb = new StringBuilder();
        sb.append(scaleId.replace("-", ""));

        if (sb.length() > 2)
            sb.insert(2, "-");
        if (sb.length() > 7)
            sb.insert(7, "-");

        return sb.toString();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(MyDeviceActivity.this, SettingsActivity.class);
        startActivity(loginIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }*/

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_update:
                if (!ConnectionDetector.isConnectingToInternet(this)) {
                    MethodUtils.errorMsg(this, getString(R.string.no_internet));
                } else if (et_id.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(this, "Please enter new scale ID");
                } else if (et_id.getText().toString().length() != 12) {
                    MethodUtils.errorMsg(this, "Please enter 10 digit scale ID");
                }else if (et_confirm_scale_id.getText().toString().trim().equals("")) {
                    MethodUtils.errorMsg(this, "Please enter confirm scale ID");
                } else if (!et_id.getText().toString().trim().equals(et_confirm_scale_id.getText().toString().trim())) {
                    MethodUtils.errorMsg(this, "New scale ID and confirm scale ID are not identical");
                } else {
                    System.out.println("replaceScaleIDFormatter: " + replaceScaleIDFormatter(et_id.getText().toString().trim()));
                    changeScaleIdToServer();
                }
                break;
            case R.id.rl_back:
                Intent loginIntent = new Intent(MyDeviceActivity.this, SettingsActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;


            case R.id.btn_scan:
                callScanner();



        }
    }

    //***AVIK
    protected void callScanner(){

        if (checkStoragePermission()) {
            Intent intent=new Intent(this, BarCodeScanner.class);
            startActivityForResult(intent,REQUEST_BAR_CODE);
        } else {
            requestStoragePermission();
        }
    }

    private String replaceScaleIDFormatter(String formattedScaleId) {
        return formattedScaleId.replaceAll("-", "");
    }

    private void showUpdateInfoDialog(String message) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name_splash));
        alertDialog.setMessage(Html.fromHtml(message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    dialog.dismiss();

                    finish();

                    /*if (LoginShared.getRegistrationDataModel(this) != null) {
                        tv_scale_id.setText("Current scale ID: " + formatScaleId(LoginShared.getRegistrationDataModel(MyDeviceActivity.this).getData().getUser().get(0).getUserMac()));
                        et_id.setText("");
                        et_confirm_scale_id.setText("");
                    }*/
                });

        alertDialog.show();
    }

    private void changeScaleIdToServer() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_addDeviceApi = apiInterface.call_addDeviceApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(), replaceScaleIDFormatter(et_id.getText().toString().trim()));
        call_addDeviceApi.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {

                        RegistrationModel registrationModel = LoginShared.getRegistrationDataModel(MyDeviceActivity.this);
                        registrationModel.getData().getUser().get(0).setUserMac(replaceScaleIDFormatter(et_id.getText().toString().trim()));
                        LoginShared.setRegistrationDataModel(MyDeviceActivity.this, registrationModel);

                        LoginShared.setUserMacId(Long.parseLong(replaceScaleIDFormatter(et_id.getText().toString().trim())));

                        JSONObject jsObject = jsonObject.getJSONObject("data");

                        showUpdateInfoDialog(jsObject.getString("message"));

                        //AVIK
                        new InstructionSharedPreference(MyDeviceActivity.this).setAnroidQ(false);


                        /*MethodUtils.errorMsg(MyDeviceActivity.this, jsObject.getString("message"));
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent loginIntent = new Intent(MyDeviceActivity.this, InstructionActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);*/

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(MyDeviceActivity.this);
                        LoginShared.destroySessionTypePreference(MyDeviceActivity.this);
                        LoginShared.setDeviceToken(MyDeviceActivity.this, deviceToken);
                        Intent loginIntent = new Intent(MyDeviceActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(MyDeviceActivity.this, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(MyDeviceActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(MyDeviceActivity.this, getString(R.string.error_occurred));
            }
        });
    }


    //***AVIK
    protected boolean checkStoragePermission() {
        return (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent=new Intent(this, BarCodeScanner.class);
                startActivityForResult(intent,REQUEST_BAR_CODE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case REQUEST_BAR_CODE:
                if ((resultCode == RESULT_OK)) {
                    et_id.setText(data.getStringExtra("barCode"));
                    et_confirm_scale_id.setText(data.getStringExtra("barCode"));
                }
                break;
        }
    }
}
