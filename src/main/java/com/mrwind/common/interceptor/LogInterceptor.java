package com.mrwind.common.interceptor;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mrwind.common.util.DateUtils;

@Component("logInterceptor")
public class LogInterceptor implements HandlerInterceptor {

	private static Log logger = LogFactory.getLog(LogInterceptor.class);
	private static final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocal StartTime");
	
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		// 打印JVM信息。
		if (logger.isDebugEnabled()){
			long beginTime = startTimeThreadLocal.get();//得到线程绑定的局部变量（开始时间）  
			long endTime = System.currentTimeMillis(); 	//2、结束时间  
	        logger.debug("计时结束："+new SimpleDateFormat("hh:mm:ss.SSS").format(endTime)+"  耗时："+DateUtils.formatDateTime(endTime - beginTime)
	        		+"  URI: "+request.getRequestURI()+"  最大内存: "+Runtime.getRuntime().maxMemory()/1024/1024
	        		+"m  已分配内存: "+Runtime.getRuntime().totalMemory()/1024/1024+"m  已分配内存中的剩余空间: "+Runtime.getRuntime().freeMemory()/1024/1024
	        		+"m  最大可用内存: "+(Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory())/1024/1024+"m"); 
		}
		
	}

	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2) throws Exception {
		if (logger.isDebugEnabled()){
			long beginTime = System.currentTimeMillis();//1、开始时间  
	        startTimeThreadLocal.set(beginTime);		//线程绑定变量（该数据只有当前请求的线程可见）  
	        logger.debug("开始计时: "+new SimpleDateFormat("hh:mm:ss.SSS").format(beginTime)
	        		+"  URI: "+request.getRequestURI());
		}
		return true;
	}

}
