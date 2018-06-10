package com.example.black.webviewcache;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewWrapper {
    private static final String TAG = "WebViewWrapper";
    private WebView webView;
    private int progress;
    private boolean isLoading;
    public String title;
    private String url;

    public WebViewWrapper(Context context, final WebViewManager.UrlLoadCallback urlLoadCallback) {
        this.webView = new WebView(context);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.i(TAG, "onLoadResource: " + url);
                // TODO 保存图片到本地
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isLoading = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                isLoading = false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String t) {
                title = t;
                urlLoadCallback.onTitle(url, t);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress = newProgress;
                urlLoadCallback.onProgress(url, newProgress);
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

    public void loadUrl(String url) {
        if(progress != 100 && !isLoading) {
            this.url = url;
            webView.loadUrl(url);
        }
    }
}
