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
 * Author：Ybao on 2015/11/7 ‏‎12:58
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ybao.pullrefreshview.utils.Loadable;
import com.ybao.pullrefreshview.utils.Pullable;
import com.ybao.pullrefreshview.utils.Refreshable;

/**
 * Created by Ybao on 2015/11/7 0007.
 */
public class RGPullRefreshLayout extends PullRefreshLayout {
    public final static int LAYOUT_NORMAL = 0x0000;
    public final static int LAYOUT_DRAWER_HEADER = 0x0001;
    public final static int LAYOUT_SCROLLER_HEADER = 0x0010;
    public final static int LAYOUT_DRAWER_FOOTER = 0x0100;
    public final static int LAYOUT_SCROLLER_FOOTER = 0x1000;

    public final static int LAYOUT_SCROLLER = LAYOUT_SCROLLER_HEADER | LAYOUT_SCROLLER_FOOTER;
    public final static int LAYOUT_DRAWER = LAYOUT_DRAWER_HEADER | LAYOUT_DRAWER_FOOTER;

    public final static int LAYOUT_HEADER_MASK = 0x0011;
    public final static int LAYOUT_FOOTER_MASK = 0x1100;

    private FrameLayout viewBox, scrollerBox;
    private int layoutType = LAYOUT_NORMAL;

    public RGPullRefreshLayout(Context context) {
        this(context, null);
    }

    public RGPullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RGPullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addViewInFrameLayout(View view, int index, ViewGroup.LayoutParams params) {
        if (viewBox == null) {
            viewBox = new FrameLayout(getContext());
            super.addView(viewBox, -1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        viewBox.addView(view, index, params);
    }

    public void addScrollInFrameLayout(View view, int index, ViewGroup.LayoutParams params) {
        if (scrollerBox == null) {
            scrollerBox = new FrameLayout(getContext());
            super.addView(scrollerBox, -1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        scrollerBox.addView(view, index, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {

        if (child instanceof Refreshable && mHeader == null) {
            mHeader = (Refreshable) child;
            mHeader.setPullRefreshLayout(this);
            if (getLayoutHeaderType(layoutType) == LAYOUT_DRAWER_HEADER) {
                addViewInFrameLayout(child, index, params);
                return;
            }
        } else if (child instanceof Loadable && mFooter == null) {
            mFooter = (Loadable) child;
            mFooter.setPullRefreshLayout(this);
            if (getLayoutFooterType(layoutType) == LAYOUT_DRAWER_FOOTER) {
                addViewInFrameLayout(child, index, params);
                return;
            }
        } else if (child instanceof Pullable && mPullView == null) {
            mPullView = (Pullable) child;
            if ((layoutType & LAYOUT_SCROLLER) != 0) {
                addScrollInFrameLayout(child, index, params);
                return;
            }
        }
        super.addView(child, index, params);
    }

    public static int getLayoutHeaderType(int type) {
        return LAYOUT_HEADER_MASK & type;
    }

    public static int getLayoutFooterType(int type) {
        return LAYOUT_FOOTER_MASK & type;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getHeight();
        if (mHeader != null && (getLayoutHeaderType(layoutType) == LAYOUT_SCROLLER_HEADER)) {
            View mHeaderView = (View) mHeader;
            headerSpanHeight = mHeader.getSpanHeight();
            headerHeight = mHeaderView.getHeight();
            mHeaderView.layout(mHeaderView.getLeft(), 0, mHeaderView.getRight(), headerHeight);
        }
        if (mFooter != null && (getLayoutFooterType(layoutType) == LAYOUT_SCROLLER_FOOTER)) {
            View mFooterView = (View) mFooter;
            footerSpanHeight = mFooter.getSpanHeight();
            footerHeight = mFooterView.getHeight();
            mFooterView.layout(mFooterView.getLeft(), height - footerHeight, mFooterView.getRight(), height);
        }

        if (scrollerBox != null) {
            scrollerBox.bringToFront();
        }
        if (viewBox != null) {
            viewBox.bringToFront();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y == 0) {
            super.scrollTo(x, y);
            if (viewBox != null) {
                viewBox.scrollTo(x, y);
            }
            if (scrollerBox != null) {
                scrollerBox.scrollTo(x, y);
            }
            return;
        } else if (isDrawer(y)) {
            viewBox.scrollTo(x, y);
        } else if (isScroller(y)) {
            scrollerBox.scrollTo(x, y);
        } else {
            super.scrollTo(x, y);
            return;
        }
        onScroll(y);

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(this, y);
        }
    }

    private boolean isDrawer(int y) {
        if (viewBox == null) {
            return false;
        }
        if ((getLayoutHeaderType(layoutType) == LAYOUT_DRAWER_HEADER) && y < 0) {
            return true;
        }
        if ((getLayoutFooterType(layoutType) == LAYOUT_DRAWER_FOOTER) && y > 0) {
            return true;
        }
        return false;
    }


    private boolean isScroller(int y) {
        if (scrollerBox == null) {
            return false;
        }
        if ((getLayoutHeaderType(layoutType) == LAYOUT_SCROLLER_HEADER) && y < 0) {
            return true;
        }
        if ((getLayoutFooterType(layoutType) == LAYOUT_SCROLLER_FOOTER) && y > 0) {
            return true;
        }
        return false;
    }

    @Override
    public int getOffsetTop() {
        if (viewBox != null) {
            int offset = viewBox.getScrollY();
            if (offset != 0) {
                return offset;
            }
        }
        if (scrollerBox != null) {
            int offset = scrollerBox.getScrollY();
            if (offset != 0) {
                return offset;
            }
        }
        return super.getOffsetTop();

    }

    public void setLayoutType(int layoutType) {
        if (this.layoutType == layoutType && (layoutType == LAYOUT_HEADER_MASK || layoutType == LAYOUT_FOOTER_MASK)) {
            return;
        }
        this.layoutType = layoutType;
        if (mHeader != null) {
            View headerView = (View) mHeader;
            removeView(headerView);
            if (viewBox != null) {
                viewBox.removeView(headerView);
            }
            mHeader = null;
            addView(headerView, headerView.getLayoutParams());
        }
        if (mFooter != null) {
            View footerView = (View) mFooter;
            removeView(footerView);
            if (viewBox != null) {
                viewBox.removeView(footerView);
            }
            mFooter = null;
            addView(footerView, footerView.getLayoutParams());
        }

        if (mPullView != null) {
            View cev = (View) mPullView;
            removeView(cev);
            if (scrollerBox != null) {
                scrollerBox.removeView(cev);
            }
            mPullView = null;
            addView(cev, cev.getLayoutParams());
        }

        if (viewBox != null && viewBox.getChildCount() <= 0) {
            removeView(viewBox);
            viewBox = null;
        }
        if (scrollerBox != null && scrollerBox.getChildCount() <= 0) {
            removeView(scrollerBox);
            scrollerBox = null;
        }
    }
}