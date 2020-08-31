package com.surefiz.screens.instruction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.users.model.UserListItem;
import com.surefiz.screens.users.model.UserListModel;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class InstructionActivity extends AppCompatActivity {
    public List<UserListItem> userLists = new ArrayList<>();
    @BindView(R.id.btndone)
    Button btn_button;
    @BindView(R.id.btn_skip)
    Button btn_skip;
    InstructionActivityonclick mInstructionActivityonclick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(this);
        mInstructionActivityonclick = new InstructionActivityonclick(this);


        new InstructionSharedPreference(InstructionActivity.this).setInstructionVisibility(InstructionActivity.this, LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());
        //LoginShared.setInstructionVisibility(InstructionActivity.this, LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());
        LoginShared.setstatusforwifivarification(InstructionActivity.this, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callUserListApi();
    }

    private void callUserListApi() {
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<UserListModel> userListModelCall = apiInterface.call_userListApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId());

        userListModelCall.enqueue(new Callback<UserListModel>() {
            @Override
            public void onResponse(Call<UserListModel> call, Response<UserListModel> response) {
                try {
                    if (response.body().getStatus() == 1) {
                        userLists.clear();
                        //userLists.addAll(response.body().getData().getUserList());
                        userLists.addAll(checkMainUserVisibility(response.body().getData().getUserList()));

                    } else if (response.body().getStatus() == 2 || response.body().getStatus() == 3) {
                        String deviceToken = LoginShared.getDeviceToken(InstructionActivity.this);
                        LoginShared.destroySessionTypePreference(InstructionActivity.this);
                        LoginShared.setDeviceToken(InstructionActivity.this, deviceToken);
                        Intent loginIntent = new Intent(InstructionActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserListModel> call, Throwable t) {
                //   MethodUtils.errorMsg(InstructionActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    // This method is to check wheather any sub-user wants to be displayed on Main users family
    private List<UserListItem> checkMainUserVisibility(List<UserListItem> userList) {

        List<UserListItem> tempUserList = new ArrayList<>();

        if (LoginShared.getRegistrationDataModel(InstructionActivity.this).getData().getUser().get(0).getScaleUserId().equals("1")) {

            tempUserList.add(userList.get(0));

            for (int i = 1; i < userList.size(); i++) {
                if (userList.get(i).getMainuservisibility() == 1) {
                    tempUserList.add(userList.get(i));
                }

                System.out.println("userDataUpdated: " + userList.get(i).getUserName() + " " + userList.get(i).getScaleUserId() + " " + userList.get(i).getMainuservisibility());
            }
        } else {

            tempUserList.addAll(userList);
        }

        return tempUserList;
    }
}
