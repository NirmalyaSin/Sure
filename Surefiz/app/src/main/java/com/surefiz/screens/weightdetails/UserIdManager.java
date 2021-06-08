package com.surefiz.screens.weightdetails;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.surefiz.screens.UDPHelper;

import cn.onecoder.scalewifi.api.impl.OnUserIdManagerListener;
import cn.onecoder.scalewifi.net.impl.OnDataListener;
import cn.onecoder.scalewifi.util.HexUtil;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class UserIdManager implements OnDataListener {
    public static final long TIMEOUT_IN_MILLIS_MIN = 300000L;
    private long timeoutInMillis = 300000L;
    private long retryPeriodInMillis = 500L;
    private static final String TAG = cn.onecoder.scalewifi.api.UserIdManager.class.getSimpleName();
    private static final int PORT = 61111;
    private static final String KEY_DATA_ID = "ID";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_USER_ID = "UserID";
    private static final String KEY_ACK_STATE = "state";
    public static final int PURE_DIGITAL_DATA_ID_STRING_LENGTH = 10;
    public static final String PURE_DIGITAL_DATA_ID_BASE_MATCH_RULE = "^\\d{10}$";
    private boolean debug = true;
    private UDPHelper udpHelper;
    private String dataId;
    private int weight;
    private int userId;
    private boolean isSettingUserId = false;
    private OnUserIdManagerListener onUserIdManagerListener;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    private Runnable setUserIdTimeoutRunnable = new Runnable() {
        public void run() {
            if (UserIdManager.this.debug) {
                System.out.println(UserIdManager.TAG + " >>>>>Timeout in setUserIdTimeoutRunnable");
            }

            sendResultMsg(false);
        }
    };

    private Runnable retrySetUserIdRunnable = new Runnable() {
        public void run() {
            if (UserIdManager.this.debug) {
                System.out.println(UserIdManager.TAG + " >>>>>Retry set user id in Runnable");
            }

            UserIdManager.this.setUserId(true, UserIdManager.this.dataId, UserIdManager.this.weight, UserIdManager.this.userId);
        }
    };
    private static final int MSG_RECEIVE_DATA = 0;
    private static final int MSG_RESULT = 1;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (UserIdManager.this.debug) {
                System.out.println(UserIdManager.TAG + " handleMessage msg:" + msg);
            }

            if (msg != null) {
                switch (msg.what) {
                    case 0:
                        if (msg.obj == null || !(msg.obj instanceof byte[])) {
                            return;
                        }

                        byte[] data = (byte[]) ((byte[]) msg.obj);
                        String string = new String(data);

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(string);
                        } catch (JSONException var10) {
                            var10.printStackTrace();
                            return;
                        }

                        if (jsonObject.has("ID") && jsonObject.has("weight")) {
                            String recvDataId;
                            int recvWeight;
                            try {
                                recvDataId = jsonObject.getString("ID");
                                recvWeight = jsonObject.getInt("weight");
                            } catch (JSONException var9) {
                                var9.printStackTrace();
                                return;
                            }

                            switch (jsonObject.length()) {
                                case 2:
                                    if (UserIdManager.this.onUserIdManagerListener != null) {
                                        UserIdManager.this.onUserIdManagerListener.onReceiveRequestUserIDPkg(recvDataId, recvWeight);
                                    }

                                    return;
                                case 3:
                                    if (jsonObject.has("state")) {
                                        int state;
                                        try {
                                            state = jsonObject.getInt("state");
                                        } catch (JSONException var11) {
                                            var11.printStackTrace();
                                            return;
                                        }

                                        UserIdManager.this.sendResultMsg(state == 1);
                                    }

                                    return;
                                default:
                                    return;
                            }
                        }

                        return;
                    case 1:
                        if (UserIdManager.this.debug) {
                            System.out.println(UserIdManager.TAG + " handleMessage MSG_RESULT msg.obj:" + msg.obj);
                        }

                        if (UserIdManager.isDataIdValid(UserIdManager.this.dataId)
                                && UserIdManager.isWeightValid(UserIdManager.this.weight)
                                && UserIdManager.isUserIdValid(UserIdManager.this.userId)
                                && msg.obj instanceof Boolean) {
                            if (UserIdManager.this.debug) {
                                System.out.println(UserIdManager.TAG + " handleMessage MSG_RESULT. to notify result to callback:" + UserIdManager.this.onUserIdManagerListener);
                            }

                            if (UserIdManager.this.onUserIdManagerListener != null) {
                                boolean result = (Boolean) msg.obj;
                                UserIdManager.this.onUserIdManagerListener.onReceiveSetUserIDAckPkg(UserIdManager.this.dataId, UserIdManager.this.weight, result ? 1 : 0);
                            }
                        }
                }

            }
        }
    };

    public UserIdManager() {
        try {
            this.udpHelper = new UDPHelper(61111);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        this.udpHelper.setDebug(this.debug);
        this.udpHelper.setReply(true);
        this.udpHelper.setTimeoutInMillis(this.timeoutInMillis);
        this.udpHelper.setOnDataListener(this);
    }

    public UserIdManager(UDPHelper helper) {
        try {
            this.udpHelper = helper;
        } catch (Exception var2) {
            var2.printStackTrace();
        }

        this.udpHelper.setDebug(this.debug);
        this.udpHelper.setReply(true);
        this.udpHelper.setTimeoutInMillis(this.timeoutInMillis);
        this.udpHelper.setOnDataListener(this);
        this.udpHelper.init();
     //   init();
    }

    public static boolean isDataIdValid(String dataId) {
        return dataId != null && dataId.matches("^\\d{10}$");
    }

    public static boolean isWeightValid(int weight) {
        return weight > 0;
    }

    public static boolean isUserIdValid(int userId) {
        return userId >= 1 && userId <= 8;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        if (this.udpHelper != null) {
            this.udpHelper.setDebug(debug);
        }

        this.debug = debug;
    }

    public boolean init() {
        if (this.udpHelper == null) {
            return false;
        } else {
            return this.udpHelper.isInit() ? true : this.udpHelper.init();
        }
    }

    public boolean isInit() {
        return this.udpHelper == null ? false : this.udpHelper.isInit();
    }

    public void close() {
        if (this.udpHelper != null) {
            this.udpHelper.close();
        }

        if (this.scheduledThreadPoolExecutor != null) {
            this.scheduledThreadPoolExecutor.shutdownNow();
            this.scheduledThreadPoolExecutor = null;
        }

        this.isSettingUserId = false;
    }

    public void setOnUserIdManagerListener(OnUserIdManagerListener onUserIdManagerListener) {
        this.onUserIdManagerListener = onUserIdManagerListener;
    }

    public boolean setUserId(String dataId, int weight, int userId) {
        return this.setUserId(false, dataId, weight, userId);
    }

    protected boolean setUserId(boolean forceSet, String dataId, int weight, int userId) {
        if (this.init() && (forceSet || !this.isSettingUserId) && isDataIdValid(dataId) && isWeightValid(weight) && isUserIdValid(userId)) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("ID", dataId);
                jsonObject.put("weight", weight);
                jsonObject.put("UserID", userId);
            } catch (JSONException var7) {
                var7.printStackTrace();
                return false;
            }

            this.udpHelper.setSendToInetAddress(this.udpHelper.getReceiveFromInetAddress());
            this.isSettingUserId = this.udpHelper.sendDataAsync(jsonObject.toString().getBytes());
            Log.e("tag", jsonObject.toString());
            if (this.isSettingUserId) {
                this.dataId = dataId;
                this.weight = weight;
                this.userId = userId;
                if (!forceSet) {
                    this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2);
                    this.scheduledThreadPoolExecutor.schedule(this.setUserIdTimeoutRunnable, this.timeoutInMillis, TimeUnit.MILLISECONDS);
                }
            }

            return this.isSettingUserId;
        } else {
            return false;
        }
    }

    public void onConnectResult(boolean success) {
        if (this.debug) {
            System.out.println(TAG + " onConnectResult success:" + success);
        }

    }

    public void onConnectTimeout() {
        if (this.debug) {
            System.out.println(TAG + " onConnectTimeout");
        }

    }

    public void onConnectError(Exception e) {
        if (this.debug) {
            System.out.println(TAG + " onConnectError exception:" + e);
        }

    }

    public void onResult(boolean sendOrReceive, Long taskId, boolean success) {
        if (this.debug) {
            System.out.println(TAG + " onResult sendOrReceive:" + sendOrReceive + " taskId:" + taskId + " success:" + success + " isSettingUserId:" + this.isSettingUserId);
        }

        if (sendOrReceive && this.isSettingUserId && this.scheduledThreadPoolExecutor != null && !this.scheduledThreadPoolExecutor.isShutdown()) {
            if (this.debug) {
                System.out.println(TAG + " onResult to schedule retrySetUserIdRunnable");
            }

            this.scheduledThreadPoolExecutor.schedule(this.retrySetUserIdRunnable, this.retryPeriodInMillis, TimeUnit.MILLISECONDS);
        }

    }

    public void onTimeout(boolean sendOrReceive, Long taskId, byte[] data) {
        if (this.debug) {
            System.out.println(TAG + " onTimeout sendOrReceive:" + sendOrReceive + " taskId:" + taskId + " data:" + (data != null ? HexUtil.encodeHexStr(data) : data));
        }

        if (sendOrReceive) {
            this.sendResultMsg(false);
        }

    }

    public void onReceiveData(Long taskId, byte[] data) {
        if (this.debug) {
            System.out.println(TAG + " onReceiveData taskId:" + taskId + " data:" + (data != null ? HexUtil.encodeHexStr(data) : data));
        }

        if (this.isInit() && data != null && data.length != 0) {
            Message message = this.handler.obtainMessage();
            message.what = 0;
            message.obj = data;
            this.handler.sendMessage(message);
        }
    }

    public void onError(boolean sendOrReceive, Long taskId, Exception e) {
        if (this.debug) {
            System.out.println(TAG + " onError sendOrReceive:" + sendOrReceive + " taskId:" + taskId + " exception:" + e);
        }

    }

    private void sendResultMsg(boolean success) {
        if (this.debug) {
            System.out.println(TAG + " sendResultMsg success:" + success + " isSettingUserId:" + this.isSettingUserId);
        }

        if (this.isInit() && this.isSettingUserId) {
        //    this.close();
            this.handler.removeMessages(1);
            Message message = this.handler.obtainMessage();
            message.what = 1;
            message.obj = success;
            this.handler.sendMessage(message);
        }
    }
}
