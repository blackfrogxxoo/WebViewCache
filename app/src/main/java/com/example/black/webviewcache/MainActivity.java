package com.example.black.webviewcache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int lastFirstCompletelyVisibleItemPosition;
    private int lastLastCompletelyVisibleItemPosition;
    private Adapter adapter;

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
            public void onProgress(String url, int progress) {
                int count = recyclerView.getChildCount();
                for(int i = 0; i < count; i++) {
                    View v = recyclerView.getChildAt(i);
                    Adapter.Holder holder = (Adapter.Holder) v.getTag();
                    if(holder != null && url.equals(UrlRepo.getUrls().get(holder.getAdapterPosition()))) {
                        holder.progressBar.setProgress(progress);
                    }
                }
            }

            @Override
            public void onTitle(String url, String title) {
                int count = recyclerView.getChildCount();
                for(int i = 0; i < count; i++) {
                    View v = recyclerView.getChildAt(i);
                    Adapter.Holder holder = (Adapter.Holder) v.getTag();
                    if(holder != null && url.equals(UrlRepo.getUrls().get(holder.getAdapterPosition()))) {
                        holder.tvTitle.setText(title);
                    }
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int fcvip = layoutManager.findFirstCompletelyVisibleItemPosition();
                int lcvip = layoutManager.findLastCompletelyVisibleItemPosition();
                onCompletelyVisibleItemsChanged(fcvip, lcvip);
                int fvip = layoutManager.findFirstVisibleItemPosition();
                int lvip = layoutManager.findLastVisibleItemPosition();
                onVisibleItemsChanged(fvip, lvip);
            }
        });

    }

    /**
     * 加载中的转为不可见，则不再加载
     * @param fvip
     * @param lvip
     */
    private void onVisibleItemsChanged(int fvip, int lvip) {

    }

    /**
     * 新的可见item，三秒后如果仍然完全可见，则开始加载
     * @param fcvip
     * @param lcvip
     */
    private void onCompletelyVisibleItemsChanged(int fcvip, int lcvip) {
        lastFirstCompletelyVisibleItemPosition = fcvip;
        lastLastCompletelyVisibleItemPosition = lcvip;
        for(int i = fcvip; i < lcvip; i++) {
            WebViewManager.INSTANCE.startLoad(UrlRepo.getUrls().get(i));
        }
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
    }

}
