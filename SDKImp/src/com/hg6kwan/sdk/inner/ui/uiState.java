package com.hg6kwan.sdk.inner.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hg6kwan.sdk.inner.log.LogUtil;

import java.util.HashMap;

/**
 * Created by xiaoer on 2016/9/22.
 */
public class uiState {

    public static int screenOrientation = -1;
    public static int screenWidth;
    public static int screenHeight;
    public static float screenDensity;
    public static float screenDensityDpi;

    public static boolean gIsPay = false;
    public static boolean gIsRegsiter = false;
    public static HashMap<Long,String> downloadList = new HashMap<>();

    public static String resetPhone = "";

    public static long timeAuth = 0;


    private static HashMap<String,Bitmap> mResMap;   //在初始化的时候从assets目录下读取到的图片的集合

    private String x;


    public static HashMap<String, Bitmap> getResMap() {
        return mResMap;
    }

    public static void setResMap(HashMap<String, Bitmap> resMap) {
        mResMap = resMap;
    }



    public static void clearFlag(){
        gIsPay = false;
        gIsRegsiter = false;
    }

    /**
     * 获取屏幕方向，并做适配
     */
    public static void adapterScreen(Context context){

        //获取context的方向,保存屏幕长宽
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        uiState.screenOrientation = wm.getDefaultDisplay().getWidth() > wm.getDefaultDisplay().getHeight()?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        uiState.screenWidth = wm.getDefaultDisplay().getWidth();
        uiState.screenHeight = wm.getDefaultDisplay().getHeight();

        DisplayMetrics metric = context.getApplicationContext().getResources().getDisplayMetrics();
        uiState.screenDensity = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        screenDensityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）

    }

    public static boolean isScreenPortrait(){
        return uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

}
