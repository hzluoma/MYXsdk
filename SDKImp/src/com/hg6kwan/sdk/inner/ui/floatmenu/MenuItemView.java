package com.hg6kwan.sdk.inner.ui.floatmenu;


import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hg6kwan.sdk.inner.ui.uiUtils;

/**
 * Created by xiaoer on 2016/11/1.
 * 用于构造悬浮菜单中的按钮，目前只有一张图片
 */
public class MenuItemView extends ImageView {
    public MenuItem getMenuItem() {
        return mMenuItem;
    }
    private MenuItem mMenuItem;

    private static int mGapSize = 40;

    public MenuItemView(Context context, MenuItem menuItem) {
        super(context);
        this.mMenuItem = menuItem;
        init(context);
    }

    private void init(Context context) {
        LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(
                menuUtils.dp2Px(mGapSize, context), menuUtils.dp2Px(mGapSize, context));
        lineLp.gravity = Gravity.CENTER;
        setVisibility(VISIBLE);
        setLayoutParams(lineLp);
        setImageBitmap(uiUtils.getResBitmap(mMenuItem.getIcon()));
    }
}
