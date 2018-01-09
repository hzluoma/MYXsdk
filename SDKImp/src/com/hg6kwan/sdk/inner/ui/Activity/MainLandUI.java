package com.hg6kwan.sdk.inner.ui.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
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

import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.task.AliPayTask;
import com.hg6kwan.sdk.inner.utils.task.TPPayTask;
import com.hg6kwan.sdk.inner.utils.task.WechatPayTask;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/22.
 */

public class MainLandUI implements View.OnClickListener{

    private Context mContext;

    @android.support.annotation.IdRes
    int id_rl_titleBar = 1;       //titleBar的ID
    @android.support.annotation.IdRes
    int id_tv_Back = 2;           //左侧返回按钮
    @android.support.annotation.IdRes
    int id_iv_Close = 3;          //右侧叉叉图案

    @android.support.annotation.IdRes
    int id_tv_account_cn = 4;          //"支付账户 : "

    @android.support.annotation.IdRes
    int id_tv_account = 5;              //重要ID 游戏商传过来的账号要设置到这个ID对应的Tv去

    @android.support.annotation.IdRes
    int id_tv_merchandise = 6;          //"购买商品 : "

    @android.support.annotation.IdRes
    int id_tv_sellInfo = 7;             //重要ID  商品信息

    @android.support.annotation.IdRes
    int id_tv_addMoney = 8;             //"支付金额 : "

    @android.support.annotation.IdRes
    int id_tv_totalPrice = 9;             //重要ID  总价格

    @android.support.annotation.IdRes
    int id_btn_confirm = 10;

    private int mChildCount;            //左侧条目的总数
    private LinearLayout mLl_swithcher_container;       //左侧支付条目的父容器
    private int mPayTag;                //目前选中的支付类型 0 微信支付 1 支付宝支付
    private Button mBtn_confirm;        //确认按钮

    private  String mUserName;                //传入进来的角色名字或账号
    private  String mPrice;         //需要支付的金额
    private  String mCpOrder;                //商品描述

    private ArrayList<String> mPayChannelList;         //包含支付渠道顺序的集合

    private OnPayclickListener OnPayclickListener;//确定按钮被点击了的监听器

    //-------------------支付宝的Handler----------------------

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;


    private ArrayList<RelativeLayout> mRlList;
    private final PayActivity mPayActivity;
    private AlertDialog mPayDiaLog;

    public MainLandUI(String userName, String price, String cpOrder, ArrayList<String>
            payChannelList, PayActivity payActivity) {
        this.mUserName = userName;
        this.mPrice = price;
        this.mCpOrder = cpOrder;
        this.mPayChannelList = payChannelList;
        mPayActivity = payActivity;
    }

    public AlertDialog getPayDiaLog() {
        if (mPayDiaLog !=null && mPayDiaLog.isShowing()) {
            return mPayDiaLog;
        }
        return null;
    }

