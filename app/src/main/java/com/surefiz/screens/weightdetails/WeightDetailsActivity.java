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
import com.surefiz.UserIdManager;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.entity.RequestUserIdInfo;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

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
    private boolean showMsgView = true;
    private boolean debug = true;
    private List<RequestUserIdInfo> requestUserIdInfoList = new ArrayList<RequestUserIdInfo>();
    private LoadingData loader;

    //Weight Measurement Units
    public int captureWeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_details);
        //Bind ButterKnife to the view
        ButterKnife.bind(this);
        //Set onClickListener here
        mWeightDetailsOnclick = new WeightDetailsOnclick(this);
        loader=new LoadingData(this);

        //Close Previous Connections
   //     ClosePrevConnections();

        //Initialize scale
        capturescaledatasetup();

        btn_go_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("@@Clicked: ", "Skip-Button");
                if (userIdManager != null) {
                    Log.e("@@Clicked-1: ", "Skip-Button");
                    Log.d("@@ScaleUsrMgr :", "Initializing.." +   userIdManager.init());
                }
            }
        });

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goNextAction();
            }
        }, GeneralToApp.SCALECONFIG_WAIT_TIME);*/

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

    public void ClosePrevConnections(){
        if (udpHelper != null) {
            udpHelper.close();
        }

        if (userIdManager != null) {
            userIdManager.close();
        }
    }

    public void capturescaledatasetup() {
     //   loader.show();
   //     ClosePrevConnections();
        try {
            udpHelper = new UDPHelper(61111);
      //      udpHelper.setDebug(debug);
       //     Log.d("@@ScaleUDP :", "Initializing.." +  udpHelper.init());

            userIdManager = new UserIdManager(udpHelper);
            userIdManager.setDebug(true);
            userIdManager.setOnUserIdManagerListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveRequestUserIDPkg(String dataId, int weight) {
        Log.d("@@Capture = " ,"dataId: " + dataId + " weight: " + weight);

        tv_kg_lb_value.setText(String.valueOf(weight));

        captureWeight = weight;

       /* if (loader.isShowing())
            loader.dismiss();
        if (requestUserIdInfoList.size() >= 100) {
            requestUserIdInfoList.remove(0);
            tv_kg_lb_value.setText(weight);
        }*/
   //     requestUserIdInfoList.add(new RequestUserIdInfo(dataId, weight));

      /*  for(RequestUserIdInfo info: requestUserIdInfoList){
            Log.d("@@capture-data: ", info.toString());
        }*/

    }

    @Override
    public void onReceiveSetUserIDAckPkg(String dataId, int weight, int status) {
        Log.d("@@CaptureUser" , "dataId: " + dataId + " weight: " + weight + " status: " + status);
       /* if (loader.isShowing())
            loader.dismiss();*/

       /* tv_kg_lb_value.setText(weight);

        for(RequestUserIdInfo info: requestUserIdInfoList){
            Log.d("@@capture-data: ", info.toString());
        }*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("@@Called: ", "onDestroyed()");
        ClosePrevConnections();
    }
}
