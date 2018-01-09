package com.hg6kwan.sdk.inner.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 一些字符串的工具
 * @author Zsh
 *
 */
public class StringHelper {

    /**
     * Pad string in tail
     * @param padString
     * @param minLength
     * @param padChar
     * @return
     */
    public static String padTrailing(String padString, int minLength, String padChar) {
        String tempPad = "";
        int lCnt = 0;

        String tempString = checkNull(padString);

        if (tempString.length() >= minLength)
            return tempString;
        else {
            for (lCnt = 1; lCnt <= minLength - tempString.length(); lCnt++) {
                tempPad = padChar + tempPad;
            }
        }
        return tempString + tempPad;
    }

    /**
     * Pad string in lead
     * @param padString
     * @param minLength
     * @param padChar
     * @return
     */
    public static String padLeading(String padString, int minLength, String padChar) {
        String tempPad = "";
        int lCnt = 0;

        String tempString = checkNull(padString);

        if (tempString.length() >= minLength)
            return tempString;
        else {
            for (lCnt = 1; lCnt <= minLength - tempString.length(); lCnt++) {
                tempPad = tempPad + padChar;
            }
        }
        return tempPad + tempString;
    }

    /**
     * Cur string according to the length
     * @param inputString
     * @param length
     * @return
     */
    public static String subString(String inputString, int length) {

        String tempString = checkNull(inputString);

        if (tempString.length() >= length)
            return tempString.substring(0, length);
        else {
            return tempString;
        }
    }

    /**
     * Check if the string contains space
     * @param inputString
     * @return
     */
    public static boolean hasSpace(String inputString) {
        boolean returnFlag = true;

        if (inputString.indexOf(" ") < 0)
            returnFlag = false;

        return returnFlag;
    }

    /**
     * Check if the string consist of alpha
     * @param inputString
     * @return
     */
    public static boolean isAlpha(String inputString) {
        int counter;

        for (counter = 0; counter < inputString.length(); counter++) {
            if (inputString.charAt(counter) >= 'a' && inputString.charAt(counter) <= 'z') continue;
            if (inputString.charAt(counter) >= 'A' && inputString.charAt(counter) <= 'Z') continue;
            return false;
        }
        return true;
    }

    /**
     * Check if the charater is alpha
     * @param inputChar
     * @return
     */
    public static boolean isAlpha(char inputChar) {
        if (inputChar >= 'a' && inputChar <= 'z') {
            return true;
        }
        if (inputChar >= 'A' && inputChar <= 'Z') {
            return true;
        }

        return false;
    }

