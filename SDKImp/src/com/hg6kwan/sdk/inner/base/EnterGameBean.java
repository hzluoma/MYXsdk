package com.hg6kwan.sdk.inner.base;

/**
 * Created by Administrator on 2016/12/13.
 */

import com.alibaba.fastjson.JSONObject;
import com.hg6kwan.sdk.inner.platform.ControlCenter;

/**
 * 记录玩家进入游戏时的信息的bean类
 */

public class EnterGameBean {
    private String uid = "";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String userName = "";
    private String serverID = "";        //游戏区服ID
    private String serverName = "";      //游戏区服名称
    private String roleID = "";          //角色ID
    private String roleName = "";        //角色名称
    private String roleLV = "";          //角色等级
    private String rechargeLV = "";      //充值等级


    private String channel = "";      //渠道ID
    private String device_Code = "";     //设备码
    private String extendstr = "";       //拓展字段

    public String getExtendstr() {
        return extendstr;
    }

    public void setExtendstr(String extendstr) {
        this.extendstr = extendstr;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleLV() {
        return roleLV;
    }

    public void setRoleLV(String roleLV) {
        this.roleLV = roleLV;
    }

    public String getRechargeLV() {
        return rechargeLV;
    }

    public void setRechargeLV(String rechargeLV) {
        this.rechargeLV = rechargeLV;
    }


    public String getDevice_Code() {
        return device_Code;
    }

    public void setDevice_Code(String device_Code) {
        this.device_Code = device_Code;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    /**
     *
     * @param serverID  游戏区服ID
     * @param serverName    游戏区服名称
     * @param roleID        角色ID
     * @param roleName      角色名称
     * @param roleLV        角色等级
     * @param rechargeLV    充值等级
     */
    public EnterGameBean(String uid, String userName,String serverID, String serverName, String
            roleID, String roleName, String  roleLV, String rechargeLV, String extendstr) {
        this.uid = uid;
        this.userName = userName;
        this.serverID = serverID;
        this.serverName = serverName;
        this.roleID = roleID;
        this.roleName = roleName;
        this.roleLV = roleLV;
        this.rechargeLV = rechargeLV;
        this.device_Code = ControlCenter.getInstance().getBaseInfo().UUID;
        this.extendstr = extendstr;
    }


    //拼接成一个Json字符串
    @Override
    public String toString() {
        return (String) JSONObject.toJSON(this);
    }
}
