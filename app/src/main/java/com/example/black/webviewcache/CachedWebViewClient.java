package com.example.black.webviewcache;

import android.annotation.TargetApi;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class CachedWebViewClient extends WebViewClient {
    private static final String TAG = "CachedWebViewClient";

    @Override
    public void onLoadResource(WebView view, final String url) {
        // 保存图片到本地
        if (Util.isImageUrl(url)) {
            Log.i(TAG, "开始单独下载网页中的图片: " + url);
            ThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    ImageCache.loadImageToCache(url);
                }
            });
        }
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        InputStream is = getLocalImageInputStream(url);
        if (is == null) {
            return super.shouldInterceptRequest(view, url);
        } else {
            return new WebResourceResponse("image/png", "utf-8", is);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        InputStream is = getLocalImageInputStream(url);
        if (is == null) {
            return super.shouldInterceptRequest(view, request);
        } else {
            Log.i(TAG, "shouldInterceptRequest: 使用本地缓存图片：" + url + "，本地：" + ImageCache.getCachePath(url));
            return new WebResourceResponse("image/png", "utf-8", is);
        }
    }

    @Nullable
    private InputStream getLocalImageInputStream(String url) {
        InputStream is = null;
        boolean isImageUrl = Util.isImageUrl(url);
        if (!isImageUrl) {
            return null; // 如果不是图片url，则为默认处理方式
        }
        boolean loaded = ImageCache.getCachePath(url) != null;
        boolean loading = false;
        if (loading) {
            // TODO 暂时不加载图片，但后面要补上
        } else {
            if (loaded) {
                // 获取本地图片
                String path = ImageCache.getCachePath(url);
                try {
                    is = new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return is;
    }
}
