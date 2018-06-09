package com.example.black.webviewcache;

import java.util.ArrayList;
import java.util.List;

public class UrlRepo {
    private static List<String> urls;
    static {
        urls = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            switch (i % 3) {
                case 0:
                    urls.add("https://github.com");
                    break;
                case 1:
                    urls.add("https://www.sogou.com");
                    break;
                case 2:
                    urls.add("https://cn.bing.com/");
                    break;
                default:
                    break;
            }

        }
    }
    public static List<String> getUrls() {
        return urls;
    }
}
