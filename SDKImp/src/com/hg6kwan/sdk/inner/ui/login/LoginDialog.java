package com.hg6kwan.sdk.inner.ui.login;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;

/**
 * Created by xiaoer on 2016/5/21.
 */
public class LoginDialog extends LoginBase implements View.OnClickListener {

    private static final String TAG = "LoginDialog";

    private EditText account_et;
    private EditText password_et;

    private Button login_btn;
    private ImageView forget_password_btn;
    private ImageView new_uer_btn;

    //删除输入框的小功能
//    private ImageView deleteAccount_iv;
//    private ImageView deletePassword_iv;

    //clickmore
    private TextView tv_acc;
    private LinearLayout ly_close;

    private PopupWindow pop;
    private LinearLayout mLoginMore;
    private boolean isShow = false;
    private LoginHistoryAdapter mAdapter;
    private ListView mListView;
    private LinearLayout input_ly_account;

    private BaseInfo baseInfo;

    public LoginDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back_layout.setVisibility(View.INVISIBLE);
        close_layout.setVisibility(View.INVISIBLE);

        baseInfo = ControlCenter.getInstance().getBaseInfo();

        String account=null;
        String password=null;
        if(baseInfo.login!=null){
            account=baseInfo.login.getU();
            password=baseInfo.login.getP();
        }

        account_et.setText(account);
        password_et.setText(password);

        //clickmore
        mLoginMore.setOnClickListener(this);
        login_btn.setOnClickListener(this);
        forget_password_btn.setOnClickListener(this);
        new_uer_btn.setOnClickListener(this);

