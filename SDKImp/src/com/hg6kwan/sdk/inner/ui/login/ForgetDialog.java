package com.hg6kwan.sdk.inner.ui.login;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.loading.LoadingBase;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaoer on 2016/10/26.
 */

public class ForgetDialog extends LoginBase implements View.OnClickListener  {
    private EditText et_phone;
    private EditText et_acc;
    private EditText et_auth;
    private Button btn_getAuth;
    private Button btn_reset;
    private TextView tv_find_pass;

    private Timer timer;
    private String errorMsg;

    private final String uaerName = "";

    private final int MSG_TIMER = 1;
    private final int MSG_AUTH_SUCCESS = 2;
    private final int MSG_AUTH_FAIL = 3;

    private Dialog mLoadingDialog;

    public ForgetDialog(Context context) {
        super(context);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMER){
                int seconds = (int) ((120000 - System.currentTimeMillis() + uiState.timeAuth)/1000);
                setTimerButton(seconds);
                if (seconds <= 0 && timer!=null)
                    timer.cancel();
            } else if (msg.what == MSG_AUTH_SUCCESS){
                closeLoading();
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.RESET);
            } else if (msg.what == MSG_AUTH_FAIL){
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
        tv_find_pass.setOnClickListener(this);
        btn_getAuth.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        refreshAuthMsg(null,true);

        if (!TextUtils.isEmpty(uiState.resetPhone)){
            et_acc.setText(uiState.resetPhone);
            et_phone.setText(uiState.resetPhone);
        }

        this.setCancelable(false);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer!=null){
                    timer.cancel();
                }
                closeLoading();
                uiState.resetPhone="";
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == back_layout){
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.LOGIN);
        }else if (v == tv_find_pass){
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.OTHER);
        }else if (v == btn_getAuth){
            //获取验证码
            if (isInputEmpty()){
                Toast.makeText(mContext,"输入不能为空!",Toast.LENGTH_SHORT).show();
                return;
            }
            String phoneNum = et_phone.getText().toString().trim();
            String errorMsg = checkPhoneInput(phoneNum);
            if (errorMsg!=null){
                Toast.makeText(mContext,errorMsg,Toast.LENGTH_SHORT).show();
            }else{
                refreshAuthMsg(phoneNum,false);
            }
        }else if (v == btn_reset){
            //提交验证
            if (mLoadingDialog!=null)
                return;
            doAuthing();
        }
    }

    //检查输入框是否为空
    private boolean isInputEmpty(){
        String acc = et_acc.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();

        return TextUtils.isEmpty(acc) || TextUtils.isEmpty(phone);
    }

    private void doAuthing(){
        final String acc = et_acc.getText().toString().trim();
        final String phone = et_phone.getText().toString().trim();
        final String auth = et_auth.getText().toString().trim();

        if (TextUtils.isEmpty(acc) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(auth)){
            Toast.makeText(mContext,"输入不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        baseInfo.regName=acc;
        baseInfo.resetAcc=acc;
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int what = 0;
                String msg = null;
                try {
                    HttpResultData result = ControlCenter.getInstance().getmLoginService().checkAuthMsg(acc,phone,auth);
                    //返回结果

                    what = result.state.getInteger("code");
                    msg = result.state.getString("msg");
                    //成功为1
                    if (what == 1) {
                        ControlCenter.getInstance().getBaseInfo().phoneNum = phone;
                        ControlCenter.getInstance().getBaseInfo().resetAcc = acc;
                        handler.sendEmptyMessage(MSG_AUTH_SUCCESS);
                    } else {
                        errorMsg = msg;
                        handler.sendEmptyMessage(MSG_AUTH_FAIL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg="验证出错!";
                    handler.sendEmptyMessage(MSG_AUTH_FAIL);
                }
            }
        }).start();
    }

    private String checkPhoneInput(String phoneNum){
        String telRegex = "[1][23456789]\\d{9}";
        //"[1]"代表第1位为数字1，"[34578]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phoneNum)){
            return "error：手机号为空！";
        }else if (11 != phoneNum.length()|!phoneNum.matches(telRegex)){
            return "error: 手机号码格式错误!";
        }
        return null;
    }

    //更新倒计时
    private void refreshAuthMsg(String phoneNum,boolean isInit){
        long currentTime = System.currentTimeMillis();
        if (isInit){
            if (currentTime - uiState.timeAuth > 120000){
                return;
            }
            //发消息更新
        }else{
            if (currentTime - uiState.timeAuth > 120000){
                uiState.timeAuth = currentTime;
                ControlCenter.getInstance().getmLoginService().sendAuthMsg(phoneNum);
            }else{
                return;
            }
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_TIMER);
            }
        },0,1000);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setTimerButton(int seconds){
        if (seconds > 0){
            btn_getAuth.setBackground(uiUtils.createAuthDrawableN());
            btn_getAuth.setText(seconds+"秒内有效");
            btn_getAuth.setEnabled(false);
        }else{
            btn_getAuth.setBackground(uiUtils.createAuthDrawable());
            btn_getAuth.setText("获取验证码");
            btn_getAuth.setEnabled(true);
        }
    }

    private void closeLoading(){
        if (mLoadingDialog!=null){
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void showLoading(){
        if (mLoadingDialog!=null)return;
        mLoadingDialog = new LoadingBase(mContext,et_phone.getText().toString().trim(),
                "正在验证...");
        mLoadingDialog.show();
    }

    @Override
    protected LinearLayout createContent(final Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //账号
        LinearLayout ly_acc = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        ly_acc.setOrientation(LinearLayout.HORIZONTAL);
        ly_acc.addView(createImageLayout("user",2,context),getLayoutParamH(1));
        et_acc = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);
        et_acc.setTextSize(ajustFontSize(size_input));
        et_acc.setHint("账号(手机注册用户,填手机号)");
        ly_acc.addView(et_acc,getLayoutParamH(input_weight_h-1));

        //手机
        LinearLayout ly_phone = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        ly_phone.setOrientation(LinearLayout.HORIZONTAL);
        ly_phone.addView(createImageLayout("user",2,context),getLayoutParamH(1));
        et_phone = uiUtils.createInput(uiUtils.INPUT.PHONE,context);
        et_phone.setTextSize(ajustFontSize(size_input));
        ly_phone.addView(et_phone,getLayoutParamH(input_weight_h -1));

        //验证
        LinearLayout ly_auth = new LinearLayout(context);
        ly_auth.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout ly_auth_input = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        ly_auth_input.setOrientation(LinearLayout.HORIZONTAL);
        ly_auth_input.addView(createImageLayout("auth",2,context),getLayoutParamH(1));
        et_auth = uiUtils.createInput(uiUtils.INPUT.AUTH,context);
        et_auth.setTextSize(ajustFontSize(size_input));

        //如果用户是因为手机号码已经注册过而跳转来这个重置密码界面的,就会把刚刚获取验证码设置进这里去,BaseInfo里面的验证码默认是""
        //只有当用户是点击手机注册,并且后台判断是已经注册过的时候,BaseInfo的auth才会被赋值
        String auth = ControlCenter.getInstance().getBaseInfo().auth;
        //如果auth不是空的,说明是从注册界面跳转过来的,省去用户去翻看验证码的步骤
        if (!TextUtils.isEmpty(auth)) {
            et_auth.setText(auth);
        }

        ly_auth_input.addView(et_auth,getLayoutParamH(input_weight_h - 5));

        btn_getAuth = uiUtils.createButton(uiUtils.BTN.AUTH,context);
        btn_getAuth.setTextSize(ajustFontSize(size_btn-2.5f));

        ly_auth.addView(ly_auth_input,getLayoutParamH(input_weight_h - 4 ));
        ly_auth.addView(new View(context),getLayoutParamH(0.3f));
        ly_auth.addView(btn_getAuth,getLayoutParamH(input_weight_h - 6.3f));

        //提交验证
        btn_reset = uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        btn_reset.setText("重置密码");
        btn_reset.setTextSize(ajustFontSize(size_btn));

        //其他方式找回密码
        LinearLayout ly_find_pass = new LinearLayout(context);
        ly_find_pass.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv_tip = new TextView(context);
        tv_find_pass = new TextView(context);
        tv_tip.setText("未绑定手机，可通过");
        tv_find_pass.setText("其他方式找回密码");

        tv_find_pass.setTextSize(ajustFontSize(size_find_pass));
        tv_tip.setTextSize(ajustFontSize(size_find_pass));
        tv_tip.setTextColor(0xffa9a9a9);
        tv_find_pass.setTextColor(uiUtils.color_red);
        tv_find_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"hello red",Toast.LENGTH_SHORT).show();
            }
        });

        ly_find_pass.addView(tv_tip);
        ly_find_pass.addView(tv_find_pass);

        layout.addView(ly_acc,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_phone,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_auth,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(btn_reset,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_find_pass,getLayoutParamV(0.8f));

        return layout;
    }
}