    public View getView(Context context) {

        mContext = context;

        //创建页面的父布局为RelativeLayout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);

        RelativeLayout layout = (RelativeLayout) uiUtils.createLayout(context, uiUtils
                        .LAYOUT
                        .RELATIVELAYOUT,
                params);

        //创建TitleBar
        RelativeLayout rll_titleBar = getTitleBar(context);

        //创建LinearLayout的属性   包含有左侧等区域和右侧空白区域,使整个区域在titleBar下面,注意addView的顺序,最里层的要先addView
        RelativeLayout.LayoutParams rl_container_below = new RelativeLayout.LayoutParams(-1, -1);

        LinearLayout ll_container_below = (LinearLayout) uiUtils.createLayout(context, uiUtils
                        .LAYOUT.LINEARLAYOUT,
                rl_container_below);
        rl_container_below.addRule(RelativeLayout.BELOW, id_rl_titleBar);

        //创建左侧switcher的父容器
        LinearLayout.LayoutParams lp_swithcer = new LinearLayout.LayoutParams(0, -1, 1.2f);

        mLl_swithcher_container = (LinearLayout) uiUtils.createLayout(context, uiUtils
                .LAYOUT.LINEARLAYOUT, lp_swithcer);
        mLl_swithcher_container.setOrientation(LinearLayout.VERTICAL);

        mRlList = new ArrayList<>();

        for (String payChannel : mPayChannelList) {
            if (payChannel.equals("10")) {
                mRlList.add(createItemContainer(context, PAYTYPE.WECHAT));
                continue;
            } else if (payChannel.equals("3")) {
                mRlList.add(createItemContainer(context, PAYTYPE.ALI));
                continue;
            } else if (payChannel.equals("6")) {
                mRlList.add(createItemContainer(context, PAYTYPE.PLATFORM));
                continue;
            } else if (payChannel.equals("4")) {
                mRlList.add(createItemContainer(context, PAYTYPE.TICKET));
                continue;
            }
        }

        //循环添加5个条目
        for (int i = 0; i < mRlList.size(); i++) {
            mLl_swithcher_container.addView(mRlList.get(i));
        }
        //根据Paychannel动态设置权重
        float weight = 2.0f;

        switch (mPayChannelList.size()) {

            case 1:
                weight = 6.0f;
                break;

            case 2:
                weight = 5.0f;
                break;

            case 3:
                weight = 4.0f;
                break;

            case 4:
                weight = 3.0f;
                break;

            case 5:
                weight = 2.0f;
                break;

        }

        //用来在条目下方占位的没有内容的frameLayout
        FrameLayout frameLayout = new FrameLayout(context);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(-1, 0, weight);
        mLl_swithcher_container.addView(frameLayout, params1);

        mChildCount = mLl_swithcher_container.getChildCount();

        for (int i = 0; i < mChildCount; i++) {
            View view = mLl_swithcher_container.getChildAt(i);
            view.setOnClickListener(this);
        }


        //创建右侧空白区域的父容器FrameLayout
        LinearLayout.LayoutParams lp_FrameContainer = new LinearLayout.LayoutParams(0, -1, 3.0f);
        FrameLayout fr_Container = (FrameLayout) uiUtils.createLayout(context, uiUtils
                .LAYOUT.FRAMELAYOUT, lp_FrameContainer);

        fr_Container.setBackgroundColor(0xffffffff);

        //创建右边区域
        fr_Container.addView(createInfoContainer(context));

        //加进左边区域和右边区域
        ll_container_below.addView(mLl_swithcher_container, lp_swithcer);
        ll_container_below.addView(fr_Container, lp_FrameContainer);

        //把titleBar的RL加进layout
        layout.addView(rll_titleBar);
        //加进titleBar下面的区域
        layout.addView(ll_container_below);

        //默认点击第一项
        onClick(mLl_swithcher_container.getChildAt(0));

        return layout;
    }

    //支付类型
    public enum PAYTYPE {
        WECHAT, ALI, JD, PLATFORM, TICKET
    }

