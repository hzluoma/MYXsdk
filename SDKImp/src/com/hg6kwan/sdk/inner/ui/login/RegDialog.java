package com.hg6kwan.sdk.inner.ui.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.BaseInfo;
import com.hg6kwan.sdk.inner.platform.ControlCenter;
import com.hg6kwan.sdk.inner.platform.ControlUI;
import com.hg6kwan.sdk.inner.ui.uiState;
import com.hg6kwan.sdk.inner.ui.uiUtils;
import com.hg6kwan.sdk.inner.utils.CommonFunctionUtils;
import com.hg6kwan.sdk.inner.utils.Constants;
import com.hg6kwan.sdk.inner.utils.StringHelper;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by xiaoer on 2016/5/21.
 */
public class RegDialog extends LoginBase implements View.OnClickListener{
    public RegDialog(Context context) {
        super(context);
    }

    private EditText account_et;
    private EditText password_et;
    private Button login_btn;

    private LinearLayout phone_reg_layout;
    private ImageView iv_old_acc;

    BaseInfo baseInfo = ControlCenter.getInstance().getBaseInfo();
    String account = "";
    String password = "";

    final boolean[] isChecked = {true};//同意协议

    //删除输入框的小功能
//    private ImageView deleteAccount_iv;
//    private ImageView deletePassword_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back_layout.setVisibility(View.INVISIBLE);
        close_layout.setVisibility(View.INVISIBLE);

        login_btn.setOnClickListener(this);
        phone_reg_layout.setOnClickListener(this);
        iv_old_acc.setOnClickListener(this);

        initData();//查看参数，设置账号密码

        //清空输入框
//
//        deleteAccount_iv.setOnClickListener(this);
//        deletePassword_iv.setOnClickListener(this);
        //设置删除按钮的可见性
//        account_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                String str = account_et.getText().toString();
//                if(hasFocus){
//                    if(!TextUtils.isEmpty(str))
//                        deleteAccount_iv.setVisibility(View.VISIBLE);
//                }else{
//                    deleteAccount_iv.setVisibility(View.GONE);
//                }
//            }
//        });
//        password_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                String str = password_et.getText().toString();
//                if(hasFocus){
//                    if(!TextUtils.isEmpty(str))
//                        deletePassword_iv.setVisibility(View.VISIBLE);
//                }else{
//                    deleteAccount_iv.setVisibility(View.GONE);
//                }
//            }
//        });

        //设置注册

        this.setCancelable(false);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                uiState.gIsRegsiter = false;
            }
        });
    }

    private void initData() {
        account = baseInfo.regName ;
        password = baseInfo.regPassword ;
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
             account = CommonFunctionUtils.getRandomAccount();
             password = CommonFunctionUtils.getRandomPassword();
        }
        account_et.setText(account);
        password_et.setText(password);
    }

    @Override
    public void onClick(View v) {
        String acc  = account_et.getText().toString().trim();
        String psd = password_et.getText().toString().trim();
        baseInfo.regName = account ;

        if(login_btn == v){
            if(!isChecked[0]){
                Toast.makeText(mContext,"必须同意协议",Toast.LENGTH_SHORT).show();
                return;
            }
            String result = checkInput(acc,psd);
            if(result!=null){
                Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();
            }else {
                //截屏保存用户的账号密码
                screenshot();
                //开始注册、通知服务端
                ControlUI.getInstance().doLoadingReg(acc,psd);
            }
        }else if(v == phone_reg_layout ) {
            new CommonFunctionUtils().checkPermission();
            //到手机注册界面
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.REG_PHONE);
        }else if (v == iv_old_acc){
            baseInfo.regName = "";
            baseInfo.regPassword = "";
            ControlUI.getInstance().startUI(mContext, ControlUI.LOGIN_TYPE.LOGIN);
        }

//        else if(v==deleteAccount_iv){
//            account_et.setText(null);
//            password_et.setText(null);
//            deleteAccount_iv.setVisibility(View.GONE);
//            deletePassword_iv.setVisibility(View.GONE);
//        }else if(v==deletePassword_iv){
//            password_et.setText(null);
//            deletePassword_iv.setVisibility(View.GONE);
//        }
    }

    private String checkInput(String account , String password1){
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password1))
            return "账号或密码都不能为空！";
        if(account.length() < 4){
            return "账号不能低于4位字符";
        }
        if(password1.length() < 6)
            return "密码不能低于6位字符";

        if (password1.equals(account))
            return "账号和密码不能一样！";

        try {
            if(StringHelper.hasChinese(account)){
                return "账号密码中不能包含中文";
            }
            if(StringHelper.hasSpace(account) || StringHelper.hasSpace(password1)){
                return "账号或密码中不能包含空格";
            }

        }catch (Exception e){
            e.printStackTrace();
            return "账号密码输入错误";
        }
        return null;
    }

    /////////////////////////////////画界面////////////////////////////////////
    @Override
    protected LinearLayout createContent(Context context) {
        //reg
        final float[] reg_content_weight_v =
                new float[]{2f,0.3f,0.5f,0.5f,  1f,0.5f,0.65f,0.5f};

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //输入框
        LinearLayout input_layout =
                uiUtils.createLayout(uiUtils.LAYOUT.INPUT,context);
        input_layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout input_ly_account = new LinearLayout(context);
        LinearLayout input_ly_password = new LinearLayout(context);
        input_ly_account.setOrientation(LinearLayout.HORIZONTAL);
        input_ly_password.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout iv_user = createImageLayout("user",2,context);
        LinearLayout iv_lock = createImageLayout("lock",2,context);

        account_et = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);
        password_et = uiUtils.createInput(uiUtils.INPUT.ACCOUT,context);
        account_et.setTextSize(ajustFontSize(size_input));
        password_et.setTextSize(ajustFontSize(size_input));
        password_et.setHint("密码");

        input_ly_account.addView(iv_user,getLayoutParamH(1));
        input_ly_account.addView(account_et,getLayoutParamH(input_weight_h -1));

        input_ly_password.addView(iv_lock,getLayoutParamH(1));
        input_ly_password.addView(password_et,getLayoutParamH(input_weight_h -1));

        input_layout.addView(input_ly_account,getLayoutParamV(1));
        input_layout.addView(uiUtils.createLayout(uiUtils.LAYOUT.INPUT_LINE,context));
        input_layout.addView(input_ly_password,getLayoutParamV(1));

        //同意协议
