package com.ybao.pullrefreshview.simple.activities.ep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ybao.pullrefreshview.layout.BaseFooterView;
import com.ybao.pullrefreshview.layout.BaseHeaderView;
import com.ybao.pullrefreshview.simple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class NormolEpActivity extends AppCompatActivity implements BaseHeaderView.OnRefreshListener, BaseFooterView.OnLoadListener {

    ListView listView;
    BaseHeaderView headerView;
    BaseFooterView footerView;

    ArrayAdapter adapter;

    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normol_ep);

        listView = (ListView) findViewById(R.id.list);
        headerView = (BaseHeaderView) findViewById(R.id.header);
        footerView = (BaseFooterView) findViewById(R.id.footer);

        list = getData(15);

        adapter = new ArrayAdapter(this, R.layout.item, list);


        listView.setAdapter(adapter);

        headerView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
    }

    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                page = 1;
                List<String> datas = getData(5);
                list.clear();
                list.addAll(datas);
                adapter.notifyDataSetChanged();
                headerView.stopRefresh();
            }
        }, 3000);
    }

    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                List<String> datas = getData(5);
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
}

