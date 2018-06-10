package com.example.black.webviewcache;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static App sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
