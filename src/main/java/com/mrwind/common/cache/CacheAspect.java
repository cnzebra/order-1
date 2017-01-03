package com.mrwind.common.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.mrwind.common.annotation.CacheEvict;
import com.mrwind.common.annotation.CacheKey;
import com.mrwind.common.annotation.Cacheable;
import com.mrwind.common.annotation.Cacheable.KeyMode;
import com.mrwind.common.annotation.CacheableLong;
import com.mrwind.common.util.SerializableUtil;

@Aspect
@Component
public class CacheAspect {

	@Resource
	RedisCache redisCache;
	private static Log log = LogFactory.getLog(CacheAspect.class);

	@Pointcut("@annotation(com.mrwind.common.annotation.Cacheable)")
	public void cacheFilter() {
	}

	@Pointcut("@annotation(com.mrwind.common.annotation.CacheableLong)")
	public void cacheLongFilter() {
	}

	/**
	 * 定义缓存逻辑
	 */
	@Around("cacheFilter()")
	public Object cache(ProceedingJoinPoint pjp) {
		Object result = null;
		Method method = getMethod(pjp);
		Cacheable cacheable = method.getAnnotation(Cacheable.class);

		String fieldKey = parseKey(cacheable.fieldKey(), method, pjp.getArgs());
		// 使用redis 的hash进行存取，易于管理
		log.info("feeldKey:" + fieldKey + "    key:" + cacheable.key() + " this is redis get");
		result = redisCache.hget(cacheable.key(), fieldKey);
		if (result == null) {
			try {
				log.info("feeldKey:" + fieldKey + "    key:" + cacheable.key() + " this go to db");
				int expireTime = cacheable.expireTime();
				result = pjp.proceed();
				Assert.notNull(fieldKey);
				redisCache.hset(cacheable.key().getBytes(), fieldKey.getBytes(), SerializableUtil.serialize(result));
				redisCache.expire(cacheable.key(), expireTime);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 定义缓存逻辑
	 */
	@Around("cacheLongFilter()")
	public Long cacheBase(ProceedingJoinPoint pjp) {
		String result = null;
		Method method = getMethod(pjp);
		CacheableLong cacheable = method.getAnnotation(CacheableLong.class);

		String fieldKey = parseKey(cacheable.fieldKey(), method, pjp.getArgs());
		// 使用redis 的hash进行存取，易于管理
		log.info("feeldKey:" + fieldKey + "    key:" + cacheable.key() + " this is redis get");
		result = redisCache.hgetString(cacheable.key(), fieldKey);
		if (result == null || Long.valueOf(result) < 0) {
			try {
				log.info("feeldKey:" + fieldKey + "    key:" + cacheable.key() + " this go to db");
				int expireTime = cacheable.expireTime();
				result = pjp.proceed().toString();
				Assert.notNull(fieldKey);
				redisCache.hset(cacheable.key().getBytes(), fieldKey.getBytes(), result.getBytes());
				redisCache.expire(cacheable.key(), expireTime);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return Long.valueOf(result);
	}

	/** * 定义清除缓存逻辑 */
	@Around(value = "@annotation(com.mrwind.common.annotation.CacheEvict)")
	public Object evict(ProceedingJoinPoint pjp) {
		Method method = getMethod(pjp);
		CacheEvict cacheEvict = method.getAnnotation(CacheEvict.class);

		String fieldKey = parseKey(cacheEvict.fieldKey(), method, pjp.getArgs());

		// 使用redis 的hash进行存取，易于管理
		return redisCache.hdel(cacheEvict.key().getBytes(), fieldKey.getBytes());
	}

	/**
	 * 获取被拦截方法对象
	 * 
	 * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象 而缓存的注解在实现类的方法上
	 * 所以应该使用反射获取当前对象的方法对象
	 */
	@SuppressWarnings("rawtypes")
	public Method getMethod(ProceedingJoinPoint pjp) {
		// 获取参数的类型
		Object[] args = pjp.getArgs();
		Class[] argTypes = new Class[pjp.getArgs().length];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		Method method = null;
		try {
			method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), argTypes);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return method;

	}

	/**
	 * 获取缓存的key值
	 * 
	 * @param pjp
	 * @param cache
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getCacheKey(ProceedingJoinPoint pjp, Cacheable cache) {

		StringBuilder buf = new StringBuilder();
		buf.append(pjp.getSignature().getDeclaringTypeName()).append(".").append(pjp.getSignature().getName());
		if (cache.key().length() > 0) {
			buf.append(".").append(cache.key());
		}

		Object[] args = pjp.getArgs();
		if (cache.keyMode() == KeyMode.DEFAULT) {
			Annotation[][] pas = ((MethodSignature) pjp.getSignature()).getMethod().getParameterAnnotations();
			for (int i = 0; i < pas.length; i++) {
				for (Annotation an : pas[i]) {
					if (an instanceof CacheKey) {
						buf.append(".").append(args[i].toString());
						break;
					}
				}
			}
		} else if (cache.keyMode() == KeyMode.BASIC) {
			for (Object arg : args) {
				if (arg instanceof String) {
					buf.append(".").append(arg);
				} else if (arg instanceof Integer || arg instanceof Long || arg instanceof Short) {
					buf.append(".").append(arg.toString());
				} else if (arg instanceof Boolean) {
					buf.append(".").append(arg.toString());
				}
			}
		} else if (cache.keyMode() == KeyMode.ALL) {
			for (Object arg : args) {
				buf.append(".").append(arg.toString());
			}
		}

		return buf.toString();
	}

	/**
	 * 获取缓存的key key 定义在注解上，支持SPEL表达式
	 * 
	 * @param pjp
	 * @return
	 */
	private String parseKey(String key, Method method, Object[] args) {

		// 获取被拦截方法参数名列表(使用Spring支持类库)
		LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
		String[] paraNameArr = u.getParameterNames(method);

		// 使用SPEL进行key的解析
		ExpressionParser parser = new SpelExpressionParser();
		// SPEL上下文
		StandardEvaluationContext context = new StandardEvaluationContext();
		// 把方法参数放入SPEL上下文中
		for (int i = 0; i < paraNameArr.length; i++) {
			context.setVariable(paraNameArr[i], args[i]);
		}
		return parser.parseExpression(key).getValue(context, String.class);
	}
}