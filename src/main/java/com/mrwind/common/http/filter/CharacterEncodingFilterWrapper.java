package com.mrwind.common.http.filter;

import com.mrwind.common.http.wrapper.MyHttpServletRequestWrapper;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CharacterEncodingFilterWrapper extends CharacterEncodingFilter{

	private String encoding;
	private boolean forceEncoding; 
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		  // 设置请求响应字符编码
        request.setCharacterEncoding(encoding);
        response.setCharacterEncoding(encoding);
        // 新增加的代码
        HttpServletRequest req = (HttpServletRequest) request;

        if (req.getMethod().equalsIgnoreCase("get")) {
            req = new MyHttpServletRequestWrapper(request, encoding);
        }
        filterChain.doFilter(req, response);
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	@Override
	public void setForceEncoding(boolean forceEncoding) {
		this.forceEncoding = forceEncoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public boolean isForceEncoding() {
		return forceEncoding;
	}
	
	
	
	
}
