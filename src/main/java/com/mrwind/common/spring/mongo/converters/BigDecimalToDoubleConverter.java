package com.mrwind.common.spring.mongo.converters;

import java.math.BigDecimal;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalToDoubleConverter implements Converter<BigDecimal, Double> {

    @Override
    public Double convert(BigDecimal source) {
        return source.doubleValue();
    }
}