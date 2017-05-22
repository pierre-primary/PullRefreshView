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

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.ybao.pullrefreshview.R;
import com.ybao.pullrefreshview.support.anim.AnimGetter;
import com.ybao.pullrefreshview.support.anim.AnimListener;
import com.ybao.pullrefreshview.support.impl.Pullable;
import com.ybao.pullrefreshview.support.overscroll.OverScrollController;
import com.ybao.pullrefreshview.support.resolver.FlingXResolver;
import com.ybao.pullrefreshview.support.resolver.FlingYResolver;
import com.ybao.pullrefreshview.support.resolver.IEventResolver;
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

    public final static int SCROLL_STATE_IDLE = 0;
    public final static int SCROLL_STATE_TOUCH_SCROLL = 1;
    public final static int SCROLL_STATE_FLING = 2;
    public final static int SCROLL_STATE_OVER_SCROLL = 3;
    protected int stateType = SCROLL_STATE_IDLE;

    protected Pullable pullable;
    private int mTouchSlop;
    protected static final int MAX_DURATION = 600;
    protected static final int MIN_DURATION = 300;
    private int maxOverScrollDist;
    private boolean canOverEnd = true;
    private boolean canOverStart = true;
    private boolean canOverScroll = true;
    protected OnScrollListener mOnScrollListener;
    protected int maxDistance = 0;
    protected int version;
    protected int MAXDISTANCE = 0;

    private boolean isDisallowIntercept = false;

    float moveP = 0;
    FlingLayoutContext flingLayoutContext;
    IEventResolver eventResolver;
    AnimGetter animGetter;
    OverScrollController overScrollController;

    Animator animator;


    public Pullable getPullable() {
        return pullable;
    }

    public FlingLayout(Context context) {
        this(context, null);
    }

    public FlingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        initAttrs(context, attrs);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        maxOverScrollDist = mTouchSlop * 10;
        version = android.os.Build.VERSION.SDK_INT;
        flingLayoutContext = new FlingLayoutContext();
        animGetter = new AnimGetter();
        overScrollController = new OverScrollController(flingLayoutContext);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        int orientation = VERTICAL;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlingLayout);
            orientation = ta.getInt(R.styleable.FlingLayout_orientation, VERTICAL);
            ta.recycle();
        }
        setOrientation(orientation);
    }

    public void setOrientation(@OrientationMode int orientation) {
        this.orientation = orientation;
        if (orientation == HORIZONTAL) {
            MAXDISTANCE = Utils.getScreenWidth(getContext()) * 3 / 5;
            eventResolver = new FlingXResolver(flingLayoutContext);
        } else if (orientation == VERTICAL) {
            MAXDISTANCE = Utils.getScreenHeight(getContext()) * 3 / 5;
            eventResolver = new FlingYResolver(flingLayoutContext);
        }
    }

    private boolean canOverEnd() {
        if (pullable != null) {
            return canOverEnd && pullable.canOverEnd();
        }
        return canOverEnd;
    }

    private boolean canOverStart() {
        if (pullable != null) {
            return canOverStart && pullable.canOverStart();
        }
        return canOverStart;
    }

    public int getMaxDistance() {
        return maxDistance > 0 ? maxDistance : MAXDISTANCE;
    }

    private void moveTo(float p) {
        setMoveP(p);
        Log.i("flingLayout", "moveP:" + p);
        boolean intercept = onScroll(p);
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, p);
        }
        if (!intercept) {
            eventResolver.setViewTranslationP(pullable.getView(), p);
        }
    }

    private void setScrollState(int stateType) {
        if (this.stateType != stateType) {
            if (this.stateType == SCROLL_STATE_OVER_SCROLL && getMoveP() != 0) {
                return;
            }
            if (stateType != SCROLL_STATE_FLING) {
                overScrollController.removeOverScrollListener();
            }
            this.stateType = stateType;
            Log.i("flingLayout", "onScrollChange:" + stateType);
            onScrollChange(stateType);
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollChange(this, stateType);
            }
        }
    }

    private void moveBy(float dp) {
        moveTo(getMoveP() + dp);
    }

    private void setMoveP(float moveP) {
        this.moveP = moveP;
    }

    public float getMoveP() {
        return moveP;
    }

    public int startMoveBy(int startDelay, AnimListener animListener, float startP, float dp) {
        return startMoveTo(startDelay, animListener, startP, startP + dp);
    }

    public int startMoveTo(int startDelay, AnimListener animListener, float startP, float endP) {
        int duration = (int) Math.abs(endP - startP);
        int time = Math.min(MAX_DURATION, duration);
        time = Math.max(MIN_DURATION, time);
        startAnim(startDelay, SCROLL_STATE_FLING, time, new AccelerateDecelerateInterpolator(), animListener, startP, endP);
        return time;
    }

    private void startAnim(int startDelay, final int state, int time, Interpolator interpolator, final AnimListener animListener, float... p) {
        stopAnim();
        setScrollState(state);
        animator = animGetter.createMoveToAnim(startDelay, time, interpolator, new AnimListener() {
            @Override
            public void onUpdate(float value) {
                moveTo(value);
                ViewCompat.postInvalidateOnAnimation(FlingLayout.this);
                if (animListener != null) {
                    animListener.onUpdate(value);
                }
            }

            @Override
            public void onAnimEnd() {
                if (stateType == state) {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                if (animListener != null) {
                    animListener.onAnimEnd();
                }
            }

            @Override
            public void onAnimCencel() {
                if (stateType == state) {
                    setScrollState(SCROLL_STATE_IDLE);
                }
                if (animListener != null) {
                    animListener.onAnimCencel();
                }
            }
        }, p);
        animator.start();
    }

    public void stopAnim() {
        if (animator != null) {
            animator.cancel();
        }
        animator = null;
    }


    private void startRelease() {
        float nowP = getMoveP();
        if (nowP != 0) {
            if (!onStartrRelease(nowP)) {
                startMoveTo(0, null, nowP, 0);
            }
        } else if (canOverScroll && pullable != null && !pullable.canOverStart() && !pullable.canOverEnd()) {
            overScrollController.addOverScrollListener();
        } else {
            setScrollState(SCROLL_STATE_IDLE);
        }
    }

    public void setPullView(Pullable pullable) {
        this.pullable = eventResolver.getPullAble(pullable);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        Pullable newPullable = eventResolver.getPullAble(child);
        if (newPullable != null) {
            pullable = newPullable;
        }
        super.addView(child, index, params);
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }


    public void setCanOverStart(boolean canOverStart) {
        this.canOverStart = canOverStart;
        if (!canOverStart && getMoveP() > 0) {
            moveTo(0);
        }
    }

    public void setCanOverEnd(boolean canOverEnd) {
        this.canOverEnd = canOverEnd;
        if (!canOverEnd && getMoveP() < 0) {
            moveTo(0);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        isDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    /******************************************************************/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            isDisallowIntercept = false;
        } else if (isDisallowIntercept && !eventResolver.isScrolling()) {
            return super.dispatchTouchEvent(ev);
        }
        stopAnim();
        return eventResolver.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return eventResolver.interceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return eventResolver.touchEvent(ev);
    }

    /******************************************************************/

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return eventResolver.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        eventResolver.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        eventResolver.onStopNestedScroll(target);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        eventResolver.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        eventResolver.onNestedPreScroll(target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return eventResolver.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return eventResolver.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        return eventResolver.getNestedScrollAxes();
    }


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        eventResolver.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return eventResolver.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return eventResolver.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        eventResolver.stopNestedScroll();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return eventResolver.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return eventResolver.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return eventResolver.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return eventResolver.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        eventResolver.onDetachedFromWindow();
    }

    /******************************************************************/

    public class FlingLayoutContext {

        public FlingLayout getFlingLayout() {
            return FlingLayout.this;
        }

        public Pullable getPullable() {
            return pullable;
        }

        public boolean superDispatchTouchEvent(MotionEvent ev) {
            return FlingLayout.super.dispatchTouchEvent(ev);
        }

        public boolean superInterceptTouchEvent(MotionEvent ev) {
            return FlingLayout.super.onInterceptTouchEvent(ev);
        }

        public boolean superTouchEvent(MotionEvent ev) {
            return FlingLayout.super.onTouchEvent(ev);
        }

        public int getTouchSlop() {
            return mTouchSlop;
        }

        public void startRelease() {
            FlingLayout.this.startRelease();
        }

        public boolean canOverStart() {
            return FlingLayout.this.canOverStart();
        }

        public boolean canOverEnd() {
            return FlingLayout.this.canOverEnd();
        }

        public void setScrollState(int stateType) {
            FlingLayout.this.setScrollState(stateType);
        }

        public int getMaxDuration() {
            return MAX_DURATION;
        }

        public int getMinDuration() {
            return MIN_DURATION;
        }

        public int getMaxDistance() {
            return FlingLayout.this.getMaxDistance();
        }

        public void moveTo(float p) {
            FlingLayout.this.moveTo(p);
        }

        public void moveBy(float dp) {
            FlingLayout.this.moveBy(dp);
        }

        public float getMoveP() {
            return FlingLayout.this.getMoveP();
        }

        public void startAnim(int startDelay, int state, int time, Interpolator interpolator, AnimListener animListener, float... p) {
            FlingLayout.this.startAnim(startDelay, state, time, interpolator, animListener, p);
        }

        public int getMaxOverScrollDist() {
            return maxOverScrollDist;
        }

        public float getVelocity() {
            return eventResolver.getVelocity();
        }
    }

    /********************************/

    protected boolean isMyChild(View view) {
        return view.getParent() == this;
    }

    protected boolean onScroll(float y) {
        return false;
    }

    protected boolean onStartrRelease(float nowY) {
        return false;
    }

    protected void onScrollChange(int stateType) {
    }

    public interface OnScrollListener {
        void onScroll(FlingLayout flingLayout, float y);

        void onScrollChange(FlingLayout flingLayout, int state);

    }

    public void setOnScrollListener(OnScrollListener mOnScrollListener) {
        this.mOnScrollListener = mOnScrollListener;
    }
}