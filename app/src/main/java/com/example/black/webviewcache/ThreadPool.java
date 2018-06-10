package com.example.black.webviewcache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

    static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }
}
