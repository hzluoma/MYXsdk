package com.hg6kwan.sdk.inner.platform;

import com.hg6kwan.sdk.inner.base.LoginResult;

/**
 * Created by xiaoer on 2016/9/22.
 */
public interface IListener {
    public void onResult(int code, String msg);

    public void onInit();

    public void onLoginResult(LoginResult result);

    public void onLogout();

    public void onPayResult(String orderId);

    //上传角色信息的接口
    public void onEnterGameResult();

    //实名认证的回调接口
    public void onIDVerification();
}
