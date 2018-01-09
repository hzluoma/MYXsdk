package com.hg6kwan.sdk.inner.ui.login;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.loading.LoadingBase;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.StringHelper;

/**
 * Created by xiaoer on 2016/10/30.
 */

public class ResetPassDialog extends LoginBase implements View.OnClickListener {
    private EditText password_et1;
    private EditText password_et2;
    private Button btn_reset;

    private Dialog mLoadingDialog;
    private String errorMsg;

    private BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();

    private final int MSG_RESET_SUCCESS = 2;
    private final int MSG_RESET_FAIL = 3;


    public ResetPassDialog(Context context) {
        super(context);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_RESET_SUCCESS){
                closeLoading();
                Toast.makeText(mContext,"密码重置成功",Toast.LENGTH_SHORT).show();
                ControlUI.getInstance().doLoadingLogin(baseInfo.resetAcc,baseInfo.resetPass);
            } else if (msg.what == MSG_RESET_FAIL){
                closeLoading();
                Toast.makeText(mContext,errorMsg,Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        close_layout.setVisibility(View.INVISIBLE);
        back_layout.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        this.setCancelable(false);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeLoading();
                baseInfo.resetAcc="";
                baseInfo.resetPass="";
            }
        });
    }

    @Override
    public void onClick(View v) {
            if (v == back_layout){
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.FORGET);
            }else if (v == btn_reset){
                doResetPass();
            }
    }

    private void doResetPass() {
        final String pass1 = password_et1.getText().toString().trim();
        final String pass2 = password_et2.getText().toString().trim();
        String result = checkInput(pass1,pass2);

        if(result!=null){
            Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneNum = ControlCenter.getInstance().getBaseInfo().phoneNum;
        showLoading(phoneNum);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int what = 0;
                String msg = null;
                try {
                    HttpResultData result = ControlCenter.getInstance().getmLoginService().resetPassword(baseInfo.resetAcc,pass1);
                    LogUtil.d("重置密码",result.toString());
                    //返回结果
                    what = result.state.getInteger("code");
                    msg = result.state.getString("msg");
                    //成功为1
                    if (what == 1) {
                        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
                        baseInfo.resetPass=pass1;
                        handler.sendEmptyMessage(MSG_RESET_SUCCESS);
                    } else {
                        errorMsg = msg;
                        handler.sendEmptyMessage(MSG_RESET_FAIL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg="重置密码出错!";
                    handler.sendEmptyMessage(MSG_RESET_FAIL);
                }

            }
        }).start();
    }

    private String checkInput(String password1,String password2){
        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        if( TextUtils.isEmpty(password1) || TextUtils.isEmpty(password2))
            return "账号或密码都不能为空！";
        if(password1.length() < 6 || password2.length() < 6)
            return "密码不能低于6位字符";
        if (!TextUtils.equals(password1,password2))
            return "两次输入密码不一致";
        if (TextUtils.equals(password1,baseInfo.regName))
            return "账号和密码不能一致";
        try {
            if(StringHelper.hasSpace(password1) || StringHelper.hasSpace(password2)){
                return "密码中不能包含空格";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "密码输入错误";
        }
        return null;
    }

    private void closeLoading(){
        if (mLoadingDialog!=null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void showLoading(String title){
        if (mLoadingDialog!=null)return;
        mLoadingDialog = new LoadingBase(mContext,title,
                "重置密码...");
        mLoadingDialog.show();
    }

    @Override
    protected LinearLayout createContent(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //输入框
        LinearLayout input_ly_password1 = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        LinearLayout input_ly_password2 = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        input_ly_password1.setOrientation(LinearLayout.HORIZONTAL);
        input_ly_password2.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout iv_lock1 = createImageLayout("lock",2,context);
        LinearLayout iv_lock2 = createImageLayout("lock",2,context);

        password_et1 = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);
        password_et2 = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);

        password_et1.setTextSize(ajustFontSize(size_input));
        password_et2.setTextSize(ajustFontSize(size_input));

        password_et1.setHint("重置密码");
        password_et2.setHint("重置密码");

        input_ly_password1.addView(iv_lock1,getLayoutParamH(1));
        input_ly_password1.addView(password_et1,getLayoutParamH(input_weight_h -1));

        input_ly_password2.addView(iv_lock2,getLayoutParamH(1));
        input_ly_password2.addView(password_et2,getLayoutParamH(input_weight_h -1));

        //登录按钮
        btn_reset =
                uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        btn_reset.setText("提交新密码");
        btn_reset.setTextSize(ajustFontSize(size_btn));

        layout.setWeightSum(6);
        layout.addView(new View(context),getLayoutParamV(0.2f));
        layout.addView(input_ly_password1,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(input_ly_password2,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.5f));
        layout.addView(btn_reset,getLayoutParamV(1));

        return layout;
    }
}
