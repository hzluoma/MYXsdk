package com.hg6kwan.sdk.inner.base;

import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import java.util.ArrayList;


/**
 *
 * 基础信息(包括全部信息)
 *
 */
public class BaseInfo {
    //固定
	public final String gVersion = Constants.SDK_VERSION;

	public String gAppId = "";
	public String gAppKey = "";

    //机型，厂商
    public String model = CommonFunctionUtils.getMode();
    public String manufacturer = CommonFunctionUtils.getManufacturer();

    public String gIMSI = null;
    public String gChannnel = "";
    public String gSessionId = null;

    public String gUid = "";//服务端分配的 uid
    public String UUID = "";
    public String nickName = "";

    //登录信息
    public String phoneNum = "";
    public String resetAcc = "";
    public String resetPass = "";
    public boolean gIsAutoLogin = false;

    public LoginInfo login = null;
    public ArrayList<LoginInfo> loginList = new ArrayList<>();

    public PayParam payParam;
    public LoginResult loginResult;

    //注册名字
    public String regName = "";
    public String regPassword = "";

    public boolean isBinding;

    //验证码
    public String auth = "";
}
