package com.ybao.pullrefreshview.support.utils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by ybao on 16/3/9.
 */
public class ViewScrollUtil {
    public static ScrollGeter getScrollGeter(View view) {
        if (view == null) {
            return null;
        }
        if (view instanceof ScrollGeter) {
            return (ScrollGeter) view;
        } else if (view instanceof AbsListView) {
            return new AbsListViewScrollGeter((AbsListView) view);
        } else if (view instanceof RecyclerView) {
            return new RecyclerViewScrollGeter((RecyclerView) view);
        }
        return null;
    }

    private static class AbsListViewScrollGeter implements ScrollGeter {
        AbsListView absListView;

        public AbsListViewScrollGeter(AbsListView absListView) {
            this.absListView = absListView;
        }

        @Override
        public int getScrollX() {
            return 0;
        }

        @Override
        public int getScrollY() {
            int firstVisiblePosition = absListView.getFirstVisiblePosition();
            View c = absListView.getChildAt(0);
            if (c == null) {
                return 0;
            }
            int top = c.getTop() - absListView.getPaddingTop();
            return -top + firstVisiblePosition * c.getHeight();
        }
    }

    private static class RecyclerViewScrollGeter implements ScrollGeter {
        RecyclerView recyclerView;
        LinearLayoutManager layoutManager;

        public RecyclerViewScrollGeter(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
            RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
            if (layout != null && layout instanceof LinearLayoutManager) {
                layoutManager = (LinearLayoutManager) layout;
            } else {
                Log.w("RecyclerViewScrollGeter", "LayoutManager is null or Not is LinearLayoutManager");
            }
        }

        @Override
        public int getScrollX() {
            return 0;
        }

        @Override
        public int getScrollY() {
            if (layoutManager != null) {
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                View c = layoutManager.findViewByPosition(firstVisiblePosition);
                if (c == null) {
                    return 0;
                }
                int top = c.getTop() - recyclerView.getPaddingTop();
                int n = firstVisiblePosition;
                if (layoutManager instanceof GridLayoutManager) {
                    int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
                    n = n / spanCount;
                }
                return -top + n * c.getHeight();
            }
            return 0;
        }
    }

    public interface ScrollGeter {
        int getScrollX();

        int getScrollY();
    }
}
