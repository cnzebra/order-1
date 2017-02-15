package com.mrwind.common.spring.mongo.converters;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DoubleToBigDecimalConverter implements Converter<Double, BigDecimal> {

    @Override
    public BigDecimal convert(Double source) {
    	return BigDecimal.valueOf(source);
    }
}