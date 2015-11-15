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
 * Author：Ybao on 2015/11/7 ‏‎0:27
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ybao.pullrefreshview.utils.Loadable;


public abstract class BaseFooterView extends RelativeLayout implements Loadable {

    public final static int NONE = 0;
    public final static int PULLING = 1;
    public final static int LOOSENT_O_LOAD = 2;
    public final static int LOADING = 3;
    public final static int LOAD_CLONE = 4;
    private int stateType = NONE;

    PullRefreshLayout refreshLayout;
    protected boolean isLockState = false;

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

    @Override
    public void onScroll(FlingLayout flingLayout, int y) {
        if (isLockState) {
            return;
        }
        if (y < getSpanHeight() && stateType != PULLING) {
            setState(PULLING);
        } else if (y > getSpanHeight() && stateType != LOOSENT_O_LOAD) {
            setState(LOOSENT_O_LOAD);
        }
    }

    @Override
    public void setPullRefreshLayout(PullRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    @Override
    public void onScrollChange(FlingLayout flingLayout, int state, int y) {
        if (state != FlingLayout.FLING) {
            return;
        }
        if (y == getSpanHeight() && !isLockState) {
            isLockState = true;
            setState(LOADING);
        } else if (y == 0 && isLockState) {
        }
    }

    public void stopLoad() {
        setState(LOAD_CLONE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(NONE);
                if (refreshLayout != null) {
                    refreshLayout.closeFooter();
                }
                isLockState = false;
            }
        }, 800);
    }

    private void setState(int state) {
        stateType = state;
        if (state == LOADING && onRefreshListener != null) {
            onRefreshListener.onLoad(this);
        }
        onStateChange(state);
    }

    public int getType() {
        return stateType;
    }

    protected abstract void onStateChange(int state);

    OnLoadListener onRefreshListener;

    public interface OnLoadListener {
        void onLoad(BaseFooterView baseFooterView);
    }

    public void setOnLoadListener(OnLoadListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}

