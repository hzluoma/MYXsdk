package com.hg6kwan.sdk.inner.ui.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.task.AliPayTask;
import com.hg6kwan.sdk.inner.utils.task.TPPayTask;
import com.hg6kwan.sdk.inner.utils.task.WechatPayTask;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016-12-17.
 * 竖屏支付界面
 */

public class MainPortraitUI implements View.OnClickListener {
    private Context mContext;

    @android.support.annotation.IdRes
    int id_ll_mainLayout = 1;       //竖屏根布局的ID

    @android.support.annotation.IdRes
    int tv_account_porcn = 2;       //支付账号

    @android.support.annotation.IdRes
    int tv_account_por = 3;      // 支付金额

    @android.support.annotation.IdRes
    int tv_merchandise_porcn = 4;  // 购买商品

    @android.support.annotation.IdRes
    int tv_yuanbao_por = 5;  // 购买元宝

    @android.support.annotation.IdRes
    int tv_price_porcn = 6;  // 充值

    @android.support.annotation.IdRes
    int tv_recharge_por = 7;  // 充值金额

    @android.support.annotation.IdRes
    int main_left_switcher_container = 8;  // 充值方式布局

    @android.support.annotation.IdRes
    int iv_payIcon = 11;  // 代金券

    @android.support.annotation.IdRes
    int id_wechat = 12;  // 代金券


    @android.support.annotation.IdRes
    int id_rl_titleBar = 13;       //titleBar的ID
    @android.support.annotation.IdRes
    int id_tv_Back = 14;           //左侧返回按钮
    @android.support.annotation.IdRes
    int id_iv_Close = 15;          //右侧叉叉图案

    private RelativeLayout rl_wxPay;
    private RelativeLayout mRl_wxPay1;
    // 支付方式
    private int payMode = 0;
    private int mCount;
    private LinearLayout mLinearTabHost;

    private ImageView mIv_wxPay;

    private int mPayTag;   //目前选中的支付类型 0 微信支付 1 支付宝支付

    private int mWeightSize = 5; // 动态设置权重

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    /**
     * 目标Activity的Class
     */
    private Class<?> localClass;

    /**
     * 目标Activity的实例
     */
    private Object localInstance;

    private final PayActivity mPayActivity;
    private RelativeLayout mWeChatPayLayout;
    private RelativeLayout mAilPayLayout;
    private RelativeLayout mPlatformPayLayout;
    private RelativeLayout mTicketPayLayout;
    private AlertDialog mPayDiaLog;

    public MainPortraitUI(String userName, String price, String cpOrder, ArrayList<String>
            payChannelList, PayActivity payActivity) {
        mUserName = userName;
        mPrice = price;
        mGoodsName = cpOrder;
        mPayChannelList = payChannelList;
        mPayActivity = payActivity;
    }

    public AlertDialog getPayDiaLog() {
        if (mPayDiaLog !=null && mPayDiaLog.isShowing()) {
            return mPayDiaLog;
        }

        return null;
    }

    private  String mUserName;                //传入进来的角色名字或账号
    private  String mPrice;         //需要支付的金额
    private  String mGoodsName;                //商品描述
    private ArrayList<String> mPayChannelList;         //包含支付渠道顺序的集合

    private OnPayclickListener OnPayclickListener;//确定按钮被点击了的监听器

