package com.hg6kwan.sdk.inner.platform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.alibaba.fastjson.JSONArray;
import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.ui.dialog.NoticeDialog;
import com.hg6kwan.sdk.inner.ui.loading.LoadingInitDialog;
import com.hg6kwan.sdk.inner.ui.login.BindingDialog;
import com.hg6kwan.sdk.inner.ui.login.BindingTipDialog;
import com.hg6kwan.sdk.inner.ui.login.ForgetDialog;
import com.hg6kwan.sdk.inner.ui.login.OtherDialog;
import com.hg6kwan.sdk.inner.ui.login.RegPhoneDialog;
import com.hg6kwan.sdk.inner.ui.login.ResetPassDialog;
import com.hg6kwan.sdk.inner.ui.loading.LoadingLoginDialog;
import com.hg6kwan.sdk.inner.ui.loading.LoadingRegDialog;
import com.hg6kwan.sdk.inner.ui.login.LoginDialog;
import com.hg6kwan.sdk.inner.ui.login.RegDialog;
import com.hg6kwan.sdk.inner.ui.login.VerificationDialog;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.web.PayDialog;
import com.hg6kwan.sdk.inner.ui.web.WebDialog;
import com.hg6kwan.sdk.inner.utils.Constants;

import java.util.ArrayList;

import static android.R.attr.type;

/**
 * Created by xiaoer on 2016/09/29.
 * ControlUI
 * sdk中界面的控制
 */
public class ControlUI {
    private static Dialog mDialog = null;

    private ControlUI() {
    }

    public static ControlUI getInstance() {
        if (null == mControlUI)
            mControlUI = new ControlUI();
        return mControlUI;
    }

    private static ControlUI mControlUI = null;

    private static Handler mHandler;

    public static void setmHandler(Handler mHandler) {
        ControlUI.mHandler = mHandler;
    }

    public static Handler getmHandler() {
        return mHandler;
    }

    //////////////////////// ui /////////////////////////////
    private void showDialog() {
        ControlCenter.getInstance().hideMenu();
        mDialog.show();
    }

    public boolean isShowingDialog() {
        return mDialog != null;
    }

    public enum LOGIN_TYPE {
        LOGIN,
        REG,
        REG_PHONE,
        OTHER,
        FORGET,
        BINDING,
        TIP,
        RESET,
        ID_VERIFICATION,
        Notice
    }

    public void startUI(Context context, LOGIN_TYPE tag) {
        clearUI();
        switch (tag) {
            case LOGIN:
                mDialog = new LoginDialog(context);
                break;
            case REG:
                mDialog = new RegDialog(context);
                break;
            case REG_PHONE:
                mDialog = new RegPhoneDialog(context);
                break;
            case OTHER:
                mDialog = new OtherDialog(context);
                break;
            case FORGET:
                mDialog = new ForgetDialog(context);
                break;
            case BINDING:
                mDialog = new BindingDialog(context);
                break;
            case TIP:
                mDialog = new BindingTipDialog(context);
                break;
            case RESET:
                mDialog = new ResetPassDialog(context);
                break;
            case ID_VERIFICATION:
                mDialog = new VerificationDialog(context);
                break;
            case Notice:
                mDialog = new NoticeDialog(context);
                break;
        }
        showDialog();
    }

    public enum WEB_TYPE {
        PAY,
        USER,
        SERVICE,
        GAME,
        GIFT,
        STRATEGY,
        NEWS,
        USER_AGREEMENT,
        ID_VERIFICATION
    }

    public enum ACTIVITY_TYPE {
        PAY
    }

    public void startUI(Context context, WEB_TYPE type) {
        clearUI();
        switch (type) {
            case PAY:
                mDialog = new PayDialog(context);
                break;

            default:
                mDialog = new WebDialog(context, type);
                break;
        }
        showDialog();
    }
    //开启支付的Activity
    public void startUI(Context context, ACTIVITY_TYPE type, String UserName, String cpOrder, String
            price, ArrayList<String> list) {

        String packageName = context.getPackageName();

        Intent intent = new Intent();
        intent.setAction(packageName + ".6kw");
        intent.putExtra("UserName",UserName);
        intent.putExtra("Price", price);
        intent.putExtra("CpOrder", cpOrder);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("payChannel",list);
        intent.putExtra("Bundle", bundle);
        context.startActivity(intent);
    }

    public void startUI(WEB_TYPE type) {
        startUI(ControlCenter.getInstance().getmContext(), type);
    }

    private void clearUI() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
            ControlCenter.getInstance().showMenu();
        }
    }

    //正真的登录
    public void doLoadingLogin(String acc, String psd) {
        clearUI();
        mDialog = new LoadingLoginDialog(ControlCenter.getInstance().getmContext(), acc, psd);
        showDialog();
    }

    //正真的注册
    public void doLoadingReg(String acc, String psd) {
        clearUI();
        mDialog = new LoadingRegDialog(ControlCenter.getInstance().getmContext(), acc, psd);
        showDialog();
    }

    //正真的初始化
    public void doLoadingInit(BaseInfo baseInfo, Context context, final String appId, final
    String appKey) {
        clearUI();
        mDialog = new LoadingInitDialog(baseInfo, context, appId, appKey);
        showDialog();
    }

    public void closeSDKUI() {
        //
        clearUI();
    }

    //退出支付
    public void exitPay(int code) {
        uiState.gIsPay = false;
        switch (code) {
            case Constants.PAY_SUCCESS:
                ControlCenter.getInstance().onPayResult(ControlCenter.getInstance().getBaseInfo()
                        .payParam.getOrderId());
                break;
            case Constants.PAY_CANCEL:
                ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL, "支付取消");
                break;
            default:
                ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL, "支付失败");
        }
        closeSDKUI();
    }


}
