package com.mrwind.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by usj-zhh on 2015/7/25.
 */
public class CodeUtils {


    /**
     * 生成指定长度的数字+字母混合的字符串
     * @param length 验证码长度
     * @return
     */
    public static String genCode(int length) {
        char[] ss = new char[length];
        int[] flag = {0,0,0}; //A-Z, a-z, 0-9
        int i=0;
        while(flag[0]==0 || flag[1]==0 || flag[2]==0 || i<length) {
            i = i%length;
            int f = (int) (Math.random()*3%3);
            if(f==0)
                ss[i] = (char) ('A'+Math.random()*26);
            else if(f==1)
                ss[i] = (char) ('a'+Math.random()*26);
            else
                ss[i] = (char) ('0'+Math.random()*10);
            if(i==4||i==9||i==14){
                ss[i] = '-';
            }
            flag[f]=1;
            i++;
        }
        return new String(ss);
    }

    /**
     * 生成指定长度的数字验证码
     * @param length
     * @return
     */
    public static String genSimpleCode(int length) {
        char[] ss = new char[length];
        int i=0;
        while( i<length) {
            ss[i] = (char) ('0'+Math.random()*10);
            i++;
        }
        return new String(ss);
    }


    public static String genOrderNum(){
        String orderNum = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        orderNum = "100"+format.format(new Date())+genSimpleCode(5);
        return orderNum;
    }

}
