package com.surefiz.screens.aboutus;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.surefiz.R;
import com.surefiz.screens.dashboard.BaseActivity;

public class AboutUsActivity extends BaseActivity {
    public View view;
    String url = "";
    WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = View.inflate(this, R.layout.activity_about_us, null);
        addContentView(view);
        viewBind();
        setHeaderView();
        url = getIntent().getStringExtra("url");
        loadUrl();
    }

    private void viewBind() {
        web = findViewById(R.id.web);
    }

    private void setHeaderView() {
        tv_universal_header.setText(getIntent().getStringExtra("header"));
        img_topbar_menu.setVisibility(getIntent().getBooleanExtra("menu", false) ?
                View.VISIBLE : View.INVISIBLE);
        btn_done.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
        rlUserSearch.setVisibility(View.GONE);
    }

    private void loadUrl() {



        WebSettings wbSettings = web.getSettings();
        wbSettings.setJavaScriptEnabled(true);
        /*wbSettings.getLoadsImagesAutomatically();
        wbSettings.setLoadsImagesAutomatically(true);*/
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        web.setWebViewClient(new WebViewClient());

        web.loadUrl(url);

        /*webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (loader != null && loader.isShowing())
                    loader.dismiss();
            }
        });*/

    }


}
