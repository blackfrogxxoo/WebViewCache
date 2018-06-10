package com.example.black.webviewcache;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageCache {
    private static final String TAG = "ImageCache";
    public static String getCachePath(String url) {
        File file = new File(Constant.IMAGE_CACHE_PATH, Util.md5(url));
        if(file.exists()) {
            return file.getPath();
        }
        return null;
    }

    public static void loadImageToCache(String imageurl){
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            writeToCacheFile(imageurl, inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeToCacheFile(String url, InputStream remoteIs) {
        File file = new File(Constant.IMAGE_CACHE_PATH, Util.md5(url));
        if(!file.getParentFile().exists() || !file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        if(file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream os = null;
        try {
            os=new FileOutputStream(file);
            byte buffer[]=new byte[4 * 1024];
            int len = 0;
            while((len = remoteIs.read(buffer)) != -1){
                os.write(buffer,0,len);
            }
            os.flush();

            Log.i(TAG, "成功保存图片： " + url);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                os.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
