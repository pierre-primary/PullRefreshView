package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * Created by ybao on 2017/8/28.
 */

public class BaseAppBarLayout extends LinearLayout {
    private int mOffset = 0;
    private ScrollerCompat mScroller;
    private FlingRunnable mFlingRunnable;

    public BaseAppBarLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public BaseAppBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public BaseAppBarLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
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
            this.mScroller = ScrollerCompat.create(getContext());
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

    protected void onScroll(int paramInt) {
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
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
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
        public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent ev) {
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
                        scroll(parent, child, dy, child.getMaxDragOffset(), 0);
                    }
                    break;
                }

                case MotionEvent.ACTION_UP:
                    if (mVelocityTracker != null) {
                        mVelocityTracker.addMovement(ev);
                        mVelocityTracker.computeCurrentVelocity(1000);
                        float yvel = mVelocityTracker.getYVelocity(mActivePointerId);
                        fling(parent, child, child.getMaxDragOffset(), 0, yvel);
                    }
                case MotionEvent.ACTION_CANCEL: {
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

            return true;
        }

        final boolean fling(CoordinatorLayout paramMc, V paramV, int minY, int maxY, float velocityY) {
            return paramV.fliing(minY, maxY, velocityY);
        }

        final int scroll(CoordinatorLayout paramMc, V paramV, int dy, int minOffset, int maxOffset) {
            return setHeaderTopBottomOffset(paramMc, paramV, dy, minOffset, maxOffset);
        }

        int setHeaderTopBottomOffset(CoordinatorLayout paramMc, V paramV, int dy, int minOffset, int maxOffset) {
            int currentOffset = paramV.getCurrentOffset();
            int newOffset = currentOffset - dy;
            newOffset = constrain(newOffset, minOffset, maxOffset);
            dy = currentOffset - newOffset;
            onScroll(dy, paramV);
            return dy;
        }

        static int constrain(int offset, int minOffset, int maxOffset) {
            if (offset < minOffset) {
                return minOffset;
            } else if (offset > maxOffset) {
                return maxOffset;
            }
            return offset;
        }

        protected void onScroll(int dy, V paramV) {
            paramV.onScroll(dy);
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
