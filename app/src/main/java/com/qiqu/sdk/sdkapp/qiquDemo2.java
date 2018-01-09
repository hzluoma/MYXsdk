package com.qiqu.sdk.sdkapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

//import com.hg6kwan.sdk.inner.platform.ControlUI;
//import com.hg6kwan.sdk.inner.ui.uiState;

/**
 * Created by Administrator on 2016/10/26.
 */

public class qiquDemo2 extends Activity implements View.OnClickListener{
    private Dialog mDialog;

    private Button btn_login;
    private Button btn_reg;
    private Button btn_other;
    private Button btn_forget;
    private Button btn_phone;
    private Button btn_binding;
    private Button btn_tip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qiqu_demo2);

        initSize(this);

        btn_login = (Button) findViewById(R.id.btn_showLogin);
        btn_login.setOnClickListener(this);
        btn_reg = (Button) findViewById(R.id.btn_showReg);
        btn_reg.setOnClickListener(this);
        btn_other = (Button) findViewById(R.id.btn_showOther);
        btn_other.setOnClickListener(this);
        btn_forget = (Button) findViewById(R.id.btn_showForget);
        btn_forget.setOnClickListener(this);
        btn_phone = (Button) findViewById(R.id.btn_showPhoneReg);
        btn_phone.setOnClickListener(this);
        btn_binding = (Button) findViewById(R.id.btn_showBinding);
        btn_binding.setOnClickListener(this);
        btn_tip = (Button) findViewById(R.id.btn_showTip);
        btn_tip.setOnClickListener(this);

    }

    private void initSize(Context context) {
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        uiState.screenWidth = wm.getDefaultDisplay().getWidth();
//        uiState.screenHeight = wm.getDefaultDisplay().getHeight();
//        uiState.screenOrientation = uiState.screenWidth > uiState.screenHeight ?
//                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public void onClick(View v) {
//        if (v == btn_login){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.LOGIN);
//        }else if (v == btn_reg){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.REG);
//        }else if (v == btn_other){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.OTHER);
//        }else if (v == btn_phone){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.REG_PHONE);
//        }else if (v == btn_forget){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.FORGET);
//        }else if (v == btn_binding){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.BINDING);
//        }else if (v == btn_tip){
//            ControlUI.getInstance().startUI(this, ControlUI.LOGIN_TYPE.TIP);
//        }
    }
}
