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
 * Author：Ybao on 2015/11/5 ‏‎17:53
 * <p/>
 * QQ: 392579823
 * <p/>
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
 * <p/>
 * 弹性下（上）拉，滑倒顶（低）部无需松开即可继续拉动
 *
 * @author Ybao
 */
public class PullRefreshLayout extends FlingLayout {

    protected int headerSpanHeight = 0;
    protected int footerSpanHeight = 0;
    protected int headerHeight = 0;
    protected int footerHeight = 0;
    protected Loadable mFooter;
    protected Refreshable mHeader;
    protected boolean hasHeader = true;
    protected boolean hasFooter = true;

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
    protected void fling(float offsetTop) {
        if (mHeader != null && offsetTop <= -headerSpanHeight) {
            startMoveTo(offsetTop, -headerSpanHeight);
        } else if (mFooter != null && offsetTop >= footerSpanHeight) {
            startMoveTo(offsetTop, footerSpanHeight);
        } else {
            startMoveTo(offsetTop, 0);
        }
    }


    @Override
    protected void onScroll(float y) {
        if (mHeader != null && y <= 0 && hasHeader) {
            mHeader.onScroll(this, y);
        }
        if (mFooter != null && y >= 0 && hasFooter) {
            mFooter.onScroll(this, y);
        }
    }

    @Override
    protected void onScrollChange(int state, float y) {
        if (mHeader != null && hasHeader) {
            mHeader.onScrollChange(this, state, y);
        }
        if (mFooter != null && hasFooter) {
            mFooter.onScrollChange(this, state, y);
        }
    }

    @Override
    public void moveTo(float y) {
        View terget = getPullView();
        if (terget == null) {
        } else if (y == 0) {
            if (mHeader != null) {
                mHeader.moveTo(terget, y);
            }
            if (mFooter != null) {
                mHeader.moveTo(terget, y);
            }
        } else if (y < 0) {
            if (mHeader != null) {
                mHeader.moveTo(terget, y);
            }
        } else if (y > 0) {
            if (mFooter != null) {
                mFooter.moveTo(terget, y);
            }
        }
        offsetTop = y;
        onScroll(y);

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, y);
        }
    }

    public int getHeaderLayoutType() {
        return mHeader != null ? mHeader.getLayoutType() : LayoutType.LAYOUT_SCROLLER;

    }

    public int getFooterLayoutType() {
        return mFooter != null ? mFooter.getLayoutType() : LayoutType.LAYOUT_SCROLLER;

    }

    public void openHeader() {
        float offsetTop = this.getOffsetTop();
        if (offsetTop == 0) {
            this.startMoveTo(0, -this.headerSpanHeight);
        }

    }

    public void openFooter() {
        float offsetTop = this.getOffsetTop();
        if (offsetTop == 0) {
            this.startMoveTo(0, this.footerSpanHeight);
        }

    }

    public void closeHeader() {
        float offsetTop = getOffsetTop();
        if (offsetTop < 0) {
            startMoveTo(offsetTop, 0);
        }

    }

    public void closeFooter() {
        float offsetTop = getOffsetTop();
        if (offsetTop > 0) {
            startMoveTo(offsetTop, 0);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof Refreshable && mHeader == null) {
            mHeader = (Refreshable) child;
            mHeader.setPullRefreshLayout(this);
        } else if (child instanceof Loadable && mFooter == null) {
            mFooter = (Loadable) child;
            mFooter.setPullRefreshLayout(this);
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getHeight();
        if (mHeader != null) {
            View mHeaderView = (View) mHeader;
            headerSpanHeight = hasHeader ? mHeader.getSpanHeight() : 0;
            headerHeight = hasHeader ? mHeaderView.getHeight() : 0;
            mHeaderView.layout(mHeaderView.getLeft(), -headerHeight, mHeaderView.getRight(), 0);
        }
        if (mFooter != null) {
            View mFooterView = (View) mFooter;
            footerSpanHeight = hasFooter ? mFooter.getSpanHeight() : 0;
            footerHeight = hasFooter ? mFooterView.getHeight() : 0;
            mFooterView.layout(mFooterView.getLeft(), height, mFooterView.getRight(), height + footerHeight);
        }
    }

    public void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
        requestLayout();
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
        requestLayout();
    }
}