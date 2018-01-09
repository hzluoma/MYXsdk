package com.hg6kwan.sdk.inner.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.InputType;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hg6kwan.sdk.inner.platform.ControlCenter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static com.hg6kwan.sdk.inner.platform.ControlUI.LOGIN_TYPE.Notice;

/**
 * Created by xiaoer on 2016/10/19.
 */

public class uiUtils {

    //颜色参数：透明，半透明
    public static final int color_transparent = 0x00000000;    //透明
    public static final int color_translucent = 0x88000000;    //半透明
    public static final int color_white       = 0xFFFFFFFF;    //白色
    public static final int color_red         = 0xfff34755;    //红色

    private static final int color_stroke_input = 0xFFD2D2D2;   //线框
    private static final int color_stroke_input_focused = 0xFF00EEEE;   //线框

    private static final int color_btn_login_pressed = 0xFF00ff12;  //登录，注册按钮
    private static final int color_btn_login_pressedN = 0xFF6495ed;

    private static final int color_btn_auth_pressed = 0xFFCCCCCC;   //验证码按钮
    private static final int color_btn_auth_pressedN = 0xFF62ABFF;

    private static final int color_btn_ignore_pressed = 0xFFCCCCCC;
    private static final int color_btn_ignore_pressedN = 0xFFCCCCCC;

    private static final int color_bg_phone = 0xFFF7F7F7;
    private static final int color_bg_menu_top = 0xD0FFFFFF;
    private static final int color_bg_menu_bottom = 0xE0161616;
    private static final int color_bg_web_top = 0xA0000000;
    private static final int color_bg_web_bottom = 0xf0ffffff;

    private static final int color_bg_loading = 0xf0cccccc;
    private static final int color_bg_tip = 0xf0ffffff;


    //尺寸参数线宽，圆角
    private static final int width_stroke_base = 1;  //线框宽度
    private static final float corner_base = 8.5f;    //圆角大小
    private static final float corner_phone = 35f;
    private static final float corner_menu = 15f;
    private static final float corner_loading = 75f;

    //生成形状图
    private static Drawable createDrawable(float corner,int colorBG,int strokeColor,int strokeWidth){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(colorBG);
        gd.setCornerRadius(corner);
        if (strokeWidth != 0 ){
            gd.setStroke(strokeWidth,strokeColor);
        }
        return gd;
    }

    private static Drawable createDrawable(float corner[],int colorBG,int strokeColor,int strokeWidth){
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(colorBG);
        gd.setCornerRadii(corner);

        if (strokeWidth != 0 ){
            gd.setStroke(strokeWidth,strokeColor);
        }
        return gd;
    }

    //LAYOUT类型
    public enum LAYOUT {
        LOGIN_BASE, INPUT, PHONE, INPUT_LINE, OTHER,
        MENU_TOP, MENU_BOTTOM, WEB_TITLE, WEB_BOTTOM,
        LOADING_BG, TIP_BG,NOTICE,

