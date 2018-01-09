package com.hg6kwan.sdk.inner.utils.task;

import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.Activity.PayActivity;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/5.
 */

public class AliPayTask {

    private PayActivity mPayActivity;

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public AliPayTask(PayActivity payActivity) {
        mPayActivity = payActivity;
    }

    public void aliPay(final String paychannel) {
        TaskUtils task = new TaskUtils(new TaskCallback() {
            @Override
            public void onPreTask() {
            }

            @Override
            public HttpResultData onBackGroudTask() {
                HttpResultData resultData = new PayService().getOrderInfo(paychannel);
                return resultData;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res == null) {
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_PAY_COIN_FAIL, "支付宝订单返回为空");
                    return;
                }
                int what = 0;
                try {

                    what = res.state.getInteger("code");

                    if (what == 1) {

                        final String OrderInfo = res.data.getString("payData");

                        //设置订单ID用于支付成功的回调
                        ControlCenter.getInstance().getBaseInfo().payParam.setOrderId(res.data
                                .getString("order_id"));

                        Runnable payRunnable = new Runnable() {
                            @Override
                            public void run() {

                                PayTask alipay = new PayTask(mPayActivity);
                                Map<String, String> result = alipay.payV2(OrderInfo, true);
                                LogUtil.i("msp", result.toString());

                                Message msg = new Message();
                                msg.what = SDK_PAY_FLAG;
                                msg.obj = result;
                                mPayActivity.getHandler().sendMessage(msg);
                            }
                        };

                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_PAY_COIN_FAIL, "解析支付宝订单错误");
                }
            }
        });
        task.execute();
    }

}
