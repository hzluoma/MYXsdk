package com.hg6kwan.sdk.inner.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.hg6kwan.sdk.inner.base.InitInfo;
import com.hg6kwan.sdk.inner.base.LoginInfo;
import com.hg6kwan.sdk.inner.context.ApplicationContext;
import com.hg6kwan.sdk.inner.log.LogUtil;
import com.hg6kwan.sdk.inner.platform.ControlCenter;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * @author Zsh
 */
public class CommonFunctionUtils {

    private static final String TAG = "CommonFunctionUtils";

    /**
     * versionCode
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    /**
     * versionName
     *
     * @param context
     * @return
     */
    public static String getVersion(Context context) {
        String retVal = "$Revision: df3d962eabe7 $";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            retVal = pi.versionName;
        } catch (NameNotFoundException ex) {
            LogUtil.e(TAG, "Can not find this application, really strange." + ex);
        }
        return retVal;
    }


    /**
     * SDCard
     *
     * @return
     */
    public static boolean isSDCardExist() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String appName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return appName;
    }


    /**
     * @param context
     * @return
     */
    public static Drawable getAppIcon(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        Drawable appIcon = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

            appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return appIcon;
    }


    /**
     * @param context
     * @return
     */
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }
        return true;
    }


    /**
     * @param context
     * @return
     */
    public static long getIPAdress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        long ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0)
            ipAddress = 1001;
        return ipAddress;
    }

    /**
     * @param context
     * @return
     */
    public static String getStringIPAdress(Context context) {
        long ip = getIPAdress(context);
        return ((ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF));
    }

    //
