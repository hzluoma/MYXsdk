package com.hg6kwan.sdk.inner.ui.login;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.uiUtils;

import java.util.Arrays;

/**
 * Created by xiaoer on 2016/10/26.
 * 登录，注册的基础页面，实现了标题
 * 具体内容留给相应的页面类去实现
 */

public abstract class LoginBase extends BaseDialog {
    protected LinearLayout back_layout;
    protected LinearLayout close_layout;

    public LoginBase(Context context) {
        super(TYPE.LOGIN_DIALOG, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(createUI(mContext),new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    //页面中输入框 editText 基础占比(横条总占比)
    protected final float input_weight_h = 10f;

    //创建登录界面(登录，注册，手机注册，忘记密码，找回密码)，一键注册暂时不要
    private LinearLayout createUI(Context context){
        //标题和内容，上下比例
        final float[] ui_weights_v = new float[]{1f,3.5f};
        //内容页，横向居中占比
        final float ui_content_weights_h = 7f;

        LinearLayout layout = uiUtils.createLayout(uiUtils.LAYOUT.LOGIN_BASE,context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout layout_content = new LinearLayout(context);
        layout_content.setOrientation(LinearLayout.HORIZONTAL);
        layout_content.setGravity(Gravity.CENTER);
        layout_content.setWeightSum(ui_content_weights_h+2);
        layout_content.addView(createContent(context),getLayoutParamH(ui_content_weights_h));

        layout.addView(createTitle(context), getLayoutParamV(ui_weights_v[0]));
        layout.addView(layout_content, getLayoutParamV(ui_weights_v[1]));
        return layout;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private LinearLayout createTitle(Context context){
        final float[] weights_h = new float[]{1.5f,7f,1f};
        final float[] left_weights_v = new float[]{1f,1.3f,2.5f};
        final float[] logo_weights_v = new float[]{1f,4f,1f};
        final float[] right_weights_v = new float[]{0.3f,0.8f,1f};

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        //////////////////////////////////////////////////

        //返回按钮
        LinearLayout left_layout = new LinearLayout(context);
        left_layout.setOrientation(LinearLayout.VERTICAL);
        left_layout.setWeightSum(getSum(left_weights_v));

        back_layout = createImageLayout("btn_back",
                Arrays.copyOfRange(left_weights_v,0,2),context);

        left_layout.addView(back_layout,getLayoutParamV(getSum(left_weights_v,2)));

        //logo
        LinearLayout logo_layout = createImageLayout("logo_land",
                logo_weights_v,context);

        //关闭按钮
        LinearLayout right_layout = new LinearLayout(context);
        right_layout.setOrientation(LinearLayout.VERTICAL);
        right_layout.setWeightSum(getSum(right_weights_v));

        close_layout = createImageLayout("close_big",
                Arrays.copyOfRange(right_weights_v,0,2),context);

        right_layout.addView(close_layout,getLayoutParamV(getSum(right_weights_v,2)));

        ///////////////////////////////////////////////
        layout.addView(left_layout,getLayoutParamH(weights_h[0]));
        layout.addView(logo_layout,getLayoutParamH(weights_h[1]));
        layout.addView(right_layout,getLayoutParamH(weights_h[2]));

        return layout;
    }

    protected float getSum(float[] fs){
        float sum = 0;
        for (float f:fs){
            sum += f;
        }
        return sum;
    }

    protected float getSum(float[] fs,int len){
        float sum = 0;
        int count = len;
        for (float f:fs){
            sum += f;
            count--;
            if (count <= 0)
                break;
        }
        return sum;
    }

    //具体实现内容留给子类去实现
    protected abstract LinearLayout createContent(Context context);
}
