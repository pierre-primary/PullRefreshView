package com.ybao.pullrefreshview.simple.activities.ex;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ybao.pullrefreshview.layout.BaseLoadView;
import com.ybao.pullrefreshview.layout.BaseRefreshView;
import com.ybao.pullrefreshview.simple.R;

/**
 * Created by Ybao on 2015/11/5 0003.
 */
public class ScrollViewActivity extends AppCompatActivity implements BaseRefreshView.OnRefreshListener, BaseLoadView.OnLoadListener {

    BaseRefreshView refreshView;
    BaseLoadView footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview);

        refreshView = (BaseRefreshView) findViewById(R.id.header);
        footerView = (BaseLoadView) findViewById(R.id.footer);

        refreshView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
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


}
