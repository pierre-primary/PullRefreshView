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
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ybao.pullrefreshview.support.utils.CanPullUtil;
import com.ybao.pullrefreshview.support.type.FooterLayoutType;
import com.ybao.pullrefreshview.support.type.HeaderLayoutType;
import com.ybao.pullrefreshview.support.impl.Loadable;
import com.ybao.pullrefreshview.support.impl.Refreshable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Ybao on 2015/11/7 0007.
 */
public class RGPullRefreshLayout extends PullRefreshLayout {
    public final static int LAYOUT_NORMAL = 0x00;
    public final static int LAYOUT_DRAWER = 0x01;
    public final static int LAYOUT_SCROLLER = 0x10;


    public RGPullRefreshLayout(Context context) {
        this(context, null);
    }

    public RGPullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RGPullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getHeight();
        if (mHeader != null && mHeader.getLayoutType() == LAYOUT_SCROLLER) {
            View mHeaderView = (View) mHeader;
            headerSpanHeight = hasHeader ? mHeader.getSpanHeight() : 0;
            headerHeight = hasHeader ? mHeaderView.getHeight() : 0;
            mHeaderView.layout(mHeaderView.getLeft(), 0, mHeaderView.getRight(), headerHeight);
        }
        if (mFooter != null && mFooter.getLayoutType() == LAYOUT_SCROLLER) {
            View mFooterView = (View) mFooter;
            footerSpanHeight = hasFooter ? mFooter.getSpanHeight() : 0;
            footerHeight = hasFooter ? mFooterView.getHeight() : 0;
            mFooterView.layout(mFooterView.getLeft(), height - footerHeight, mFooterView.getRight(), height);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y == 0) {
            super.scrollTo(x, y);
            return;
        } else if (isDrawer(y)) {
        } else if (isScroller(y)) {
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
        if (mHeader != null && mHeader.getLayoutType() == LAYOUT_DRAWER && y < 0) {
            ViewCompat.setTranslationY((View) mHeader, -y);
            return true;
        }
        if (mFooter != null && mFooter.getLayoutType() == LAYOUT_DRAWER && y > 0) {
            ViewCompat.setTranslationY((View) mFooter, -y);
            return true;
        }
        return false;
    }


    private boolean isScroller(int y) {
        if (mHeader.getLayoutType() == LAYOUT_SCROLLER && y < 0) {
            if (mPullView != null) {
                ViewCompat.setTranslationY(mPullView, -y);
            }
            return true;
        }
        if (mFooter.getLayoutType() == LAYOUT_SCROLLER && y > 0) {
            if (mPullView != null) {
                ViewCompat.setTranslationY(mPullView, -y);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getOffsetTop() {
        if (mPullView != null) {
            int offset = -(int) ViewCompat.getTranslationY(mPullView);
            if (offset != 0) {
                return offset;
            }
        }
        if (mHeader != null) {
            int offset = -(int) ViewCompat.getTranslationY((View) mHeader);
            if (offset != 0) {
                return offset;
            }
        }
        if (mFooter != null) {
            int offset = -(int) ViewCompat.getTranslationY((View) mFooter);
            if (offset != 0) {
                return offset;
            }
        }
        return super.getOffsetTop();
    }
}