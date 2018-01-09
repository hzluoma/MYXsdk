package com.hg6kwan.sdk.inner.base;


import java.io.Serializable;

/**
 * 玩家登陆信息
 */
public class LoginInfo implements Serializable{
	private String u;
    private String p;
    private boolean isTip = true;

    //
	public LoginInfo() {
		this.u = "";
		this.p = "";
	}
    public LoginInfo(String acc, String psw) {
        this.u = acc;
        this.p = psw;
    }
    public LoginInfo(String acc,String psw,boolean isTip){
        this(acc,psw);
        this.isTip = isTip;
    }
    //
    public String getU() {
        return u;
    }
    public void setU(String u) {
        if (u == null)return;
        this.u = u;
    }
    public String getP() {
        return p;
    }
    public void setP(String p) {
        if (p ==null)return;
        this.p = p;
    }
    public void setTip(){
        this.isTip = false;
    }
    public boolean isTip(){
        return isTip;
    }

}