    /**
     * Conver the string from one encoding to another
     * @param inputString
     * @param oldEncode
     * @param newEncode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String converEncode(String inputString, String oldEncode,
                                      String newEncode) throws UnsupportedEncodingException {

        String returnString = "";


        if (inputString == null) return returnString;
        try {
            returnString = new String(inputString.getBytes(oldEncode), newEncode);
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        return returnString;
    }

    /**
     * Conver bytes to hex string
     * @param inputByte
     * @return
     */
    public static String byteToHex(byte inputByte) {

        char hexDigitArray[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        char[] tempArray = {hexDigitArray[(inputByte >> 4) & 0x0f],
                            hexDigitArray[inputByte & 0x0f]};

        return new String(tempArray);
    }

    /**
     * Get binary length of string
     * @param inputString
     * @return
     */
    public static int bin2int(String inputString) {
        int index;
        int tempLength;
        int returnLength = 0;
        byte[] tempArray;

        if (inputString == null) return returnLength;

        tempArray = inputString.getBytes();
        tempLength = tempArray.length;
        for (index = 0; index < tempLength; returnLength = (returnLength * 2) + tempArray[index] - '0', index++) ;

        return returnLength;
    }

    /**
     * Conver byte to binary
     * @param inputByte
     * @return
     */
    public static String byte2bin(byte inputByte) {
        char lbinDigit[] = {'0', '1'};
        char[] lArray = {lbinDigit[(inputByte >> 7) & 0x01],

                         lbinDigit[(inputByte >> 6) & 0x01],
                         lbinDigit[(inputByte >> 5) & 0x01],
                         lbinDigit[(inputByte >> 4) & 0x01],
                         lbinDigit[(inputByte >> 3) & 0x01],
                         lbinDigit[(inputByte >> 2) & 0x01],
                         lbinDigit[(inputByte >> 1) & 0x01],
                         lbinDigit[inputByte & 0x01]};

        return new String(lArray);
    }

    /**
     * Check if the string contains chinese
     * @param inputString
     * @return
     * @throws UnsupportedEncodingException
     */
    public static boolean hasChinese(String inputString) throws UnsupportedEncodingException {
        byte[] tempArray;
        String tempCode = new String();
        boolean returnFlag = false;

        try {
//            lUniStr = new String(inputString.getBytes(), "big5");
            tempArray = inputString.getBytes("UnicodeBig");
        } catch (UnsupportedEncodingException e) {
            throw e;
        }
        for (int lk = 0; lk < tempArray.length; lk++) {
            if (tempArray[lk] == 0) {
                lk++;
            } else {
                tempCode = byteToHex(tempArray[lk++]) +
                        byteToHex(tempArray[lk]);

                if (!tempCode.equals("feff")) {
                    returnFlag = true;
                    break;
                }
            }
        }
        return returnFlag;
    }

    /**
     * Replace the special char in html
     * @param inputString
     * @return
     */
    public static String fixHTML(String inputString) {

        int index = 0;
        char tempChar;
        StringBuffer stringBuffer;


        if (inputString == null) return "";
        if ((inputString.trim()).equals("")) return inputString;

        stringBuffer = new StringBuffer(inputString);

        while (index < stringBuffer.length()) {
            if ((tempChar = stringBuffer.charAt(index)) == '"') {
                stringBuffer.replace(index, index + 1, "&#34;");
                index += 5;
                continue;
            }
            if (tempChar == '\'') {  //"'"
                stringBuffer.replace(index, index + 1, "&#39;");
                index += 5;
                continue;
            }
            if (tempChar == '&') {
                try {
                    if ((tempChar = stringBuffer.charAt(index + 1)) == '#') {
                        index += 2;
                        continue;
                    }
                } catch (StringIndexOutOfBoundsException ex) {
                    //needn't to do anything
                }

                stringBuffer.replace(index, index + 1, "&amp;");
                index += 5;
                continue;
            }
            if (tempChar == '<') {
                stringBuffer.replace(index, index + 1, "&lt;");
                index += 4;
                continue;
            }
            if (tempChar == '>') {
                stringBuffer.replace(index, index + 1, "&gt;");
                index += 4;
                continue;
            }
            index++;
        }
        return stringBuffer.toString();
    }

    /**
     * Conver "'" to "''"
     * @param inputString
     * @return
     */
    public static String fixSQL(String inputString) {

        int index = 0;
        StringBuffer stringBuffer;

        if (inputString == null) return "";
        if ((inputString.trim()).equals("")) return inputString;

        stringBuffer = new StringBuffer(inputString);

        while (index < stringBuffer.length()) {
            if (stringBuffer.charAt(index) == '\'') {
                stringBuffer.replace(index, index + 1, "''");
                index += 2;
                continue;
            }
            index++;
        }
        return stringBuffer.toString();
    }


    /**
     * Check if the string is null, if yes return ""
     * @param rStr
     * @return
     */
    public static String checkNull(String rStr) {
        if (rStr == null)
            return "";
        else
            return rStr.trim();
    }

    /**
         * Check if the string is null, if yes return ""
         * @param rStr
         * @return
         */
        public static String checkNull(String rStr,String defaultValue) {
            if (rStr == null || rStr.equals(""))
                return defaultValue;
            else
                return rStr.trim();
        }

    /**
     * Trim the string if it 's not null
     * @param rStr
     * @return
     */
    public static String trim(String rStr) {
        if (rStr == null) {
            return rStr;
        } else {
            return rStr.trim();
        }
    }


    /**
     * Replace substring in the input string
     * @param inputString
     * @param beReplaced
     * @param replaceTo
     * @return
     */
    public static String replaceString(String inputString, String beReplaced, String replaceTo) {
        int index = 0;
        String returnString = "";

        returnString = inputString;

        do {
            index = inputString.indexOf(beReplaced, index);

            if (index == -1) break;

            returnString = inputString.substring(0, index) + replaceTo + inputString.substring(index + beReplaced.length());
            index += replaceTo.length();
            inputString = returnString;

        } while (true);

        return returnString.substring(0, returnString.length());
    }

    /**
     * 判断是否数字形式
     * @param inputString
     * @return
     */
    public static boolean isNumeric(String inputString) {
        int counter;

        if (inputString == null || inputString.trim().length() == 0) {
            return false;
        }

        for (counter = 0; counter < inputString.length(); counter++) {
            if (!(inputString.charAt(counter) >= '0' && inputString.charAt(counter) <= '9')) {
                return false;
            }
        }
        return true;
    }


    /**
     * 分裂为数组（List形式）
     * @param inputString
     * @param delimiter
     * @return
     */
    public static ArrayList<String> split(String inputString, String delimiter) {
    	ArrayList<String> returnVector = new ArrayList<String>();
        int position = -1;

        position = inputString.indexOf(delimiter);
        while (position >= 0) {
            if (position > 0)
                returnVector.add(inputString.substring(0, position));

            inputString = inputString.substring(position + delimiter.length());
            position = inputString.indexOf(delimiter);
        }

        if (!inputString.equals(""))
            returnVector.add(inputString);

        return returnVector;

    }

    /**
     * 分裂为数组
     * @author Zsh
     * @param inputString
     * @param delimiter
     * @return
     */
    public static String[] splitToArray(String inputString, String delimiter) {
        List<String> arrayList = new ArrayList<String>();
        String[] result;
        int position = -1;

        if (inputString == null) return new String[0];

        position = inputString.indexOf(delimiter);
        while (position >= 0) {
            //if (position > 0)
                arrayList.add(inputString.substring(0, position));

            inputString = inputString.substring(position + delimiter.length());
            position = inputString.indexOf(delimiter);
        }

        if (!inputString.equals(""))
            arrayList.add(inputString);

        result = new String[arrayList.size()];
        arrayList.toArray(result);
        return result;

    }


    /**
     * Replace some special character in the input string
     * @param inputString
     * @return
     */
    public static String fixWML(String inputString) {
        int index = 0;
        char ch;

        if (inputString == null) return "";
        StringBuffer strbuff = new StringBuffer(inputString);

        while (index < strbuff.length()) {
            if ((ch = strbuff.charAt(index)) == '"') {
                strbuff.replace(index, index + 1, "&#34;");
                index += 5;
                continue;
            }

            if (ch == '\'') {
                strbuff.replace(index, index + 1, "&#39;");
                index += 5;
                continue;
            }

            if (ch == '$') {
                strbuff.replace(index, index + 1, "$$");
                index += 2;
                continue;
            }

            if (ch == '&') {
                strbuff.replace(index, index + 1, "&amp;");
                index += 5;
                continue;
            }

            if (ch == '<') {
                strbuff.replace(index, index + 1, "&lt;");
                index += 4;
                continue;
            }

            if (ch == '>') {
                strbuff.replace(index, index + 1, "&gt;");
                index += 4;
                continue;
            }

            index++;
        }

        return strbuff.toString();
    }

    public static String removeString(String tmp, String start, String end) {
        int startPosi = -1;
        int endPosi = -1;
        startPosi = tmp.indexOf(start);
        if (startPosi >= 0) {
            endPosi = tmp.indexOf(end, startPosi);
            if (endPosi >= startPosi) {
                tmp = tmp.substring(0, startPosi) + tmp.substring(endPosi + 2);
            }
        }
        return tmp;
    }

    /**
     * 格式化字符串，如123-->000123
     * @param inStr
     * @param formt
     * @return
     */
    public static String formatString(String inStr,String formt){
    	String rtnStr=null;
    	if(inStr==null || inStr.equals("")){
    		return inStr;
    	}

    	if(inStr.length()>=formt.length()){
    		return inStr;
    	}

    	int subLend = formt.length() - inStr.length();
    	rtnStr = formt.substring(0, subLend) + inStr;

    	return rtnStr;
    }

    /**
     * 连接字符串数组里的元素
     * @author Zsh
     * @param array 目标数组
     * @param delimiter 分隔符
     * @return
     */
    public static String join(String[] array, String delimiter){
    	StringBuffer buffer = new StringBuffer();
    	for(String key : array){
			buffer.append(key);
			if(isNotBlank(delimiter))
				buffer.append(delimiter);
		}
    	String ret = buffer.toString();
    	if(ret.endsWith(delimiter)){
    		ret = ret.substring(0, ret.length() - 1);
    	}
    	return ret;
    }

    /**
     * 连接字符串数组里的元素
     * @author Zsh
     * @param array 目标数组
     * @param delimiter 分隔符
     * @return
     */
    public static String join(Integer[] array, String delimiter){
    	StringBuffer buffer = new StringBuffer();
    	for(int key : array){
			buffer.append(key);
			if(isNotBlank(delimiter))
				buffer.append(delimiter);
		}
    	String ret = buffer.toString();
    	if(ret.endsWith(delimiter)){
    		ret = ret.substring(0, ret.length() - 1);
    	}
    	return ret;
    }

    /**
     * 连接字符串数组里的元素
     * @author Zsh
     * @param array 目标数组
     * @param delimiter 分隔符
     * @return
     */
    public static String join(Collection<?> array, String delimiter){
    	StringBuffer buffer = new StringBuffer();
    	if(array == null) return "";
    	for(Object key : array){
			buffer.append(key);
			if(isNotBlank(delimiter))
				buffer.append(delimiter);
		}
    	String ret = buffer.toString();
    	if(ret.endsWith(delimiter)){
    		ret = ret.substring(0, ret.length() - 1);
    	}
    	return ret;
    }

    /**
     * 判断字符串非null且非空
     * @author Zsh
     * @param input
     * @return
     */
    public static boolean isNotBlank(String input){
    	return input != null && !input.equals("");
    }

    /**
     * 判断字符串为null或为空
     * @param input
     * @return
     */
    public static boolean isBlank(String input) {
    	return input == null || input.equals("");
    }

    /** *
     * 从ip的字符串形式得到字节数组形式
     * @param ip 字符串形式的ip
     * @return 字节数组形式的ip
     */
    public static byte[] getIpByteArrayFromString(String ip) {
        byte[] ret = new byte[4];
        java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
        try {
            ret[0] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte)(Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    /**
     * 对原始字符串进行编码转换，如果失败，返回原始的字符串
     * @param s 原始字符串
     * @param srcEncoding 源编码方式
     * @param destEncoding 目标编码方式
     * @return 转换编码后的字符串，失败返回原始字符串
     */
    public static String getString(String s, String srcEncoding, String destEncoding) {
        try {
            return new String(s.getBytes(srcEncoding), destEncoding);
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    /**
     * 根据某种编码方式将字节数组转换成字符串
     * @param b 字节数组
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, String encoding) {
        try {
            return new String(b, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b);
        }
    }

    /** *
     * 根据某种编码方式将字节数组转换成字符串
     * @param b 字节数组
     * @param offset 要转换的起始位置
     * @param len 要转换的长度
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }

    /** *
     * @param ip ip的字节数组形式
     * @return 字符串形式的ip
     */
    public static String getIpStringFromBytes(byte[] ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(ip[0] & 0xFF);
        sb.append('.');
        sb.append(ip[1] & 0xFF);
        sb.append('.');
        sb.append(ip[2] & 0xFF);
        sb.append('.');
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }

    /**
     * 生成随机字符串
     * @param intLength 字符串的长度
     * @return
     */
    public static String getRandKeys( int intLength ) {

        String retStr = "";
        String strTable = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz@=";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for ( int i = 0; i < intLength; i++ ) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor( dblR );
                char c = strTable.charAt( intR );
                if ( ( '0' <= c ) && ( c <= '9' ) ) {
                    count++;
                }
                retStr += strTable.charAt( intR );
            }
            if ( count >= 2 ) {
                bDone = false;
            }
        } while ( bDone );

        return retStr;
    }
}
