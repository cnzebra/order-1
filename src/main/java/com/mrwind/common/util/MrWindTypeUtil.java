package com.mrwind.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.util.TypeUtils;

public class MrWindTypeUtil extends TypeUtils {

	public static Date castToDate(Object value) {
		if (value == null) {
			return null;
		}

		if (value instanceof Date) { // 使用频率最高的，应优先处理
			return (Date) value;
		}

		if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		}

		long longValue = -1;

		if (value instanceof Number) {
			longValue = ((Number) value).longValue();
			return new Date(longValue);
		}

		if (value instanceof String) {
			String strVal = (String) value;

			JSONScanner dateLexer = new JSONScanner(strVal);
			try {
				if (dateLexer.scanISO8601DateIfMatch(false)) {
					Calendar calendar = dateLexer.getCalendar();
					return calendar.getTime();
				}
			} finally {
				dateLexer.close();
			}

			if (strVal.startsWith("/Date(") && strVal.endsWith(")/")) {
				String dotnetDateStr = strVal.substring(6, strVal.length() - 2);
				strVal = dotnetDateStr;
			}

			if (strVal.indexOf('-') != -1) {
				String format;
				if (strVal.length() == JSON.DEFFAULT_DATE_FORMAT.length()) {
					format = JSON.DEFFAULT_DATE_FORMAT;
				} else if (strVal.length() == 10) {
					format = "yyyy-MM-dd";
				} else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
					format = "yyyy-MM-dd HH:mm:ss";
				} else if (strVal.length() == "2016-01-05T15:06:58+0800".length()) {
					format = "yyyy-MM-dd'T'HH:mm:ssZ";
				} else {
					format = "yyyy-MM-dd HH:mm:ss.SSS";
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat(format, JSON.defaultLocale);
				dateFormat.setTimeZone(JSON.defaultTimeZone);
				try {
					return (Date) dateFormat.parse(strVal);
				} catch (ParseException e) {
					throw new JSONException("can not cast to Date, value : " + strVal);
				}
			}

			if (strVal.length() == 0) {
				return null;
			}

			longValue = Long.parseLong(strVal);
		}
		return new Date(longValue);
	}
}
