package com.ybao.pullrefreshview.simple.activities.ex;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ybao.pullrefreshview.layout.BaseFooterView;
import com.ybao.pullrefreshview.layout.BaseHeaderView;
import com.ybao.pullrefreshview.simple.R;

/**
 * Created by Ybao on 2015/11/7 0007.
 */
public class TextViewActivity extends AppCompatActivity implements BaseHeaderView.OnRefreshListener, BaseFooterView.OnLoadListener {

    BaseHeaderView headerView;
    BaseFooterView footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textview);

        headerView = (BaseHeaderView) findViewById(R.id.header);
        footerView = (BaseFooterView) findViewById(R.id.footer);


        headerView.setOnRefreshListener(this);
        footerView.setOnLoadListener(this);
    }


    @Override
    public void onRefresh(BaseHeaderView baseHeaderView) {
        baseHeaderView.postDelayed(new Runnable() {
            @Override
            public void run() {
                headerView.stopRefresh();
            }
        }, 3000);
    }

    @Override
    public void onLoad(BaseFooterView baseFooterView) {
        baseFooterView.postDelayed(new Runnable() {
            @Override
            public void run() {
                footerView.stopLoad();
            }
        }, 3000);
    }
}
