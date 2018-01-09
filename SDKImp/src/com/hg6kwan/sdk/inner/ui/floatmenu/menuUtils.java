package com.hg6kwan.sdk.inner.ui.floatmenu;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;

/**
 * Created by xiaoer on 2016/11/1.
 */

public class menuUtils {
    private static final String TAG = "menuUtils";

    public static AnimationSet getExitScaleAlphaAnimation(Animation.AnimationListener mAnimationListener) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f));
        animationSet.addAnimation(new AlphaAnimation(1f, 0f));
        animationSet.setDuration(500);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(mAnimationListener);
        return animationSet;
    }

    public static AnimationSet getScaleAlphaAnimation(int duration, Animation.AnimationListener mAnimationListener) {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f));
        animationSet.addAnimation(new AlphaAnimation(0.5f, 1.0f));
        animationSet.setDuration(duration);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(mAnimationListener);
        return animationSet;
    }

    public static AnimationSet getScaleAlphaAnimation(Animation.AnimationListener mAnimationListener) {
        ScaleAnimation mScaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mScaleAnimation.setInterpolator(new LinearInterpolator());
        AlphaAnimation mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAlphaAnimation.setInterpolator(new LinearInterpolator());

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(mScaleAnimation);
        animationSet.addAnimation(mAlphaAnimation);
        animationSet.setDuration(200);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.setFillAfter(false);
        animationSet.setAnimationListener(mAnimationListener);
        return animationSet;
    }

    public static int getDimension(Context context, int id) {
        return context.getResources().getDimensionPixelSize(id);
    }

    public static int px2dp(float pxVal, Context mContext) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pxVal, mContext.getResources().getDisplayMetrics()));
    }

    public static int dp2Px(float dp, Context mContext) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                mContext.getResources().getDisplayMetrics());
    }

    public static final FrameLayout.LayoutParams createLayoutParams(int width, int height) {
        return new FrameLayout.LayoutParams(width, height);
    }

    public static final FrameLayout.LayoutParams createMatchParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public static final FrameLayout.LayoutParams createWrapParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static final FrameLayout.LayoutParams createWrapMatchParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public static final FrameLayout.LayoutParams createMatchWrapParams() {
        return createLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //    public static void setInsets(Activity context, View view) {
    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
    //        SystemBarTintManager tintManager = new SystemBarTintManager(context);
    //        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
    //        view.setPadding(0, config.getPixelInsetTop(false), config.getPixelInsetRight(), config.getPixelInsetBottom());
    //    }
    //
    //    public static int getInsetsTop(Activity context, View view) {
    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return 0;
    //        SystemBarTintManager tintManager = new SystemBarTintManager(context);
    //        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
    //        return config.getPixelInsetTop(false);
    //    }
    //
    //    public static int getInsetsBottom(Activity context, View view) {
    //        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return 0;
    //        SystemBarTintManager tintManager = new SystemBarTintManager(context);
    //        SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
    //        return config.getPixelInsetBottom();
    //    }
}
