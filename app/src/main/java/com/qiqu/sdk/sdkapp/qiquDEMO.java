package com.qiqu.sdk.sdkapp;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.hg6kwan.sdk.HG6kwanChannelCode;
import com.hg6kwan.sdk.HG6kwanChannelListener;
import com.hg6kwan.sdk.HG6kwanChannelSDK;
import com.hg6kwan.sdk.inner.base.LoginResult;

import static android.os.Build.MODEL;

/**
 * Created by xiaoer on 2016/09/29.
 * demoActivity
 * cp参考用例
 */
public class MainActivity extends Activity{
    private Context ctx;
    private final String tag = "==MainActivity==";

    private final HG6kwanChannelSDK sdk = HG6kwanChannelSDK.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qiqu_demo);
        ctx=this;

        //所有监听器都在这里设置（必须在sdk初始化之前，否则sdk初始化的错误不能监听）
        sdk.wdSetListener(new HG6kwanChannelListener(){

            //用于返回各种成功或者出错的信息,msg用于返回额外信息
            @Override
            public void onResult(int code, final String msg) {
                switch (code){
                    case HG6kwanChannelCode.COM_INIT_SDK_FAIL:
//                        Log.d(tag,"初始化失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"初始化失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case HG6kwanChannelCode.COM_LOGIN_ACCOUNT_FAIL:
//                        Log.d(tag,"登录失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"登录失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case HG6kwanChannelCode.COM_LOGOUT_PLT_FAIL:
//                        Log.d(tag,"注销失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"注销失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case HG6kwanChannelCode.COM_PAY_COIN_FAIL:
//                        Log.d(tag,"支付失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"支付失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onInit() {
                //初始化成功，用于游戏做相应处理
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"初始化成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //登录成功
            @Override
            public void onLoginResult(final LoginResult result) {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        String info = "uid:"+result.getUuid()+",user_name:"+result.getUsername()+
                                ",nickname:"+result.getNickname()+",sessionId:"+result.getSessionId();
                        Toast.makeText(MainActivity.this,"登录成功"+info,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //注销成功,(这里要 switchAccount)
            @Override
            public void onLogout() {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"注销成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //支付成功
            @Override
            public void onPayResult(final String orderId) {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"支付成功，订单号为："+orderId,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        String appId = "1000000";
        String appKey = "8a706e870f7afd73411b23e626440365";

        //sdk初始化（很重要）
        sdk.wdInital(ctx,appId,appKey);


    }

    public void login(View v){
        sdk.wdRunOnMainThread(new Runnable() {
            @Override
            public void run() {
                sdk.wdLogin();
            }
        });
    }

    public void logout(View v){
        //此处参数为 注销后是否跳转到登录页面,如果不设置前面的开关，这里默认是跳转到login页面
        sdk.wdRunOnMainThread(new Runnable() {
            @Override
            public void run() {
                sdk.wdLogout();
            }
        });
    }


    public void pay(View v){
        sdk.wdRunOnMainThread(new Runnable() {
            @Override
            public void run() {
                //serverId,roleId,coin,desc,extendStr
                sdk.wdPay("1001","player1",6,"代币充值","附加参数");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sdk.onActivityResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sdk.onActivityPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sdk.onActivityDestroy();
    }
}
