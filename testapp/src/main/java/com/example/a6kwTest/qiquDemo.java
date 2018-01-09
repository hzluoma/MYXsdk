package com.example.a6kwTest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.myx.sdk.MYXChannelCode;
import com.myx.sdk.MYXChannelListener;
import com.myx.sdk.MYXChannelSDK;

public class qiquDemo extends Activity {

    private Context ctx;
    private final MYXChannelSDK sdk = MYXChannelSDK.getInstance();
    private String mUid;
    private String mUserName;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx=this;
        String appId = "1000000";
        String appKey = "b5d6ad09709d5eac958e8fb8ad511c76";

        //sdk初始化（很重要）
        sdk.wdInital(ctx,appId,appKey);

        mTextView = (TextView) findViewById(R.id.tv);

        //所有监听器都在这里设置（必须在sdk初始化之前，否则sdk初始化的错误不能监听）
        sdk.wdSetListener(new MYXChannelListener(){

            //用于返回各种成功或者出错的信息,msg用于返回额外信息
            @Override
            public void onResult(int code, final String msg) {
                switch (code){
                    case MYXChannelCode.COM_INIT_SDK_FAIL:
//                        Log.d(tag,"初始化失败，msg："+msg);
                        sdk.wdRunOnMainThread( new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(qiquDemo.this,"初始化失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case MYXChannelCode.COM_LOGIN_ACCOUNT_FAIL:
//                        Log.d(tag,"登录失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(qiquDemo.this,"登录失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case MYXChannelCode.COM_LOGOUT_PLT_FAIL:
//                        Log.d(tag,"注销失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(qiquDemo.this,"注销失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case MYXChannelCode.COM_PAY_COIN_FAIL:
//                        Log.d(tag,"支付失败，msg："+msg);
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(qiquDemo.this,"支付失败，msg："+msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case MYXChannelCode.COM_ENTERGAME_FAIL:
                        sdk.wdRunOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(qiquDemo.this,"上传用户信息失败，msg："+msg,Toast.LENGTH_SHORT)
                                        .show();
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
                        String version = "";
                        //version = Constants.SDK_VERSION;
                        Toast.makeText(qiquDemo.this,"初始化成功 " + version,Toast.LENGTH_LONG).show();
                    }
                });
            }

            //登录成功  result.getuuid()是渠道的唯一账户标识  服务端的验证接口可作验证
            @Override
            public void onLoginResult(final com.myx.sdk.inner.base.LoginResult result) {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        String info = "uid:"+result.getUuid()+",user_name:"+result.getUsername()+
                                ",nickname:"+result.getNickname()+",sessionId:"+result.getSessionId();
                        mUid = result.getUuid();
                        mUserName = result.getUsername();
                        Toast.makeText(qiquDemo.this,"登录成功"+info,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //注销成功,(这里要 switchAccount)
            @Override
            public void onLogout() {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(qiquDemo.this,"注销成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //支付成功
            @Override
            public void onPayResult(final String orderId) {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(qiquDemo.this,"支付成功，订单号为："+orderId,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onEnterGameResult() {
                sdk.wdRunOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(qiquDemo.this,"上传玩家信息成功",Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onIDVerification() {
                Toast.makeText(qiquDemo.this,"玩家已完成实名认证",Toast.LENGTH_SHORT)
                        .show();
            }
        });
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

    /**
     * 支付渠道参数类型以及长度等限制
     * 						含义          数据类型           长度限制         数据要求
     *  price   		    价钱      		String			6位数            纯数值的字符串,比如1或1001
     *  serverName		    服务器名字		String			18个字符         无限制
     *  serverID			服务器ID		    String			10位             纯数值的字符串,比如1或1001
     *  roleName			角色名字			String			30个字符         无限制
     *  roleID			    角色ID			String			20位数           纯数值的字符串,比如1或1001
     *  roleLevel			角色等级			String			10个字符         无限制
     *  goodsName			充值等级			String			20个字符         无限制
     *  goodsID		        商品ID			int				20位数           必须是纯数值的字符串,比如1或1001
     *  cpOrder             游戏方订单信息   String          64个字符         无限制
     *  extendstr           拓展字段			String		    255             无限制
     */
    public void pay(View v){
        sdk.wdRunOnMainThread(new Runnable() {
            @Override
            public void run() {
                String price = "1";                //价格,注意不能传带小数点的          单位 : 元(人民币)
                String serverName = "艾欧尼亚";     //服务器名称
                String serverID = "1001";          //服务器ID
                String roleName = "龙傲天";        //角色名称
                String roleID = "65535";           //角色ID
                String roleLevel = "30";           //角色等级
                String goodsName = "游戏币充值";    //商品名称
                int goodsID = 10086;               //商品ID
                String cpOrder = System.currentTimeMillis() + "";        //游戏方订单信息
                String extendstr = "附加参数";      //附加参数

                sdk.wdPay(price,serverName,serverID,roleName,roleID,roleLevel,
                        goodsName, goodsID,cpOrder, extendstr);
            }
        });
    }

    /**
     * 上传玩家的各种信息到渠道服务器
     * 						含义          数据类型           长度限制         数据要求
     *  serverID			服务器ID		    String			10位             纯数值的字符串,比如1或1001
     *  serverName		    服务器名字		String			10个字符         无限制
     *  roleID			    角色ID			String			12位数           纯数值的字符串,比如1或1001
     *  roleName			角色名字			String			28个字符         无限制
     *  roleLV			    角色等级			String			10个字符         无限制
     *  payLevel			充值等级			String			10个字符         无限制
     *  extendstr			拓展字段			String		    255(不建议过长)  无限制
     */
    public void enterGame(View view) {
        String serverID = "3";                  //游戏区服ID
        String serverName = "艾欧尼亚";          //游戏区服名称
        String roleID = "666";                  //角色ID
        String roleName = "Faker";              //角色名称
        String roleLV = "30";                   //角色等级
        String payLevel = "心悦会员10";          //充值等级
        String extendstr = "拓展字段";           //拓展字段

        sdk.wdEnterGame(serverID,serverName,roleID,roleName,roleLV,payLevel,
                extendstr);

    }


    public void ID_VERIFICATION(View view) {
        sdk.wdIDVerification();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
