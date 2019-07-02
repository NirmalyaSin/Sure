package com.surefiz.screens.aboutus;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

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
        img_topbar_menu.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.GONE);
        iv_edit.setVisibility(View.GONE);
        btn_add.setVisibility(View.GONE);
    }

    private void loadUrl() {

        web.loadUrl(url);

        WebSettings wbSettings = web.getSettings();
        wbSettings.setJavaScriptEnabled(true);
        wbSettings.getLoadsImagesAutomatically();
        wbSettings.setLoadsImagesAutomatically(true);
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