    /**
     * 创建左侧条目的控件
     *
     * @param context
     * @param paytype 支付类型
     */
    private RelativeLayout createItemContainer(Context context, PAYTYPE paytype) {

        LinearLayout.LayoutParams lp_WxPay = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        RelativeLayout rl_WXPay = new RelativeLayout(context);
        rl_WXPay.setBackgroundColor(0xffFaFaFa);
        rl_WXPay.setLayoutParams(lp_WxPay);

        ImageView iv_WxPay = uiUtils.createImageView(context);

        RelativeLayout.LayoutParams rlp_iv_WxPay = new RelativeLayout.LayoutParams(uiUtils
                .getDipSize(117.5f), uiUtils.getDipSize(25));
        rlp_iv_WxPay.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        rlp_iv_WxPay.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        rlp_iv_WxPay.setMargins(0, uiUtils.getDipSize(8), 0, 0);

        iv_WxPay.setLayoutParams(rlp_iv_WxPay);

        //根据传入的支付类型,设置不同的选择器作为背景
        switch (paytype) {

            case WECHAT:
                StateListDrawable wechatDrawable = new StateListDrawable();

                wechatDrawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_wechatpay")));

                wechatDrawable.addState(new int[]{}, new BitmapDrawable(uiState
                        .getResMap().get("ll_wechatpay_enable")));

                iv_WxPay.setImageDrawable(wechatDrawable);
                break;

            case ALI:
                StateListDrawable aliPayDrawable = new StateListDrawable();

                aliPayDrawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_alipay")));

                aliPayDrawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_alipay_enable")));

                iv_WxPay.setImageDrawable(aliPayDrawable);
                break;

            case JD:
                StateListDrawable jdPayDrawable = new StateListDrawable();

                jdPayDrawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_jdpay")));

                jdPayDrawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_jdpay_enable")));

                iv_WxPay.setImageDrawable(jdPayDrawable);
                break;

            case PLATFORM:
                StateListDrawable platformDrawable = new StateListDrawable();

                platformDrawable.addState(new int[]{android.R.attr.state_enabled}, new
                        BitmapDrawable
                        (uiState.getResMap().get("ll_platform")));

                platformDrawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_platform_enable")));

                iv_WxPay.setImageDrawable(platformDrawable);
                break;

            case TICKET:
                StateListDrawable ticketDrawable = new StateListDrawable();

                ticketDrawable.addState(new int[]{android.R.attr.state_enabled}, new BitmapDrawable
                        (uiState.getResMap().get("ll_ticket")));

                ticketDrawable.addState(new int[]{}, new BitmapDrawable(uiState.getResMap().get("ll_ticket_enable")));

                iv_WxPay.setImageDrawable(ticketDrawable);
                break;
        }


        //微信支付条目区域下面的一条线
        View lineView = uiUtils.createView(context);
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

        //加入ImageView
        rl_WXPay.addView(iv_WxPay);
        //加入线条
        rl_WXPay.addView(lineView);

        return rl_WXPay;
    }

