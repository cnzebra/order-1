package com.mrwind.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by admin on 16/7/1.
 */
public class QueryDateUtils {

    static Log log = LogFactory.getLog(QueryDateUtils.class);

    public static Map<String, Date> getCurrentDatePeriod() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("startOfToday", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        dateMap.put("endOfToday", calendar.getTime());
        return dateMap;
    }

    public static Map<String, Date> getFrontDatePeriod() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("startOfToday", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        dateMap.put("endOfToday", calendar.getTime());
        return dateMap;


    }

    public static Date getFrontDate(int i){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, i);
        return calendar.getTime();
    }

    public static Map<String, Date> getCurrentDatePeriod(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("startOfToday", calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        dateMap.put("endOfToday", calendar.getTime());
        return dateMap;


    }

    /**
     * 改变小时
     * @param date
     * @param i 正数为添，负数为减
     * @return
     */
    public static Date changeHour(Date date, int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, i);

        return calendar.getTime();

    }

    /**
     * 日期修改分钟
     * @param date
     * @param min
     * @return
     */
    public static Date changeMins(Date date, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, +min);
        return calendar.getTime();

    }

    /**
     * 添加毫秒
     * @param date
     * @param millisecond
     * @return
     */
    public static Date getPreSecPeriod(Date date, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, millisecond);

        return calendar.getTime();

    }

    public static Date getHoursPeriod(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);

        return calendar.getTime();
    }

    public static Date dateMaker(String intime) {//将08:10的时间转换为当前日期的(2016-08-01 08:10)
        String[] hourminute = intime.split(":");
        int hour = Integer.parseInt(hourminute[0]);
        int minute = Integer.parseInt(hourminute[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    public static Date dateMaker(String intime, int min) {//将08:10的时间转换为当前日期的(2016-08-01 08:10)
        String[] hourminute = intime.split(":");
        int hour = Integer.parseInt(hourminute[0]);
        int minute = Integer.parseInt(hourminute[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, -min);
        Date date = calendar.getTime();
        return date;
    }

    public static Date dateMaker(Date startDate, String intime) {//将yyyy-MM-dd的时间添加时分
        String[] hourminute = intime.split(":");
        int hour = Integer.parseInt(hourminute[0]);
        int minute = Integer.parseInt(hourminute[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 转换成yyyy-MM-dd格式的字符串
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }


    public static Date yearMonthDayMaker(String intime) {//将08:10的时间转换为当前日期的(2016-08-01 08:10)
        Date calDate = null;
        try {
            String[] date = intime.split("-");
            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            calDate = calendar.getTime();
        } catch (Exception e) {
            log.error("日期格式错误", e);
        }
        return calDate;


    }


    public static String dateSecondFromater(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);


    }

    public static String dateDayFromater(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);


    }

    public static String dateMonthFormater(Date date) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(date);


    }


    /**
     * 判断某一时间是否在一个区间内
     *
     * @param sourceTime
     *            时间区间,半闭合,如[10:00-20:00)
     * @param curTime
     *            需要判断的时间 如10:00
     * @return
     * @throws IllegalArgumentException
     */
    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            }
            else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }

    }

    public static Map<String, Date> getDatePeriod(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 9, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Map<String, Date> dateMap = new HashMap<>();
        dateMap.put("startOfToday", calendar.getTime());
        calendar.set(2016, 9, day + 1);
        dateMap.put("endOfToday", calendar.getTime());
        return dateMap;


    }

    /**
     * 比较HH:MM格式字符串的大小
     * 后面时间大则返回true
     * @param frontTime
     * @param endTime
     * @return
     */
    public static boolean compareHm(String frontTime, String endTime){
        boolean result = false;
        String[] beginArr = frontTime.split(":");
        String[] endArr = endTime.split(":");
        int beginH = Integer.parseInt(beginArr[0]);
        int beginM = Integer.parseInt(beginArr[1]);

        // 截取结束时间时分
        int endH = Integer.parseInt(endArr[0]);
        int endM = Integer.parseInt(endArr[1]);

        if(endH > beginH){
            result = true;
        }else if(endH == beginH && endM >= beginM){
            result = true;
        }else{
            result = false;
        }
        return result;
    }

    public static Date getHoursPeriod(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