    private OnBackViewOnClockListener onBackViewClockLostener; // 返回监听器


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public View getPortrait(Context context) {
        this.mContext = context;

        // 创建竖屏的根布局
        LinearLayout linearRoot = new LinearLayout(mContext);
        // 设置宽高
        linearRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置垂直方式
        linearRoot.setOrientation(LinearLayout.VERTICAL);

        // 购买商品详情
        RelativeLayout layoutData = new RelativeLayout(mContext);
        layoutData.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, 0, 2.5f));
        layoutData.setGravity(Gravity.CENTER_VERTICAL);
        layoutData.setBackgroundColor(Color.WHITE);


        // payNum 支付账号
        TextView payNum = new TextView(mContext);
        payNum.setText("购买商品: ");
        payNum.setTextColor(0xff747474);
        payNum.setId(tv_account_porcn);
        payNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        // 属性
        RelativeLayout.LayoutParams paramsNum = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsNum.setMargins(uiUtils.getDipSize(20), uiUtils.getDipSize(17), 0, 0);
        payNum.setLayoutParams(paramsNum);

        // payMoney 支付金额
        TextView payMoney = new TextView(mContext);
        payMoney.setText(mGoodsName);
        payMoney.setTextColor(0xff737373);
        payMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        payMoney.setSingleLine(true);
        payMoney.setEllipsize(TextUtils.TruncateAt.END);
        // 支付金额ID
        payMoney.setId(tv_account_por);
        // 支付金额属性
        RelativeLayout.LayoutParams paramspayNum = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        // 设置在支付账号的右边
        paramspayNum.addRule(RelativeLayout.RIGHT_OF, payNum.getId());
        paramspayNum.setMargins(0, uiUtils.getDipSize(17), 0, 0);
        payMoney.setLayoutParams(paramspayNum);
        // 购买商品
        TextView buygoods = new TextView(mContext);
        buygoods.setText("支付账户: ");
        buygoods.setTextColor(0xff737373);
        buygoods.setId(tv_merchandise_porcn);
        // 购买商品属性
        RelativeLayout.LayoutParams paramsBuygoods = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams
                        .WRAP_CONTENT);
        paramsBuygoods.setMargins(uiUtils.getDipSize(20), uiUtils.getDipSize(10), 0, 0);
        // 设置在支付账号的下面
        paramsBuygoods.addRule(RelativeLayout.ABOVE, payNum.getId());
        // 字体大小
        buygoods.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        buygoods.setLayoutParams(paramsBuygoods);
        // 购买商品信息
        TextView bcommodity = new TextView(mContext);
        // ID
        bcommodity.setId(tv_yuanbao_por);
        bcommodity.setText(mUserName);
        bcommodity.setTextColor(0xff737373);
        bcommodity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        bcommodity.setTextColor(Color.RED);
        // 购买商品属性
        RelativeLayout.LayoutParams paramsBcommodity = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams
                        .WRAP_CONTENT);
        paramsBcommodity.addRule(RelativeLayout.RIGHT_OF, buygoods.getId());
        paramsBcommodity.addRule(RelativeLayout.ABOVE, payMoney.getId());
        paramsBcommodity.setMargins(0, uiUtils.getDipSize(10), 0, 0);
        bcommodity.setLayoutParams(paramsBcommodity);
        // 充值
        TextView paySum = new TextView(mContext);
        // ID
        paySum.setId(tv_price_porcn);
        paySum.setText("充值金额: ");
        paySum.setTextColor(0xff737373);
        paySum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        // 属性
        RelativeLayout.LayoutParams paramsPaySum = new RelativeLayout.LayoutParams(RelativeLayout
                .LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsPaySum.setMargins(uiUtils.getDipSize(20), uiUtils.getDipSize(17), 0, 0);
        paramsPaySum.addRule(RelativeLayout.BELOW, payNum.getId());
        paySum.setLayoutParams(paramsPaySum);
        // 充值金额
        TextView paySumMoney = new TextView(mContext);
        // ID
        paySumMoney.setId(tv_recharge_por);
        paySumMoney.setText(mPrice + "元");
        paySumMoney.setTextColor(0xff737373);
        paySumMoney.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        paySumMoney.setTextColor(Color.RED);
        // 属性
        RelativeLayout.LayoutParams paramsPaySumMoney = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams
                        .WRAP_CONTENT);
        paramsPaySumMoney.setMargins(0, uiUtils.getDipSize(17), 0, 0);
        paramsPaySumMoney.addRule(RelativeLayout.RIGHT_OF, paySum.getId());
        paramsPaySumMoney.addRule(RelativeLayout.BELOW, payNum.getId());
        paySumMoney.setLayoutParams(paramsPaySumMoney);
        // 添加TextView
        layoutData.addView(payNum);
        layoutData.addView(payMoney);
        layoutData.addView(buygoods);
        layoutData.addView(bcommodity);
        layoutData.addView(paySum);
        layoutData.addView(paySumMoney);

        // 请选择充值方式
        RelativeLayout layoutMode = new RelativeLayout(mContext);
        layoutMode.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                .MATCH_PARENT, 0, 0.5f));
        layoutMode.setBackgroundColor(0xffF5F5F5);
        // 请选择充值方式TextView

        TextView textMode = new TextView(mContext);
        textMode.setTextColor(0xff717171);
        textMode.setText("请选择充值方式");
        // 属性
        RelativeLayout.LayoutParams paramsPayMode = new RelativeLayout.LayoutParams
                (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams
                        .WRAP_CONTENT);
        paramsPayMode.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsPayMode.setMargins(uiUtils.getDipSize(20), 0, 0, 0);
        textMode.setLayoutParams(paramsPayMode);
        // 添加View
        layoutMode.addView(textMode);

        // 充值选项卡根布局
        FrameLayout payTabHost = new FrameLayout(mContext);
        LinearLayout.LayoutParams paramsTabHost = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, 0, 5f);
        payTabHost.setLayoutParams(paramsTabHost);
        // 充值选项卡父布局
        mLinearTabHost = new LinearLayout(mContext);
        LinearLayout.LayoutParams tabHostParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams
                .MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tabHostParams.weight = 5;
        mLinearTabHost.setLayoutParams(tabHostParams);
        mLinearTabHost.setOrientation(LinearLayout.VERTICAL);
        // ID
        mLinearTabHost.setId(main_left_switcher_container);

        // 微信支付
        mWeChatPayLayout = createItemContainer(mContext, PAYTYPE.WECHAT);
        // 支付宝
        mAilPayLayout = createItemContainer(mContext, PAYTYPE.ALI);
        // 京东
        //RelativeLayout jDPayLayout = createItemContainer(mContext, PAYTYPE.JD);
        // 平台币支付
        mPlatformPayLayout = createItemContainer(mContext, PAYTYPE.PLATFORM);
        //代金券
        mTicketPayLayout = createItemContainer(mContext, PAYTYPE.TICKET);


        // 点击事件
        mWeChatPayLayout.setOnClickListener(this);
        mAilPayLayout.setOnClickListener(this);
        //jDPayLayout.setOnClickListener(this);
        mPlatformPayLayout.setOnClickListener(this);
        mTicketPayLayout.setOnClickListener(this);

        // 这是根据服务端传递过来的数据进行显示支付渠道
        for (String payChannel : mPayChannelList) {
            if (payChannel.equals("10")) {
                mLinearTabHost.addView(mWeChatPayLayout);
                continue;
            } else if (payChannel.equals("3")) {
                mLinearTabHost.addView(mAilPayLayout);
                continue;
            } else if (payChannel.equals("6")) {
                mLinearTabHost.addView(mPlatformPayLayout);

                continue;
            } else if (payChannel.equals("4")) {
                mLinearTabHost.addView(mTicketPayLayout);

            }
        }

        // 空白区域,为了适配,权重值需要动态设置
        LinearLayout frameLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, 0, (mWeightSize - mPayChannelList.size()));

        mLinearTabHost.addView(frameLayout, layoutParams);

        mCount = mLinearTabHost.getChildCount();
        //默认点击第一项
        onClick(mLinearTabHost.getChildAt(0));
        // 支付选项卡添加父View
        payTabHost.addView(mLinearTabHost);

        // 点击按钮父控件
        LinearLayout layoutButton = new LinearLayout(mContext);
        // 属性
        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, 0, 1.5f);

        // 设置子空间居中显示
        layoutButton.setGravity(Gravity.CENTER);
        // 设置属性
        layoutButton.setLayoutParams(paramsButton);
        layoutButton.setBackgroundColor(Color.WHITE);

        // 按钮shape
        GradientDrawable btn_shape = new GradientDrawable();
        btn_shape.setShape(GradientDrawable.RECTANGLE);
        btn_shape.setColor(0xFF6495ed);
        btn_shape.setStroke(uiUtils.getDipSize(1), 0xffc9c9c9);
        btn_shape.setCornerRadius(12.0f);


        // 确认充值按钮
        Button payButton = new Button(mContext);
        LinearLayout.LayoutParams rlp_btn_confirm = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, -2);
        payButton.setTextColor(0xFFFFFFFF);
        payButton.setText("确认支付");

        payButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        rlp_btn_confirm.setMargins(uiUtils.getDipSize(25), 0, uiUtils.getDipSize(25), 0);
        payButton.setPadding(0, uiUtils.getDipSize(16), 0, uiUtils.getDipSize(16));
        payButton.setLayoutParams(rlp_btn_confirm);
        payButton.setBackgroundDrawable(btn_shape);

        // 根据从服务端获取的渠道ID执行不同的支付方法
        payButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String channel = mPayChannelList.get(mPayTag);

                switch (channel) {
                    //当支付渠道ID是支付宝支付时
                    case Constants.PAYCHANNEL_ALI:

                        AliPayTask aliPayTask = new AliPayTask(mPayActivity);
                        aliPayTask.aliPay(Constants.PAYCHANNEL_ALI);

                        break;

                    //当支付渠道ID是微信支付时
                    case Constants.PAYCHANNEL_WECHAT:

                        WechatPayTask wechatPayTask = new WechatPayTask(mPayActivity);
                        wechatPayTask.payOnWechat(Constants.PAYCHANNEL_WECHAT);

                        break;

                    //当支付渠道ID是平台币时
                    case Constants.PAYCHANNEL_PLATFORM:
                        //启动平台币支付的任务栈
                        TPPayTask task1 = new TPPayTask(mPayActivity);
                        task1.payOnPlatform(Constants.PAYCHANNEL_PLATFORM);
                        break;

                    //当支付渠道是代金券时
                    case Constants.PAYCHANNEL_TICKET:
                        //启动代金券支付的任务栈
                        TPPayTask task2 = new TPPayTask(mPayActivity);
                        task2.payByTicket(Constants.PAYCHANNEL_TICKET);
                        //弹窗
                        showPayResult("");
                        break;
                }
            }

        });

        // 加入添加按钮
        layoutButton.addView(payButton);

        // 添加View
        linearRoot.addView(getTitleBar(mContext));
        linearRoot.addView(layoutData);
        linearRoot.addView(layoutMode);
        linearRoot.addView(payTabHost);
        linearRoot.addView(layoutButton);
        return linearRoot;

    }

    public static HashMap<String, Bitmap> loadAssetsImg(Context context) {
        AssetManager am = context.getAssets();
        try {
            String[] imgs = am.list("qiqu_img");
            if (imgs == null || imgs.length == 0)
                return null;
            HashMap<String, Bitmap> map = new HashMap<>(imgs.length);
            for (String imgName : imgs) {
                InputStream is = am.open("qiqu_img/" + imgName);
                Bitmap bp = BitmapFactory.decodeStream(is);
                is.close();

                map.put(imgName.substring(0, imgName.indexOf(".")), bp);
            }
            return map;
         } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        int indexOfChild = mLinearTabHost.indexOfChild(view);

        // 被点击的view
        mPayTag = indexOfChild;

        // 设置被选中的不能再点击
        for (int i = 0; i < mCount; i++) {

            View childView = mLinearTabHost.getChildAt(i);
            //被点中的设置为不能再被点击
            setEnabled(childView, indexOfChild != i);
        }
    }



    public enum PAYTYPE {
        WECHAT, ALI, JD, PLATFORM, TICKET;

    }
    public RelativeLayout createItemContainer(Context context, PAYTYPE paytype) {

        LinearLayout.LayoutParams lp_WxPay = new LinearLayout.LayoutParams(-1, 0,1f);
        mRl_wxPay1 = new RelativeLayout(context);
        mRl_wxPay1.setBackgroundColor(0xffF5F5F5);

        mRl_wxPay1.setLayoutParams(lp_WxPay);

        mIv_wxPay = new ImageView(mContext);

        RelativeLayout.LayoutParams rlp_iv_WxPay = new RelativeLayout.LayoutParams(uiUtils
                .getDipSize(128.5f), uiUtils.dp2px(28));
        rlp_iv_WxPay.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        rlp_iv_WxPay.setMargins(uiUtils.getDipSize(20), 0, 0, 0);

        mIv_wxPay.setLayoutParams(rlp_iv_WxPay);

        //被点击的区域的单位
        StateListDrawable Drawable = new StateListDrawable();
        switch (paytype) {

            case WECHAT:
                // 选择器
                Drawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_wechatpay")));

                Drawable.addState(new int[]{}, new BitmapDrawable(uiState
                        .getResMap().get("ll_wechatpay_enable")));
                break;
            case ALI:
                // 选择器
                Drawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_alipay")));

                Drawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_alipay_enable")));

                break;
            case PLATFORM:
                StateListDrawable platformDrawable = new StateListDrawable();
                Drawable.addState(new int[]{android.R.attr.state_enabled}, new
                        BitmapDrawable
                        (uiState.getResMap().get("ll_platform")));

                Drawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_platform_enable")));
                break;
            case TICKET:
                // 选择器
                Drawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_ticket")));

                Drawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_ticket_enable")));
                break;
        }
        // 设置选择器
        mIv_wxPay.setImageDrawable(Drawable);

        //微信支付条目区域下面的一条线
        View lineView = new View(mContext);
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(-1, uiUtils
                .getDipSize(2));
        viewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        lineView.setLayoutParams(viewParams);

        //线条颜色的选择器
        StateListDrawable viewBackground = new StateListDrawable();
        viewBackground.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable
                (0xfff5f5f5));
        viewBackground.addState(new int[]{}, new ColorDrawable(0xFF6495ed));

        lineView.setBackgroundDrawable(viewBackground);

        ImageView imageDot = new ImageView(mContext);
        RelativeLayout.LayoutParams paramsDot = new RelativeLayout.LayoutParams(uiUtils
                .getDipSize(18), uiUtils.getDipSize(18));
        paramsDot.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramsDot.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsDot.setMargins(0, 0, uiUtils.getDipSize(20), 0);

        //点的选择器
        StateListDrawable dotBackground = new StateListDrawable();

        dotBackground.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                (uiState.getResMap().get("dot")));
        dotBackground.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("dot_enable")));
        imageDot.setImageDrawable(dotBackground);
        imageDot.setLayoutParams(paramsDot);

        //加入ImageView
        mRl_wxPay1.addView(mIv_wxPay);
        // 加入小圆点
        mRl_wxPay1.addView(imageDot);
        //加入线条
        mRl_wxPay1.addView(lineView);

        return mRl_wxPay1;
    }


    private void setEnabled(View view, boolean enable) {

        if (view instanceof ViewGroup && enable) {
            view.setBackgroundColor(0xffFaFaFa);
        } else if (view instanceof ViewGroup) {
            view.setBackgroundColor(Color.WHITE);
        }

        view.setEnabled(enable);
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                setEnabled(child, enable);
            }
        }
    }


    public void affirmPayDiaLog() {

        //获取dialog的背景图
        BitmapDrawable dialog_BackGround = new BitmapDrawable(uiState.getResMap()
                .get("dialog_background"));

        // 已充值按钮shape
        GradientDrawable btn_shape_position = new GradientDrawable();
        btn_shape_position.setShape(GradientDrawable.RECTANGLE);
        btn_shape_position.setColor(0xff118EEA);
        btn_shape_position.setCornerRadius(12.0f);

        // 已充值按钮shape
        GradientDrawable btn_shape_continue = new GradientDrawable();
        btn_shape_continue.setShape(GradientDrawable.RECTANGLE);
        btn_shape_continue.setColor(0xffF1594B);
        btn_shape_continue.setCornerRadius(12.0f);

        //设置dialog为圆角
        GradientDrawable dialogShape = new GradientDrawable();
        dialogShape.setStroke(uiUtils.getDipSize(5),0xffbfbfbf);

        // DiaLog根布局
        LinearLayout diaLogLayout = new LinearLayout(mContext);
        diaLogLayout.setOrientation(LinearLayout.VERTICAL);
        diaLogLayout.setBackgroundDrawable(dialogShape);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        diaLogLayout.setBackgroundDrawable(dialog_BackGround);
        diaLogLayout.setBackgroundColor(0xffffffff);
        diaLogLayout.setWeightSum(3);
        diaLogLayout.setLayoutParams(dialogParams);


        // 关闭图标父容器
        RelativeLayout closeLayout = new RelativeLayout(mContext);
        LinearLayout.LayoutParams closeLayoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, 0, 2f);
        closeLayoutParams.setMargins(0, 0, 0, uiUtils.dp2px(30));
        closeLayout.setGravity(Gravity.RIGHT);

        // 提示文本容器
        RelativeLayout textLayout = new RelativeLayout(mContext);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, 0, 1f);
        textLayout.setGravity(Gravity.CENTER);
        textParams.setMargins(0, 0, 0, uiUtils.dp2px(30));

        // 是否已经完成充值
        TextView textView = new TextView(mContext);
        textView.setText("是否已完成充值?");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        textView.setTextColor(0xff5E5E5E);
        // 提示文本容器添加View
        textLayout.addView(textView);

        // 两个按钮容器
        LinearLayout buttonLayout = new LinearLayout(mContext);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        // 两个按钮容器属性
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, 0, 1f);
        buttonParams.setMargins(0, 0, 0, uiUtils.dp2px(40));
        buttonLayout.setLayoutParams(buttonParams);

        // 已充值按钮
