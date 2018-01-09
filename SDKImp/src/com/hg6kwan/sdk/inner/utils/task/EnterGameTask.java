package com.hg6kwan.sdk.inner.utils.task;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hg6kwan.sdk.inner.base.EnterGameBean;
import com.hg6kwan.sdk.inner.base.ReturnCode;
import com.hg6kwan.sdk.inner.net.HttpResultData;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.service.EnterGameService;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.Activity.PayActivity;

import java.util.Map;

/**
 * Created by Administrator on 2017/1/5.
 */

public class EnterGameTask {

    private Context mContext;


    public EnterGameTask(Context context) {
        mContext = context;
    }

    public void postUserInfo(final EnterGameBean enterGameBean) {

        TaskUtils task = new TaskUtils(new TaskCallback() {
            @Override
            public void onPreTask() {
            }

            @Override
            public HttpResultData onBackGroudTask() {
                HttpResultData resultData = new EnterGameService().enterGame(enterGameBean);
                return resultData;
            }

            @Override
            public void OnUpdateProgress(int progress) {

            }

            @Override
            public void OnPostTask(HttpResultData res) {

                int what = 0;
                try {

                    what = res.state.getInteger("code");

                    ControlCenter.getInstance().onEnterGameResult();

                } catch (Exception e) {
                    e.printStackTrace();
                    ControlCenter.getInstance().onResult(ReturnCode
                            .COM_ENTERGAME_FAIL, "上传游戏角色信息时出错");
                }
            }
        });
        task.execute();
    }

}