        this.setCancelable(false);
    }


    @Override
    public void dismiss() {
        closePopWindow();
        super.dismiss();
    }

    private void closePopWindow() {
        if(isShow && pop!=null){
            pop.dismiss();
            pop=null;
            isShow=false;
        }
    }

    class LoginHistoryAdapter extends BaseAdapter {
        public LoginHistoryAdapter(){
            super();
        }

        @Override
        public int getCount() {
            return baseInfo.loginList==null?0:baseInfo.loginList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView==null){
                holder = new ViewHolder();
                convertView = createLoginMore(mContext);
                holder.del_iv = ly_close;
                holder.account_tv = tv_acc;
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            if(holder!=null){
                int len = baseInfo.loginList.size()-1;
                final String name = baseInfo.loginList.get(len-position).getU();
                final String password = baseInfo.loginList.get(len-position).getP();
                convertView.setId(position);
                holder.setId(position);
                holder.account_tv.setText(name);
                holder.account_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        account_et.setText(name);
                        password_et.setText(password);
                        closePopWindow();
                    }
                });

                holder.del_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(mContext).setMessage("您确定要删除： "+ name +"的账号信息吗？")
                                .setCancelable(false)
                                .setPositiveButton("确定", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CommonFunctionUtils.delete_loginList(mContext,baseInfo.loginList,name);
                                        String acc = "";
                                        String pass = "";
                                        if(baseInfo.login!=null){
                                            acc = baseInfo.login.getU();
                                            pass = baseInfo.login.getP();
                                        }
                                        if(baseInfo.loginList.isEmpty()){
                                            closePopWindow();
                                        }else{
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }).setNegativeButton("取消", null).create().show();
                    }
                });
            }
            return convertView;
        }

        class ViewHolder {
            public LinearLayout del_iv;
            public TextView account_tv;
            void setId(int position){
                del_iv.setId(position);
                account_et.setId(position);
            }
        }
    }

    @Override
    public void onClick(View v) {
        //登录
        if(v==login_btn){
            do_login();
        }else if(v==new_uer_btn){
            //跳转到注册页面(ControlCenter.register)
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.REG);
        }else if(v==forget_password_btn){
            //忘记密码(改成web了？)
            try {
                ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.FORGET);
            }catch (Exception e){}

        }else if(v == mLoginMore){
            clickLoginMore();
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void clickLoginMore() {
        if(baseInfo.loginList==null || baseInfo.loginList.size()<1){
            return;
        }
        if(pop == null){
            if(mAdapter==null){
                mAdapter=new LoginHistoryAdapter();
            }
            mListView=new ListView(mContext);

            GradientDrawable gd = new GradientDrawable();
            gd.setColor(0xfaffffff);
            gd.setCornerRadii(new float[]{0,0,0,0,15,15,15,15});
            gd.setStroke(1,0xFFD2D2D2);
            mListView.setBackground(gd);

            mListView.setDivider(new ColorDrawable(0xffc0c3c4));
            mListView.setDividerHeight(1);
            pop=new PopupWindow(mListView, input_ly_account.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            mListView.setAdapter(mAdapter);
            pop.setBackgroundDrawable(new ColorDrawable());
            pop.setOutsideTouchable(true);
            pop.showAsDropDown(input_ly_account,0,1);
            isShow = true;
        }else if(isShow){
            pop.dismiss();
            isShow=false;
        }else if(!isShow){
            pop.showAsDropDown(input_ly_account,0,1);
            isShow=true;
        }
    }

    private void do_login() {
        String account = account_et.getText().toString().trim();
        String password = password_et.getText().toString().trim();

        if(checkLoginInputText(account,password)){
            ControlUI.getInstance().doLoadingLogin(account,password);
        }
    }

    public boolean checkLoginInputText(String account, String password){
        if(account == null || account.length() < 4){
            Toast.makeText(mContext, "账号应为4–20个数字，字母或下划线", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.length() <4){
            Toast.makeText(mContext, "密码应至少为4个字符，区分大小写", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /////////////////////////////////画界面////////////////////////////////////

    //loginMore
    private LinearLayout createLoginMore(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout ly_text = new LinearLayout(context);
        ly_text.setOrientation(LinearLayout.VERTICAL);
        ly_text.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);

        tv_acc = new TextView(context);
        tv_acc.setTextColor(0xc0000000);
        tv_acc.setTextSize(ajustFontSize(size_input-1.5f));
        ly_text.addView(tv_acc);

        ly_close = createImageLayout("close_little",2,context);

        layout.addView(new View(context),getLayoutParamH(1));
        layout.addView(ly_text,getLayoutParamH(8));
        layout.addView(ly_close,new LinearLayout.LayoutParams(0,
                (int) (input_ly_account.getHeight()*0.85),2));

        return layout;
    }

    @Override
    protected LinearLayout createContent(Context context) {
        //垂直占比
        final float[] login_content_weight_v =
                new float[]{2f,0.3f,1f,0.3f,    0.6f,0.1f,1f,0.6f};

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //输入框
        LinearLayout input_layout =
                uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        input_layout.setOrientation(LinearLayout.VERTICAL);
        input_ly_account = new LinearLayout(context);
        LinearLayout input_ly_password = new LinearLayout(context);
        input_ly_account.setOrientation(LinearLayout.HORIZONTAL);
        input_ly_password.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout iv_user = createImageLayout("user",2,context);
        LinearLayout iv_lock = createImageLayout("lock",2,context);
        mLoginMore = createImageLayout("arrow_down",0.7f,context);

        account_et = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);
        password_et = uiUtils.createInput(uiUtils.INPUT.PASSWORD,context);

        account_et.setTextSize(ajustFontSize(size_input));
        password_et.setTextSize(ajustFontSize(size_input));

        input_ly_account.addView(iv_user,getLayoutParamH(1));
        input_ly_account.addView(account_et,getLayoutParamH(input_weight_h -2));
        input_ly_account.addView(mLoginMore,getLayoutParamH(1));

        input_ly_password.addView(iv_lock,getLayoutParamH(1));
        input_ly_password.addView(password_et,getLayoutParamH(input_weight_h -1));

        input_layout.addView(input_ly_account,getLayoutParamV(1));
        input_layout.addView(uiUtils.createLayout(uiUtils.LAYOUT.INPUT_LINE,context));
        input_layout.addView(input_ly_password,getLayoutParamV(1));

        //登录按钮
        login_btn =
                uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        login_btn.setText("登 录");
        login_btn.setTextSize(ajustFontSize(size_btn));

        //客服电话
//        LinearLayout phone_layout = new LinearLayout(context);
//        phone_layout.setOrientation(LinearLayout.HORIZONTAL);
//        phone_layout.setWeightSum(10);
//
//        LinearLayout phone_bg = uiUtils.createLayout(uiUtils.LAYOUT.PHONE,context);
//        phone_layout.addView(phone_bg,getLayoutParamH(4.5f));

        //用户注册，忘记密码
        LinearLayout forget_layout = new LinearLayout(context);
        forget_layout.setOrientation(LinearLayout.HORIZONTAL);
        forget_layout.setGravity(Gravity.RIGHT);
        forget_layout.setWeightSum(10);

        new_uer_btn = new ImageView(context);
        new_uer_btn.setImageBitmap(uiUtils.getResBitmap("btn_newreg"));
        forget_password_btn = new ImageView(context);
        forget_password_btn.setImageBitmap(uiUtils.getResBitmap("btn_forget"));

        forget_layout.addView(new_uer_btn,getLayoutParamH(3.2f));
        forget_layout.addView(new View(context),getLayoutParamH(0.2f));
        forget_layout.addView(forget_password_btn,getLayoutParamH(2.8f));


        layout.addView(new View(context),getLayoutParamV(login_content_weight_v[7]));
        layout.addView(input_layout,getLayoutParamV(login_content_weight_v[0]));
        layout.addView(new View(context),getLayoutParamV(login_content_weight_v[1]));
        layout.addView(login_btn,getLayoutParamV(login_content_weight_v[2]));
        layout.addView(new View(context),getLayoutParamV(login_content_weight_v[3]));
//        layout.addView(phone_layout,getLayoutParamV(login_content_weight_v[4]));
//        layout.addView(new View(context),getLayoutParamV(login_content_weight_v[5]));
        layout.addView(forget_layout,getLayoutParamV(login_content_weight_v[6]));
        layout.addView(new View(context),getLayoutParamV(login_content_weight_v[7]));
        return layout;
    }

}
