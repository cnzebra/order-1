package com.mrwind.common.util;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

public class JsonUtil {
	
	public static Object filterProperty(Object object,String propertys){
		if(StringUtils.isEmpty(propertys)){
			return object;
		}
		SimplePropertyPreFilter filter=new SimplePropertyPreFilter(propertys.split(","));
		String res = JSON.toJSONString(object,filter);
		return JSONObject.parse(res);
	}

}
