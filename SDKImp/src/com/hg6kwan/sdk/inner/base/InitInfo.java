package com.hg6kwan.sdk.inner.base;

import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

import com.hg6kwan.sdk.inner.platform.ControlCenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 存储初始化需要发送到服务器的数据的javaBean
 * Created by Roman on 2017/6/6.
 */

public class InitInfo {

    private static InitInfo mInitInfo;

    private String channel_ID = "";      //获取到的解密后的channelID  解密不了的就会为空
    private String logicChannel = "";    //未解密前的channelID       只要meta-data下有对应的文件,就一定会被赋值,为空说明没有被赋值
    private String u8OldChannel = "";    //旧的channel值,如果没读取到,也即meta-data下没有对应的u8channel开头的文件,就为空字符串
    private String androidVersion = "";  //安卓版本号
    private String manufacturer = "";    //手机厂商
    private String model = "";           //手机型号

    private InitInfo() {}

    public static InitInfo getInstance() {
        if (mInitInfo == null) {
            mInitInfo = new InitInfo();
        }
        return mInitInfo;
    }

    public String getChannel_ID() {
        return channel_ID;
    }

    public void setChannel_ID(String channel_ID) {
        this.channel_ID = channel_ID;
    }

    public String getLogicChannel() {
        return logicChannel;
    }

    public void setLogicChannel(String logicChannel) {
        this.logicChannel = logicChannel;
    }

    public String getAndroidVersion() {
        return androidVersion;
    }

    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }

    public String getU8OldChannel() {
        return u8OldChannel;
    }

    public void setU8OldChannel(String u8OldChannel) {
        this.u8OldChannel = u8OldChannel;
    }

    public boolean isMIUIRom(){
//        String property = getSystemProperty("ro.miui.ui.version.name");
        String property = getSystemProperty("ro.build.version.emui");
        return !TextUtils.isEmpty(property);
    }

    @Override
    public String toString() {
        return "InitInfo{" +
                "channel_ID='" + channel_ID + '\'' +
                ", logicChannel='" + logicChannel + '\'' +
                ", u8OldChannel='" + u8OldChannel + '\'' +
                ", androidVersion='" + androidVersion + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                '}';
    }

}
