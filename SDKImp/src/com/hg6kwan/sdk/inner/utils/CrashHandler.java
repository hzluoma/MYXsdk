package com.hg6kwan.sdk.inner.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;

import com.hg6kwan.sdk.inner.log.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Response;

import static com.alipay.sdk.app.statistic.c.s;


/**
 * Created by Roman on 2017/3/23.
 * 用来捕获引起程序crash的异常
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    //文件夹目录
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/crash_log/";
    //文件名
    private static final String FILE_NAME = "crash";
    //文件名后缀
    private static final String FILE_NAME_SUFFIX = ".trace";
    //上下文
    private Context mContext;

    //单例模式
    private static CrashHandler sInstance = new CrashHandler();
    private long mCurrent;
    private String mTime;
    private PackageManager mPm;
    private PackageInfo mPi;

    private CrashHandler() {}
    public static CrashHandler getInstance() {
        return sInstance;
    }

    /**
     * 初始化方法
     *
     * @param context
     */
    public void init(Context context) {
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        //获取Context，方便内部使用
        mContext = context.getApplicationContext();
    }

    /**
     * 捕获异常回掉
     *
     * @param thread 当前线程
     * @param ex     异常信息
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //导出异常信息到SD卡
        dumpExceptionToSDCard(ex);
        //上传异常信息到服务器
        uploadExceptionToServer(ex);

        //延时2秒杀死进程
//        SystemClock.sleep(2000);
//        Process.killProcess(Process.myPid());
    }

    /**
     * 导出异常信息到SD卡
     *
     * @param ex
     */
    private void dumpExceptionToSDCard(Throwable ex) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //创建文件夹
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取当前时间
        mCurrent = System.currentTimeMillis();
        mTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mCurrent));
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + mTime + FILE_NAME_SUFFIX);
        try {
            //输出流操作
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出手机信息和异常信息
            mPm = mContext.getPackageManager();
            mPi = mPm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (mPm == null) {
                return;
            }
            pw.println("发生异常时间：" + mTime);
            pw.println("应用版本：" + mPi.versionName);
            pw.println("应用版本号：" + mPi.versionCode);
            pw.println("android版本号：" + Build.VERSION.RELEASE);
            pw.println("android版本号API：" + Build.VERSION.SDK_INT);
            pw.println("手机制造商:" + Build.MANUFACTURER);
            pw.println("手机型号：" + Build.MODEL);
            ex.printStackTrace(pw);
            //关闭输出流
            pw.close();
        } catch (Exception e) {

        }
    }

    /**
     * 上传异常信息到服务器
     *
     * @param ex
     */
    private void uploadExceptionToServer(Throwable ex) {
        Error error = new Error(ex.getMessage());

        StackTraceElement[] stackTrace;
        Throwable cause;
        StringBuilder causeSB = new StringBuilder("");
        StringBuilder stackTraceSB = new StringBuilder("");
        while (true) {
            cause = ex.getCause();

            if (cause != null) {
                causeSB = causeSB.append("捕获的cause : " + cause.toString() + "\r\n");
                stackTrace = cause.getStackTrace();
                for (int i = 0; i < stackTrace.length; i++) {
                    stackTraceSB = stackTraceSB.append("捕获的StackTrace" + i + " : " +
                            stackTrace[i] +
                            "\r\n");
                }
                ex = cause;
            } else {
                break;
            }
        }

        try {

            String versionName = mPi.versionName == null ? "unKnownVersionName" : mPi.versionName;
            int versionCode = mPi.versionCode;

            OkHttpUtils.post().url(Constants.CRASH_UPDATE_URL)
                    .addParams("crashTime", "发生异常时间" + mTime)
                    .addParams("versionName","包名 : " + mContext.getPackageName() + "应用版本：" +
                            versionName)
                    .addParams("versionCode","应用版本号：" + mPi.versionCode)
                    .addParams("androidVersion","android版本号：" + Build.VERSION.RELEASE)
                    .addParams("androidVersionAPI","android版本号API：" + Build.VERSION.SDK_INT)
                    .addParams("manufacturer","手机制造商:" + Build.MANUFACTURER)
                    .addParams("model","手机型号：" + Build.MODEL)
                    .addParams("errorMessage","全部Cause : " + causeSB.toString() +
                            "全部StackTrace : " + stackTraceSB.toString())
                    .build()
                    .execute(new Callback() {
                        @Override
                        public Object parseNetworkResponse(Response response, int i) throws
                                Exception {
                            return null;
                        }

                        @Override
                        public void onError(Call call, Exception e, int i) {

                        }

                        @Override
                        public void onResponse(Object o, int i) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
