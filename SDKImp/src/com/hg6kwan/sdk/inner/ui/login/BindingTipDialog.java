package com.hg6kwan.sdk.inner.ui.login;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.floatmenu.FloatMenuManager;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDBDao;

/**
 * Created by xiaoer on 2016/10/30.
 */

public class BindingTipDialog extends BaseDialog implements View.OnClickListener {
    private Button btn_binding;
    private Button btn_ignore_now;
    private Button btn_ignore_always;

    public BindingTipDialog(Context context) {
        super(TYPE.BINDING_TIP,context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(createContent(mContext),new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        btn_binding.setOnClickListener(this);
        btn_ignore_now.setOnClickListener(this);
        btn_ignore_always.setOnClickListener(this);
        this.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        ControlUI.getInstance().closeSDKUI();
        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        if (v == btn_binding){
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.BINDING);
            return;
        }else if (v == btn_ignore_now){
            ControlCenter.getInstance().onLoginResult(baseInfo.loginResult);
        }else if (v == btn_ignore_always){
            baseInfo.login.setTip();
            CommonFunctionUtils.setLoginListToSharePreferences(mContext,baseInfo.loginList);
            ControlCenter.getInstance().onLoginResult(baseInfo.loginResult);
        }
        //查询是否需要弹出消息公告
        NoticeDBDao.getInstance(mContext).isOpenNoticeDialog();
        //登陆成功后开启悬浮窗
        FloatMenuManager.getInstance().startFloatView(mContext);
    }

    protected LinearLayout createContent(Context context) {
        LinearLayout layout = uiUtils.createLayout(uiUtils.LAYOUT.TIP_BG,context);
        layout.setOrientation(LinearLayout.VERTICAL);
        //logo
        LinearLayout ly_logo = createImageLayout("logo_land",10,context);

        LinearLayout ly_content = new LinearLayout(context);
        ly_content.setOrientation(LinearLayout.VERTICAL);

        int padSize = (int) ajustFontSize(5);
        ly_content.setPadding(padSize,padSize,padSize,padSize);

        //tips
        TextView tv_tips = new TextView(context);
        String str_tips = "当前账号未绑定手机\n绑定后可自行找回密码，保护账号安全";
        int bstart = str_tips.indexOf("找回密码，保护账号安全");
        int bend = bstart + "找回密码，保护账号安全".length();

        SpannableString style = new SpannableString(str_tips);
        style.setSpan(new ForegroundColorSpan(Color.BLACK),0,bstart, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.RED),bstart,bend, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new AbsoluteSizeSpan((int) ajustFontSize(12),true)
                ,0,str_tips.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_tips.setText(style);
        tv_tips.setGravity(Gravity.CENTER);
        tv_tips.setLineSpacing(5,1.2f);

        //绑定手机
        btn_binding = uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        btn_binding.setText("绑定手机");
        btn_binding.setTextSize(ajustFontSize(size_btn));

        //忽略本次，不在提示
        btn_ignore_now = uiUtils.createButton(uiUtils.BTN.IGNORE,context);
        btn_ignore_always = uiUtils.createButton(uiUtils.BTN.IGNORE,context);
        btn_ignore_now.setText("忽略本次");
        btn_ignore_now.setTextSize(ajustFontSize(size_btn));
        btn_ignore_always.setText("不再提示");
        btn_ignore_always.setTextSize(ajustFontSize(size_btn));

        LinearLayout ly_ignore = new LinearLayout(context);
        ly_ignore.setOrientation(LinearLayout.HORIZONTAL);
        ly_ignore.addView(btn_ignore_now,getLayoutParamH(5));
        ly_ignore.addView(new View(context),getLayoutParamH(1));
        ly_ignore.addView(btn_ignore_always,getLayoutParamH(5));

        ly_content.addView(tv_tips,getLayoutParamV(2.5f));
        ly_content.addView(btn_binding,getLayoutParamV(1.3f));
        ly_content.addView(new View(context),getLayoutParamV(0.3f));
        ly_content.addView(ly_ignore,getLayoutParamV(1.3f));

        layout.addView(ly_logo,getLayoutParamV(1));
        layout.addView(ly_content,getLayoutParamV(4));

        return layout;
    }
}
