package com.hg6kwan.sdk.inner.service;

import android.util.Log;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.EnterGameBean;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.net.HttpUtility;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/12/13.
 */

/**
 * 发送用户登录游戏后的信息给服务器
 */
public class EnterGameService extends HttpUtility {

    public HttpResultData enterGame(EnterGameBean enterGameBean) {
        try {

            HttpResultData result = new HttpResultData();

            BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();

            String appkey = baseInfo.gAppKey;
            String appid = baseInfo.gAppId;
            String uid = enterGameBean.getUid();
            String userName = enterGameBean.getUserName();
            String roleName = enterGameBean.getRoleName();
            String roleID = enterGameBean.getRoleID();
            String roleLV = enterGameBean.getRoleLV();
            String serverID = enterGameBean.getServerID();
            String serverName = enterGameBean.getServerName();
            String rechargeLV = enterGameBean.getRechargeLV();
            String channel = baseInfo.gChannnel;
            String imeiCode = ControlCenter.getInstance().getBaseInfo().UUID;
            String extendstr = enterGameBean.getExtendstr();

            String service = Constants.SVERVICE_ENTER_GAME;

            JSONObject data = new JSONObject();
            try {
                data.put("uid", uid);
                data.put("roleName", roleName);
                data.put("userName", userName);
                data.put("roleID", roleID);
                data.put("roleLevel", roleLV);
                data.put("serverID", serverID);
                data.put("serverName", serverName);
                data.put("payLevel", rechargeLV);
                data.put("channel", channel);
                data.put("imeiCode", imeiCode);
                data.put("extends", extendstr);

                LogUtil.i("6kwEntergame", "userName: " + userName);
                LogUtil.i("6kwEntergame", "uid: " + uid);

                LogUtil.i("enterGame", "enterGame: " + data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            //签名数据
            String sign = CommonFunctionUtils.getSignString(service, appid, appkey, data);
            LogUtil.i("enterGame", "enterGame: " + sign);
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

                com.alibaba.fastjson.JSONObject body = com.alibaba.fastjson.JSONObject
                        .parseObject(response.body().string());
                LogUtil.i("enterGame", "enterGame: " + body.toString());

                result.state = body.getJSONObject("state");

                transformsException(response.code(), result.data);

                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
