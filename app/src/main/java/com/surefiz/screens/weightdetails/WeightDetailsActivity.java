package com.surefiz.screens.weightdetails;

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
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.entity.RequestUserIdInfo;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.onecoder.scalewifi.api.UserIdManager;
import cn.onecoder.scalewifi.api.impl.OnUserIdManagerListener;
import cn.onecoder.scalewifi.net.socket.udp.UDPHelper;

public class WeightDetailsActivity extends AppCompatActivity implements OnUserIdManagerListener {

    @BindView(R.id.btn_kg)
    Button btn_kg;

    @BindView(R.id.btn_lbs)
    Button btn_lbs;

    @BindView(R.id.btn_go_next)
    Button btn_go_next;

    @BindView(R.id.tv_kg_lb_value)
    TextView tv_kg_lb_value;

    private UDPHelper udpHelper;
    private UserIdManager userIdManager;

    private boolean showMsgView = true;
    private boolean debug = true;
    private List<RequestUserIdInfo> requestUserIdInfoList;

    private LoadingData loader;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_details);
        loader=new LoadingData(this);
        requestUserIdInfoList = new ArrayList<RequestUserIdInfo>();
        ButterKnife.bind(this);
        new WeightDetailsOnclick(this);
        capturescaledatasetup();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goNextAction();

            }
        }, GeneralToApp.SCALECONFIG_WAIT_TIME);

    }

    private void goNextAction() {
        loader.dismiss();
        btn_go_next.setVisibility(View.VISIBLE);

        if (userIdManager == null) {
            Toast.makeText(this, "UserIdManager not init yet.", Toast.LENGTH_SHORT).show();
        }
       boolean ret = userIdManager.init();
        if (ret) {
            Toast.makeText(this, "UserIdManager  init sucessfully.", Toast.LENGTH_SHORT).show();

        }
    }

    private void capturescaledatasetup() {
        loader.show();
        try {
            udpHelper = new UDPHelper(61111);
            //udpHelper = new UDPHelper(6666);
            udpHelper.setDebug(debug);
            userIdManager = new UserIdManager();
            userIdManager.setOnUserIdManagerListener(this);
            userIdManager.setDebug(debug);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveRequestUserIDPkg(String dataId, int weight) {
        System.out.println("saghjg" + " 收到请求userID广播 dataId:" + dataId + " weight:" + weight);
        if (loader.isShowing())
            loader.dismiss();
        if (requestUserIdInfoList.size() >= 100) {
            requestUserIdInfoList.remove(0);
        }
        requestUserIdInfoList.add(new RequestUserIdInfo(dataId, weight));

    }

    @Override
    public void onReceiveSetUserIDAckPkg(String dataId, int weight, int status) {
        System.out.println("AFDAYTF" + " 收到设置userID ACK广播 dataId:" + dataId + " weight:" + weight + " status:" + status);
        if (loader.isShowing())
            loader.dismiss();

    }
    @Override
    protected void onDestroy() {
        if (udpHelper != null) {
            udpHelper.close();
        }
        if (userIdManager != null) {
            userIdManager.close();
        }
        super.onDestroy();
    }
}
