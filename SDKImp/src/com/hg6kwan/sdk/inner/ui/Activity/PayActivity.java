package com.hg6kwan.sdk.inner.ui.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.hg6kwan.sdk.inner.base.PayResult;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.task.WechatPayTask;

import java.util.ArrayList;
import java.util.Map;

public class PayActivity extends Activity{

    private  String mUserName;                //传入进来的角色名字或账号
    private  String mPrice;         //需要支付的金额
    private  String mGoodsName;                //商品描述
    private String mPayChannel;         //包含支付渠道顺序的数组
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private MainLandUI mMainLand;
    private MainPortraitUI mPortrait;

    //判断是否启动了微信支付的标记,如果启动了微信支付,那么就会在onResume的时候去查询支付结果
    private boolean WxPayFlag = false;

    public MainLandUI getMainLand() {
        return mMainLand;
    }

    public MainPortraitUI getPortrait() {
        return mPortrait;
    }

    public boolean getWxPayFlag() {
        return WxPayFlag;
    }

    public void setWxPayFlag(boolean wxPayFlag) {
        WxPayFlag = wxPayFlag;
    }


    public PayActivity getPayActivity() {
        return mPayActivity;
    }

    private PayActivity mPayActivity;

    public PayActivity() {

    }

    //-------------------支付宝的Handler----------------------

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public Handler getHandler() {
        return mHandler;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();


                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //支付宝支付成功时 回调接口通知CP
                        ControlCenter.getInstance().onPayResult(ControlCenter.getInstance()
                                .getBaseInfo().payParam.getOrderId());

                        //6001为用户中途取消
                    } else if (TextUtils.equals(resultStatus, "6001")) {
                        //通过回调接口通知外面
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,
                                "用户取消");
                    } else if (TextUtils.equals(resultStatus, "6002")) {
                        //通过回调接口通知外面
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,
                                "网络连接出错");
                    } else {
                        //通过回调接口通知外面
                        ControlCenter.getInstance().onResult(ReturnCode.COM_PAY_COIN_FAIL,
                                "支付不成功");
                    }

                    //当充值结束后(无论成功或失败),屏幕是横向的时候弹的dialog
                    if (uiState.screenOrientation == 0) {
                        if (mMainLand != null) {
                            mMainLand.affirmPayDiaLog();
                        }
                    } else {
                        //当充值失败,屏幕是竖向时弹的dialog
                        if (mPortrait != null) {
                            mPortrait.affirmPayDiaLog();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    //------------------界面--------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPayActivity = PayActivity.this;

        Intent intent = getIntent();

        mUserName = intent.getStringExtra("UserName");
        mPrice = intent.getStringExtra("Price");
        mGoodsName = intent.getStringExtra("CpOrder");
        Bundle bundle = intent.getBundleExtra("Bundle");
        ArrayList<String> list = bundle.getStringArrayList("payChannel");

        if (uiState.screenOrientation == 0) {
            //显示横屏充值界面
            mMainLand = new MainLandUI(mUserName,mPrice,mGoodsName,list,mPayActivity);
            setContentView(mMainLand.getView(this));
            //设置横屏不可旋转
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mMainLand.setPayOnclickListener(new MainLandUI.OnPayclickListener() {
                @Override
                public void onPayClick() {
                    if (mMainLand.getPayDiaLog() != null) {
                        mMainLand.getPayDiaLog().dismiss();
                    }
                    finish();
                }
            });
        } else {
            // 显示竖屏充值界面
            mPortrait = new MainPortraitUI(mUserName,mPrice,mGoodsName,list,mPayActivity);
            setContentView(mPortrait.getPortrait(this));
            //设置竖屏不可旋转
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mPortrait.setPayOnclickListener(new MainPortraitUI.OnPayclickListener() {
                @Override
                public void onPayClick() {
                    if (mPortrait.getPayDiaLog() != null) {
                        mPortrait.getPayDiaLog().dismiss();
                    }
                    finish();
                }
            });

            mPortrait.setBackViewOnClockListener(new MainPortraitUI.OnBackViewOnClockListener() {
                @Override
                public void onBackView() {
                    ControlUI.getInstance().exitPay(Constants.PAY_CANCEL);
                    finish();
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //当启动了微信支付后WxPayFlag会改变,去查询支付结果,查询完后,WxPayFlag会重新变回true
        if (WxPayFlag) {
            WechatPayTask wechatPayTask = new WechatPayTask(this);
            wechatPayTask.checkWechatResult(ControlCenter.getInstance().getBaseInfo().payParam
                    .getOrderId());

            //当充值结束后(无论成功或失败),屏幕是横向的时候弹的dialog
            if (uiState.screenOrientation == 0) {
                if (mMainLand != null) {
                    mMainLand.affirmPayDiaLog();
                }
            } else {
                //当充值失败,屏幕是竖向时弹的dialog
                if (mPortrait != null) {
                    mPortrait.affirmPayDiaLog();
                }
            }
        }
    }
    //屏蔽返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showSuccessDialog() {
        if (uiState.screenOrientation == 0) {
            if (mMainLand != null) {
                mMainLand.affirmPayDiaLog();
            }
        } else {
            //当充值失败,屏幕是竖向时弹的dialog
            if (mPortrait != null) {
                mPortrait.affirmPayDiaLog();
            }
        }
    }

    public void showTPpayFailDialog() {
        if (uiState.screenOrientation == 0) {
            if (mPayActivity.getMainLand() != null) {
                mPayActivity.getMainLand().showPayResult("");
            }
        } else {
            //当充值失败,屏幕是竖向时弹的dialog
            if (mPayActivity.getPortrait() != null) {
                mPayActivity.getPortrait().showPayResult("");
            }
        }
    }
}
