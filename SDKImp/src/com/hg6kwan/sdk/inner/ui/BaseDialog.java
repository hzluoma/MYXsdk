package com.hg6kwan.sdk.inner.ui;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.R.string.no;


/**
 * Created by xiaoer on 2016/5/23.
 */
public class BaseDialog extends Dialog {
    protected enum TYPE {
        LOGIN_DIALOG,
        FLOAT_WEB,
        FLOAT_MENU,
        LOADING_DIALOG,
        BINDING_TIP,
        ID_VERIFICATION,
        NOTICE
    }

    //登录界面 尺寸参数比例:
    //LOGIN
    protected final float LOGIN_WIDTH_LAND = 0.7F;
    protected final float LOGIN_WIDTH_PORT = 0.95F;
    protected final float LOGIN_HEIGHT_WIDTH = 0.65F;

    //floatMenu
    protected final float FMENU_WIDTH_LAND = 0.5F;
    protected final float FMENU_WIDTH_PORT = 0.85F;
    protected final float FMENU_HEIGHT_WIDTH = 0.5F;

    //floatWeb(横竖屏对应比例不同)
    protected final float FWEB_WIDTH_LAND = 0.9F;
    protected final float FWEB_HEIGTH_LAND = 0.9F;

    protected final float FWEB_WIDTH_PORT = 0.9F;
    protected final float FWEB_HEIGHT_PORT = 0.9F;

    //loading_tip
    protected final float TIP_WIDTH_LAND = 0.4F;
    protected final float TIP_WIDTH_PORT = 0.80F;
    protected final float TIP_HEIGHT_WIDTH = 0.95F;

    //loading
    public final int LOADING_DIALOG_WIDTH = 370;
    public final int LOADING_DIALOG_HEIGHT = 140;
    public final int LOADING_DIALOG_Y = 50;

    //IDVerification
    public final float VERIFICATION_WIDTH_LAND = 0.48F;
    public final float VERIFICATION_WIDTH_PORT = 0.858F;
    public final float VERIFICATION_HEIGHT_WIDTH = 1.05F;

    //NoticeDialog
    public final float NOTICE_WIDTH_LAND = 0.73F;
    public final float NOTICE_WIDTH_PORT = 0.816F;
    public final float NOTICE_HEIGHT_WIDTH_PORT = 1.4F;
    public final float NOTICE_HEIGHT_WIDTH_LAND = 0.622F;

    protected final float size_input = 12;
    protected final float size_btn = 14;
    protected final float size_btn_login = 13;
    protected final float size_treaty = 8f;
    protected final float size_find_pass = 10f;
    protected final float size_tips = 7.5f;

    ///////////////////////////////////
    protected TYPE type;
    protected Context mContext;
    protected float sizeRatio;

    protected float mWidth;
    protected float mHeight;
    protected int mX = 0;
    protected int mY = 0;
    ////////////////////////////////////

    public BaseDialog(TYPE type, Context context) {
        super(context);
        mContext = context;
        this.type = type;
    }

    private void initSize() {
        switch (type) {
            case LOGIN_DIALOG:
                if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mWidth = (int) (uiState.screenWidth * LOGIN_WIDTH_LAND);
                } else {
                    mWidth = (int) (uiState.screenWidth * LOGIN_WIDTH_PORT);
                }
                mHeight = (int) (mWidth * LOGIN_HEIGHT_WIDTH);
                break;
            case FLOAT_WEB:
                if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mWidth = (int) (uiState.screenWidth * FWEB_WIDTH_LAND);
                    mHeight = (int) (uiState.screenHeight * FWEB_HEIGTH_LAND);
                } else {
                    mWidth = (int) (uiState.screenWidth * FWEB_WIDTH_PORT);
                    mHeight = (int) (uiState.screenHeight * FWEB_HEIGHT_PORT);
                }
                break;
            case FLOAT_MENU:
                if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mWidth = (int) (uiState.screenWidth * FMENU_WIDTH_LAND);
                } else {
                    mWidth = (int) (uiState.screenWidth * FMENU_WIDTH_PORT);
                }
                mHeight = (int) (mWidth * FMENU_HEIGHT_WIDTH);
                break;
            case LOADING_DIALOG:
                mWidth = LOADING_DIALOG_WIDTH;
                mHeight = LOADING_DIALOG_HEIGHT;
                mY = LOADING_DIALOG_Y;
                break;
            case BINDING_TIP:
                if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mWidth = (int) (uiState.screenWidth * TIP_WIDTH_LAND);
                } else {
                    mWidth = (int) (uiState.screenWidth * TIP_WIDTH_PORT);
                }
                mHeight = (int) (mWidth * TIP_HEIGHT_WIDTH);
                break;
            case ID_VERIFICATION:
                if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mWidth = (int) (uiState.screenWidth * VERIFICATION_WIDTH_LAND);
                } else {
                    mWidth = (int) (uiState.screenWidth * VERIFICATION_WIDTH_PORT);
                }
                mHeight = (int) (mWidth * VERIFICATION_HEIGHT_WIDTH);
                break;
            case NOTICE:
                if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    mWidth = (int) (uiState.screenWidth * NOTICE_WIDTH_LAND);
                    mHeight = (int) (mWidth * NOTICE_HEIGHT_WIDTH_LAND);
                } else {
                    mWidth = (int) (uiState.screenWidth * NOTICE_WIDTH_PORT);
                    mHeight = (int) (mWidth * NOTICE_HEIGHT_WIDTH_PORT);
                }

                break;
        }

        //初始化时计算好窗体比例，用于后续计算字体大小(这里只会计算一次)
