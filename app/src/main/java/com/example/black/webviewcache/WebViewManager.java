package com.example.black.webviewcache;

import android.util.Log;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public enum WebViewManager {
    INSTANCE;
    private static final String TAG = "WebViewManager";
    Map<String, WebViewWrapper> webViewWrapperMap = new HashMap<>();
    boolean hasWebView(String url) {
        WebViewWrapper webView = webViewWrapperMap.get(url);
        return webView != null;
    }
    void addWebView(String url, WebViewWrapper webViewWrapper) {
        if(webViewWrapperMap.containsKey(url)) {
            return;
        }
        webViewWrapperMap.put(url, webViewWrapper);
    }
    void removeWebView(String url) {
        stopLoading(url);
        webViewWrapperMap.remove(url);
    }
    void startLoad(String url) {
        WebViewWrapper webViewWrapper = webViewWrapperMap.get(url);
        if(webViewWrapper == null) {
            Log.e(TAG, "startLoad: 不存在url=(" + url + ")的WebView，请检查代码");
            return;
        }
        if(webViewWrapper.getProgress() != 100 && !webViewWrapper.isLoading()) {
            Log.i(TAG, "startLoad: url=(" + url + ")开始加载...");
            webViewWrapper.getWebView().loadUrl(url);
            webViewWrapper.setLoading(true);
        } else {
            Log.i(TAG, "startLoad: url=(" + url + ")已完成加载");
        }
    }
    void stopLoading(String url) {
        WebViewWrapper webViewWrapper = webViewWrapperMap.get(url);
        if(webViewWrapper == null) {
            Log.e(TAG, "startLoad: 不存在url=(" + url + ")的WebView，请检查代码");
            return;
        }
        webViewWrapper.getWebView().stopLoading();
    }

    public void setProgress(String url, int progress) {
        WebViewWrapper webViewWrapper = webViewWrapperMap.get(url);
        if(webViewWrapper == null) {
            Log.e(TAG, "startLoad: 不存在url=(" + url + ")的WebView，请检查代码");
            return;
        }
        webViewWrapper.setProgress(progress);
        if(progress == 100) {
            webViewWrapper.setLoading(false);
        }
    }
    public int getProgress(String url) {
        WebViewWrapper webViewWrapper = webViewWrapperMap.get(url);
        if(webViewWrapper == null) {
            Log.e(TAG, "startLoad: 不存在url=(" + url + ")的WebView，请检查代码");
            return 0;
        }
        return webViewWrapper.getProgress();
    }

    void release() {
        Set<Map.Entry<String, WebViewWrapper>> entry = webViewWrapperMap.entrySet();
        Iterator<Map.Entry<String, WebViewWrapper>> iterator = entry.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WebViewWrapper> item = iterator.next();
            stopLoading(item.getKey());
        }
        webViewWrapperMap.clear();
    }
}
