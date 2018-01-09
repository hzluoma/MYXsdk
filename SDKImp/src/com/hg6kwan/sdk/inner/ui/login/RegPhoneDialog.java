package com.hg6kwan.sdk.inner.ui.login;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class RegPhoneDialog extends LoginBase implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_auth;
    private Button btn_getAuth;
    private EditText et_pass;
    private Button btn_reg;

    private Timer timer;
    private String errorMsg;

    private final int MSG_TIMER = 1;
    private final int MSG_REG_SUCCESS = 2;
    private final int MSG_REG_FAIL = 3;

    private Dialog mLoadingDialog;

    public RegPhoneDialog(Context context) {
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
            } else if (msg.what == MSG_REG_SUCCESS){
                closeLoading();
                Toast.makeText(mContext,"手机注册成功",Toast.LENGTH_SHORT).show();
                final String phone = et_phone.getText().toString().trim();
                final String pass = et_pass.getText().toString().trim();
                ControlUI.getInstance().doLoadingLogin(phone,pass);
            } else if (msg.what == MSG_REG_FAIL){
                //如果手机已经注册过了跳转到修改密码界面
                if ("手机号已被注册".equals(errorMsg)){
                    final Context context = ControlCenter.getInstance().getmContext();
                    final AlertDialog isShowDialog = new AlertDialog.Builder(context).create();

                    uiState.resetPhone = et_phone.getText().toString().trim();
                    isShowDialog.setTitle("错误");
                    isShowDialog.setMessage("该手机号已经注册过,是否修改该手机账号密码？");
                    isShowDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "修改密码", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ControlUI.getInstance().startUI(context, ControlUI.LOGIN_TYPE.FORGET);
                        }
                    });
                    isShowDialog.setButton(AlertDialog.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                            isShowDialog.dismiss();
                            closeLoading();
                        }
                    });
                    isShowDialog.setCancelable(false);
                    isShowDialog.show();
                }else{
                    closeLoading();
                    Toast.makeText(mContext,errorMsg,Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        close_layout.setVisibility(View.INVISIBLE);
        back_layout.setOnClickListener(this);
        btn_getAuth.setOnClickListener(this);
        btn_reg.setOnClickListener(this);

        refreshAuthMsg(null,true);

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
        }else if (v == btn_reg){
            if (mLoadingDialog!=null)
                return;
            doRegPhone();
        }
    }

    //检查输入框是否为空
    private boolean isInputEmpty(){
        String phone = et_phone.getText().toString().trim();
        return TextUtils.isEmpty(phone);
    }

    private void doRegPhone(){
        final String phone = et_phone.getText().toString().trim();
        final String auth = et_auth.getText().toString().trim();
        final String pass = et_pass.getText().toString().trim();

        //将获取到的验证码保存起来,用于万一用户的手机号码注册过的时候,跳转到修改密码的界面时,不用用户重复输入验证码
        ControlCenter.getInstance().getBaseInfo().auth = auth;

        if (TextUtils.isEmpty(pass)|| TextUtils.isEmpty(phone) || TextUtils.isEmpty(auth)){
            Toast.makeText(mContext,"输入不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }else if(phone.equals(pass)){
            Toast.makeText(mContext,"密码不能和账号一样！",Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int what = 0;
                String msg = null;
                try {
                    HttpResultData result = ControlCenter.getInstance().getmLoginService().regPhone(phone,pass,phone,auth);
                    //返回结果
                    what = result.state.getInteger("code");
                    msg = result.state.getString("msg");
                    //成功为1
                    if (what == 1) {
                        handler.sendEmptyMessage(MSG_REG_SUCCESS);
                    } else {
                        errorMsg = msg;
                        handler.sendEmptyMessage(MSG_REG_FAIL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg="注册失败!";
                    handler.sendEmptyMessage(MSG_REG_FAIL);
                }
            }
        }).start();
    }

    private String checkPhoneInput(String phoneNum){
        String telRegex = "[1][23456789]\\d{9}";
        //"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、4、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phoneNum)){
            return "error：手机号为空！";
        }else if (11 != phoneNum.length()|!phoneNum.matches(telRegex)){
            return "error: 手机号码格式错误!";
        }
//        else if (!phoneNum.matches(telRegex)){
//            return "error: 手机号码格式错误!";
//        }
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
                "手机注册...");
        mLoadingDialog.show();
    }

    @Override
    protected LinearLayout createContent(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //手机
        LinearLayout ly_phone = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        ly_phone.setOrientation(LinearLayout.HORIZONTAL);
        ly_phone.addView(createImageLayout("phone_dark",2,context),getLayoutParamH(1));
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
        ly_auth_input.addView(et_auth,getLayoutParamH(input_weight_h - 5));

        btn_getAuth = uiUtils.createButton(uiUtils.BTN.AUTH,context);
        btn_getAuth.setTextSize(TypedValue.COMPLEX_UNIT_DIP,ajustFontSize(size_btn-2.5f));
        ly_auth.addView(ly_auth_input,getLayoutParamH(input_weight_h - 4 ));
        ly_auth.addView(new View(context),getLayoutParamH(0.3f));
        ly_auth.addView(btn_getAuth,getLayoutParamH(input_weight_h - 6.3f));

        //密码
        LinearLayout ly_pass = uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        ly_pass.setOrientation(LinearLayout.HORIZONTAL);
        ly_pass.addView(createImageLayout("lock",2,context),getLayoutParamH(1));
        et_pass = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);
        et_pass.setTextSize(ajustFontSize(size_input));
        et_pass.setHint("密码");
        ly_pass.addView(et_pass,getLayoutParamH(input_weight_h-1));

        //注册
        btn_reg = uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        btn_reg.setText("手机注册");
        btn_reg.setTextSize(ajustFontSize(size_btn));

        layout.addView(ly_phone,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_auth,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_pass,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(btn_reg,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(1));
        return layout;
    }
}
