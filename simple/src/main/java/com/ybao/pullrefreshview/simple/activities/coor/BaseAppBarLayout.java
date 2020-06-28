package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by ybao on 2017/8/28.
 */

public class BaseAppBarLayout extends LinearLayout implements NestedScrollingChild {
    private int mOffset = 0;
    private Scroller mScroller;
    private FlingRunnable mFlingRunnable;
    NestedScrollingChildHelper mNestedScrollingChildHelper;

    public BaseAppBarLayout(Context context) {
        super(context);
        init();
    }

    public BaseAppBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseAppBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    public void setOffset(int mOffset) {
        if (this.mOffset != mOffset) {
            onOffsetChanged();
        }
        this.mOffset = mOffset;
        ViewHelper.setTranslationY(this, mOffset);
    }

    public int getCurrentOffset() {
        return mOffset;
    }

    protected int getMaxDragOffset() {
        return -getHeight();
    }


    public boolean fliing(int minY, int maxY, float velocityY) {
        stopScroll();
        if (this.mScroller == null) {
            this.mScroller = new Scroller(getContext());
        }
        this.mScroller.fling(0, getCurrentOffset(), 0, Math.round(velocityY), 0, 0, minY, maxY);
        if (this.mScroller.computeScrollOffset()) {
            this.mFlingRunnable = new FlingRunnable();
            this.mFlingRunnable.start();
            return true;
        }
        onFlyFinished();
        return false;
    }

    public void stopScroll() {
        if (this.mFlingRunnable != null) {
            this.mFlingRunnable.stop();
            this.mFlingRunnable = null;
        }
        if (this.mFlingRunnable != null && !this.mScroller.isFinished()) {
            this.mScroller.abortAnimation();
        }
    }

    protected void onFlyFinished() {

    }

    protected void onOffsetChanged() {
    }

    protected int onScroll(int paramInt) {
        return 0;
    }


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

    public static class Behavior<V extends BaseAppBarLayout> extends CoordinatorLayout.Behavior<V> {
        private static final int INVALID_POINTER = -1;

        private boolean mIsBeingDragged;
        private int mActivePointerId = INVALID_POINTER;
        private int mLastMotionY;
        private int mTouchSlop = -1;
        private VelocityTracker mVelocityTracker;

        boolean canDragView(View view) {
            return true;
        }

        private void ensureVelocityTracker() {
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            }
        }

        @Override
        public boolean onInterceptTouchEvent(androidx.coordinatorlayout.widget.CoordinatorLayout parent, V child, MotionEvent ev) {
            if (mTouchSlop < 0) {
                mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
            }

            final int action = ev.getAction();

            if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
                return true;
            }

            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    mIsBeingDragged = false;
                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();
                    if (canDragView(child) && parent.isPointInChildBounds(child, x, y)) {
                        mLastMotionY = y;
                        mActivePointerId = ev.getPointerId(0);
                        ensureVelocityTracker();
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final int activePointerId = mActivePointerId;
                    if (activePointerId == INVALID_POINTER) {
                        break;
                    }
                    final int pointerIndex = ev.findPointerIndex(activePointerId);
                    if (pointerIndex == -1) {
                        break;
                    }

                    final int y = (int) ev.getY(pointerIndex);
                    final int yDiff = Math.abs(y - mLastMotionY);
                    if (yDiff > mTouchSlop) {
                        mIsBeingDragged = true;
                        mLastMotionY = y;
                        child.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    mIsBeingDragged = false;
                    mActivePointerId = INVALID_POINTER;
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
                }
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.addMovement(ev);
            }

            return mIsBeingDragged;
        }

        @Override
        public boolean onTouchEvent(androidx.coordinatorlayout.widget.CoordinatorLayout parent, V child, MotionEvent ev) {
            if (mTouchSlop < 0) {
                mTouchSlop = ViewConfiguration.get(parent.getContext()).getScaledTouchSlop();
            }

            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN: {
                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();

                    if (parent.isPointInChildBounds(child, x, y) && canDragView(child)) {
                        mLastMotionY = y;
                        mActivePointerId = ev.getPointerId(0);
                        ensureVelocityTracker();
                    } else {
                        return false;
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                    if (activePointerIndex == -1) {
                        return false;
                    }

                    final int y = (int) ev.getY(activePointerIndex);
                    int dy = mLastMotionY - y;

                    if (!mIsBeingDragged && Math.abs(dy) > mTouchSlop) {
                        mIsBeingDragged = true;
                        if (dy > 0) {
                            dy -= mTouchSlop;
                        } else {
                            dy += mTouchSlop;
                        }
                    }

                    if (mIsBeingDragged) {
                        mLastMotionY = y;
                        int[] hh = new int[2];
                        child.dispatchNestedPreScroll(0, dy, hh, null);
                        dy -= hh[1];
                        dy -= scroll(parent, child, dy, child.getMaxDragOffset(), 0);
                        child.dispatchNestedScroll(0, 0, 0, dy, null);
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(ev);
                        mVelocityTracker.computeCurrentVelocity(1000);
                        float yvel = mVelocityTracker.getYVelocity(mActivePointerId);
                        if (!child.dispatchNestedPreFling(0, yvel)) {
                            child.dispatchNestedFling(0, yvel, fling(parent, child, child.getMaxDragOffset(), 0, yvel));
                        }
                    }
                case MotionEvent.ACTION_CANCEL: {
                    mIsBeingDragged = false;
                    mActivePointerId = INVALID_POINTER;
                    child.stopNestedScroll();
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                    break;
                }
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.addMovement(ev);
            }

            return true;
        }

        final boolean fling(androidx.coordinatorlayout.widget.CoordinatorLayout paramMc, V paramV, int minY, int maxY, float velocityY) {
            return paramV.fliing(minY, maxY, velocityY);
        }

        final int scroll(androidx.coordinatorlayout.widget.CoordinatorLayout paramMc, V paramV, int dy, int minOffset, int maxOffset) {
            return setHeaderTopBottomOffset(paramMc, paramV, dy, minOffset, maxOffset);
        }

        int setHeaderTopBottomOffset(androidx.coordinatorlayout.widget.CoordinatorLayout paramMc, V paramV, int dy, int minOffset, int maxOffset) {
            return onScroll(dy, paramV);
        }

        static int constrain(int offset, int minOffset, int maxOffset) {
            if (offset < minOffset) {
                return minOffset;
            } else if (offset > maxOffset) {
                return maxOffset;
            }
            return offset;
        }

        protected int onScroll(int dy, V paramV) {
            return paramV.onScroll(dy);
        }
    }

    class FlingRunnable implements Runnable {
        public FlingRunnable start() {
            ViewCompat.postOnAnimation(BaseAppBarLayout.this, this);
            return this;
        }

        public void stop() {
            BaseAppBarLayout.this.removeCallbacks(this);
        }

        @Override
        public void run() {
            if (mScroller != null) {
                if (!mScroller.computeScrollOffset()) {
                    onFlyFinished();
                    return;
                }
                int currentOffset = getCurrentOffset();
                int offset = mScroller.getCurrY();
                onScroll(currentOffset - offset);
                ViewCompat.postOnAnimation(BaseAppBarLayout.this, this);
            }
        }
    }
}
