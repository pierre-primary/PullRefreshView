package com.ybao.pullrefreshview.simple.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ybao.adapter.recyclerview.StandardAdapter;
import com.ybao.pullrefreshview.layout.BaseLoadView;
import com.ybao.pullrefreshview.layout.BaseRefreshView;
import com.ybao.pullrefreshview.simple.Config;
import com.ybao.pullrefreshview.simple.R;
import com.ybao.pullrefreshview.simple.activities.coor.RecyclerPartnerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class NormalRefreshActivity extends AppCompatActivity implements BaseRefreshView.OnRefreshListener, BaseLoadView.OnLoadListener {

    RecyclerPartnerView recyclerView;
    BaseRefreshView refreshView;
    BaseLoadView footerView;

    RecyclerViewAdapter adapter;

    List<String> list = new ArrayList<String>();
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_refresh);


        recyclerView = (RecyclerPartnerView) findViewById(R.id.list);
        refreshView = (BaseRefreshView) findViewById(R.id.header);
        footerView = (BaseLoadView) findViewById(R.id.footer);
        ((com.ybao.pullrefreshview.simple.activities.coor.FlingAppBarLayout) findViewById(R.id.AppBarLayout)).setPartner(recyclerView);

        list = getData(Config.DataSize);

        adapter = new RecyclerViewAdapter();
        adapter.setData(list);
        adapter.setItemClickListener(new StandardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                recyclerView.scrollToPosition(position);
            }
        });
        linearLayoutManager = new LinearLayoutManager(NormalRefreshActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        refreshView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
        refreshView.startRefresh();
    }

    @Override
    public void onRefresh(BaseRefreshView baseRefreshView) {
        baseRefreshView.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                List<String> datas = getData(Config.DataSize);
                recyclerView.backToTop();
                list.clear();
                list.addAll(datas);
                adapter.setData(list);
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

    class RecyclerViewAdapter extends StandardAdapter {
        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            ((TextView) holder.itemView).setText(getItem(position).toString());

        }
    }
}

