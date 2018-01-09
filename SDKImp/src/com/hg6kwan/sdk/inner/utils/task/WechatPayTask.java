package com.hg6kwan.sdk.inner.utils.task;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.Activity.PayActivity;
import com.hg6kwan.sdk.inner.ui.uiState;

import static com.hg6kwan.sdk.inner.base.ReturnCode.COM_PAY_COIN_FAIL;

/**
 * Created by Administrator on 2017/1/20.
 */

public class WechatPayTask{

    private PayActivity mPayActivity;

    public WechatPayTask(PayActivity payActivity) {
        mPayActivity = payActivity;
    }

    public void payOnWechat(final String paychannel) {
        TaskUtils task = new TaskUtils(new TaskCallback() {

            @Override
            public void onPreTask() {

            }

            @Override
            public HttpResultData onBackGroudTask() {
                PayService payService = new PayService();
                HttpResultData res = payService.getWeChatOrder(paychannel);
                return res;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res == null) {
                    ControlCenter.getInstance().onResult(
                            COM_PAY_COIN_FAIL, "微信订单返回为空");
                    return;
                }

                int what = 0;
                try {
                    what = res.state.getInteger("code");
                   if (what == 1) {
                       String url = res.data.getString("payURL");
                       String order_id = res.data.getString("order_id");
                       ControlCenter.getInstance().getBaseInfo().payParam.setOrderId(order_id);

                           //调起微信支付
                           Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                           mPayActivity.startActivity(intent);
                           //设置标记 在PayActivity重新获得焦点的时候,去请求查询支付结果
                           mPayActivity.setWxPayFlag(true);
                       }



                } catch (Throwable e) {
                    e.printStackTrace();

                    if (uiState.screenOrientation == 0) {
                        if (mPayActivity.getMainLand() != null) {
                            mPayActivity.getMainLand().affirmPayDiaLog();
                        }
                    } else {
                        //当充值失败,屏幕是竖向时弹的dialog
                        if (mPayActivity.getPortrait() != null) {
                            mPayActivity.getPortrait().affirmPayDiaLog();
                        }
                    }
                    ControlCenter.getInstance().onResult(
                            COM_PAY_COIN_FAIL, "启动微信支付出错");
                }
            }
        });
        task.execute();
    }

    /**
     * 查询微信支付的结果支付结果
     * @param order_id
     */
    public void checkWechatResult(final String order_id) {
        TaskUtils task = new TaskUtils(new TaskCallback() {

            @Override
            public void onPreTask() {

            }

            @Override
            public HttpResultData onBackGroudTask() {
                PayService payService = new PayService();
                HttpResultData res = payService.checkWXPayResult(order_id);
                return res;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {
                if (res == null) {
                    ControlCenter.getInstance().onResult(
                            COM_PAY_COIN_FAIL, "微信订单返回为空");
                    return;
                }

                int what = 0;
                try {
                    what = res.state.getInteger("code");
                    if (what == 1) {
                            //支付成功,通知外面
                            ControlCenter.getInstance().onPayResult(order_id);
                        } else{
                            //支付失败的,通知外面
                            ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL, "微信支付失败");
                        }
                    //查询完支付结果以后改变这个标记
                    mPayActivity.setWxPayFlag(false);
                } catch (Throwable e) {
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(
                            COM_PAY_COIN_FAIL, "查询微信支付结果出错");
                }
            }
        });
        task.execute();
    }

}
