package com.hg6kwan.sdk.inner.platform;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.base.EnterGameBean;
import com.hg6kwan.sdk.inner.base.InitInfo;
import com.hg6kwan.sdk.inner.base.LoginResult;
import com.hg6kwan.sdk.inner.base.PayParam;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.service.LoginService;
import com.hg6kwan.sdk.inner.ui.floatmenu.FloatMenuManager;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.SPUtil;
import com.hg6kwan.sdk.inner.utils.UserUtil;
import com.hg6kwan.sdk.inner.utils.task.EnterGameTask;
import com.hg6kwan.sdk.inner.utils.task.H5GetOrderTask;
import com.hg6kwan.sdk.inner.utils.task.PayStateTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by bt on 2016/09/29.
 * ControlCenter
 * 实现sdk各个功能的控制中心
 */
public class ControlCenter {
    //迟点优化网络
    private static LoginService mLoginService;
    private static ControlCenter instance;

    private List<IListener> listeners;
    private Activity mContext;
    private Handler mainThreadHandler;
    private BaseInfo baseInfo;
    private boolean isInitOk = false;
    private static Object mLock = new Object();
    //assets 中图片数量
    public final int imgNumInAssets = 58;


    private ControlCenter(){
            baseInfo = new BaseInfo();
            mainThreadHandler = new Handler(Looper.getMainLooper());
            listeners = new ArrayList<IListener>(1);
            mLoginService = new LoginService();
    }

    public static ControlCenter getInstance(){
        synchronized(mLock) {
            if (instance == null) {
                instance = new ControlCenter();
            }
        }
        return instance;
    }

    public void resetData(){
        if (listeners != null){
            listeners.clear();
        }
    }

    public boolean isInited() {
        if (baseInfo == null)
            return false;
        return uiState.screenOrientation != -1 && isInitOk;
    }

    public void setInitOK(){
        this.isInitOk = true;
    }

