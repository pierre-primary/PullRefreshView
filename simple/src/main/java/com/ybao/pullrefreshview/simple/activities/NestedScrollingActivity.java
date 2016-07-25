package com.ybao.pullrefreshview.simple.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ybao.pullrefreshview.simple.R;
import com.ybao.pullrefreshview.simple.fragment.Fragment0;
import com.ybao.pullrefreshview.simple.fragment.Fragment2;
import com.ybao.pullrefreshview.simple.fragment.Fragment3;
import com.ybao.pullrefreshview.simple.fragment.Fragment1;
import com.ybao.pullrefreshview.simple.fragment.Fragment4;

import java.util.ArrayList;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class NestedScrollingActivity extends AppCompatActivity {
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nestedscrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new Fragment0());
        fragments.add(new Fragment1());
        fragments.add(new Fragment2());
        fragments.add(new Fragment3());
        fragments.add(new Fragment4());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(myFragmentPagerAdapter);
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;
        ArrayList<String> strings;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
            strings = new ArrayList<>();
            strings.add("ListView");
            strings.add("RecyclerView");
            strings.add("ScrollView");
            strings.add("NestedScrollView");
            strings.add("WebView");
        }


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= strings.size()) {
                return "第" + position + "个";
            }
            return strings.get(position);
        }
    }
}
