package com.ybao.pullrefreshview.simple.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.ybao.pullrefreshview.layout.FlingLayout;

/**
 * Created by Ybao on 16/7/30.
 */
public class MiFliingLayout extends FlingLayout {
    public MiFliingLayout(Context context) {
        super(context);
    }

    public MiFliingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MiFliingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected boolean onScroll(float y) {
        View view = getPullView();
        int heigth = view.getMeasuredHeight();
        if (y >= 0) {
            ViewCompat.setPivotY(view, 0);
            ViewCompat.setScaleY(view, (heigth + y) / heigth);
        } else {
            ViewCompat.setPivotY(view, heigth);
            ViewCompat.setScaleY(view, (heigth - y) / heigth);
        }
        return true;
    }
}
