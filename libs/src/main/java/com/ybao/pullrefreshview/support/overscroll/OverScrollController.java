package com.ybao.pullrefreshview.support.overscroll;

import android.os.Handler;
import android.view.animation.DecelerateInterpolator;

import com.ybao.pullrefreshview.layout.FlingLayout;
import com.ybao.pullrefreshview.support.anim.interpolator.DecelerateAccelerateInterpolator;

/**
 * Created by ybao on 2017/5/14.
 */

public class OverScrollController {
    Handler handler = new Handler();
    ScrollChangedListener scrollChangedListener = new ScrollChangedListener();
    protected FlingLayout.FlingLayoutContext c;

    public OverScrollController(FlingLayout.FlingLayoutContext flingLayoutContext) {
        this.c = flingLayoutContext;
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
            if (c.canOverStart()) {
                float oh = (float) Math.sqrt(c.getVelocity() * 3 / times);
                oh = Math.min(c.getMaxOverScrollDist(), oh);
                if (oh > c.getTouchSlop()) {
                    float moveP = c.getMoveP();
                    startBounce(moveP, moveP + oh);
                } else {
                    c.setScrollState(FlingLayout.SCROLL_STATE_IDLE);
                }
                return;
            } else if (c.canOverEnd()) {
                float oh = (float) -Math.sqrt(-c.getVelocity() * 3 / times);
                oh = -Math.min(c.getMaxOverScrollDist(), -oh);
                if (-oh > c.getTouchSlop()) {
                    float moveP = c.getMoveP();
                    startBounce(moveP, moveP + oh);
                } else {
                    c.setScrollState(FlingLayout.SCROLL_STATE_IDLE);
                }
                return;
            }
            if (times > 60) {
                c.setScrollState(FlingLayout.SCROLL_STATE_IDLE);
            } else {
                c.setScrollState(FlingLayout.SCROLL_STATE_FLING);
                handler.postDelayed(this, 16);
            }
        }
    }

    public int startBounce(float startP, float endP) {
        int duration = (int) Math.abs(endP - startP);
        int time = Math.min(c.getMaxDuration() * 2, duration);
        time = Math.max(c.getMinDuration(), time);
        c.startAnim(0, FlingLayout.SCROLL_STATE_OVER_SCROLL, time, new DecelerateAccelerateInterpolator(), null, startP, endP, startP);
        return time;
    }
}
