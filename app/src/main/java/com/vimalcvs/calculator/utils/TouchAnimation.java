package com.vimalcvs.calculator.utils;

import android.animation.ObjectAnimator;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class TouchAnimation implements View.OnTouchListener {
    private static final float SCALE_DOWN_FACTOR = 0.8f;
    private static final long ANIMATION_DURATION = 150;

    private final View view;
    private ObjectAnimator xAnimator;
    private ObjectAnimator yAnimator;

    public TouchAnimation(View view) {
        this.view = view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
                    startScaleDownAnimation();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_RELEASE);
                    startScaleUpAnimation();
                    break;
                default:
                    break;
            }
        } catch (Exception ignored) {

        }
        return false;
    }

    private void startScaleDownAnimation() {
        cancelAnimations(); // 取消之前的动画

        xAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, SCALE_DOWN_FACTOR);
        yAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, SCALE_DOWN_FACTOR);

        xAnimator.setDuration(ANIMATION_DURATION);
        yAnimator.setDuration(ANIMATION_DURATION);

        xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        yAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        xAnimator.start();
        yAnimator.start();
    }

    private void startScaleUpAnimation() {
        cancelAnimations(); // 取消之前的动画

        xAnimator = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.0f);
        yAnimator = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.0f);

        xAnimator.setDuration(ANIMATION_DURATION);
        yAnimator.setDuration(ANIMATION_DURATION);

        xAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        yAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        xAnimator.start();
        yAnimator.start();
    }

    private void cancelAnimations() {
        if (xAnimator != null && xAnimator.isRunning()) {
            xAnimator.cancel();
        }
        if (yAnimator != null && yAnimator.isRunning()) {
            yAnimator.cancel();
        }
    }
}
