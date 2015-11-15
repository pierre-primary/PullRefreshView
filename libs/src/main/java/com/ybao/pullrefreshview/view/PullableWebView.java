/**
 * Copyright 2015 Pengyuan-Jiang
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Author：Ybao on 2015/11/3 ‏‎11:43
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.ybao.pullrefreshview.utils.Pullable;

public class PullableWebView extends WebView implements Pullable {

    float scale = 1;

    public PullableWebView(Context context) {
        this(context, null);
    }

    public PullableWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullableWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        scale = getScale();
    }

    @Override
    public boolean isGetTop() {
        if (getScrollY() <= 0)
            return true;
        else
            return false;
    }

    @Override
    public boolean isGetBottom() {
        if (getScrollY() >= getContentHeight() * scale - getMeasuredHeight())
            return true;
        else
            return false;
    }
}