//        Button buttonPosition = new Button(mContext);
//        buttonPosition.setWidth(uiUtils.getDipSize(150));
//        LinearLayout.LayoutParams positionParams = new LinearLayout.LayoutParams(-2, ViewGroup
//                .LayoutParams.WRAP_CONTENT);
//        buttonPosition.setText("已完成");
//        buttonPosition.setTextColor(Color.WHITE);
//        buttonPosition.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        Button buttonPosition = new Button(mContext);
        LinearLayout.LayoutParams positionParams = new LinearLayout.LayoutParams(0, ViewGroup
                .LayoutParams.WRAP_CONTENT, 1);
        positionParams.setMargins(uiUtils.getDipSize(20), 0, 0, 0);
        buttonPosition.setText("已充值");
        buttonPosition.setTextColor(Color.WHITE);
        buttonPosition.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        // 继续充值按钮
        Button exitButton = new Button(mContext);
        exitButton.setText("退出");
        exitButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        exitButton.setTextColor(Color.WHITE);
        exitButton.setBackgroundDrawable(btn_shape_continue);
        LinearLayout.LayoutParams continueParams = new LinearLayout.LayoutParams(0, ViewGroup
                .LayoutParams.WRAP_CONTENT, 1);
        continueParams.setMargins(uiUtils.getDipSize(24), 0, uiUtils.getDipSize(24), 0);

        // 设置shape
        buttonPosition.setBackgroundDrawable(btn_shape_position);

        // 按钮容器添加View
        buttonLayout.addView(buttonPosition, positionParams);
        buttonLayout.addView(exitButton, continueParams);

        // Dialog加入View
        diaLogLayout.addView(closeLayout,closeLayoutParams);
        diaLogLayout.addView(textLayout, textParams);
        diaLogLayout.addView(buttonLayout, buttonParams);


        // 是否已充值对话框
        mPayDiaLog = new AlertDialog.Builder(mContext,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return false;
                    }
                }).create();
        //设置窗口
        mPayDiaLog.setCustomTitle(diaLogLayout);
        mPayDiaLog.setCanceledOnTouchOutside(false);

        mPayDiaLog.show();


        // 已充值
        buttonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnPayclickListener != null) {
                    OnPayclickListener.onPayClick();
                }
            }
        });

        // 退出按钮
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnPayclickListener != null) {
                    OnPayclickListener.onPayClick();
                }
            }
        });

    }

    /**
     * 平台币和代金券的结果展示dialog
     */
    public void showPayResult(String text) {

        //获取dialog的背景图
        BitmapDrawable dialog_BackGround = new BitmapDrawable(uiState.getResMap()
                .get("dialog_background"));

        // 已充值按钮shape
        GradientDrawable btn_shape_position = new GradientDrawable();
        btn_shape_position.setShape(GradientDrawable.RECTANGLE);
        btn_shape_position.setColor(0xffF1594B);
        btn_shape_position.setCornerRadius(12.0f);


        //设置dialog为圆角
        GradientDrawable dialogShape = new GradientDrawable();
        dialogShape.setStroke(uiUtils.getDipSize(5),0xffbfbfbf);

        // DiaLog根布局
        LinearLayout diaLogLayout = new LinearLayout(mContext);
        diaLogLayout.setOrientation(LinearLayout.VERTICAL);
        diaLogLayout.setBackgroundDrawable(dialogShape);
        LinearLayout.LayoutParams dialogParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        diaLogLayout.setBackgroundDrawable(dialog_BackGround);
        diaLogLayout.setBackgroundColor(0xffffffff);
        diaLogLayout.setWeightSum(3);
        diaLogLayout.setLayoutParams(dialogParams);


        // 关闭图标父容器
        RelativeLayout closeLayout = new RelativeLayout(mContext);
        LinearLayout.LayoutParams closeLayoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, 0, 2f);
        closeLayoutParams.setMargins(0, 0, 0, uiUtils.dp2px(30));
        closeLayout.setGravity(Gravity.RIGHT);

        // 提示文本容器
        RelativeLayout textLayout = new RelativeLayout(mContext);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(RelativeLayout
                .LayoutParams.MATCH_PARENT, 0, 1f);
        textLayout.setGravity(Gravity.CENTER);
        textParams.setMargins(0, 0, 0, uiUtils.dp2px(30));

        // 是否已经完成充值
        TextView textView = new TextView(mContext);
        textView.setText("您的余额不足!");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        textView.setTextColor(0xff5E5E5E);
        // 提示文本容器添加View
        textLayout.addView(textView);

        // 两个按钮容器
        LinearLayout buttonLayout = new LinearLayout(mContext);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        // 两个按钮容器属性
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, 0, 1f);
        buttonParams.setMargins(0, 0, 0, uiUtils.dp2px(40));
        buttonLayout.setLayoutParams(buttonParams);

        Button buttonPosition = new Button(mContext);
        LinearLayout.LayoutParams positionParams = new LinearLayout.LayoutParams(0, ViewGroup
                .LayoutParams.WRAP_CONTENT, 1);
        positionParams.setMargins(uiUtils.getDipSize(50), 0, uiUtils.getDipSize(50), 0);
        buttonPosition.setText("退出");
        buttonPosition.setTextColor(Color.WHITE);
        buttonPosition.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        // 设置shape
        buttonPosition.setBackgroundDrawable(btn_shape_position);

        // 按钮容器添加View
        buttonLayout.addView(buttonPosition, positionParams);
