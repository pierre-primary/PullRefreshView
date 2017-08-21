package com.ybao.pullrefreshview.simple.activities;

import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.*;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
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

    int n = 0;

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        boolean handled = false;
        List<Point> list = new ArrayList<>();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);

            final LayoutParams lp = (LayoutParams) view.getLayoutParams();

            final Behavior viewBehavior = lp.getBehavior();
            if (viewBehavior instanceof AppBarLayout.ScrollingViewBehavior) {
                int x = ((AppBarLayout.ScrollingViewBehavior) viewBehavior).getLeftAndRightOffset();
                int y = ((AppBarLayout.ScrollingViewBehavior) viewBehavior).getTopAndBottomOffset();
                Point point = new Point();
                point.x = x;
                point.y = y;
                list.add(point);
            }
        }
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        for (int i = 0; i < childCount; i++) {
            final View view = getChildAt(i);

            final LayoutParams lp = (LayoutParams) view.getLayoutParams();

            final Behavior viewBehavior = lp.getBehavior();
            if (viewBehavior instanceof AppBarLayout.ScrollingViewBehavior) {
                int x = ((AppBarLayout.ScrollingViewBehavior) viewBehavior).getLeftAndRightOffset();
                int y = ((AppBarLayout.ScrollingViewBehavior) viewBehavior).getTopAndBottomOffset();

                Point point = list.get(i);
                if (point.x != x || point.y != y) {
                    handled = true;
                    break;
                }
            }
        }
        if (!handled) {
            n++;
            if (n > 100) {
                n = 0;
                dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
            }
        }
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

    @Override
    public int getNestedScrollAxes() {
        return super.getNestedScrollAxes();
    }
}
