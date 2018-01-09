package com.hg6kwan.sdk.inner.service;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;

import java.io.File;

/**
 * Created by bt on 2016/8/1.
 */
public class InstallApkReceiver extends BroadcastReceiver {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onReceive(Context context, Intent intent) {
        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
        if(!uiState.downloadList.keySet().contains(reference)){
            return;
        }
        CommonFunctionUtils.install(new File(Environment.getExternalStorageDirectory()
                +"/download/"+ uiState.downloadList.get(reference)) ,context );
        uiState.downloadList.remove(reference);
    }
}
