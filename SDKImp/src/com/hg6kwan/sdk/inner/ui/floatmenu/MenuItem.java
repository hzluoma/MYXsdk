package com.hg6kwan.sdk.inner.ui.floatmenu;

import android.view.View;

public class MenuItem {
    public enum TYPE{
        NEWS,
        SERVICE,
        GIFT,
        GAME,
        USER,
        LOGOUT
    }

    private TYPE type;

    private String icon;

    private int diameter = 50;
    private View.OnClickListener onClickListener;

    public MenuItem(TYPE type, View.OnClickListener listener){
        this.type = type;
        this.onClickListener = listener;
        switch (type){
            case NEWS:
                this.icon = "menu_news";
                break;
            case SERVICE:
                this.icon = "menu_service";
                break;
            case GIFT:
                this.icon = "menu_gift";
                break;
            case GAME:
                this.icon = "menu_game";
                break;
            case USER:
                this.icon = "menu_user";
                break;
            case LOGOUT:
                this.icon = "menu_logout";
                break;
        }
    }


    public TYPE getType() {
        return this.type;
    }

    public String getIcon() {
        return  icon;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
