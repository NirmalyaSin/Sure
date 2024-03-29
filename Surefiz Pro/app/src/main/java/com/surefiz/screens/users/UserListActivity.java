package com.surefiz.screens.users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.dialog.AddUserDialog;
import com.surefiz.interfaces.MoveTutorial;
import com.surefiz.interfaces.OnUiEventClick;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.aboutus.AboutUsActivity;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.users.adapter.UserListAdapter;
import com.surefiz.screens.users.model.UserListItem;
import com.surefiz.screens.users.model.UserListModel;
import com.surefiz.screens.weightdetails.UserIdManager;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.SpacesItemDecoration;
import com.surefiz.utils.encryption.MD5;
import com.surefiz.utils.progressloader.LoadingData;

import java.util.ArrayList;
import java.util.List;

import cn.onecoder.scalewifi.net.socket.udp.UDPHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserListActivity extends BaseActivity implements OnUiEventClick {

    public View view;
    RecyclerView rv_items;

    LoadingData loadingData;
    List<UserListItem> userLists = new ArrayList<>();
    UserListAdapter adapter;
    UserIdManager userIdManager;
    private UDPHelper udpHelper;
    private Button btn_add_user;
    private RelativeLayout rl_add_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_user_list, null);
        addContentView(view);
        loadingData = new LoadingData(this);
        setViewBind();

        setHeaderView();
        //if (!getIntent().getBooleanExtra("isFromPushNotification", false)) {
        callUserListApi();
        //}

        addUserDialog();
        //doneUserDialog();
        setRecyclerViewItem();
    }

    private void setHeaderView() {
        tv_universal_header.setText("List Of Users");
        iv_edit.setVisibility(View.GONE);
        findViewById(R.id.iv_weight_managment).setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);


        img_topbar_menu.setVisibility(View.VISIBLE);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void doneUserDialog() {

        if (getIntent().getBooleanExtra("showSkipButton", false)) {
            findViewById(R.id.btn_skip).setVisibility(View.GONE);
            findViewById(R.id.rl_back).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.rl_back).setOnClickListener(v -> {
            onBackPressed();
        });

        findViewById(R.id.btn_skip).setOnClickListener(v -> {
            Intent loginIntent = new Intent(UserListActivity.this, DashBoardActivity.class);
            startActivity(loginIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }

    private void addUserDialog() {
        btn_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebLink();
            }
        });

        //btn_add_user.setOnClickListener(v -> new ChooseOptionDialog(UserListActivity.this, UserListActivity.this).show());
    }

    private void openWebLink() {

        String url="https://www.surefiz.com/Home/Adduser?id="+LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId()
                +"&key="+ MD5.encrypt(LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPassword());

        Log.d("Web_URL",":::"+url);

        Intent aboutIntent = new Intent(this, AboutUsActivity.class);
        aboutIntent.putExtra("url", url);
        aboutIntent.putExtra("header", "Add User");
        aboutIntent.putExtra("menu", false);
        startActivityForResult(aboutIntent, 200);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            callUserListApi();
        }
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


                        if(response.body().getData().getSubUserAddStatus()==0){
                            rl_add_user.setVisibility(View.GONE);
                        }else{
                            rl_add_user.setVisibility(View.VISIBLE);
                            showAddUserButton(response.body().getData().getSubUserAddStatus(), response.body().getData().getUserList().size());
                        }


                        userLists.clear();
                        userLists.addAll(checkMainUserVisibility(response.body().getData().getUserList()));
                        adapter.notifyDataSetChanged();


                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(UserListActivity.this);
                        LoginShared.destroySessionTypePreference(UserListActivity.this);
                        LoginShared.setDeviceToken(UserListActivity.this, deviceToken);
                        Intent loginIntent = new Intent(UserListActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        MethodUtils.errorMsg(UserListActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(UserListActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<UserListModel> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(UserListActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void showAddUserButton(int subUserAddStatus, int userListSize) {
        if (subUserAddStatus == 0 && userListSize > 3) {
            btn_add_user.setEnabled(false);
            btn_add_user.setBackground(ContextCompat.getDrawable(UserListActivity.this, R.drawable.login_edit_rounded_corner_blue));
            btn_add_user.setTextColor(ContextCompat.getColor(UserListActivity.this, android.R.color.black));
            btn_add_user.setVisibility(View.GONE);
        } else {
            btn_add_user.setEnabled(true);
            btn_add_user.setBackground(ContextCompat.getDrawable(UserListActivity.this, R.drawable.login_button_gradient));
            btn_add_user.setTextColor(ContextCompat.getColor(UserListActivity.this, android.R.color.white));
            btn_add_user.setVisibility(View.VISIBLE);
        }
    }


    // This method is to check wheather any sub-user wants to be displayed on Main users family
    private List<UserListItem> checkMainUserVisibility(List<UserListItem> userList) {

        List<UserListItem> tempUserList = new ArrayList<>();

        if (LoginShared.getRegistrationDataModel(UserListActivity.this).getData().getUser().get(0).getScaleUserId().equals("1")) {

            tempUserList.add(userList.get(0));

            for (int i = 1; i < userList.size(); i++) {
                //if (userList.get(i).getMainuservisibility() == 1) {
                tempUserList.add(userList.get(i));
                //}

                System.out.println("userDataUpdated: " + userList.get(i).getUserName() + " " + userList.get(i).getScaleUserId() + " " + userList.get(i).getMainuservisibility() + " " + userList.get(i).getIsUserHaveCompleteInfo());
            }
        } else {
            for (int i = 0; i < userList.size(); i++) {
                if (String.valueOf(userList.get(i).getScaleUserId()).equalsIgnoreCase(LoginShared.getRegistrationDataModel(UserListActivity.this).getData().getUser().get(0).getScaleUserId())) {
                    tempUserList.add(userList.get(i));
                }

                System.out.println("userDataUpdated: " + userList.get(i).getUserName() + " " + userList.get(i).getScaleUserId() + " " + userList.get(i).getMainuservisibility());
            }
        }

        return tempUserList;
    }

    private void callUserDeleteApi(int userId, int position) {
        loadingData.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<UserListModel> userListModelCall = apiInterface.call_delteUserApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                "" + userId);

        userListModelCall.enqueue(new Callback<UserListModel>() {
            @Override
            public void onResponse(Call<UserListModel> call, Response<UserListModel> response) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                try {
                    if (response.body().getStatus() == 1) {
                        if (adapter != null)
                            adapter.notifyItemRemoved(position);
                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(UserListActivity.this);
                        LoginShared.destroySessionTypePreference(UserListActivity.this);
                        LoginShared.setDeviceToken(UserListActivity.this, deviceToken);
                        Intent loginIntent = new Intent(UserListActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        MethodUtils.errorMsg(UserListActivity.this, response.body().getData().getMessage());
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(UserListActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<UserListModel> call, Throwable t) {
                if (loadingData != null && loadingData.isShowing())
                    loadingData.dismiss();
                MethodUtils.errorMsg(UserListActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void setRecyclerViewItem() {
        adapter = new UserListAdapter(this, userLists, false, this);
        rv_items.setAdapter(adapter);
        rv_items.setItemAnimator(new DefaultItemAnimator());
        rv_items.setItemAnimator(new DefaultItemAnimator());
        SpacesItemDecoration decoration = new SpacesItemDecoration(10);
        rv_items.addItemDecoration(decoration);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv_items.setLayoutManager(mLayoutManager);
    }

    private void setViewBind() {
        rv_items = view.findViewById(R.id.rv_items);
        btn_add_user = findViewById(R.id.btn_add_user);
        rl_add_user = findViewById(R.id.rl_add_user);
    }


    @Override
    public void onUiClick(Intent intent, int eventCode) {
        if (eventCode == 1) {
            if (intent != null) {
                int usrId = intent.getIntExtra("user", 0);
                int position = intent.getIntExtra("position", 0);
                callUserDeleteApi(usrId, position);
            }
        } else if (eventCode == 101) {
            if (intent != null) {
                boolean isDialog = intent.getBooleanExtra("userDialog", false);

                new AddUserDialog(UserListActivity.this, new MoveTutorial() {
                    @Override
                    public void onSuccess(String success) {
                        if (success.equals("1")) {
                            if (LoginShared.getDashboardPageFrom(UserListActivity.this).equals("1")) {
                                LoginShared.setWeightPageFrom(UserListActivity.this, "2");
                                Intent loginIntent = new Intent(UserListActivity.this, WeightDetailsActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                LoginShared.setWeightPageFrom(UserListActivity.this, "3");
                                Intent loginIntent = new Intent(UserListActivity.this, InstructionActivity.class);
                                startActivity(loginIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }
                    }
                }, isDialog).show();
            }

        }
    }
}
