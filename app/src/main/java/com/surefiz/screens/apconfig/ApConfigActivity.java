package com.surefiz.screens.apconfig;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.onecoder.scalewifi.api.ScaleWiFiConfig;
import cn.onecoder.scalewifi.api.impl.OnScaleWiFiConfigResultListener;

public class ApConfigActivity extends BaseActivity implements View.OnClickListener, OnScaleWiFiConfigResultListener,
        PopupMenu.OnMenuItemClickListener {
    private EditText editSSID, editBSSID, editPassword;
    private Button btnlockwifi, btnConfigure;
    public View view;
    private PopupMenu popup;
    LoadingData loader;
    private WifiManager mWifiManager;
    WifiReceiver wifiReceiver = new WifiReceiver();
    private List<ScanResult> scanResultsWifi = new ArrayList<>();
    private ScaleWiFiConfig scaleWiFiConfig;
    private boolean isAutoConnecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_ap_config, null);
        addContentView(view);
        ButterKnife.bind(view);
        loader = new LoadingData(this);
        scaleWiFiConfig = new ScaleWiFiConfig();
        initializeView();
        showConnectedWifiSSID();

    }

    private void initializeView() {
        editSSID = view.findViewById(R.id.editSSID);
        editBSSID = view.findViewById(R.id.editBSSID);
        editPassword = view.findViewById(R.id.editPassword);
        btnlockwifi = view.findViewById(R.id.btnlockwifi);
        btnConfigure = view.findViewById(R.id.btnConfigure);
        editPassword.setTransformationMethod
                (HideReturnsTransformationMethod.getInstance());
        btnlockwifi.setOnClickListener(this);
        btnConfigure.setOnClickListener(this);
        editSSID.setOnClickListener(this);
        rl_back.setOnClickListener(this);
        setHeaderView();
    }

    private void setHeaderView() {
        tv_universal_header.setText("AP Configuration");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
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
                break;
            case R.id.btnlockwifi:
                ssid = editSSID.getText().toString();
                pwd = editPassword.getText().toString();
                if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(this, "Please input SSID and password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                editSSID.setEnabled(false);
                editPassword.setEnabled(false);
                btnlockwifi.setEnabled(false);
                btnConfigure.setEnabled(true);
                btnlockwifi.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edit_rounded_corner_blue));
                btnConfigure.setBackground(ContextCompat.getDrawable(this, R.drawable.login_submit_rounded_corner));
                break;
            case R.id.editSSID:
                hideSoftKeyBoard();
                PermissionHelper permissionHelper = new PermissionHelper(this);
                if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
                    isAutoConnecting = false;
                    hideSoftKeyBoard();
                    getAvailableSSID();
                } else {
                    permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
                }
                break;
        }
    }

    private void showConnectedWifiSSID() {
        try {
            mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager != null) {
                WifiManager.MulticastLock lock = mWifiManager.createMulticastLock("Log_Tag");
                lock.acquire();
            }
        } catch (Exception e) {
            Log.d("Wifi Exception", "" + e.getMessage().toString());
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

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            scanResultsWifi.clear();
//            Toast.makeText(mWifiConfigActivity, "Wifi List Received", Toast.LENGTH_LONG).show();
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
        if (loader.isShowing()) {
            loader.dismiss();
        }
    }

    private void autoConnect() {
        unregisterReceiver(wifiReceiver);
        final String scaleName = "WS915_V2.6_V1.5-";
        int scaleId = LoginShared.getScaleUserId(this);
        String networkSSID = scaleName + scaleId;
        ScanResult scanResult = null;
        for (ScanResult result : scanResultsWifi) {
            if (result.SSID.equals(networkSSID)) {
                scanResult = result;
                break;
            }
        }
        if (scanResult != null) {
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
                    btnConfigure.setBackground(ContextCompat.getDrawable(this, R.drawable.login_edit_rounded_corner_blue));
                    changeAPConfig();
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Could not find wifi network with scale name " + networkSSID, Toast.LENGTH_LONG).show();
        }
    }

    private void changeAPConfig() {
        new Handler().postDelayed(() -> {
            String ssid = editSSID.getText().toString();
            String pwd = editPassword.getText().toString();
            scaleWiFiConfig.apConfig(ssid, pwd, ApConfigActivity.this);
            showDialog("Configuring...", true);
        }, 3000);
    }

    private void showalertdialog(boolean success) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.app_name));
        if (!success) {
            alertDialog.setMessage("Your Configuration failed to complete.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> {
                        dialog.dismiss();
                    });
        } else {
            if (getIntent().getBooleanExtra("wifi", false)) {
                alertDialog.setMessage(getResources().getString(R.string.configrution));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Update Weight",
                        (dialog, which) -> {
                            dialog.dismiss();
                            LoginShared.setstatusforwifivarification(this, true);
                            Intent instruc = new Intent(this, InstructionActivity.class);
                            startActivity(instruc);
                            finish();
                        });
            } else {
                alertDialog.setMessage("Your AP Configuration completed successfully.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        });
            }
        }
        alertDialog.show();
    }

    @Override
    public void onApConfigResult(boolean success) {
        showalertdialog(success);
        if (loader.isShowing())
            loader.dismiss();
    }

    @Override
    public void onSmartLinkConfigResult(boolean success) {
        showalertdialog(success);
        if (loader.isShowing())
            loader.dismiss();
    }
}
