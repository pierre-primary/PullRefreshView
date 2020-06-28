package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ybao.pullrefreshview.support.impl.VPullable;
import com.ybao.pullrefreshview.support.utils.VCanPullUtil;

/**
 * Created by Y-bao on 2017/8/21 0021.
 */

public class FlingCoordinatorLayout extends CoordinatorLayout implements VPullable, NestedScrollingChild {
    private VPullable mPullable;
    NestedScrollingChildHelper mNestedScrollingChildHelper;

    public FlingCoordinatorLayout(Context context) {
        this(context, null);
    }

    public FlingCoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlingCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        VPullable pullable;
        if (this.mPullable == null && (pullable = VCanPullUtil.getPullAble(child)) != null) {
            this.mPullable = pullable;
        }

        super.addView(child, index, params);
    }

    @Override
    public boolean canOverEnd() {
        return this.mPullable != null ? this.mPullable.canOverEnd() : false;
    }

    @Override
    public boolean canOverStart() {
        return this.mPullable != null ? this.mPullable.canOverStart() : false;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void scrollAViewBy(int dp) {
        if (this.mPullable != null) {
            this.mPullable.scrollAViewBy(dp);
        }
    }

    /*******************************************/

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    /********************************/

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        dispatchNestedPreScroll(dx, dy, consumed, null);
        dx -= consumed[0];
        dy -= consumed[1];

        int[] parentConsumed = new int[2];
        super.onNestedPreScroll(target, dx, dy, parentConsumed);
        consumed[0] += parentConsumed[0];
        consumed[1] += parentConsumed[1];
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (super.onNestedFling(target, velocityX, velocityY, consumed)) {
            return true;
        }
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (dispatchNestedPreFling(velocityX, velocityY)) {
            return true;
        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }
}
