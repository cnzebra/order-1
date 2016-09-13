package com.mrwind.common.annotation;

import java.lang.annotation.*;

/**
 * Created by lilemin on 16/5/12.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonToObject {
    String value() default "";
}
