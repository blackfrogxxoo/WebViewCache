package com.example.black.webviewcache;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
        WebViewWrapper webViewWrapper = new WebViewWrapper(context, urlLoadCallback, url);
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
            Log.i(TAG, "startLoad: url=(" + url + ") 开始加载...");
            webViewWrapper.loadUrl();
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
            WebView webView = item.getValue().getWebView();
            webView.clearHistory();
            webView.destroy();
            webView = null;
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

    /**
     * 遍历所有webViewWrapper，如果lastCompletelyVisibleTimestamp已经是3秒前且不为0，则开始加载
     */
    public void startLoadAll() {
        Set<Map.Entry<String, WebViewWrapper>> entry = webViewWrapperMap.entrySet();
        Iterator<Map.Entry<String, WebViewWrapper>> iterator = entry.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WebViewWrapper> item = iterator.next();
            if(item.getValue() != null && item.getValue().getWebView() != null) {
                WebViewWrapper wrapper = item.getValue();
                if(wrapper.getLastCompletelyVisibleTimestamp() > 0 && System.currentTimeMillis() - wrapper.getLastCompletelyVisibleTimestamp() > 3000) {
                    startLoad(wrapper.getUrl());
                }
            }
        }
    }

    public void onVisibleUrls(List<String> visibleUrls, List<String> completelyVisibleUrls) {
        Set<Map.Entry<String, WebViewWrapper>> entry = webViewWrapperMap.entrySet();
        Iterator<Map.Entry<String, WebViewWrapper>> iterator = entry.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, WebViewWrapper> item = iterator.next();
            if(item.getValue() != null && item.getValue().getWebView() != null) {
                WebViewWrapper wrapper = item.getValue();
                if(!visibleUrls.contains(wrapper.getUrl())) {
                    // 完全不可见，lastCompletelyVisibleTimestamp重置为0
                    wrapper.setLastCompletelyVisibleTimestamp(0);
                } else if(completelyVisibleUrls.contains(wrapper.getUrl()) && wrapper.getLastCompletelyVisibleTimestamp() == 0) {
                    // 完全可见，如果lastCompletelyVisibleTimestamp = 0, 则置为当前时间戳
                    wrapper.setLastCompletelyVisibleTimestamp(System.currentTimeMillis());
                }
            }
        }
    }

    interface UrlLoadCallback {
        void onLoad(String url, boolean isLoading, String title, int progress);
    }
}
