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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.floatmenu.FloatMenuManager;
import com.hg6kwan.sdk.inner.ui.loading.LoadingBase;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDBDao;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDomain;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiaoer on 2016/10/26.
 */

public class BindingDialog extends LoginBase implements View.OnClickListener {
    private EditText et_phone;
    private EditText et_auth;
    private Button btn_getAuth;
    private Button btn_binding;

    private Timer timer;
    private String errorMsg;

    public BindingDialog(Context context) {
        super(context);
    }

    private final int MSG_TIMER = 1;
    private final int MSG_BINDING_SUCCESS = 2;
    private final int MSG_BINDING_FAIL = 3;

    private Dialog mLoadingDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_TIMER){
                int seconds = (int) ((120000 - System.currentTimeMillis() + uiState.timeAuth)/1000);
                setTimerButton(seconds);
                if (seconds <= 0 && timer!=null)
                    timer.cancel();
            } else if (msg.what == MSG_BINDING_SUCCESS){
                closeLoading();
                Toast.makeText(mContext,"手机绑定成功",Toast.LENGTH_SHORT).show();
                ControlUI.getInstance().closeSDKUI();
            } else if (msg.what == MSG_BINDING_FAIL){
                closeLoading();
                Toast.makeText(mContext,errorMsg,Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back_layout.setVisibility(View.INVISIBLE);
        close_layout.setOnClickListener(this);
        btn_getAuth.setOnClickListener(this);
        btn_binding.setOnClickListener(this);

        refreshAuthMsg(null,true);

        this.setCancelable(false);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer!=null){
                    timer.cancel();
                }
                closeLoading();
                ControlCenter.getInstance().onLoginResult(ControlCenter.getInstance().getBaseInfo().loginResult);
                //登陆成功后开启悬浮窗
                FloatMenuManager.getInstance().startFloatView(mContext);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == close_layout){
            ControlUI.getInstance().closeSDKUI();
            //查询是否需要打开公告弹窗
            NoticeDBDao.getInstance(mContext).isOpenNoticeDialog();
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
        }else if (v == btn_binding){
            //绑定手机
            if (mLoadingDialog!=null)
                return;
            doBinding();
        }
    }

    //检查输入框是否为空
    private boolean isInputEmpty(){
        String phone = et_phone.getText().toString().trim();
        return TextUtils.isEmpty(phone);
    }

    private void doBinding(){
        final String phone = et_phone.getText().toString().trim();
        final String auth = et_auth.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(auth)){
            Toast.makeText(mContext,"输入不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int what = 0;
                String msg = null;
                try {
                    HttpResultData result = ControlCenter.getInstance().getmLoginService().bindingPhone(ControlCenter.getInstance().getBaseInfo().login.getU(),phone,auth);
                    //返回结果

                    what = result.state.getInteger("code");
                    msg = result.state.getString("msg");
                    //成功为1
                    if (what == 1) {
                        handler.sendEmptyMessage(MSG_BINDING_SUCCESS);
                    } else {
                        errorMsg = msg;
                        handler.sendEmptyMessage(MSG_BINDING_FAIL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMsg="验证出错!";
                    handler.sendEmptyMessage(MSG_BINDING_FAIL);
                }

            }
        }).start();
    }

    private String checkPhoneInput(String phoneNum){
        if (TextUtils.isEmpty(phoneNum)){
            return "error：手机号为空！";
        }else if (11 != phoneNum.length()){
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
                "绑定手机...");
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
        btn_getAuth.setTextSize(ajustFontSize(size_btn-5f));

        ly_auth.addView(ly_auth_input,getLayoutParamH(input_weight_h - 4 ));
        ly_auth.addView(new View(context),getLayoutParamH(0.3f));
        ly_auth.addView(btn_getAuth,getLayoutParamH(input_weight_h - 6.3f));

        //立即绑定
        btn_binding = uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        btn_binding.setText("立即绑定");
        btn_binding.setTextSize(ajustFontSize(size_btn_login));

        //温馨提示
        LinearLayout ly_tip_title = new LinearLayout(context);
        ly_tip_title.setOrientation(LinearLayout.HORIZONTAL);
        ly_tip_title.setWeightSum(10);

        ImageView iv_tip_title = new ImageView(context);
        iv_tip_title.setImageBitmap(uiUtils.getResBitmap("tip_red"));
        ly_tip_title.addView(iv_tip_title,getLayoutParamH(2.5f));

        TextView tv_tipMsg = new TextView(context);
        tv_tipMsg.setText("1.手机仅作为身份验证，不收取任何费用，请放心使用。\n" +
                "2.本公司承诺保障你的隐私，不会泄露你的手机号码。");
        tv_tipMsg.setTextSize(ajustFontSize(size_tips));
        tv_tipMsg.setTextColor(0xffa9a9a9);
        tv_tipMsg.setPadding(15,0,0,15);

        layout.addView(ly_phone,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(ly_auth,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));
        layout.addView(btn_binding,getLayoutParamV(1));
        layout.addView(new View(context),getLayoutParamV(0.3f));

        layout.addView(ly_tip_title,getLayoutParamV(0.45f));
        layout.addView(new View(context),getLayoutParamV(0.15f));
        layout.addView(tv_tipMsg,getLayoutParamV(1.2f));

        return layout;
    }

}
