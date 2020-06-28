package com.ybao.pullrefreshview.support.event;

import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.ybao.pullrefreshview.layout.FlingLayout;

public class EventVerticalHelper extends EventHelper {
    public EventVerticalHelper(FlingLayout flingLayout, FlingLayout.ScrollHelper mScrollHelper) {
        super(flingLayout, mScrollHelper);
    }

    @Override
    protected boolean dispatchScroll(MotionEvent ev, int tdx, int tdy) {
        if (isScrolling || Math.abs(tdy) > Math.abs(tdx)) {
            isScrolling = true;
            mFlingLayout.getParent().requestDisallowInterceptTouchEvent(true);
            int offset = (int) mScrollHelper.getOffset();
            int mdy = tdy;
            if (offset == 0) {
                //开始时 在0,0处
                //判断是否可以滑动
                if ((mdy > 0 && mFlingLayout.canOverStart()) || (mdy < 0 && mFlingLayout.canOverEnd())) {
                    mScrollHelper.offsetBy(mdy);
                    return true;
                }
            } else {
                //当不在0,0处
                ev.setAction(MotionEvent.ACTION_CANCEL);//屏蔽原事件

                if ((offset < 0 && offset + mdy >= 0) || (offset > 0 && offset + mdy <= 0)) {
                    //在0,0附近浮动
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    mScrollHelper.offsetTo(0);
                } else if ((offset > 0 && mdy > 0) || (offset < 0 && mdy < 0)) {
                    //是否超过最大距离
                    int maxDistance = mFlingLayout.getMaxDistance();
                    if (maxDistance > 0 && offset + mdy >= maxDistance) {
                        mScrollHelper.offsetTo(maxDistance);
                    } else if (maxDistance > 0 && offset + mdy <= -maxDistance) {
                        mScrollHelper.offsetTo(-maxDistance);
                    } else {
                        int ps = mdy * (Math.abs(offset) + maxDistance) / (2 * maxDistance);
                        mdy = mdy - ps;
                        mScrollHelper.offsetBy(mdy);
                    }
                } else {
                    mScrollHelper.offsetBy(mdy);
                }
            }
        } else {
            ev.setLocation(tdx, downY);
        }
        return false;
    }
}
