package com.hg6kwan.sdk.inner.ui.floatmenu;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.UserUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.start;
import static android.view.WindowManager.LayoutParams.TYPE_TOAST;

public class FloatMenu extends FrameLayout implements OnTouchListener {
    private final static String TAG = "FloatMenu";
    private final int HANDLER_TYPE_HIDE_LOGO = 100;
    private final int HANDLER_TYPE_CANCEL_ANIM = 101;

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private Context mContext;


    private ImageView mFloatLogoImv;//悬浮球的logo
    private ImageView mFloatLoaderImv;//围绕悬浮球的动画背景图，可用于做旋转或其它动画，看具体的设计
    private LinearLayout mFloatMenuLine;//悬浮菜单的载体，横向线性布局
    private FrameLayout mFloatLogoFra;//悬浮球的logo和动画背景图的载体

    private ArrayList<MenuItem> mMenuItems; //菜单对象的集合
    private ArrayList<MenuItemView> mMenuItemViews; //菜单中的Item对应的view集合

    private boolean mIsRight;//当前悬浮球是否悬停在右边
    private boolean isInitingLoader;//当前悬浮球的动画是否首次加载
    private boolean isActionLoading;//当前悬浮球动画是否是点击事件触发的动画
    private boolean isSmall;//是否出去小球状态

    private float mTouchStartX;//记录首次按下的位置x
    private float mTouchStartY;//记录首次按下的位置y

    private int mScreenWidth;//屏幕宽度
    private int mScreenHeight;//屏幕高度

    private boolean mShowLoader = true;//是否显示加载动画

    private Timer mTimer;//一段时间没有操作 定时隐藏菜单，缩小悬浮球 的定时器，定时器可以设置一个对应的定时任务
    private TimerTask mTimerTask;//配合定时器的定时任务，本质是一个runnable,包装了定时相关业务方法


