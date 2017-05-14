package com.ybao.pullrefreshview.support.resolver;

import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.ybao.pullrefreshview.layout.FlingLayout;
import com.ybao.pullrefreshview.support.impl.HPullable;
import com.ybao.pullrefreshview.support.impl.Pullable;
import com.ybao.pullrefreshview.support.utils.HCanPullUtil;

/**
 * Created by ybao on 2017/5/14.
 */

public class FlingXResolver extends EventResolver {
    protected float downY, downX;
    private boolean isScrolling = false;
    protected float tepmX;
    protected float tepmY;
    int mPointerId;

    public FlingXResolver(FlingLayout.FlingLayoutContext flingLayoutContext) {
        super(flingLayoutContext);
    }

    @Override
    public Pullable getPullAble(View view) {
        return HCanPullUtil.getPullAble(view);
    }

    @Override
    public Pullable getPullAble(Pullable pullable) {
        if (pullable instanceof HPullable) {
            return null;
        }
        return null;
    }

    @Override
    public void setViewTranslationP(View view, float value) {
        if (view == null) {
            return;
        }
        ViewHelper.setTranslationX(view, value);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float moveX = c.getMoveP();
        int pointerCount = ev.getPointerCount();
        int pointerIndex = ev.getActionIndex();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPointerId = ev.getPointerId(pointerIndex);
                float x = ev.getX(pointerIndex);
                float y = ev.getY(pointerIndex);
                tepmY = downY = y;
                tepmX = downX = x;
                if (moveX != 0) {
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
                if (isScrolling || Math.abs(mx - downX) > c.getTouchSlop() && (Math.abs(dataX) > Math.abs(dataY))) {
                    isScrolling = true;
                    if (moveX == 0) {
                        //开始时 在0,0处
                        //判断是否可以滑动
                        if ((dataX > 0 && c.canOverStart()) || (dataX < 0 && c.canOverEnd())) {
                            c.moveBy(dataX);
                            return true;
                        }
                    } else {
                        //当不在0,0处
                        ev.setAction(MotionEvent.ACTION_CANCEL);//屏蔽原事件

                        if ((moveX < 0 && moveX + dataX >= 0) || (moveX > 0 && moveX + dataX <= 0)) {
                            //在0,0附近浮动
                            ev.setAction(MotionEvent.ACTION_DOWN);
                            c.moveTo(0);
                        } else if ((moveX > 0 && dataX > 0) || (moveX < 0 && dataX < 0)) {
                            //是否超过最大距离
                            int maxDistance = c.getMaxDistance();
                            if (Math.abs(moveX) < maxDistance) {
                                int ps = 0;
                                int hDataX = dataX / 2;
                                ps = (int) (-hDataX * Math.abs(moveX) / (float) maxDistance) - hDataX;
                                c.moveBy(ps + dataX);
                            } else if (moveX > maxDistance) {
                                c.moveTo(maxDistance);
                            } else if (moveX < -maxDistance) {
                                c.moveTo(-maxDistance);
                            }
                        } else {
                            c.moveBy(dataX);
                        }
                    }
                } else {
                    ev.setLocation(mx, downX);
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
        return c.superDispatchTouchEvent(ev) || isScrolling;
    }

    @Override
    public boolean touchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
        }
        return isScrolling;
    }

    @Override
    protected void createVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        float yvelocity = mVelocityTracker.getYVelocity();
        float xvelocity = mVelocityTracker.getXVelocity();
        if (Math.abs(yvelocity) > Math.abs(xvelocity)) {
            velocity = 0;
        } else {
            velocity = xvelocity;
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        int[] offsetInWindow = new int[2];
        dispatchNestedScroll(dxConsumed, dyConsumed, dyConsumed, dyUnconsumed, offsetInWindow);
        c.moveBy(-dxUnconsumed - offsetInWindow[0]);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (!isNestedScrollingEnabled()) {
            setNestedScrollingEnabled(true);
        }
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        float moveX = c.getMoveP();
        if (c.getPullable() == null || moveX == 0) {
            dispatchNestedPreScroll(dx, dy, consumed, null);
        } else {
            consumed[1] = 0;
            stopNestedScroll();
            int dataX = -dx;
            if ((moveX < 0 && moveX + dataX >= 0) || (moveX > 0 && moveX + dataX <= 0)) {
                c.moveTo(0);
                startNestedScroll(ViewCompat.SCROLL_AXIS_HORIZONTAL);
                consumed[0] = (int) (moveX - 0);
                int[] pconsumed = new int[2];
                dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], pconsumed, null);
                consumed[0] += pconsumed[0];
                consumed[1] += pconsumed[1];
            } else if ((moveX > 0 && dataX > 0) || (moveX < 0 && dataX < 0)) {
                //是否超过最大距离
                int maxDistance = c.getMaxDistance();
                if (Math.abs(moveX) < maxDistance) {
                    int ps = 0;
                    int hDataX = dataX / 2;
                    ps = (int) (-hDataX * Math.abs(moveX) / (float) maxDistance) - hDataX;
                    c.moveBy(ps + dataX);
                } else if (moveX > maxDistance) {
                    c.moveTo(maxDistance);
                } else if (moveX < -maxDistance) {
                    c.moveTo(-maxDistance);
                }
                consumed[0] = dx;
            } else {
                c.moveBy(dataX);
                consumed[0] = dx;
            }
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean consumed = dispatchNestedPreFling(velocityX, velocityY);
        if (consumed) {
            return true;
        }
        if (c.canOverStart() && velocityX > 0) {
            return true;
        } else if (c.canOverEnd() && velocityX < 0) {
            return true;
        }
        return false;
    }
}
