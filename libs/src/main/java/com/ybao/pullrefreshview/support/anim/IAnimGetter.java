package com.ybao.pullrefreshview.support.anim;

import android.view.animation.Interpolator;

import com.nineoldandroids.animation.Animator;

/**
 * Created by ybao on 2017/5/14.
 */

public interface IAnimGetter {
    Animator createMoveToAnim(int offstart, int duration, Interpolator interpolator, AnimListener animListener, float... p);
}