    /**
     * sdk的初始化，设置全局参数baseInfo
     * 说明：如果宿主没屏蔽自动转屏，可以导致重复调用此方法
    */
    public void inital(Context context, final String appId, final String appKey){
        mContext = (Activity) context;

        //Request Permission if SDK Version > Android L
        CommonFunctionUtils functionUtils = new CommonFunctionUtils();
        functionUtils.checkPermission();

        //load assets
        HashMap<String,Bitmap> map = uiUtils.loadAssetsImg(mContext);
        if (map == null || map.keySet().size() != imgNumInAssets){
            onResult(ReturnCode.COM_INIT_SDK_FAIL,"sdk图片资源初始化失败");
            return;
        }
        uiState.setResMap(map);
        //
        //resetData();
        //初始化

        reInit(context,appId,appKey);
        //
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLoginService.notifyInitSDK();
            }
        }).start();
        //创建按钮
        reCreatMenu();
    }




    //是否登陆
    public boolean isLogin(){
		return !TextUtils.isEmpty(baseInfo.gSessionId);
	}

    //登陆信息
    private boolean hasAccountInfo(){return baseInfo.login!=null;}

    /**
     * 初始化
     */
    public void reInit(Context context, final String appId, final String appKey){
        if(!isInited()){
            ControlUI.getInstance().doLoadingInit(baseInfo,context,appId,appKey);
        }
    }

    /**
     * 登陆
     */
    public void login(){
        if (!isInited()){
            onResult(ReturnCode.COM_LOGIN_ACCOUNT_FAIL,"sdk尚未初始化！");return;
        }
		//登陆->获取历史信息，如果没有发现则弹出一键注册窗口
        if(!hasAccountInfo()){
            //注册界面(一键注册)
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.REG);
        }else{
            if(baseInfo.gIsAutoLogin){
                //此功能尚未实现(默认false)
                autoLogin();
            }else{
                //登录界面(有账号信息)
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.LOGIN);
            }
        }
	}

    //自动登陆中(loading dialog)
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void autoLogin(){
        //自动登陆
        ControlUI.getInstance().doLoadingLogin(baseInfo.login.getU(),baseInfo.login.getP());
    }

    /**
     * 注销
    */
    public void logout(){
        if (!isLogin()){
            onResult(ReturnCode.COM_LOGOUT_PLT_FAIL,"尚未登陆");
        }else {
            baseInfo.gSessionId = null;
            onLogout();
        }
        //当注销后就停止轮询
        UserUtil.stopCheckAcc();
        runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.LOGIN);
                }
            });
    }

    /**
     *
     * @param price
     * @param serverName
     * @param ServerID
     * @param roleName
     * @param roleID
     * @param roleLevel
     * @param cpOrder
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void pay(final String price,final String
            serverName,final String
            ServerID,final String roleName,final String roleID,final String roleLevel,final
    String goodsNAME,final int goodsID,final String cpOrder,final String extendstr){
        //判断有没有初始化
        if (!isInited()){
            onResult(ReturnCode.COM_PAY_COIN_FAIL,"sdk未初始化");return;
        }
        //判断有没有登录
        if (!isLogin()){
            onResult(ReturnCode.COM_PAY_COIN_FAIL,"账号未登录");return;
        }
        //先检查是否有打开实名验证的开关,没打开就跳过
        if (baseInfo.loginResult.isTrueNameSwitch()) {
            //检查是否进行过实名认证,如果没有就弹出实名认证,实名验证过了就进一步检查是否成年
            if (baseInfo.loginResult.isTrueName()) {
                //成年了就可以进行充值
                if (baseInfo.loginResult.isAdult()) {

                    //未成年就不能进行充值了
                } else {
                    Toast.makeText(mContext, "因国家法规规定,未成年用户不可进行游戏充值", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(mContext, "国家法规规定,手游充值消费前须进行实名认证,未成年玩家不可在游戏内消费", Toast.LENGTH_SHORT)
                        .show();
                this.IDVerification();
                return;
            }
        } else {

        }

        String uid = baseInfo.gUid;
        String username = baseInfo.loginResult.getUsername();

        baseInfo.payParam = new PayParam(uid,username,price, serverName, ServerID, roleName, roleID,
                roleLevel,
                goodsNAME, goodsID, cpOrder, extendstr);

        //获取支付渠道 H5支付 or  APP支付  APP支付获取对应的渠道ID并显示
        new PayStateTask().getPayState(uid,username,price, serverName, ServerID,
                        roleName, roleID, roleLevel, goodsNAME, goodsID, cpOrder, extendstr);

    }

    //H5支付
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void H5Pay(final String uid, String price, final String serverId, final String roleName,
                      final String goodsNAME, final int goodsId, final String cpOrder, final String
                              extendStr){
        //H5支付获取订单号并跳转到H5的任务栈,在H5的任务栈会启动H5支付的dialog
        H5GetOrderTask h5GetOrderTask = new H5GetOrderTask();
        h5GetOrderTask.getOrderId(uid,price,serverId,roleName,goodsNAME,goodsId,cpOrder,extendStr);

    }

    /**
     * 记录玩家进入游戏时的一系列数据
     */
    public void enterGame(String serverID,String serverName,String
            roleID,String roleName, String roleLV,String payLevel,String extendstr) {

        //判断有没有初始化
        if (!isInited()){
            onResult(ReturnCode.COM_ENTERGAME_FAIL,"sdk未初始化");return;
        }
        //判断有没有登录
        if (!isLogin()){
            onResult(ReturnCode.COM_ENTERGAME_FAIL,"账号未登录");return;
        }

        String username = baseInfo.loginResult.getUsername();
        String uid = baseInfo.gUid;

        EnterGameBean enterGameBean = new EnterGameBean(uid,username,serverID,serverName,roleID,
                roleName,
                roleLV,payLevel,extendstr);

        EnterGameTask enterGameTask = new EnterGameTask(mContext);
        enterGameTask.postUserInfo(enterGameBean);
    }

    /**
     * 实名验证接口
     */
    public void IDVerification() {
        //判断有没有初始化
        if (!isInited()){
            Toast.makeText(mContext, "sdk未初始化", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断有没有登录
        if (!isLogin()){
            Toast.makeText(mContext, "账号未登录", Toast.LENGTH_SHORT).show();
            return;
        }
        //根据登录的时候获得的信息判断是否需要打开实名认证,由后台控制
        boolean trueNameSwitch = baseInfo.loginResult.isTrueNameSwitch();
        boolean trueName = baseInfo.loginResult.isTrueName();
        //如果服务端不打开这个开关,即trueNameSwitch为false
        if (!trueNameSwitch) {
            return;
        }
        //弹出让用户去完善身份信息的窗口,如果trueName为空,就说明没有进行实名认证,有值的话就是有
        if (trueName) {
            return;
        }
        //这里是防止已经实名认证过又没有重新登录,结果又可以调起实名验证的情况
        SPUtil spUtil = new SPUtil(mContext, "trueNameList");
        String isVerification = spUtil.getString(baseInfo.login.getU(), "");
        if (!TextUtils.isEmpty(isVerification)) {
            return;
        }
        //弹出实名认证的询问窗口
//        ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.ID_VERIFICATION);
        //打开实名认证的网页窗口
        ControlUI.getInstance().startUI(mContext, ControlUI.WEB_TYPE.ID_VERIFICATION);
    }


    public BaseInfo getBaseInfo(){return baseInfo;}

    public BaseInfo getBaseInfo(JSONObject jsonObject){

        baseInfo.gSessionId = jsonObject.get("sid").toString();
        baseInfo.gUid = jsonObject.get("uid").toString();
        baseInfo.nickName = jsonObject.get("nick_name").toString();
        baseInfo.isBinding = TextUtils.equals(jsonObject.get("isBindMobile").toString(),"0")?false:true;
        return baseInfo;
    }

    public void runOnMainThread(Runnable runnable){
        if(mainThreadHandler != null){
            mainThreadHandler.post(runnable);
            return;
        }

        if (mContext != null){
            mContext.runOnUiThread(runnable);
        }
    }

    public LoginService getmLoginService() {
        if (mLoginService == null) {
            mLoginService = new LoginService();
        }
        return mLoginService;
    }

    public Context getmContext(){return mContext;}


    public void setListener(IListener listener){
        if(listeners != null && listeners.size() == 0 && listener != null){
            this.listeners.add(listener);
        }
    }

    //监听器的回调(5个)
    public void onResult(int code, String msg){
        for(IListener listener : listeners){
            listener.onResult(code,msg);
        }
    }

    public void onInit(){
        for(IListener listener : listeners){
            listener.onInit();
        }
    }
    public void onLoginResult(LoginResult token){
        for (IListener listener : listeners){
            listener.onLoginResult(token);
        }
    }
    public void onLogout(){
        for(IListener listener : listeners){
            listener.onLogout();
        }
    }
    public void onPayResult(String orderId){
        for (IListener listener : listeners){
            listener.onPayResult(orderId);
        }
    }

    public void onEnterGameResult(){
        for (IListener listener : listeners){
            listener.onEnterGameResult();
        }
    }

    //实名认证接口的回调
    public void onIDVerification(){
        for (IListener listener : listeners){
            listener.onIDVerification();
        }
    }

    private void hideBothNavigationBarAndStatusBar() {
        View decorView = mContext.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //监听游戏主Activity，对悬浮窗做处理
    public void showMenu(){
        if (isLogin()){
            FloatMenuManager.getInstance().showFloatingView();
        }
    }

    //重新打开menu,需要处于登陆状态
    public void reCreatMenu(){
        if (isLogin()&&mContext!=null){
            //登陆之后直接
            FloatMenuManager.getInstance().startFloatView(mContext);
        }
    }

    //隐藏浮动
    public void hideMenu(){
        FloatMenuManager.getInstance().hideFloatingView();
    }

    //正常化浮动
    public void normalizeMenu(){
        FloatMenuManager.getInstance().normalizeFlaotingView();
    }

    //窗口恢复
    public void onActivityResume() {
        if (!ControlUI.getInstance().isShowingDialog())
            showMenu();
    }

    //窗口暂停
    public void onActivityPause() {
        hideMenu();
    }

    public void onActivityDestroy() {
        FloatMenuManager.getInstance().destroy();
    }


}
