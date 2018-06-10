package com.example.black.webviewcache;

import android.content.Context;
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
    private UrlLoadCallback urlLoadCallback;

    boolean hasWebView(String url) {
        WebViewWrapper webView = webViewWrapperMap.get(url);
        return webView != null;
    }
    void addWebView(Context context, String url) {
        if(webViewWrapperMap.containsKey(url)) {
            return;
        }
        WebViewWrapper webViewWrapper = new WebViewWrapper(context, urlLoadCallback);
        webViewWrapperMap.put(url, webViewWrapper);
    }
    void removeWebView(String url) {
        stopLoading(url);
        webViewWrapperMap.remove(url);
    }
    void startLoad(String url) {
        WebViewWrapper webViewWrapper = webViewWrapperMap.get(url);
        if(webViewWrapper == null) {
            Log.e(TAG, "startLoad: 不存在url=(" + url + ")的WebView");
            return;
        }
        if(webViewWrapper.getProgress() != 100 && !webViewWrapper.isLoading()) {
            Log.i(TAG, "startLoad: url=(" + url + ")开始加载...");
            webViewWrapper.loadUrl(url);
        } else {
            Log.i(TAG, "startLoad: url=(" + url + ")已完成加载");
        }
    }
    void stopLoading(String url) {
        WebViewWrapper webViewWrapper = webViewWrapperMap.get(url);
        if(webViewWrapper == null) {
            Log.e(TAG, "startLoad: 不存在url=(" + url + ")的WebView");
            return;
        }
        webViewWrapper.getWebView().stopLoading();
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

    public void setUrlLoadCallback(UrlLoadCallback urlLoadCallback) {
        this.urlLoadCallback = urlLoadCallback;
    }

    public void clearCache() {
        Set<Map.Entry<String, WebViewWrapper>> entry = webViewWrapperMap.entrySet();
        Iterator<Map.Entry<String, WebViewWrapper>> iterator = entry.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WebViewWrapper> item = iterator.next();
            if(item.getValue() != null && item.getValue().getWebView() != null) {
                item.getValue().getWebView().clearCache(true);
                break;
            }
        }
        release();
    }

    interface UrlLoadCallback {
        void onProgress(String url, int progress);

        void onTitle(String url, String title);
    }
}
