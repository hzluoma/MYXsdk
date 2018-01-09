package com.hg6kwan.sdk.inner.ui.dialog;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.BaseDialog;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDBDao;
import com.hg6kwan.sdk.inner.utils.Dao.NoticeDomain;

import java.util.List;

/**
 * Created by Roman on 2017/4/27.
 */

public class NoticeDialog extends BaseDialog implements View.OnClickListener{

    private final float ICON_HEIGHT_PORT = 0.265F;           //图片icon的容器的高度相对于dialog的百分比
    private final float ICON_HEIGHT_LAND = 0.4F;           //图片icon的容器的高度相对于dialog的百分比

    private final float SV_HEIGHT_PORT_PERCENT = 0.759F;
    private final float SV_HEIGHT_LAND_PERCENT = 0.741F;

    private final float IV_ICON_HEIGHT = 0.761f; //这个是图片的宽高比,根据图片尺寸计算而来
    private final float IV_ICON_WIDTH_PORT = 0.199F;  //竖屏时图片的宽度相对于dialog的百分比
    private final float IV_ICON_WIDTH_LAND = 0.179F;  //横屏时图片的宽度相对于dialog的百分比

    private final float PADDING_LEFT_PORT = 0.03F;  //竖屏时,TextView和按钮左右padding值相对于dialog的宽度的百分比
    private final float PADDING_LEFT_LAND = 0.05F;  //横屏时,TextView和按钮左右padding值相对于dialog的宽度的百分比

    private final float PADDING_TOP_PORT_BTN = 0.055F;  //竖屏时 按钮顶部padding值相对于dialog的宽度的百分比
    private final float PADDING_TOP_LAND_BTN = 0.049F;  //横屏时 按钮顶部padding值相对于dialog的宽度的百分比

    private final float MARGIN_TOP_PORT_BTN = 0.027F;  //竖屏时 按钮底部margin值相对于dialog的宽度的百分比
    private final float MARGIN_TOP_LAND_BTN = 0.030F;  //横屏时 按钮底部margin值相对于dialog的宽度的百分比

    private final float BTN_WIDTH_PORT = 0.487F;         //竖屏时,按钮的宽度相当于dialog的宽度的百分比
    private final float BTN_height_PORT = 0.10F;         //竖屏时,按钮的高度相当于dialog的宽度的百分比
    private final float BTN_WIDTH_LAND = 0.337F;         //横屏时,按钮的宽度相当于dialog的宽度的百分比
    private final float BTN_HEIGHT_LAND = 0.137F;         //横屏时,按钮的宽度相当于dialog的宽度的百分比

    private final float CB_height_PORT = 0.038F;         //竖屏时,checkbox的和底部的margin值相当于dialog的宽度的百分比
    private final float CB_height_LAND = 0.033F;         //横屏时,checkbox的和底部的margin值相当于dialog的宽度的百分比

    private final float IV_HEIGHT_PORT = 0.028f;         //竖屏时,imageview的高相当于dialog的宽度的百分比
    private final float IV_HEIGHT_LAND = 0.039f;         //横屏时,imageview的高相当于dialog的宽度的百分比
    private final float IV_WIDTH_HEIGHT_PERCENT = 8.66f;         //图片宽高比



    private int mIv_icon_width;                     //imageview的宽
    private int mIv_icon_height;                    //imageview的高
    private int mPadding_left;                      //TextView的左右margin/padding值
    private int mBtn_padding_top;                   //button的顶部margin值
    private int mBtn_margin_bottom;                //button的底部margin值
    private int mBtn_width;
    private int mBtn_height;
    private int mIv_width;
    private int mIv_height;
    private int mSV_HEIGHT;

    public boolean isChecked = true;
    private ImageView mCb;
    private Bitmap mBmp_checked;
    private Bitmap mBmp_unchecked;
    private NoticeDBDao mNoticeDBDao;

    public boolean isChecked() {
        return isChecked;
    }

    @android.support.annotation.IdRes
    int id_btn = 1;

    @android.support.annotation.IdRes
    int id_cb = 2;

    private int mCb_margin_bottom;


