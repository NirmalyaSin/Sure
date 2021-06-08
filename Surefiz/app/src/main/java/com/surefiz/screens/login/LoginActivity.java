package com.surefiz.screens.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.rts.commonutils_2_0.netconnection.ConnectionDetector;
import com.surefiz.R;
import com.surefiz.apilist.ApiList;
import com.surefiz.networkutils.ApiInterface;
import com.surefiz.networkutils.AppConfig;
import com.surefiz.screens.dashboard.DashBoardActivity;
import com.surefiz.screens.registration.model.RegistrationModel;
import com.surefiz.screens.welcome.WelcomeActivity;
import com.surefiz.sharedhandler.LoginShared;
import com.surefiz.utils.MethodUtils;
import com.surefiz.utils.progressloader.LoadingData;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 007;
    private static final int RC_SIGN_IN_GOOGLE = 100;
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
    @BindView(R.id.googleSignInButton)
    ImageView googleSignInButton;
    @BindView(R.id.iv_twiter)
    ImageView iv_twiter;
    @BindView(R.id.tv_register)
    TextView tv_register;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    private CallbackManager callbackManager;
    private LoginClickEvent loginClickEvent;
    private GoogleApiClient googleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        loginClickEvent = new LoginClickEvent(this);
        iv_facebook.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        // REMOVE
        //editEmail.setText("kannanrasimo12@gmail.com");
        //editPassword.setText("12345678");

        startFireBase();

        //getHashKey();
    }

    private void startFireBase() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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
                if (!ConnectionDetector.isConnectingToInternet(this)) {
                    MethodUtils.errorMsg(this, this.getString(R.string.no_internet));
                } else {
                    callFacebooklogin();
                }
                break;

            case R.id.googleSignInButton:
                Auth.GoogleSignInApi.signOut(googleApiClient);

                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN_GOOGLE);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

        } else if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            if (callbackManager != null)
                callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String id = account.getId();
            String idToken = account.getIdToken();
            String name = account.getDisplayName();
            String email = account.getEmail();
            Uri photoUrl = account.getPhotoUrl();

            System.out.println("googleData: " + id + "\n" + email + "\n" + name + "\n" + photoUrl + "\n" + idToken);

            callapiforSocaillogin(id, email, name, getString(R.string.google_login_type), photoUrl == null ? "" : photoUrl.toString(), "", "", idToken);
        } else {
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }


    private void callapiforSocaillogin(String socicalID, String emailId,
                                       String fullName, String medianame,
                                       String userImage, String dob,
                                       String phoneNumber, String socicalToken) {

        LoadingData loader = new LoadingData(LoginActivity.this);
        loader.show_with_label("Loading");
        Retrofit retrofit = AppConfig.getRetrofit(ApiList.BASE_URL);
        final ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ResponseBody> socialoginapicall = apiInterface.call_socialloginApi(socicalID,
                emailId, fullName, medianame, medianame, userImage, dob, phoneNumber, "2", socicalToken, LoginShared.getDeviceToken(LoginActivity.this));

        socialoginapicall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();

                try {

                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.d("@@LoginDataSocial : ", jsonObject.toString());

                    loginClickEvent.navigateAfterLogin(responseString,true);

                } catch (Exception e) {
                    MethodUtils.errorMsg(LoginActivity.this, getString(R.string.error_occurred));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (loader != null && loader.isShowing())
                    loader.dismiss();
                MethodUtils.errorMsg(LoginActivity.this, getString(R.string.error_occurred));
            }
        });
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
        callapiforSocaillogin(socialId, socialEmail, socialName, getString(R.string.fb_login_type), socialProfileImage,
                "", "", currentAccessToken.toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()){
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }else{
            super.onBackPressed();
        }
    }
}