//        float ratioWidth = getRatio(this.type,uiState.screenOrientation);
//        float widthReal = ratioWidth * uiState.screenWidth;
//        float widthFix = 0;
//        if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//            widthFix = getRatio(this.type,ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) * 750;
//        }else{
//            widthFix = getRatio(this.type,ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) * 1080;
//        }
        sizeRatio = (float) Math.sqrt(mWidth * mWidth + mHeight * mHeight) / uiState.screenDensityDpi / 2;
        //LogUtil.d("sizeRatio","sizeRatio="+sizeRatio);
    }

    protected LinearLayout.LayoutParams getImageLayoutParamV(float weight) {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                weight*sizeRatio);
    }

    protected LinearLayout.LayoutParams getImageLayoutParamH(float weight) {
        return new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                weight*sizeRatio);
    }

    protected LinearLayout.LayoutParams getLayoutParamV(float weight) {
        return new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                weight);
    }

    protected LinearLayout.LayoutParams getLayoutParamH(float weight) {
        return new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                weight);
    }

    protected LinearLayout createImageLayout(String resName, float weight, Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setWeightSum(weight + 2);

        ImageView iv = new ImageView(context);
        iv.setImageBitmap(uiUtils.getResBitmap(resName));
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        layout.addView(iv, getLayoutParamV(weight));
        return layout;
    }

    protected LinearLayout createImageLayout(String image_res, float[] weights, Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        ImageView iv = new ImageView(context);
        iv.setImageBitmap(uiUtils.getResBitmap(image_res));
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        if (weights.length == 2) {
            layout.setGravity(Gravity.BOTTOM);
            layout.setWeightSum(weights[0] + weights[1]);
            layout.addView(iv, getLayoutParamV(weights[1]));
        } else if (weights.length == 3) {
            layout.setGravity(Gravity.CENTER);
            layout.setWeightSum(weights[0] + weights[1] + weights[2]);
            layout.addView(iv, getLayoutParamV(weights[1]));
        }
        return layout;
    }

    protected TextView createTextView(String message,float size){
        TextView textView = new TextView(mContext);
        textView.setText(message);
        textView.setTextSize(ajustFontSize(size));
        return textView;

    }

    protected TextView createButton(String count,float size){
        Button button = new Button(mContext);
        button.setText(count);
        button.setTextSize(ajustFontSize(size));
        button.setOnClickListener(countListener);
        return button;
    }

    protected TextView createEditText(float size){
        EditText text = new EditText(mContext);
        text.setTextSize(ajustFontSize(size));

        return text;
    }

    View.OnClickListener countListener =new View.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置背景为透明，不变暗
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getWindow().setDimAmount(0);
        if (type == TYPE.LOADING_DIALOG) {
            getWindow().setGravity(Gravity.TOP);
        }
        initSize();
    }

    @Override
    public void show() {
        super.show();
        //设置窗口大小
        WindowManager.LayoutParams p = this.getWindow().getAttributes();

        p.width = (int) mWidth;
        p.height = (int) mHeight;
        p.x = mX == 0 ? p.x : mX;
        p.y = mY == 0 ? p.y : mY;
        //LogUtil.d("dialog", "dialogWidth = " + mWidth + " , dialogHeight = " + mHeight);
        this.getWindow().setAttributes(p);
    }

    //参数为在竖版时，最小屏幕的情况下能适配的字体大小
    protected float ajustFontSize(float size) {
        return size * sizeRatio;
    }

    private float getRatio(TYPE type, int orient) {
        if (orient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            switch (type) {
                case LOGIN_DIALOG:
                    return LOGIN_WIDTH_LAND;
                case FLOAT_WEB:
                    return FWEB_WIDTH_LAND;
                case FLOAT_MENU:
                    return FMENU_WIDTH_LAND;
                case BINDING_TIP:
                    return TIP_WIDTH_LAND;
            }
        } else {
            switch (type) {
                case LOGIN_DIALOG:
                    return LOGIN_WIDTH_PORT;
                case FLOAT_WEB:
                    return FWEB_WIDTH_PORT;
                case FLOAT_MENU:
                    return FMENU_WIDTH_PORT;
                case BINDING_TIP:
                    return TIP_WIDTH_PORT;
            }
        }
        return 0;
    }
}
