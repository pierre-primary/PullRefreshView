package com.ybao.pullrefreshview.simple.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ybao.pullrefreshview.simple.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class FlingActivity extends AppCompatActivity {

    ListView listView;

    ArrayAdapter adapter;

    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fling);
        listView = (ListView) findViewById(R.id.list);
        list = getData(15);
        adapter = new ArrayAdapter(this, R.layout.item, list);

        listView.setAdapter(adapter);
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
