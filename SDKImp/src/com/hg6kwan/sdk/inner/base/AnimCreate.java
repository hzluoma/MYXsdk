package com.hg6kwan.sdk.inner.base;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

/**
 * Created by bt on 2016/7/7.
 */
public class AnimCreate {
    public static void RotateAlways(View v){
        RotateAnimation anim = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(1000);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        v.startAnimation(anim);
    }
}
