package com.surefiz.screens.accountability;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.accountability.adapter.AllCircleUserAdapter;
import com.surefiz.screens.accountability.models.CircleUserResponse;
import com.surefiz.screens.accountability.models.User;
import com.surefiz.screens.accountability.models.removeuser.RemoveUserAccount;
import com.surefiz.screens.acountabiltySearch.SearchAcountabilityActivity;
import com.surefiz.screens.boardcast.BoardCastActivity;
import com.surefiz.screens.chat.ChatActivity;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.screens.privacy.PrivacyActivity;
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

    private static final int ADD_FRIEND_REQUEST = 1001;
    public View view;
    private RecyclerView recyclerView;
    private LoadingData loadingData;
    private ArrayList<User> arrayListUsers = new ArrayList<User>();
    private AllCircleUserAdapter mAllCircleUserAdapter;
    private SwipeRefreshLayout swiperefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_accountability, null);
        addContentView(view);

        initializeImageLoader();
        setHeaderView();
        setSwipeRefresh();
        setRecyclerViewItem();
        LoginShared.setWeightFromNotification(this, "0");
        loadingData = new LoadingData(this);
        callCircleUserListApi(false);
    }

    private void initializeImageLoader() {
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LoginShared.setWeightFromNotification(this, "0");
        callCircleUserListApi(false);
    }

    private void callCircleUserListApi(boolean isSwipe) {

        if(!isSwipe)
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
                }else{
                    swiperefresh.setRefreshing(false);
                    arrayListUsers.clear();
                }

                try {
                    if (response.body().getStatus() == 1) {
                        if (response.body().getData().getUserList().size() > 0) {
                            arrayListUsers.addAll(response.body().getData().getUserList());
                            Log.d("@@UserItem : ", arrayListUsers.get(0).toString());
                        } else {
                            MethodUtils.errorMsg(AcountabilityActivity.this, "No User Found.");
                        }
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(AcountabilityActivity.this);
                        LoginShared.destroySessionTypePreference(AcountabilityActivity.this);
                        LoginShared.setDeviceToken(AcountabilityActivity.this, deviceToken);
                        Intent loginIntent = new Intent(AcountabilityActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
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

    private void setSwipeRefresh(){

        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeColors(R.color.blue, R.color.purple, R.color.blue);

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("onRefresh", "onRefresh called from SwipeRefreshLayout");

                        swiperefresh.setRefreshing(true);
                        callCircleUserListApi(true);


                    }
                }
        );
    }

    private void setRecyclerViewItem() {
        recyclerView = view.findViewById(R.id.rv_items);
        mAllCircleUserAdapter = new AllCircleUserAdapter(this,
                arrayListUsers, this);
        recyclerView.setAdapter(mAllCircleUserAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        recyclerView.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setHeaderView() {


        tv_universal_header.setText("Accountability Circle");
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        iv_AddPlus.setVisibility(View.GONE);
        rlFriendRequest.setVisibility(View.GONE);
        findViewById(R.id.iv_setting).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_setting).setOnClickListener(view1 -> {
            Intent privacyIntent = new Intent(this, PrivacyActivity.class);
            startActivity(privacyIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        findViewById(R.id.ll_acc_friend).setOnClickListener(view1 -> {
            startActivityForResult(new Intent(AcountabilityActivity.this,
                    SearchAcountabilityActivity.class), ADD_FRIEND_REQUEST);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        findViewById(R.id.ll_acc_message).setOnClickListener(view1 -> {
            Intent messageIntent = new Intent(this, BoardCastActivity.class);
            startActivity(messageIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //finish();
        });
    }

    @Override
    public void onViewClick(int position) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_FRIEND_REQUEST && resultCode == Activity.RESULT_OK) {
            callCircleUserListApi(false);
        }
    }

    @Override
    public void onSendMessageClick(int position) {

        //Go to Chat Page
        Intent chatIntent = new Intent(this, ChatActivity.class);
        chatIntent.putExtra("reciver_id", arrayListUsers.get(position).getUser_id());
        chatIntent.putExtra("reciverName", arrayListUsers.get(position).getUser_name());
        startActivity(chatIntent);
    }

    @Override
    public void onPerformanceClick(int position) {

        Intent intent = new Intent(AcountabilityActivity.this, DashBoardActivity.class);
        intent.putExtra("id", arrayListUsers.get(position).getUser_id());
        intent.putExtra("page", "1");
        intent.putExtra("Performance", "1");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();

    }

    private ImageLoader imageLoader;

    @Override
    public void onRemoveClick(int position) {
        //callRemoveAccountUserApi(position);
        showUserConfirmation(position);
    }

    @Override
    public void onImageClick(int position) {
        showUserImage(position);
    }

    private void showUserImage(int position) {

        Dialog dialog =new Dialog(this);
        dialog.setContentView(R.layout.expanded_image_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ImageView ivUserImage=dialog.findViewById(R.id.ivUserImage);
        ImageView ivCloseButton=dialog.findViewById(R.id.ivCloseButton);
        imageLoader.displayImage(arrayListUsers.get(position).getUser_search_image(), ivUserImage);

        ivCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showUserConfirmation(int position) {

        AlertDialog alertDialog = new AlertDialog.Builder(AcountabilityActivity.this).create();
        alertDialog.setTitle(this.getResources().getString(R.string.delete_user_confirmation));
        //alertDialog.setMessage("Your Configuration failed to complete. Would you like to configure AP?");
        alertDialog.setMessage("Do you want to unfriend " + arrayListUsers.get(position).getUser_name() + "?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                (dialog, which) -> {
                    dialog.dismiss();
                    callRemoveAccountUserApi(position);
                });
        alertDialog.show();
    }


    private void callRemoveAccountUserApi(int position) {

        loadingData.show_with_label("Loading");

        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<RemoveUserAccount> removeAccountUserApi = apiInterface.call_ApiforRemove_accuser(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(),
                arrayListUsers.get(position).getUser_id());

        removeAccountUserApi.enqueue(new Callback<RemoveUserAccount>() {
            @Override
            public void onResponse(Call<RemoveUserAccount> call, Response<RemoveUserAccount> response) {
                if (loadingData != null && loadingData.isShowing()) {
                    loadingData.dismiss();
                }

                try {
                    //Log.d("@@removeAccountUser : ", jsonObject.toString());
                    if (response.body().getStatus() == 1) {
                        //arrayListUsers.addAll(response.body().getData().getUserList());
                        Log.d("@@UserItem : ", arrayListUsers.get(0).toString());
                        arrayListUsers.remove(position);
                        MethodUtils.errorMsg(AcountabilityActivity.this, response.body().getData().getMessage());

                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(AcountabilityActivity.this);
                        LoginShared.destroySessionTypePreference(AcountabilityActivity.this);
                        LoginShared.setDeviceToken(AcountabilityActivity.this, deviceToken);
                        Intent loginIntent = new Intent(AcountabilityActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        MethodUtils.errorMsg(AcountabilityActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(AcountabilityActivity.this, getString(R.string.error_occurred));
                }

                mAllCircleUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<RemoveUserAccount> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(AcountabilityActivity.this, getString(R.string.error_occurred));
            }
        });


    }
}