//        buttonLayout.addView(exitButton, continueParams);

        // Dialog加入View
        diaLogLayout.addView(closeLayout,closeLayoutParams);
        diaLogLayout.addView(textLayout, textParams);
        diaLogLayout.addView(buttonLayout, buttonParams);


        // 是否已充值对话框
        mPayDiaLog = new AlertDialog.Builder(mContext,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            return true;
                        }
                        return false;
                    }
                }).create();
        //设置窗口
        mPayDiaLog.setCustomTitle(diaLogLayout);
        mPayDiaLog.setCanceledOnTouchOutside(false);

        mPayDiaLog.show();


        // 已充值
        buttonPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OnPayclickListener != null) {
                    OnPayclickListener.onPayClick();
                }
            }
        });

    }

    //确认支付监听器
    public void setPayOnclickListener(OnPayclickListener onOnclickListener) {

        this.OnPayclickListener = onOnclickListener;

    }

    // 返回监听器
    public void setBackViewOnClockListener(OnBackViewOnClockListener onBackViewOnClockListener) {
        this.onBackViewClockLostener = onBackViewOnClockListener;
    }

    /**
     * 设置已支付按钮接口
     */
    public interface OnPayclickListener {
        public void onPayClick();
    }

    public interface  OnBackViewOnClockListener {
        public void onBackView();
    }


    private RelativeLayout getTitleBar(Context context) {
        //创建titleBar
        RelativeLayout.LayoutParams params_titleBar = new RelativeLayout.LayoutParams(-1, uiUtils
                .getDipSize(40));

        RelativeLayout rll_titleBar = (RelativeLayout) uiUtils.createLayout(context, uiUtils
                        .LAYOUT.RELATIVELAYOUT,
                params_titleBar);
        rll_titleBar.setBackgroundColor(0xffffffff);
        rll_titleBar.setId(id_rl_titleBar);

        //创建titleBar左侧的返回按钮,并设置属性
        TextView tv_Back = uiUtils.createTextView(context);
        BitmapDrawable drawable = new BitmapDrawable(uiState.getResMap()
                .get("back"));
        //为了屏幕适配,这里设置18dp
        drawable.setBounds(0, 0, uiUtils.getDipSize(18), uiUtils.getDipSize(18));
        tv_Back.setCompoundDrawables(drawable, null, null, null);
        tv_Back.setTextColor(0xff747474);
        tv_Back.setText("返回");
        tv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackViewClockLostener.onBackView();
            }
        });
        RelativeLayout.LayoutParams lp_tvBack = new RelativeLayout.LayoutParams(-2, -2);
        lp_tvBack.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
        lp_tvBack.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        tv_Back.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv_Back.setId(id_tv_Back);          //设置ID


        //把返回tv设置进titlebar
        rll_titleBar.addView(tv_Back, lp_tvBack);

        //创建titlebar右侧的×按钮
        ImageView iv_close = uiUtils.createImageView(context);
        RelativeLayout.LayoutParams lp_IvClose = new RelativeLayout.LayoutParams(uiUtils
                .getDipSize(18), uiUtils.getDipSize(18));
        lp_IvClose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
        lp_IvClose.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        lp_IvClose.setMargins(0, uiUtils.dp2px(10), uiUtils.dp2px(5), uiUtils.dp2px(5));
        iv_close.setImageDrawable(new BitmapDrawable(uiState.getResMap()
                .get("close")));
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setId(id_iv_Close);        //设置ID
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackViewClockLostener.onBackView();

            }
        });
        //把ImageView设置进rl_titleBar
        rll_titleBar.addView(iv_close, lp_IvClose);

        //titlebar下面的一条线
        View view = uiUtils.createView(context);
        view.setBackgroundColor(0xffe7e7e7);
        RelativeLayout.LayoutParams lp_View = new RelativeLayout.LayoutParams(-1, uiUtils
                .getDipSize(1));
        lp_View.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
        rll_titleBar.addView(view, lp_View);     //加入这条线
        return rll_titleBar;
    }

}
