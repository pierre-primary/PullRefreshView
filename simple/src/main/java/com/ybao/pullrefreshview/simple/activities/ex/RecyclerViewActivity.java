package com.ybao.pullrefreshview.simple.activities.ex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ybao.adapter.recyclerview.StandardAdapter;
import com.ybao.pullrefreshview.layout.BaseLoadView;
import com.ybao.pullrefreshview.layout.BaseRefreshView;
import com.ybao.pullrefreshview.simple.Config;
import com.ybao.pullrefreshview.simple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class RecyclerViewActivity extends AppCompatActivity implements BaseRefreshView.OnRefreshListener, BaseLoadView.OnLoadListener {

    RecyclerView recyclerView;
    BaseRefreshView refreshView;
    BaseLoadView footerView;

    RecyclerViewAdapter adapter;

    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        refreshView = (BaseRefreshView) findViewById(R.id.header);
        footerView = (BaseLoadView) findViewById(R.id.footer);

        list = getData(Config.DataSize);

        adapter = new RecyclerViewAdapter();
        adapter.setData(list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        refreshView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
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

