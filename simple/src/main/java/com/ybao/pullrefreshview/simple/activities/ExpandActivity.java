package com.ybao.pullrefreshview.simple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ybao.pullrefreshview.simple.R;
import com.ybao.pullrefreshview.simple.activities.ep.DrawerEpActivity;
import com.ybao.pullrefreshview.simple.activities.ep.NormolEpActivity;
import com.ybao.pullrefreshview.simple.activities.ep.RG2Activity;
import com.ybao.pullrefreshview.simple.activities.ep.RGActivity;
import com.ybao.pullrefreshview.simple.activities.ep.ScrollerEpActivity;


/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class ExpandActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandActivity.this, NormolEpActivity.class));
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandActivity.this, DrawerEpActivity.class));
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandActivity.this, ScrollerEpActivity.class));
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandActivity.this, RGActivity.class));
            }
        });
        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ExpandActivity.this, RG2Activity.class));
            }
        });
    }
}
