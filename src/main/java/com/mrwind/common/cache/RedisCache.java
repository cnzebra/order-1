package com.mrwind.common.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.mrwind.common.util.SerializableUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;

@Component
public class RedisCache {
	private static final String RESPONSE_OK = "OK";
	
	private Jedis redis = null;
	@Resource JedisPool jedisPool;

	/**
	 * 对key进行模糊查询
	 * @Description: TODO
	 * @param @param key
	 * @param @return   
	 * @return Set<String>  
	 * @throws
	 * @author ZJ
	 * @date 2016-3-29
	 */
	public Set<String> getKeys(String key){
		return redis.keys(key);
	}
	
	public Object set(String key, int expire, String value) {
		redis =jedisPool.getResource(); 
		String msg = redis.set(SafeEncoder.encode(key), SafeEncoder.encode(value));
		if(msg.equals(RESPONSE_OK)) {
			return redis.expire(SafeEncoder.encode(key), expire);
		} else {
			return msg;
		}
	}
	
	public Long getPK(String tableName, int interval) {
		long pk = this.incr("PK"+tableName, interval);
		if(pk<10000000){
			this.set("PK"+tableName, Integer.MAX_VALUE, 10000000+"");
			pk=10000000;
		}
		return pk;
	}
	
	public Object set(String key, int expire, byte[] value) {
		redis =jedisPool.getResource(); 
		String msg = redis.set(SafeEncoder.encode(key), value);
		if(msg.equals(RESPONSE_OK)) {
			return redis.expire(SafeEncoder.encode(key), expire);
		} else {
			return msg;
		}
	}
	
	public Object append(String key, String value) {
		redis =jedisPool.getResource(); 
		return redis.append(SafeEncoder.encode(key), SafeEncoder.encode(value));
	}

	public Object append(String key, byte[] value) {
		redis =jedisPool.getResource(); 
		return redis.append(SafeEncoder.encode(key), value);
	}

	public long incr(String key, long by) {
		redis =jedisPool.getResource(); 
		return redis.incrBy(SafeEncoder.encode(key), by);
	}

	public long decr(String key, long by) {
		redis =jedisPool.getResource(); 
		return redis.decrBy(SafeEncoder.encode(key), by);
	}

	public String getString(String key) {
		redis =jedisPool.getResource(); 
		byte[] result = redis.get(SafeEncoder.encode(key));
		if (null != result) {
			return SafeEncoder.encode(result);
		} else {
			return null;
		}
	}

	public byte[] getBinary(String key) {
		redis =jedisPool.getResource(); 
		return redis.get(SafeEncoder.encode(key));
	}

	public Object delete(String key) {
		redis =jedisPool.getResource(); 
		return redis.del(SafeEncoder.encode(key));
	}

	public Long expire(String key, int seconds) {
		redis =jedisPool.getResource(); 
		return redis.expire(SafeEncoder.encode(key), seconds);
	}
	
	public byte[] substr(byte[] key, int start, int end) {
		redis =jedisPool.getResource(); 
		return redis.substr(key, start, end);
	}

	public Long rpush(byte[] key, byte[]... args) {
		redis =jedisPool.getResource(); 
		return redis.rpush(key, args);
	}

	public Long lpush(byte[] key, byte[]... args) {
		redis =jedisPool.getResource(); 
		return redis.lpush(key, args);
	}

	public byte[] lpop(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.lpop(key);
	}

	public byte[] rpop(byte[] key) {	
		redis =jedisPool.getResource(); 
		return redis.rpop(key);
	}

	public String ltrim(byte[] key, long start, long end) {
		redis =jedisPool.getResource(); 
		return redis.ltrim(key, start, end);
	}

	public String lset(byte[] key, long index, byte[] value) {
		redis =jedisPool.getResource(); 
		return redis.lset(key, index, value);
	}

	public Long llen(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.llen(key);
	}

	public byte[] lindex(byte[] key, long index) {
		redis =jedisPool.getResource(); 
		return redis.lindex(key, index);
	}

