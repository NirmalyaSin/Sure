package com.surefiz.screens.wificonfig;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.sharedhandler.LoginShared;

import java.util.ArrayList;
import java.util.List;

import cn.onecoder.scalewifi.api.ScaleWiFiConfig;
import cn.onecoder.scalewifi.api.impl.OnScaleWiFiConfigResultListener;

public class WifiActivityClickEvent implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, OnScaleWiFiConfigResultListener {
    private WifiConfigActivity mWifiConfigActivity;
    private PermissionHelper permissionHelper;
    private WifiManager mWifiManager;
    private PopupMenu popup;
    private List<ScanResult> scanResultsWifi = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ScaleWiFiConfig scaleWiFiConfig;
    WifiReceiver wifiReceiver = new WifiReceiver();


    public WifiActivityClickEvent(WifiConfigActivity activity) {
        this.mWifiConfigActivity = activity;
        setClickEvent();
        permissionHelper = new PermissionHelper(mWifiConfigActivity);
        scaleWiFiConfig = new ScaleWiFiConfig();
    }


    private void setClickEvent() {
        mWifiConfigActivity.btnConfigure.setOnClickListener(this);
        mWifiConfigActivity.iv_showPassword.setOnClickListener(this);
        mWifiConfigActivity.iv_hidePassword.setOnClickListener(this);
        mWifiConfigActivity.editSSID.setOnClickListener(this);
        mWifiConfigActivity.btn_skip_config.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editSSID:
                hideSoftKeyBoard();
                if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
                    //Scan for available wifi list
                    //  mWifiConfigActivity.unregisterReceiver(wifiReceiver);
                    getAvailableSSID();
                } else {
                    permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
                }
                break;

            case R.id.btnConfigure:
                wificonfigblankvalidation();
                break;

            case R.id.iv_showPassword:
                mWifiConfigActivity.iv_showPassword.setVisibility(View.GONE);
                mWifiConfigActivity.iv_hidePassword.setVisibility(View.VISIBLE);
                mWifiConfigActivity.editPassword.setTransformationMethod
                        (PasswordTransformationMethod.getInstance());
                break;
            case R.id.iv_hidePassword:
                mWifiConfigActivity.iv_showPassword.setVisibility(View.VISIBLE);
                mWifiConfigActivity.iv_hidePassword.setVisibility(View.GONE);
                mWifiConfigActivity.editPassword.setTransformationMethod
                        (HideReturnsTransformationMethod.getInstance());
                break;
            case R.id.btn_skip_config:
                Intent details = new Intent(mWifiConfigActivity, InstructionActivity.class);
                mWifiConfigActivity.startActivity(details);
                mWifiConfigActivity.finish();
                break;

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void wificonfigblankvalidation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mWifiConfigActivity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED &&
                    mWifiConfigActivity.checkSelfPermission(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE) == PackageManager.PERMISSION_GRANTED &&
                    mWifiConfigActivity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                String ssid = mWifiConfigActivity.editSSID.getText().toString();
                String pwd = mWifiConfigActivity.editPassword.getText().toString();
                String bssid = mWifiConfigActivity.editBSSID.getText().toString();

                if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(mWifiConfigActivity, "Plz input ssid and pwd.", Toast.LENGTH_SHORT).show();
                } else {
                    mWifiConfigActivity.unregisterReceiver(wifiReceiver);
                    progressDialog.setMessage("Please wait ");
                    progressDialog.show();
                    Log.d("@@Sent-SmartLink : ", ssid + "\n" + pwd + "\n" + bssid);
                    //  scaleWiFiConfig.apConfig(ssid, pwd, this);
                    scaleWiFiConfig.smartLinkConfig(mWifiConfigActivity, ssid, bssid, pwd, this);
                }
            } else {

                mWifiConfigActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            String ssid = mWifiConfigActivity.editSSID.getText().toString();
            String pwd = mWifiConfigActivity.editPassword.getText().toString();
            String bssid = mWifiConfigActivity.editBSSID.getText().toString();
            if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(mWifiConfigActivity, "Plz input ssid and pwd.", Toast.LENGTH_SHORT).show();
            } else {
                mWifiConfigActivity.unregisterReceiver(wifiReceiver);
                progressDialog.setMessage("Please wait ");
                progressDialog.show();
                //  scaleWiFiConfig.apConfig(ssid, pwd, this);
                scaleWiFiConfig.smartLinkConfig(mWifiConfigActivity, ssid, bssid, pwd, this);
            }
        }
    }

    private void hideSoftKeyBoard() {
        View view = mWifiConfigActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mWifiConfigActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getAvailableSSID() {
        mWifiManager = (WifiManager) mWifiConfigActivity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        mWifiConfigActivity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();
        progressDialog = new ProgressDialog(mWifiConfigActivity);
        progressDialog.setMessage("Finding Available WiFi-Network");
        progressDialog.show();
    }

    @Override
    public void onApConfigResult(boolean success) {
        progressDialog.dismiss();
        if (success)
            Toast.makeText(mWifiConfigActivity, "wificonfig done", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mWifiConfigActivity, "wificonfig  not done", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onSmartLinkConfigResult(boolean sucess) {
        progressDialog.dismiss();
        if (sucess)
            showalertdialog();
            // Toast.makeText(mWifiConfigActivity, "wificonfig done", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mWifiConfigActivity, "wifi configruation  not done", Toast.LENGTH_LONG).show();


    }

    private void showalertdialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(mWifiConfigActivity).create();
        alertDialog.setTitle(mWifiConfigActivity.getResources().getString(R.string.app_name));
        alertDialog.setMessage(mWifiConfigActivity.getResources().getString(R.string.configrution));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Update Weight",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginShared.setstatusforwifivarification(true);
                        Intent instruc = new Intent(mWifiConfigActivity, InstructionActivity.class);
                        mWifiConfigActivity.startActivity(instruc);
                        mWifiConfigActivity.finish();
                    }
                });
        alertDialog.show();
    }


    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            scanResultsWifi.clear();
            Toast.makeText(mWifiConfigActivity, "Wifi List Received", Toast.LENGTH_LONG).show();
            scanResultsWifi = mWifiManager.getScanResults();
            progressDialog.dismiss();
            //Show popup menu
            ShowSSIDList();
        }

    }

    public void ShowSSIDList() {
        popup = new PopupMenu(mWifiConfigActivity, mWifiConfigActivity.editSSID, Gravity.CENTER);
        popup.setOnMenuItemClickListener(this);
        Menu popupMenuItem = popup.getMenu();

        for (int i = 0; i < scanResultsWifi.size(); i++) {
            popupMenuItem.add(scanResultsWifi.get(i).SSID);
        }

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        popup.dismiss();

        for (ScanResult result : scanResultsWifi) {
            if (result.SSID.equals(item.getTitle())) {
                mWifiConfigActivity.editSSID.setText(result.SSID);
                mWifiConfigActivity.editBSSID.setText(result.BSSID);
                popup.dismiss();
                Log.d("@@Selected-Wifi : ", result.SSID + " (" + result.BSSID + ")");
                break;
            }
        }

        return true;
    }


}
