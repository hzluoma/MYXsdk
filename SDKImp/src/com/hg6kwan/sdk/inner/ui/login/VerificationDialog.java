package com.hg6kwan.sdk.inner.ui.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;

/**
 * Created by Roman on 2017/4/27.
 */

public class VerificationDialog extends BaseDialog {

    public VerificationDialog(TYPE type, Context context) {
        super(TYPE.ID_VERIFICATION, context);
    }

    public VerificationDialog(Context context) {
        this(TYPE.ID_VERIFICATION, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(createUI(mContext),new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    private LinearLayout createUI(Context context){
        //dialog的根布局
        LinearLayout linearLayout = uiUtils.createLayout(uiUtils.LAYOUT.LOGIN_BASE,context);
        linearLayout.setBackgroundColor(0xFFFAFAFA);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        linearLayout.setLayoutParams(layoutParams);
        //设置权重总和
        linearLayout.setWeightSum(1);
        //设置子控件排列方向
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //旗帜imageView的父布局
        LinearLayout ll_flag_parent = new LinearLayout(mContext );
        //父布局属性
        LinearLayout.LayoutParams llp_flag_parent = new LinearLayout.LayoutParams(-1, 0, 0.5f);
        //设置子控件排列方向
        ll_flag_parent.setOrientation(LinearLayout.HORIZONTAL);
        //设置权重总和
        ll_flag_parent.setWeightSum(1);
        //设置居中
        ll_flag_parent.setGravity(Gravity.CENTER_HORIZONTAL);
        llp_flag_parent.setMargins(0, (int) (mHeight * 0.08),0,0);
        ll_flag_parent.setLayoutParams(llp_flag_parent);

        //旗帜图案的容器imageView
        ImageView iv_flag = new ImageView(mContext);
        //imageView的宽高属性
        LinearLayout.LayoutParams llp_flag = new LinearLayout.LayoutParams((int)(mHeight * 0.3 /
                0.8) , -1);
        //获取图片的bitmap
        Bitmap bitmap_flag = uiState.getResMap().get("flag");
        //设置图片
        iv_flag.setImageBitmap(bitmap_flag);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(uiState.getResMap().get("flag"));
        iv_flag.setImageDrawable(bitmapDrawable);
        //设置缩放类型
        iv_flag.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //字体图片的父布局
        LinearLayout ll_text_parent = new LinearLayout(mContext);
        //父布局属性
        LinearLayout.LayoutParams llp_iv_parent = new LinearLayout.LayoutParams(-1, 0, 0.19f);
        //设置子控件排列方向
        ll_text_parent.setOrientation(LinearLayout.VERTICAL);
        //设置居中
        ll_text_parent.setGravity(Gravity.CENTER_VERTICAL);
        llp_iv_parent.setMargins(0,0,0,0);

        //文字图片的容器
        ImageView iv_text = new ImageView(mContext);
        iv_text.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams llp_iv_text = new LinearLayout.LayoutParams(-1 , -1);

        Bitmap bmp_text = uiState.getResMap().get("text_verification");
        //设置图片
        iv_text.setImageBitmap(bmp_text);
        iv_text.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //按钮
        Button button = new Button(mContext);
        //按钮的属性
        LinearLayout.LayoutParams llp_btn = new LinearLayout.LayoutParams((int) (mWidth *
                0.89f), 0,0.2f);
        llp_btn.setMargins((int)(mWidth * 0.053),(int) (mHeight * 0.05f),(int)(mWidth * 0.053),0);

        //按钮的shape
        GradientDrawable btn_shape = new GradientDrawable();
        btn_shape.setShape(GradientDrawable.RECTANGLE);
        btn_shape.setColor(0xfffea501);
        //描边
        btn_shape.setStroke(uiUtils.getDipSize(1), 0xffc9c9c9);
        //圆角
        btn_shape.setCornerRadius(6.0f);
        //设置Shape
        button.setBackgroundDrawable(btn_shape);
        //设置按钮内容
        button.setText("前往完善");
        //设置字体颜色
        button.setTextColor(0xffffffff);
        //设置字体大小
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        //设置点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlUI.getInstance().closeSDKUI();
                //打开实名认证的网页窗口
                ControlUI.getInstance().startUI(mContext, ControlUI.WEB_TYPE.ID_VERIFICATION);
            }
        });

        //添加View
        ll_flag_parent.addView(iv_flag,llp_flag);
        ll_text_parent.addView(iv_text,llp_iv_text);
        linearLayout.addView(ll_flag_parent,llp_flag_parent);
        linearLayout.addView(ll_text_parent,llp_iv_parent);
        linearLayout.addView(button,llp_btn);
        return linearLayout;
    }
}
