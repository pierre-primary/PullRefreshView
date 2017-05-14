package com.ybao.pullrefreshview.support.resolver;

import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParentHelper;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import com.ybao.pullrefreshview.layout.FlingLayout;

/**
 * Created by ybao on 2017/5/14.
 */

public abstract class EventResolver implements IEventResolver {
    protected FlingLayout.FlingLayoutContext c;

    protected float velocity = 0;
    protected VelocityTracker mVelocityTracker;

    protected NestedScrollingParentHelper mParentHelper;
    protected NestedScrollingChildHelper mChildHelper;

    public EventResolver(FlingLayout.FlingLayoutContext flingLayoutContext) {
        this.c = flingLayoutContext;
        mParentHelper = new NestedScrollingParentHelper(c.getFlingLayout());
        mChildHelper = new NestedScrollingChildHelper(c.getFlingLayout());
    }


    @Override
    public void dispatchVelocity(MotionEvent ev) {
        acquireVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                createVelocity();
            case MotionEvent.ACTION_CANCEL:
                releaseVelocityTracker();
        }
    }

    protected abstract void createVelocity();

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    @Override
    public float getVelocity() {
        return velocity;
    }

    @Override
    public boolean interceptTouchEvent(MotionEvent ev) {
        return c.superInterceptTouchEvent(ev);
    }

    /**********************************/

    @Override
    public void onStopNestedScroll(View target) {
        c.startRelease();
        stopNestedScroll();
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public int getNestedScrollAxes() {
        return mParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mChildHelper.hasNestedScrollingParent();
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
        mChildHelper.onDetachedFromWindow();
    }
}