    /**
     * 创建商品详情界面
     *
     * @param context
     */
    private RelativeLayout createInfoContainer(Context context) {

        //创建父布局容器
        RelativeLayout.LayoutParams rlp_InfoContainer = new RelativeLayout.LayoutParams(-1, -1);
        RelativeLayout rl_InfoContainer = new RelativeLayout(context);
        rl_InfoContainer.setLayoutParams(rlp_InfoContainer);


        //创建 "支付账号 : " 的textView
        TextView tv_account_cn = new TextView(context);
        RelativeLayout.LayoutParams rlp_account_cn = new RelativeLayout.LayoutParams(-2, -2);
        rlp_account_cn.setMargins(uiUtils.getDipSize(35), uiUtils.getDipSize(30), 0, 0);
        tv_account_cn.setText("支付账户 : ");
        tv_account_cn.setTextColor(0xff737373);
        tv_account_cn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv_account_cn.setId(id_tv_account_cn);
        tv_account_cn.setLayoutParams(rlp_account_cn);


        //创建重要TextView   账号
        TextView tv_account = new TextView(context);
        RelativeLayout.LayoutParams rlp_account = new RelativeLayout.LayoutParams(-2, -2);
        rlp_account.setMargins(0, uiUtils.getDipSize(30), 0, 0);
        tv_account.setSingleLine(true);
        tv_account.setEllipsize(TextUtils.TruncateAt.END);
        if (mUserName != null) {
            tv_account.setText(mUserName);
        }
        tv_account.setTextColor(0xff737373);
        tv_account.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv_account.setId(id_tv_account);
        rlp_account.addRule(RelativeLayout.RIGHT_OF, tv_account_cn.getId());
        tv_account.setLayoutParams(rlp_account);

        //创建第二行TextView   "购买商品 : "
        TextView tv_merchandise_cn = new TextView(context);
        RelativeLayout.LayoutParams rlp_merchandise = new RelativeLayout.LayoutParams(-2, -2);
        rlp_merchandise.setMargins(0, uiUtils.getDipSize(8), 0, 0);
        tv_merchandise_cn.setText("购买商品 : ");
        tv_merchandise_cn.setTextColor(0xff737373);
        tv_merchandise_cn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv_merchandise_cn.setId(id_tv_merchandise);
        rlp_merchandise.addRule(RelativeLayout.BELOW, tv_account_cn.getId());
        rlp_merchandise.addRule(RelativeLayout.ALIGN_LEFT, tv_account_cn.getId());
        tv_merchandise_cn.setLayoutParams(rlp_merchandise);

        //第二行重要信息TextView  商品信息
        TextView tv_sellinfo = new TextView(context);
        RelativeLayout.LayoutParams rlp_sellInfo = new RelativeLayout.LayoutParams(-2, -2);
        rlp_sellInfo.setMargins(0, uiUtils.getDipSize(8), 0, 0);
        tv_sellinfo.setSingleLine(true);
        tv_sellinfo.setEllipsize(TextUtils.TruncateAt.END);
        if (mCpOrder != null) {
            tv_sellinfo.setText(mCpOrder);
        }
        tv_sellinfo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv_sellinfo.setTextColor(0xfff34755);
        tv_sellinfo.setId(id_tv_sellInfo);
        rlp_sellInfo.addRule(RelativeLayout.BELOW, tv_account_cn.getId());
        rlp_sellInfo.addRule(RelativeLayout.RIGHT_OF, tv_merchandise_cn.getId());
        tv_sellinfo.setLayoutParams(rlp_sellInfo);

        //创建第三行TextView  "充值金额 : "
        TextView tv_addMoney = new TextView(context);
        RelativeLayout.LayoutParams rlp_addMoney = new RelativeLayout.LayoutParams(-2, -2);
        rlp_addMoney.setMargins(0, uiUtils.getDipSize(12), 0, 0);
        tv_addMoney.setText("充值金额 : ");
        tv_addMoney.setTextColor(0xff737373);
        tv_addMoney.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv_addMoney.setId(id_tv_addMoney);
        rlp_addMoney.addRule(RelativeLayout.BELOW, tv_merchandise_cn.getId());
        rlp_addMoney.addRule(RelativeLayout.ALIGN_LEFT, tv_merchandise_cn.getId());
        tv_addMoney.setLayoutParams(rlp_addMoney);


        //创建第三行的重要TextView  充值金额totalPrice
        TextView tv_totalPrice = new TextView(context);
        RelativeLayout.LayoutParams rlp_totalPrice = new RelativeLayout.LayoutParams(-2, -2);
        rlp_totalPrice.setMargins(0, uiUtils.getDipSize(12), 0, 0);
        tv_totalPrice.setText(mPrice + "元");
        tv_totalPrice.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        tv_totalPrice.setTextColor(0xfff34755);
        tv_totalPrice.setId(id_tv_totalPrice);
        rlp_totalPrice.addRule(RelativeLayout.BELOW, tv_merchandise_cn.getId());
        rlp_totalPrice.addRule(RelativeLayout.RIGHT_OF, tv_addMoney.getId());
        tv_totalPrice.setLayoutParams(rlp_totalPrice);

        //确认按钮的shape
        GradientDrawable btn_shape = new GradientDrawable();
        btn_shape.setShape(GradientDrawable.RECTANGLE);
        btn_shape.setColor(0xFF6495ed);
        //描边
        btn_shape.setStroke(uiUtils.getDipSize(1), 0xffc9c9c9);
        //圆角
        btn_shape.setCornerRadius(3.0f);

        //创建确认按钮
        mBtn_confirm = new Button(context);
        RelativeLayout.LayoutParams rlp_btn_confirm = new RelativeLayout.LayoutParams(-2, -2);
        rlp_btn_confirm.setMargins(0, uiUtils.getDipSize(10), 0, 0);
        rlp_btn_confirm.addRule(RelativeLayout.BELOW, tv_totalPrice.getId());
        rlp_btn_confirm.addRule(RelativeLayout.ALIGN_LEFT, tv_merchandise_cn.getId());
        mBtn_confirm.setText("确认支付");
        mBtn_confirm.setTextColor(0xffffffff);
        mBtn_confirm.setBackgroundDrawable(btn_shape);
        mBtn_confirm.setPadding(uiUtils.getDipSize(80), uiUtils.getDipSize(8), uiUtils
                .getDipSize(80), uiUtils.getDipSize(8));
        mBtn_confirm.setLayoutParams(rlp_btn_confirm);
        mBtn_confirm.setId(id_btn_confirm);
        //设置按钮的点击事件
        mBtn_confirm.setOnClickListener(this);

        //加进TextView
        rl_InfoContainer.addView(tv_account_cn);
        rl_InfoContainer.addView(tv_account);
        rl_InfoContainer.addView(tv_merchandise_cn);
        rl_InfoContainer.addView(tv_sellinfo);
        rl_InfoContainer.addView(tv_addMoney);
        rl_InfoContainer.addView(tv_totalPrice);
        rl_InfoContainer.addView(mBtn_confirm);

        return rl_InfoContainer;
    }

