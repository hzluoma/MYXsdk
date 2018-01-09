package com.hg6kwan.sdk.inner.ui.loading;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.LoginResult;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.floatmenu.FloatMenuManager;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDBDao;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDomain;
import com.hg6kwan.sdk.inner.utils.MD5;
import com.hg6kwan.sdk.inner.utils.task.HttpTask;

import java.util.HashMap;
import java.util.List;

/**
 * Created by xiaoer on 2016/7/7.
 */
public class LoadingLoginDialog extends LoadingBase {
    private String acc;
    private String psd;
    private Context mContext;

    private String token;
    private String errorMsg;

    protected NoticeDBDao mNoticeDBDao;

    public LoadingLoginDialog(Context context, String acc, String psd){
        super(context,acc,"正在登录...");
        mContext=context;
        this.acc=acc;
        this.psd=psd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ControlUI.getInstance().setmHandler(mHandler);
        new LoginThread(mContext,acc,psd).start();
    }

    private Handler mHandler = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            ControlUI.getInstance().closeSDKUI();
            BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
            if(msg.what == 1){
                //这里要返回登录成功后渠道服务器返回的信息
                if (baseInfo.login==null || (!baseInfo.isBinding && baseInfo.login.isTip())){
                    ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.TIP);

                }else {
                    //登陆成功后开启悬浮窗
                    FloatMenuManager.getInstance().startFloatView(mContext);
                    //开启悬浮窗后判断是否有公告信息要展示
                    NoticeDBDao.getInstance(mContext).isOpenNoticeDialog();

                    ControlCenter.getInstance().onLoginResult(ControlCenter.getInstance().getBaseInfo().loginResult);
                }
            }else{
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.LOGIN);
                ControlCenter.getInstance().onResult(ReturnCode.COM_LOGIN_ACCOUNT_FAIL,errorMsg);
            }
        }
    };

    public class LoginThread extends Thread {
        private String mAccount;
        private String mPassword;
        private Context mContext;

        public LoginThread(Context context, String mAccount, String mPassword) {
            super();
            this.mAccount = mAccount;
            this.mPassword = mPassword;
            this.mContext = context;
        }

        //
        @Override
        public void run() {
            int what = 0;
            String msg = null;
            try {
                HttpResultData result = ControlCenter.getInstance().getmLoginService().login(mAccount, mPassword);

                what = result.state.getInteger("code");
                msg = result.state.getString("msg");
                //成功为1
                if (what == 1) {
                    //返回值data
                    BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
                    baseInfo.gSessionId = result.data.getString("sid");
                    baseInfo.gUid = result.data.getString("uid");
                    baseInfo.nickName = result.data.getString("nick_name");
                    baseInfo.isBinding = TextUtils.equals(result.data.getString("isBindMobile"),"0")?false:true;

                    //保存登录时渠道server返回的信息
                    LoginResult loginResult = new LoginResult();
                    loginResult.setUuid(result.data.getString("uid"));
                    loginResult.setNickname(result.data.getString("nick_name"));
                    loginResult.setUsername(result.data.getString("user_name"));
                    loginResult.setSessionId(result.data.getString("sid"));
                    //保存实名认证相关的信息  包括四个主要数据 : 1.是否实名认证过 2.实名验证是否开启  3.是否老用户  4.是否成年
                    //获取是否实名认证过
                    Boolean isTrueName = TextUtils.isEmpty(result.data.getString("trueName")) ?
                            false : true;
                    loginResult.setTrueName(isTrueName);
                    //获取实名验证的开关
                    Boolean isTrueNameSwitch = "0".equals(result.data.getString("trueNameSwitch")
                    ) ? false : true;
                    loginResult.setTrueNameSwitch(isTrueNameSwitch);

                    Boolean isOldUser = result.data.getInteger("isOldUser") == 0 ? false : true;
                    loginResult.setOldUser(isOldUser);

                    Boolean isAdult = result.data.getInteger("adult") == 0 ? false : true;
                    loginResult.setAdult(isAdult);

                    loginResult.setExtension(result.toString());
                    baseInfo.loginResult=loginResult;

                    try {
                        if (result.data.getJSONObject("noticeHtml") != null && result.data
                                .getJSONObject("noticeHtml").getJSONObject("0") != null) {

                            //获取服务端传来的公告信息
                            JSONObject noticeHtml = result.data.getJSONObject("noticeHtml").getJSONObject
                                    ("0");
                            String sContent = noticeHtml.getString("sContent");
                            String noticeID = noticeHtml.getString("sId");
                            //根据服务端传来的公告信息去数据库查询是否已经保存了这条信息
                            mNoticeDBDao = NoticeDBDao.getInstance(mContext);
                            mNoticeDBDao.openDataBase();

                            /**根据消息的ID来查询返回的是null的时候,可能的情况有两种 :
                             * 1.刚安装上去,数据库里面还没有数据
                             * 2.数据库里面没有关于这条消息,但是存有别的消息,就先清除数据,再插入数据
                             */
                            if (mNoticeDBDao.queryData(noticeID) == null) {
                                NoticeDomain noticeDomain = new NoticeDomain();
                                noticeDomain.setMessageID(noticeID);
                                noticeDomain.setContent(sContent);

                                mNoticeDBDao.deleteAllData();
                                mNoticeDBDao.insertData(noticeDomain);
                            }
                            //关闭数据库
                            mNoticeDBDao.closeDataBase();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    CommonFunctionUtils.update_loginList(mContext,baseInfo.loginList,mAccount,mPassword);

                    if(!baseInfo.loginList.isEmpty()){
                        baseInfo.login = baseInfo.loginList.get(baseInfo.loginList.size()-1);
                    }
                } else {
                    errorMsg = msg;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg="登录异常";
            }
            //这里是用来当重新登录账号后,之前在floatMenu的show()方法里面设置了这个IsCheck为false,重新登录设为true,开启轮询
            Constants.IS_CHECKACC = true;
            showFixTime();
            //将returnCode发送到Handler,在对应的界面中调用exit（正常退出）或cancel(取消)
            if(ControlUI.getmHandler()!=null){
                ControlUI.getmHandler().sendEmptyMessage(what);
            }
        }
    }
}
