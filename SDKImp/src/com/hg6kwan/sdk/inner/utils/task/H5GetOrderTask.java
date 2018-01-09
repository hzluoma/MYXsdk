package com.hg6kwan.sdk.inner.utils.task;

import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.loading.LoadingPayDialog;

/**
 * Created by Administrator on 2017/1/5.
 * 获取H5支付订单的任务栈
 */

public class H5GetOrderTask {

    public void getOrderId(final String uid, String price, final String serverId, final String roleName,
                           final String goodsNAME, final int goodsId, final String cpOrder, final String
                                   extendStr){

        //needPayCoins需要支付的钱财
        final int coinCount = Integer.parseInt(price) < 0 ? 0 : Integer.parseInt(price);

        final String[] orderId = {""};
        final String[] payUrl = {""};

        TaskUtils task = new TaskUtils(new TaskCallback() {

            @Override
            public void onPreTask() {
            }

            @Override
            public HttpResultData onBackGroudTask() {

                try{
                    PayService payService = new PayService();
                    //获取订单号
                    HttpResultData result = payService.getOrderId
                            (uid,serverId, roleName,goodsNAME,goodsId,extendStr);
                    return result;
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res==null){
                    ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,"订单返回为空");
                    return;
                }
                int what = 0;

                try{

                    what = res.state.getInteger("code");
                    if(what == 1){
                        //获取订单号成功
                        orderId[0] = res.data.getString("order_id");
                        payUrl[0] = res.data.getString("pay_url");

                        //通过构造方法,赋值给PayParam里面的成员变量,用于支付成功时的回调
                        ControlCenter.getInstance().getBaseInfo().payParam.setOrderId(orderId[0]);

                        ControlUI.getInstance().startUI(ControlCenter.getInstance().getmContext(), ControlUI
                                .WEB_TYPE.PAY);
                    }else {
                        String errorMsg = res.state.getString("msg");
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,"获取订单失败，msg:"+errorMsg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,"解析订单错误");
                }

            }
        });
        task.execute();
    }


}