    @NonNull
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
        tv_Back.setText("返回");
        tv_Back.setTextColor(0xff747474);
        RelativeLayout.LayoutParams lp_tvBack = new RelativeLayout.LayoutParams(-2, -2);
        lp_tvBack.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
        lp_tvBack.addRule(RelativeLayout.CENTER_VERTICAL, 1);
        tv_Back.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv_Back.setId(id_tv_Back);          //设置ID
        setonClick(tv_Back);                //设置点击

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
        setonClick(iv_close);               //设置点击
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

    public void affirmPayDiaLog() {

        //获取dialog的背景图
        BitmapDrawable dialog_BackGround = new BitmapDrawable(uiState.getResMap()
                .get("dialog_background"));

        // 已充值按钮shape
        GradientDrawable btn_shape_position = new GradientDrawable();
        btn_shape_position.setShape(GradientDrawable.RECTANGLE);
        btn_shape_position.setColor(0xff118EEA);
        btn_shape_position.setCornerRadius(12.0f);

        // 退出按钮shape
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
        Button buttonPosition = new Button(mContext);
        LinearLayout.LayoutParams positionParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
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
        LinearLayout.LayoutParams continueParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
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

    /**
     * 设置已支付按钮接口
     */
    public interface OnPayclickListener {
        public void onPayClick();
    }


    private void setonClick(View view) {
        view.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View view) {

        //
        if (view.getId() == mBtn_confirm.getId()) {

            final String channel = mPayChannelList.get(mPayTag);

            switch (channel) {
                case Constants.PAYCHANNEL_ALI:

                    AliPayTask aliPayTask = new AliPayTask(mPayActivity);
                    aliPayTask.aliPay(Constants.PAYCHANNEL_ALI);

                    break;

                case Constants.PAYCHANNEL_WECHAT:

                    WechatPayTask wechatPayTask = new WechatPayTask(mPayActivity);
                    wechatPayTask.payOnWechat(Constants.PAYCHANNEL_WECHAT);

                    break;

                case Constants.PAYCHANNEL_PLATFORM:
                    //启动平台币支付的任务栈
                    TPPayTask task1 = new TPPayTask(mPayActivity);
                    task1.payOnPlatform(Constants.PAYCHANNEL_PLATFORM);
                    break;

                case Constants.PAYCHANNEL_TICKET:
                    //启动代金券支付的任务栈
                    TPPayTask task2 = new TPPayTask(mPayActivity);
                    task2.payByTicket(Constants.PAYCHANNEL_TICKET);
                    break;
            }

            return;
        }

        if (view.getId() == id_tv_Back || view.getId() == id_iv_Close) {
            if (OnPayclickListener != null) {
                ControlUI.getInstance().exitPay(Constants.PAY_CANCEL);
                OnPayclickListener.onPayClick();
            }
        }

        //被点击的区域的单位
        int indexOfChild = mLl_swithcher_container.indexOfChild(view);

        mPayTag = indexOfChild;

        //代金券以下的占位区域点击就返回
        if (indexOfChild >= mRlList.size()) {
            return;
        }

        for (int i = 0; i < mChildCount; i++) {

            View childView = mLl_swithcher_container.getChildAt(i);
            //被点中的设置为不能再被点击
            setEnabled(childView, indexOfChild != i);
        }
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

}
