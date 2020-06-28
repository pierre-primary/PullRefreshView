package com.ybao.pullrefreshview.simple.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ybao.pullrefreshview.layout.BaseLoadView;
import com.ybao.pullrefreshview.layout.BaseRefreshView;
import com.ybao.pullrefreshview.simple.Config;
import com.ybao.pullrefreshview.simple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 16/7/25.
 */
public class Fragment0 extends Fragment implements BaseRefreshView.OnRefreshListener, BaseLoadView.OnLoadListener {
    View view;

    ListView listView;
    BaseRefreshView refreshView;
    BaseLoadView footerView;

    ArrayAdapter adapter;

    List<String> list = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment0, container, false);
        listView = findViewById(R.id.list);
        refreshView = findViewById(R.id.header);
        footerView = findViewById(R.id.footer);

        list = getData(Config.DataSize);

        adapter = new ArrayAdapter(getContext(), R.layout.item, list);

        listView.setAdapter(adapter);

        refreshView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
        return view;
    }

    @Override
    public void onRefresh(BaseRefreshView baseRefreshView) {
        baseRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                List<String> datas = getData(Config.DataSize);
                list.clear();
                list.addAll(datas);
                adapter.notifyDataSetChanged();
                refreshView.stopRefresh();
            }
        }, 3000);
    }

    @Override
    public void onLoad(BaseLoadView baseFooterView) {
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                List<String> datas = getData(Config.DataSize);
                list.addAll(datas);
                adapter.notifyDataSetChanged();
                footerView.stopLoad();
            }
        }, 3000);
    }


    int page = 1;

    private List<String> getData(int n) {
        List<String> datas = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            datas.add("第" + page + "页,第" + i + "条");
        }
        return datas;
    }

    public <T> T findViewById(int id) {
        return (T) view.findViewById(id);

    }
}