/**
 * Copyright 2015 Pengyuan-Jiang
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Author：Ybao on 2015/11/7 ‏‎0:27
 * <p>
 * QQ: 392579823
 * <p>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.layout;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.ybao.pullrefreshview.support.anim.AnimListener;
import com.ybao.pullrefreshview.support.impl.OnEndListener;
import com.ybao.pullrefreshview.support.impl.Pullable;
import com.ybao.pullrefreshview.support.impl.Refreshable;
import com.ybao.pullrefreshview.support.type.LayoutType;

public abstract class BaseHeaderView extends RelativeLayout implements Refreshable {

    public final static int NONE = 0;
    public final static int PULLING = 1;
    public final static int LOOSENT_O_REFRESH = 2;
    public final static int REFRESHING = 3;
    public final static int REFRESH_CLONE = 4;
    private int stateType = NONE;

    private PullRefreshLayout pullRefreshLayout;

    private boolean isLockState = false;

    OnRefreshListener onRefreshListener;

    private int scrollState = FlingLayout.SCROLL_STATE_IDLE;

    public BaseHeaderView(Context context) {
        this(context, null);
    }

    public BaseHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    protected boolean isLockState() {
        return isLockState;
    }

    public int getLayoutType() {
        return LayoutType.LAYOUT_NORMAL;
    }


    private void setState(int state) {
        if (isLockState || stateType == state) {
            return;
        }
        Log.i("BaseHeaderView", "" + state);
        this.stateType = state;
        if (state == REFRESHING) {
            isLockState = true;
            pullRefreshLayout.setRefreshing(true);
            if (onRefreshListener != null) {
                onRefreshListener.onRefresh(this);
            }
        } else {
            pullRefreshLayout.setRefreshing(false);
        }
        onStateChange(state);
    }

    public int getType() {
        return stateType;
    }

    @Override
    public void setPullRefreshLayout(PullRefreshLayout pullRefreshLayout) {
        this.pullRefreshLayout = pullRefreshLayout;
    }

    @Override
    public void startRefresh() {
        startRefresh(0);
    }

    public void startRefresh(final int d) {
        int h = getMeasuredHeight();
        if (h > 0) {
            toShowAndRefresh(d);
        } else {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    toShowAndRefresh(d);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }

    }

    private void toShowAndRefresh(int d) {
        if (pullRefreshLayout != null) {
            float moveY = pullRefreshLayout.getMoveP();
            if (moveY == 0) {
                float headerSpanHeight = getSpanHeight();
                pullRefreshLayout.startMoveTo(d, new AnimListener() {
                    @Override
                    public void onUpdate(float value) {

                    }

                    @Override
                    public void onAnimEnd() {
                        setState(REFRESHING);
                    }

                    @Override
                    public void onAnimCencel() {

                    }
                }, 0, headerSpanHeight);
            }
        }
    }

    @Override
    public void stopRefresh() {
        stopRefresh(null);
    }

    public void stopRefresh(OnEndListener onEndListener) {
        isLockState = false;
        setState(REFRESH_CLONE);
        close(400, onEndListener);
    }

    private void close(int startDelay, final OnEndListener onEndListener) {
        if (this.pullRefreshLayout != null) {
            float moveY = pullRefreshLayout.getMoveP();
            if (moveY > 0) {
                pullRefreshLayout.startMoveTo(startDelay, new AnimListener() {
                    @Override
                    public void onUpdate(float value) {

                    }

                    @Override
                    public void onAnimEnd() {
                        setState(NONE);
                        if (onEndListener != null) {
                            onEndListener.onEnd();
                        }
                    }

                    @Override
                    public void onAnimCencel() {
                        setState(NONE);
                    }
                }, moveY, 0);
            } else {
                setState(NONE);
            }
        }
    }

    @Override
    public boolean onScroll(float y) {
        boolean intercept = false;
        int layoutType = getLayoutType();
        if (layoutType == LayoutType.LAYOUT_SCROLLER) {
            ViewCompat.setTranslationY(this, getMeasuredHeight());
        } else if (layoutType == LayoutType.LAYOUT_DRAWER) {
            ViewCompat.setTranslationY(this, y);
            Pullable pullable = pullRefreshLayout.getPullable();
            if (pullable != null) {
                ViewCompat.setTranslationY(pullable.getView(), 0);
            }
            intercept = true;
        } else if (layoutType == LayoutType.LAYOUT_NOT_MOVE) {
            ViewCompat.setTranslationY(this, 0);
        } else {
            ViewCompat.setTranslationY(this, y);
        }
        float headerSpanHeight = getSpanHeight();
        if (scrollState == FlingLayout.SCROLL_STATE_TOUCH_SCROLL) {
            if (y >= headerSpanHeight) {
                setState(LOOSENT_O_REFRESH);
            } else {
                setState(PULLING);
            }
        }
        return intercept;
    }

    @Override
    public void onScrollChange(int state) {
        scrollState = state;
    }

    @Override
    public boolean onStartrRelease(float nowY) {
        float headerSpanHeight = getSpanHeight();
        if (nowY >= headerSpanHeight) {
            pullRefreshLayout.startMoveTo(0, null, nowY, headerSpanHeight);
            setState(REFRESHING);
            return true;
        }
        pullRefreshLayout.startMoveTo(0, null, nowY, 0);
        setState(NONE);
        return false;
    }

    public abstract float getSpanHeight();

    protected abstract void onStateChange(int state);

    public interface OnRefreshListener {
        void onRefresh(BaseHeaderView baseHeaderView);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

}
