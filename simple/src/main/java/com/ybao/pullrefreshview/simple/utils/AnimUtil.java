package com.ybao.pullrefreshview.simple.utils;

import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Ybao on 2015/11/7 0007.
 */
public class AnimUtil {
    public static void startRotation(View view, float toRotation) {
        ObjectAnimator.ofFloat(view, "rotation", ViewHelper.getRotation(view), toRotation).start();
    }

    public static void startRotation(View view, float toRotation, long duration, long startDelay) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", ViewHelper.getRotation(view), toRotation).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();
    }

    public static void startRotation(View view, float toRotation, long duration, long startDelay, int times) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", ViewHelper.getRotation(view), toRotation).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setRepeatCount(times);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    public static void startShow(View view, float fromAlpha, long duration, long startDelay) {
        ViewHelper.setAlpha(view, fromAlpha);
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", fromAlpha, 1f).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();
    }

    public static void startHide(final View view, long duration, long startDelay) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", ViewHelper.getAlpha(view), 0f).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.start();

    }

    public static void startShow(View view, float fromAlpha) {
        ViewHelper.setAlpha(view, fromAlpha);
        view.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(view, "alpha", fromAlpha, 1f).start();
    }

    public static void startHide(final View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(view, "alpha", ViewHelper.getAlpha(view), 0f).start();

    }


    public static void startScale(final View view, float toScale) {
        ObjectAnimator.ofFloat(view, "scaleX", ViewHelper.getScaleX(view), toScale).start();
        ObjectAnimator.ofFloat(view, "scaleY", ViewHelper.getScaleY(view), toScale).start();
    }

    public static void startScale(final View view, float toScale, long duration, long startDelay, Interpolator setInterpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "scaleX", ViewHelper.getScaleX(view), toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
        objectAnimator = ObjectAnimator.ofFloat(view, "scaleY", ViewHelper.getScaleY(view), toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
    }

    public static void startScale(final View view, float fromScale, float toScale) {
        ObjectAnimator.ofFloat(view, "scaleX", fromScale, toScale).start();
        ObjectAnimator.ofFloat(view, "scaleY", fromScale, toScale).start();
    }


    public static void startScale(final View view, float fromScale, float toScale, long duration, long startDelay, Interpolator setInterpolator) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "scaleX", fromScale, toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
        objectAnimator = ObjectAnimator.ofFloat(view, "scaleY", fromScale, toScale).setDuration(duration);
        objectAnimator.setStartDelay(startDelay);
        objectAnimator.setInterpolator(setInterpolator);
        objectAnimator.start();
    }

    public static void startFromY(final View view, float fromY) {
        ObjectAnimator.ofFloat(view, "translationY", fromY, 0).start();
    }

    public static void startToY(final View view, float toY) {
        ObjectAnimator.ofFloat(view, "translationY", 0, toY).start();
    }
}
