package com.example.black.webviewcache;

import android.webkit.WebView;

public class WebViewWrapper {
    private WebView webView;
    private int progress;
    private boolean isLoading;

    public WebViewWrapper(WebView webView) {
        this.webView = webView;
    }

    public WebView getWebView() {
        return webView;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
