package com.ybao.pullrefreshview.support.event;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.ybao.pullrefreshview.layout.FlingLayout;

public abstract class EventHelper implements IEventHelper {
    protected final FlingLayout mFlingLayout;
    protected final FlingLayout.ScrollHelper mScrollHelper;

    protected boolean isScrolling = false;
    protected float velocity = 0;
    protected float downY, downX;
    private float tepmX;
    private float tepmY;
    private int mPointerId;

    protected VelocityTracker mVelocityTracker;

    public EventHelper(FlingLayout flingLayout, FlingLayout.ScrollHelper mScrollHelper) {
        this.mFlingLayout = flingLayout;
        this.mScrollHelper = mScrollHelper;
    }

    @Override
    public boolean isScrolling() {
        return isScrolling;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScrollHelper.startScroll();
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
                float offset = mScrollHelper.getOffset();
                if (offset != 0) {
                    return true;
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
                boolean dps = dispatchScroll(ev, (int) (mx - tepmX), (int) (my - tepmY));
                tepmX = mx;
                tepmY = my;
                if (dps) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                createVelocity(ev);
            case MotionEvent.ACTION_CANCEL:
                mScrollHelper.stopScroll();
                isScrolling = false;
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
        return isScrolling;
    }

    protected abstract boolean dispatchScroll(MotionEvent ev, int dx, int dy);

    @Override
    public boolean touchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
        }
        return isScrolling;
    }

    protected void createVelocity(MotionEvent ev) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float yvelocity = mVelocityTracker.getYVelocity();
        float xvelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xvelocity) > Math.abs(yvelocity)) {
            velocity = 0;
        } else {
            velocity = yvelocity;
        }
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
}