	public List<byte[]> lrange(byte[] key, long start, long end) {
		redis =jedisPool.getResource(); 
		return redis.lrange(key, start, end);
	}

	public Long sadd(byte[] key, byte[]... member) {
		redis =jedisPool.getResource(); 
		return redis.sadd(key, member);
	}
	
	public Long sadd(String key, String members) {
		redis =jedisPool.getResource(); 
		return redis.sadd(key, members);
	}

	public Long srem(byte[] key, byte[]... member) {
		redis =jedisPool.getResource(); 
		return redis.srem(key, member);
	}

	public byte[] spop(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.spop(key);
	}

	public Long scard(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.scard(key);
	}

	public Boolean sismember(byte[] key, byte[] member) {
		redis =jedisPool.getResource(); 
		return redis.sismember(key, member);
	}

	public List<byte[]> srandmember(byte[] key, int count) {
		redis =jedisPool.getResource(); 
		return redis.srandmember(key, count);
	}

	public Set<byte[]> smembers(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.smembers(key);
	}
	
	public Set<String> smembers(String key) {
		redis =jedisPool.getResource(); 
		return redis.smembers(key);
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		redis =jedisPool.getResource(); 
		return redis.hset(key, field, value);
	}
	
	public Long hset(String key, String field, Object value) {
		redis =jedisPool.getResource(); 
		return redis.hset(key.getBytes(), field.getBytes(), SerializableUtil.serialize(value));
	}
	
	

	public String hmset(byte[] key, Map<byte[], byte[]> hash) {
		redis =jedisPool.getResource(); 
		return redis.hmset(key, hash);
	}

	public Long hincrBy(byte[] key, byte[] field, long value) {
		redis =jedisPool.getResource(); 
		return redis.hincrBy(key, field, value);
	}

	public Long hdel(byte[] key, byte[]... field) {
		redis =jedisPool.getResource(); 
		return redis.hdel(key, field);
	}

	public Boolean hexists(byte[] key, byte[] field) {
		redis =jedisPool.getResource(); 
		return redis.hexists(key, field);
	}

	public Long hlen(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.hlen(key);
	}

	public Object hget(byte[] key, byte[] field) {
		redis =jedisPool.getResource(); 
		return SerializableUtil.deserialize(redis.hget(key, field));
	}
	
	public Object hget(String key, String field) {
		redis =jedisPool.getResource(); 
		return SerializableUtil.deserialize(redis.hget(key.getBytes(), field.getBytes()));
	}
	
	/**
	 *HM 是hashmap的意思 获取hashmap结构的数据
	 * @Description: TODO
	 * @param @param key
	 * @param @param fields
	 * @param @return   
	 * @return List<byte[]>  
	 * @throws
	 * @author ZJ
	 * @date 2016-7-1
	 */
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		redis =jedisPool.getResource(); 
		return redis.hmget(key, fields);
	}

	public Set<byte[]> hkeys(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.hkeys(key);
	}

	public Collection<byte[]> hvals(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.hvals(key);
	}

	public Map<byte[], byte[]> hgetAll(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.hgetAll(key);
	}

	public List<byte[]> sort(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.sort(key);
	}

	public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		redis =jedisPool.getResource(); 
		return redis.sort(key, sortingParameters);
	}

	public Boolean exists(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.exists(key);
	}

	public String type(byte[] key) {
		redis =jedisPool.getResource(); 
		return redis.type(key);
	}

	public Object set(String key, int expire, Object o) {
		redis =jedisPool.getResource(); 
		return set(key, expire, SerializableUtil.serialize(o));
	}

	public Object getObject(String key) {
		redis =jedisPool.getResource(); 
		byte[] binary = getBinary(key);
		if(binary ==null){
			return null;
		}
		return SerializableUtil.deserialize(binary);
	}

	public Object rpush(String key,String strings) {
		redis =jedisPool.getResource(); 
		return redis.rpush(key, strings);
	}

	public List<String> lrange(String key,int start,int end) {
		redis =jedisPool.getResource(); 
		return redis.lrange(key, start, end);
	}
}
