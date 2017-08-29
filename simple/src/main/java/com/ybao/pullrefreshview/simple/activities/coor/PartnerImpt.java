package com.ybao.pullrefreshview.simple.activities.coor;

/**
 * Created by Y-bao on 2017/8/28 0028.
 */

public interface PartnerImpt {
    void setPartner(AppBarLayout appBarLayout);

    boolean canHeaderDrag();

    int getBottomItemOffset();

    int getLeadingItemOffset();

    void scrollBy(int paramInt1, int paramInt2);

    void stopScroll();
}
