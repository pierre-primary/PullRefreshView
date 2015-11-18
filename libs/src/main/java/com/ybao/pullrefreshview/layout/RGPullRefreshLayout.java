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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ybao.pullrefreshview.utils.FooterLayoutType;
import com.ybao.pullrefreshview.utils.HeaderLayoutType;
import com.ybao.pullrefreshview.utils.Loadable;
import com.ybao.pullrefreshview.utils.Pullable;
import com.ybao.pullrefreshview.utils.Refreshable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by Ybao on 2015/11/7 0007.
 */
public class RGPullRefreshLayout extends PullRefreshLayout {
    public final static int LAYOUT_NORMAL = 0x00;
    public final static int LAYOUT_DRAWER = 0x01;
    public final static int LAYOUT_SCROLLER = 0x10;

    private int headerLayoutType = LAYOUT_NORMAL;
    private int footerLayoutType = LAYOUT_NORMAL;

    private FrameLayout viewBox, scrollerBox;


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
            headerLayoutType = getLayoutType(mHeader, HeaderLayoutType.class);
            if (headerLayoutType == LAYOUT_DRAWER) {
                addViewInFrameLayout(child, index, params);
                return;
            }
        } else if (child instanceof Loadable && mFooter == null) {
            mFooter = (Loadable) child;
            mFooter.setPullRefreshLayout(this);
            footerLayoutType = getLayoutType(mFooter, FooterLayoutType.class);
            if (footerLayoutType == LAYOUT_DRAWER) {
                addViewInFrameLayout(child, index, params);
                return;
            }
        } else if (child instanceof Pullable && mPullView == null) {
            mPullView = (Pullable) child;
            if (headerLayoutType == LAYOUT_SCROLLER || footerLayoutType == LAYOUT_SCROLLER) {
                addScrollInFrameLayout(child, index, params);
                return;
            }
        }
        super.addView(child, index, params);
    }

    public int getLayoutHeaderType() {
        return headerLayoutType;
    }

    public int getLayoutFooterType() {
        return footerLayoutType;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = getHeight();
        if (mHeader != null && headerLayoutType == LAYOUT_SCROLLER) {
            View mHeaderView = (View) mHeader;
            headerSpanHeight = mHeader.getSpanHeight();
            headerHeight = mHeaderView.getHeight();
            mHeaderView.layout(mHeaderView.getLeft(), 0, mHeaderView.getRight(), headerHeight);
        }
        if (mFooter != null && footerLayoutType == LAYOUT_SCROLLER) {
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
        if (headerLayoutType == LAYOUT_DRAWER && y < 0) {
            return true;
        }
        if (footerLayoutType == LAYOUT_DRAWER && y > 0) {
            return true;
        }
        return false;
    }


    private boolean isScroller(int y) {
        if (scrollerBox == null) {
            return false;
        }
        if (headerLayoutType == LAYOUT_SCROLLER && y < 0) {
            return true;
        }
        if (footerLayoutType == LAYOUT_SCROLLER && y > 0) {
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

    private static int getLayoutType(Object object, Class<? extends Annotation> cls) {
        Class<? extends Object> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field f : declaredFields) {
            if (f.getAnnotation(cls) != null) {
                f.setAccessible(true);
                try {
                    return f.getInt(object);
                } catch (Exception e) {
                    Log.e("initLayoutType", e.getMessage());
                }
            }
        }
        return LAYOUT_NORMAL;
    }
}