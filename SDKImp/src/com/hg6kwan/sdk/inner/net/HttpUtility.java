package com.hg6kwan.sdk.inner.net;

import android.text.TextUtils;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.InitInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.MD5;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import static android.R.attr.data;
import static android.content.ContentValues.TAG;


public class HttpUtility {
    public static final int CALLRESULT_OK = 200;
    public static final int CALLRESULT_SERVER_ERROR = 555;
    public static final int CALLRESULT_USER_ERROR = 444;
    public static final int CALLRESULT_TIMEOUT = 408;
    private TreeMap<Integer, Class<? extends Exception>> m_dicExceptionClass;

    public HttpUtility() {
        m_dicExceptionClass = new TreeMap<Integer, Class<? extends Exception>>();
        this.registerException(null);
    }


    /**
     * 设置同一调用服务中的方法后的错误异常，如果只是设置本服务类自己的错误和异常类对早表，则Override该方法。如果有其它很多服务类可以共享该对早表，则由外界直接调用该方法传入
     *
     * @param mapExceptionClass = 外界传入的错误编码和异常类的对应关系表。
     */
    public void registerException(TreeMap<Integer, Class<? extends Exception>> mapExceptionClass) {
        if (mapExceptionClass != null) {
            this.m_dicExceptionClass.clear();
            this.m_dicExceptionClass = mapExceptionClass;
        }
    }

    protected void registerExceptionEntry(int nErrCode, Class<? extends Exception> classException){
        m_dicExceptionClass.put(nErrCode, classException);
    }

    /**
     * @param userName  用户名
     * @param psd       密码
     * @param phoneNum  手机号码
     * @param code      验证码
     * @param service   请求ID
     * @param url       请求URL
     * @param role      角色
     * @param extendstr 拓展字段
     * @return result   返回结果
     * @throws Exception
     */
    public HttpResultData getResult(String userName, String psd, String phoneNum, String code, String service, String url, String role, String extendstr, String server) {

        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        String channel = baseInfo.gChannnel;
        String appkey = baseInfo.gAppKey;
        String appid = baseInfo.gAppId;
        String uuid = baseInfo.UUID;
        String sid = baseInfo.gSessionId;
        String password = MD5.getMD5String(psd);

        if (userName == null || TextUtils.isEmpty(userName)){
                userName = baseInfo.login.getU();
        }

        JSONObject data = new JSONObject();
        try {
            data.put("username", userName);
            data.put("passwd", password);
            data.put("channel", channel);
            data.put("code", code);
            data.put("udid", uuid);
            data.put("mobile", phoneNum);
            data.put("sid", sid);
            data.put("server", server);
            data.put("role", role);
            data.put("extends", extendstr);

            if (Constants.SVERVICE_REG_PHONE.equals(service) || Constants.SERVICE_REG.equals(service)) {
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
                    LogUtil.e(Constants.tag,e.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //签名数据
        String sign = CommonFunctionUtils.getSignString(service, appid, appkey, data);
        LogUtil.i(Constants.tag, data.toString());

        HttpResultData result = new HttpResultData();

        try {
            okhttp3.Response response = OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("service", service)
                    .addParams("appid", appid)
                    .addParams("data", data.toString())
                    .addParams("sign", sign)
                    .build()
                    .execute();

            com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());

            result.state = body.getJSONObject("state");
            result.data = body.getJSONObject("data");
            LogUtil.i(TAG, "getResult: " + result.state);
            LogUtil.i(TAG, "getResult: " + result.data);
            transformsException(response.code(), result.data);

            return result;
        } catch (Exception e) {

        }

        return result;

    }

    public HttpResultData getResult(String userName, String psd, String phoneNum, String code,
                                    String service, String url, String role, String extendstr, String server,HashMap<String,Object> requestMap) {

        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        String channel = baseInfo.gChannnel;
        String appkey = baseInfo.gAppKey;
        String appid = baseInfo.gAppId;
        String uuid = baseInfo.UUID;
        String sid = baseInfo.gSessionId;
        String password = MD5.getMD5String(psd);

        if (userName == null || TextUtils.isEmpty(userName)){
            userName = baseInfo.login.getU();
        }

        JSONObject data = new JSONObject();
        try {
            data.put("username", userName);
            data.put("passwd", password);
            data.put("channel", channel);
            data.put("code", code);
            data.put("udid", uuid);
            data.put("mobile", phoneNum);
            data.put("sid", sid);
            data.put("server", server);
            data.put("role", role);
            data.put("extends", extendstr);

            if (Constants.SVERVICE_REG_PHONE.equals(service) || Constants.SERVICE_REG.equals(service)) {
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
                    LogUtil.e(Constants.tag,e.toString());
                }
            }

            for (String requestKey:requestMap.keySet()) {
                data.put(requestKey, requestMap.get(requestKey));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //签名数据
        String sign = CommonFunctionUtils.getSignString(service, appid, appkey, data);
        LogUtil.i(Constants.tag, "data : " + data.toString());

        HttpResultData result = new HttpResultData();

        try {
            okhttp3.Response response = OkHttpUtils
                    .post()
                    .url(url)
                    .addParams("service", service)
                    .addParams("appid", appid)
                    .addParams("data", data.toString())
                    .addParams("sign", sign)
                    .build()
                    .execute();

            com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());

            result.state = body.getJSONObject("state");
            result.data = body.getJSONObject("data");
            LogUtil.i(TAG, "getResult: " + result.state);
            transformsException(response.code(), result.data);

            return result;
        } catch (Exception e) {

        }

        return result;

    }

    protected void transformsException(int code, com.alibaba.fastjson.JSONObject data) throws Exception {
        if (code != HttpUtility.CALLRESULT_OK) {
            if (code == HttpUtility.CALLRESULT_TIMEOUT) {
                throw new TimeoutException();
            } else {
                throw new Exception("服务器异常:" + code + "|result:" + data.toString());
            }
        }
    }

}
