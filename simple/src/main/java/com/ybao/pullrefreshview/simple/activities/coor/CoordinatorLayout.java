package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.support.design.widget.*;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Y-bao on 2017/8/21 0021.
 */

public class CoordinatorLayout extends android.support.design.widget.CoordinatorLayout implements NestedScrollingChild {
    NestedScrollingChildHelper mNestedScrollingChildHelper;

    public CoordinatorLayout(Context context) {
        this(context, null);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        setNestedScrollingEnabled(true);
    }


    /*******************************************/

    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    /********************************/

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
        stopNestedScroll();
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        dispatchNestedPreScroll(dx, dy, consumed, null);
        dx -= consumed[0];
        dy -= consumed[1];

        int[] parentConsumed = new int[2];
        super.onNestedPreScroll(target, dx, dy, parentConsumed);
        consumed[0] += parentConsumed[0];
        consumed[1] += parentConsumed[1];
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (super.onNestedFling(target, velocityX, velocityY, consumed)) {
            return true;
        }
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (dispatchNestedPreFling(velocityX, velocityY)) {
            return true;
        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    HashMap<RecyclerView, RecyclerView.OnScrollListener> onScrollListenerHashMap = new HashMap<>();

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = ((RecyclerView) child);
            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    CoordinatorLayout.this.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    CoordinatorLayout.this.onScrollStateChanged(recyclerView, newState);
                }
            };
            recyclerView.addOnScrollListener(onScrollListener);
            onScrollListenerHashMap.put(recyclerView, onScrollListener);
        }
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                continue;
            }
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            final android.support.design.widget.CoordinatorLayout.Behavior behavior = lp.getBehavior();
            if (behavior instanceof Behavior) {
                ((Behavior) behavior).onScrolled(CoordinatorLayout.this, view, recyclerView, dx, dy);
            }
        }
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                continue;
            }
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
            final android.support.design.widget.CoordinatorLayout.Behavior behavior = lp.getBehavior();
            if (behavior instanceof Behavior) {
                ((Behavior) behavior).onScrollStateChanged(CoordinatorLayout.this, view, recyclerView, newState);
            }
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof RecyclerView) {
            RecyclerView recyclerView = ((RecyclerView) child);
            if (onScrollListenerHashMap.containsKey(recyclerView)) {
                recyclerView.removeOnScrollListener(onScrollListenerHashMap.remove(recyclerView));
            }
        }
    }

    public static class Behavior<T extends View> extends android.support.design.widget.CoordinatorLayout.Behavior<T> {
        public Behavior() {
            super();
        }

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void onScrolled(android.support.design.widget.CoordinatorLayout coordinatorLayout, View child, T t, int dx, int dy) {
        }

        public void onScrollStateChanged(android.support.design.widget.CoordinatorLayout coordinatorLayout, View child, T t, int newState) {
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return super.getNestedScrollAxes();
    }
}
