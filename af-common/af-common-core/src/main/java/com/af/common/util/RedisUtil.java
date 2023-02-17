package com.af.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
/**
 * redis 工具类
 * @Author Scott
 *
 */
@Component
public class RedisUtil {

	private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setDBIndex(int index) {
		JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
		jedisConnectionFactory.setDatabase(index);//切换到指定的db上(index指具体的值)
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
	}

	/**
	 * 有序存储（将一个或多个成员元素及其分数值加入到有序集当中）
	 *
	 * @param key
	 * @param value
	 * @param score
	 * @return
	 */
	public Boolean zset(String key, String value, double score) {
		return redisTemplate.opsForZSet().add(key, value, score);
	}

	
	/**
	 * 删除缓存，通过前缀
	 *
	 * @param keys 可以传一个值 或多个
	 */
	public void delByprex(String prex) {
		prex = prex+"**";
        Set<String> keys = redisTemplate.keys(prex);
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
	}
	
	/**
	 * 指定缓存失效时间
	 *
	 * @param key  键
	 * @param timeSecond 时间(秒)
	 * @return
	 */
	public boolean expire(String key, long timeSecond) {
		try {
			if (timeSecond > 0) {
				redisTemplate.expire(key, timeSecond, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据key 获取过期时间
	 *
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}


	/**
	 * 查找匹配key
	 *
	 * @param pattern key
	 * @return /
	 */
	public List<String> scan(String pattern) {
		ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		RedisConnection rc = Objects.requireNonNull(factory).getConnection();
		Cursor<byte[]> cursor = rc.scan(options);
		List<String> result = new ArrayList<>();
		while (cursor.hasNext()) {
			result.add(new String(cursor.next()));
		}
		try {
			RedisConnectionUtils.releaseConnection(rc, factory);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}


	/**
	 * 分页查询 key
	 *
	 * @param patternKey key
	 * @param page       页码
	 * @param size       每页数目
	 * @return /
	 */
	public List<String> findKeysForPage(String patternKey, int page, int size) {
		ScanOptions options = ScanOptions.scanOptions().match(patternKey).build();
		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		RedisConnection rc = Objects.requireNonNull(factory).getConnection();
		Cursor<byte[]> cursor = rc.scan(options);
		List<String> result = new ArrayList<>(size);
		int tmpIndex = 0;
		int fromIndex = page * size;
		int toIndex = page * size + size;
		while (cursor.hasNext()) {
			if (tmpIndex >= fromIndex && tmpIndex < toIndex) {
				result.add(new String(cursor.next()));
				tmpIndex++;
				continue;
			}
			// 获取到满足条件的数据后,就可以退出了
			if (tmpIndex >= toIndex) {
				break;
			}
			tmpIndex++;
			cursor.next();
		}
		try {
			RedisConnectionUtils.releaseConnection(rc, factory);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 根据key 获取所有以key开头的键的集合
	 *
	 * @param key
	 * @return 返回所有key的集合
	 */
	public Set<String> keys(String key) {
		try {
			return redisTemplate.keys(key + "*");
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.EMPTY_SET;
		}
	}
	/**
	 * 判断key是否存在
	 *
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public boolean hasKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除缓存
	 *
	 * @param keys 可以传一个值 或多个
	 */
	public void del(String... keys) {
		if (keys != null && keys.length > 0) {
			if (keys.length == 1) {
				boolean result = redisTemplate.delete(keys[0]);
				log.debug("--------------------------------------------");
				log.debug(new StringBuilder("删除缓存：").append(keys[0]).append("，结果：").append(result).toString());
				log.debug("--------------------------------------------");
			} else {
				Set<String> keySet = new HashSet<>();
				for (String key : keys) {
					keySet.addAll(redisTemplate.keys(key));
				}
				long count = redisTemplate.delete(keySet);
				log.debug("--------------------------------------------");
				log.debug("成功删除缓存：" + keySet.toString());
				log.debug("缓存删除数量：" + count + "个");
				log.debug("--------------------------------------------");
			}
		}
	}

	// ============================String=============================

	/**
	 * 普通缓存获取
	 *
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key) {
		return key == null ? null : redisTemplate.opsForValue().get(key);
	}

	/**
	 * 批量获取
	 *
	 * @param keys
	 * @return
	 */
	public List<Object> multiGet(List<String> keys) {
		List list = redisTemplate.opsForValue().multiGet(new HashSet<>(keys));
		List resultList = new ArrayList();
		Optional.ofNullable(list).ifPresent(e -> list.forEach(ele -> Optional.ofNullable(ele).ifPresent(resultList::add)));
		return resultList;
	}


	/**
	 * 普通缓存放入
	 *
	 * @param key   键
	 * @param value 值
	 * @return true成功 false失败
	 */
	public boolean set(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 键不存在时设置
	 * <p>
	 * 普通缓存放入并设置时间
	 *
	 * @param key
	 * @param value
	 * @param time
	 * @return
	 */
	public boolean setIfAbsent(String key, Object value, long time) {
		try {
			return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key      键
	 * @param value    值
	 * @param time     时间
	 * @param timeUnit 类型
	 * @return true成功 false 失败
	 */
	public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, timeUnit);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 递增
	 *
	 * @param key 键
	 * @param by  要增加几(大于0)
	 * @return
	 */
	public long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 递减
	 *
	 * @param key 键
	 * @param delta  要减少几(小于0)
	 * @return
	 */
	public long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, -delta);
	}

	// ================================Map=================================

	/**
	 * HashGet
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return 值
	 */
	public Object hget(String key, String item) {
		return redisTemplate.opsForHash().get(key, item);
	}

	/**
	 * 获取hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Map<Object, Object> hmget(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public boolean hmset(String key, Map<String, Object> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * HashSet 并设置时间
	 *
	 * @param key  键
	 * @param map  对应多个键值
	 * @param time 时间(秒)
	 * @return true成功 false失败
	 */
	public boolean hmset(String key, Map<String, Object> map, long time) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true 成功 false失败
	 */
	public boolean hset(String key, String item, Object value) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public boolean hset(String key, String item, Object value, long time) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除hash表中的值
	 *
	 * @param key  键 不能为null
	 * @param item 项 可以使多个 不能为null
	 */
	public void hdel(String key, Object... item) {
		redisTemplate.opsForHash().delete(key, item);
	}

	/**
	 * 判断hash表中是否有该项的值
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return true 存在 false不存在
	 */
	public boolean hHasKey(String key, String item) {
		return redisTemplate.opsForHash().hasKey(key, item);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param by   要增加几(大于0)
	 * @return
	 */
	public double hincr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, by);
	}

	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param by   要减少记(小于0)
	 * @return
	 */
	public double hdecr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, -by);
	}

	// ============================set=============================

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key 键
	 * @return
	 */
	public Set<Object> sGet(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public boolean sHasKey(String key, Object value) {
		try {
			return redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将数据放入set缓存
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public long sSet(String key, Object... values) {
		try {
			return redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 将set数据放入缓存
	 *
	 * @param key    键
	 * @param time   时间(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public long sSetAndTime(String key, long time, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().add(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取set缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public long sGetSetSize(String key) {
		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 移除值为value的
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数
	 */
	public long setRemove(String key, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	// ===============================list=================================

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public List<Object> lGet(String key, long start, long end) {
		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取list缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public long lGetListSize(String key) {
		try {
			return redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 通过索引 获取list中的值
	 *
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public Object lGetIndex(String key, long index) {
		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public boolean lSet(String key, Object value) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public boolean lLeftSet(String key, Object value) {
		try {
			redisTemplate.opsForList().leftPush(key, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return
	 */
	public boolean lSet(String key, Object value, long time) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public boolean lSet(String key, List<Object> value) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return
	 */
	public boolean lSet(String key, List<Object> value, long time) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据索引修改list中的某条数据
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return
	 */
	public boolean lUpdateIndex(String key, long index, Object value) {
		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数
	 */
	public long lRemove(String key, long count, Object value) {
		try {
			Long remove = redisTemplate.opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 移出并获取列表的第一个元素
	 *
	 * @param key
	 * @return 删除的元素
	 */
	public Object lLeftPop(String key) {
		return redisTemplate.opsForList().leftPop(key);
	}

	/**
	 * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 *
	 * @param key
	 * @param timeout 等待时间
	 * @param unit    时间单位
	 * @return
	 */
	public Object lBLeftPop(String key, long timeout, TimeUnit unit) {
		return redisTemplate.opsForList().leftPop(key, timeout, unit);
	}

	/**
	 * 移除并获取列表最后一个元素
	 *
	 * @param key
	 * @return 删除的元素
	 */
	public Object lRightPop(String key) {
		return redisTemplate.opsForList().rightPop(key);
	}

	/**
	 * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 *
	 * @param key
	 * @param timeout 等待时间
	 * @param unit    时间单位
	 * @return
	 */
	public Object lBRightPop(String key, long timeout, TimeUnit unit) {
		return redisTemplate.opsForList().rightPop(key, timeout, unit);
	}

	/**
	 * @param prefix 前缀
	 * @param ids    id
	 */
	public void delByKeys(String prefix, Set<Long> ids) {
		Set<String> keys = new HashSet<>();
		for (Long id : ids) {
			keys.addAll(redisTemplate.keys(new StringBuffer(prefix).append(id).toString()));
		}
		long count = redisTemplate.delete(keys);
		// 此处提示可自行删除
		log.debug("--------------------------------------------");
		log.debug("成功删除缓存：" + keys.toString());
		log.debug("缓存删除数量：" + count + "个");
		log.debug("--------------------------------------------");
	}

	/**
	 * 获取所有的key和值
	 */
	public Map<String, Object> getListKeyAndVule(String prefix) {
		Map<String, Object> map = new HashMap<>();
		Set<String> keys = redisTemplate.keys(prefix.concat("*")).stream().map(Object::toString).collect(Collectors.toSet());
		if (keys.size() <= 0) {
			return map;
		}
		for (String key : keys) {
			map.put(key.replace(prefix, ""), get(key));
		}
		return map;
	}

	/**
	 * 获取所有的key和值
	 */
	public Map<String, List<Object>> getListKeyAndLValue(String prefix) {
		Map<String, List<Object>> map = new HashMap<>();
		Set<String> keys = redisTemplate.keys(prefix.concat("*")).stream().map(Object::toString).collect(Collectors.toSet());
		if (keys.size() <= 0) {
			return map;
		}
		for (String key : keys) {
			map.put(key.replace(prefix, ""), lGet(key, 0, -1));
		}
		return map;
	}

}
