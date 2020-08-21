package com.app.smartuni;

import android.os.Bundle;
import android.os.StrictMode;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.app.smartuni.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyCvActivity extends BaseActivity {

    @BindView(R.id.webView) WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My CV");
        ButterKnife.bind(this);
        initWebView();
    }

    private void initWebView() {
       StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
       StrictMode.setVmPolicy(builder.build());
       WebSettings webSettings=mWebView.getSettings();
       webSettings.setJavaScriptEnabled(true);
       webSettings.setDomStorageEnabled(true);
       //mWebView.setInitialScale(30);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.loadUrl("http://192.168.43.12/Resume-CV/");
    }
}
