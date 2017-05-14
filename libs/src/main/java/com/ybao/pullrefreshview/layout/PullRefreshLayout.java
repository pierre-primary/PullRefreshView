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
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ybao.pullrefreshview.support.impl.Loadable;
import com.ybao.pullrefreshview.support.impl.Refreshable;
import com.ybao.pullrefreshview.support.type.LayoutType;


/**
 * 经典下拉刷新，上拉加载的通用控件（可用于任意控件 如 ListView GridView WebView ScrollView）
 * <p>
 * 弹性下（上）拉，滑倒顶（低）部无需松开即可继续拉动
 *
 * @author Ybao
 */
public class PullRefreshLayout extends FlingLayout {

    protected Loadable mFooter;
    protected Refreshable mHeader;
    protected boolean hasHeader = true;
    protected boolean hasFooter = true;
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
    }

    @Override
    protected boolean onScroll(float y) {
        if (mHeader != null && isShowRefreshView() && y >= 0) {
            boolean intercept = mHeader.onScroll(y);
            if (y != 0) {
                return intercept;
            }
        }
        if (mFooter != null && isShowLoadView() && y <= 0) {
            boolean intercept = mFooter.onScroll(y);
            if (y != 0) {
                return intercept;
            }
        }
        return false;
    }

    @Override
    protected void onScrollChange(int stateType) {
        if (mHeader != null && isShowRefreshView()) {
            mHeader.onScrollChange(stateType);
        }
        if (mFooter != null && isShowLoadView()) {
            mFooter.onScrollChange(stateType);
        }

    }

    @Override
    protected boolean onStartrRelease(float nowY) {
        if (mHeader != null && nowY > 0 && isShowRefreshView()) {
            return mHeader.onStartrRelease(nowY);
        } else if (mFooter != null && nowY < 0 && isShowLoadView()) {
            return mFooter.onStartrRelease(nowY);
        }
        return false;
    }

    public void startRefresh() {
        if (mHeader != null && isShowRefreshView()) {
            mHeader.startRefresh();
        }
    }

    public void startLoad() {
        if (mFooter != null && isShowLoadView()) {
            mFooter.startLoad();
        }
    }

    public void stopRefresh() {
        if (mHeader != null && isShowRefreshView()) {
            mHeader.stopRefresh();
        }
    }

    public void stopLoad() {
        if (mFooter != null && isShowLoadView()) {
            mFooter.stopLoad();
        }
    }

    public void setHeader(Refreshable header) {
        if (mHeader != null && isMyChild((View) mHeader)) {
            removeView((View) mHeader);
        }
        this.mHeader = header;
        mHeader.setPullRefreshLayout(this);
    }

    public void setFooter(Loadable footer) {
        if (mFooter != null && isMyChild((View) mFooter)) {
            removeView((View) mFooter);
        }
        this.mFooter = footer;
        mFooter.setPullRefreshLayout(this);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof Refreshable) {
            setHeader((Refreshable) child);
        } else if (child instanceof Loadable) {
            setFooter((Loadable) child);
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getHeight();
        if (mHeader != null && isMyChild((View) mHeader)) {
            View mHeaderView = (View) mHeader;
            mHeaderView.layout(mHeaderView.getLeft(), -mHeaderView.getMeasuredHeight(), mHeaderView.getRight(), 0);
        }
        if (mFooter != null && isMyChild((View) mFooter)) {
            View mFooterView = (View) mFooter;
            mFooterView.layout(mFooterView.getLeft(), height, mFooterView.getRight(), height + mFooterView.getMeasuredHeight());
        }
    }

    private boolean isShowRefreshView() {
        return hasHeader && !isLoading && stateType != SCROLL_STATE_OVER_SCROLL;
    }

    private boolean isShowLoadView() {
        return hasFooter && !isRefreshing && stateType != SCROLL_STATE_OVER_SCROLL;
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

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public boolean isHasFooter() {
        return hasFooter;
    }
}