package com.ybao.pullrefreshview.simple.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ybao.pullrefreshview.layout.BaseLoadView;
import com.ybao.pullrefreshview.layout.BaseRefreshView;
import com.ybao.pullrefreshview.simple.R;

/**
 * Created by Ybao on 16/7/25.
 */
public class Fragment4 extends Fragment implements BaseRefreshView.OnRefreshListener, BaseLoadView.OnLoadListener {
    View view;

    BaseRefreshView refreshView;
    BaseLoadView footerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment4, container, false);
        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url){
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.indexOf("objc://") != -1) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.loadUrl("http://www.baidu.com");
        refreshView = findViewById(R.id.header);
        footerView = findViewById(R.id.footer);

        refreshView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
        return view;
    }


    @Override
    public void onRefresh(BaseRefreshView baseRefreshView) {
        baseRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshView.stopRefresh();
            }
        }, 3000);
    }

    @Override
    public void onLoad(BaseLoadView baseFooterView) {
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                footerView.stopLoad();
            }
        }, 3000);
    }


    public <T> T findViewById(int id) {
        return (T) view.findViewById(id);

    }
}