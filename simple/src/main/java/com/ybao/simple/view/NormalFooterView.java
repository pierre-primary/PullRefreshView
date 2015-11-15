package com.ybao.simple.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.layout.BaseFooterView;
import com.ybao.simple.R;
import com.ybao.simple.utils.AnimUtil;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class NormalFooterView extends BaseFooterView {

    TextView textView;
    View tagImg;
    View progress;
    View stateImg;


    public NormalFooterView(Context context) {
        this(context, null);
    }

    public NormalFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NormalFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_footer_normal, this, true);
        textView = (TextView) findViewById(R.id.text);
        tagImg = findViewById(R.id.tag);
        progress = findViewById(R.id.progress);
        stateImg = findViewById(R.id.state);
    }


    @Override
    protected void onStateChange(int state) {
        stateImg.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        tagImg.setVisibility(View.VISIBLE);
        ViewHelper.setAlpha(textView, 1);
        ViewHelper.setAlpha(tagImg, 1);
        switch (state) {
            case NONE:
                break;
            case PULLING:
                textView.setText("上拉加载更多");
                AnimUtil.startRotation(tagImg, 0);
                break;
            case LOOSENT_O_LOAD:
                textView.setText("松开加载");
                AnimUtil.startRotation(tagImg, 180);
                break;
            case LOADING:
                textView.setText("正在加载");
                AnimUtil.startShow(progress, 0.1f, 400, 200);
                AnimUtil.startHide(textView);
                AnimUtil.startHide(tagImg);
                break;
            case LOAD_CLONE:
                AnimUtil.startScale(stateImg, 0.3f, 1f, 500, 50, new OvershootInterpolator());
                AnimUtil.startShow(stateImg, 0.1f, 300, 150);
                AnimUtil.startHide(progress, 150, 0);
                textView.setVisibility(View.INVISIBLE);
                tagImg.setVisibility(View.INVISIBLE);
                textView.setText("加载完成");
                break;

        }

    }

    @Override
    public int getSpanHeight() {
        return getHeight();
    }
}
