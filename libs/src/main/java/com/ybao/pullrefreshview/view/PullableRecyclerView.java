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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.ybao.pullrefreshview.utils.Pullable;

public class PullableRecyclerView extends RecyclerView implements Pullable {
    LinearLayoutManager layoutManager;

    public PullableRecyclerView(Context context) {
        super(context);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout != null && layout instanceof LinearLayoutManager) {
            layoutManager = (LinearLayoutManager) layout;
        }
        super.setLayoutManager(layout);
    }

    @Override
    public boolean isGetTop() {
        if (layoutManager != null) {
            int count = layoutManager.getItemCount();
            if (count == 0) {
                return true;
            } else if (layoutManager.findFirstVisibleItemPosition() == 0 && getChildAt(0).getTop() >= 0) {
                return true;
            }

        }
        return false;
    }

    @Override
    public boolean isGetBottom() {

        if (layoutManager != null) {
            int count = layoutManager.getItemCount();
            if (count == 0) {
                return true;
            } else if (layoutManager.findLastVisibleItemPosition() == count - 1
                    && getChildAt(
                    layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition())
                    .getBottom() <= getMeasuredHeight()) {
                return true;
            }

        }
        return false;
    }

}
