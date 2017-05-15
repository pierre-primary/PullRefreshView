package com.ybao.pullrefreshview.extras;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.ybao.pullrefreshview.support.impl.VPullable;


public class PullableImageView extends ImageView implements VPullable {

    public PullableImageView(Context context) {
        super(context);
    }

    public PullableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public boolean canOverStart() {
        return true;
    }

    @Override
    public boolean canOverEnd() {
        return true;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void scrollAViewBy(int dp) {
    }
}
