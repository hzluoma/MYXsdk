package com.hg6kwan.sdk.inner.base;

/**
 * Created by xiaoer on 2016/9/23.
 */
public class LoginResult {
    //解析后的数据
    private String sessionId;
    private String uuid;
    private String username;
    private String nickname;
    //将服务端返回全部打包
    private String extension;
    //是否有实名认证,空字符串代表没有
    private boolean trueName = false;
    //控制是否显示实名认证窗口的开关
    private boolean trueNameSwitch = false;
    //是否是成年人的标识   登录返回的字段里面0代表不是1代表是
    private boolean isAdult = false;
    //是否是开启实名制验证之前的用户 根据登录返回的字段isOldUser判断,0代表不是,1代表是
    private boolean isOldUser = false;

    public boolean isTrueNameSwitch() {
        return trueNameSwitch;
    }

    public void setTrueNameSwitch(boolean trueNameSwitch) {
        this.trueNameSwitch = trueNameSwitch;
    }

    public boolean isTrueName() {
        return trueName;
    }

    public void setTrueName(boolean trueName) {
        this.trueName = trueName;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public void setAdult(boolean adult) {
        isAdult = adult;
    }

    public boolean isOldUser() {
        return isOldUser;
    }

    public void setOldUser(boolean oldUser) {
        isOldUser = oldUser;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sid) {
        this.sessionId = sid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
