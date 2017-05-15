package com.ybao.pullrefreshview.support.anim;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by ybao on 2017/5/14.
 */

public class AnimGetter implements IAnimGetter {

    @Override
    public Animator createMoveToAnim(int startDelay, int duration, Interpolator interpolator, final AnimListener animListener, float... p) {
        ValueAnimator animator = ValueAnimator.ofFloat(p);
        animator.setStartDelay(startDelay);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animListener.onUpdate((float) animation.getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animListener.onAnimEnd();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                animListener.onAnimCencel();
            }
        });
        return animator;
    }
}
