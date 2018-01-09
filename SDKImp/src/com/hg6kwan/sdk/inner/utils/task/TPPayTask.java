package com.hg6kwan.sdk.inner.utils.task;

import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.Activity.PayActivity;
import com.hg6kwan.sdk.inner.ui.loading.LoadingPayDialog;
import com.hg6kwan.sdk.inner.ui.uiState;

/**
 * Created by Administrator on 2017/1/5.
 */

public class TPPayTask {

    private PayActivity mPayActivity;

    public TPPayTask(PayActivity payActivity) {
        mPayActivity = payActivity;
    }

    public void payOnPlatform(final String paychannel) {
        TaskUtils task = new TaskUtils(new TaskCallback() {
            @Override
            public void onPreTask() {
                //显示进度条
                LoadingPayDialog.init(mPayActivity);
                LoadingPayDialog.getInstance().show();
            }

            @Override
            public HttpResultData onBackGroudTask() {
                HttpResultData res = new PayService().getOrderInfo(paychannel);
                return res;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res == null) {
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_PAY_COIN_FAIL, "平台币支付结果响应为空");
                    return;
                }
                int what = 0;
                try {

                    what = res.state.getInteger("code");
                    if (what == 1) {

                        //设置订单ID用于支付成功的回调
                        ControlCenter.getInstance().getBaseInfo().payParam.setOrderId(res.data
                                .getString("order_id"));

                        //当平台币支付成功的时候,通知外面
                        ControlCenter.getInstance().onPayResult(ControlCenter.getInstance()
                                .getBaseInfo().payParam.getOrderId());
                        //展示成功支付的dialog
                        mPayActivity.showSuccessDialog();

                    } else {
                        //当平台币支付失败的时候,通知外面
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,"平台币支付失败");
                        //支付失败的时候展示平台币余额不足的dialog
                        mPayActivity.showTPpayFailDialog();

                    }
                    //进度条消失
                    LoadingPayDialog.getInstance().dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_PAY_COIN_FAIL, "解析平台币支付信息错误");
                }
            }
        });
        task.execute();
    }

    public void payByTicket(final String paychannel) {
        TaskUtils task = new TaskUtils(new TaskCallback() {
            @Override
            public void onPreTask() {
                //显示进度条
                LoadingPayDialog.init(mPayActivity);
                LoadingPayDialog.getInstance().show();
            }

            @Override
            public HttpResultData onBackGroudTask() {
                HttpResultData res = new PayService().getOrderInfo(paychannel);
                return res;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res == null) {
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_PAY_COIN_FAIL, "代金券支付结果响应为空");
                    return;
                }
                int what = 0;
                try {
                    what = res.state.getInteger("code");
                    if (what == 1) {

                        //设置订单ID用于支付成功的回调
                        ControlCenter.getInstance().getBaseInfo().payParam.setOrderId(res.data
                                .getString("order_id"));

                        //当代金券支付成功的时候,通知外面
                        ControlCenter.getInstance().onPayResult(ControlCenter.getInstance()
                                .getBaseInfo().payParam.getOrderId());

                        //展示dialog
                        mPayActivity.showSuccessDialog();

                    } else {
                        //当代金券支付失败的时候,通知外面
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL, "代金券支付失败");
                        //支付失败的时候展示平台币余额不足的dialog
                        mPayActivity.showTPpayFailDialog();
                    }
                    //进度条消失
                    LoadingPayDialog.getInstance().dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_PAY_COIN_FAIL, "解析代金券支付信息错误");
                }
            }
        });
        task.execute();
    }

}