        LINEARLAYOUT,RELATIVELAYOUT,FRAMELAYOUT
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static LinearLayout createLayout(LAYOUT type, Context context){
        LinearLayout ll = new LinearLayout(context);
        switch (type){
            case LOGIN_BASE:
                ll.setBackground(createDrawable(corner_base,color_white,0,0));
                break;
            case INPUT:
                ll.setBackground(createDrawable(corner_base,color_transparent,
                        color_stroke_input,width_stroke_base));
//                StateListDrawable sd = new StateListDrawable();
//                sd.addState(new int[]{android.R.attr.state_focused},
//                        createDrawable(corner_base,color_transparent,color_stroke_input_focused,width_stroke_base));
//                sd.addState(new int[]{},
//                        createDrawable(corner_base,color_transparent,color_stroke_input,width_stroke_base));
//                ll.setBackground(sd);
                break;
            case PHONE:
                ll.setBackground(createDrawable(corner_phone,color_bg_phone,
                        color_stroke_input,width_stroke_base));
                break;
            case INPUT_LINE:
                ll.setBackgroundColor(color_stroke_input);
                ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        width_stroke_base));
                break;
            case OTHER:
                ll.setBackground(createDrawable(corner_base,color_bg_phone,0,0));
                break;
            case MENU_TOP:
                ll.setBackground(createDrawable(new float[]{corner_menu,corner_menu,corner_menu,corner_menu,0,0,0,0},
                        color_bg_menu_top,0,0));
                break;
            case MENU_BOTTOM:
                ll.setBackground(createDrawable(new float[]{0,0,0,0,corner_menu,corner_menu,corner_menu,corner_menu},
                        color_bg_menu_bottom,0,0));
                break;
            case WEB_TITLE:
                ll.setBackground(createDrawable(new float[]{corner_menu,corner_menu,corner_menu,corner_menu,0,0,0,0},
                        color_bg_web_top,0,0));
                break;
            case WEB_BOTTOM:
                ll.setBackground(createDrawable(new float[]{0,0,0,0,corner_menu,corner_menu,corner_menu,corner_menu},
                        color_bg_web_bottom,0,0));
                break;
            case LOADING_BG:
                ll.setBackground(createDrawable(corner_loading,color_bg_loading,0,0));
                break;
            case TIP_BG:
                ll.setBackground(createDrawable(corner_base,color_bg_tip,0,0));
                break;
            case NOTICE:
                ll.setBackground(createDrawable(12f,color_white,0,0));
                break;
        }
        return ll;
    }


    //按钮类型
    public enum BTN {
        LOGIN, AUTH, IGNORE
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Button createButton(BTN type, Context context){
        Button btn = new Button(context);
        btn.setPadding(0,0,0,0);
        StateListDrawable sd = new StateListDrawable();
        switch (type){
            case LOGIN:
                sd.addState(new int[]{android.R.attr.state_pressed},
                        createDrawable(corner_base,color_btn_login_pressed,0,0));
                sd.addState(new int[]{},
                        createDrawable(corner_base,color_btn_login_pressedN,0,0));
                btn.setTextColor(Color.WHITE);
                break;
            case AUTH:
                sd.addState(new int[]{android.R.attr.state_pressed},
                        createDrawable(corner_base,color_btn_auth_pressed,0,0));
                sd.addState(new int[]{},
                        createDrawable(corner_base,color_btn_auth_pressedN,0,0));
                btn.setText("获取验证码");
                btn.setTextColor(Color.WHITE);
                break;
            case IGNORE:
                sd.addState(new int[]{android.R.attr.state_pressed},
                        createDrawable(corner_base,color_btn_ignore_pressed,0,0));
                sd.addState(new int[]{},
                        createDrawable(corner_base,color_btn_ignore_pressedN,0,0));
                btn.setTextColor(0xff777777);
                break;
        }
        btn.setBackground(sd);
        return btn;
    }

    public static Drawable createAuthDrawable(){
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed},
                createDrawable(corner_base,color_btn_auth_pressed,0,0));
        sd.addState(new int[]{},
                createDrawable(corner_base,color_btn_auth_pressedN,0,0));
        return sd;
    }

    public static Drawable createAuthDrawableN(){
        return createDrawable(corner_base,0xFFcccccc,0,0);
    }


    //输入框类型
    public enum INPUT {
        ACCOUT, PASSWORD , PHONE , AUTH
    }
    public static EditText createInput(INPUT type, Context context){
        final int input_text_color = 0xFF3D3D3D;
        final int input_hint_color = 0xFFD2D2D2;
        final int input_max_length = 16;
        final String account_hint = "账号";
        final String password_hint = "密码";
        final String phone_hint = "手机号码";
        final String auth_hint = "验证码";

        EditText et = new EditText(context);
        et.setSingleLine();
        et.setTextColor(input_text_color);
        et.setHintTextColor(input_hint_color);
        et.setIncludeFontPadding(false);
        et.setPadding(0,0,0,0);
        et.setCursorVisible(true);
        et.setMaxEms(input_max_length);

        et.setBackgroundColor(color_transparent);
        switch (type){
            case ACCOUT:
                et.setHint(account_hint);
                break;
            case PASSWORD:
                et.setHint(password_hint);
                et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |
                    InputType.TYPE_CLASS_TEXT);
                break;
            case PHONE:
                et.setHint(phone_hint);
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case AUTH:
                et.setHint(auth_hint);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
        return et;
    }

    public enum TEXT {
        PHONE_NUM, OLD_ACCOUNT, OTHER_TITLE, OTHER_QQ,
        OTHER_URL1, OTHER_URL2, OTHER_TEL
    }
    public static TextView creatText(TEXT type, Context context){
        final int phone_color = 0xfff3f3f3;

        TextView tv = new TextView(context);
        tv.setTextColor(phone_color);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setPadding(0,0,0,0);
        switch (type){
            case PHONE_NUM:
                tv.setText("客服：400-400-4000");
                break;
            case OLD_ACCOUNT:
                tv.setText("已有账号登录");
                break;
            case OTHER_TITLE:
                tv.setText("你可以通过以下方式联系我们 ：");
                tv.setTextColor(0xfff34755);
                break;
            case OTHER_URL1:
                tv.setTextColor(0xff777777);
                tv.setText("官方网址 ：");
                break;
            case OTHER_URL2:
                tv.setTextColor(0xff777777);
                tv.setAutoLinkMask(Linkify.ALL);
                tv.setText("www.6kw.com");
                break;
            case OTHER_QQ:
                tv.setTextColor(0xff777777);
                tv.setText("客服QQ ：800811699");
                break;
            case OTHER_TEL:
                tv.setTextColor(0xff777777);
                tv.setAutoLinkMask(Linkify.ALL);
                tv.setText("客服热线：020-89855765");
                break;
        }
        return tv;
    }

    public static HashMap<String,Bitmap> loadAssetsImg(Context context){
        AssetManager am = context.getAssets();
        try {
            String[] imgs =am.list("qiqu_img");
            if (imgs == null || imgs.length==0)
                return null;
            HashMap<String,Bitmap> map = new HashMap<>(imgs.length);
            for (String imgName : imgs){
                InputStream is = am.open("qiqu_img/"+imgName);
                Bitmap bp = BitmapFactory.decodeStream(is);
                is.close();

                map.put(imgName.substring(0,imgName.indexOf(".")),bp);
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getResBitmap(String resName){
        return uiState.getResMap().get(resName);
    }

// -----------------------------------2016/12/20--------------------------------------------------
    public static ViewGroup createLayout(Context context, LAYOUT type, ViewGroup.LayoutParams
            params) {

        ViewGroup viewGroup = null;

        switch (type) {

            case RELATIVELAYOUT:

                viewGroup = createRelativeLayout(context,params);

                break;

            case LINEARLAYOUT:

                viewGroup = createLinearLayout(context,params);

                break;

            case FRAMELAYOUT:

                viewGroup = createFrameLayout(context,params);

                break;
        }

        return viewGroup;
    }

    private static RelativeLayout createRelativeLayout(Context context, ViewGroup.LayoutParams
            params) {

        RelativeLayout relativeLayout = new RelativeLayout(context);

        relativeLayout.setLayoutParams(params);

        return relativeLayout;

    }

    private static LinearLayout createLinearLayout(Context context, ViewGroup.LayoutParams params) {

        LinearLayout linearLayout = new LinearLayout(context);

        linearLayout.setLayoutParams(params);

        return linearLayout;

    }


    private static ViewGroup createFrameLayout(Context context, ViewGroup.LayoutParams params) {

        FrameLayout frameLayout = new FrameLayout(context);

        frameLayout.setLayoutParams(params);

        return frameLayout;

    }

    //px转dp
    public static int dp2px(int dp) {
        return (int) (dp * uiState.screenDensity);
    }

    //将传入的int数值转换成dip
    public static int getDipSize(int value) {
        Resources res = ControlCenter.getInstance().getmContext().getResources();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res
                .getDisplayMetrics()) + 0.5f);
    }

    //将传入的float数值转换成dip
    public static int getDipSize(float value) {
        Resources res = ControlCenter.getInstance().getmContext().getResources();
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res
                .getDisplayMetrics()) + 0.5f);
    }

    //创建TV
    public static TextView createTextView(Context context) {
        TextView textView = new TextView(context);
        return textView;
    }

    //创建IV
    public static ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        return imageView;
    }

    //创建View
    public static View createView(Context context) {
        View view = new View(context);
        return view;
    }


}
