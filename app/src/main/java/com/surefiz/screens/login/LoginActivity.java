package com.surefiz.screens.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.utils.MethodUtils;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import com.google.firebase.auth.FirebaseUser;
import com.surefiz.R;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.editEmail)
    EditText editEmail;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.tv_forgetPassword)
    TextView tv_forgetPassword;
    @BindView(R.id.iv_facebook)
    ImageView iv_facebook;
    @BindView(R.id.iv_twiter)
    ImageView iv_twiter;
    @BindView(R.id.tv_register)
    TextView tv_register;
    private CallbackManager callbackManager;

    private LoginClickEvent loginClickEvent;
    private static final int RC_SIGN_IN = 007;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        loginClickEvent = new LoginClickEvent(this);
        iv_facebook.setOnClickListener(this);
        //getHashKey();

        // REMOVE
        editEmail.setText("debopam@capitalnumbers.com");
        editPassword.setText("12345678");
        /*editEmail.setText("khokhar@rasimo.com");
        editPassword.setText("12345678");*/

    }

    private void getHashKey() {
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.surefiz",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_facebook:
                /*if (!ConnectionDetector.isConnectingToInternet(this)) {
                    MethodUtils.errorMsg(this, this.getString(R.string.no_internet));
                } else {
                    callFacebooklogin();
                }*/
                MethodUtils.errorMsg(LoginActivity.this, "Under Development");
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

        } else {
            if (callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void callFacebooklogin() {
        if (AccessToken.getCurrentAccessToken() != null) {
            requestData();
            return;
        }
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email,public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        requestData();
                        loginResult.getAccessToken();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println();
                        exception.printStackTrace();
                    }
                });
    }

    public void requestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                JSONObject json = response.getJSONObject();

                parseFacebookJsonAndAPiCall(json, AccessToken.getCurrentAccessToken());

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture.width(750).height(750)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void parseFacebookJsonAndAPiCall(final JSONObject jsonObjectFbResult, final AccessToken currentAccessToken) {
        if (jsonObjectFbResult == null)
            return;

        final String socialEmail = jsonObjectFbResult.optString("email");
        String socialId = jsonObjectFbResult.optString("id");
        String socialName = jsonObjectFbResult.optString("name");
//        String socialBirthDay = jsonObjectFbResult.optString("birthday");
        String password = socialId + "astro!@#";
        String socialProfileImage = "";
        if (jsonObjectFbResult.optJSONObject("picture") != null) {
            JSONObject obj = jsonObjectFbResult.optJSONObject("picture");
            if (obj.has("data")) {
                JSONObject data = obj.optJSONObject("data");
                if (data.has("url")) {
                    socialProfileImage = data.optString("url");
                }
            }
        }
        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_LONG).show();
//        loginwithSocial(socialName, socialId, getString(R.string.fb_login_type));
    }

}
