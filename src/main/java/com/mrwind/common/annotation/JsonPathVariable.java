package com.mrwind.common.annotation;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2015/10/12 0012.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonPathVariable {
    String value() default "";
}
