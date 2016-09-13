package com.mrwind.common.framework.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mrwind.common.bean.Result;
import com.mrwind.common.constant.ConfigConstant;
import com.mrwind.common.util.HttpUtil;

@Aspect
@Component
public class CommonAspect {

	// 本地异常日志记录对象
	private static final Logger logger = LoggerFactory.getLogger(CommonAspect.class);
	
	

	@Pointcut("@annotation(com.mrwind.common.annotation.IPFilter)")
	public void ipFilter() {
	}

	@Pointcut("@annotation(com.mrwind.common.annotation.AuthorizationFilter)")
	public void authorization() {

	}

	@Around("authorization()")
	public Result doAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		String authorizationToken = request.getHeader("AuthorizationFilter");
		if (StringUtils.isEmpty(authorizationToken)) {
			response.setStatus(401);
			return Result.error("权限错误，已被服务器拒绝");
		}

		authorizationToken = authorizationToken.substring(6);
		String userId = HttpUtil.getUserIdByToken(authorizationToken);
		if (StringUtils.isEmpty(userId)) {
			response.setStatus(401);
			return Result.error("权限错误，已被服务器拒绝");
		}
		return (Result) joinPoint.proceed();
	}

	@Around("ipFilter()")
	public Result doIPFilter(ProceedingJoinPoint joinPoint) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();

		String ip = getRemoteHost(request);
		logger.debug("aop filter ip =>" + ip);
		Boolean filter = true;
		Integer iS_FILTER = ConfigConstant.IS_FILTER;
		if (iS_FILTER == 1) {
			int indexOf = ip.indexOf(ConfigConstant.FILTER_IP);
			filter = indexOf > -1;
		}
		if (!filter) {
			response.setStatus(404);
			logger.warn("服务器拒绝了" + ip + "的访问");
			return Result.error("服务器拒绝了这次访问");
		}
		return (Result) joinPoint.proceed();

	}

	public static String getRemoteHost(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

}
