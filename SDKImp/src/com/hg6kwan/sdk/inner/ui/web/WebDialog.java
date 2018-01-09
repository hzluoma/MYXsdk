package com.hg6kwan.sdk.inner.ui.web;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.SPUtil;

import java.io.File;

/**
 * Created by xiaoer on 2016/7/15.
 * 悬浮窗口--子页面
 */
public  class WebDialog extends BaseDialog   implements View.OnClickListener{
    private LinearLayout im_back;
    private LinearLayout im_close;
    protected WebView mWebView;

    private BaseInfo baseInfo;
    private String mTitle;
    private String mService;
    private final String HTTP_FLOAT_DIALOG = "http://mp.gzjykj.com/sdk_turn_url.php?type=";

    //以后扩展
//    public static final String FD_UPDATE_PWD = "update_pwd";
//    public static final String FD_MOBILE = "mobile";
//    public static final String FD_VOUCHERS = "vouchers";
//    public static final String FD_MY_GIFT = "my_gift";
//    public static final String FD_NOTICE = "notice";

    public WebDialog(Context context, ControlUI.WEB_TYPE type) {
        super(TYPE.FLOAT_WEB,context);
        mContext = context;
        switch (type){
            case USER:
                mTitle = "用户中心";
                mService = "user";
                break;
            case NEWS:
                mTitle = "公告";
                mService = "news";
                break;
            case SERVICE:
                mTitle = "客服中心";
                mService = "services";
                break;
            case GAME:
                mTitle = "游戏";
                mService = "games";
                break;
            case GIFT:
                mTitle = "礼包";
                mService = "gifts";
                break;
            case STRATEGY:
                mTitle = "攻略";
                mService = "strategy";
                break;
            case USER_AGREEMENT:
                mTitle = "用户协议";
                mService = "userAgreement";
                break;
            case ID_VERIFICATION:
                mTitle = "实名认证";
                mService = "trueName";
                break;
        }
        baseInfo= ControlCenter.getInstance().getBaseInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createUI(mContext), new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            im_back.setOnClickListener(this);
            im_close.setOnClickListener(this);

        initWebView(mWebView);
        webPost(mService);
        this.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if (v == im_close){
            ControlUI.getInstance().closeSDKUI();
        }else if (v == im_back){
            if(mWebView.canGoBack())
                    mWebView.goBack();
        }
    }

