package com.xiaosw.api.util;

import com.xiaosw.api.logger.Logger;

import java.security.MessageDigest;

/**
 * des ：
 * created by ：wuchangbin
 * created on：2017/2/15 16:40
 */
public class ParseMD5Util {

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description:  32位小写MD5
     */
    public static String parseStrToMd5L32(String str){
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes){
                int bt = b&0xff;
                if (bt < 16){
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (Exception e) {
            Logger.e(e);
            return reStr;
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description: 32位大写MD5
     */
    public static String parseStrToMd5U32(String str){
        String reStr = null;
        try {
            reStr = parseStrToMd5L32(str);
            if (reStr != null){
                reStr = reStr.toUpperCase();
            }
        } catch (Exception e) {
            Logger.e(e);
            return reStr;
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description: 16位大写MD5
     */
    public static String parseStrToMd5U16(String str){
        String reStr = null;
        try {
            reStr = parseStrToMd5L32(str);
            if (reStr != null){
                reStr = reStr.toUpperCase().substring(8, 24);
            }
        } catch (Exception e) {
            Logger.e(e);
            return reStr;
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Date: 2013-9-6
     * @Author: lulei
     * @Description: 16位小写MD5
     */
    public static String parseStrToMd5L16(String str){
        String reStr = null;
        try {
            reStr = parseStrToMd5L32(str);
            if (reStr != null){
                reStr = reStr.substring(8, 24);
            }
        } catch (Exception e) {
            Logger.e(e);
            return reStr;
        }
        return reStr;
    }
}
