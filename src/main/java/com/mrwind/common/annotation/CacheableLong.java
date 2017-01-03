package com.mrwind.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解 存入数据近缓存
 * 此注解只能用于做long类型
 * @author 周杰
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheableLong {
    String key();
    String fieldKey() ;
    int expireTime() default 3600;
    public KeyMode keyMode() default KeyMode.DEFAULT;       //key的后缀模式  
    
    public enum KeyMode{  
        DEFAULT,    //只有加了@CacheKey的参数,才加入key后缀中  
        BASIC,      //只有基本类型参数,才加入key后缀中,如:String,Integer,Long,Short,Boolean  
        ALL;        //所有参数都加入key后缀  
    }  
}