package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Y-bao on 2017/8/28 0028.
 */

public class RecyclerPartnerView extends RecyclerView implements PartnerImpt {
    private boolean mIsScrolling;
    private FlingAppBarLayout mPartner;

    private int mLastheight = 0;

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
    public int scrollBy(int dy) {
        if (dy > 0) {
            int bOffset = getBottomItemOffset();
            if (bOffset != Integer.MAX_VALUE) {
                if (bOffset <= 0) {
                    dy = 0;
                } else if (bOffset > 0 && bOffset - dy <= 0) {
                    dy = bOffset;
                }
            }
        } else if (dy < 0) {
            int tOffset = getTopItemOffset();
            if (tOffset != Integer.MAX_VALUE) {
                if (tOffset >= 0) {
                    dy = 0;
                } else if (tOffset < 0 && tOffset - dy >= 0) {
                    dy = tOffset;
                }
            }
        }
        scrollBy(0, dy);
        return dy;
    }

    @Override
    public boolean canHeaderDrag() {
        return true;
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
        postDelayed(runnable, 200);
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

    public FlingAppBarLayout getPartner() {
        return this.mPartner;
    }


    public boolean isScrolling() {
        return this.mIsScrolling;
    }

    @Override
    public void setPartner(FlingAppBarLayout paramxim) {
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
            if (mLastheight == mPartner.getMeasuredHeight()) {
                return true;
            }
            mLastheight = mPartner.getMeasuredHeight();
            setClipToPadding(false);
            setPadding(getPaddingLeft(), mPartner.getMeasuredHeight(), getPaddingRight(), getPaddingBottom());
            return true;
        }
    };

    public void onScrollStateChanged(int newState) {
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

    public void onScrolled(int dy) {
        removeCallbacks(runnable);
        if (mPartner != null) {
            RecyclerView.ViewHolder topViewHolder = findViewHolderForAdapterPosition(0);
            boolean hasY = false;
            int y = 0;
            if (topViewHolder != null) {
                hasY = true;
                y = topViewHolder.itemView.getTop();
            }
            mPartner.onPartnerScrolled(hasY, y - getPaddingTop(), -dy);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onScrolled(0);
        }
    };

    class MyOnScrollListener extends OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerPartnerView.this.onScrollStateChanged(newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            RecyclerPartnerView.this.onScrolled(dy);
        }
    }
}
