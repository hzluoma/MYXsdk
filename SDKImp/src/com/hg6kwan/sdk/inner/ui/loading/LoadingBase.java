package com.hg6kwan.sdk.inner.ui.loading;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hg6kwan.sdk.inner.base.AnimCreate;
import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Constants;

/**
 * Created by xiaoer on 2016/7/7.
 */
public class LoadingBase extends BaseDialog {
    protected String title;
    protected String msg;

    protected Context mContext;
    protected long startTime;

    protected ImageView iv_loading;

    public LoadingBase(Context context, String title, String msg){
        super(TYPE.LOADING_DIALOG,context);
        mContext=context;
        this.title=title;
        this.msg=msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime=System.currentTimeMillis();

        this.setContentView(createUI(mContext));
        AnimCreate.RotateAlways(iv_loading);
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
    }

    protected void showFixTime(){
        long custTime = System.currentTimeMillis()-startTime;
        if(custTime < Constants.LOADING_DIALOG_SHOW_TIME){
            SystemClock.sleep(Constants.LOADING_DIALOG_SHOW_TIME-custTime);
        }
    }

    private LinearLayout createUI(Context context){
        LinearLayout layout = uiUtils.createLayout(uiUtils.LAYOUT.LOADING_BG,context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout ly_user = createImageLayout("loading_user",9,context);
        LinearLayout ly_content = new LinearLayout(context);
        ly_content.setOrientation(LinearLayout.VERTICAL);

        //title
        LinearLayout ly_top = new LinearLayout(context);
        ly_top.setOrientation(LinearLayout.VERTICAL);
        ly_top.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        TextView tv_title = new TextView(context);
        tv_title.setText(title);
        tv_title.setTextSize(16);
        tv_title.setTextColor(Color.BLACK);
        ly_top.addView(tv_title);

        LinearLayout ly_bottom = new LinearLayout(context);
        ly_bottom.setOrientation(LinearLayout.HORIZONTAL);
        ly_bottom.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        //loading
        LinearLayout ly_loading = new LinearLayout(context);
        ly_loading.setOrientation(LinearLayout.VERTICAL);
        ly_loading.setGravity(Gravity.CENTER_VERTICAL);
        ly_loading.setWeightSum(12);
        iv_loading = new ImageView(context);
        iv_loading.setImageBitmap(uiUtils.getResBitmap("loading_spinner"));
        ly_loading.addView(iv_loading,getLayoutParamV(6));
        //msg
        LinearLayout ly_msg = new LinearLayout(context);
        ly_msg.setOrientation(LinearLayout.VERTICAL);
        ly_msg.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv_msg = new TextView(context);
        tv_msg.setText(msg);
        tv_msg.setTextSize(14);
        tv_msg.setTextColor(0x80000000);
        ly_msg.addView(tv_msg);

        ly_bottom.addView(ly_loading,getLayoutParamH(1));
        ly_bottom.addView(ly_msg,getLayoutParamH(7));

        ly_content.addView(ly_top,getLayoutParamV(3));
        ly_content.addView(ly_bottom,getLayoutParamV(2));

        layout.addView(ly_user,getLayoutParamH(1));
        layout.addView(ly_content,getLayoutParamH(2));
        return layout;
    }

}
