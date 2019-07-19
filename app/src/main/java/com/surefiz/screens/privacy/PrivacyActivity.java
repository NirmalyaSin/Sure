package com.surefiz.screens.privacy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.login.LoginActivity;
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
                String selectedIDs = "";
                String unSelectedIds = "";
                for (PrivacySetting setting : arrayListPrivacySetting) {
                    if (setting.getPrivacyEnabled().equals("1")) {
                        selectedIDs = selectedIDs + setting.getPrivacyId() + ",";
                    } else {
                        unSelectedIds = unSelectedIds + setting.getPrivacyId() + ",";
                    }
                }

                String finalSelectedIds = "";
                if (!selectedIDs.equalsIgnoreCase("")) {
                    finalSelectedIds = selectedIDs.substring(0, selectedIDs.lastIndexOf(","));
                }
                String finalUnSelectedIds = unSelectedIds.substring(0, unSelectedIds.lastIndexOf(","));

                Log.d("@@Selected-IDs : ", finalSelectedIds);
                Log.d("@@Un-Selected-IDs : ", finalUnSelectedIds);

                //Call Api to update the privacy.
                callUpdatePrivacyApi(finalSelectedIds, finalUnSelectedIds);
            }
        });
    }

    private void setRecyclerViewItem() {
        mPrivacyAdapter = new PrivacyAdapter(this, arrayListPrivacySetting, this);
        recyclerView.setAdapter(mPrivacyAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
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

        Log.d("@@Sent-Privacy : ", "token = " + "\nsenderId =" + senderId);

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
                } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                    String deviceToken = LoginShared.getDeviceToken(PrivacyActivity.this);
                    LoginShared.destroySessionTypePreference(PrivacyActivity.this);
                    LoginShared.setDeviceToken(PrivacyActivity.this, deviceToken);
                    Intent loginIntent = new Intent(PrivacyActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    MethodUtils.errorMsg(PrivacyActivity.this, response.body().getData().getMessage());
                }
                //Refresh the list
                mPrivacyAdapter.notifyDataSetChanged();
                //Hide save button
                buttonSaveChanges.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PrivacyListResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                //Show Error dialog
                MethodUtils.errorMsg(PrivacyActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void callUpdatePrivacyApi(final String selected, final String unSelected) {
        //Show loader
        loadingData.show_with_label("Saving...");
        //Call API Using Retrofit
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        String token = LoginShared.getRegistrationDataModel(this).getData().getToken();
        String senderId = LoginShared.getRegistrationDataModel(this).getData().getUser()
                .get(0).getUserId();

        Log.d("@@Sent-update : ", "token = " + "\nsenderId =" + senderId
                + "\nselectedIDs =" + selected + "\nunSelectedIds =" + unSelected);

        final Call<PrivacyListResponse> call_UpdatePrivacyList = apiInterface
                .call_UpdatePrivacyList(token, senderId, selected.trim(), unSelected.trim());
        call_UpdatePrivacyList.enqueue(new Callback<PrivacyListResponse>() {
            @Override
            public void onResponse(Call<PrivacyListResponse> call,
                                   Response<PrivacyListResponse> response) {

                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }

                if (response.body().getStatus() == 1) {
                    //Show Alert dialog
                    showResponseDialog(response.body().getStatus(),
                            response.body().getData().getMessage());
                } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                    String deviceToken = LoginShared.getDeviceToken(PrivacyActivity.this);
                    LoginShared.destroySessionTypePreference(PrivacyActivity.this);
                    LoginShared.setDeviceToken(PrivacyActivity.this, deviceToken);
                    Intent loginIntent = new Intent(PrivacyActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    MethodUtils.errorMsg(PrivacyActivity.this,
                            response.body().getData().getMessage());
                }
            }

            @Override
            public void onFailure(Call<PrivacyListResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }
                MethodUtils.errorMsg(PrivacyActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    @Override
    public void onSelected(int position) {
        buttonSaveChanges.setVisibility(View.VISIBLE);
        arrayListPrivacySetting.get(position).setPrivacyEnabled("1");
        Log.d("@@Privacy : ", arrayListPrivacySetting.get(position).getPrivacyEnabled());
        mPrivacyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onUnSelectd(int position) {
        buttonSaveChanges.setVisibility(View.VISIBLE);
        arrayListPrivacySetting.get(position).setPrivacyEnabled("0");
        Log.d("@@Privacy : ", arrayListPrivacySetting.get(position).getPrivacyEnabled());
        mPrivacyAdapter.notifyDataSetChanged();
    }

    public void showResponseDialog(int status, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Cancel the dialog.
                dialog.dismiss();
                //Call Privacy list Api Again
                callPrivacyListApi();
            }
        });

        dialog.create();
        dialog.show();
    }
}
