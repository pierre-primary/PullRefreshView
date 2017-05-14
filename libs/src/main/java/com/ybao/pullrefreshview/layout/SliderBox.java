package com.ybao.pullrefreshview.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ybao.pullrefreshview.support.impl.VPullable;
import com.ybao.pullrefreshview.support.utils.VCanPullUtil;

/**
 * 其他附带控件的父控件
 * 处理事件传递
 * Created by Ybao on 17/1/16.
 */

public class SliderBox extends FrameLayout implements VPullable, NestedScrollingChild, NestedScrollingParent {
    private VPullable mPullable;
    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;

    public SliderBox(Context context) {
        super(context);
        init();
    }

    public SliderBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SliderBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SliderBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        VPullable pullable;
        if (mPullable == null && (pullable = VCanPullUtil.getPullAble(child)) != null) {
            mPullable = pullable;
        }
        super.addView(child, index, params);
    }

    @Override
    public boolean canOverEnd() {
        if (mPullable != null) {
            return mPullable.canOverEnd();
        }
        return false;
    }

    @Override
    public boolean canOverStart() {
        if (mPullable != null) {
            return mPullable.canOverStart();
        }
        return false;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void scrollAViewBy(int dp) {
        if (mPullable != null) {
            mPullable.scrollAViewBy(dp);
        }
    }

    /*****************************************************************************************/

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (!isNestedScrollingEnabled()) {
            setNestedScrollingEnabled(true);
        }
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(View target) {
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        dispatchNestedScroll(0, dyConsumed, 0, dyUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        dispatchNestedPreScroll(0, dy, consumed, null);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mChildHelper.stopNestedScroll();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mChildHelper.onDetachedFromWindow();
    }
}
