package com.example.black.webviewcache;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewWrapper {
    private WebView webView;
    private int progress;
    private boolean isLoading;
    public String title;

    public WebViewWrapper(Context context, final WebViewManager.UrlLoadCallback urlLoadCallback) {
        this.webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String t) {
                title = t;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress = newProgress;
                urlLoadCallback.onProgress(webView.getUrl(), newProgress);
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDatabaseEnabled(true);
        settings.setAppCacheEnabled(true);
    }

    public WebView getWebView() {
        return webView;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public String getTitle() {
        return title;
    }

    public void loadUrl(String url) {
        if(progress != 100 && !isLoading) {
            webView.loadUrl(url);
            isLoading = true;
        }
    }
}
