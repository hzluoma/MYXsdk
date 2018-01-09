package com.hg6kwan.sdk.inner.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoer on 2016/09/29.
 * 常量定义
 */
public class Constants {

    //用于LogUtil使用的tag
    public static final String tag = "hg6kwLog";

    //SDK
    public static final String SDK_VERSION = "1.2.0.100";
    public static final String SDK_NAME = "QiQu_SDK";
    public static final String SDK_PRES_FILE = "data_sdk_preferences.xml";
    public static final String SDK_DATA = "data";
    public static final String SDK_ROOT = "com.u8.sdk";
    public static final String SDK_CHANNEL = "QIQU_CHANNEL_ID";

    //默认
    public static final String DEFAULT_SERVER_ID = "1";

    //登录账号列表的最大值
    public static final int LOGIN_ACCOUNT_MAX = 6;

    //PAY
    public static final String HTTP_GET_ORDER_URL = "http://qd.6kw.com";
    public static final String HTTP_GET_ORDER_SERVICE = "sdk.pay.fororder";

    //
    public static final String QIQU_ACCOUNT = "qiqu_account";
    public static final String QIQU_PASSWORD = "qiqu_password";
    public static final String QIQU_IS_AUTO_LOGIN = "qiqu_is_auto_login";
    public static final String QIQU_IS_UPLOAD_SERVER = "qiqu_is_upload";

    //
    public static final String QIQU_LOGIN_INFO_LIST = "qiqu_login_info_list";


    //aes
    public static final String AES_ENCODE_KEY = "abcdeeffa@#$_qiqu_xxf";

    //loadingDialog显示最小时间(毫秒)
    public static final long LOADING_DIALOG_SHOW_TIME = 800;

    //key
    public static final String FLOAT_KEY = "2fb959ad17fc7eec6a710680583baef6";

    //payCode
    public static final int PAY_SUCCESS = 0;
    public static final int PAY_CANCEL = -152;
    public static final int PAY_SUBMIT = -151;
    public static final int PAY_FAIL = -150;

    public static boolean DEBUG = true;

    public static final String BASE_URL = "http://mp.gzjykj.com";
    public static final String SERVICE_REG = "sdk.user.reg";
    public static final String SVERVICE_LOGIN = "sdk.user.login";
    public static final String SVERVICE_GET_AUTH = "sdk.user.code";
    public static final String SVERVICE_CHECK_AUTH = "sdk.user.checkUserMobileCode";
    public static final String SVERVICE_RESET_PASS = "sdk.user.updatePwd";
    public static final String SVERVICE_BIND_PHONE = "sdk.user.bindMobile";
    public static final String SVERVICE_GET_PAYSTATE = "sdk.pay.getPayState";
    public static final String SVERVICE_REG_PHONE = "sdk.user.mobileReg";
    public static final String SVERVICE_ENTER_GAME = "sdk.game.enterGame";
    public static final String HTTP_INIT_SDK_SERVICE = "sdk.game.initsdk";

    //获取APP支付的相关信息
    public static final String SERVICE_GET_APPORDER = "sdk.pay.getAppOrder";

    //查询微信支付的回调结果的接口
    public static final String SERVICE_CHECK_WXRESULT = "sdk.pay.query";

    //支付渠道ID
    public static final String PAYCHANNEL_ALI = "3";
    public static final String PAYCHANNEL_WECHAT = "10";
    public static final String PAYCHANNEL_TICKET = "4";
    public static final String PAYCHANNEL_PLATFORM = "6";

    //上传程序崩溃信息的URL
    public static String CRASH_UPDATE_URL = "http://h5.6kw.com/api/saveAppErrors.php";

    //截屏文件的文件名临时存储  如果登录失败就删除此文件
    public static String SCREEN_SHOT_FILE = "";

    public static int RECEIVER_RANDOM_NUM = -1;

    //每60秒轮询检测账号密码的接口
    public static final String SERVICE_CHECK_ACCOUNT = "sdk.info.checkPwd";
    /**
     * 是否开启轮询的控制器,目前主要用于在FloatMenu里面开启轮询,因为bindService的缘故,在floatMenu的show方法会被两次调用,为了不开启两次轮询
     * 设置了这个控制器,在登录后会被设为true,执行完里面的代码后就会被设为false,直到下次登录再次设为true.
     */
    public static boolean IS_CHECKACC = true;
}
