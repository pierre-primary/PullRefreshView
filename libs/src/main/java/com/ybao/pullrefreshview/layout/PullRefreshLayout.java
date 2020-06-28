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
 * Author：Ybao on 2015/11/5 ‏‎17:53
 * <p>
 * QQ: 392579823
 * <p>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.ybao.pullrefreshview.support.anim.AnimListener;
import com.ybao.pullrefreshview.support.impl.Loadable;
import com.ybao.pullrefreshview.support.impl.Refreshable;
import com.ybao.pullrefreshview.support.utils.Utils;


/**
 * 经典下拉刷新，上拉加载的通用控件（可用于任意控件 如 ListView GridView WebView ScrollView）
 * <p>
 * 弹性下（上）拉，滑倒顶（低）部无需松开即可继续拉动
 *
 * @author Ybao
 */
public class PullRefreshLayout extends FlingLayout {

    protected Refreshable mRefresher;
    protected Loadable mLoader;
    protected boolean hasRefresher = true;
    protected boolean hasLoader = true;
    protected boolean isRefreshing = false;
    protected boolean isLoading = false;

    public PullRefreshLayout(Context context) {
        this(context, null);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    @Override
    protected boolean onScroll(float y) {
        if (mRefresher != null && isShowRefreshView() && y >= 0) {
            boolean intercept = mRefresher.onScroll(y);
            if (y != 0) {
                return intercept;
            }
        }
        if (mLoader != null && isShowLoadView() && y <= 0) {
            boolean intercept = mLoader.onScroll(y);
            if (y != 0) {
                return intercept;
            }
        }
        return false;
    }

    @Override
    protected void onScrollChange(int scrollState) {
        if (mRefresher != null && isShowRefreshView()) {
            mRefresher.onScrollChange(scrollState);
        }
        if (mLoader != null && isShowLoadView()) {
            mLoader.onScrollChange(scrollState);
        }

    }

    @Override
    protected boolean onStartRelease(float nowY) {
        if (mRefresher != null && nowY > 0 && isShowRefreshView()) {
            return mRefresher.onStartRelease(nowY);
        } else if (mLoader != null && nowY < 0 && isShowLoadView()) {
            return mLoader.onStartRelease(nowY);
        }
        return false;
    }

    public void setRefresher(Refreshable refresher) {
        if (mRefresher != null && isMyChild((View) mRefresher)) {
            removeView((View) mRefresher);
        }
        this.mRefresher = refresher;
        mRefresher.setPullRefreshLayout(this);
    }

    public void setLoader(Loadable loader) {
        if (mLoader != null && isMyChild((View) mLoader)) {
            removeView((View) mLoader);
        }
        this.mLoader = loader;
        mLoader.setPullRefreshLayout(this);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof Refreshable) {
            setRefresher((Refreshable) child);
        } else if (child instanceof Loadable) {
            setLoader((Loadable) child);
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getHeight();
        if (mRefresher != null && isMyChild((View) mRefresher)) {
            View mRefreshView = (View) mRefresher;
            mRefreshView.layout(mRefreshView.getLeft(), -mRefreshView.getMeasuredHeight(), mRefreshView.getRight(), 0);
        }
        if (mLoader != null && isMyChild((View) mLoader)) {
            View mLoaderView = (View) mLoader;
            mLoaderView.layout(mLoaderView.getLeft(), height, mLoaderView.getRight(), height + mLoaderView.getMeasuredHeight());
        }
    }

    private boolean isShowRefreshView() {
        return hasRefresher && scrollState != SCROLL_STATE_OVER_SCROLL;
    }

    private boolean isShowLoadView() {
        return hasLoader && scrollState != SCROLL_STATE_OVER_SCROLL;
    }

    void setRefreshing(boolean refresh) {
        isRefreshing = refresh;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    Animator disHeaderAnim;

    public void setHasHeader(boolean hasRefresher) {
        this.hasRefresher = hasRefresher;
        float startP = getOffset();
        if (!hasRefresher && startP < 0 && disHeaderAnim == null) {
            mRefresher.onScrollChange(SCROLL_STATE_FLING);
            disHeaderAnim = disEnableByAnim(new AnimListener() {
                @Override
                public void onUpdate(float value) {
                    if (mRefresher != null) {
                        mRefresher.onScroll(value);
                    }
                }

                @Override
                public void onAnimEnd() {
                    if (mRefresher != null) {
                        mRefresher.onScrollChange(SCROLL_STATE_IDLE);
                    }
                    disHeaderAnim = null;
                }

                @Override
                public void onAnimCencel() {
                    if (mRefresher != null) {
                        mRefresher.onScrollChange(SCROLL_STATE_IDLE);
                    }
                    disHeaderAnim = null;
                }
            }, startP, 0);
            disHeaderAnim.start();
        } else if (hasRefresher && disHeaderAnim != null) {
            disHeaderAnim.cancel();
            disHeaderAnim = null;
        }
    }

    private Animator disEnableByAnim(final AnimListener animListener, float startP, float endP) {
        int duration = (int) Math.abs(endP - startP);
        int time = Math.min(Utils.MAX_DURATION, duration);
        time = Math.max(Utils.MIN_DURATION, time);
        return animGetter.createMoveToAnim(0, time, new DecelerateInterpolator(), animListener, startP, endP);
    }

    public boolean isHasHeader() {
        return hasRefresher;
    }


    Animator disFooterAnim;

    public void setHasFooter(boolean hasLoader) {
        this.hasLoader = hasLoader;
        float startP = getOffset();
        if (!hasLoader && startP < 0 && disFooterAnim == null) {
            mLoader.onScrollChange(SCROLL_STATE_FLING);
            disFooterAnim = disEnableByAnim(new AnimListener() {
                @Override
                public void onUpdate(float value) {
                    if (mLoader != null) {
                        mLoader.onScroll(value);
                    }
                }

                @Override
                public void onAnimEnd() {
                    if (mLoader != null) {
                        mLoader.onScrollChange(SCROLL_STATE_IDLE);
                    }
                    disFooterAnim = null;
                }

                @Override
                public void onAnimCencel() {
                    if (mLoader != null) {
                        mLoader.onScrollChange(SCROLL_STATE_IDLE);
                    }
                    disFooterAnim = null;
                }
            }, startP, 0);
            disFooterAnim.start();
        } else if (hasLoader && disFooterAnim != null) {
            disFooterAnim.cancel();
            disFooterAnim = null;
        }
    }

    public boolean isHasFooter() {
        return hasLoader;
    }
}