package com.hg6kwan.sdk.inner.ui.web;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
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

import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.service.PayService;
import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Constants;

/**
 * Created by xiaoer on 2016/9/29.
 */

public class PayDialog extends BaseDialog implements View.OnClickListener {

    private int payCode = Constants.PAY_CANCEL;
    private PayService mPayService = new PayService();
    private boolean isExit;

    private LinearLayout im_back;
    private LinearLayout im_close;
    protected WebView mWebView;

    private String mTitle = "6kw支付";

    public PayDialog(Context context) {
        super(TYPE.FLOAT_WEB,context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createUI(mContext), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        im_back.setOnClickListener(this);
        im_close.setOnClickListener(this);

        initWebView(mWebView);
        mPayService.pay(mWebView);

        this.setCancelable(false);
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

        ly_top.addView(im_back,getLayoutParamH(1));
        ly_top.addView(tv_title,getLayoutParamH(8));
        ly_top.addView(im_close,getLayoutParamH(1));

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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initWebView(final WebView webView) {
        webView.setBackgroundColor(0x00000000);
        webView.requestFocus();
        webView.setVerticalScrollBarEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.addJavascriptInterface(new PayDialog.JSBridge(),"qiqu_pay");

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String s1, String s2, String s3, long l) {
                try {
                    //支持插件下载
                    Uri localUri = Uri.parse(url);
                    Intent localIntent = new Intent(Intent.ACTION_VIEW, localUri);
                    localIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    webView.getContext().startActivity(localIntent);
                }catch (Exception e){
                    LogUtil.e("DownLoadEorror",e.toString());
                }
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //LogUtil.d("url","url="+url);
                if (url.startsWith("http:")|| url.startsWith("https:")){
                    view.loadUrl(url);
                }else{
                    //
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                        webView.getContext().startActivity(intent);
                    }catch (Exception e){
                        LogUtil.e("LoadUrlError",e.toString());
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
            public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        //设置内置client
        WebChromeClient client = new WebChromeClient(){
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
        };
        //
        webView.setWebChromeClient(client);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View view) {
        if(view == im_close){
            if(isExit)
                return;
            isExit=true;
            ControlUI.getInstance().exitPay(payCode);
        }else if (view == im_back){
            if(mWebView.canGoBack())
                mWebView.goBack();
        }
    }

    //提供给html页面调用
    public class JSBridge{
        @JavascriptInterface
        public void setPayFlag(String flag){
            payCode=Integer.parseInt(flag);
        }
        @JavascriptInterface
        public void exitPay(String flag){
            payCode=Integer.parseInt(flag);
            ControlUI.getInstance().exitPay(payCode);
        }
    }

}
