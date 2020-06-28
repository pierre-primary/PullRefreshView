package com.ybao.pullrefreshview.support.nested;

import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;

public interface INestedHelper extends NestedScrollingChild, NestedScrollingParent {
    void onDetachedFromWindow();
}
