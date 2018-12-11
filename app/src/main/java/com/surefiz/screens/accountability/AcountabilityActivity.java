package com.surefiz.screens.accountability;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.accountability.adapter.AllCircleUserAdapter;
import com.surefiz.screens.accountability.models.CircleUserResponse;
import com.surefiz.screens.accountability.models.User;
import com.surefiz.screens.acountabiltySearch.SearchAcountabilityActivity;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AcountabilityActivity extends BaseActivity implements AllCircleUserAdapter.OnCircleViewClickListener {

    public View view;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<User> arrayListUsers = new ArrayList<User>();
    private AllCircleUserAdapter mAllCircleUserAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_accountability, null);
        addContentView(view);
        setHeaderView();
        setRecyclerViewItem();
        loadingData = new LoadingData(this);

        iv_AddPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AcountabilityActivity.this,
                        SearchAcountabilityActivity.class));
            }
        });

        callCircleUserListApi();
    }

    private void callCircleUserListApi() {
        loadingData.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<CircleUserResponse> circleUserListApi = apiInterface.call_CircleUserListApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());

        circleUserListApi.enqueue(new Callback<CircleUserResponse>() {
            @Override
            public void onResponse(Call<CircleUserResponse> call, Response<CircleUserResponse> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                    arrayListUsers.clear();
                }

                try {
                    if (response.body().getStatus() == 1) {
                        arrayListUsers.addAll(response.body().getData().getUserList());
                        Log.d("@@UserItem : ", arrayListUsers.get(0).toString());
                    } else {
                        MethodUtils.errorMsg(AcountabilityActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(AcountabilityActivity.this, getString(R.string.error_occurred));
                }

                mAllCircleUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CircleUserResponse> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(AcountabilityActivity.this, getString(R.string.error_occurred));
            }
        });


    }

    private void setRecyclerViewItem() {
        recyclerView = view.findViewById(R.id.rv_items);
        mAllCircleUserAdapter = new AllCircleUserAdapter(this,
                arrayListUsers, this);
        recyclerView.setAdapter(mAllCircleUserAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Accountability Circle");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.VISIBLE);

    }

    @Override
    public void onViewClick(int position) {

    }

    @Override
    public void onSendMessageClick(int position) {

    }

    @Override
    public void onPerformanceClick(int position) {
        Intent intent = new Intent(AcountabilityActivity.this, DashBoardActivity.class);
        intent.putExtra("id", arrayListUsers.get(position).getUser_id());
        startActivity(intent);
        finish();

    }
}
