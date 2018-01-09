package com.hg6kwan.sdk.inner.utils;

import android.os.Handler;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.utils.task.HttpTask;

import java.util.HashMap;

/**
 * 用于账号的一个工具类
 * Created by Roman on 2017/7/19.
 */

public class UserUtil {

    private static Handler handler = new Handler();
    private static int mDelayTime = 60000;
    private static int mTime = 0;
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
                handler.postDelayed(runnable, mDelayTime);
               checkAcc();
        }
    };

    public static void checkAccount() {
        handler.postDelayed(runnable, 1000);
    }

    public static void stopCheckAcc() {
        handler.removeCallbacks(runnable);
    }

    private static void checkAcc() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
                String acc = baseInfo.login.getU();
                String pwd = baseInfo.login.getP();

                final HttpTask httpTask = new HttpTask();
                final HashMap<String, Object> requestMap = new HashMap<>();

                requestMap.put("username", acc);
                requestMap.put("passwd", MD5.getMD5String(pwd));
                final HttpResultData[] result = new HttpResultData[1];

                 result[0] = httpTask.postData(Constants.SERVICE_CHECK_ACCOUNT,
                        requestMap);
                int what = 0;
                what =  result[0].state.getInteger("code");
                //根据服务器返回的检测账号密码的结果,0即不正确()
                if (what == 0) {
                    ControlCenter.getInstance().runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ControlCenter.getInstance().getmContext(), result[0].state.getString("msg")
                                    , Toast.LENGTH_LONG).show();
                        }
                    });
                    //停止轮询
                    stopCheckAcc();
                    ControlCenter.getInstance().logout();
                }
            }
        }).start();

    }


}
