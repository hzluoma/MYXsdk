package com.hg6kwan.sdk.inner.ui.floatmenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.UserUtil;

import java.util.Timer;
import java.util.TimerTask;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


/**
 * Created by xiaoer on 2016/11/1.
 */

public class FloatMenuService extends Service implements View.OnClickListener {
    private FloatMenu mFloatMenu;
    private AlertDialog isShowDialog = null;
    private final static String TAG = FloatMenuService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        return new MenuServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //创建悬浮菜单
        mFloatMenu = new FloatMenu.Builder(this)
                .addMenuItem(MenuItem.TYPE.USER, this)
                .addMenuItem(MenuItem.TYPE.GAME, this)
                .addMenuItem(MenuItem.TYPE.NEWS, this)
                .addMenuItem(MenuItem.TYPE.GIFT, this)
                .addMenuItem(MenuItem.TYPE.SERVICE, this)
                .addMenuItem(MenuItem.TYPE.LOGOUT, this)
                .build();
        mFloatMenu.setFocusable(true);
        mFloatMenu.show();
    }


    @Override
    public void onClick(View v) {
        if (v instanceof MenuItemView) {
            if (isShowDialog !=null && isShowDialog.isShowing()) return;
            MenuItemView menuItemView = (MenuItemView) v;
            MenuItem.TYPE type = menuItemView.getMenuItem().getType();
            switch (type) {
                case USER:
                    ControlUI.getInstance().startUI(ControlUI.WEB_TYPE.USER);
                    break;
                case GAME:
                    ControlUI.getInstance().startUI(ControlUI.WEB_TYPE.GAME);
                    break;
                case NEWS:
                    ControlUI.getInstance().startUI(ControlUI.WEB_TYPE.NEWS);
                    break;
                case GIFT:
                    ControlUI.getInstance().startUI(ControlUI.WEB_TYPE.GIFT);
                    break;
                case SERVICE:
                    ControlUI.getInstance().startUI(ControlUI.WEB_TYPE.SERVICE);
                    break;
                case LOGOUT:
                    creatLogoutAsk();
                    break;
            }
        }
    }

    //是否注销
    public void creatLogoutAsk(){
        final Context context = ControlCenter.getInstance().getmContext();
        isShowDialog = new AlertDialog.Builder(context).create();
        isShowDialog.setTitle("提示");
        isShowDialog.setMessage("你确定要注销本账号吗？");
        isShowDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ControlCenter.getInstance().logout();
            }
        });
        isShowDialog.setButton(AlertDialog.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                isShowDialog.dismiss();
            }
        });
        isShowDialog.show();
    }

    /**
     * Show float.
     */
    public void showFloat() {
        if (mFloatMenu != null)
            mFloatMenu.show();
    }

    /**
     * Hide float.
     */
    public void hideFloat() {
        if (mFloatMenu != null) {
            mFloatMenu.hide();
        }
    }

    /**
     * Normalize float
     */
    public void normalize(){
        if (mFloatMenu != null){
            mFloatMenu.normalize();
        }
    }

    /**
     * Destroy float.
     */
    public void destroyFloat() {
        hideFloat();
        if (mFloatMenu != null) {
            mFloatMenu.destroy();
        }
        mFloatMenu = null;
    }

    /**
     * On destroy.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyFloat();
    }


    public class MenuServiceBinder extends Binder {
        public FloatMenuService getService() {
            return FloatMenuService.this;
        }
    }
}
