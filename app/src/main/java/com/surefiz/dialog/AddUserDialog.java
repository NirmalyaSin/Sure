package com.surefiz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.interfaces.MoveTutorial;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.users.UserListActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddUserDialog extends Dialog {
    Activity activity;
    ImageView iv_cross;
    EditText et_name;
    EditText et_email;
    Button btn_submit;
    LoadingData loader;
    MoveTutorial moveTutorial;

    public AddUserDialog(Activity activity, MoveTutorial moveTutorial) {
        super(activity, R.style.DialogStyle);
        this.activity = activity;
        this.moveTutorial = moveTutorial;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_user);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        loader = new LoadingData(activity);
        iv_cross = findViewById(R.id.iv_cross);
        btn_submit = findViewById(R.id.btn_submit);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);

        iv_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter user name");
                } else if (et_email.getText().toString().equals("")) {
                    MethodUtils.errorMsg(activity, "Please enter user email");
                } else if (!MethodUtils.isValidEmail(et_email.getText().toString())) {
                    MethodUtils.errorMsg(activity, "Please enter a valid email address");
                } else if (!ConnectionDetector.isConnectingToInternet(activity)) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.no_internet));
                } else {
                    addUserApi();
                }
            }
        });
    }

    private void addUserApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        final Call<ResponseBody> call_addUser = apiInterface.call_adduserApi(LoginShared.getRegistrationDataModel(activity).getData().getToken(),
                LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserId(), LoginShared.getRegistrationDataModel(activity).getData().getUser().get(0).getUserMac(),
                et_name.getText().toString().trim(), et_email.getText().toString().trim(), "10",
                "175", "150", "12345678", "0", "9874859685",
                "09-02-1994", "2", "1");
        call_addUser.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jObject = jsonObject.getJSONObject("data");
                        int scaleUserId = jObject.optInt("scaleUserId");
                        LoginShared.setScaleUserId(scaleUserId);
                        LoginShared.setWeightPageFrom(activity, "2");
                        moveTutorial.onSuccess("1");
                        dismiss();
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(activity, activity.getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(activity, activity.getString(R.string.error_occurred));
            }
        });
    }
}
