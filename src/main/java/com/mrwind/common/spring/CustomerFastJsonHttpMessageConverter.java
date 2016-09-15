package com.mrwind.common.spring;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

public class CustomerFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

	   public static SerializeConfig mapping = new SerializeConfig();

	    private String defaultDateFormat;

	    @Override
	    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
	        OutputStream out = outputMessage.getBody();
	        String text = JSON.toJSONString(obj, mapping, super.getFeatures());
	        byte[] bytes = text.getBytes(getCharset());
	        out.write(bytes);
	    }

	    public void setDefaultDateFormat(String defaultDateFormat) {
	        mapping.put(java.util.Date.class, new SimpleDateFormatSerializer(defaultDateFormat));
	    }

		public String getDefaultDateFormat() {
			return defaultDateFormat;
		}
}
