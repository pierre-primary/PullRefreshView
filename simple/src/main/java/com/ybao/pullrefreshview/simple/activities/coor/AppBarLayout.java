package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.support.utils.Utils;


/**
 * Created by Y-bao on 2017/8/21 0021.
 */
@CoordinatorLayout.DefaultBehavior(AppBarLayout.Behavior.class)
public class AppBarLayout extends LinearLayout {
    public AppBarLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public AppBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof android.support.design.widget.AppBarLayout.LayoutParams;
    }

    @Override
    protected android.support.design.widget.AppBarLayout.LayoutParams generateDefaultLayoutParams() {
        return new android.support.design.widget.AppBarLayout.LayoutParams(android.support.design.widget.AppBarLayout.LayoutParams.MATCH_PARENT, android.support.design.widget.AppBarLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public android.support.design.widget.AppBarLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new android.support.design.widget.AppBarLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.support.design.widget.AppBarLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (Build.VERSION.SDK_INT >= 19 && p instanceof LinearLayout.LayoutParams) {
            return new android.support.design.widget.AppBarLayout.LayoutParams((LinearLayout.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new android.support.design.widget.AppBarLayout.LayoutParams((MarginLayoutParams) p);
        }
        return new android.support.design.widget.AppBarLayout.LayoutParams(p);
    }

    int hh1 = 0;
    int hh2 = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = 0;
        for (int i = 0, n = getChildCount(); i < n; i++) {
            View view = getChildAt(i);
            android.support.design.widget.AppBarLayout.LayoutParams lp = (android.support.design.widget.AppBarLayout.LayoutParams) view.getLayoutParams();
            int scrollFlags = lp.getScrollFlags();
            if ((scrollFlags & android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS) != 0) {
                hh1 = height;
            } else if ((scrollFlags & android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                hh2 = height;
            }
            height += view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
    }

    public void scroll(int y, int dy) {
        int mt = -(int) ViewHelper.getTranslationY(this);
        int t = y;
        if (y > 0 && mt < y - dy) {
            t = mt;
            t += dy;
            if (t < hh1) {
                t = hh1;
            }
        }
        if (t > hh2) {
            t = hh2;
        }
        mt = t;
        ViewHelper.setTranslationY(this, -mt);
    }

    public static class ScrollingViewBehavior extends CoordinatorLayout.Behavior {

        public ScrollingViewBehavior() {
        }

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            final int childLpHeight = child.getLayoutParams().height;
            if (childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT || childLpHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {

                int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                if (availableHeight == 0) {
                    availableHeight = parent.getHeight();
                }

                final int height = availableHeight;
                final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, childLpHeight == ViewGroup.LayoutParams.MATCH_PARENT
                        ? View.MeasureSpec.EXACTLY
                        : View.MeasureSpec.AT_MOST);
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                return true;
            }
            return false;
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
            final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            final Rect available = new Rect();
            available.set(parent.getPaddingLeft() + lp.leftMargin, parent.getPaddingTop() + lp.topMargin,
                    parent.getWidth() - parent.getPaddingRight() - lp.rightMargin,
                    parent.getHeight() - parent.getPaddingBottom() - lp.bottomMargin);

            final Rect out = new Rect();

            GravityCompat.apply(resolveGravity(lp.gravity), child.getMeasuredWidth(),
                    child.getMeasuredHeight(), available, out, layoutDirection);

            child.layout(out.left, out.top, out.right, out.bottom);

            final View header = findFirstDependency(parent);

            if (header != null) {
                if (child instanceof ViewGroup) {
                    child.setPadding(0, header.getMeasuredHeight(), 0, 0);
                    ((ViewGroup) child).setClipToPadding(false);
                    ((RecyclerView) child).scrollToPosition(0);
                    ((RecyclerView) child).scrollBy(0, -header.getMeasuredHeight());
                }
            }
            return false;
        }

        AppBarLayout findFirstDependency(ViewGroup parent) {
            for (int i = 0, z = parent.getChildCount(); i < z; i++) {
                View view = parent.getChildAt(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout) view;
                }
            }
            return null;
        }

        private static int resolveGravity(int gravity) {
            return gravity == Gravity.NO_GRAVITY ? GravityCompat.START | Gravity.TOP : gravity;
        }
    }

    static class Behavior extends com.ybao.pullrefreshview.simple.activities.coor.CoordinatorLayout.Behavior {

        int y = 0;

        @Override
        public void onScrolled(CoordinatorLayout coordinatorLayout, View child, View view, int dx, int dy) {
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.ViewHolder topViewHolder = recyclerView.findViewHolderForAdapterPosition(0);
            if (topViewHolder == null) {
                y += dy;
            } else {
                y = -topViewHolder.itemView.getTop();
            }
            AppBarLayout appBarLayout = (AppBarLayout) child;
            appBarLayout.scroll(y + child.getMeasuredHeight(), dy);
        }

        @Override
        public void onScrollStateChanged(CoordinatorLayout coordinatorLayout, View child, View view, int newState) {
            AppBarLayout appBarLayout = (AppBarLayout) child;
        }
    }
}
