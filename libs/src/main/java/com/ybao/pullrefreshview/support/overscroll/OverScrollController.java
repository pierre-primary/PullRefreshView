package com.ybao.pullrefreshview.support.overscroll;

import android.os.Handler;
import android.os.Looper;
import android.view.ViewConfiguration;

import com.ybao.pullrefreshview.layout.FlingLayout;
import com.ybao.pullrefreshview.support.anim.interpolator.DecelerateAccelerateInterpolator;
import com.ybao.pullrefreshview.support.utils.Utils;

/**
 * Created by ybao on 2017/5/14.
 */

public class OverScrollController {
    private Handler handler = new Handler(Looper.getMainLooper());
    private ScrollChangedListener scrollChangedListener = new ScrollChangedListener();
    private final FlingLayout mFlingLayout;
    private final FlingLayout.ScrollOverHelper mScrollOverHelper;
    private final int mTouchSlop;
    private final int mMaxOverScrollDist;

    public OverScrollController(FlingLayout flingLayout, FlingLayout.ScrollOverHelper scrollOverHelper) {
        mFlingLayout = flingLayout;
        mScrollOverHelper = scrollOverHelper;
        mTouchSlop = ViewConfiguration.get(flingLayout.getContext()).getScaledTouchSlop();
        mMaxOverScrollDist = 10 * mTouchSlop;
    }

    public void addOverScrollListener() {
        removeOverScrollListener();
        scrollChangedListener.reset();
        handler.post(scrollChangedListener);
    }


    public void removeOverScrollListener() {
        handler.removeCallbacks(scrollChangedListener);
    }

    class ScrollChangedListener implements Runnable {
        int times = 0;

        public void reset() {
            times = 0;
        }

        @Override
        public void run() {
            times++;
            if (mFlingLayout.canOverStart()) {
                float oh = (float) Math.sqrt(mScrollOverHelper.getVelocity() * 3 / times);
                oh = Math.min(mMaxOverScrollDist, oh);
                if (oh > mTouchSlop) {
                    float offset = mFlingLayout.getOffset();
                    flingOver(offset, offset + oh);
                } else {
                    mScrollOverHelper.setScrollState(FlingLayout.SCROLL_STATE_IDLE);
                }
                return;
            } else if (mFlingLayout.canOverEnd()) {
                float oh = (float) -Math.sqrt(-mScrollOverHelper.getVelocity() * 3 / times);
                oh = -Math.min(mMaxOverScrollDist, -oh);
                if (-oh > mTouchSlop) {
                    float offset = mFlingLayout.getOffset();
                    flingOver(offset, offset + oh);
                } else {
                    mScrollOverHelper.setScrollState(FlingLayout.SCROLL_STATE_IDLE);
                }
                return;
            }
            if (times > 130) {
                mScrollOverHelper.setScrollState(FlingLayout.SCROLL_STATE_IDLE);
            } else {
                mScrollOverHelper.setScrollState(FlingLayout.SCROLL_STATE_FLING);
                handler.postDelayed(this, 16);
            }
        }
    }

    private void flingOver(float startP, float endP) {
        mScrollOverHelper.startScrollAnim(0, FlingLayout.SCROLL_STATE_OVER_SCROLL, (Utils.MIN_DURATION + Utils.MAX_DURATION) / 2, new DecelerateAccelerateInterpolator(), null, startP, endP, startP);
    }
}
