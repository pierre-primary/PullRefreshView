package com.ybao.pullrefreshview.simple.utils;

import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Ybao on 2015/11/7 0007.
 */
public class AnimUtil {
    public static Animator startRotation(View view, float toRotation) {
        Animator animator = ObjectAnimator.ofFloat(view, "rotation", ViewHelper.getRotation(view), toRotation);
        animator.start();
        return animator;
    }

    public static Animator startRotation(View view, float toRotation, long duration, long startDelay) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", ViewHelper.getRotation(view), toRotation).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();
        return objectAnimator;
    }

    public static Animator startRotation(View view, float toRotation, long duration, long startDelay, int times) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", ViewHelper.getRotation(view), toRotation).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setRepeatCount(times);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
        return objectAnimator;
    }

    public static Animator startShow(View view, float fromAlpha, long duration, long startDelay) {
        ViewHelper.setAlpha(view, fromAlpha);
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", fromAlpha, 1f).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();
        return objectAnimator;
    }

    public static Animator startHide(final View view, long duration, long startDelay) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", ViewHelper.getAlpha(view), 0f).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();
        return objectAnimator;
    }

    public static Animator startShow(View view, float fromAlpha) {
        ViewHelper.setAlpha(view, fromAlpha);
        view.setVisibility(View.VISIBLE);
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", fromAlpha, 1f);
        animator.start();
        return animator;
    }

    public static Animator startHide(final View view) {
        view.setVisibility(View.VISIBLE);
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", ViewHelper.getAlpha(view), 0f);
        animator.start();
        return animator;
    }


    public static Animator startScale(final View view, float toScale) {
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(ObjectAnimator.ofFloat(view, "scaleX", ViewHelper.getScaleX(view), toScale), ObjectAnimator.ofFloat(view, "scaleY", ViewHelper.getScaleY(view), toScale));
        animator.start();
        return animator;
    }

    public static Animator startScale(final View view, float toScale, long duration, long startDelay, Interpolator setInterpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "scaleX", ViewHelper.getScaleX(view), toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
        objectAnimator = ObjectAnimator.ofFloat(view, "scaleY", ViewHelper.getScaleY(view), toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
        return objectAnimator;
    }

    public static Animator startScale(final View view, float fromScale, float toScale) {
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(ObjectAnimator.ofFloat(view, "scaleX", fromScale, toScale), ObjectAnimator.ofFloat(view, "scaleY", fromScale, toScale));
        animator.start();
        return animator;
    }


    public static Animator startScale(final View view, float fromScale, float toScale, long duration, long startDelay, Interpolator setInterpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "scaleX", fromScale, toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
        objectAnimator = ObjectAnimator.ofFloat(view, "scaleY", fromScale, toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
        return objectAnimator;
    }

    public static Animator startFromY(final View view, float fromY) {
        Animator animator = ObjectAnimator.ofFloat(view, "translationY", fromY, 0);
        animator.start();
        return animator;
    }

    public static Animator startToY(final View view, float toY) {
        Animator animator = ObjectAnimator.ofFloat(view, "translationY", 0, toY);
        animator.start();
        return animator;
    }
}