//        LinearLayout treaty_layout = new LinearLayout(context);
        LinearLayout treaty_layout = createTreatyLayout(context);
        //注册按钮
        login_btn =
                uiUtils.createButton(uiUtils.BTN.LOGIN,context);
        login_btn.setText("一键注册");
        login_btn.setTextSize(ajustFontSize(size_btn_login));

        //跳转按钮
        LinearLayout jump_layout = new LinearLayout(context);
        jump_layout.setOrientation(LinearLayout.HORIZONTAL);

//        LinearLayout old_account_layout = createImageLayout(R.drawable.btn_hasacc,15,context);
        phone_reg_layout = createImageLayout("btn_phonereg",3,context);

        iv_old_acc = new ImageView(context);
        iv_old_acc.setImageBitmap(uiUtils.getResBitmap("btn_hasacc"));
//
//        ImageView iv_phone_reg = new ImageView(context);
//        iv_phone_reg.setImageResource(R.drawable.btn_phonereg);

        jump_layout.addView(iv_old_acc,getLayoutParamH(3.5f));
        jump_layout.addView(new LinearLayout(context),getLayoutParamH(5));
        jump_layout.addView(phone_reg_layout,getLayoutParamH(2.5f));

        layout.addView(input_layout,getLayoutParamV(reg_content_weight_v[0]));
        layout.addView(new LinearLayout(context),getLayoutParamV(reg_content_weight_v[1]));
        layout.addView(treaty_layout,getLayoutParamV(reg_content_weight_v[2]));
        layout.addView(new LinearLayout(context),getLayoutParamV(reg_content_weight_v[3]));

        layout.addView(login_btn,getLayoutParamV(reg_content_weight_v[4]));
        layout.addView(new LinearLayout(context),getLayoutParamV(reg_content_weight_v[5]));
        layout.addView(jump_layout,getLayoutParamV(reg_content_weight_v[6]));
        layout.addView(new LinearLayout(context),getLayoutParamV(reg_content_weight_v[7]));
        return layout;
    }

    private LinearLayout createTreatyLayout(Context context){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout img_layout = new LinearLayout(context);
        img_layout.setOrientation(LinearLayout.VERTICAL);
        img_layout.setGravity(Gravity.CENTER_VERTICAL);
        img_layout.setWeightSum(10);

        final ImageView iv_png = new ImageView(context);
        iv_png.setImageBitmap(uiUtils.getResBitmap("treaty_red"));
        img_layout.addView(iv_png,getImageLayoutParamV(12));

        final TextView tv1 = new TextView(context);
        tv1.setText(" 我已阅读并同意漫布游戏");
        tv1.setTextColor(0xff777777);
        tv1.setTextSize(ajustFontSize(size_treaty));
        tv1.setSingleLine(true);

        TextView tv2 = new TextView(context);
        tv2.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        tv2.setTextColor(Color.RED);
        tv2.setText("用户协议");
        tv2.setClickable(true);
        tv2.setTextSize(ajustFontSize(size_treaty));
        tv2.setSingleLine(true);

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ControlUI.getInstance().startUI(ControlUI.WEB_TYPE.USER_AGREEMENT);
            }
        });

        layout.setWeightSum(15);
        layout.addView(img_layout,getLayoutParamH(1.3f));
        layout.addView(tv1);
        layout.addView(tv2);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChecked[0]){
                    iv_png.setImageBitmap(uiUtils.getResBitmap("treaty_gray"));
                    isChecked[0] = false;
                }else {
                    iv_png.setImageBitmap(uiUtils.getResBitmap("treaty_red"));
                    isChecked[0] = true;
                }
            }
        });
        return layout;
    }

    //截屏
    private void screenshot() {
        // 获取屏幕
        View dView = getWindow().getDecorView();
        dView.setDrawingCacheEnabled(true);
        dView.buildDrawingCache();
        Bitmap bmp = dView.getDrawingCache();
        if (bmp != null) {
            try {
                // 获取SD卡中Picture的存储路径
                String picPath = Environment.getExternalStorageDirectory().getPath() +
                        "/Pictures/6kw";
                // 图片文件路径
                String filePath = picPath + File.separator + System.currentTimeMillis() +
                        ".png";

                //临时保存截屏文件路径,如果登录不成功就删除
                Constants.SCREEN_SHOT_FILE = filePath;

                //如果Picture文件下6kw文件夹不存在就创建
                File dir = new File(picPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(filePath);
                FileOutputStream os = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();
//                通知图库更新
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(file);
                intent.setData(uri);
                mContext.sendBroadcast(intent);
            } catch (Exception e) {

            }
        }
    }
}
