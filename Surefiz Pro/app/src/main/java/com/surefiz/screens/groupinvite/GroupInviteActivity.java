package com.surefiz.screens.groupinvite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.groupinvite.adapter.GroupInviteAdapter;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupInviteActivity extends AppCompatActivity {
    @BindView(R.id.rv_registration_group)
    RecyclerView rv_registration_group;
    GroupInviteAdapter adapter;

    private LoadingData loader;
    private int count;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_invite);
        ButterKnife.bind(this);
        loader = new LoadingData(this);
        init();
    }

    private void init() {
        count = getIntent().getIntExtra("count", 0);
        rv_registration_group.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        adapter = new GroupInviteAdapter(this, count);
        rv_registration_group.setAdapter(adapter);
    }


    @OnClick({R.id.btn_register_invite, R.id.btn_register_skip})
    public void manageClicks(View view) {
        switch (view.getId()) {
            case R.id.btn_register_invite:
                getGroupData();
                break;
            case R.id.btn_register_skip:
                Intent loginIntent = new Intent(this, OtpActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }

    private void getGroupData() {
        if (adapter != null) {
            ArrayList<ModelGroupInvite> groupInvites = adapter.getData();
            if (groupInvites != null && groupInvites.size() > 0) {
                boolean oneFilled = false;
                for (int i = 0; i < groupInvites.size(); i++) {
                    if (!groupInvites.get(i).getName().isEmpty() &&
                            !groupInvites.get(i).getEmail().isEmpty()) {
                        oneFilled = true;
                    }
                }
                if (!oneFilled) {
                    Toast.makeText(this, "Please fill at least one user details", Toast.LENGTH_SHORT).show();
                } else {
                    // cal api
                    String allNames = "";
                    String allEmails = "";
                    for (int i = 0; i < groupInvites.size(); i++) {
                        allNames += groupInvites.get(i).getName();
                        allEmails += groupInvites.get(i).getEmail();
                        if (i < groupInvites.size() - 1) {
                            allNames += ",";
                            allEmails += ",";
                        }
                    }
                    callAPI(allNames, allEmails);
                }
            }
        }
    }

    private void callAPI(String allNames, String allEmails) {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        RegistrationModel user = LoginShared.getRegistrationDataModel(this);
        String mainUserId = user.getData().getUser().get(0).getUserId();
        String mainUserName = user.getData().getUser().get(0).getUserName();
        Call<ResponseBody> groupAPI = apiInterface.call_groupApi(mainUserId, mainUserName, allNames, allEmails);
        groupAPI.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String resp = response.body().string();
                    JSONObject jsonObject = new JSONObject(resp);
                    if (jsonObject.optInt("status") == 1) {
                        // send to otp
                        Intent loginIntent = new Intent(GroupInviteActivity.this, OtpActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(GroupInviteActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception ex) {
                    MethodUtils.errorMsg(GroupInviteActivity.this, getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(GroupInviteActivity.this, getString(R.string.error_occurred));
            }
        });
    }
}
