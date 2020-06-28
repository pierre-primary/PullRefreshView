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

import androidx.core.view.ViewCompat;

import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.support.anim.AnimListener;
import com.ybao.pullrefreshview.support.impl.Loadable;
import com.ybao.pullrefreshview.support.impl.OnEndListener;
import com.ybao.pullrefreshview.support.impl.Pullable;
import com.ybao.pullrefreshview.support.type.LayoutType;


public abstract class BaseFooterView extends RelativeLayout implements Loadable {

    public final static int NONE = 0;
    public final static int PULLING = 1;
    public final static int LOOSENT_O_LOAD = 2;
    public final static int LOADING = 3;
    public final static int LOAD_CLONE = 4;
    public final static int LOAD_ClOSE = 5;
    private int footerState = NONE;

    private PullRefreshLayout pullRefreshLayout;

    private boolean isLockState = false;

    private OnLoadListener onLoadListener;

    private int waitStartLoad = -1;

    private int scrollState = FlingLayout.SCROLL_STATE_IDLE;

    public BaseFooterView(Context context) {
        this(context, null);
    }

    public BaseFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    public int getLayoutType() {
        return LayoutType.LAYOUT_NORMAL;
    }

    protected void setState(int state) {
        if (isLockState || footerState == state) {
            return;
        }
        if (state != LOAD_ClOSE && (footerState == LOADING && state != LOAD_CLONE)) {
            return;
        }
        Log.i("BaseFooterView", "" + state);
        this.footerState = state;
        if (state == LOAD_ClOSE) {
            isLockState = true;
        } else if (state == LOADING) {
            pullRefreshLayout.setLoading(true);
            if (onLoadListener != null) {
                onLoadListener.onLoad(this);
            }
        } else {
            pullRefreshLayout.setLoading(false);
        }
        onStateChange(state);
    }


    public int getState() {
        return footerState;
    }


    @Override
    public void setPullRefreshLayout(PullRefreshLayout pullRefreshLayout) {
        this.pullRefreshLayout = pullRefreshLayout;
    }


    @Override
    public void startLoad() {
        startLoad(0);
    }

    @Override
    public void startLoad(int startDelay) {
        int h = getMeasuredHeight();
        if (h > 0) {
            toShowAndLoad(startDelay);
        } else {
            waitStartLoad = Math.max(startDelay, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (waitStartLoad >= 0) {
            toShowAndLoad(waitStartLoad);
            waitStartLoad = -1;
        }
    }

    private void toShowAndLoad(int startDelay) {
        if (this.pullRefreshLayout != null && footerState != LOADING) {
            float moveY = pullRefreshLayout.getMoveP();
            float footerSpanHeight = getSpanHeight();
            setState(LOADING);
            pullRefreshLayout.startMoveTo(startDelay, null, moveY, -footerSpanHeight);
        }
    }

    @Override
    public void stopLoad() {
        stopLoad(null);
    }

    @Override
    public void stopLoad(OnEndListener onEndListener) {
        setState(LOAD_CLONE);
        close(400, onEndListener);
    }

    @Override
    public void closeState() {
        setState(LOAD_ClOSE);
    }

    @Override
    public void reSetCloseState() {
        isLockState = false;
        setState(NONE);
    }

    private void close(int startDelay, final OnEndListener onEndListener) {
        if (this.pullRefreshLayout != null) {
            final float moveY = pullRefreshLayout.getMoveP();
            if (moveY < 0) {
                pullRefreshLayout.startMoveTo(startDelay, new AnimListener() {
                    float value = moveY;

                    @Override
                    public void onUpdate(float n) {
                        float newValue = pullRefreshLayout.getMoveP();
                        Pullable pullable = pullRefreshLayout.getPullable();
                        if (pullable != null) {
                            pullable.scrollAViewBy((int) (newValue - value));
                        }
                        value = newValue;
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
        int footerLayoutType = getLayoutType();
        if (footerLayoutType == LayoutType.LAYOUT_SCROLLER) {
            ViewHelper.setTranslationY(this, -getMeasuredHeight());
        } else if (footerLayoutType == LayoutType.LAYOUT_DRAWER) {
            ViewHelper.setTranslationY(this, y);
            Pullable pullable = pullRefreshLayout.getPullable();
            if (pullable != null) {
                ViewCompat.setTranslationY(pullable.getView(), 0);
            }
            intercept = true;
        } else if (footerLayoutType == LayoutType.LAYOUT_NOT_MOVE) {
            ViewHelper.setTranslationY(this, 0);
        } else {
            ViewHelper.setTranslationY(this, y);
        }
        float footerSpanHeight = getSpanHeight();
        if (scrollState == FlingLayout.SCROLL_STATE_TOUCH_SCROLL) {
            if (y <= -footerSpanHeight) {
                setState(LOOSENT_O_LOAD);
            } else {
                setState(PULLING);
            }
        }
        return intercept;
    }

    @Override
    public void onScrollChange(int state) {
        if (footerState != LOADING && footerState != LOAD_ClOSE) {
            scrollState = state;
        }
    }

    @Override
    public boolean onStartRelease(float nowY) {
        float footerSpanHeight = getSpanHeight();
        if (footerState != LOADING && footerState != LOAD_ClOSE && nowY <= -footerSpanHeight) {
            pullRefreshLayout.startMoveTo(0, null, nowY, -footerSpanHeight);
            setState(LOADING);
            return true;
        }
        pullRefreshLayout.startMoveTo(0, null, nowY, 0);
        setState(NONE);
        return true;
    }

    public abstract float getSpanHeight();

    protected abstract void onStateChange(int state);

    public interface OnLoadListener {
        void onLoad(BaseFooterView baseFooterView);
    }

    public void setOnLoadListener(OnLoadListener onRefreshListener) {
        this.onLoadListener = onRefreshListener;
    }
}

