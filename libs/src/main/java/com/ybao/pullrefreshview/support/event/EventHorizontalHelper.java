package com.ybao.pullrefreshview.support.event;

import android.view.MotionEvent;

import com.ybao.pullrefreshview.layout.FlingLayout;

public class EventHorizontalHelper extends EventHelper {
    public EventHorizontalHelper(FlingLayout flingLayout, FlingLayout.ScrollHelper mScrollHelper) {
        super(flingLayout, mScrollHelper);
    }

    @Override
    protected boolean dispatchScroll(MotionEvent ev, int tdx, int tdy) {
        if (isScrolling || Math.abs(tdx) > Math.abs(tdy)) {
            isScrolling = true;
            mFlingLayout.getParent().requestDisallowInterceptTouchEvent(true);
            int offset = (int) mFlingLayout.getOffset();
            int mdx = tdx;
            if (offset == 0) {
                //开始时 在0,0处
                //判断是否可以滑动
                if ((mdx > 0 && mFlingLayout.canOverStart()) || (mdx < 0 && mFlingLayout.canOverEnd())) {
                    mScrollHelper.offsetBy(mdx);
                    return true;
                }
            } else {
                //当不在0,0处
                ev.setAction(MotionEvent.ACTION_CANCEL);//屏蔽原事件

                if ((offset < 0 && offset + mdx >= 0) || (offset > 0 && offset + mdx <= 0)) {
                    //在0,0附近浮动
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    mScrollHelper.offsetTo(0);
                } else if ((offset > 0 && mdx > 0) || (offset < 0 && mdx < 0)) {
                    //是否超过最大距离
                    int maxDistance = mFlingLayout.getMaxDistance();
                    if (maxDistance > 0 && offset + mdx >= maxDistance) {
                        mScrollHelper.offsetTo(maxDistance);
                    } else if (maxDistance > 0 && offset + mdx <= -maxDistance) {
                        mScrollHelper.offsetTo(-maxDistance);
                    } else {
                        int ps = mdx * (Math.abs(offset) + maxDistance) / (2 * maxDistance);
                        mdx = mdx - ps;
                        mScrollHelper.offsetBy(mdx);
                    }
                } else {
                    mScrollHelper.offsetBy(mdx);
                }
            }
        } else {
            ev.setLocation(downX, tdy);
        }
        return false;
    }

    @Override
    protected void dispatchFling(float velocityX, float velocityY) {
        mScrollHelper.setVelocity(velocityX);
    }
}
