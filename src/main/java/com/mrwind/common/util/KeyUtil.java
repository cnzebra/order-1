package com.mrwind.common.util;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2015/10/7 0007.
 */
public class KeyUtil {
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Numbers.toString(hi | (val & (hi - 1)), Numbers.MAX_RADIX)
                .substring(1);
    }


    /**
     * 以62进制（字母加数字）生成19位UUID，最短的UUID
     *
     * @return
     */
    public static String uuid_old() {
        UUID uuid = UUIDUtils.create();
        StringBuilder sb = new StringBuilder();
        sb.append(digits(uuid.getMostSignificantBits() >> 32, 8));
        sb.append(digits(uuid.getMostSignificantBits() >> 16, 4));
        sb.append(digits(uuid.getMostSignificantBits(), 4));
        sb.append(digits(uuid.getLeastSignificantBits() >> 48, 4));
        sb.append(digits(uuid.getLeastSignificantBits(), 12));
        return sb.toString();
    }

    public static String uuid() {
        UUID uuid = UUIDUtils.create();
        String str[] = uuid.toString().split("-");
        StringBuilder sb = new StringBuilder();
        sb.append(str[2]);
        sb.append(str[1]);
        sb.append(str[0]);
        sb.append(str[3]);
        sb.append(str[4]);
        return sb.toString();
    }

    public static Long getScore(String uuid){
        if(uuid!=null)
            return Long.parseLong(uuid.substring(0, 16), 16);
        return new Long(0);
    }

    public static Long getScoreWithDate(Date date){
        return date.getTime();
    }
}
