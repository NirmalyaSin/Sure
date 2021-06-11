package com.surefiz.screens.apconfig;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.surefiz.R;
import com.surefiz.dialog.CustomAlert;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.screens.setupPreparation.SetUpPreparation;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.onecoder.scalewifi.api.ScaleWiFiConfig;
import cn.onecoder.scalewifi.api.impl.OnScaleWiFiConfigResultListener;

public class ApConfigActivity extends AppCompatActivity implements View.OnClickListener,
        OnScaleWiFiConfigResultListener,
        PopupMenu.OnMenuItemClickListener {
    LoadingData loader;
    WifiReceiver wifiReceiver = new WifiReceiver();
    private EditText editSSID, editBSSID, editPassword;
    private Button btnlockwifi, btnConfigure,btn_skip;
    private PopupMenu popup;
    private WifiManager mWifiManager;
    private List<ScanResult> scanResultsWifi = new ArrayList<>();
    private ScaleWiFiConfig scaleWiFiConfig;
    private boolean isAutoConnecting = false;
    private ImageView iv_showPassword;
    private ImageView iv_hidePassword;
    private RelativeLayout rl_back;
    private static int LOCATION_SETTINGS = 1000;
    private static int DELAY = 15000;
    private PermissionHelper permissionHelper;
    private ConnectivityManager.NetworkCallback networkCallback;
    ConnectivityManager connectivityManager;

    protected boolean fromSettings=false;
    public boolean fromLogin=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ap_config);
        ButterKnife.bind(this);
        permissionHelper = new PermissionHelper(this);
        loader = new LoadingData(this);
        scaleWiFiConfig = new ScaleWiFiConfig();

        if(getIntent().hasExtra("fromLogin")){
            fromLogin=getIntent().getBooleanExtra("fromLogin",false);
        }

        if(getIntent().hasExtra("fromSettings")){
            fromSettings=getIntent().getBooleanExtra("fromSettings",false);
        }

        initializeView();

        if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION) && checkLocationStatus()) {

            showConnectedWifiSSID();
        } else if (!permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
            permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
        } else if (!checkLocationStatus()) {
            buildAlertMessageNoGps();
        }


    }

    private void initializeView() {
        //TextView tvApConfig = view.findViewById(R.id.tvApConfig);
        //tvApConfig.setText(Html.fromHtml(getString(R.string.ap_config)));
        editSSID = findViewById(R.id.editSSID);
        editBSSID = findViewById(R.id.editBSSID);
        editPassword = findViewById(R.id.editPassword);
        btnlockwifi = findViewById(R.id.btnlockwifi);
        btnConfigure = findViewById(R.id.btnConfigure);
        iv_showPassword = findViewById(R.id.iv_showPassword);
        iv_hidePassword = findViewById(R.id.iv_hidePassword);
        rl_back = findViewById(R.id.rl_back);
        btn_skip = findViewById(R.id.btn_skip);
        //btnConfigure.setVisibility(View.INVISIBLE);
        btnlockwifi.setOnClickListener(this);
        btnConfigure.setOnClickListener(this);
        editSSID.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
        iv_showPassword.setOnClickListener(this);
        iv_hidePassword.setOnClickListener(this);

        if(fromLogin)
            btn_skip.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                onBackPressed();
                break;

            case R.id.btn_skip:
                if (!new InstructionSharedPreference(this).isInstructionShown(this, LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId())) {
                    Intent details = new Intent(this, InstructionActivity.class);
                    startActivity(details);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    break;
                } else {
                    Intent details = new Intent(this, DashBoardActivity.class);
                    startActivity(details);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }

                break;
            case R.id.btnConfigure:

                if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION) && checkLocationStatus()) {

                    String ssid = editSSID.getText().toString();
                    String pwd = editPassword.getText().toString();
                    if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {

                        MethodUtils.errorMsg(this,"Please enter your WiFi Password.");

                        return;
                    }
                    editSSID.setEnabled(false);
                    editPassword.setEnabled(false);
                    btnlockwifi.setEnabled(false);
                    //btnConfigure.setVisibility(View.VISIBLE);
                    btnConfigure.setEnabled(true);
                    btnlockwifi.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edit_rounded_corner_blue));
                    btnConfigure.setBackground(ContextCompat.getDrawable(this, R.drawable.login_button_gradient));


                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                        androidQ();
                    }else {

                        apConfigAction();
                    }

                } else if (!permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {

                    permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
                } else if (!checkLocationStatus()) {
                    buildAlertMessageNoGps();
                }


                break;
       /*     case R.id.btnlockwifi:

                if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION) && checkLocationStatus()) {

                    String ssid = editSSID.getText().toString();
                    String pwd = editPassword.getText().toString();
                    if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                        Toast.makeText(this, "Please input SSID and password.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editSSID.setEnabled(false);
                    editPassword.setEnabled(false);
                    btnlockwifi.setEnabled(false);
                    btnConfigure.setVisibility(View.VISIBLE);
                    btnConfigure.setEnabled(true);
                    btnlockwifi.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edit_rounded_corner_blue));
                    btnConfigure.setBackground(ContextCompat.getDrawable(this, R.drawable.login_button_gradient));

                } else if (!permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {

                    permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
                } else if (!checkLocationStatus()) {
                    buildAlertMessageNoGps();
                }
                break;*/


            case R.id.editSSID:


                hideSoftKeyBoard();
                if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION) && checkLocationStatus()) {

                    isAutoConnecting = false;
                    hideSoftKeyBoard();
                    getAvailableSSID();
                } else if (!permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
                    permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
                } else if (!checkLocationStatus()) {
                    buildAlertMessageNoGps();
                }
                break;
            case R.id.iv_showPassword:

                iv_showPassword.setVisibility(View.GONE);
                iv_hidePassword.setVisibility(View.VISIBLE);
                editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                break;
            case R.id.iv_hidePassword:

                iv_showPassword.setVisibility(View.VISIBLE);
                iv_hidePassword.setVisibility(View.GONE);
                editPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance());
                break;
        }
    }

    private void apConfigAction(){

        String ssid = editSSID.getText().toString();
        String pwd = editPassword.getText().toString();
        if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
            MethodUtils.errorMsg(this,"Please enter your WiFi Password.");
            return;
        }
        editSSID.setEnabled(false);
        editPassword.setEnabled(false);
        isAutoConnecting = true;
        hideSoftKeyBoard();
        getAvailableSSID();
    }

    private void androidQ() {

        final String scaleName = "WS915_V2.6_V1.5-";
        long scaleId = LoginShared.getUserMacId(this);
        String networkSSID = " "+scaleName + scaleId+" ";

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setHeaderText("Please note important information below");

        customAlert.setSubText(""+Html.fromHtml(getString(R.string.androidQ)+ "<b>"+networkSSID+"</b>"+getString(R.string.androidQ2)));
        customAlert.setKeyName("","Got It");
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.cancel();

                apConfigAction();
            }
        });

    }


    private void buildAlertMessageNoGps() {

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText("Your GPS seems to be disabled, you need to enable it to use this service?");
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customAlert.cancel();
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, LOCATION_SETTINGS);
            }
        });
    }

    public boolean checkLocationStatus() {

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void showConnectedWifiSSID() {

        try {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager != null) {

                WifiManager.MulticastLock lock = mWifiManager.createMulticastLock("Log_Tag");
                lock.acquire();
            }
        } catch (Exception e) {
            Log.d("Wifi Exception", "" + e.getMessage());

        }

        if (mWifiManager != null) {
            mWifiManager.getWifiState();
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null) {

                String ssid = wifiInfo.getSSID();


                if (!TextUtils.isEmpty(ssid) && ssid.length() > 2
                        && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                String bssid = wifiInfo.getBSSID();
                editSSID.setText(ssid);
                editBSSID.setText(bssid);

            }
        }
    }

    void getAvailableSSID() {

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
        if (!loader.isShowing()) {
            loader.show_with_label("Connecting");
        }
    }

    private void hideSoftKeyBoard() {

        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        popup.dismiss();

        for (ScanResult result : scanResultsWifi) {

            if (result.SSID.equals(menuItem.getTitle())) {
                editSSID.setText(result.SSID);
                editBSSID.setText(result.BSSID);
                popup.dismiss();
                Log.d("@@Selected-Wifi : ", result.SSID + " (" + result.BSSID + ")");
                break;
            }
        }

        return true;
    }

    void ShowSSIDList() {

        popup = new PopupMenu(this, editSSID, Gravity.CENTER);
        popup.setOnMenuItemClickListener(this);
        Menu popupMenuItem = popup.getMenu();

        for (int i = 0; i < scanResultsWifi.size(); i++) {
            popupMenuItem.add(scanResultsWifi.get(i).SSID);
        }


        unregisterReceiver(wifiReceiver);
        popup.show();
    }

    private void showDialog(String meassge, boolean isShow) {
        if (isShow && !loader.isShowing()) {
            loader.show_with_label(meassge);
            return;
        }
    }

    private void autoConnect() {

        unregisterReceiver(wifiReceiver);
        final String scaleName = "WS915_V2.6_V1.5-";
        long scaleId = LoginShared.getUserMacId(this);
        String networkSSID = scaleName + scaleId;


        ScanResult scanResult = null;
        for (ScanResult result : scanResultsWifi) {
            if (result.SSID.endsWith("" + scaleId)) {
                scanResult = result;
                break;
            }
        }


        if (scanResult != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                btnConfigure.setEnabled(false);
                btnConfigure.setBackground(ContextCompat.getDrawable(ApConfigActivity.this, R.drawable.login_edit_rounded_corner_blue));

                NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                        .setSsid(scanResult.SSID)
                        .setBssid(MacAddress.fromString(scanResult.BSSID))
                        .build();
                final NetworkRequest request =
                        new NetworkRequest.Builder()
                                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                                .setNetworkSpecifier(specifier)
                                .build();
                connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        //ConnectivityManager.setProcessDefaultNetwork(network);
                        connectivityManager.bindProcessToNetwork(network);
                        changeAPConfig();

                    }

                    @Override
                    public void onLosing(@NonNull Network network, int maxMsToLive) {
                        super.onLosing(network, maxMsToLive);
                    }
                };
                if (connectivityManager != null)
                    connectivityManager.requestNetwork(request, networkCallback);
                else {
                    Toast.makeText(this, "Could not connect to wifi network with scale name " + networkSSID, Toast.LENGTH_LONG).show();
                }
            } else {

                // Old code
                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + networkSSID + "\"";
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();

                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {

                            new Handler().postDelayed(() -> {
                                if (loader.isShowing()) {
                                    loader.dismiss();
                                }

                                btnConfigure.setEnabled(false);
                                btnConfigure.setBackground(ContextCompat.getDrawable(ApConfigActivity.this, R.drawable.login_edit_rounded_corner_blue));
                                showApConfigCofirmationDialog(networkSSID);
                            },DELAY);
                        }else{

                            btnConfigure.setEnabled(false);
                            btnConfigure.setBackground(ContextCompat.getDrawable(ApConfigActivity.this, R.drawable.login_edit_rounded_corner_blue));
                            showApConfigCofirmationDialog(networkSSID);
                        }

                        break;
                    }
                }
            }
        } else {
            Toast.makeText(this, "Could not find wifi network with scale name " + networkSSID, Toast.LENGTH_LONG).show();
        }
    }

    private void changeAPConfig() {

        //showDialog("Configuring...", true);
        new Handler().postDelayed(() -> {

            String ssid = editSSID.getText().toString();
            String pwd = editPassword.getText().toString();
            scaleWiFiConfig.apConfig(ssid, pwd, ApConfigActivity.this);

        }, 1000);
    }

    private void showApConfigCofirmationDialog(String SSID) {

        String messageStr = getResources().getString(R.string.app_name_splash) + " Wants to Join Wi-Fi Network \"" + SSID + "\"";

        CustomAlert customAlert=new CustomAlert(this);
        customAlert.setSubText(messageStr);
        customAlert.setCancelVisible();
        customAlert.setKeyName("Cancel","Join");
        customAlert.show();

        customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customAlert.dismiss();
            }
        });

        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                customAlert.dismiss();
                changeAPConfig();
            }
        });
    }


    private void showalertdialog(boolean success) {

        CustomAlert customAlert = new CustomAlert(this);

        if (!success) {
            customAlert.setSubText(getString(R.string.Config_failed));
            customAlert.setCancelVisible();
            customAlert.setKeyName("Cancel","Try Again");

            customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlert.dismiss();

                    Intent intent=new Intent(ApConfigActivity.this, SetUpPreparation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            });

            customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(fromSettings){
                        Intent loginIntent = new Intent(ApConfigActivity.this, SettingsActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    }else if(fromLogin){
                        customAlert.dismiss();
                    }
                }
            });

        }else {

            if (getIntent().getBooleanExtra("wifi", false)) {

                customAlert.setSubText(getResources().getString(R.string.configuration));
                customAlert.setKeyName("","Step on the Scale");
                customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlert.dismiss();
                        //LoginShared.setstatusforwifivarification(ApConfigActivity.this, true);

                        if (!new InstructionSharedPreference(ApConfigActivity.this).isInstructionShown(ApConfigActivity.this, LoginShared.getRegistrationDataModel(ApConfigActivity.this).getData().getUser().get(0).getUserId())) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                                androidQRedirection(InstructionActivity.class);

                            }else {
                                Intent instruc = new Intent(ApConfigActivity.this, InstructionActivity.class);
                                startActivity(instruc);
                                finish();
                            }
                        } else {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                                androidQRedirection(DashBoardActivity.class);

                            }else {
                                Intent dashBoardIntent = new Intent(ApConfigActivity.this, DashBoardActivity.class);
                                startActivity(dashBoardIntent);
                                finishAffinity();
                            }
                        }
                    }
                });

            } else {

                customAlert.setSubText("Your AP Configuration completed successfully.");
                customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlert.dismiss();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            androidQRedirection(SettingsActivity.class);
                        }else {
                            Intent intent = new Intent(ApConfigActivity.this, SettingsActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });

            }
        }

        customAlert.show();
    }

    protected void androidQRedirection(Class x){

        Intent mStartActivity = new Intent(ApConfigActivity.this, x);
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ApConfigActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)ApConfigActivity.this.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 0, mPendingIntent);
        System.exit(0);

        finishAffinity();
    }

    @Override
    public void onApConfigResult(boolean success) {

        /*if (loader!=null &&loader.isShowing())
            loader.dismiss();*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        showalertdialog(success);
    }

    @Override
    public void onSmartLinkConfigResult(boolean success) {

    }

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {

                scanResultsWifi.clear();
                scanResultsWifi = mWifiManager.getScanResults();

            if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {

                if (loader.isShowing()) {
                    loader.dismiss();
                }
            }

            if (isAutoConnecting)
                autoConnect();
            else
                ShowSSIDList();


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PermissionHelper.PERMISSION_FINE_LOCATION) {
            if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
                if (!checkLocationStatus()) {

                    buildAlertMessageNoGps();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == LOCATION_SETTINGS && checkLocationStatus()) {

            showConnectedWifiSSID();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
