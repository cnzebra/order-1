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

	@Resource
	JedisPool redisPool;

	/**
	 * 对key进行模糊查询
	 * 
	 * @Description: TODO
	 * @param @param
	 *            key
	 * @param @return
	 * @return Set<String>
	 * @throws @author
	 *             ZJ
	 * @date 2016-3-29
	 */
	public Set<String> getKeys(String key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.keys(key);
		} finally {
			redis.close();
		}
	}

	public Object set(String key, int expire, String value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			String msg = redis.set(SafeEncoder.encode(key), SafeEncoder.encode(value));
			if (msg.equals(RESPONSE_OK)) {
				return redis.expire(SafeEncoder.encode(key), expire);
			} else {
				return msg;
			}
		} finally {
			redis.close();
		}

	}

	public Long getPK(String tableName, int interval) {
		long pk = this.incr("PK" + tableName, interval);
		if (pk < 10000000) {
			this.set("PK" + tableName, Integer.MAX_VALUE, 10000000 + "");
			pk = 10000000;
		}
		return pk;
	}

	public Long getTestPK(String tableName, int interval) {
		long pk = this.incr("PK" + tableName, interval);
		if (pk < 900000000) {
			this.set("PK" + tableName, Integer.MAX_VALUE, 900000000 + "");
			pk = 900000000;
		}
		return pk;
	}

	public Object set(String key, int expire, byte[] value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			String msg = redis.set(SafeEncoder.encode(key), value);
			if (msg.equals(RESPONSE_OK)) {
				return redis.expire(SafeEncoder.encode(key), expire);
			} else {
				return msg;
			}
		} finally {
			redis.close();
		}
	}

	public Object append(String key, String value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.append(SafeEncoder.encode(key), SafeEncoder.encode(value));
		} finally {
			redis.close();
		}
	}

	public Object append(String key, byte[] value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.append(SafeEncoder.encode(key), value);
		} finally {
			redis.close();
		}
	}

	public long incr(String key, long by) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.incrBy(SafeEncoder.encode(key), by);
		} finally {
			redis.close();
		}
	}

	public long decr(String key, long by) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.decrBy(SafeEncoder.encode(key), by);
		} finally {
			redis.close();
		}
	}

	public String getString(String key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			byte[] result = redis.get(SafeEncoder.encode(key));
			if (null != result) {
				return SafeEncoder.encode(result);
			} else {
				return null;
			}
		} finally {
			redis.close();
		}
	}

	public byte[] getBinary(String key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.get(SafeEncoder.encode(key));
		} finally {
			redis.close();
		}
	}

	public Object delete(String key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.del(SafeEncoder.encode(key));
		} finally {
			redis.close();
		}
	}

	public Long expire(String key, int seconds) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.expire(SafeEncoder.encode(key), seconds);
		} finally {
			redis.close();
		}
	}

	public byte[] substr(byte[] key, int start, int end) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.substr(key, start, end);
		} finally {
			redis.close();
		}
	}

	public Long rpush(byte[] key, byte[]... args) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.rpush(key, args);
		} finally {
			redis.close();
		}
	}

	public Long lpush(byte[] key, byte[]... args) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lpush(key, args);
		} finally {
			redis.close();
		}
	}

	public byte[] lpop(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lpop(key);
		} finally {
			redis.close();
		}
	}

	public byte[] rpop(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.rpop(key);
		} finally {
			redis.close();
		}
	}

	public String ltrim(byte[] key, long start, long end) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.ltrim(key, start, end);
		} finally {
			redis.close();
		}
	}

	public String lset(byte[] key, long index, byte[] value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lset(key, index, value);
		} finally {
			redis.close();
		}
	}

	public Long llen(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.llen(key);
		} finally {
			redis.close();
		}
	}

	public byte[] lindex(byte[] key, long index) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lindex(key, index);
		} finally {
			redis.close();
		}
	}

	public List<byte[]> lrange(byte[] key, long start, long end) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lrange(key, start, end);
		} finally {
			redis.close();
		}
	}

	public Long sadd(byte[] key, byte[]... member) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sadd(key, member);
		} finally {
			redis.close();
		}
	}

	public Long sadd(String key, String members) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sadd(key, members);
		} finally {
			redis.close();
		}
	}

	public Long srem(byte[] key, byte[]... member) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.srem(key, member);
		} finally {
			redis.close();
		}
	}

	public byte[] spop(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.spop(key);
		} finally {
			redis.close();
		}
	}

	public Long scard(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.scard(key);
		} finally {
			redis.close();
		}
	}

	public Boolean sismember(byte[] key, byte[] member) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sismember(key, member);
		} finally {
			redis.close();
		}
	}

	public List<byte[]> srandmember(byte[] key, int count) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.srandmember(key, count);
		} finally {
			redis.close();
		}
	}

	public Set<byte[]> smembers(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.smembers(key);
		} finally {
			redis.close();
		}
	}

	public Set<String> smembers(String key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.smembers(key);
		} finally {
			redis.close();
		}
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hset(key, field, value);
		} finally {
			redis.close();
		}
	}

	public Long hset(String key, String field, Object value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hset(key.getBytes(), field.getBytes(), SerializableUtil.serialize(value));
		} finally {
			redis.close();
		}
	}

	public String hmset(byte[] key, Map<byte[], byte[]> hash) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hmset(key, hash);
		} finally {
			redis.close();
		}
	}

	public Long hincrBy(byte[] key, byte[] field, long value) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hincrBy(key, field, value);
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			redis.close();
		}
		return 0l;
	}

	public Long hdel(byte[] key, byte[]... field) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hdel(key, field);
		} finally {
			redis.close();
		}
	}

	public Boolean hexists(byte[] key, byte[] field) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hexists(key, field);
		} finally {
			redis.close();
		}
	}

	public Long hlen(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hlen(key);
		} finally {
			redis.close();
		}
	}

	public Object hget(byte[] key, byte[] field) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			byte[] hget = redis.hget(key, field);
			if (hget == null)
				return null;
			return SerializableUtil.deserialize(hget);
		} finally {
			redis.close();
		}
	}

	public Object hget(String key, String field) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			byte[] hget = redis.hget(key.getBytes(), field.getBytes());
			if (hget == null || hget.length == 0)
				return null;
			return SerializableUtil.deserialize(hget);
		} finally {
			redis.close();
		}
	}
	
	public String hgetString(String key, String field) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			byte[] hget = redis.hget(key.getBytes(), field.getBytes());
			if (hget == null || hget.length == 0)
				return null;
			return new String(hget);
		} finally {
			redis.close();
		}
	}

	/**
	 * HM 是hashmap的意思 获取hashmap结构的数据
	 * 
	 * @Description: TODO
	 * @param @param
	 *            key
	 * @param @param
	 *            fields
	 * @param @return
	 * @return List<byte[]>
	 * @throws @author
	 *             ZJ
	 * @date 2016-7-1
	 */
	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hmget(key, fields);
		} finally {
			redis.close();
		}
	}

	public Set<byte[]> hkeys(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hkeys(key);
		} finally {
			redis.close();
		}
	}

	public Collection<byte[]> hvals(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hvals(key);
		} finally {
			redis.close();
		}
	}

	public Map<byte[], byte[]> hgetAll(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.hgetAll(key);
		} finally {
			redis.close();
		}
	}

	public List<byte[]> sort(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sort(key);
		} finally {
			redis.close();
		}
	}

	public List<byte[]> sort(byte[] key, SortingParams sortingParameters) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.sort(key, sortingParameters);
		} finally {
			redis.close();
		}
	}

	public Boolean exists(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.exists(key);
		} finally {
			redis.close();
		}
	}

	public String type(byte[] key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.type(key);
		} finally {
			redis.close();
		}
	}

	public Object set(String key, int expire, Object o) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return set(key, expire, SerializableUtil.serialize(o));
		} finally {
			redis.close();
		}
	}

	public Object getObject(String key) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			byte[] binary = getBinary(key);
			if (binary == null) {
				return null;
			}
			return SerializableUtil.deserialize(binary);
		} finally {
			redis.close();
		}
	}

	public Object rpush(String key, String strings) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.rpush(key, strings);
		} finally {
			redis.close();
		}
	}

	public List<String> lrange(String key, int start, int end) {
		Jedis redis = null;
		try {
			redis = redisPool.getResource();
			return redis.lrange(key, start, end);
		} finally {
			redis.close();
		}
	}
}
