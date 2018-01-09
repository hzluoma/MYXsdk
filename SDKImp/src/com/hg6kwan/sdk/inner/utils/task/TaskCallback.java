package com.hg6kwan.sdk.inner.utils.task;

/**
 * Created by Administrator on 2017/1/5.
 */

import com.hg6kwan.sdk.inner.net.HttpResultData;

/**
 * AsyncTask的封装接口
 */
public interface TaskCallback {

    void onPreTask();                       //执行前
    HttpResultData onBackGroudTask();               //子线程执行
    void OnUpdateProgress(int progress);    //进度
    void OnPostTask(HttpResultData result);         //最后运行在主线程

}
