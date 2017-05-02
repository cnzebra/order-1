package com.mrwind.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.client.fluent.Request;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by starmezhh on 16/5/13.
 */
public class Md5Util {

    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    public static String getShortUrl(String long_url) {


       return HttpUtil.short_url("http://api.t.sina.com.cn/short_url/shorten.json?source=2815391962&url_long="
                + URLEncoder.encode(long_url));
    }

    public static void main(String[] args) {
        System.out.println(getShortUrl("http://dev.wechat.123feng.com:10060/#/summary/1"));
//        generateShortUrl("http://dev.fh.123feng.com:10020/");
    }

}
