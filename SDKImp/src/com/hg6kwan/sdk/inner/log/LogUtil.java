package com.hg6kwan.sdk.inner.log;

import android.util.Log;

import com.hg6kwan.sdk.inner.utils.Constants;

/**
 * Created by Administrator on 2016-11-18.
 */

public class LogUtil {

    public static void i(String TAG, String message) {

        if (Constants.DEBUG) {

            Log.i(TAG, message);

        }

    }

    public static void d(String TAG, String message) {

        if (Constants.DEBUG) {

            Log.d(TAG, message);

        }

    }

    public static void v(String TAG, String message) {

        if (Constants.DEBUG) {

            Log.v(TAG, message);

        }

    }

    public static void e(String TAG, String message) {

        if (Constants.DEBUG) {

            Log.e(TAG, message);

        }

    }

    public static void w(String TAG, String message) {

        if (Constants.DEBUG) {

            Log.w(TAG, message);

        }

    }

}