    final Handler mTimerHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_TYPE_HIDE_LOGO) {
                if (isInitingLoader) {
                    isInitingLoader = false;
                    mShowLoader = false;
                    isSmall = true;
                    LayoutParams params = (LayoutParams) mFloatLogoFra.getLayoutParams();
                    int padding75 = (params.width) * 2 / 5;
                    int padding10 = 7;
                    if (mIsRight) {
                        if (params.rightMargin <= 0) {
                            mFloatLogoFra.setPadding(padding10, padding10, 0, padding10);
                            params.setMargins(0, 0, -padding75, 0);
                            mFloatLogoFra.setLayoutParams(params);
                        }
                    } else {
                        if (params.leftMargin >= 0) {
                            params.setMargins(-padding75, 0, 0, 0);
                            mFloatLogoFra.setLayoutParams(params);
                            mFloatLogoFra.setPadding(0, padding10, padding10, padding10);
                        }
                    }
                    mWmParams.alpha = 0.6f;
                    mWindowManager.updateViewLayout(FloatMenu.this, mWmParams);
                    refreshFloatMenu(mIsRight);
                    hideLiner();
                }
            } else if (msg.what == HANDLER_TYPE_CANCEL_ANIM) {
                resetLogoSize();
                mFloatLoaderImv.clearAnimation();
                mFloatLoaderImv.setVisibility(View.GONE);
                mShowLoader = false;
            }
            super.handleMessage(msg);
        }
    };

    private void showLinear(){
        if (mFloatMenuLine.getVisibility() == View.GONE)
            mFloatMenuLine.setVisibility(View.VISIBLE);
    }

    private void hideLiner(){
        if (mFloatMenuLine.getVisibility() == View.VISIBLE)
            mFloatMenuLine.setVisibility(View.GONE);
    }

    private void resetLogoSize() {
        LayoutParams floatLogoLayoutParams = menuUtils.createLayoutParams(menuUtils.dp2Px(50, mContext), menuUtils.dp2Px(50, mContext));
        if (mIsRight){
            floatLogoLayoutParams.gravity = Gravity.RIGHT;
        }else {
            floatLogoLayoutParams.gravity = Gravity.LEFT;
        }
        mFloatLogoFra.setLayoutParams(floatLogoLayoutParams);
        mFloatLogoFra.setPadding(0, 0, 0, 0);
        isSmall = false;
    }

    public static class Builder {

        private Context mContext;
        private ArrayList<MenuItem> menuItems = new ArrayList<>();


        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder addMenuItem(MenuItem.TYPE type,OnClickListener listener){
            menuItems.add(new MenuItem(type,listener));
            return this;
        }

        public Builder menuItems(ArrayList<MenuItem> menuItems) {
            menuItems.clear();
            this.menuItems = menuItems;
            return this;
        }

        public FloatMenu build() {
            return new FloatMenu(this);
        }
    }

    public FloatMenu(Builder builder) {
        super(builder.mContext);

        this.mMenuItems = builder.menuItems;
        this.mContext = builder.mContext;
        init(this.mContext);
        //
    }

    public ArrayList<MenuItem> getMenuItems() {
        return mMenuItems;
    }

    /**
     * @param mContext mContext
     * @return return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private View createView(final Context mContext) {
        FrameLayout rootFloatView = new FrameLayout(mContext);
        LayoutParams params = menuUtils.createLayoutParams(LayoutParams.MATCH_PARENT, menuUtils.dp2Px(50, mContext));
        params.gravity = Gravity.CENTER;
        rootFloatView.setLayoutParams(params);

        //动作条
        mFloatMenuLine = new LinearLayout(mContext);
        LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(menuUtils.dp2Px(346, mContext), menuUtils.dp2Px(45, mContext));
        mFloatMenuLine.setLayoutParams(lineLp);
        mFloatMenuLine.setOrientation(LinearLayout.HORIZONTAL);
        mFloatMenuLine.setBackground(new BitmapDrawable(uiUtils.getResBitmap("menu_line_bg")));

        //动作集合
        mMenuItemViews = generateMenuItemViews();
        addMenuItemViews();

        rootFloatView.addView(mFloatMenuLine);

        //logo包括一张图片  和 动画效果
        mFloatLogoFra = new FrameLayout(mContext);
        LayoutParams floatLogoLayoutParams = menuUtils.createLayoutParams(menuUtils.dp2Px(50, mContext), menuUtils.dp2Px(50, mContext));
        mFloatLogoFra.setLayoutParams(floatLogoLayoutParams);
        floatLogoLayoutParams.gravity = Gravity.CENTER;

        //图片
        mFloatLogoImv = new ImageView(mContext);
        LayoutParams imgLp = getImageViewLayoutParams();
        mFloatLogoImv.setLayoutParams(imgLp);
        mFloatLogoImv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mFloatLogoImv.setImageBitmap(uiUtils.getResBitmap("float_logo"));
        //动画
        LayoutParams layoutParams = getImageViewLayoutParams();
        mFloatLoaderImv = new ImageView(mContext);
        mFloatLoaderImv.setLayoutParams(layoutParams);
        mFloatLoaderImv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mFloatLoaderImv.setImageBitmap(uiUtils.getResBitmap("menu_logo_anim"));
        mFloatLoaderImv.setVisibility(INVISIBLE);

        mFloatLogoFra.setClipChildren(false);
        mFloatLogoFra.setClipToPadding(false);

        rootFloatView.setClipChildren(false);
        rootFloatView.setClipToPadding(false);

        mFloatMenuLine.setClipChildren(false);
        mFloatMenuLine.setClipToPadding(false);

        mFloatLogoFra.addView(mFloatLogoImv);
        mFloatLogoFra.addView(mFloatLoaderImv);
        rootFloatView.addView(mFloatLogoFra);

        rootFloatView.setOnTouchListener(this);
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFloatMenuLine.getVisibility() == View.GONE){
                    showLinear();
                }else {
                    hideLiner();
                }
            }
        });

        rootFloatView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        return rootFloatView;
    }

    //首次弹出菜单时，item会从透明到不透明，从小到大的动画效果
    private void addMenuItemViews() {
        for (final MenuItemView menuItemView : mMenuItemViews) {
            menuItemView.setVisibility(VISIBLE);
            mFloatMenuLine.addView(menuItemView);
        }
    }

    private LayoutParams getImageViewLayoutParams() {
        LayoutParams imgLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgLp.gravity = Gravity.CENTER;
        return imgLp;
    }

    private ArrayList<MenuItemView> generateMenuItemViews() {
        ArrayList<MenuItemView> menuItemViews = new ArrayList<>(mMenuItems.size());
        for (MenuItem item : mMenuItems) {
            menuItemViews.add(generateMenuItemView(item));
        }
        return menuItemViews;
    }


    private MenuItemView generateMenuItemView(MenuItem item) {
        MenuItemView menuItemView = new MenuItemView(mContext, item);
        setMenuItemOnClickListener(menuItemView,item.getOnClickListener());
        return menuItemView;
    }

    //中间先代理一次menuitem的点击事件，增加每次点击前先隐藏悬浮的菜单
    private void setMenuItemOnClickListener(final MenuItemView menuItemView, final View.OnClickListener onClickListener) {
        menuItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFloatMenuLine.getVisibility() == VISIBLE) {
                    hideLiner();
                }
                onClickListener.onClick(menuItemView);
            }
        });
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        this.mWmParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT>Build.VERSION_CODES.N) {
            mWmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mWmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        mWmParams.format = PixelFormat.RGBA_8888;

        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;;
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mWmParams.x = 0;
        mWmParams.y = mScreenHeight * 3 / 7;
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;

        //添加视图
        addView(createView(mContext));
        bringToFront();
        mWindowManager.addView(this, mWmParams);
        mTimer = new Timer();
        mShowLoader = true;
        refreshFloatMenu(mIsRight);
        //
        hideLiner();
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev){
        //
        hideLiner();
        //
        return super.onTouchEvent(ev);
    }
    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getVisibility() == View.GONE) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;

        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                if (mIsRight) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        removeTimerTask();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                isInitingLoader = true;
                resetLogoSize();
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    if (mFloatMenuLine.getVisibility() == View.VISIBLE){
                        return false;
                    }
                    mWmParams.x = x;
                    mWmParams.y = y;
                    hideLiner();
                    mWindowManager.updateViewLayout(this, mWmParams);
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mWmParams.x >= mScreenWidth / 2) {
                    mWmParams.x = mScreenWidth;
                    mIsRight = true;
                } else if (mWmParams.x < mScreenWidth / 2) {
                    mIsRight = false;
                    mWmParams.x = 0;
                }
                refreshFloatMenu(mIsRight);
                timerForHide();
                mWindowManager.updateViewLayout(this, mWmParams);
                mTouchStartX = mTouchStartY = 0;
                break;
        }
        return false;
    }

    private void removeTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
        }
    }

    //正常化
    public void normalize(){
        try {
            if(!isSmall&&mFloatMenuLine.getVisibility() == VISIBLE) {
                hideLiner();
                resetLogoSize();
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                //removeTimerTask();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        }
    }

    public void hide() {
        try {
            setVisibility(View.GONE);
            Message message = mTimerHandler.obtainMessage();
            message.what = HANDLER_TYPE_HIDE_LOGO;
            mTimerHandler.sendMessage(message);
            removeTimerTask();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        }
    }

    public void show() {
        setVisibility(View.VISIBLE);
        isInitingLoader = true;
        resetLogoSize();
        mWmParams.alpha = 1f;
        mWindowManager.updateViewLayout(this, mWmParams);
        //
        if (mShowLoader) {
            mShowLoader = false;
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f));
            animationSet.addAnimation(new AlphaAnimation(0.5f, 1.0f));
            animationSet.setDuration(500);
            animationSet.setInterpolator(new AccelerateInterpolator());
            animationSet.setFillAfter(false);
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFloatLoaderImv.setVisibility(VISIBLE);
                    Animation rotaAnimation = new RotateAnimation(0f, +360f, Animation.RELATIVE_TO_PARENT,
                            0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
                    rotaAnimation.setInterpolator(new LinearInterpolator());
                    rotaAnimation.setRepeatCount(Animation.INFINITE);
                    rotaAnimation.setDuration(800);
                    rotaAnimation.setRepeatMode(Animation.RESTART);
                    mFloatLoaderImv.startAnimation(rotaAnimation);
                    cancleAnim();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mFloatLogoImv.startAnimation(animationSet);
            timerForHide();
        }
        if (Constants.IS_CHECKACC) {
            UserUtil.checkAccount();
            Constants.IS_CHECKACC = !Constants.IS_CHECKACC;
        }
    }

//    public void startLoaderAnim() {
//        resetLogoSize();
//        isActionLoading = true;
//        removeTimerTask();
//        Animation rotaAnimation = new RotateAnimation(0f, +360f, Animation.RELATIVE_TO_PARENT,
//                0.5f, Animation.RELATIVE_TO_PARENT, 0.5f);
//        rotaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//        rotaAnimation.setRepeatCount(Animation.INFINITE);
//        rotaAnimation.setDuration(800);
//        rotaAnimation.setRepeatMode(Animation.RESTART);
//        mFloatLoaderImv.startAnimation(rotaAnimation);
//
//    }
//
//    public void changeLogo(int logoDrawableRes, int msgDrawableRes, int menuItemPosition) {
//        mFloatLogoImv.setImageResource(logoDrawableRes);
//        mMenuItemViews.get(menuItemPosition).setImageView(msgDrawableRes);
//    }
//
//
//    public void stopLoaderAnim() {
//        isActionLoading = false;
//        mFloatLoaderImv.clearAnimation();
//        mFloatLoaderImv.setVisibility(View.GONE);
//        timerForHide();
//    }

    public void cancleAnim() {
        mShowLoader = false;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mTimerHandler.sendEmptyMessage(HANDLER_TYPE_CANCEL_ANIM);
            }
        }, 3000);
    }


    /**
     * 刷新float view menu
     * @param right right
     * 左边时：悬浮球logo的大小是50dp，所有当左右切换时，需要将菜单中的第一个item对应悬浮的位置右移50dp
     * 右侧同理
     */
    @SuppressLint("RtlHardcoded")
    private void refreshFloatMenu(boolean right) {
        int padding = menuUtils.dp2Px(4, mContext);
        int padding58 = menuUtils.dp2Px(58, mContext);
        int count = mMenuItemViews.size();

        LayoutParams lineParams = (LayoutParams) mFloatMenuLine.getLayoutParams();
        lineParams.gravity = Gravity.CENTER_VERTICAL;
        mFloatMenuLine.setLayoutParams(lineParams);

        if (right) {
            LayoutParams paramsFloatImage = (LayoutParams) mFloatLogoImv.getLayoutParams();
            paramsFloatImage.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
            mFloatLogoImv.setLayoutParams(paramsFloatImage);


            LayoutParams mFloatLoaderImvLayoutParams = (LayoutParams) mFloatLoaderImv.getLayoutParams();
            mFloatLoaderImvLayoutParams.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
            mFloatLoaderImv.setLayoutParams(mFloatLoaderImvLayoutParams);


            LayoutParams paramsFlFloat = (LayoutParams) mFloatLogoFra.getLayoutParams();
            paramsFlFloat.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
            mFloatLogoFra.setLayoutParams(paramsFlFloat);

            for (int i = 0; i < count; i++) {
                MenuItemView menuItemView = mMenuItemViews.get(i);
                if (i == count - 1) {
                    //最后一个(logo在右边)
                    LinearLayout.LayoutParams paramsMenuClose = (LinearLayout.LayoutParams) menuItemView.getLayoutParams();
                    paramsMenuClose.rightMargin = padding58;
                    paramsMenuClose.leftMargin = padding;
                    menuItemView.setLayoutParams(paramsMenuClose);
                } else {
                    LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) menuItemView.getLayoutParams();
                    paramsMenuAccount.rightMargin = padding;
                    paramsMenuAccount.leftMargin = padding;
                    menuItemView.setLayoutParams(paramsMenuAccount);
                }
            }
        } else {
            LayoutParams params = (LayoutParams) mFloatLogoImv.getLayoutParams();
            params.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
            mFloatLogoImv.setLayoutParams(params);


            LayoutParams mFloatLoaderImvLayoutParams = (LayoutParams) mFloatLoaderImv.getLayoutParams();
            mFloatLoaderImvLayoutParams.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
            mFloatLoaderImv.setLayoutParams(mFloatLoaderImvLayoutParams);


            LayoutParams paramsFlFloat = (LayoutParams) mFloatLogoFra.getLayoutParams();
            paramsFlFloat.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
            mFloatLogoFra.setLayoutParams(paramsFlFloat);

            for (int i = 0; i < count; i++) {
                MenuItemView menuItemView = mMenuItemViews.get(i);
                if (i == 0) {
                    LinearLayout.LayoutParams paramsMenuClose = (LinearLayout.LayoutParams) menuItemView.getLayoutParams();
                    paramsMenuClose.rightMargin = padding;
                    paramsMenuClose.leftMargin = padding58;
                    menuItemView.setLayoutParams(paramsMenuClose);
                } else {
                    LinearLayout.LayoutParams paramsMenuAccount = (LinearLayout.LayoutParams) menuItemView.getLayoutParams();
                    paramsMenuAccount.rightMargin = padding;
                    paramsMenuAccount.leftMargin = padding;
                    menuItemView.setLayoutParams(paramsMenuAccount);
                }
            }
        }
    }

    /**
     */
    private void timerForHide() {
        if (isActionLoading) {
            Log.e("", "加载动画正在执行,不能启动隐藏悬浮的定时器");
            return;
        }
        isInitingLoader = true;
        //结束任务
        if (mTimerTask != null) {
            try {
                mTimerTask.cancel();
                mTimerTask = null;
            } catch (Exception ignored) {
            }
        }
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mTimerHandler.obtainMessage();
                message.what = HANDLER_TYPE_HIDE_LOGO;
                mTimerHandler.sendMessage(message);
            }
        };
        if (isInitingLoader) {
            if (mTimer != null) {
                mTimer.schedule(mTimerTask, 6000, 3000);
            }
        }
    }

    public void destroy() {
        hide();
        removeFloatView();
        removeTimerTask();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        try {
            mTimerHandler.removeMessages(1);
        } catch (Exception ignored) {
        }
    }


}