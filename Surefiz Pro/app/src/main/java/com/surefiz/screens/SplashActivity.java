package com.surefiz.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.surefiz.R;
import com.surefiz.screens.accountability.AcountabilityActivity;
import com.surefiz.screens.bmidetails.BMIDetailsActivity;
import com.surefiz.screens.boardcast.BoardCastActivity;
import com.surefiz.screens.chat.ChatActivity;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.login.LoginActivity;
import com.surefiz.screens.notifications.NotificationActivity;
import com.surefiz.screens.otp.OtpActivity;
import com.surefiz.screens.progressstatus.ProgressStatusActivity;
import com.surefiz.screens.registration.MembershipActivity;
import com.surefiz.screens.registration.RegistrationActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.setupPreparation.SetUpPreparation;
import com.surefiz.screens.userconfirmation.UserConfirmationActivity;
import com.surefiz.screens.weightdetails.WeightDetailsActivity;
import com.surefiz.screens.welcome.WelcomeActivity;
import com.surefiz.sharedhandler.InstructionSharedPreference;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.GeneralToApp;
import com.surefiz.utils.MessagDateConverter;
import com.surefiz.utils.MethodUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SplashActivity extends AppCompatActivity {

    RegistrationModel loginModel;
    private String notificationPage;
    private String receiver_id;
    private JSONObject jsonObject1;
    private String getServerDate = "", getServerTime = "", progressUserId = "", contentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MethodUtils.fullScreen(this);

        System.out.println("instructionShown: "+ new InstructionSharedPreference(SplashActivity.this).getInstructionVisibility(SplashActivity.this));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            jsonObject1 = null;
            if (getIntent().hasExtra("_fbSourceApplicationHasBeenSet") && !getIntent().hasExtra("pushData")) {
                LoginShared.setWeightFromNotification(this, "0");
               // System.out.print("SureFIZ Pro");
            } else if (getIntent().hasExtra("pushData")) {
                String value = bundle.get("pushData").toString();
                try {
                    jsonObject1 = new JSONObject(value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject1.optInt("pushType") == 2) {
                    receiver_id = jsonObject1.optString("senderId");
                    LoginShared.setWeightFromNotification(this, "2");
                } else if (jsonObject1.optInt("pushType") == 4) {
                    LoginShared.setWeightFromNotification(this, "4");
                } else if (jsonObject1.optInt("pushType") == 5) {
                    LoginShared.setWeightFromNotification(this, "5");
                } else if (jsonObject1.optInt("pushType") == 6) {
                    LoginShared.setWeightFromNotification(this, "6");
                    progressUserId = jsonObject1.optString("userId");
                    contentId = jsonObject1.optString("contentId");
                } else if (jsonObject1.optInt("pushType") == 1) {
                    LoginShared.setWeightFromNotification(this, "1");
                    getServerDate = jsonObject1.optString("lastServerUpdateDate");
                    getServerTime = jsonObject1.optString("lastServerUpdateTime");
                } else if (jsonObject1.optInt("pushType") == 10) {
                    LoginShared.setWeightFromNotification(this, "10");
                    getServerDate = jsonObject1.optString("lastServerUpdateDate");
                    getServerTime = jsonObject1.optString("lastServerUpdateTime");
                } else if (jsonObject1.optInt("pushType") == 7) {
                    LoginShared.setWeightFromNotification(this, "7");
                } else if (jsonObject1.optInt("pushType") == 3) {
                    LoginShared.setWeightFromNotification(this, "3");
                }else if (jsonObject1.optInt("pushType") == 12) {
                    LoginShared.setWeightFromNotification(this, "12");
                }
            } else {
                LoginShared.setWeightFromNotification(this, "0");
            }
        } else {
            LoginShared.setWeightFromNotification(this, "0");
        }
        if (getIntent().getStringExtra("notificationFlag") != null) {
            notificationPage = getIntent().getStringExtra("notificationFlag");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    Log.d("@@Splash : ", "onCreate() ==> " + notificationPage);
                    if (notificationPage != null) {
                        if (notificationPage.equals("1")) {
                            LoginShared.setWeightPageFrom(SplashActivity.this, "0");
                        }
                    } else {
                        notificationPage = "0";
                    }
                } catch (Exception e) {
                    Log.d("exception", "exception happened weight");
                    notificationPage = "0";
                    e.printStackTrace();
                }

                if(LoginShared.getAccessToken(SplashActivity.this).equals("")){

                    Intent loginIntent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();

                }else {
                    navigate();
                }

            }
        }, GeneralToApp.SPLASH_WAIT_TIME);

    }

    private void navigate() {
        Log.d("@@SNotification : ", String.valueOf(notificationPage));

        if (!LoginShared.getWeightFromNotification(this).equals("0")) {
            if (LoginShared.getWeightFromNotification(this).equals("1") || LoginShared.getWeightFromNotification(this).equals("10")) {

                if (jsonObject1.has("userWeight"))
                    if (jsonObject1.optInt("userWeight") == 0) {

                        Intent intent = new Intent(this, DashBoardActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();

                    } else {

                        String dateStr = getServerDate + " " + getServerTime;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                        try {

                            Date date = dateFormat.parse(MessagDateConverter.getConvertedNotificationDate(dateStr));
                            Date currentDate = new Date();
                            long diff = currentDate.getTime() - date.getTime();

                            //*********************AVIK

                            Log.d("Time_Diff", ":::::" + currentDate.getTime() + "-" + date.getTime() + "=" + diff);
                            int diffSecond = (int) (diff / 1000);
                            if (diffSecond < 120) {
                                int remainingTime = 120 - diffSecond;

                                Intent intent = new Intent(this, WeightDetailsActivity.class);
                                intent.putExtra("timerValue", remainingTime);
                                intent.putExtra("fromPush", "1");

                                if (LoginShared.getWeightFromNotification(this).equals("10")) {
                                    intent.putExtra("shouldOpenWeightAssignView", true);
                                    intent.putExtra("userWeight", jsonObject1.optInt("userWeight"));
                                    intent.putExtra("scaleMacAddress", jsonObject1.optString("scaleMacAddress"));
                                    intent.putExtra("text", jsonObject1.optString("text"));

                                }

                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                Intent intent = new Intent(this, DashBoardActivity.class);
                                intent.putExtra("expired", "1");
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
            } else if (LoginShared.getWeightFromNotification(this).equals("2")) {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("reciver_id", receiver_id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }  else if (LoginShared.getWeightFromNotification(this).equals("4")) {
                Intent intent = new Intent(this, AcountabilityActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("5")) {
                String serverUserId = "";
                String scaleUserId = "";
                if (jsonObject1 != null) {
                    serverUserId = jsonObject1.optString("serverUserId");
                    scaleUserId = jsonObject1.optString("ScaleUserId");
                }
                Intent intent = new Intent(this, BMIDetailsActivity.class);
                intent.putExtra("notificationFlag", "1");
                intent.putExtra("serverUserId", serverUserId);
                intent.putExtra("ScaleUserId", scaleUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("6")) {
                Intent intent = new Intent(this, ProgressStatusActivity.class);
                intent.putExtra("userId", progressUserId);
                intent.putExtra("contentId", contentId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("7")) {
                Intent intent = new Intent(this, UserConfirmationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }else if (LoginShared.getWeightFromNotification(this).equals("12")) {
                Intent intent = new Intent(this, BoardCastActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            } else if (LoginShared.getWeightFromNotification(this).equals("3")) {
                Intent intent = new Intent(this, NotificationActivity.class);
                intent.putExtra("fromDashboard", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        } else if (LoginShared.getRegistrationDataModel(this) != null &&
                LoginShared.getRegistrationDataModel(this).getData() != null &&
                LoginShared.getRegistrationDataModel(this).getData().getToken().equals("") &&
                LoginShared.getRegistrationDataModel(this).getData().getToken() == null &&
                LoginShared.getRegistrationDataModel(this).getData().getUser().get(0).getUserProfileCompleteStatus() == 0) {
            Intent regIntent = new Intent(SplashActivity.this, MembershipActivity.class);
            regIntent.putExtra("completeStatus", "0");
            startActivity(regIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            if (LoginShared.getRegistrationDataModel(this) != null) {
                if (LoginShared.getRegistrationDataModel(this).getData() != null) {
                    if (LoginShared.getRegistrationDataModel(this).getData().getToken().equals("") ||
                            LoginShared.getRegistrationDataModel(this).getData().getToken() == null) {
                        if (LoginShared.getWelcome(this)) {
                            Intent loginIntent = new Intent(this, LoginActivity.class);
                            startActivity(loginIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        } else {
                            Intent loginIntent = new Intent(this, WelcomeActivity.class);
                            startActivity(loginIntent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    } else if (!LoginShared.getstatusforOtpvarification(this)) {
                        Intent intent = new Intent(this, OtpActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else if (!LoginShared.getstatusforwifivarification(this)) {
                        //AVIK
                        //Intent intent = new Intent(this, WifiConfigActivity.class);
                        Intent intent = new Intent(this, SetUpPreparation.class);
                        intent.putExtra("fromLogin",true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }  else {
                        Intent intent = new Intent(this, DashBoardActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                } else {
                    if (LoginShared.getWelcome(this)) {
                        Intent loginIntent = new Intent(this, LoginActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Intent loginIntent = new Intent(this, WelcomeActivity.class);
                        startActivity(loginIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }
                }
            }else if (!LoginShared.getRegistrationResponse(this).equals("") && LoginShared.getRegistrationDataModel(this)==null) {

                 if (!LoginShared.getRegistrationComplete(this)) {
                    Intent intent = new Intent(this, RegistrationActivity.class);
                    intent.putExtra("completeStatus", "0");
                    intent.putExtra("registrationModelData",LoginShared.getRegistrationResponse(this));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }else{
                     Intent loginIntent = new Intent(this, LoginActivity.class);
                     startActivity(loginIntent);
                     overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                     finish();
                 }

            } else {
                if (LoginShared.getWelcome(this)) {
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                } else {
                    Intent loginIntent = new Intent(this, WelcomeActivity.class);
                    startActivity(loginIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                }
            }
        }
    }
}
