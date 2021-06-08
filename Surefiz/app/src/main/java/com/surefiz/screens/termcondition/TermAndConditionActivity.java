package com.surefiz.screens.termcondition;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.surefiz.R;
import com.surefiz.screens.dashboard.BaseActivity;
import com.surefiz.utils.progressloader.LoadingData;

public class TermAndConditionActivity extends BaseActivity {
    public View view;
    String url = "";
    WebView web;
    private LoadingData loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_term_and_condition, null);
        addContentView(view);
        viewBind();
        setHeaderView();
        //url = "https://www.surefiz.com/AboutUs";
        url = "https://www.surefiz.com/Home/termsandconditions";
        loadUrl();
    }

    private void viewBind() {
        web = findViewById(R.id.web);
        loader = new LoadingData(this);
    }

    private void setHeaderView() {
        //tv_universal_header.setText(getResources().getString(R.string.surefiz_register));
        tv_universal_header.setText("Terms & Conditions");
        img_topbar_menu.setVisibility(View.GONE);
        btn_done.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
        rl_back.setVisibility(View.VISIBLE);

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUrl() {
        loader.show();

        WebSettings wbSettings = web.getSettings();
        wbSettings.setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web.setWebViewClient(new WebViewClient());

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("@@Term-Condition : ", "onPageFinished()");
                if (loader != null && loader.isShowing())
                    loader.dismiss();
            }
        });

        web.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
