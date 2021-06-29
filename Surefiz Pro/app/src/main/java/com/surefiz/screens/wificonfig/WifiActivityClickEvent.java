package com.surefiz.screens.wificonfig;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
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
import com.surefiz.dialog.CustomAlert;
import com.surefiz.helpers.PermissionHelper;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.screens.apconfig.ApConfigActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.screens.setupPreparation.SendWifiLog;
import com.surefiz.screens.setupPreparation.SetUpPreparation;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import cn.onecoder.scalewifi.api.ScaleWiFiConfig;
import cn.onecoder.scalewifi.api.impl.OnScaleWiFiConfigResultListener;

public class WifiActivityClickEvent implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, OnScaleWiFiConfigResultListener {
    WifiReceiver wifiReceiver = new WifiReceiver();
    LoadingData loader;
    boolean isWifiReceived = false;
    private WifiConfigActivity mWifiConfigActivity;
    private PermissionHelper permissionHelper;
    private WifiManager mWifiManager;
    private PopupMenu popup;
    private List<ScanResult> scanResultsWifi = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ScaleWiFiConfig scaleWiFiConfig;


    public WifiActivityClickEvent(WifiConfigActivity activity) {
        this.mWifiConfigActivity = activity;
        setClickEvent();
        permissionHelper = new PermissionHelper(mWifiConfigActivity);
        scaleWiFiConfig = new ScaleWiFiConfig();
        loader = new LoadingData(activity);

        if (permissionHelper.checkPermission(PermissionHelper.PERMISSION_FINE_LOCATION)) {
            showConnectedWifiSSID();
        } else {
            permissionHelper.requestForPermission(PermissionHelper.PERMISSION_FINE_LOCATION);
        }

    }

    public void showConnectedWifiSSID() {


        try {
            mWifiManager = (WifiManager) mWifiConfigActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager != null) {
                WifiManager.MulticastLock lock = mWifiManager.createMulticastLock("Log_Tag");
                lock.acquire();
            }
          /*
           if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                if (lock == null)
                    lock = mWifiManager.createMulticastLock("WiFi_Lock");
                lock.setReferenceCounted(true);
                lock.acquire();
            }*/
        } catch (Exception e) {
            Log.d("Wifi Exception", "" + e.getMessage());
        }


