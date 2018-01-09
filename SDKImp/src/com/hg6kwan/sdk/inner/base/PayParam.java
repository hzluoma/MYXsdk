package com.hg6kwan.sdk.inner.base;

/**
 * Created by mp on 2016/9/23.
 */
public class PayParam {

    private String uid;                 //账户唯一标识
    private String userName;            //账号
    private String price;               //价格
    private String serverName;          //服务器名称
    private String serverId;            //服务器ID
    private String roleName;            //角色名称
    private String roleID;              //角色ID
    private String roleLevel;           //角色等级
    private String goodsName;           //商品名称
    private int goodsID;                //商品ID
    private String cpOrder;             //订单ID
    private String extendstr;           //附加参数

    private String payUrl;
    private String orderId = "";        //用于回调接口通知外面的订单号

    public PayParam(String uid, String userName, String price, String serverName, String
            serverId, String roleName, String roleID, String roleLevel, String goodsName, int
                            goodsID, String cpOrder, String extendstr) {
        this.uid = uid;
        this.userName = userName;
        this.price = price;
        this.serverName = serverName;
        this.serverId = serverId;
        this.roleName = roleName;
        this.roleID = roleID;
        this.roleLevel = roleLevel;
        this.goodsName = goodsName;
        this.goodsID = goodsID;
        this.cpOrder = cpOrder;
        this.extendstr = extendstr;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(String roleLevel) {
        this.roleLevel = roleLevel;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(int goodsID) {
        this.goodsID = goodsID;
    }

    public String getCpOrder() {
        return cpOrder;
    }

    public void setCpOrder(String cpOrder) {
        this.cpOrder = cpOrder;
    }

    public String getExtendstr() {
        return extendstr;
    }

    public void setExtendstr(String extendstr) {
        this.extendstr = extendstr;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
