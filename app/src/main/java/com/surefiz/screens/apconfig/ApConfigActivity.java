package com.surefiz.screens.apconfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Bundle;
import android.os.Handler;
import android.os.PatternMatcher;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.onecoder.scalewifi.api.ScaleWiFiConfig;
import cn.onecoder.scalewifi.api.impl.OnScaleWiFiConfigResultListener;

public class ApConfigActivity extends BaseActivity implements View.OnClickListener,
        OnScaleWiFiConfigResultListener,
        PopupMenu.OnMenuItemClickListener {
    public View view;
    LoadingData loader;
    WifiReceiver wifiReceiver = new WifiReceiver();
    private EditText editSSID, editBSSID, editPassword;
    private Button btnlockwifi, btnConfigure;
    private PopupMenu popup;
    private WifiManager mWifiManager;
    private List<ScanResult> scanResultsWifi = new ArrayList<>();
    private ScaleWiFiConfig scaleWiFiConfig;
    private boolean isAutoConnecting = false;
    private ImageView iv_showPassword;
    private ImageView iv_hidePassword;
    private static int LOCATION_SETTINGS = 1000;
    private PermissionHelper permissionHelper;
    private ConnectivityManager.NetworkCallback networkCallback;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_ap_config, null);


        addContentView(view);
        ButterKnife.bind(view);
        permissionHelper = new PermissionHelper(this);
        loader = new LoadingData(this);
        scaleWiFiConfig = new ScaleWiFiConfig();
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
        editSSID = view.findViewById(R.id.editSSID);
        editBSSID = view.findViewById(R.id.editBSSID);
        editPassword = view.findViewById(R.id.editPassword);
        btnlockwifi = view.findViewById(R.id.btnlockwifi);
        btnConfigure = view.findViewById(R.id.btnConfigure);
        iv_showPassword = view.findViewById(R.id.iv_showPassword);
        iv_hidePassword = view.findViewById(R.id.iv_hidePassword);
        //editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        btnConfigure.setVisibility(View.INVISIBLE);
        btnlockwifi.setOnClickListener(this);
        btnConfigure.setOnClickListener(this);
        editSSID.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        iv_showPassword.setOnClickListener(this);
        iv_hidePassword.setOnClickListener(this);
        setHeaderView();
    }

    private void setHeaderView() {

        tv_universal_header.setText("AP Configuration");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                onBackPressed();
                break;
            case R.id.btnConfigure:

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                    //if(!new InstructionSharedPreference(ApConfigActivity.this).getAnroidQ())
                        androidQ();
                }else {
                    apConfigAction();
                }

                break;
            case R.id.btnlockwifi:


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


                break;
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
            Toast.makeText(this, "Please input SSID and password.", Toast.LENGTH_SHORT).show();
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

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please note important information below");
        builder.setMessage(Html.fromHtml(getString(R.string.androidQ)+ "<b>"+networkSSID+"</b>"+getString(R.string.androidQ2)))
                .setCancelable(false)
                .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        apConfigAction();

                    }
                });
        final AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, you need to enable it to use this service?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, LOCATION_SETTINGS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
            loader.show_with_label("Loading");
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
                        btnConfigure.setEnabled(false);
                        btnConfigure.setBackground(ContextCompat.getDrawable(ApConfigActivity.this, R.drawable.login_edit_rounded_corner_blue));
                        showApConfigCofirmationDialog(networkSSID);

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

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name_splash));

        String messageStr = getResources().getString(R.string.app_name_splash) + " Wants to Join Wi-Fi Network \"" + SSID + "\"";

        alertDialog.setMessage(messageStr);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Join",
                (dialog, which) -> {

                    dialog.dismiss();
                    changeAPConfig();
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                (dialog, which) -> {
                    dialog.dismiss();

                    /*btnlockwifi.setEnabled(false);
                    btnConfigure.setEnabled(true);
                    btnlockwifi.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edit_rounded_corner_blue));
                    btnConfigure.setBackground(ContextCompat.getDrawable(this, R.drawable.login_button_gradient));*/
                });
        alertDialog.show();
    }


    private void showalertdialog(boolean success) {


        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name_splash));
        if (!success) {

            alertDialog.setMessage("Your Configuration failed to complete.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
        } else {
            if (getIntent().getBooleanExtra("wifi", false)) {
                alertDialog.setMessage(getResources().getString(R.string.configrution));
                //alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Update Weight",
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Step on Scale",
                        (dialog, which) -> {
                            dialog.dismiss();
                            LoginShared.setstatusforwifivarification(this, true);

                            if (!new InstructionSharedPreference(ApConfigActivity.this).isInstructionShown(ApConfigActivity.this, LoginShared.getRegistrationDataModel(ApConfigActivity.this).getData().getUser().get(0).getUserId())) {
                                Intent instruc = new Intent(this, InstructionActivity.class);
                                startActivity(instruc);
                                finish();
                            } else {
                                Intent dashBoardIntent = new Intent(this, DashBoardActivity.class);
                                startActivity(dashBoardIntent);
                                finish();
                            }
                        });
            } else {

                alertDialog.setMessage("Your AP Configuration completed successfully.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> {
                            //new InstructionSharedPreference(ApConfigActivity.this).setAnroidQ(true);
                            dialog.dismiss();
                            finish();
                        });
            }
        }
        alertDialog.show();
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

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        showalertdialog(success);*/
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {

                scanResultsWifi.clear();
                scanResultsWifi = mWifiManager.getScanResults();
                if (loader.isShowing()) {
                    loader.dismiss();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
