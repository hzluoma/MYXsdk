package com.hg6kwan.sdk.inner.ui.floatmenu;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;

import com.hg6kwan.sdk.inner.utils.UserUtil;

import java.io.ObjectStreamException;

/**
 * Created by xiaoer on 2016/11/1.
 */

public class FloatMenuManager implements ServiceConnectionManager.QdServiceConnection{
    private ServiceConnectionManager mServiceConnectionManager;
    private FloatMenuManager(){}

    //静态内部类实现单例  优于双重检查锁(DCL)单例
    public static FloatMenuManager getInstance() {
        return MenuHolder.single;
    }

    /**
     * 静态内部类能够解决DCL双重检查锁失效的问题
     */
    private static class MenuHolder {
        private static final FloatMenuManager single = new FloatMenuManager();
    }

    /**
     * 防止反序列获取新的单例
     *
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return MenuHolder.single;
    }

    private FloatMenuService mMenuService;

    //此处context为 application的context，将其与FloatMenuService绑定
    public void startFloatView(Context context) {
        if (mMenuService != null) {
            mMenuService.showFloat();
            return;
        }
        //直接覆盖原来存在的manager
        mServiceConnectionManager = new ServiceConnectionManager(context, FloatMenuService.class, this);
        mServiceConnectionManager.bindToService();
    }

    /**
     * 显示悬浮图标
     */
    public void showFloatingView() {
        if (mMenuService != null) {
            mMenuService.showFloat();
        }
    }

    /**
     * 隐藏悬浮图标
     */
    public void hideFloatingView() {
        if (mMenuService != null) {
            mMenuService.hideFloat();
        }
    }

    public void normalizeFlaotingView(){
        if (mMenuService != null) {
            mMenuService.normalize();
        }
    }

    /**
     * 释放QDSDK数据
     */
    public void destroy() {
        if (mMenuService != null) {
            mMenuService.hideFloat();
            mMenuService.destroyFloat();
        }
        if (mServiceConnectionManager != null) {
            mServiceConnectionManager.unbindFromService();
        }
        mMenuService = null;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMenuService = ((FloatMenuService.MenuServiceBinder) service).getService();
        if (mMenuService != null) {
            mMenuService.showFloat();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMenuService = null;
    }
}
