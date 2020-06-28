package com.ybao.pullrefreshview.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.ybao.pullrefreshview.R;
import com.ybao.pullrefreshview.support.pullable.IPullableHelper;
import com.ybao.pullrefreshview.support.pullable.Pullable;
import com.ybao.pullrefreshview.support.pullable.PullableHorizontalHelper;
import com.ybao.pullrefreshview.support.pullable.PullableVerticalHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 其他附带控件的父控件
 * 处理事件传递
 * Created by Ybao on 17/1/16.
 */

public class PullableBox extends FrameLayout implements NestedScrollingChild, NestedScrollingParent, Pullable {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    protected int orientation = VERTICAL;

    private Pullable mPullable;
    private NestedScrollingParentHelper mParentHelper;
    private NestedScrollingChildHelper mChildHelper;
    private IPullableHelper pullableHelper;
    private int Axes;

    public PullableBox(Context context) {
        super(context);
        init();
        initAttrs(context, null);
    }

    public PullableBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context, attrs);
    }

    public PullableBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullableBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        initAttrs(context, attrs);
    }

    private void init() {
        mParentHelper = new NestedScrollingParentHelper(this);
        mChildHelper = new NestedScrollingChildHelper(this);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        int orientation = VERTICAL;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Pullable);
            orientation = ta.getInt(R.styleable.Pullable_orientation, VERTICAL);
            ta.recycle();
        }
        setOrientation(orientation);
    }

    public void setOrientation(@OrientationMode int orientation) {
        this.orientation = orientation;
        if (orientation == HORIZONTAL) {
            pullableHelper = new PullableHorizontalHelper();
            Axes = ViewCompat.SCROLL_AXIS_HORIZONTAL;
        } else if (orientation == VERTICAL) {
            pullableHelper = new PullableVerticalHelper();
            Axes = ViewCompat.SCROLL_AXIS_VERTICAL;
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Pullable pullable = pullableHelper.getPullAble(child);
        if (pullable != null) {
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
    public void scrollBy(int dp) {
        if (mPullable != null) {
            mPullable.scrollBy(dp);
        }
    }

    /*****************************************************************************************/

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (!isNestedScrollingEnabled()) {
            setNestedScrollingEnabled(true);
        }
        return (nestedScrollAxes & Axes) == Axes;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, null);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        dispatchNestedPreScroll(dx, dy, consumed, null);
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
