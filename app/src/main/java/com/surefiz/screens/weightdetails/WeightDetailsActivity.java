package com.surefiz.screens.weightdetails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.surefiz.R;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.users.UserListActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.progressloader.LoadingData;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.onecoder.scalewifi.api.impl.OnUserIdManagerListener;
import cn.onecoder.scalewifi.net.socket.udp.UDPHelper;

public class WeightDetailsActivity extends AppCompatActivity implements OnUserIdManagerListener {

    @BindView(R.id.btn_kg)
    Button btn_kg;

    @BindView(R.id.btnSkipWeight)
    Button btnSkipWeight;

    @BindView(R.id.btn_lbs)
    Button btn_lbs;

    @BindView(R.id.btn_go_next)
    Button btn_go_next;

    @BindView(R.id.tv_kg_lb_value)
    TextView tv_kg_lb_value;

    private WeightDetailsOnclick mWeightDetailsOnclick;
    private UDPHelper udpHelper;
    private UserIdManager userIdManager;
    private LoadingData loader;

    //Weight Measurement Units
    public int captureWeight = 0;
    private String data_id = "";
    private String userName, scaleId;
    private int scaleUserId;
    private String calledFrom;
    private boolean isWeightReceived;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_details);
        //Bind ButterKnife to the view
        ButterKnife.bind(this);
        handler = new Handler();

        scaleUserId = LoginShared.getScaleUserId(this);
        userName = LoginShared.getRegistrationDataModel(this).getData()
                .getUser().get(0).getUserName();
        scaleId = LoginShared.getRegistrationDataModel(this).getData()
                .getUser().get(0).getUserMac();

        //     calledFrom = LoginShared.getWeightPageFrom(this);

        //Set onClickListener here
        mWeightDetailsOnclick = new WeightDetailsOnclick(this);
        //Initialize Loader
        loader = new LoadingData(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        calledFrom = LoginShared.getWeightPageFrom(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("2")) {
            captureWeight = Integer.parseInt(LoginShared.getCapturedWeight(WeightDetailsActivity.this));
            //Set text value to lbs
            mWeightDetailsOnclick.onClick(btn_lbs);
            btn_kg.setBackgroundResource(R.drawable.weight_blue_button);
            btn_lbs.setBackgroundResource(R.drawable.weight_white_button);
            btn_kg.setTextColor(getResources().getColor(R.color.whiteColor));
            btn_lbs.setTextColor(getResources().getColor(R.color.whiteColor));
        }

        //Initialize scale
        capturescaledatasetup();
    }

    private void goNextAction() {
        if (loader.isShowing()) {
            loader.dismiss();
        }
        btn_go_next.setVisibility(View.VISIBLE);

        //Close UDP Connection
        //   ClosePrevConnections();
    }

    public void ClosePrevConnections() {
        if (userIdManager != null) {
            userIdManager.close();
        }
    }

    public void capturescaledatasetup() {
        //Start time Count-down
        startTimerCountDown();
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

    private void startTimerCountDown() {
        loader.show();
        btn_go_next.setVisibility(View.GONE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goNextAction();
            }
        }, GeneralToApp.SCALECONFIG_WAIT_TIME);
    }

    @Override
    public void onReceiveRequestUserIDPkg(String dataId, int weight) {

        if (loader.isShowing()) {
            loader.dismiss();
        }

        Log.d("@@CaptureInner = ", "dataId: " + dataId + " weight: " + weight);
        Log.d("@@calledFrom = ", "" + calledFrom);
        captureWeight = weight;

        if (LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("2")) {

            Log.d("@@scaleId = ", "" + scaleId);
            Log.d("@@Weight = ", "" + LoginShared.getCapturedWeight(this));
            Log.d("@@scaleUserId = ", "" + LoginShared.getScaleUserId(this));
            //Set userID
            boolean setUser = userIdManager.setUserId(LoginShared.getRegistrationDataModel(this).getData()
                            .getUser().get(0).getUserMac(),
                    Integer.parseInt(LoginShared.getCapturedWeight(this)), LoginShared.getScaleUserId(this));
            Log.d("@@SetUser = ", "" + setUser);

            Toast.makeText(this, "User id submitted to server.", Toast.LENGTH_LONG).show();
            if (loader.isShowing()) {
                loader.dismiss();
            }
            //LoginShared.setWeightPageFrom(WeightDetailsActivity.this, "0");
            //LoginShared.setDashboardPageFrom(WeightDetailsActivity.this, "0");
            btn_go_next.setVisibility(View.VISIBLE);

        } else {
            if (!isWeightReceived) {
                //Set text value to kg
                mWeightDetailsOnclick.onClick(btn_lbs);
                isWeightReceived = true;

                if (scaleId.equals(dataId)) {
                    if (LoginShared.getWeightPageFrom(WeightDetailsActivity.this).equals("3")) {
                        boolean setUser = userIdManager.setUserId(LoginShared.getRegistrationDataModel(this).getData()
                                        .getUser().get(0).getUserMac(),
                                weight, LoginShared.getScaleUserId(this));
                        Log.d("@@SetUser = ", "" + setUser);
                        Toast.makeText(this, "User id submitted to server.", Toast.LENGTH_LONG).show();
                        if (loader.isShowing()) {
                            loader.dismiss();
                        }
                        btn_go_next.setVisibility(View.VISIBLE);
                    } else {
                        showUserSelectionDialog(dataId, weight);
                    }
                } else {
                    try {
                        showDifferentScaleIdDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void onReceiveSetUserIDAckPkg(String dataId, int weight, int status) {
        Log.d("@@CaptureUser-id: ", "dataId: " + dataId
                + " weight: " + weight + " status: " + status);

        if (status == 1) {
            ClosePrevConnections();
            handler.removeCallbacksAndMessages(null);
            if (loader.isShowing()) {
                loader.dismiss();
            }
            btn_go_next.setVisibility(View.VISIBLE);
        }
    }

    public void showDifferentScaleIdDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Received weight from different scale.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginShared.setDashboardPageFrom(WeightDetailsActivity.this, "0");
                goToDashboard();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginShared.setDashboardPageFrom(WeightDetailsActivity.this, "0");
                goToDashboard();
                dialog.dismiss();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    public void goToDashboard() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        startActivity(intent);
        finish();
    }

    public void showUserSelectionDialog(String dataId, int weight) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Weight is given by you or someone else?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Is this " + userName + "?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Set userID
                boolean setUser = userIdManager.setUserId(dataId, weight, scaleUserId);
                Log.d("@@SetUser = ", "" + setUser);
                //LoginShared.setWeightPageFrom(WeightDetailsActivity.this, "0");
                //LoginShared.setDashboardPageFrom(WeightDetailsActivity.this, "0");
                btn_go_next.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Someone Else", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginShared.setDashboardPageFrom(WeightDetailsActivity.this, "1");
                LoginShared.setCapturedWeight(WeightDetailsActivity.this, String.valueOf(weight));
                Intent intent = new Intent(WeightDetailsActivity.this, UserListActivity.class);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        alertDialog.create();
        alertDialog.show();
    }


}
