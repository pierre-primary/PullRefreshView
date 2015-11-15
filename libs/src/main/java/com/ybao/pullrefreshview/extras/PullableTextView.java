package com.ybao.pullrefreshview.extras;


import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ybao.pullrefreshview.utils.Pullable;

public class PullableTextView extends TextView implements Pullable {

    public PullableTextView(Context context) {
        super(context);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public PullableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public PullableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public boolean isGetTop() {
        if (getScrollY() == 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean isGetBottom() {
        if (getScrollY() >= (getLayout().getHeight() - getMeasuredHeight() + getCompoundPaddingBottom() + getCompoundPaddingTop()))
            return true;
        else
            return false;
    }

}
