package com.ybao.pullrefreshview.simple.activities.other;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ybao.pullrefreshview.layout.BaseFooterView;
import com.ybao.pullrefreshview.layout.BaseHeaderView;
import com.ybao.pullrefreshview.layout.FlingLayout;
import com.ybao.pullrefreshview.simple.R;
import com.ybao.pullrefreshview.support.utils.ViewScrollUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 16/7/30.
 */
public class HeaderActivity extends AppCompatActivity implements BaseFooterView.OnLoadListener, AbsListView.OnScrollListener, FlingLayout.OnScrollListener {

    FlingLayout flingLayout;
    ListView listView;
    BaseFooterView footerView;
    View animHeader;

    ArrayAdapter adapter;

    List<String> list = new ArrayList<String>();

    ViewScrollUtil.ScrollGeter scrollGeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);

        flingLayout = (FlingLayout) findViewById(R.id.root);
        listView = (ListView) findViewById(R.id.list);
        footerView = (BaseFooterView) findViewById(R.id.footer);
        animHeader = findViewById(R.id.anim_header);
        scrollGeter = ViewScrollUtil.getScrollGeter(listView);

        list = getData(15);

        adapter = new ArrayAdapter(this, R.layout.item, list);

        listView.setAdapter(adapter);
        flingLayout.setOnScrollListener(this);
        listView.setOnScrollListener(this);
        footerView.setOnLoadListener(this);
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

    @Override
    public void onScroll(FlingLayout flingLayout, float y) {
        onMove();
    }

    @Override
    public void onScrollChange(FlingLayout flingLayout, int state) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        onMove();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    private void onMove() {
        int oy = (int) flingLayout.getMoveP() - scrollGeter.getScrollY();
        if (oy > 0) {
            int imgHeaderHeigth = animHeader.getMeasuredHeight();
            float ph = 1 + (float) oy / (float) imgHeaderHeigth;
            ViewCompat.setPivotY(animHeader, 0);
            ViewCompat.setScaleX(animHeader, ph);
            ViewCompat.setScaleY(animHeader, ph);

        } else {
            ViewCompat.setScaleX(animHeader, 1);
            ViewCompat.setScaleY(animHeader, 1);
            ViewCompat.setTranslationY(animHeader, oy);
        }
    }
}