//    public static String getLocalIpAddress() {
//        try {
//            String ipv4;
//            ArrayList<NetworkInterface> mylist = Collections
//                    .list(NetworkInterface.getNetworkInterfaces());
//
//            for (NetworkInterface ni : mylist) {
//
//                ArrayList<InetAddress> ialist = Collections.list(ni
//                        .getInetAddresses());
//                for (InetAddress address : ialist) {
//                    if (!address.isLoopbackAddress()
//                            && InetAddressUtils.isIPv4Address(ipv4 = address
//                            .getHostAddress())) {
//                        return ipv4;
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//
//        }
//        return null;
//    }

    public static long ip2int(String ip) {
        String[] items = ip.split("\\.");
        return Long.valueOf(items[0]) << 24
                | Long.valueOf(items[1]) << 16
                | Long.valueOf(items[2]) << 8 | Long.valueOf(items[3]);
    }


    /**
     * @param file
     * @param context
     */
    public static void install(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    /**
     * @param context
     * @return height*width
     */
    public static String getPhoneDisplay(Context context) {
        try {
            DisplayMetrics localDisplayMetrics = new DisplayMetrics();
            WindowManager localWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            localWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);

            int i = localDisplayMetrics.widthPixels;
            int j = localDisplayMetrics.heightPixels;

            return String.valueOf(j) + "*" + String.valueOf(i);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return "Unknown";
    }


    /**
     * 创建玩家UUID
     *
     * @param ctx
     */
    public static String creatUUID(Context ctx) {

        Boolean permission = isPermission(ctx);
        // 判断是否有权限
        if (permission) {
            TelephonyManager mTeleManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            ApplicationContext.shareContext().UDID = android.provider.Settings.System.getString(ctx.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID) + "-" + mTeleManager.getDeviceId();

            return ApplicationContext.shareContext().UDID;

        } else {
            //初始化SP文件工具类
            SPUtil spUtil = new SPUtil(ctx, "common");
            String imeiCode = spUtil.getString("imeiCode", "");
            //在6.0以上的设备,万一没有获取到ReadPhoneState这个权限,就根据时间戳和随机字符串生成一个唯一标识码,并保存在SP文件
            //假如是第一次初始化就生成唯一标识
            if ("".equals(imeiCode)) {
                Random random = new Random(100);
                // 获取当前时间戳
                long timeMillis = System.currentTimeMillis() + random.nextInt();

                return MD5.getMD5String(timeMillis + "");
            }
            //之前已经生成过imeiCode就直接返回
            return imeiCode;
        }
    }


    /**
     * 清除窗口
     *
     * @param dialog
     */
    public static void cancelDialog(Dialog dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 是否模拟器
     *
     * @param context
     * @return
     */
    public static boolean isEmulator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (imei == null || imei.equals("000000000000000")) {
            return true;
        }
        return false;
    }

    /**
     * 删除目录
     *
     * @param path
     */
    public static void deleteDir(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            if (file.listFiles().length == 0) {
                file.delete();
            } else {
                File delFile[] = file.listFiles();
                int i = file.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        deleteDir(delFile[j].getAbsolutePath());
                    }
                    delFile[j].delete();
                }
                file.delete();
            }
        }
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deleteSubFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File subFile : files) {
                    if (subFile.isDirectory())
                        deleteDir(subFile.getAbsolutePath());
                    else
                        subFile.delete();
                }
            }
        }
    }


    /**
     * 退出应用
     *
     * @param context
     */
    @SuppressWarnings("deprecation")
    public static void exitApp(Context context) {
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
            System.exit(0);
        } else {// android2.1
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            am.restartPackage(context.getPackageName());
        }
    }

    /**
     * 得到application节点中的META_DATA的值
     */
    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData.containsKey(key)) {
                return appInfo.metaData.get(key).toString();
            } else {
                return null;
            }
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public static String getLogicChannel(Context context, String prefix) {
        String channel = "10000";
        String channel1 = null;
        String channel2 = null;

        //将读取到的未解密值和解密后的值赋值给InitInfo
        InitInfo instance = InitInfo.getInstance();

        ApplicationInfo appInfo = context.getApplicationInfo();
        String sourceDir = appInfo.sourceDir;
        ZipFile zip = null;
        Enumeration e;
        String ret = null;

        try {

            zip = new ZipFile(sourceDir);
            e = zip.entries();
            while (e.hasMoreElements()) {
                ZipEntry split = (ZipEntry) e.nextElement();
                String entryName = split.getName();
                if (entryName.startsWith("META-INF/" + "@&#^%")) {
                    instance.setLogicChannel(entryName);
                    String[] splits = entryName.split("_");
                    if (splits != null && splits.length >= 2) {
                        String result = entryName.substring(splits[0].length() + 1);
                        ret = new String(Base64.decode(result));
                        channel1 = checkChannel(ret);
                        instance.setChannel_ID(channel1);
                    }
                } else if (entryName.startsWith("META-INF/" + prefix)) {
                    instance.setU8OldChannel(entryName);
                    ret = entryName;
                    channel2 = checkChannel(ret);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
        if (channel1 != null && channel2 != null) {
            if (channel1.equalsIgnoreCase(channel2)) {
                channel = channel1;
            } else {
                channel = "-10000";
            }
        }
        Log.e("channel", "" + channel);
        return channel;
    }

    private static String checkChannel(String ret) {
        String result = "";
        if (!TextUtils.isEmpty(ret)) {
            String[] splits = ret.split("_");
            if (splits != null && splits.length >= 2) {
                String s = ret.substring(splits[0].length() + 1);
                if (isNumeric(s)) {
                    result = s;
                } else {
                    result = "-10000";
                }
            }
        }
        return result;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 获取随机账号
     * 10位随机数字
     *
     * @return
     */
    public static String getRandomAccount() {
        return String.valueOf((int) (Math.random() * 9))
                + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9))
                + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9))
                + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9) + String.valueOf((int) (Math.random() * 9))
                + String.valueOf((int) (Math.random() * 9))
        );
    }

    /**
     * 获取随机密码
     * 6位随机数字
     *
     * @return
     */
    public static String getRandomPassword() {
        return String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9))
                + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9)) + String.valueOf((int) (Math.random() * 9));
    }

    /**
     * 设置些属性(get &&　set )
     *
     * @param context
     * @param key
     * @param value
     */
    public static void setSharePreferences(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SDK_PRES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringKeyForValue(Context context, String key) {
        SharedPreferences mSettings = context.getSharedPreferences(Constants.SDK_PRES_FILE, 0);
        String value = mSettings.getString(key, "");
        return value;
    }

    public static void setSharePreferences(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SDK_PRES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getStringKeyForBoolValue(Context context, String key) {
        SharedPreferences mSettings = context.getSharedPreferences(Constants.SDK_PRES_FILE, 0);
        Boolean value = mSettings.getBoolean(key, true);
        return value;
    }


    /**
     * 保存信息
     *
     * @param context
     * @param info    1 acc 2 psw
     */
    public static void setLogiInfoToSharePreferences(Context context, String[] info) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SDK_PRES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.QIQU_ACCOUNT, info[0]);
        try {
            editor.putString(Constants.QIQU_PASSWORD, AESUtils.encrypt(Constants.AES_ENCODE_KEY, info[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.commit();
    }


    /**
     * 获取登录信息
     */
    public static LoginInfo getLoginInfoFromSharePreferences(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(Constants.SDK_PRES_FILE, Context.MODE_PRIVATE);
        LoginInfo info = new LoginInfo();
        //获取返回解密后的密码
        try {
            String acc = mSettings.getString(Constants.QIQU_ACCOUNT, "");
            String psw = mSettings.getString(Constants.QIQU_PASSWORD, "");
            if (!TextUtils.isEmpty(psw)) {
                psw = AESUtils.decrypt(Constants.AES_ENCODE_KEY, psw);
            }
            info.setU(acc);
            info.setP(psw);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        return info;
    }

    //根据account删除保存在LoginList中的信息
    public static void delete_loginList(Context context, ArrayList<LoginInfo> list, String account) {
        if (list == null || list.size() == 0) return;
        int flag = 0;
        for (LoginInfo info : list) {
            if (info.getU() == account)
                break;
            flag++;
        }
        if (flag != list.size()) {
            list.remove(flag);
            setLoginListToSharePreferences(context, list);
        }
    }

    public static void update_loginList(Context context, ArrayList<LoginInfo> list, String account, String password) {
        if (list.size() <= 0) {
            list.add(new LoginInfo(account, password));
        } else {
            int flag = 0;
            boolean isTip = true;
            for (LoginInfo info : list) {
                if (info.getU().equals(account)) {
                    isTip = info.isTip();
                    break;
                }
                flag++;
            }

            if (flag >= list.size()) {
                if (flag >= Constants.LOGIN_ACCOUNT_MAX) {
                    list.remove(0);
                }
                list.add(new LoginInfo(account, password, isTip));
            } else {
                list.remove(flag);
                list.add(new LoginInfo(account, password, isTip));
            }
        }
        setLoginListToSharePreferences(context, list);
    }

    //获取登录信息列表
    public static ArrayList<LoginInfo> getLoginListFromSharePreferences(Context context) {
        SharedPreferences mSettings = context.getSharedPreferences(Constants.SDK_PRES_FILE, Context.MODE_PRIVATE);
        ArrayList<LoginInfo> list = new ArrayList<>();
        try {
            String loginInfoList = mSettings.getString(Constants.QIQU_LOGIN_INFO_LIST, "");
            if (!TextUtils.isEmpty(loginInfoList)) {
                String[] strings = loginInfoList.split(",");
                for (String str : strings) {
                    String[] acc_pwd = str.split(":");
                    String acc = acc_pwd[0];
                    String pwd = acc_pwd[1];
                    boolean isTip = true;
                    //1：true，2：false
                    if (TextUtils.equals(acc_pwd[2], "2")) {
                        isTip = false;
                    }
                    if (!TextUtils.isEmpty(pwd)) {
                        pwd = AESUtils.decrypt(Constants.AES_ENCODE_KEY, pwd);
                    }
                    list.add(new LoginInfo(acc, pwd, isTip));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void setLoginListToSharePreferences(Context context, ArrayList<LoginInfo> list) {
        SharedPreferences settings = context.getSharedPreferences(Constants.SDK_PRES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        StringBuilder sb = new StringBuilder();
        try {
            for (LoginInfo info : list) {
                String pwd = TextUtils.isEmpty(info.getP()) ? "" : AESUtils.encrypt(Constants.AES_ENCODE_KEY, info.getP());
//                String str = info.getU()+":"+info.getP();
                sb.append(info.getU());
                sb.append(":");
                sb.append(pwd);
                sb.append(":");
                String tipStr = info.isTip() ? "1" : "2";
                sb.append(tipStr);
                sb.append(",");
            }
            if (sb.length() > 1) {
                sb.deleteCharAt(sb.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putString(Constants.QIQU_LOGIN_INFO_LIST, sb.toString());
        editor.commit();
    }


    /**
     * SDK 通讯加密
     *
     * @param service
     * @param appID
     * @param appKey
     * @param data
     * @return
     */
    public static String getSignString(String service, String appID, String appKey, JSONObject data) {
        //
        try {
            StringBuilder sb = new StringBuilder();
            ArrayList<String> array = new ArrayList();
            Iterator iterator = data.keys();
            while (iterator.hasNext()) {
                array.add(iterator.next().toString());
            }
            Object[] keys = array.toArray();
            Arrays.sort(keys);
            //构造SIGN
            sb.append(appID);
            sb.append(service);
            String key = null;
            String split = "";
            for (int i = 0; i < keys.length; i++) {
                key = keys[i].toString();
                sb.append(split).append(key).append("=").append(data.getString(key));
                split = "&";
            }
            sb.append(appKey);
            String str = sb.toString();
            return MD5.getMD5String(URLEncoder.encode(new String(str.getBytes(), "UTF8"), "UTF8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSignFloat(String type, String username, String session) {
        StringBuilder sb = new StringBuilder();
        sb.append(type).append(username).append(session).append(Constants.FLOAT_KEY);
        String str = sb.toString();
        try {
            return MD5.getMD5String(URLEncoder.encode(new String(str.getBytes(), "UTF8"), "UTF8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 弹出PHP服务端返回的错误信息
     */
    public static void showErrorMsg(Context context, final String Msg) {
        try {
            Toast.makeText(context, Msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
        }
    }

    /**
     * 获取Php的返回码
     */
//    public static int getReturnCodeByPhpCode(String code){
//        //
//        if (TextUtils.isEmpty(code)){
//            return ReturnCode.COM_PLATFORM_ERROR_UNKNOWN;
//        }
//        //
//        int icode;
//        switch (Integer.valueOf(code)){
//            case 1:
//                icode = ReturnCode.COM_PLATFORM_SUCCESS;
//                break;
//            case 0:
//                icode = ReturnCode.COM_PLATFORM_ERROR_UNKNOWN;
//                break;
//            default:
//                icode = ReturnCode.COM_PLATFORM_ERROR_UNKNOWN;
//                break;
//        }
//        return icode;
//    }
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static String getMode() {
        return Build.MODEL;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }


    // 判断当前版本是否是6.0
    public static Boolean isPermission(Context context) {

        // 判断手机版本是否大于等于23
        if (Build.VERSION.SDK_INT >= 23) {
            LogUtil.d("TAG", "当前版本是6.0以上");
            // 判断是否已经获取到权限
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                // 进入到这里表示有权限
                return true;
            } else {
                // 表示没有获取到权限
                return false;
            }

        } else {
            return true;

        }
    }
    //检查是否有获取权限并申请权限
    public void checkPermission() {
        //当系统版本大于等于6.0的时候就会去检查权限是否获取
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            //是否获取了写入内存的权限,true就是没有,false就是有
            Boolean is_StoragePermission_Get = ContextCompat.checkSelfPermission(ControlCenter
                    .getInstance().getmContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED;
            ////是否获取了获得电话信息的权限,true就是没有,false就是有
            Boolean is_PhoneStatePermision_Get = ContextCompat.checkSelfPermission(ControlCenter
                    .getInstance().getmContext(),
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED;

            if (is_StoragePermission_Get && is_PhoneStatePermision_Get) {
                //如果都没有获取到权限就一起申请
                ActivityCompat.requestPermissions((Activity) ControlCenter.getInstance().getmContext(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest
                                .permission.READ_PHONE_STATE},
                        100);
                //检查是否其中一个权限没有获取
            } else if (is_StoragePermission_Get) {
                //申请写入内存的权限
                ActivityCompat.requestPermissions((Activity) ControlCenter.getInstance()
                        .getmContext(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            } else if (is_PhoneStatePermision_Get) {
                //申请获取电话状态的权限
                ActivityCompat.requestPermissions((Activity) ControlCenter.getInstance()
                                .getmContext(),
                        new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
            }
            //如果都申请到权限就什么都不做
        }
    }

}
