package com.hg6kwan.sdk.inner.net;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2016-11-19.
 */

public class HttpResultData {
    public JSONObject state;
    public JSONObject data;

    @Override
    public String toString() {
        if (state != null && data != null) {
            return "{\"state\":" + state.toString() + "," + "\"data\":" + data.toString() + "}";
        }
        return "{\"state\":" + "null" + "," + "\"data\":" + "null" + "}";
    }
}
