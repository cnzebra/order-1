package com.mrwind.common.spring;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.annotation.JsonToObject;

/**
 * Created by lilemin on 16/5/12.
 */
public class JsonParamArgumentResolver implements HandlerMethodArgumentResolver {

    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(JsonToObject.class)) {
            return false;
        }
        return true;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        JSONObject json = getJsonFormRequestBody(webRequest);
        return JSONObject.toJavaObject(json, parameter.getParameterType());
    }
    private JSONObject getJsonFormRequestBody(NativeWebRequest webRequest){
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        StringBuffer jb = new StringBuffer();
        String line = null;
        JSONObject jsonObject = new JSONObject();
        try {
            BufferedReader reader = servletRequest.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            jsonObject = JSONObject.parseObject(jb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
