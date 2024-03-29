package com.surefiz.screens.progressstatus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProgressStatusActivity extends AppCompatActivity implements View.OnClickListener {

    //9180,9211,9015
    @BindView(R.id.tv_desc)
    TextView tv_desc;
    @BindView(R.id.tvHeader)
    TextView tvHeader;
    @BindView(R.id.rl_progress)
    LinearLayout rl_progress;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.btn_dashboard)
    Button btn_dashboard;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_weight)
    TextView tv_weight;
    @BindView(R.id.tv_muscle)
    TextView tv_muscle;
    @BindView(R.id.tv_fats)
    TextView tv_fats;
    @BindView(R.id.tv_water)
    TextView tv_water;
    @BindView(R.id.tv_bmi)
    TextView tv_bmi;
    @BindView(R.id.tv_bmr)
    TextView tv_bmr;
    @BindView(R.id.tv_visceral_fat)
    TextView tv_visceral_fat;
    @BindView(R.id.tv_body_type)
    TextView tv_body_type;
    @BindView(R.id.tv_body_score)
    TextView tv_body_score;
    @BindView(R.id.tv_link2)
    TextView tv_link2;
    @BindView(R.id.profile_image)
    ImageView profile_image;
    @BindView(R.id.rl_back)
    RelativeLayout rl_back;
    private LoadingData loader;
    private String userId = "", contentId = "";
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_status);
        ButterKnife.bind(this);
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ProgressStatusActivity.this)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        if (getIntent().getStringExtra("userId") != null) {
            userId = getIntent().getStringExtra("userId");
            contentId = getIntent().getStringExtra("contentId");
        }

//        tv_desc.setMovementMethod(new ScrollingMovementMethod());
        loader = new LoadingData(ProgressStatusActivity.this);

//        if (contentId.equals("1")) {
        if (!ConnectionDetector.isConnectingToInternet(ProgressStatusActivity.this)) {
            MethodUtils.errorMsg(ProgressStatusActivity.this, ProgressStatusActivity.this.getString(R.string.no_internet));
        } else {
            getProgressStatus();
        }
      /* } else {
            showDashBoardDialog();
        }*/
        setClickEvent();

    }

    private void showDashBoardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("User select content type is not available. Please try again.")
                .setCancelable(false)
                .setPositiveButton("Dashboard", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ProgressStatusActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setClickEvent() {
        btn_dashboard.setOnClickListener(this);
        rl_back.setOnClickListener(this);
    }

    private void getProgressStatus() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        final Call<ResponseBody> progress = apiInterface.call_ProgressDetailsApi(
                LoginShared.getRegistrationDataModel(ProgressStatusActivity.this).getData().getToken(),
                userId, contentId);
        progress.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        JSONObject object = jsObject.optJSONObject("progressDetails");
                        setData(object, object.optString("progressDetailsLink1"),
                                object.optString("progressDetailsLink2"));

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        String deviceToken = LoginShared.getDeviceToken(ProgressStatusActivity.this);
                        LoginShared.destroySessionTypePreference(ProgressStatusActivity.this);
                        LoginShared.setDeviceToken(ProgressStatusActivity.this, deviceToken);
                        Intent loginIntent = new Intent(ProgressStatusActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(ProgressStatusActivity.this, jsObject.getString("message"));
                    }

                } catch (Exception e) {
                    MethodUtils.errorMsg(ProgressStatusActivity.this, getString(R.string.error_occurred));
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(ProgressStatusActivity.this, getString(R.string.error_occurred));
            }
        });
    }

    private void setData(JSONObject object, String progressDetailsLink1, String progressDetailsLink2) {
        if (object.optString("Header_Details") != null) {
            tv_name.setText(object.optString("Header_Details"));
        }
        if (object.optString("progressDetails") != null) {
            tv_desc.setText(Html.fromHtml(object.optString("progressDetails") + " \n" + progressDetailsLink1));
            Linkify.addLinks(tv_desc, Linkify.WEB_URLS);
            tv_desc.setLinkTextColor(Color.parseColor("#3981F5"));
        }

        //If content type is equals 14 then the stats will not be shown.

        if (object.optString("content_Type").equals("14")){
            tvHeader.setVisibility(View.GONE);
            rl_progress.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }
        JSONObject jsonObject = object.optJSONObject("baseline");

        if (jsonObject.optString("Weight") != null) {
            tv_weight.setText("Weight: " + jsonObject.optString("Weight"));
        }
        if (jsonObject.optString("Muscle") != null) {
            tv_muscle.setText("Muscle: " + jsonObject.optString("Muscle"));
        }
        if (jsonObject.optString("Fats") != null) {
            tv_fats.setText("Fats: " + jsonObject.optString("Fats"));
        }
        if (jsonObject.optString("Water") != null) {
            tv_water.setText("Water: " + jsonObject.optString("Water"));
        }
        if (jsonObject.optString("BMI") != null) {
            tv_bmi.setText("BMI: " + jsonObject.optString("BMI"));
        }

        if (jsonObject.optString("BMI") != null) {
            tv_bmr.setText("BMR: " + jsonObject.optString("BMR"));
        }

        if (jsonObject.optString("VFAL") != null) {
            tv_visceral_fat.setText("Visceral Fat: " + jsonObject.optInt("VFAL"));
        }

        if (jsonObject.optString("Score") != null) {
            tv_body_score.setText("Body Score: " + jsonObject.optInt("Score"));
        }

        if (jsonObject.optString("bodytype") != null) {
            tv_body_type.setText("Body Type: " + jsonObject.optString("bodytype"));
        }

        if (jsonObject.optString("progressDetailsLink2") != null) {
            tv_link2.setText(Html.fromHtml(progressDetailsLink2));
            Linkify.addLinks(tv_link2, Linkify.WEB_URLS);
            tv_link2.setLinkTextColor(Color.parseColor("#3981F5"));
        }
        if (object.optString("image") != null) {
            String url = object.optString("image");
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, profile_image);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dashboard:
                Intent intent = new Intent(this, DashBoardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finishAffinity();
                break;
            case R.id.rl_back:
                onBackPressed();
        }
    }
}
