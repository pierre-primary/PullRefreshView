package com.ybao.pullrefreshview.simple.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.layout.BaseFooterView;
import com.ybao.pullrefreshview.simple.R;
import com.ybao.pullrefreshview.simple.utils.AnimUtil;
import com.ybao.pullrefreshview.support.type.LayoutType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ybao on 2015/11/3 0003.
 */
public class EndFooterView extends BaseFooterView {

    TextView textView;
    View tagImg;
    View progress;
    View stateImg;


    public EndFooterView(Context context) {
        this(context, null);
    }

    public EndFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_footer_end, this, true);
        textView = (TextView) findViewById(R.id.text);
        tagImg = findViewById(R.id.tag);
        progress = findViewById(R.id.progress);
        stateImg = findViewById(R.id.state);
    }

    List<Animator> animators = new ArrayList<>();

    @Override
    protected void onStateChange(int state) {
        for (Animator animator : animators) {
            animator.cancel();
        }
        animators.clear();
        stateImg.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.VISIBLE);
        tagImg.setVisibility(View.VISIBLE);
        ViewHelper.setAlpha(textView, 1);
        ViewHelper.setAlpha(tagImg, 1);
        switch (state) {
            case NONE:
            case PULLING:
                textView.setText("上拉加载更多");
                animators.add(AnimUtil.startRotation(tagImg, 0));
                break;
            case LOOSENT_O_LOAD:
                textView.setText("松开加载");
                animators.add(AnimUtil.startRotation(tagImg, 180));
                break;
            case LOADING:
                textView.setText("正在加载");
                animators.add(AnimUtil.startShow(progress, 0.1f, 400, 200));
                animators.add(AnimUtil.startHide(textView));
                animators.add(AnimUtil.startHide(tagImg));
                break;
            case LOAD_CLONE:
                animators.add(AnimUtil.startScale(stateImg, 0.3f, 1f, 500, 50, new OvershootInterpolator()));
                animators.add(AnimUtil.startShow(stateImg, 0.1f, 300, 150));
                animators.add(AnimUtil.startHide(progress, 150, 0));
                textView.setVisibility(View.INVISIBLE);
                tagImg.setVisibility(View.INVISIBLE);
                textView.setText("加载完成");
                break;

        }

    }

    @Override
    public float getSpanHeight() {
        return 0;
    }

    @Override
    public int getLayoutType() {
        return LayoutType.LAYOUT_NOT_MOVE;
    }
}
