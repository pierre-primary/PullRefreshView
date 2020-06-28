package com.ybao.pullrefreshview.simple.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ybao.pullrefreshview.layout.BaseLoadView;
import com.ybao.pullrefreshview.layout.BaseRefreshView;
import com.ybao.pullrefreshview.simple.R;

/**
 * Created by Ybao on 16/7/25.
 */
public class Fragment2 extends Fragment implements BaseRefreshView.OnRefreshListener, BaseLoadView.OnLoadListener {
    View view;

    BaseRefreshView refreshView;
    BaseLoadView footerView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment2, container, false);

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