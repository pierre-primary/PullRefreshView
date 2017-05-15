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

    protected boolean isScrolling = false;

    protected float velocity = 0;
    protected float downY, downX;
    protected float tepmX;
    protected float tepmY;
    int mPointerId;

    protected VelocityTracker mVelocityTracker;

    protected NestedScrollingParentHelper mParentHelper;
    protected NestedScrollingChildHelper mChildHelper;

    public EventResolver(FlingLayout.FlingLayoutContext flingLayoutContext) {
        this.c = flingLayoutContext;
        mParentHelper = new NestedScrollingParentHelper(c.getFlingLayout());
        mChildHelper = new NestedScrollingChildHelper(c.getFlingLayout());
    }

    protected void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    protected void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    protected abstract void createVelocity(MotionEvent ev);

    @Override
    public float getVelocity() {
        return velocity;
    }

    @Override
    public boolean isScrolling() {
        return isScrolling;
    }

    protected abstract boolean tryToMove(MotionEvent ev, float oldX, float oldY, float x, float y);

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        c.setScrollState(FlingLayout.SCROLL_STATE_TOUCH_SCROLL);
        acquireVelocityTracker(ev);
        int pointerCount = ev.getPointerCount();
        int pointerIndex = ev.getActionIndex();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPointerId = ev.getPointerId(pointerIndex);
                float x = ev.getX(pointerIndex);
                float y = ev.getY(pointerIndex);
                tepmY = downY = y;
                tepmX = downX = x;
                if (!isNestedScrollingEnabled()) {
                    float moveP = c.getMoveP();
                    if (moveP != 0) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointerId = ev.getPointerId(pointerIndex);
                tepmX = ev.getX(pointerIndex);
                tepmY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mPointerId);
                float mx;
                float my;
                if (pointerCount > pointerIndex && pointerIndex >= 0) {
                    mx = ev.getX(pointerIndex);
                    my = ev.getY(pointerIndex);
                } else {
                    mx = ev.getX();
                    my = ev.getY();
                }
                //意图分析，避免误操作
                float _tepmX = tepmX;
                float _tepmY = tepmY;
                tepmX = mx;
                tepmY = my;
                if (!isNestedScrollingEnabled()) {
                    if (tryToMove(ev, _tepmX, _tepmY, mx, my)) {
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                createVelocity(ev);
            case MotionEvent.ACTION_CANCEL:
                if (!isNestedScrollingEnabled()) {
                    c.startRelease();
                    isScrolling = false;
                }
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 获取离开屏幕的手指的索引
                int pointerIdLeave = ev.getPointerId(pointerIndex);
                if (mPointerId == pointerIdLeave) {
                    // 离开屏幕的正是目前的有效手指，此处需要重新调整，并且需要重置VelocityTracker
                    int reIndex = pointerIndex == 0 ? 1 : 0;
                    mPointerId = ev.getPointerId(reIndex);
                    // 调整触摸位置，防止出现跳动
                    tepmX = ev.getX(reIndex);
                    tepmY = ev.getY(reIndex);
                }
        }
        return c.superDispatchTouchEvent(ev) || isScrolling;
    }

    @Override
    public boolean interceptTouchEvent(MotionEvent ev) {
        return c.superInterceptTouchEvent(ev);
    }

    @Override
    public boolean touchEvent(MotionEvent ev) {
        if (isNestedScrollingEnabled()) {
            return c.superTouchEvent(ev);
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
            }
            return isScrolling;
        }
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
