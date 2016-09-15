package com.mrwind.common.spring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mrwind.common.annotation.JsonPathVariable;
import com.mrwind.common.wrapper.JsonObjectWrapper;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import javax.servlet.ServletException;

/**
 * Created by Administrator on 2015/10/12 0012.
 */
public class JsonHandlerMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver  {



    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter methodParameter) {
        JsonPathVariable jsonPathVariable = methodParameter.getParameterAnnotation(JsonPathVariable.class);
        return new PathVariableNamedValueInfo(jsonPathVariable);
    }

    @Override
    protected Object resolveName(String s, MethodParameter methodParameter, NativeWebRequest nativeWebRequest) throws Exception {
        String uriTempVar = nativeWebRequest.getParameter(s);
        if(uriTempVar==null||uriTempVar.equalsIgnoreCase(""))
            return new JsonObjectWrapper(new JSONObject());
        return new JsonObjectWrapper(JSON.parseObject(uriTempVar));
    }

    @Override
    protected void handleMissingValue(String s, MethodParameter methodParameter) throws ServletException {
        throw new ServletRequestBindingException("Missing URI template variable '" + s +
                "' for method parameter of type " + methodParameter.getParameterType().getSimpleName());
    }

    public boolean supportsParameter(MethodParameter methodParameter) {
        if (!methodParameter.hasParameterAnnotation(JsonPathVariable.class)) {
            return false;
        }
        return true;
    }


    private static class PathVariableNamedValueInfo extends NamedValueInfo {
        public PathVariableNamedValueInfo(JsonPathVariable annotation) {
            super(annotation.value(), true, ValueConstants.DEFAULT_NONE);
        }
    }
}
