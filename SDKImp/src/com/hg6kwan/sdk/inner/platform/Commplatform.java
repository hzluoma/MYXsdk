package com.hg6kwan.sdk.inner.platform;

import android.content.Context;
import com.hg6kwan.sdk.MYXChannelListener;

/**
 * Created by mp on 2016/09/29.
 * Commplatform
 */
public abstract class Commplatform {

	public void wdRunOnMainThread(Runnable runnable){
        ControlCenter.getInstance().runOnMainThread(runnable);
    }

	/**
	 * 初始化sdk
	 * @param context,appid,appkey
	*/
    public void wdInital(Context context,String appId,String appKey){
        ControlCenter.getInstance().inital(context,appId,appKey);
    }


	/**
	 * 注册监听器
	 * @param listener
	 */
	public void wdSetListener(MYXChannelListener listener){
		ControlCenter.getInstance().setListener(listener);
	}

    /**
     * 登录
     */
	public void wdLogin(){
		ControlCenter.getInstance().login();
	}


	/**
	 * 注销
	 */
	public void wdLogout(){
		ControlCenter.getInstance().logout();
	}


	/**
	 * 进入游戏时上传玩家的各种信息到服务器
	 * 							  含义          数据类型       长度限制
	 * @param serverID			服务器ID		    int				10位
	 * @param serverName		服务器名字		String			10个字符
	 * @param roleID			角色ID			int				12位数
	 * @param roleName			角色名字			String			28个字符
	 * @param roleLV			角色等级			String			10个字符
	 * @param payLevel			充值等级			String			10个字符
     * @param extendstr			拓展字段			String			64个字符
     */
	public void wdEnterGame(String serverID,String serverName,String
			roleID,String roleName, String roleLV,String payLevel,String extendstr) {
		ControlCenter.getInstance().enterGame(serverID,serverName,roleID,roleName,
				roleLV, payLevel,extendstr);
	}

	/**
	 *
	 * @param price
	 * @param serverName
	 * @param serverId
	 * @param roleName
	 * @param roleID
	 * @param roleLevel
	 * @param goodsName
	 * @param goodsId
     * @param cpOrder
     * @param extendstr
     */
    public void wdPay(String price,String serverName,String serverId,
					  String
			roleName,String roleID,String roleLevel,String goodsName,int goodsId,String cpOrder,
					  String extendstr){
        ControlCenter.getInstance().pay(price,serverName,serverId,roleName,roleID,
				roleLevel,goodsName,goodsId,cpOrder,extendstr);
    }


    /**
     * 操作浮标
     * @param
     */

    public void wdSetFloatMenu(int h){
        switch (h){
            case 1:
                ControlCenter.getInstance().showMenu();
                break;
            case 2:
                ControlCenter.getInstance().hideMenu();
                break;
            case 3:
                ControlCenter.getInstance().normalizeMenu();
                break;
        }

    }

	public void wdIDVerification() {
		ControlCenter.getInstance().IDVerification();
	}

    public void onActivityResume(){
		ControlCenter.getInstance().onActivityResume();
	}

	public void onActivityPause(){
		ControlCenter.getInstance().onActivityPause();
	}

	public void onActivityDestroy(){
		ControlCenter.getInstance().onActivityDestroy();
	}

}
