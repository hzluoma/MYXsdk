package com.hg6kwan.sdk.inner.ui.loading;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.service.LoginService;
import com.hg6kwan.sdk.inner.utils.Constants;

import java.io.File;

/**
 * Created by xiaoer on 2016/7/7.
 */
public class LoadingRegDialog extends LoadingBase {
    private String acc;
    private String psd;
    private Context mContext;

    private String token;
    private String errorMsg;

    public LoadingRegDialog(Context context, String acc, String psd) {
        super(context, acc, "正在注册...");
        mContext = context;
        this.acc = acc;
        this.psd = psd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ControlUI.setmHandler(mHandler);
        startRegThread(mContext, acc, psd);
    }

    private Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            ControlUI.setmHandler(null);
            if (msg.what == 1) {
                ControlUI.getInstance().doLoadingLogin(acc, psd);
            } else {
                //当登录不成功的时候删除掉截屏文件
                deleteScreenShot();

                BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
                baseInfo.regName=acc;
                baseInfo.regPassword=psd;
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.REG);
                ControlCenter.getInstance().onResult(ReturnCode.COM_LOGIN_ACCOUNT_FAIL, errorMsg);
            }
        }
    };

    //    //登陆线程
    public void startRegThread(Context context, String mAccount, String mPassword) {
        RegThread thread = new RegThread(context, mAccount, mPassword);
        thread.start();
    }

    public class RegThread extends Thread {
        private String mAccount;
        private String mPassword;
        private Context mContext;

        public RegThread(Context context, String mAccount, String mPassword) {
            super();
            this.mAccount = mAccount;
            this.mPassword = mPassword;
            this.mContext = context;
        }

        //
        @Override
        public void run() {
            int what = 0;
            //
            try {
                final HttpResultData result = new LoginService().register(acc, psd);
                //返回结果
                LogUtil.d("reg result",result.toString());
                what = result.state.getInteger("code");
                errorMsg = result.state.getString("msg");

                if (what == 1) {
                    //token = (String) HttpUtility.parseHttpResult(result, "data");
                    //Map map1 = JSONHelper.toMap(token);
                    BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
                    baseInfo.gSessionId = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            showFixTime();

            //将returnCode发送到Handler,在对应的界面中调用exit（正常退出）或cancel(取消)
            if (ControlUI.getmHandler() != null) {
                ControlUI.getmHandler().sendEmptyMessage(what);
            }
        }
    }

    private void deleteScreenShot() {
        File file = new File(Constants.SCREEN_SHOT_FILE);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
