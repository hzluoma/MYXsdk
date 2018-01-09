package com.hg6kwan.sdk.inner.ui.login;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.uiUtils;

/**
 * Created by xiaoer on 2016/10/26.
 */

public class OtherDialog extends LoginBase implements View.OnClickListener{
    private TextView tv_qq;
    public OtherDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        close_layout.setVisibility(View.INVISIBLE);

        this.setCancelable(false);
        back_layout.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        if (v == back_layout){
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.FORGET);
        }else if (v == tv_qq){
            ClipboardManager myClipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            myClipboard.setText("800811699");
            Toast.makeText(mContext,"已复制qq:800811699于剪切板中！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected LinearLayout createContent(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView tv_title = uiUtils.creatText(uiUtils.TEXT.OTHER_TITLE,context);
        TextView tv_url1 = uiUtils.creatText(uiUtils.TEXT.OTHER_URL1,context);
        TextView tv_url2 = uiUtils.creatText(uiUtils.TEXT.OTHER_URL2,context);
        tv_qq = uiUtils.creatText(uiUtils.TEXT.OTHER_QQ,context);
        TextView tv_tel = uiUtils.creatText(uiUtils.TEXT.OTHER_TEL,context);

        tv_title.setTextSize(ajustFontSize(14));
        tv_url1.setTextSize(ajustFontSize(12));
        tv_url2.setTextSize(ajustFontSize(12));
        tv_qq.setTextSize(ajustFontSize(12));
        tv_tel.setTextSize(ajustFontSize(12));

        LinearLayout ly_url = uiUtils.createLayout(uiUtils.LAYOUT.OTHER,context);
        ly_url.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ly_qq = uiUtils.createLayout(uiUtils.LAYOUT.OTHER,context);
        ly_url.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout ly_tel = uiUtils.createLayout(uiUtils.LAYOUT.OTHER,context);
        ly_url.setOrientation(LinearLayout.HORIZONTAL);
        ly_url.setWeightSum(10);
        ly_qq.setWeightSum(10);
        ly_tel.setWeightSum(10);

        ly_url.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        ly_url.addView(createImageLayout("home",2,context),
                getLayoutParamH(1));
        ly_url.addView(tv_url1);
        ly_url.addView(tv_url2);

        ly_qq.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        ly_qq.addView(createImageLayout("qq",2,context),
                getLayoutParamH(1));
        ly_qq.addView(tv_qq);

        ly_tel.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        ly_tel.addView(createImageLayout("dialing_light",2,context),
                getLayoutParamH(1));
        ly_tel.addView(tv_tel);

        layout.addView(new View(context),getLayoutParamV(0.5f));
        layout.addView(tv_title,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_url,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_qq,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_tel,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(1.6f));

        return layout;
    }
}
