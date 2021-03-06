package com.mrwind.common.constant;

import org.springframework.core.PriorityOrdered;

public class ConfigConstant implements PriorityOrdered{

	public static String API_JAVA_HOST = "http://dev.api.gomrwind.com:5000/" ;
	
	public static String API_DFENG_XIN_HOST = "http://123.57.45.209:5000/";

	public static String API_WECHAT_HOST = "http://dev.wechat.123feng.com:10060/";

	public static Integer IS_FILTER=0;
	
	public static String FILTER_IP="";
	
	public static String OUR_HOST = "1";
	
	public void setAPI_JAVA_HOST(String aPI_JAVA_HOST) {
		API_JAVA_HOST = aPI_JAVA_HOST;
	}
	public void setAPI_DFENG_XIN_HOST(String aPI_DFENG_XIN_HOST) {
		API_DFENG_XIN_HOST = aPI_DFENG_XIN_HOST;
	}
	public static void setOUR_HOST(String oUR_HOST) {
		OUR_HOST = oUR_HOST;
	}
	public void setAPI_WECHAT_HOST(String aPI_WECHAT_HOST) {
		API_WECHAT_HOST = aPI_WECHAT_HOST;
	}
	
	@Override
	public int getOrder() {
		return PriorityOrdered.HIGHEST_PRECEDENCE;
	}

	public static void setIS_FILTER(Integer iS_FILTER) {
		IS_FILTER = iS_FILTER;
	}

	public static void setFILTER_IP(String fILTER_IP) {
		FILTER_IP = fILTER_IP;
	}
}
