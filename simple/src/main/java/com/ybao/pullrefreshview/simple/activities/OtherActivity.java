package com.ybao.pullrefreshview.simple.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ybao.pullrefreshview.simple.R;
import com.ybao.pullrefreshview.simple.activities.other.HeaderActivity;
import com.ybao.pullrefreshview.simple.activities.other.MIFlingActivity;


/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtherActivity.this, MIFlingActivity.class));
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OtherActivity.this, HeaderActivity.class));
            }
        });
    }
}
