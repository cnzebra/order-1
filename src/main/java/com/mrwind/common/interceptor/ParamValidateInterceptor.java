package com.mrwind.common.interceptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component("paramValidateInterceptor")
public class ParamValidateInterceptor implements HandlerInterceptor {

	@SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(ParamValidateInterceptor.class);
	
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		
//		if(HandlerMethod.class.equals(handler.getClass())){
//
//			HandlerMethod method = (HandlerMethod) handler;
//		}
//
		return true;
	}

	public String URLEncode(String str){
		String rt = "";
		try {
			rt = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		rt = str;
		return rt;
	}
	
}
