package com.mrwind.common.framework.spring;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import com.mrwind.common.framework.custom.CustomSqlDateEditor;
import com.mrwind.common.framework.custom.CustomTimestampEditor;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA. User: zhangyu Date: 11-1-12 Time: 下午7:56
 */

public class CommonBindingInitializer implements WebBindingInitializer {

	public void initBinder(WebDataBinder binder, WebRequest request) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomSqlDateEditor(dateFormat, true));
		binder.registerCustomEditor(Timestamp.class, new CustomTimestampEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm"), true));
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
	}
}