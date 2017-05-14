package com.ybao.pullrefreshview.support.utils;

import android.content.Context;

/**
 * Created by Ybao on 16/7/24.
 */
public class Utils {
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