    private LinearLayout createUI(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout ly_top = uiUtils.createLayout(uiUtils.LAYOUT.WEB_TITLE,context);
        ly_top.setOrientation(LinearLayout.HORIZONTAL);
        ly_top.setGravity(Gravity.CENTER);
        LinearLayout ly_bottom = uiUtils.createLayout(uiUtils.LAYOUT.WEB_BOTTOM,context);

        im_back = createImageLayout("back_web",5f,context);
        im_close = createImageLayout("close_web",5f,context);

        TextView tv_title = new TextView(context);
        tv_title.setText(mTitle);
        tv_title.setGravity(Gravity.CENTER);
        tv_title.setTextColor(Color.WHITE);
        tv_title.setTextSize(ajustFontSize(10));

            ly_top.addView(im_back, getLayoutParamH(1));
            ly_top.addView(tv_title, getLayoutParamH(8));
            ly_top.addView(im_close, getLayoutParamH(1));

        LinearLayout ly_web = new LinearLayout(context);
        ly_web.setBackgroundColor(0xf0ffffff);
        mWebView = new WebView(context);
        ly_web.addView(mWebView,new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        layout.addView(ly_top,getLayoutParamV(1));
        int web_weight = uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? 10 : 15;
        layout.addView(ly_web,getLayoutParamV(web_weight));
        layout.addView(ly_bottom,getLayoutParamV(0.2f));

        return layout;
    }

    public class JSBridge {
        @JavascriptInterface
        public void jumpPage(String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void toast(String msg) {
            LogUtil.d(Constants.tag,"调用到了toast");
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void changePsd(String isOk, String msg) {//0:失败，1：成功
            int flag = Integer.valueOf(isOk);
            if (flag == 1) {
                baseInfo.login.setP(msg);
                CommonFunctionUtils.setLoginListToSharePreferences(mContext, baseInfo.loginList);
                Toast.makeText(mContext, "密码修改成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        }

        @JavascriptInterface
        public void changeNick(String nickname) {
            baseInfo.nickName = nickname;
        }

        //下载apk
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @JavascriptInterface
        public void downLoadApk(final String apk) {
            String relativelyPath = System.getProperty("user.dir");
            final String apkName = apk.substring(apk.lastIndexOf(relativelyPath) + 1);

            if (uiState.downloadList.size() > 0 && uiState.downloadList.values().contains
                    (apkName)) {
                Toast.makeText(mContext, "apk已在下载中", Toast.LENGTH_SHORT).show();
                return;
            }

            File apkFile = new File(Environment.getExternalStorageDirectory()
                    + "/download/" + apkName);
            if (apkFile.exists()) {
                CommonFunctionUtils.install(apkFile, mContext);
                return;
            }
            //提示是否下载
            new AlertDialog.Builder(mContext).setMessage("您确定要下载此游戏吗？")
                    .setCancelable(false)
                    .setPositiveButton("确定", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LogUtil.d("download", "开始下载");
                            //下载
                            DownloadManager downloadManager = (DownloadManager) mContext
                                    .getSystemService(Context.DOWNLOAD_SERVICE);
                            DownloadManager.Request request = new DownloadManager.Request(Uri
                                    .parse(apk));
                            request.setMimeType("application/vnd.android.package-archive");
                            request.setDestinationInExternalPublicDir(Environment
                                    .DIRECTORY_DOWNLOADS, apkName);
                            long down_reference = downloadManager.enqueue(request);
                            //加入记录
                            uiState.downloadList.put(down_reference, apkName);
                        }
                    }).setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create().show();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @JavascriptInterface
        public void clipGiftCode(String code) {
            ClipboardManager myClipboard = (ClipboardManager) mContext.getSystemService(Context
                    .CLIPBOARD_SERVICE);
            myClipboard.setText(code);
            Toast.makeText(mContext, "复制成功，请到游戏中使用！", Toast.LENGTH_SHORT).show();
        }

        /**
         * 关闭实名验证的JS接口,因为是实名验证的窗口一旦显示后,除非完成实名认证除非不可关闭
         */
        @JavascriptInterface
        public void closeTrueName(int adult) {
            //记录已经实名认证
            SPUtil spUtil = new SPUtil(mContext, "trueNameList");
            spUtil.putString(baseInfo.login.getU(), "1");
            //设置baseinfo里面的loginResult的值,不然充值会要求实名验证
            baseInfo.loginResult.setTrueName(true);
            Boolean isAdult = adult == 0 ? false : true;
            baseInfo.loginResult.setAdult(isAdult);
            //回调接口,通知外面已经完成实名认证
            ControlCenter.getInstance().onIDVerification();
            ControlCenter.getInstance().runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    ControlUI.getInstance().closeSDKUI();
                }
            });
        }
    }


    protected void webPost(String type) {
        String username = baseInfo.login.getU();
        String session = baseInfo.gSessionId;
        String appId = baseInfo.gAppId;
        final String url = HTTP_FLOAT_DIALOG +type;
        final String params = "&username=" + username + "&sid=" + session+"&appid="+appId
                + "&sign=" + CommonFunctionUtils.getSignFloat(type, username, session);
        //异步加载
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mWebView.postUrl(url, params.getBytes());
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void initWebView(final WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(0x00000000);
        webView.requestFocus();
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(new JSBridge(),"qiqu_float");
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String s1, String s2, String s3, long l) {
                LogUtil.d("download","开始下载");
                try {
                    //支持插件下载
                    Uri localUri = Uri.parse(url);
                    LogUtil.e("NotSupportURL",localUri.toString());
                    Intent localIntent = new Intent(Intent.ACTION_VIEW, localUri);
                    localIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    webView.getContext().startActivity(localIntent);
                }catch (Exception e){
                    LogUtil.e("DownLoadEorror",e.toString());
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                } else {
                    //
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        //intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                        webView.getContext().startActivity(intent);
                    } catch (Exception e) {
                        LogUtil.e("LoadUrlError", e.toString());
                    }
                }
                //
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //支持ssl
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        //设置内置client
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message,JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }
        });

    }
}
