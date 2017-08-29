package com.ybao.pullrefreshview.simple.activities.coor;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;


/**
 * Created by Y-bao on 2017/8/21 0021.
 */
@CoordinatorLayout.DefaultBehavior(BaseAppBarLayout.Behavior.class)
public class AppBarLayout extends BaseAppBarLayout {

    ValueAnimator valueAnimator = null;
    private PartnerImpt mPartner;

    public AppBarLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public AppBarLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof android.support.design.widget.AppBarLayout.LayoutParams;
    }

    @Override
    protected android.support.design.widget.AppBarLayout.LayoutParams generateDefaultLayoutParams() {
        return new android.support.design.widget.AppBarLayout.LayoutParams(android.support.design.widget.AppBarLayout.LayoutParams.MATCH_PARENT, android.support.design.widget.AppBarLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public android.support.design.widget.AppBarLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new android.support.design.widget.AppBarLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.support.design.widget.AppBarLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (Build.VERSION.SDK_INT >= 19 && p instanceof LayoutParams) {
            return new android.support.design.widget.AppBarLayout.LayoutParams((LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new android.support.design.widget.AppBarLayout.LayoutParams((MarginLayoutParams) p);
        }
        return new android.support.design.widget.AppBarLayout.LayoutParams(p);
    }

    int hh1 = 0;
    int hh2 = 0;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int height = 0;
        hh2 = 0;
        for (int i = 0, n = getChildCount(); i < n; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() == View.GONE) {
                continue;
            }
            android.support.design.widget.AppBarLayout.LayoutParams lp = (android.support.design.widget.AppBarLayout.LayoutParams) view.getLayoutParams();
            int scrollFlags = lp.getScrollFlags();
            if ((scrollFlags & android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS) != 0) {
                hh1 = height;
            } else if ((scrollFlags & android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                hh2 = height;
            }
            height += view.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
        }
        if (hh2 <= 0) {
            hh2 = height;
        }
    }

    @Override
    protected int getMaxDragOffset() {
        return -hh2;
    }

    public void onPartnerScrollStart() {
        stopScroll();
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }


    public void onPartnerScrollStop() {
        final int mt = -(int) ViewHelper.getTranslationY(this);
        if (mt < hh2 && mt >= (hh2 + hh1) / 2) {
            valueAnimator = ValueAnimator.ofFloat(mt, hh2);
        } else if (mt > hh1 && mt < (hh2 + hh1) / 2) {
            valueAnimator = ValueAnimator.ofFloat(mt, hh1);
        }

        if (valueAnimator == null) {
            return;
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int now = mt;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newp = (int) Float.parseFloat(animation.getAnimatedValue().toString());
                if (mPartner != null) {
                    mPartner.scrollBy(0, newp - now);
                    now = newp;
                }
            }
        });
        valueAnimator.start();
    }

    public void onPartnerScrolled(boolean hasY, int y, int dy) {
        int currentOffset = getCurrentOffset();
        int t = y;
        if (!hasY || (y < 0 && currentOffset > y - dy)) {
            t = currentOffset;
            t += dy;
            if (t > -hh1) {
                t = -hh1;
            }
        }
        if (t < -hh2) {
            t = -hh2;
        }
        setOffset(t);
    }

    public void setPartner(PartnerImpt mPartner) {
        if (this.mPartner != null) {
            this.mPartner.setPartner(null);
        }
        this.mPartner = mPartner;
        this.mPartner.setPartner(this);
    }

    public PartnerImpt getPartner() {
        return mPartner;
    }

    @Override
    protected int onScroll(int dy) {
        if (this.mPartner != null) {
            this.mPartner.stopScroll();
            if (dy > 0) {
                int bOffset = this.mPartner.getBottomItemOffset();
                if (bOffset != Integer.MAX_VALUE) {
                    if (bOffset <= 0) {
                        dy = 0;
                    } else if (bOffset > 0 && bOffset - dy <= 0) {
                        dy = bOffset;
                    }
                }
            } else if (dy < 0) {
                int tOffset = this.mPartner.getTopItemOffset();
                if (tOffset != Integer.MAX_VALUE) {
                    if (tOffset >= 0) {
                        dy = 0;
                    } else if (tOffset < 0 && tOffset - dy >= 0) {
                        dy = tOffset;
                    }
                }
            }
            this.mPartner.scrollBy(0, dy);
            return dy;
        }
        return 0;
    }
}