      /*  mWifiManager = (WifiManager) mWifiConfigActivity.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);*/
        if (mWifiManager != null) {
            mWifiManager.getWifiState();
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                //String ssid="";
                //System.out.println("SSID: "+wifiInfo.getSSID());
                String ssid;
                if (!wifiInfo.getSSID().contains("unknown ssid")) {
                    ssid = wifiInfo.getSSID();
                } else {
                    ssid = "";
                }
                if (!TextUtils.isEmpty(ssid) && ssid.length() > 2
                        && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    ssid = ssid.substring(1, ssid.length() - 1);
                }
                String bssid = wifiInfo.getBSSID();
                mWifiConfigActivity.editSSID.setText(ssid);
                mWifiConfigActivity.editBSSID.setText(bssid);
            }
        }
    }


    private void setClickEvent() {
        mWifiConfigActivity.btnConfigure.setOnClickListener(this);
        mWifiConfigActivity.iv_showPassword.setOnClickListener(this);
        mWifiConfigActivity.iv_hidePassword.setOnClickListener(this);
        mWifiConfigActivity.editSSID.setOnClickListener(this);
        mWifiConfigActivity.btn_skip_config.setOnClickListener(this);
        mWifiConfigActivity.ivPlayPauseOnlyWifi.setOnClickListener(this);
        mWifiConfigActivity.btnYes.setOnClickListener(this);
        mWifiConfigActivity.btnNo.setOnClickListener(this);
        //mWifiConfigActivity.editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
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
                hideSoftKeyBoard();
                String ssid = mWifiConfigActivity.editSSID.getText().toString();
                String pwd = mWifiConfigActivity.editPassword.getText().toString();
                String bssid = mWifiConfigActivity.editBSSID.getText().toString();

                if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                    MethodUtils.errorMsg(mWifiConfigActivity, "Please enter your WiFi Password.");
                } else {
                    new SendWifiLog("6", mWifiConfigActivity, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if (intent.getBooleanExtra("success", false)) {
                                next();
                            }
                        }
                    });
                }
                break;
            case R.id.iv_showPassword:
                mWifiConfigActivity.iv_showPassword.setVisibility(View.GONE);
                mWifiConfigActivity.iv_hidePassword.setVisibility(View.VISIBLE);
                mWifiConfigActivity.editPassword.setTransformationMethod
                        (HideReturnsTransformationMethod.getInstance());

                break;
            case R.id.iv_hidePassword:
                mWifiConfigActivity.iv_showPassword.setVisibility(View.VISIBLE);
                mWifiConfigActivity.iv_hidePassword.setVisibility(View.GONE);
                mWifiConfigActivity.editPassword.setTransformationMethod
                        (PasswordTransformationMethod.getInstance());
                break;
            case R.id.btn_skip_config:

                //System.out.println("instructionShown: " + new InstructionSharedPreference(mWifiConfigActivity).getInstructionVisibility(mWifiConfigActivity));

                if (!new InstructionSharedPreference(mWifiConfigActivity).isInstructionShown(mWifiConfigActivity, LoginShared.getRegistrationDataModel(mWifiConfigActivity).getData().getUser().get(0).getUserId())) {
                    Intent details = new Intent(mWifiConfigActivity, InstructionActivity.class);
                    mWifiConfigActivity.startActivity(details);
                    mWifiConfigActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    mWifiConfigActivity.finish();
                    //LoginShared.setstatusforwifivarification(mWifiConfigActivity, true);
                    break;
                } else {
                    Intent details = new Intent(mWifiConfigActivity, DashBoardActivity.class);
                    mWifiConfigActivity.startActivity(details);
                    mWifiConfigActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    mWifiConfigActivity.finish();
                    //LoginShared.setstatusforwifivarification(mWifiConfigActivity, true);
                }

            case R.id.ivPlayPauseOnlyWifi:

                if (!mWifiConfigActivity.isOnlyWifiVideoPlayed) {
                    mWifiConfigActivity.video_view_only_oneWifi.start();
                    mWifiConfigActivity.ivPlayPauseOnlyWifi.setImageResource(R.drawable.pause);
                    mWifiConfigActivity.ivPlayPauseOnlyWifi.setVisibility(View.GONE);
                    mWifiConfigActivity.btnYes.setEnabled(false);
                    mWifiConfigActivity.btnYes.setBackground(mWifiConfigActivity.getResources().getDrawable(R.drawable.button_disable_background));
                    mWifiConfigActivity.btnNo.setEnabled(false);
                    mWifiConfigActivity.btnNo.setBackground(mWifiConfigActivity.getResources().getDrawable(R.drawable.button_disable_background));
                    mWifiConfigActivity.isOnlyWifiVideoPlayed = true;
                } else {
                    mWifiConfigActivity.video_view_only_oneWifi.pause();
                    mWifiConfigActivity.ivPlayPauseOnlyWifi.setImageResource(R.drawable.play);
                    mWifiConfigActivity.isOnlyWifiVideoPlayed = false;
                    mWifiConfigActivity.btnYes.setBackground(mWifiConfigActivity.getResources().getDrawable(R.drawable.login_button_gradient));
                    mWifiConfigActivity.btnYes.setEnabled(true);
                    mWifiConfigActivity.btnNo.setBackground(mWifiConfigActivity.getResources().getDrawable(R.drawable.login_button_gradient));
                    mWifiConfigActivity.btnNo.setEnabled(true);

                    /*if (mWifiConfigActivity.videoNegativeCount == 0) {
                        mWifiConfigActivity.callDialog();
                    } else {
                        mWifiConfigActivity.btnYes.setBackground(mWifiConfigActivity.getResources().getDrawable(R.drawable.login_button_gradient));
                        mWifiConfigActivity.btnYes.setEnabled(true);
                    }*/
                }
                break;


            case R.id.btnYes:

                if (mWifiConfigActivity.videoNegativeCount == 0) {
                    new SendWifiLog("9", mWifiConfigActivity, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if (intent.getBooleanExtra("success", false)) {
                                wificonfigblankvalidation();
                            }
                        }
                    });
                } else {
                    new SendWifiLog("8", mWifiConfigActivity, new OnUiEventClick() {
                        @Override
                        public void onUiClick(Intent intent, int eventCode) {
                            if (intent.getBooleanExtra("success", false)) {
                                wificonfigblankvalidation();
                            }
                        }
                    });
                }
                break;

            case R.id.btnNo:

                new SendWifiLog("7", mWifiConfigActivity, new OnUiEventClick() {
                    @Override
                    public void onUiClick(Intent intent, int eventCode) {
                        if (intent.getBooleanExtra("success", false)) {
                            mWifiConfigActivity.video_view_only_oneWifi.stopPlayback();
                            mWifiConfigActivity.videoNegativeCount++;
                            mWifiConfigActivity.startNewVideo();
                        }
                    }
                });


                break;

        }
    }

    private void next() {
        mWifiConfigActivity.sv_main.setVisibility(View.GONE);
        mWifiConfigActivity.setUpSmartWifiView();
    }

    private void buildAlertMessageNoGps() {

        CustomAlert customAlert = new CustomAlert(mWifiConfigActivity);
        customAlert.setSubText("Your GPS seems to be disabled, you need to enable it to use this service?");
        customAlert.setKeyName("", "Yes");
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.cancel();
                mWifiConfigActivity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    public boolean checkLocationStatus() {
        final LocationManager manager = (LocationManager) mWifiConfigActivity.getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void wificonfigblankvalidation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mWifiConfigActivity.checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE)
                    == PackageManager.PERMISSION_GRANTED &&
                    mWifiConfigActivity.checkSelfPermission(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE)
                            == PackageManager.PERMISSION_GRANTED) {

                String ssid = mWifiConfigActivity.editSSID.getText().toString();
                String pwd = mWifiConfigActivity.editPassword.getText().toString();
                String bssid = mWifiConfigActivity.editBSSID.getText().toString();

                if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                    MethodUtils.errorMsg(mWifiConfigActivity, "Please enter your WiFi Password.");
                } else {
                    //  mWifiConfigActivity.unregisterReceiver(wifiReceiver);
                    if (!loader.isShowing()) {
                        loader.show();
                    }
                    Log.d("@@Sent-SmartLink : ", ssid + "\n" + pwd + "\n" + bssid);
                    //  scaleWiFiConfig.apConfig(ssid, pwd, this);
                    scaleWiFiConfig.smartLinkConfig(mWifiConfigActivity, ssid, bssid, pwd, this);
                }
            } else {

                mWifiConfigActivity.requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_MULTICAST_STATE}, 0);
            }
        } else {
            String ssid = mWifiConfigActivity.editSSID.getText().toString();
            String pwd = mWifiConfigActivity.editPassword.getText().toString();
            String bssid = mWifiConfigActivity.editBSSID.getText().toString();
            if (TextUtils.isEmpty(ssid) || TextUtils.isEmpty(pwd)) {
                Toast.makeText(mWifiConfigActivity, "Plz input ssid and pwd.", Toast.LENGTH_SHORT).show();
            } else {
                /*progressDialog.setMessage("Please wait ");
                progressDialog.show();*/
                if (!loader.isShowing()) {
                    loader.show();
                }
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

        mWifiConfigActivity.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();
        /*progressDialog = new ProgressDialog(mWifiConfigActivity);
        progressDialog.setMessage("Finding Available WiFi-Network");
        progressDialog.show();*/
        if (!loader.isShowing()) {
            loader.show();
        }
    }

    @Override
    public void onApConfigResult(boolean success) {
        if (loader.isShowing()) {
            loader.dismiss();
        }
        new SendWifiLog(mWifiConfigActivity.editSSID.getText().toString() + "," + mWifiConfigActivity.editPassword.getText().toString(), mWifiConfigActivity, new OnUiEventClick() {
            @Override
            public void onUiClick(Intent intent, int eventCode) {
            }
        });
        if (success)
            Toast.makeText(mWifiConfigActivity, "Wifi Config done", Toast.LENGTH_LONG).show();
        else {

            CustomAlert customAlert = new CustomAlert(mWifiConfigActivity);
            customAlert.setSubText(mWifiConfigActivity.getString(R.string.smart_config_failed));
            customAlert.setCancelVisible();
            customAlert.setKeyName("Cancel", "AP Config");
            customAlert.show();

            customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlert.dismiss();
                    //LoginShared.setstatusforwifivarification(mWifiConfigActivity, false);
                    Intent instruc = new Intent(mWifiConfigActivity, ApConfigActivity.class);
                    instruc.putExtra("wifi", true);
                    mWifiConfigActivity.startActivity(instruc);
                }
            });

            customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlert.dismiss();
                }
            });
        }
    }


    @Override
    public void onSmartLinkConfigResult(boolean sucess) {
        if (loader.isShowing()) {
            loader.dismiss();
        }
        new SendWifiLog(mWifiConfigActivity.editSSID.getText().toString() + "," + mWifiConfigActivity.editPassword.getText().toString(), mWifiConfigActivity, new OnUiEventClick() {
            @Override
            public void onUiClick(Intent intent, int eventCode) {
            }
        });
        if (sucess) {
            if (mWifiConfigActivity.fromSettings) {
                showalertdialog(true);
            } else if (mWifiConfigActivity.fromLogin) {
                showalertdialog(false);
            }
            // Toast.makeText(mWifiConfigActivity, "wificonfig done", Toast.LENGTH_LONG).show();
        } else {

            CustomAlert customAlert = new CustomAlert(mWifiConfigActivity);
            customAlert.setSubText(mWifiConfigActivity.getString(R.string.Config_failed));
            customAlert.setCancelVisible();
            customAlert.setKeyName("Cancel", "Try Again");
            customAlert.show();

            customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customAlert.dismiss();

                    //wificonfigblankvalidation();

                    Intent intent = new Intent(mWifiConfigActivity, SetUpPreparation.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mWifiConfigActivity.startActivity(intent);
                    mWifiConfigActivity.finish();
                }
            });

            customAlert.btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mWifiConfigActivity.fromSettings) {
                        Intent loginIntent = new Intent(mWifiConfigActivity, SettingsActivity.class);
                        mWifiConfigActivity.startActivity(loginIntent);
                        mWifiConfigActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        mWifiConfigActivity.finishAffinity();
                    } else if (mWifiConfigActivity.fromLogin) {
                        customAlert.dismiss();
                    }
                }
            });
        }
    }

    private void showalertdialog(boolean isFromSettingPage) {

        CustomAlert customAlert = new CustomAlert(mWifiConfigActivity);
        customAlert.setSubText(mWifiConfigActivity.getString(R.string.configuration));
        if (!isFromSettingPage) {
            customAlert.setKeyName("", "Next");
        }
        customAlert.show();
        customAlert.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlert.dismiss();
                if (isFromSettingPage) {

                    Intent intent = new Intent(mWifiConfigActivity, SettingsActivity.class);
                    mWifiConfigActivity.startActivity(intent);
                    mWifiConfigActivity.finishAffinity();

                } else {
                    //LoginShared.setstatusforwifivarification(mWifiConfigActivity, true);
                    if (!new InstructionSharedPreference(mWifiConfigActivity).isInstructionShown(mWifiConfigActivity, LoginShared.getRegistrationDataModel(mWifiConfigActivity).getData().getUser().get(0).getUserId())) {
                        Intent instruc = new Intent(mWifiConfigActivity, InstructionActivity.class);
                        mWifiConfigActivity.startActivity(instruc);
                        mWifiConfigActivity.finishAffinity();
                    } else {
                        Intent dashboardIntent = new Intent(mWifiConfigActivity, DashBoardActivity.class);
                        mWifiConfigActivity.startActivity(dashboardIntent);
                        mWifiConfigActivity.finishAffinity();

                    }
                }
            }
        });

    }


    public void ShowSSIDList() {
        popup = new PopupMenu(mWifiConfigActivity, mWifiConfigActivity.editSSID, Gravity.CENTER);
        popup.setOnMenuItemClickListener(this);
        Menu popupMenuItem = popup.getMenu();

        for (int i = 0; i < scanResultsWifi.size(); i++) {
            popupMenuItem.add(scanResultsWifi.get(i).SSID);
        }

        mWifiConfigActivity.unregisterReceiver(wifiReceiver);
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

    class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            scanResultsWifi.clear();
//            Toast.makeText(mWifiConfigActivity, "Wifi List Received", Toast.LENGTH_LONG).show();
            scanResultsWifi = mWifiManager.getScanResults();
            if (loader.isShowing()) {
                loader.dismiss();
            }
            //Show popup menu
            ShowSSIDList();
        }

    }


}
