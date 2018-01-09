package com.hg6kwan.sdk.inner.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*************************************************
 * MD5 算法的Java/JSP Bean JAVA/JSP MD5 Author: www.web745.com JAVA/JSP MD5
 * javabean 为免费发布，但转载请保留原作者信息，谢谢 Last Modified:10,Mar,2001 md5 类实现了RSA Data
 * Security, Inc.在提交给IETF 的RFC1321中的MD5 message-digest 算法。
 *************************************************/

public class MD5 {
	/** 
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合 
     */  
    protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',  
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
  
    protected static MessageDigest messagedigest = null;  
    static {  
        try {  
            messagedigest = MessageDigest.getInstance("MD5");  // 获取md5 加密对象
        } catch (NoSuchAlgorithmException nsaex) {  
            System.err.println(MD5.class.getName()  
                    + "初始化失败，MessageDigest不支持MD5Util。");  
            nsaex.printStackTrace();  
        }  
    }  
      
    /** 
     * 生成字符串的md5校验值 
     *  
     * @param s 
     * @return 
     */  
    public static String getMD5String(String s) {  
        return getMD5String(s.getBytes());  
    }  
    
    public   static  String getMD5String( byte [] bytes) {  
        messagedigest.update(bytes); 
        return  bufferToHex(messagedigest.digest());  
    } 
    
    private   static  String bufferToHex( byte  bytes[]) {  
        return  bufferToHex(bytes,  0 , bytes.length);  
    }  
  
    private   static  String bufferToHex( byte  bytes[],  int  m,  int  n) {  
        StringBuffer stringbuffer = new  StringBuffer( 2  * n);  
        int  k = m + n;  
        for  ( int  l = m; l < k; l++) {  
            appendHexPair(bytes[l], stringbuffer);  
        }  
        return  stringbuffer.toString();  
    }   
    
    private   static   void  appendHexPair( byte  bt, StringBuffer stringbuffer) {
        char  c0 = hexDigits[(bt &  0xf0 ) >>  4 ]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char  c1 = hexDigits[bt &  0xf ]; // 取字节中低 4 位的数字转换
        stringbuffer.append(c0);  
        stringbuffer.append(c1);  
    }
    // 微信MD5加密工具
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits2[d1] + hexDigits2[d2];
    }


    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }


    private static final String hexDigits2[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}

