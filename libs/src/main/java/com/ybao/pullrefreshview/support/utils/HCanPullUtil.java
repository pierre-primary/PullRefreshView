package com.ybao.pullrefreshview.support.utils;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.ybao.pullrefreshview.support.impl.HPullable;

/**
 * Created by ybao on 16/3/7.
 */
public class HCanPullUtil {

    public static HPullable getPullAble(View view) {
        if (view == null) {
            return null;
        }
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (view instanceof HPullable) {
            return (HPullable) view;
        } else if (view instanceof ScrollView || view instanceof NestedScrollView) {
            return new ScrollViewCanPull((ViewGroup) view);
        } else if (view instanceof RecyclerView) {
            return new RecyclerViewCanPull((RecyclerView) view);
        }
        return null;
    }

    private static class ScrollViewCanPull implements HPullable {
        public ScrollViewCanPull(ViewGroup scrollView) {
            this.scrollView = scrollView;
        }

        ViewGroup scrollView;

        @Override
        public boolean canOverStart() {
            if (scrollView.getScrollX() <= 0)
                return true;
            else
                return false;
        }

        @Override
        public boolean canOverEnd() {
            if (scrollView.getChildCount() == 0) {
                return true;
            }
            if (scrollView.getScrollX() >= (scrollView.getChildAt(0).getWidth() - scrollView.getMeasuredWidth()))
                return true;
            else
                return false;
        }

        @Override
        public View getView() {
            return scrollView;
        }

        @Override
        public void scrollAViewBy(int dp) {
            if (scrollView.getChildCount() != 0) {
                float maxScrollY = scrollView.getChildAt(0).getWidth() - scrollView.getMeasuredWidth();
                if (scrollView.getScrollX() + dp >= maxScrollY) {
                    scrollView.scrollTo((int) maxScrollY, 0);
                } else {
                    scrollView.scrollBy(dp, 0);
                }
            }
        }
    }


    private static class RecyclerViewCanPull implements HPullable {
        public RecyclerViewCanPull(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        RecyclerView recyclerView;
        LinearLayoutManager layoutManager;

        private void initLayoutManager() {
            if (layoutManager == null) {
                RecyclerView.LayoutManager layout = recyclerView.getLayoutManager();
                if (layout != null && layout instanceof LinearLayoutManager) {
                    layoutManager = (LinearLayoutManager) layout;
                }
            }
        }

        @Override
        public boolean canOverStart() {
            initLayoutManager();
            if (layoutManager != null) {
                if (layoutManager.getItemCount() == 0) {
                    return true;
                } else if (layoutManager.findFirstVisibleItemPosition() == 0 && recyclerView.getChildAt(0).getLeft() >= recyclerView.getPaddingLeft()) {
                    return true;
                }
            }
            return false;
        }


        @Override
        public boolean canOverEnd() {
            initLayoutManager();
            if (layoutManager != null) {
                int count = layoutManager.getItemCount();
                if (count == 0) {
                    return true;
                } else if (layoutManager.findLastCompletelyVisibleItemPosition() == count - 1) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public View getView() {
            return recyclerView;
        }

        @Override
        public void scrollAViewBy(int dp) {
            recyclerView.scrollBy(dp, 0);
        }
    }
}
