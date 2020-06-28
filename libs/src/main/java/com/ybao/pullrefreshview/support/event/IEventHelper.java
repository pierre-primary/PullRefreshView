package com.ybao.pullrefreshview.support.event;

import android.view.MotionEvent;

public interface IEventHelper {

    boolean isScrolling();

    boolean dispatchTouchEvent(MotionEvent ev);

    boolean touchEvent(MotionEvent ev);
}
