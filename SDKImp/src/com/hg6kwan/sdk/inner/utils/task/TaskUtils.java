package com.hg6kwan.sdk.inner.utils.task;

import android.os.AsyncTask;

import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.utils.task.TaskCallback;

/**
 * Created by Administrator on 2017/1/5.
 * 对AsyncTask的封装
 */

public class TaskUtils extends AsyncTask<Void, Integer, HttpResultData> {

    private static TaskCallback callback;

    public TaskUtils(TaskCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.onPreTask();
        super.onPreExecute();
    }

    @Override
    protected HttpResultData doInBackground(Void... params) {
        HttpResultData resultData = callback.onBackGroudTask();
        return resultData;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        callback.OnUpdateProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(HttpResultData res) {
        callback.OnPostTask(res);
        super.onPostExecute(res);
    }
}
