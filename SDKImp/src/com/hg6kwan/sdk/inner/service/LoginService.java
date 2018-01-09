package com.hg6kwan.sdk.inner.service;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.InitInfo;
import com.hg6kwan.sdk.inner.base.LoginInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.net.HttpUtility;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginService extends HttpUtility {

    public void notifyInitSDK() {

        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        String appkey = baseInfo.gAppKey;
        String service = Constants.HTTP_INIT_SDK_SERVICE;
        String appid = baseInfo.gAppId;
        String channel = baseInfo.gChannnel;
        String uuid = baseInfo.UUID;


        final JSONObject data = new JSONObject();
        try {

            InitInfo instance = InitInfo.getInstance();
            String channel_id = instance.getChannel_ID();
            String logicChannel = instance.getLogicChannel();
            String androidVersion = instance.getAndroidVersion();
            String manufacturer = instance.getManufacturer();
            String phoneModel = instance.getModel();

            String u8OldChannel = instance.getU8OldChannel();

            if (channel_id == null) {
                channel_id = "";
            }

            data.put("decryptChannel", channel_id);
            data.put("unDecryptChannel", logicChannel);
            data.put("androidVersion", androidVersion);
            data.put("manufacturer", manufacturer);
            data.put("phoneModel", phoneModel);
            data.put("oldChannel", u8OldChannel);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            data.put("channel", channel);
            data.put("udid", uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

            //签名数据
            String sign = CommonFunctionUtils.getSignString(service, appid, appkey, data);
            LogUtil.i("SIGN", "getResult: " + sign);

            HttpResultData result = new HttpResultData();

            try {
                okhttp3.Response response = OkHttpUtils
                        .post()
                        .url(Constants.BASE_URL)
                        .addParams("service", service)
                        .addParams("appid", appid)
                        .addParams("data", data.toString())
                        .addParams("sign", sign)
                        .build()
                        .execute();



                com.alibaba.fastjson.JSONObject body = com.alibaba.fastjson.JSONObject.parseObject(response.body().string());
                result.state = body.getJSONObject("state");
                result.data = body.getJSONObject("data");
                Log.i("notifyInitSDK", "notifyInitSDK: " + body.toString());

                int what = 0;
                    what = result.state.getInteger("code");
                ArrayList<LoginInfo> loginlist = CommonFunctionUtils
                        .getLoginListFromSharePreferences(ControlCenter.getInstance()
                        .getmContext());
                //如果loginList的size是0的话,就说明没有登录过,可以去后台查询是否有在这台设备登录过,然后获取账号显示
                //如果loginList的size不为0,说明登录过,跳出方法,这是保证此方法只会在第一次安装好游戏后才使用,后面不再使用
                if (loginlist.size() != 0) {
                    return;
                }
                if (what == 1) {
                        String username = result.data.getString("username");
                        if (!TextUtils.isEmpty(username)) {
                            LoginInfo loginInfo = new LoginInfo();
                            loginInfo.setU(username);
                            loginInfo.setP("");
                            ArrayList<LoginInfo> list = new ArrayList<>();
                            list.add(loginInfo);
                            CommonFunctionUtils.setLoginListToSharePreferences(ControlCenter.getInstance()
                                    .getmContext(),list);
                            //因为登录的时候会根据baseInfo.loginList有没有被赋值来判断是否开启登录框或注册框,这里赋值一下
                            baseInfo.login = loginInfo;
                        }
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    /**
     * 登录
     *
     * @param userName 用户名
     * @param password 密码 加密
     * @return 成功则返回用户登录的SessionId，帐号密码错误则返回-1
     * @throws Exception
     */
    public HttpResultData login(String userName, String password) {
        try {
            HttpResultData result = getResult(userName, password, "", "", Constants.SVERVICE_LOGIN, Constants.BASE_URL, "", "","");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 注册
     *
     * @param userName
     * @param password
     * @return 成功则返回用户在平台的id，帐号已存在返回-1，格式不正确返回-2，其它错误返回-3.
     * @throws Exception
     */
    public HttpResultData register(String userName, String password) {
        try {
            HttpResultData result = getResult(userName, password, "", "", Constants.SERVICE_REG, Constants.BASE_URL, "", "","");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 手机注册
     *
     * @param userName 手机号码
     * @param password 密码
     * @param auth     验证码
     * @throws Exception
     */


    public HttpResultData regPhone(String userName, String password, String phone,String auth) {
        try {
            HashMap<String, Object> requestMap = new HashMap<>();
            requestMap.put("pwd", password);
            HttpResultData result = getResult(userName, password, phone, auth, Constants
                    .SVERVICE_REG_PHONE, Constants.BASE_URL, "", "","",requestMap);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送手机短信验证码
     *
     * @param phoneNum 手机号码
     * @throws Exception
     */

    public void sendAuthMsg(final String phoneNum) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getResult(phoneNum, "", phoneNum, "", Constants.SVERVICE_GET_AUTH, Constants.BASE_URL, "", "","");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 检测短信验证码
     *
     * @param userName 账号
     * @param phoneNum 手机号码
     * @param code     短信验证码
     * @throws Exception
     */
    public HttpResultData checkAuthMsg(String userName, String phoneNum, String code) {
        try {
            HttpResultData result = getResult(userName, "", phoneNum, code, Constants.SVERVICE_CHECK_AUTH, Constants.BASE_URL, "", "","");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 绑定手机
     *
     * @param phoneNum 手机号码
     * @param code     短信验证码
     * @throws Exception
     */
    public HttpResultData bindingPhone(String userName,String phoneNum, String code) {
        try {
            HttpResultData result = getResult(userName, "", phoneNum, code, Constants.SVERVICE_BIND_PHONE, Constants.BASE_URL, "", "","");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 重置密码
     *
     * @param psd 密码(md5后的密码)
     * @throws Exception
     */
    public HttpResultData resetPassword(String username,String psd) {
        try {
            HttpResultData result = getResult(username, psd, "", "", Constants.SVERVICE_RESET_PASS, Constants.BASE_URL, "", "","");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}