package com.ybao.pullrefreshview.support.resolver;

import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ybao on 2017/5/14.
 */

public interface IEventResolver extends NestedScrollingChild, NestedScrollingParent {

    void setViewTranslationP(View view, float value);

    boolean dispatchTouchEvent(MotionEvent ev);

    boolean interceptTouchEvent(MotionEvent ev);

    boolean touchEvent(MotionEvent ev);

    void dispatchVelocity(MotionEvent ev);

    float getVelocity();

    void onDetachedFromWindow();
}
