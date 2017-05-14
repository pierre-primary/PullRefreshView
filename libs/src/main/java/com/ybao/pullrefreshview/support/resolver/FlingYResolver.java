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
    protected float downY, downX;
    private boolean isScrolling = false;
    protected float tepmX;
    protected float tepmY;
    int mPointerId;

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
    protected void createVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        velocity = mVelocityTracker.getYVelocity();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float moveY = c.getMoveP();
        int pointerCount = ev.getPointerCount();
        int pointerIndex = ev.getActionIndex();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPointerId = ev.getPointerId(pointerIndex);
                float x = ev.getX(pointerIndex);
                float y = ev.getY(pointerIndex);
                tepmY = downY = y;
                tepmX = downX = x;
                if (moveY != 0) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointerId = ev.getPointerId(pointerIndex);
                tepmX = ev.getX(pointerIndex);
                tepmY = ev.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                pointerIndex = ev.findPointerIndex(mPointerId);
                float mx;
                float my;
                if (pointerCount > pointerIndex && pointerIndex >= 0) {
                    mx = ev.getX(pointerIndex);
                    my = ev.getY(pointerIndex);
                } else {
                    mx = ev.getX();
                    my = ev.getY();
                }
                //意图分析，避免误操作
                int dataX = (int) (mx - tepmX);
                int dataY = (int) (my - tepmY);
                tepmX = mx;
                tepmY = my;
                if (isScrolling || Math.abs(my - downY) > c.getTouchSlop() && (Math.abs(dataY) > Math.abs(dataX))) {
                    isScrolling = true;
                    if (moveY == 0) {
                        //开始时 在0,0处
                        //判断是否可以滑动
                        if ((dataY > 0 && c.canOverStart()) || (dataY < 0 && c.canOverEnd())) {
                            c.moveBy(dataY);
                            return true;
                        } else {
                            c.superDispatchTouchEvent(ev);
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
                    ev.setLocation(mx, downY);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                c.startRelease();
                isScrolling = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 获取离开屏幕的手指的索引
                int pointerIdLeave = ev.getPointerId(pointerIndex);
                if (mPointerId == pointerIdLeave) {
                    // 离开屏幕的正是目前的有效手指，此处需要重新调整，并且需要重置VelocityTracker
                    int reIndex = pointerIndex == 0 ? 1 : 0;
                    mPointerId = ev.getPointerId(reIndex);
                    // 调整触摸位置，防止出现跳动
                    tepmX = ev.getX(reIndex);
                    tepmY = ev.getY(reIndex);
                }
        }
        return isScrolling || c.superDispatchTouchEvent(ev);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
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
            consumed[0] = 0;
            stopNestedScroll();
            int dataY = -dy;
            if ((moveY < 0 && moveY + dataY >= 0) || (moveY > 0 && moveY + dataY <= 0)) {
                c.moveTo(0);
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                consumed[1] = (int) (moveY - 0);
                int[] pconsumed = new int[2];
                dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], pconsumed, null);
                consumed[0] += pconsumed[0];
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
