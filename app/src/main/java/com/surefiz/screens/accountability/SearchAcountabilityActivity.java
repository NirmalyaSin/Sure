package com.surefiz.screens.accountability;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.AddUserDialog;
import com.surefiz.interfaces.MoveTutorial;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.users.adapter.UserListAdapter;
import com.surefiz.screens.users.model.UserList;
import com.surefiz.screens.users.model.UserListModel;
import com.surefiz.screens.weightdetails.UserIdManager;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import cn.onecoder.scalewifi.net.socket.udp.UDPHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchAcountabilityActivity extends BaseActivity {

    public View view;
    RecyclerView rv_items;

    LoadingData loadingData;
    List<UserList> userLists = new ArrayList<>();
    UserListAdapter adapter;
    UserIdManager userIdManager;
    private UDPHelper udpHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_search_accountability);
        view = View.inflate(this, R.layout.activity_search_accountability, null);
        addContentView(view);
        loadingData = new LoadingData(this);
        setHeaderView();
        setViewBind();
   //     callUserListApi();
    //    setRecyclerViewItem();
    }

    private void callUserListApi() {
        loadingData.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<UserListModel> userListModelCall = apiInterface.call_userListApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());

        userListModelCall.enqueue(new Callback<UserListModel>() {
            @Override
            public void onResponse(Call<UserListModel> call, Response<UserListModel> response) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                try {
                    if (response.body().getStatus() == 1) {
                        userLists.clear();
                        userLists.addAll(response.body().getData().getUserList());
                        adapter.notifyDataSetChanged();
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        Intent loginIntent = new Intent(SearchAcountabilityActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        MethodUtils.errorMsg(SearchAcountabilityActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(SearchAcountabilityActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<UserListModel> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(SearchAcountabilityActivity.this, getString(R.string.error_occurred));
            }
        });


    }

    private void setRecyclerViewItem() {
        adapter = new UserListAdapter(this, userLists);
        rv_items.setAdapter(adapter);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration((int) 10);
        rv_items.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_items.setLayoutManager(mLayoutManager);
    }

    private void setViewBind() {
        rv_items = view.findViewById(R.id.rv_items);
    }

    private void setHeaderView() {
        tv_universal_header.setText("Search Users");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.VISIBLE);
        /*if (LoginShared.getDashboardPageFrom(this).equals("0")) {
            img_topbar_menu.setVisibility(View.VISIBLE);
            btn_done.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            img_topbar_menu.setVisibility(View.GONE);
            btn_done.setVisibility(View.VISIBLE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        }*/
    }
}
