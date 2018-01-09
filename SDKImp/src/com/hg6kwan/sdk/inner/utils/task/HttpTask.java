package com.hg6kwan.sdk.inner.utils.task;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.net.HttpUtility;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeoutException;


/**
 * Created by Roman on 2017/7/19.
 */

public class HttpTask {

    public HttpResultData postData(String service, HashMap<String, Object> requestMap) {


        BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
        String appid = baseInfo.gAppId;
        String appkey = baseInfo.gAppKey;

        JSONObject data = new JSONObject();
        HttpResultData result = new HttpResultData();

        try {
        for (String requestKey:requestMap.keySet()) {
            data.put(requestKey, requestMap.get(requestKey));
        }

        //签名数据
        String sign = CommonFunctionUtils.getSignString(service, appid, appkey, data);


            okhttp3.Response response = OkHttpUtils
                    .post()
                    .url(Constants.BASE_URL)
                    .addParams("service", service)
                    .addParams("appid", appid)
                    .addParams("data", data.toString())
                    .addParams("sign", sign)
                    .build()
                    .execute();

            com.alibaba.fastjson.JSONObject body= com.alibaba.fastjson.JSONObject.parseObject(response.body().string());
            if (body.getJSONObject("state") != null) {
                result.state = body.getJSONObject("state");
            }
            if (service != Constants.SERVICE_CHECK_ACCOUNT) {
                result.data = body.getJSONObject("data");
            }
            LogUtil.d(Constants.tag, "getResult: " + result.state);
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
