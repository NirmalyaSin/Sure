package com.surefiz.screens.dashboard;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.aboutus.AboutUsActivity;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.boardcast.BoardCastActivity;
import com.surefiz.screens.instruction.InstructionActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.screens.profile.ProfileActivity;
import com.surefiz.screens.reminders.ReminderActivity;
import com.surefiz.screens.settings.SettingsActivity;
import com.surefiz.screens.users.UserListActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.base_rl_contentview)
    public RelativeLayout base_rl_contentview;
    @BindView(R.id.img_topbar_menu_profile)
    public CircleImageView img_topbar_menu_profile;
    @BindView(R.id.tv_user_name)
    public TextView tv_user_name;
    @BindView(R.id.tv_dashboard)
    public TextView tv_dashboard;
    @BindView(R.id.tv_weight)
    public TextView tv_weight;
    @BindView(R.id.tv_users)
    public TextView tv_users;
    @BindView(R.id.tv_profile)
    public TextView tv_profile;
    @BindView(R.id.tv_circle)
    public TextView tv_circle;
    @BindView(R.id.tv_message)
    public TextView tv_message;
    @BindView(R.id.tv_reminder)
    public TextView tv_reminder;
    @BindView(R.id.tv_notification)
    public TextView tv_notification;
    @BindView(R.id.tv_settings)
    public TextView tv_settings;
    @BindView(R.id.tv_about)
    public TextView tv_about;
    @BindView(R.id.tv_forum)
    public TextView tv_forum;
    @BindView(R.id.tv_signout)
    public TextView tv_signout;
    @BindView(R.id.img_topbar_menu)
    public ImageButton img_topbar_menu;
    @BindView(R.id.rlFriendRequest)
    public RelativeLayout rlFriendRequest;
    @BindView(R.id.rlUserSearch)
    public RelativeLayout rlUserSearch;
    @BindView(R.id.tvFriendRequestCount)
    public TextView tvFriendRequestCount;
    @BindView(R.id.tv_universal_header)
    public TextView tv_universal_header;
    @BindView(R.id.iv_edit)
    public ImageView iv_edit;
    @BindView(R.id.iv_AddPlus)
    public ImageView iv_AddPlus;
    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;
    @BindView(R.id.btn_add)
    public Button btn_add;
    @BindView(R.id.btn_done)
    public Button btn_done;
    @BindView(R.id.rl_back)
    public RelativeLayout rl_back;
    @BindView(R.id.tvAppVersion)
    public TextView tvAppVersion;


    LoadingData loader;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageLoader imageLoader;

    public static String md5Converter(String s) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")), 0, s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        loader = new LoadingData(this);
        clickEvent();
        iv_AddPlus.setVisibility(View.GONE);
        initializeImageLoader();
        showData();
        initializeDrawer();
        showAppVersion();
    }

    private void showAppVersion() {
        String osVersion = android.os.Build.VERSION.RELEASE;

        String appVersion = "";
        //int appVersionCode = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersion = pInfo.versionName;
            //appVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvAppVersion.setText("Version: " + appVersion);
    }

    private void initializeImageLoader() {
        DisplayImageOptions opts = new DisplayImageOptions.Builder().cacheInMemory(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()).defaultDisplayImageOptions(opts)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void showData() {
        tv_user_name.setText(LoginShared.getUserName(this));
        showImage();
    }

    public void showImage() {
        /*if (LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPhoto().equals("") ||
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPhoto().equalsIgnoreCase("null") ||
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPhoto() == null) {
            img_topbar_menu_profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.prof_img_placeholder));
        } else if (!LoginShared.getViewProfileDataModel(this).getData().getUser().get(0).getUserImage().equals("") ||
                !LoginShared.getViewProfileDataModel(this).getData().getUser().get(0).getUserImage().equalsIgnoreCase("null") ||
                LoginShared.getViewProfileDataModel(this).getData().getUser().get(0).getUserImage() != null) {
            String url = LoginShared.getViewProfileDataModel(this).getData().getUser().get(0).getUserImage();
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, img_topbar_menu_profile);
        } else {
            String url = LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPhoto();
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, img_topbar_menu_profile);
        }*/
        if (LoginShared.getUserPhoto(this).equals("") ||
                LoginShared.getUserPhoto(this).equalsIgnoreCase("null") ||
                LoginShared.getUserPhoto(this) == null) {
            img_topbar_menu_profile.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.prof_img_placeholder));
        } else {
            String url = LoginShared.getUserPhoto(this);
            url = url.replace(" ", "20%");
            imageLoader.displayImage(url, img_topbar_menu_profile);
        }
    }

    private void clickEvent() {
        img_topbar_menu.setOnClickListener(this);
        tv_profile.setOnClickListener(this);
        tv_users.setOnClickListener(this);
        tv_about.setOnClickListener(this);
        tv_forum.setOnClickListener(this);
        tv_dashboard.setOnClickListener(this);
        tv_weight.setOnClickListener(this);
        tv_circle.setOnClickListener(this);
        tv_message.setOnClickListener(this);
        tv_reminder.setOnClickListener(this);
        tv_notification.setOnClickListener(this);
        tv_settings.setOnClickListener(this);
        tv_signout.setOnClickListener(this);
    }

    public void addContentView(View view) {
        base_rl_contentview.removeAllViews();
        base_rl_contentview.addView(view,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
    }

    /**
     * Initialize side menu
     */
    private void initializeDrawer() {
//        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, R.string.app_name
        ) {
            @Override
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        initializeDrawerToggle(mDrawerToggle);
    }

    public void initializeDrawerToggle(ActionBarDrawerToggle mDrawerToggle) {
        this.mDrawerToggle = mDrawerToggle;
    }

    /**
     * To check whether the side menu is open or not
     */
    private boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null)
            mDrawerToggle.onConfigurationChanged(newConfig);

    }

    /*public String md5Converter(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_topbar_menu:
                showData();
                if (isDrawerOpen())
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.tv_users:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent userIntent = new Intent(this, UserListActivity.class);
                userIntent.putExtra("showSkipButton", true);
                startActivity(userIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                //finish();
                break;
            case R.id.tv_profile:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent loginIntent = new Intent(this, ProfileActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_dashboard:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent dashBoardIntent = new Intent(this, DashBoardActivity.class);
                dashBoardIntent.putExtra("isFromMenu", true);
                startActivity(dashBoardIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finishAffinity();
                break;
            case R.id.tv_about:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent aboutIntent = new Intent(this, AboutUsActivity.class);
                aboutIntent.putExtra("url", "https://www.surefiz.com/AboutUs");
                aboutIntent.putExtra("header", "About Us");
                aboutIntent.putExtra("menu", true);
                startActivity(aboutIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_forum:
                mDrawerLayout.closeDrawer(Gravity.LEFT);

                /*String forumUrl = "https://www.surefiz.com/beta/forum/?id=" + LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId() +
                        "&key=" + md5Converter(LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPassword());*/


                String forumUrl = "https://www.surefiz.com/Home/Forum?id=" + LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId() +
                        "&key=" + md5Converter(LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserPassword());

                System.out.println("forumUrl: " + forumUrl);
                aboutIntent = new Intent(this, AboutUsActivity.class);
                aboutIntent.putExtra("url", forumUrl);
                aboutIntent.putExtra("header", "Forum");
                aboutIntent.putExtra("menu", true);
                startActivity(aboutIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_weight:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                LoginShared.setWeightPageFrom(this, "1");
                LoginShared.setScaleUserId
                        (Integer.parseInt(LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).
                                getScaleUserId()));
                Intent insIntent = new Intent(this, InstructionActivity.class);
                startActivity(insIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_circle:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                //    MethodUtils.errorMsg(this, "Under Development");
                Intent circleIntent = new Intent(this, AcountabilityActivity.class);
                startActivity(circleIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_message:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent messageIntent = new Intent(this, BoardCastActivity.class);
                startActivity(messageIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_reminder:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                //       MethodUtils.errorMsg(this, "Under Development");
                Intent reminderIntent = new Intent(this, ReminderActivity.class);
                startActivity(reminderIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_notification:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                //      MethodUtils.errorMsg(this, "Under Development");
                Intent notificationIntent = new Intent(this, NotificationActivity.class);
                startActivity(notificationIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_settings:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                //Intent settingsIntent = new Intent(this, BMIDetailsActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.tv_signout:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                /*String deviceToken = LoginShared.getDeviceToken(BaseActivity.this);
                LoginShared.destroySessionTypePreference();
                LoginShared.setDeviceToken(BaseActivity.this, deviceToken);
                Intent logIntent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(logIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();*/
                callLogoutApi();
                break;
        }
    }

    private void callLogoutApi() {
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> callLogout = apiInterface.call_logoutApi(LoginShared.getRegistrationDataModel(this).getData().getToken(),
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserId(),
                LoginShared.getRegistrationDataModel(this).getData().getToken());

        callLogout.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                try {
                    String responseString = response.body().string();

                    JSONObject jsonObject = new JSONObject(responseString);
                    if (jsonObject.optInt("status") == 1) {

                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(BaseActivity.this, "Successfully Logged Out");


                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String deviceToken = LoginShared.getDeviceToken(BaseActivity.this);
                                LoginShared.destroySessionTypePreference(BaseActivity.this);
                                LoginShared.setDeviceToken(BaseActivity.this, deviceToken);
                                LoginShared.setAccessToken(BaseActivity.this, "");
                                Intent logIntent = new Intent(BaseActivity.this, LoginActivity.class);
                                startActivity(logIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finishAffinity();
                            }
                        }, GeneralToApp.SPLASH_WAIT_TIME);

                    } else if (jsonObject.optInt("status") == 2 || jsonObject.optInt("status") == 3) {
                        Intent loginIntent = new Intent(BaseActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finishAffinity();
                    } else {
                        JSONObject jsObject = jsonObject.getJSONObject("data");
                        MethodUtils.errorMsg(BaseActivity.this, jsObject.getString("message"));
                    }
                } catch (Exception e) {
                    MethodUtils.errorMsg(BaseActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(BaseActivity.this, getString(R.string.error_occurred));
            }
        });
    }
}
