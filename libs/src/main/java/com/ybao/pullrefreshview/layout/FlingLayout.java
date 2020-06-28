/**
 * Copyright 2015 Pengyuan-Jiang
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Author：Ybao on 2015/11/5  ‏‎17:49
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.R;
import com.ybao.pullrefreshview.support.anim.AnimGetter;
import com.ybao.pullrefreshview.support.anim.AnimListener;
import com.ybao.pullrefreshview.support.event.EventHorizontalHelper;
import com.ybao.pullrefreshview.support.event.EventVerticalHelper;
import com.ybao.pullrefreshview.support.event.IEventHelper;
import com.ybao.pullrefreshview.support.nested.INestedHelper;
import com.ybao.pullrefreshview.support.nested.NestedHorizontalHelper;
import com.ybao.pullrefreshview.support.nested.NestedVerticalHelper;
import com.ybao.pullrefreshview.support.overscroll.OverScrollController;
import com.ybao.pullrefreshview.support.pullable.IPullableHelper;
import com.ybao.pullrefreshview.support.pullable.Pullable;
import com.ybao.pullrefreshview.support.pullable.PullableHorizontalHelper;
import com.ybao.pullrefreshview.support.pullable.PullableVerticalHelper;
import com.ybao.pullrefreshview.support.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FlingLayout extends FrameLayout implements NestedScrollingChild, NestedScrollingParent {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    protected int orientation = VERTICAL;

    public final static int SCROLL_STATE_SCROLL = 0x001;
    public final static int SCROLL_STATE_TOUCH = 0x010;
    public final static int SCROLL_STATE_OVER = 0x100;
    /**
     * 滑动状态--静止状态
     */
    public final static int SCROLL_STATE_IDLE = 0x000;
    /**
     * 滑动状态--滑动状态
     */
    public final static int SCROLL_STATE_TOUCH_SCROLL = SCROLL_STATE_TOUCH | SCROLL_STATE_SCROLL;
    /**
     * 滑动状态--自动滚动状态
     */
    public final static int SCROLL_STATE_FLING = SCROLL_STATE_SCROLL;
    /**
     * 滑动状态--越界滚动状态
     */
    public final static int SCROLL_STATE_OVER_SCROLL = SCROLL_STATE_OVER | SCROLL_STATE_SCROLL;

    protected int scrollState = SCROLL_STATE_IDLE;
    /**
     * 滑动主体
     */
    protected Pullable mPullable;
    /**
     * 滑出结束点开关
     */
    private boolean canOverEnd = true;
    /**
     * 滑出起始点开关
     */
    private boolean canOverStart = true;
    /**
     * 越界滑动开关
     */
    private boolean canOverScroll = true;
    protected OnScrollListener mOnScrollListener;
    /**
     * 外部设置最大滑动距离
     */
    private int maxDistance = 0;
    /**
     * 默认最大滑动距离
     */
    private int MAXDISTANCE = 0;

    /**
     * 是否被子控件阻止事件的标识
     */
    private boolean isDisallowIntercept = false;

    /**
     * 当前位置
     */
    private float _offset = 0;


    /**
     * 动画生成器
     */
    protected AnimGetter animGetter;

    /**
     * 内部类隐藏接口
     */
    private ScrollHelper scrollHelper;
    private ScrollOverHelper scrollOverHelper;
    /**
     * 帮助类
     */
    IPullableHelper pullableHelper;
    IEventHelper eventHelper;
    INestedHelper nestedHelper;
    /**
     * 越界滑动控制器
     */
    OverScrollController overScrollController;
    /**
     * 主动画
     */
    Animator animator;

    public FlingLayout(Context context) {
        super(context);
        init(context);
        initAttrs(context, null);
    }

    public FlingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    public FlingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        initAttrs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlingLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        initAttrs(context, attrs);
    }

    private void init(Context context) {
        animGetter = new AnimGetter();
        scrollHelper = new ScrollHelper();
        scrollOverHelper = new ScrollOverHelper();
        overScrollController = new OverScrollController(this, scrollOverHelper);
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
            MAXDISTANCE = Utils.getScreenWidth(getContext()) * 3 / 5;
            pullableHelper = new PullableHorizontalHelper();
            eventHelper = new EventHorizontalHelper(this, scrollHelper);
            nestedHelper = new NestedHorizontalHelper(this, scrollHelper);
        } else if (orientation == VERTICAL) {
            MAXDISTANCE = Utils.getScreenHeight(getContext()) * 3 / 5;
            pullableHelper = new PullableVerticalHelper();
            eventHelper = new EventVerticalHelper(this, scrollHelper);
            nestedHelper = new NestedVerticalHelper(this, scrollHelper);
        }
    }

    /**
     * 判断是否可以滑出结束点
     */
    public boolean canOverEnd() {
        if (mPullable != null) {
            return canOverEnd && mPullable.canOverEnd();
        }
        return canOverEnd;
    }

    /**
     * 判断是否可以滑出开始点
     */
    public boolean canOverStart() {
        if (mPullable != null) {
            return canOverStart && mPullable.canOverStart();
        }
        return canOverStart;
    }

    /**
     * @return 最大滑动距离
     */
    public int getMaxDistance() {
        return maxDistance > 0 ? maxDistance : MAXDISTANCE;
    }

    /**
     * 移到指定距离
     *
     * @param dp 距离
     */
    private void offsetBy(float dp) {
        offsetTo(getOffset() + dp);
    }

    /**
     * 移到指定位置
     *
     * @param offset 位置
     */
    private void offsetTo(float offset) {
        setOffset(offset);
        Log.i("flingLayout", "offset:" + offset);
        boolean intercept = onScroll(offset);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, offset);
        }
        if (!intercept && mPullable != null) {
            ViewHelper.setTranslationY(mPullable.getView(), offset);
//            eventResolver.setViewTranslationP(pullable.getView(), offset);
        }
    }

    private void setOffset(float offset) {
        this._offset = offset;
    }

    public float getOffset() {
        return _offset;
    }

    private void addScrollState(int state) {
        setScrollState(scrollState | state);
    }

    private void removeScrollState(int state) {
        setScrollState(scrollState & ~state);
    }

    /**
     * 设置滑动状态
     *
     * @param scrollState 滑动状态
     */
    private void setScrollState(int scrollState) {
        if (this.scrollState != scrollState) {
            if (this.scrollState == SCROLL_STATE_OVER_SCROLL && getOffset() != 0) {
                return;
            }
            if (scrollState != SCROLL_STATE_FLING) {
                overScrollController.removeOverScrollListener();
            }
            this.scrollState = scrollState;
            Log.i("flingLayout", "onScrollChange:" + scrollState);
            onScrollChange(scrollState);
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollChange(this, scrollState);
            }
        }
    }

    /**
     * 松手fling 到指定距离
     *
     * @param startDelay   动画开始延时
     * @param animListener 插值器
     * @param startP       起始点
     * @param dp           距离
     * @return 动画执行时间
     */
    public int startMoveBy(int startDelay, AnimListener animListener, float startP, float dp) {
        return startMoveTo(startDelay, animListener, startP, startP + dp);
    }

    /**
     * 松手fling 到指定点
     *
     * @param startDelay   动画开始延时
     * @param animListener 插值器
     * @param startP       起始点
     * @param endP         结束点
     * @return 动画执行时间
     */
    public int startMoveTo(int startDelay, AnimListener animListener, float startP, float endP) {
        int duration = (int) Math.abs(endP - startP);
        int time = Math.min(Utils.MAX_DURATION, duration);
        time = Math.max(Utils.MIN_DURATION, time);
        startScrollAnim(startDelay, SCROLL_STATE_FLING, time, new AccelerateDecelerateInterpolator(), animListener, startP, endP);
        return time;
    }

    /**
     * 主动画
     *
     * @param startDelay   动画开始延时
     * @param state        动画启动时的SCROLL状态
     * @param time         动画时间
     * @param interpolator 插值器
     * @param animListener 动画状态回调
     * @param p            关键帧
     */
    private void startScrollAnim(int startDelay, final int state, int time, Interpolator interpolator, final AnimListener animListener, float... p) {
        stopAnim();
        setScrollState(state);
        animator = animGetter.createMoveToAnim(startDelay, time, interpolator, new AnimListener() {
            @Override
            public void onUpdate(float value) {
                offsetTo(value);
                ViewCompat.postInvalidateOnAnimation(FlingLayout.this);
                if (animListener != null) {
                    animListener.onUpdate(value);
                }
            }

            @Override
            public void onAnimEnd() {
                if (scrollState == state) {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                if (animListener != null) {
                    animListener.onAnimEnd();
                }
            }

            @Override
            public void onAnimCencel() {
                if (scrollState == state) {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                if (animListener != null) {
                    animListener.onAnimCencel();
                }
            }
        }, p);
        animator.start();
    }

    /**
     * 停止主动画
     */
    public void stopAnim() {
        if (animator != null) {
            animator.cancel();
        }
        animator = null;
    }


    private void startRelease() {
        float nowOffset = getOffset();
        if (nowOffset != 0) {
            if (!onStartRelease(nowOffset)) {
                startMoveTo(0, null, nowOffset, 0);
            }
        } else if (canOverScroll && mPullable != null && !mPullable.canOverStart() && !mPullable.canOverEnd()) {
            overScrollController.addOverScrollListener();
        } else {
            setScrollState(SCROLL_STATE_IDLE);
        }
    }

    public void setPullView(Pullable pullable) {
        mPullable = pullable;
    }


    public Pullable getPullable() {
        return mPullable;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Pullable newPullable = pullableHelper.getPullAble(child);
        if (newPullable != null) {
            mPullable = newPullable;
        }
        super.addView(child, index, params);
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }


    public void setCanOverStart(boolean canOverStart) {
        this.canOverStart = canOverStart;
        if (!canOverStart && getOffset() > 0) {
            offsetTo(0);
        }
    }

    public void setCanOverEnd(boolean canOverEnd) {
        this.canOverEnd = canOverEnd;
        if (!canOverEnd && getOffset() < 0) {
            offsetTo(0);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        isDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /********************************手势处理**********************************/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isNestedScrollingEnabled()) {
            return super.dispatchTouchEvent(ev);
        }
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {//松手重置
            isDisallowIntercept = false;
        } else if (isDisallowIntercept && !eventHelper.isScrolling()) {//避免拦截，避免子手势中断
            return super.dispatchTouchEvent(ev);
        }
        stopAnim();
        boolean dte = eventHelper.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev) || dte;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isNestedScrollingEnabled()) {
            return super.onTouchEvent(ev);
        }
        return eventHelper.touchEvent(ev);
    }

    /*********************************NestedScrolling*********************************/

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return nestedHelper.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        nestedHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        nestedHelper.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        nestedHelper.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        nestedHelper.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return nestedHelper.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return nestedHelper.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return nestedHelper.getNestedScrollAxes();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedHelper.stopNestedScroll();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return nestedHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        nestedHelper.onDetachedFromWindow();
    }

    /********************************提供外部事件调用**********************************/

    public class ScrollHelper {
        private float velocity = 0;

        public void offsetTo(float offset) {
            FlingLayout.this.offsetTo(offset);
        }

        public void offsetBy(float dp) {
            FlingLayout.this.offsetBy(dp);
        }

        public void startTouchScroll() {
            FlingLayout.this.setScrollState(SCROLL_STATE_TOUCH_SCROLL);
        }

        public void stopTouchScroll() {
            FlingLayout.this.startRelease();
        }

        public void setVelocity(float velocity) {
            this.velocity = velocity;
        }

        public float getVelocity() {
            return velocity;
        }
    }

    public class ScrollOverHelper {

        public void setScrollState(int state) {
            FlingLayout.this.setScrollState(state);
        }

        public void startScrollAnim(int startDelay, final int state, int time, Interpolator interpolator, final AnimListener animListener, float... p) {
            FlingLayout.this.startScrollAnim(startDelay, state, time, interpolator, animListener, p);
        }

        public float getVelocity() {
            return scrollHelper.getVelocity();
        }
    }

    /********************************/

    protected boolean isMyChild(View view) {
        return view.getParent() == this;
    }

    protected boolean onScroll(float y) {
        return false;
    }

    protected boolean onStartRelease(float nowY) {
        return false;
    }

    protected void onScrollChange(int scrollState) {
    }

    public interface OnScrollListener {
        void onScroll(FlingLayout flingLayout, float y);

        void onScrollChange(FlingLayout flingLayout, int state);

    }

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }
}