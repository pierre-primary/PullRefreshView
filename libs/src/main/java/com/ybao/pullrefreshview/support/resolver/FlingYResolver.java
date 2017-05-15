package com.ybao.pullrefreshview.support.resolver;

import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.layout.FlingLayout;
import com.ybao.pullrefreshview.support.impl.Pullable;
import com.ybao.pullrefreshview.support.impl.VPullable;
import com.ybao.pullrefreshview.support.utils.VCanPullUtil;

/**
 * Created by ybao on 2017/5/14.
 */

public class FlingYResolver extends EventResolver {

    public FlingYResolver(FlingLayout.FlingLayoutContext flingLayoutContext) {
        super(flingLayoutContext);
    }

    @Override
    public Pullable getPullAble(View view) {
        return VCanPullUtil.getPullAble(view);
    }

    @Override
    public Pullable getPullAble(Pullable pullable) {
        if (pullable instanceof VPullable) {
            return null;
        }
        return null;
    }

    @Override
    public void setViewTranslationP(View view, float value) {
        if (view == null) {
            return;
        }
        ViewHelper.setTranslationY(view, value);
    }

    @Override
    protected void createVelocity(MotionEvent ev) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float yvelocity = mVelocityTracker.getYVelocity();
        float xvelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(xvelocity) > Math.abs(yvelocity)) {
            velocity = 0;
        } else {
            velocity = yvelocity;
        }
    }

    @Override
    protected boolean tryToMove(MotionEvent ev, float oldX, float oldY, float x, float y) {
        int dataX = (int) (x - oldX);
        int dataY = (int) (y - oldY);
        if (isScrolling || Math.abs(dataY) > Math.abs(dataX)) {
            isScrolling = true;
            c.getFlingLayout().getParent().requestDisallowInterceptTouchEvent(true);
            float moveY = c.getMoveP();
            if (moveY == 0) {
                //开始时 在0,0处
                //判断是否可以滑动
                if ((dataY > 0 && c.canOverStart()) || (dataY < 0 && c.canOverEnd())) {
                    c.moveBy(dataY);
                    return true;
                }
            } else {
                //当不在0,0处
                ev.setAction(MotionEvent.ACTION_CANCEL);//屏蔽原事件

                if ((moveY < 0 && moveY + dataY >= 0) || (moveY > 0 && moveY + dataY <= 0)) {
                    //在0,0附近浮动
                    ev.setAction(MotionEvent.ACTION_DOWN);
                    c.moveTo(0);
                } else if ((moveY > 0 && dataY > 0) || (moveY < 0 && dataY < 0)) {
                    //是否超过最大距离
                    int maxDistance = c.getMaxDistance();
                    if (Math.abs(moveY) < maxDistance) {
                        int ps = 0;
                        int hDataY = dataY / 2;
                        ps = (int) (-hDataY * Math.abs(moveY) / (float) maxDistance) - hDataY;
                        c.moveBy(ps + dataY);
                    } else if (moveY > maxDistance) {
                        c.moveTo(maxDistance);
                    } else if (moveY < -maxDistance) {
                        c.moveTo(-maxDistance);
                    }
                } else {
                    c.moveBy(dataY);
                }
            }
        } else {
            ev.setLocation(x, downY);
        }
        return false;
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        c.getFlingLayout().getParent().requestDisallowInterceptTouchEvent(true);
        int[] offsetInWindow = new int[2];
        dispatchNestedScroll(dxConsumed, dyConsumed, dyConsumed, dyUnconsumed, offsetInWindow);
        c.moveBy(-dyUnconsumed - offsetInWindow[1]);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (!isNestedScrollingEnabled()) {
            setNestedScrollingEnabled(true);
        }
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        float moveY = c.getMoveP();
        if (c.getPullable() == null || moveY == 0) {
            dispatchNestedPreScroll(dx, dy, consumed, null);
        } else {
            c.getFlingLayout().getParent().requestDisallowInterceptTouchEvent(true);
            consumed[0] = dx;
            stopNestedScroll();
            int dataY = -dy;
            if ((moveY < 0 && moveY + dataY >= 0) || (moveY > 0 && moveY + dataY <= 0)) {
                c.moveTo(0);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                consumed[1] = (int) (moveY - 0);
                int[] pconsumed = new int[2];
                dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], pconsumed, null);
                consumed[1] += pconsumed[1];
            } else if ((moveY > 0 && dataY > 0) || (moveY < 0 && dataY < 0)) {
                //是否超过最大距离
                int maxDistance = c.getMaxDistance();
                if (Math.abs(moveY) < maxDistance) {
                    int ps = 0;
                    int hDataY = dataY / 2;
                    ps = (int) (-hDataY * Math.abs(moveY) / (float) maxDistance) - hDataY;
                    c.moveBy(ps + dataY);
                } else if (moveY > maxDistance) {
                    c.moveTo(maxDistance);
                } else if (moveY < -maxDistance) {
                    c.moveTo(-maxDistance);
                }
                consumed[1] = dy;
            } else {
                c.moveBy(dataY);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean consumed = dispatchNestedPreFling(velocityX, velocityY);
        if (consumed) {
            return true;
        }
        if (c.canOverStart() && velocityY > 0) {
            return true;
        } else if (c.canOverEnd() && velocityY < 0) {
            return true;
        }
        return false;
    }
}