    public NoticeDialog(TYPE type, Context context) {
        super(TYPE.NOTICE, context);
    }

    public NoticeDialog(Context context) {
        this(TYPE.NOTICE, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //根据横屏或竖屏设置好控件的百分比
        if (uiState.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mIv_icon_width = (int) (mWidth * IV_ICON_WIDTH_LAND);
            mIv_icon_height = (int) (mIv_icon_width * IV_ICON_HEIGHT);
            mPadding_left = (int) (mWidth * PADDING_LEFT_LAND);
            mBtn_padding_top = (int) (mHeight * PADDING_TOP_LAND_BTN);
            mBtn_width = (int) (mWidth * BTN_WIDTH_LAND);
            mBtn_height = (int) (mHeight * BTN_HEIGHT_LAND);
            mBtn_margin_bottom = (int) (mHeight * MARGIN_TOP_LAND_BTN);
            mCb_margin_bottom = (int) (mHeight * CB_height_LAND);
            mIv_height = (int) (mHeight * IV_HEIGHT_LAND);
            mIv_width = (int) (mIv_height * IV_WIDTH_HEIGHT_PERCENT);
            mSV_HEIGHT = (int) (mHeight * SV_HEIGHT_LAND_PERCENT);
        } else {
            mIv_icon_width = (int) (mWidth * IV_ICON_WIDTH_PORT);
            mIv_icon_height = (int) (mIv_icon_width * IV_ICON_HEIGHT);
            mPadding_left = (int) (mWidth * PADDING_LEFT_PORT);
            mBtn_padding_top = (int) (mHeight * PADDING_TOP_PORT_BTN);
            mBtn_width = (int) (mWidth * BTN_WIDTH_PORT);
            mBtn_height = (int) (mHeight * BTN_height_PORT);
            mBtn_margin_bottom = (int) (mHeight * MARGIN_TOP_PORT_BTN);
            mCb_margin_bottom = (int) (mHeight * CB_height_PORT);
            mIv_height = (int) (mHeight * IV_HEIGHT_PORT);
            mIv_width = (int) (mIv_height * IV_WIDTH_HEIGHT_PERCENT);
            mSV_HEIGHT = (int) (mHeight * SV_HEIGHT_PORT_PERCENT);
        }

        this.setContentView(createUI(mContext),new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
        ));
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    private View createUI(Context context){

        //dialog的根布局
        LinearLayout linearLayout = uiUtils.createLayout(uiUtils.LAYOUT.NOTICE,context);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
//        linearLayout.setLayoutParams(layoutParams);
//        //设置权重总和
//        linearLayout.setWeightSum(1);
//        //设置子控件排列方向
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout rl_root = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(-1, -1);
        rl_root.setLayoutParams(rlp);
        rl_root.setClickable(true);

        //根布局的背景shape
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xffffffff);
        gd.setCornerRadius(12f);
        rl_root.setBackground(gd);

        //ScrollView
        ScrollView sv = new ScrollView(mContext);
        RelativeLayout.LayoutParams lp_sv = new RelativeLayout.LayoutParams(-1, mSV_HEIGHT);
        sv.setLayoutParams(lp_sv);
        //隐藏滚动条
        sv.setVerticalScrollBarEnabled(false);
        sv.setFocusableInTouchMode(true);

        //因为ScrollView只能有一个子View,因此需要再有一个父容器
        LinearLayout ll_parent = new LinearLayout(mContext);
        LinearLayout.LayoutParams llp_parent = new LinearLayout.LayoutParams(-1, -1);
        ll_parent.setOrientation(LinearLayout.VERTICAL);
        ll_parent.setLayoutParams(llp_parent);

        RelativeLayout rl_Icon = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams rlp_Icon = new RelativeLayout.LayoutParams(-1, (int)
                (mHeight * ICON_HEIGHT_PORT));

        //Icon的容器ImageView
        ImageView iv_icon = new ImageView(mContext);
        RelativeLayout.LayoutParams rlp_iv_icon = new RelativeLayout.LayoutParams(mIv_icon_width,
                mIv_icon_height);
        rlp_iv_icon.addRule(RelativeLayout.CENTER_IN_PARENT);
        //获取图片的bitmap
        Bitmap bitmap_notice = uiState.getResMap().get("notice");
        iv_icon.setImageBitmap(bitmap_notice);
        iv_icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //用来显示html文本的TextView
        WebView webView = new WebView(mContext);
        webView.setPadding(mPadding_left, 0, mPadding_left, 0);
        mNoticeDBDao = NoticeDBDao.getInstance(mContext);
        mNoticeDBDao.openDataBase();
        List<NoticeDomain> domainList = mNoticeDBDao.queryDataList();
        NoticeDomain noticeDomain = domainList.get(0);
        String noticeContent = noticeDomain.getContent();
        webView.loadDataWithBaseURL(null, noticeContent, "text/html", "utf-8", null);
        webView.getSettings().setJavaScriptEnabled(true);
        //webview的属性
        LinearLayout.LayoutParams llp_wv = new LinearLayout.LayoutParams(-2, -2);
        llp_wv.setMargins(mPadding_left,0,mPadding_left,0);

        //按钮 : 我知道了
        Button button = createBtn();

        //用ImageView和选择器做一个CheckBox
        mCb = new ImageView(mContext);
        //设置ID
        mCb.setId(id_cb);
        RelativeLayout.LayoutParams rlp_cb = new RelativeLayout.LayoutParams(mIv_width, mIv_height);
//        rlp_cb.addRule(RelativeLayout.BELOW, id_btn);
        rlp_cb.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rlp_cb.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlp_cb.setMargins(0,0,0,mCb_margin_bottom);
        //设置属性
        mCb.setLayoutParams(rlp_cb);
        mCb.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //获取选择器需要的两张图片
        mBmp_checked = uiState.getResMap().get("cb_checked");
        mBmp_unchecked = uiState.getResMap().get("cb_unchecked");
        mCb.setImageBitmap(mBmp_checked);
        mCb.setClickable(true);

        rl_Icon.addView(iv_icon,rlp_iv_icon);
        ll_parent.addView(rl_Icon,rlp_Icon);
        ll_parent.addView(webView,llp_wv);
        rl_root.addView(mCb,rlp_cb);
        rl_root.addView(button);
        sv.addView(ll_parent);
        rl_root.addView(sv);

        mCb.setOnClickListener(this);

        return rl_root;
    }


