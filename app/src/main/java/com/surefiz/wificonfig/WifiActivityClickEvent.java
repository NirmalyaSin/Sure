package com.surefiz.wificonfig;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.registration.RegistrationActivity;

import java.util.ArrayList;
import java.util.List;

public class WifiActivityClickEvent implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private WifiConfigActivity mWifiConfigActivity;
    private PermissionHelper permissionHelper;
    private WifiManager mWifiManager;
    private PopupMenu popup;
    private List<ScanResult> scanResultsWifi = new ArrayList<>();
    private ProgressDialog progressDialog;

    public WifiActivityClickEvent(WifiConfigActivity activity) {
        this.mWifiConfigActivity = activity;
        setClickEvent();
        permissionHelper = new PermissionHelper(mWifiConfigActivity);
    }


    private void setClickEvent() {
        mWifiConfigActivity.btnConfigure.setOnClickListener(this);
        mWifiConfigActivity.iv_showPassword.setOnClickListener(this);
        mWifiConfigActivity.iv_hidePassword.setOnClickListener(this);
        mWifiConfigActivity.editSSID.setOnClickListener(this);
        mWifiConfigActivity.tv_skip.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.editSSID:
                hideSoftKeyBoard();
                if(permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)){
                    //Scan for available wifi list
                    getAvailableSSID();
                }else {
                    permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
                }


                break;

            case R.id.btnConfigure:
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

            case R.id.tv_skip:
           //     mWifiConfigActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

        }
    }

    private void hideSoftKeyBoard() {
        View view = mWifiConfigActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mWifiConfigActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void getAvailableSSID(){
        mWifiManager = (WifiManager) mWifiConfigActivity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        mWifiConfigActivity.registerReceiver(new WifiReceiver(),
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();

        progressDialog = new ProgressDialog(mWifiConfigActivity);
        progressDialog.setMessage("Finding Available WiFi-Network");
        progressDialog.show();
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

    public void ShowSSIDList(){
        popup = new PopupMenu(mWifiConfigActivity, mWifiConfigActivity.editSSID);
        popup.setOnMenuItemClickListener(this);
        Menu popupMenuItem = popup.getMenu();


        for (int i = 0; i < scanResultsWifi.size(); i++){
            popupMenuItem.add(scanResultsWifi.get(i).SSID);
        }

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        mWifiConfigActivity.editSSID.setText(item.getTitle());
        popup.dismiss();

        for(ScanResult result:scanResultsWifi){
            if(result.SSID.equals(item.getTitle())){
                mWifiConfigActivity.editBSSID.setText(result.BSSID);
                Log.d("Selected-Wifi : ", result.SSID+ " ("+result.BSSID+")");
                break;
            }
        }

        return true;
    }

}
