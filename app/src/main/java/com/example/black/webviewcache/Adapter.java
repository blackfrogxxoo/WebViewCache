package com.example.black.webviewcache;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    private final Context context;

    Adapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_main_list, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final String url = UrlRepo.getUrls().get(position);
        holder.tvUrl.setText(url);
        WebViewWrapper wrapper = WebViewManager.INSTANCE.webViewWrapperMap.get(url);
        if(wrapper != null) {
            holder.progressBar.setProgress(wrapper.getProgress());
            holder.tvTitle.setText(wrapper.getWebView().getTitle());
            holder.progressBar.setVisibility(wrapper.isLoading() ? View.VISIBLE : View.INVISIBLE);
            holder.tvLoading.setText(wrapper.isLoading() ? "loading" : (wrapper.getProgress() == 100 ? "finished" : ""));
        } else {
            holder.progressBar.setProgress(0);
            holder.tvTitle.setText(null);
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.tvLoading.setText("");
            WebViewManager.INSTANCE.addWebView(context, url);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, WebViewActivity.class);
                it.putExtra(WebViewActivity.URL, url);
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return UrlRepo.getUrls().size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView tvUrl;
        TextView tvLoading;
        TextView tvTitle;
        Holder(View itemView) {
            super(itemView);
            itemView.setTag(this);
            progressBar = itemView.findViewById(R.id.progress_bar);
            tvUrl = itemView.findViewById(R.id.tv_url);
            tvLoading = itemView.findViewById(R.id.tv_loading);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
