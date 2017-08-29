package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Y-bao on 2017/8/28 0028.
 */

public class RecyclerPartnerView extends RecyclerView implements PartnerImpt {
    public boolean mIsScrolling;
    public AppBarLayout mPartner;

    public RecyclerPartnerView(Context paramContext) {
        super(paramContext);
        init();
    }

    public RecyclerPartnerView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public RecyclerPartnerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private void init() {
        addOnScrollListener(new MyOnScrollListener());
    }

    public void backToTop() {
        scrollToPosition(0);
        if (this.mPartner != null) {
            this.mPartner.setOffset(0);
        }
    }

    @Override
    public boolean canHeaderDrag() {
        return true;
    }

    @Override
    public int getBottomItemOffset() {
        LayoutManager lm = getLayoutManager();
        int n = 0;
        if (lm != null && (n = lm.getChildCount()) > 0) {
            View view = lm.getChildAt(n - 1);
            if (view != null && lm.getPosition(view) == getAdapter().getItemCount() - 1) {
                return view.getBottom() - getHeight() - getPaddingBottom();
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public int getTopItemOffset() {
        LayoutManager lm = getLayoutManager();
        if (lm != null) {
            View view = lm.getChildAt(0);
            if (view != null && lm.getPosition(view) == 0) {
                return view.getTop() - getPaddingTop();
            }
        }
        return Integer.MAX_VALUE;
    }

    public AppBarLayout getPartner() {
        return this.mPartner;
    }


    public boolean isScrolling() {
        return this.mIsScrolling;
    }

    @Override
    public void setPartner(AppBarLayout paramxim) {
        if (this.mPartner != null) {
            this.mPartner.getViewTreeObserver().removeOnPreDrawListener(onPreDrawListener);
        }
        if (paramxim == null) {
            return;
        }
        this.mPartner = paramxim;
        this.mPartner.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
    }

    ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (height == mPartner.getMeasuredHeight()) {
                return true;
            }
            height = mPartner.getMeasuredHeight();
            setClipToPadding(false);
            setPadding(getPaddingLeft(), mPartner.getMeasuredHeight(), getPaddingRight(), getPaddingBottom());
            return true;
        }
    };

    int height = 0;

    class MyOnScrollListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mIsScrolling = false;
                if (mPartner != null) {
                    mPartner.onPartnerScrollStop();
                }
            } else {
                mIsScrolling = true;
                if (mPartner != null) {
                    mPartner.onPartnerScrollStart();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mPartner != null) {
                RecyclerView.ViewHolder topViewHolder = findViewHolderForAdapterPosition(0);
                boolean hasY = false;
                int y = 0;
                if (topViewHolder != null) {
                    hasY = true;
                    y = topViewHolder.itemView.getTop();
                }
                mPartner.onPartnerScrolled(hasY, y - recyclerView.getPaddingTop(), -dy);
            }
        }
    }
}
