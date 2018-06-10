package com.example.black.webviewcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";
    private WebView webView;
    private int progress;
    private boolean isLoading;
    public String title;
    private String url;
    private long lastCompletelyVisibleTimestamp;

    public WebViewWrapper(final Context context, final WebViewManager.UrlLoadCallback urlLoadCallback, String initUrl) {
        this.webView = new WebView(context);
        this.url = initUrl;
        webView.setWebViewClient(new CachedWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isLoading = true;
                urlLoadCallback.onLoad(url, isLoading, title, progress);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                isLoading = false;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progress = 0;
                title = "Load error";
                isLoading = false;
                urlLoadCallback.onLoad(url, isLoading, title, progress);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                progress = 0;
                title = "Load error";
                isLoading = false;
                urlLoadCallback.onLoad(url, isLoading, title, progress);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String t) {
                title = t;
                urlLoadCallback.onLoad(url, isLoading, title, progress);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress = newProgress;
                if(newProgress == 100) {
                    isLoading = false;
                }
                urlLoadCallback.onLoad(url, isLoading, title, progress);
            }
        });
        Util.initWebViewSettings(webView);
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

    public void loadUrl() {
        if(progress != 100 && !isLoading) {
            webView.loadUrl(url);
        }
    }

    public long getLastCompletelyVisibleTimestamp() {
        return lastCompletelyVisibleTimestamp;
    }

    public void setLastCompletelyVisibleTimestamp(long lastCompletelyVisibleTimestamp) {
        this.lastCompletelyVisibleTimestamp = lastCompletelyVisibleTimestamp;
    }

    public String getUrl() {
        return url;
    }
}
