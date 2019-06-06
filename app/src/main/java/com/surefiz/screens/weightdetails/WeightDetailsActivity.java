package com.surefiz.screens.weightdetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.UDPHelper;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.users.adapter.UserListAdapter;
import com.surefiz.screens.users.model.UserListItem;
import com.surefiz.screens.users.model.UserListModel;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import cn.onecoder.scalewifi.api.impl.OnUserIdManagerListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WeightDetailsActivity extends AppCompatActivity implements OnUiEventClick, OnUserIdManagerListener {

    //    private WeightDetailsOnclick mWeightDetailsOnclick;
    private UDPHelper udpHelper;
    private UserIdManager userIdManager;


    //Weight Measurement Units
    public int captureWeight = 0;
    private String data_id = "";
    private String userName, scaleId;
    private int scaleUserId = -1;
    private String calledFrom;
    private boolean isWeightReceived;
    Handler handler, handler1;
    private boolean isTimerOff = true;
    private String timerValue = "0";
    private String fromPush = "";

    // new
    private LoadingData loader;
    private List<UserListItem> userLists = new ArrayList<>();
    private UserListAdapter adapter;
    private RecyclerView rv_items;
    private Button btn_go_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Log.d("@@LifeCycle : ", "onCreate()");


        setHeader();
        viewBind();
        setRecyclerViewItem();
        callUserListApi();

        handler = new Handler();
        handler1 = new Handler();

        LoginShared.setWeightFromNotification(this, "0");
        if (getIntent().getStringExtra("timerValue") != null) {
            timerValue = getIntent().getStringExtra("timerValue");
        } else {
            timerValue = "0";
        }
        if (getIntent().getStringExtra("fromPush") != null) {
            fromPush = getIntent().getStringExtra("fromPush");
        } else {
            fromPush = "";
        }

//        scaleUserId = LoginShared.getScaleUserId(this);
        userName = LoginShared.getRegistrationDataModel(this).getData()
                .getUser().get(0).getUserName();
        scaleId = LoginShared.getRegistrationDataModel(this).getData()
                .getUser().get(0).getUserMac();

        try {
            if (getIntent().getStringExtra("notificationFlag").equals("1")) {
                LoginShared.setWeightPageFrom(WeightDetailsActivity.this, "0");
            }
        } catch (NullPointerException e) {
            Log.d("exception", "exception happened weight");
            e.printStackTrace();
        }

        //Start time Count-down
        startTimerCountDown();
    }

    private void setHeader() {
        findViewById(R.id.btn_skip).setOnClickListener(view -> goToDashboard());
        findViewById(R.id.rl_back).setOnClickListener(view -> onBackPressed());
    }

    private void viewBind() {
        //Initialize Loader
        loader = new LoadingData(this);
        rv_items = findViewById(R.id.rv_items);
        btn_go_next = findViewById(R.id.btn_go_next);
        findViewById(R.id.btn_add_user).setVisibility(View.GONE);
    }

    private void setRecyclerViewItem() {
        adapter = new UserListAdapter(this, userLists, true, this);
        rv_items.setAdapter(adapter);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        rv_items.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_items.setLayoutManager(mLayoutManager);
    }

    private void callUserListApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<UserListModel> userListModelCall = apiInterface.call_userListApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());

        userListModelCall.enqueue(new Callback<UserListModel>() {
            @Override
            public void onResponse(Call<UserListModel> call, Response<UserListModel> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    if (response.body().getStatus() == 1) {
                        userLists.clear();
                        /*List<UserListItem> temp = response.body().getData().getUserList();
                        if (temp != null && temp.size() > 0) {
                            for (int i = 0; i < temp.size(); i++) {
                                if (temp.get(i).getIsUserHaveCompleteInfo() == 1) {
                                    userLists.add(temp.get(i));
                                }
                            }
                        }*/
                        userLists.addAll(response.body().getData().getUserList());
                        adapter.notifyDataSetChanged();
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(WeightDetailsActivity.this);
                        LoginShared.destroySessionTypePreference(WeightDetailsActivity.this);
                        LoginShared.setDeviceToken(WeightDetailsActivity.this, deviceToken);
                        Intent loginIntent = new Intent(WeightDetailsActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        MethodUtils.errorMsg(WeightDetailsActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(WeightDetailsActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<UserListModel> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(WeightDetailsActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    @Override
    public void onUiClick(Intent intent, int eventCode) {
        if (eventCode == 1001) {
            scaleUserId = intent.getIntExtra("id", 0);
            if (isWeightReceived) {
                saveWeightToSDK();
            }
        }
    }

    private void saveWeightToSDK() {
        if (isTimerOff) {
            boolean setUser = userIdManager.setUserId(data_id, captureWeight, scaleUserId);
            Log.d("@@SetUser = ", "" + setUser);
            if (setUser) {
                goToDashboard();
            } else
                showcancelationDialog();
        }
    }

    public void goToDashboard() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Initialize Weight
        captureWeight = 0;
        data_id = "";
        isWeightReceived = false;

        try {
            udpHelper = new UDPHelper(61111);
            userIdManager = new UserIdManager(udpHelper);
            userIdManager.setDebug(true);
            userIdManager.setOnUserIdManagerListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClosePrevConnections() {
        if (userIdManager != null) {
            userIdManager.close();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("@@LifeCycle : ", "onStop()");
        //Cancel Loader and timer
        cancelTimerAndLoader();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cancel Loader and timer
        cancelTimerAndLoader();
    }

    private void cancelTimerAndLoader() {

        if (loader.isShowing()) {
            loader.dismiss();
        }
        isTimerOff = false;
        handler.removeCallbacksAndMessages(null);
        btn_go_next.setVisibility(View.INVISIBLE);

        //Close UDP Connections
        ClosePrevConnections();
    }

    @Override
    public void onReceiveRequestUserIDPkg(String dataId, int weight) {
        if (loader.isShowing()) {
            loader.dismiss();
        }
        if (!isWeightReceived) {

            if (scaleId.equals(dataId)) {
                isWeightReceived = true;
                data_id = dataId;
                captureWeight = weight;
                Log.d("@@CaptureInner = ", "dataId: " + dataId + " weight: " + weight);
                Log.d("@@calledFrom = ", "" + LoginShared.getWeightPageFrom(WeightDetailsActivity.this));
                if (scaleUserId != -1) {
                    saveWeightToSDK();
                }
            }

        }


        /*captureWeight = weight;

        if (LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("2")) {

            Log.d("@@scaleId = ", "" + scaleId);
            Log.d("@@Weight = ", "" + LoginShared.getCapturedWeight(this));
            Log.d("@@scaleUserId = ", "" + LoginShared.getScaleUserId(this));
            //Set text value to lbs
//            mWeightDetailsOnclick.onClick(btn_lbs);

            //Set userID
            if (isTimerOff) {
                boolean setUser = userIdManager.setUserId(LoginShared.getRegistrationDataModel(this).getData()
                                .getUser().get(0).getUserMac(),
                        Integer.parseInt(LoginShared.getCapturedWeight(this)), LoginShared.getScaleUserId(this));
                Log.d("@@SetUser = ", "" + setUser);
                handler1.removeCallbacksAndMessages(null);
            } else {
//                showcancelationDialog();
            }
        } else {
            if (!isWeightReceived) {
                //Set text value to lbs
//                mWeightDetailsOnclick.onClick(btn_lbs);
                isWeightReceived = true;

                if (scaleId.equals(dataId)) {
                    if (LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("3")) {
                        if (isTimerOff) {
                            boolean setUser = userIdManager.setUserId(LoginShared.getRegistrationDataModel(this).getData()
                                            .getUser().get(0).getUserMac(),
                                    weight, LoginShared.getScaleUserId(this));

                            Log.d("@@SetUser = ", "" + setUser);
//                            Toast.makeText(this, "User id submitted to server.", Toast.LENGTH_LONG).show();
                            handler1.removeCallbacksAndMessages(null);
                        } else {
//                            showcancelationDialog();
                        }
                    } else {
                        try {
//                            showUserSelectionDialog(dataId, weight);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
//                        showDifferentScaleIdDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }

    @Override
    public void onReceiveSetUserIDAckPkg(String dataId, int weight, int status) {
        Log.d("@@CaptureUser-id: ", "dataId: " + dataId
                + " weight: " + weight + " status: " + status);
        if (status == 1) {
            cancelTimerAndLoader();
        } else {
            showcancelationDialog();
        }

        LoginShared.setWeightPageFrom(WeightDetailsActivity.this, "0");
        LoginShared.setDashboardPageFrom(WeightDetailsActivity.this, "0");
    }

    private void startTimerCountDown() {
        if (LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("2") ||
                LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("3")) {
//            loader.show();
            System.out.print("Sure");
//            btn_go_next.setVisibility(View.VISIBLE);
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showcancelationDialog();
                }
            }, 6000);
        } else {
            loader.show();
            btn_go_next.setVisibility(View.GONE);
        }

        if (timerValue != null) {
            if (Integer.parseInt(timerValue) * 1000 > 0) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goNextAction();
                    }
                }, Integer.parseInt(timerValue) * 1000);
            } else {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goNextAction();
                    }
                }, GeneralToApp.SCALECONFIG_WAIT_TIME);
            }
        } else {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goNextAction();
                }
            }, GeneralToApp.SCALECONFIG_WAIT_TIME);
        }
    }

    private void goNextAction() {
        if (loader.isShowing()) {
            loader.dismiss();
        }
        isTimerOff = false;
        /*if (fromPush.equals("1")) {

        }*/
//        btn_go_next.setVisibility(View.VISIBLE);
        showcancelationDialog();
        //Close UDP Connection
        //   ClosePrevConnections();
    }

    public void showcancelationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Sorry! Cannot connect to scale. Please try later.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToDashboard();
            }
        });

        alertDialog.create();

        alertDialog.show();
    }

}
