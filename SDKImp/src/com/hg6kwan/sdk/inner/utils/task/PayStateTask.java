package com.hg6kwan.sdk.inner.utils.task;

import android.app.ProgressDialog;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.loading.LoadingPayDialog;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Administrator on 2017/1/5.\
 * 支付任务栈
 */

public class PayStateTask {

    public PayStateTask() {}

    /**
     * 获取支付渠道的任务栈
     */
    public void getPayState(final String uid, final String userName, final String price, final
    String serverName, final String ServerID, final String roleName, final String roleID, final String roleLevel, final
    String goodsNAME, final int goodsID, final String cpOrder, final String extendstr) {

        TaskUtils taskUtils = new TaskUtils(new TaskCallback() {
            @Override
            public void onPreTask() {
            }

            @Override
            public HttpResultData onBackGroudTask() {
                HttpResultData result;
                try{
                    //获取订单号
                    result = new PayService().getPayState(uid,userName,price,serverName, ServerID,
                            roleName,
                            roleID, roleLevel, goodsNAME,goodsID,cpOrder,extendstr);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
                return result;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res==null){
                    ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,"获取支付渠道返回为空");
                }

                int what = 0;
                try{
                    what = res.state.getInteger("code");
                    //1才继续,0则终止不允许充值
                    if(what == 1){
                        //继续判断是APP支付 or H5支付   1是app支付  0是H5支付
                        if (res.data.getInteger("payState") == 1) {
                            JSONObject payChannel = res.data.getJSONObject("payChannel");

                            Set<String> set = payChannel.keySet();
                            ArrayList<String> list = new ArrayList<>();
                            list.addAll(set);

                            ControlUI.getInstance().startUI(ControlCenter.getInstance().getmContext(),
                                    ControlUI
                                    .ACTIVITY_TYPE.PAY,
                                    ControlCenter.getInstance().getBaseInfo().login.getU(), goodsNAME,
                                    price,
                                    list);
                            //调用H5支付
                        } else {
                            ControlCenter.getInstance().H5Pay(ControlCenter.getInstance()
                                    .getBaseInfo().login.getU()
                                    ,price,
                                    ServerID, roleName,
                                    goodsNAME,
                                    goodsID,cpOrder, extendstr);
                        }
                    }else {
                        String errorMsg = res.state.getString("msg");
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,
                                "获取订单信息错误，msg:"+errorMsg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,"解析订单错误");
                }
            }
        });
        taskUtils.execute();
    }

}
