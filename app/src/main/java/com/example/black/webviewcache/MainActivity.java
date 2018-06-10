package com.example.black.webviewcache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Adapter adapter;
    private Timer loadTimer;
    private boolean activityResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(this);
        WebViewManager.INSTANCE.setUrlLoadCallback(new WebViewManager.UrlLoadCallback() {
            @Override
            public void onLoad(String url, boolean isLoading, String title, int progress) {
                int count = recyclerView.getChildCount();
                for(int i = 0; i < count; i++) {
                    View v = recyclerView.getChildAt(i);
                    Adapter.Holder holder = (Adapter.Holder) v.getTag();
                    if(holder != null && url.equals(UrlRepo.getUrls().get(holder.getAdapterPosition()))) {
                        holder.progressBar.setProgress(progress);
                        holder.tvTitle.setText(title);
                        holder.progressBar.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
                        holder.tvLoading.setText(isLoading ? "loading" : (progress == 100 ? "finished" : ""));
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);

        loadTimer = new Timer("LoadTimer");
        loadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(activityResumed) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int fcvip = layoutManager.findFirstCompletelyVisibleItemPosition();
                            int lcvip = layoutManager.findLastCompletelyVisibleItemPosition();
                            int fvip = layoutManager.findFirstVisibleItemPosition();
                            int lvip = layoutManager.findLastVisibleItemPosition();
                            onVisibleItemsChanged(fvip, lvip, fcvip, lcvip);
                            WebViewManager.INSTANCE.startLoadAll();
                        }
                    });

                }
            }
        }, 100, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityResumed = false;
    }

    /**
     * 加载中的转为不可见，则不再加载
     * @param fvip
     * @param lvip
     */
    private void onVisibleItemsChanged(int fvip, int lvip, int fcvip, int lcvip) {
        List<String> visibleUrls = new ArrayList<>();
        for(int i = fvip; i <= lvip; i ++) {
            String url = UrlRepo.getUrls().get(i);
            if(!visibleUrls.contains(url)) {
                visibleUrls.add(url);
            }
        }

        List<String> completelyVisibleUrls = new ArrayList<>();
        for(int i = fcvip; i <= lcvip; i ++) {
            String url = UrlRepo.getUrls().get(i);
            if(!completelyVisibleUrls.contains(url)) {
                completelyVisibleUrls.add(url);
            }
        }
        WebViewManager.INSTANCE.onVisibleUrls(visibleUrls, completelyVisibleUrls);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_cache:
                WebViewManager.INSTANCE.clearCache();
                File imageCaches = new File(Constant.IMAGE_CACHE_PATH);
                if (imageCaches.exists() && imageCaches.isDirectory()) {
                    for (File file : imageCaches.listFiles()) {
                        file.delete();
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebViewManager.INSTANCE.release();
        loadTimer.cancel();
    }

}
