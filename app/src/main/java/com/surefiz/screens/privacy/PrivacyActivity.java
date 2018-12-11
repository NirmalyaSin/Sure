package com.surefiz.screens.privacy;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.chat.adapter.ChatAdapter;
import com.surefiz.screens.chat.model.ChatListResponse;
import com.surefiz.screens.chat.model.Conversation;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.privacy.adapter.PrivacyAdapter;
import com.surefiz.screens.privacy.model.PrivacyListResponse;
import com.surefiz.screens.privacy.model.PrivacySetting;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PrivacyActivity extends BaseActivity implements PrivacyAdapter.OnPrivacyListener {
    public View view;
    private RecyclerView recyclerView;
    private Button buttonSaveChanges;
    private LoadingData loadingData;
    private ArrayList<PrivacySetting> arrayListPrivacySetting = new ArrayList<PrivacySetting>();
    private PrivacyAdapter mPrivacyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_privacy, null);
        addContentView(view);
        initializeView();

    }

    private void initializeView() {
        setHeaderView();
        loadingData = new LoadingData(this);
        recyclerView = view.findViewById(R.id.recyclerView);
        buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges);
        //Setup RecyclerView
        setRecyclerViewItem();
        //Api Call to fetch all privacy list
        callPrivacyListApi();

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedIDs ="";
                String unSelectedIds = "";
                for (PrivacySetting setting:arrayListPrivacySetting){
                    if(setting.getPrivacyEnabled().equals("1")){
                        selectedIDs = selectedIDs+setting.getPrivacyId()+",";
                    }else {
                        unSelectedIds = unSelectedIds+setting.getPrivacyId()+",";
                    }
                }

                Log.d("@@Selected-IDs : ", selectedIDs.substring(0, selectedIDs.lastIndexOf(",")));
                Log.d("@@Un-Selected-IDs : ", unSelectedIds.substring(0, unSelectedIds.lastIndexOf(",")));
            }
        });
    }

    private void setRecyclerViewItem() {
        mPrivacyAdapter = new PrivacyAdapter(this, arrayListPrivacySetting, this);
        recyclerView.setAdapter(mPrivacyAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

    }


    private void setHeaderView() {
        tv_universal_header.setText("Privacy");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        img_topbar_menu.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void callPrivacyListApi() {
        //Show loader
        loadingData.show_with_label("Loading...");
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String senderId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                .get(0).getUserId();

        Log.d("@@Sent-Privacy : ","token = " +"\nsenderId ="+senderId);

        final Call<PrivacyListResponse> call_PrivacyListApi = apiInterface
                .call_PrivacyListApi(token, senderId);
        call_PrivacyListApi.enqueue(new Callback<PrivacyListResponse>() {
            @Override
            public void onResponse(Call<PrivacyListResponse> call,
                                   Response<PrivacyListResponse> response) {

                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                //Clear the previous list
                arrayListPrivacySetting.clear();

                if (response.body().getStatus() == 1) {
                    //Add Items to the list
                    arrayListPrivacySetting.addAll(response.body().getData().getPrivacySettings());
                }

                mPrivacyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<PrivacyListResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                    MethodUtils.errorMsg(PrivacyActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    @Override
    public void onSelected(int position) {
        arrayListPrivacySetting.get(position).setPrivacyEnabled("1");
        Log.d("@@Privacy : ", arrayListPrivacySetting.get(position).getPrivacyEnabled());
        mPrivacyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUnSelectd(int position) {
        arrayListPrivacySetting.get(position).setPrivacyEnabled("0");
        Log.d("@@Privacy : ", arrayListPrivacySetting.get(position).getPrivacyEnabled());
        mPrivacyAdapter.notifyDataSetChanged();
    }
}