    /**
     * 创建按钮,设置点击事件
     * @return
     */
    private Button createBtn() {
        //按钮 : 我知道了
        Button button = new Button(mContext);
        button.setText("我知道了");
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        button.setTextColor(0xffffffff);
        //button的属性
        RelativeLayout.LayoutParams rlp_btn = new RelativeLayout.LayoutParams(mBtn_width,
                mBtn_height);
        //设置id
        button.setId(id_btn);
        rlp_btn.addRule(RelativeLayout.ABOVE,id_cb);
        rlp_btn.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //按钮的shape
        GradientDrawable btn_shape = new GradientDrawable();
        btn_shape.setShape(GradientDrawable.RECTANGLE);
        btn_shape.setColor(0xfffea501);
        //描边
        btn_shape.setStroke(uiUtils.getDipSize(1), 0xffc9c9c9);
        //圆角
        btn_shape.setCornerRadius(10.0f);

        //设置button背景
        button.setBackground(btn_shape);
        //设置button的padding
        rlp_btn.setMargins(mPadding_left, mBtn_padding_top, mPadding_left, mBtn_margin_bottom);
        button.setLayoutParams(rlp_btn);
        button.setOnClickListener(this);
        return button;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == id_cb) {
            if (isChecked()) {
                isChecked = false;
                mCb.setImageBitmap(mBmp_unchecked);
            } else {
                isChecked = true;
                mCb.setImageBitmap(mBmp_checked);
            }
        }

        if (v.getId() == id_btn) {
            //
            if (isChecked()) {
                mNoticeDBDao.updateData(1, "0");
            } else {
                mNoticeDBDao.updateData(1, "1");
            }
            mNoticeDBDao.closeDataBase();
            ControlUI.getInstance().closeSDKUI();
        }
    }
}
