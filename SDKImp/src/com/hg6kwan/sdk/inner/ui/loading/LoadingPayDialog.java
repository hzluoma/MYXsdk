package com.hg6kwan.sdk.inner.ui.loading;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2017/1/7.
 */

public class LoadingPayDialog extends ProgressDialog{


    private static LoadingPayDialog mLoadingPayDialog;

    private LoadingPayDialog(Context context) {
        super(context);
    }

    public static void init(Context context) {

            mLoadingPayDialog = new LoadingPayDialog(context);
            mLoadingPayDialog.setMessage("正在运行支付操作");
            mLoadingPayDialog.setProgressStyle(STYLE_SPINNER);
            mLoadingPayDialog.setIndeterminate(true);

    }

    public static LoadingPayDialog getInstance() {
            return mLoadingPayDialog;
    }
}
