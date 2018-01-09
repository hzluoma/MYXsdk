package com.hg6kwan.sdk.inner.ui.loading;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.InitInfo;
import com.hg6kwan.sdk.inner.base.LoginInfo;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.CrashHandler;
import com.hg6kwan.sdk.inner.utils.SPUtil;

import java.util.ArrayList;


/**
 * Created by xiaoer on 2016/10/29.
 */

public class LoadingInitDialog extends LoadingBase {
    public LoadingInitDialog(BaseInfo baseInfo, Context context, final String appId, final String appKey) {
        super(context, "   漫布游戏 ", "正在初始化...");
        this.baseInfo = baseInfo;
        mContext = context;
        this.appId = appId;
        this.appKey = appKey;
    }
    private BaseInfo baseInfo;

    private String appId;
    private String appKey;
    private Context mContext;

    private String errorMsg;

    private final int INIT_SUCCESS = 1;
    private final int INIT_FAIL    = 2;
    private final int PER_CAMERA = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startInitThread(mContext, appId, appKey);
    }

    private void exitInit(int what){
        ControlUI.getInstance().closeSDKUI();
        if (what == INIT_SUCCESS){
            ControlCenter.getInstance().setInitOK();
            ControlCenter.getInstance().onInit();
        }else {
            ControlCenter.getInstance().onResult(ReturnCode.COM_INIT_SDK_FAIL,errorMsg);
        }
    }

    /**
     * 获取SDK的channel
     * (应该是对内部公会的识别)
     */
    private String getChannel(Context context){
        String chStr = CommonFunctionUtils.getLogicChannel(context, "u8channel_");

        return chStr;
    }


    //初始化线程
    public void startInitThread(final Context context, final String appId, final String appKey) {
        //
        baseInfo.gChannnel = getChannel(context);
        if (baseInfo.gChannnel.equalsIgnoreCase("-10000")){
            errorMsg = "获取渠道channel失败";
            exitInit(INIT_FAIL);
            return;
        }

        if ((context == null) || TextUtils.isEmpty(appId) || TextUtils.isEmpty(appKey)){
            errorMsg = "参数不能为空";
            exitInit(INIT_FAIL);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //基本信息
                baseInfo.gAppId = appId;
                baseInfo.gAppKey = appKey;
                baseInfo.UUID = CommonFunctionUtils.creatUUID(mContext);

                uiState.adapterScreen(mContext);

                baseInfo.loginList = CommonFunctionUtils.getLoginListFromSharePreferences(mContext);

                if(baseInfo.loginList!=null && baseInfo.loginList.size() > 0){
                    baseInfo.login = baseInfo.loginList.get(baseInfo.loginList.size()-1);
                }
                baseInfo.gSessionId = null;

                //设置异常捕获
//                CrashHandler crashHandler = CrashHandler.getInstance();
//                crashHandler.init(mContext);

                //获取初始化和注册的时候要传到服务端的信息,包括手机型号,安卓版本等等
                int sdkInt = Build.VERSION.SDK_INT;
                String release = Build.VERSION.RELEASE;
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                InitInfo instance = InitInfo.getInstance();
                instance.setAndroidVersion(release);
                instance.setManufacturer(manufacturer);
                instance.setModel(model);

                showFixTime();

                exitInit(INIT_SUCCESS);
            }
        }).start();
    }

}
