package com.cracker.api.mc.cache.redis;

import com.cracker.api.mc.cache.redis.pool.IRedisPool;
import com.cracker.api.mc.cache.redis.pool.RedisClusterPool;
import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ListPosition;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 *
 * 自定义集群模式下操作指令类：RedisClusterCommands
 * 适用于集群模式|分片模式
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-27
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisClusterCommands extends AbstractRedisCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisNonClusterCommands.class);

    /**
     * 全局唯一的连接池资源
     */
    private final IRedisPool redisPool;


    private RedisClusterCommands(IRedisPool redisPool) {
        if (redisPool == null) {
            throw new NullPointerException("RedisClusterCommands, pool is null");
        }
        this.redisPool = redisPool;
    }

    @Override
    public String get(String key) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().get(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to get, key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to get, key：%s", key), e);
        }
    }


    @Override
    public String set(String key, String value) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().set(key, value);
        } catch (Exception ex) {
            LOGGER.error("RedisClusterCommands, fail to set, key: {}, value: {}", key, value, ex);
            throw new CommonException(String.format("RedisClusterCommands, fail to set, key: %s, value: %s", key, value), ex);
        }
    }

    @Override
    public String hget(String key, String field) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hget(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hget, key：%s, field：%s", key, field), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hget, key：%s, field：%s", key, field), e);
        }
    }

    @Override
    public Boolean exists(String key) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().exists(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to exists, key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to exists, key：%s", key), e);
        }
    }

    @Override
    public Boolean hexists(String key, String field) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hexists(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hexists, key：%s，field：%s", key, field), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hexists, key：%s，field：%s", key, field), e);
        }
    }

    @Override
    public Long setnx(String key, String value) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().setnx(key, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to setnx, key：%s, value：%s", key, value), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to setnx, key：%s, value：%s", key, value), e);
        }
    }

    @Override
    public Long hset(String key, Map<String, String> hash) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hset(key, hash);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hset, key：%s, hash：%s", key, hash.toString()), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hset, key：%s, hash：%s", key, hash.toString()), e);
        }
    }

    @Override
    public Long hset(String key, String field, String value) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hset(key, field, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hset, key：%s, field：%s, value：%s", key, field, value), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hset, key：%s, field：%s, value：%s", key, field, value), e);
        }
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hsetnx(key, field, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hsetnx, key：%s, field：%s，value：%s", key, field, value), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hsetnx, key：%s, field：%s，value：%s", key, field, value), e);
        }
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hmset(key, hash);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hmset, key：%s, hash：%s", key, hash.toString()), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hmset, key：%s, hash：%s", key, hash.toString()), e);
        }
    }


    @Override
    public Long expire(String key, int seconds) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to expire，key：%s，seconds：%s", key, seconds), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to expire，key：%s，seconds：%s", key, seconds), e);
        }
    }


    @Override
    public Long hlen(String key) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hlen(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hlen，key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hlen，key：%s", key), e);
        }
    }


    @Override
    public Long hdel(String key, String... field) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hdel(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hdel，key：%s，value：%s", key, Arrays.toString(field)), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hdel，key：%s，value：%s", key, Arrays.toString(field)), e);
        }
    }

    @Override
    public Long hDelAll(String key) {
        try {
            Set<String> allFiled = ((RedisClusterPool) redisPool).getJedisClusterClient().hgetAll(key).keySet();
            if (Assert.isNotNull(allFiled)) {
                for (String field : allFiled) {
                    ((RedisClusterPool) redisPool).getJedisClusterClient().hdel(key, field);
                }
            }
            return 1L;
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hDelAll，key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hDelAll，key：%s", key), e);
        }
    }


    @Override
    public Long del(String key) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().del(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to del，key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to del，key：%s", key), e);
        }
    }


    @Override
    public Long lpush(String key, String... strings) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().lpush(key, strings);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to lpush，key：%s，string：%s", key, Arrays.toString(strings)), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to lpush，key：%s，string：%s", key, Arrays.toString(strings)), e);
        }
    }


    @Override
    public Long llen(String key) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().llen(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to llen，key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to llen，key：%s", key), e);
        }
    }


    @Override
    public String lset(String key, long index, String value) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().lset(key, index, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to lset, key：%s, index：%s，value：%s", key, index, value), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to lset, key：%s, index：%s，value：%s", key, index, value), e);
        }
    }


    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().linsert(key, where, pivot, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to linsert，key：%s，pivot：%s，value：%s", key, pivot, value), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to linsert，key：%s，pivot：%s，value：%s", key, pivot, value), e);
        }
    }


    @Override
    public String lindex(String key, long index) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().lindex(key, index);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to lindex, key：%s, index：%s", key, index), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to lindex, key：%s, index：%s", key, index), e);
        }
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        try {
            return ((RedisClusterPool) redisPool).getJedisClusterClient().hgetAll(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisClusterCommands, fail to hgetAll, key：%s", key), e);
            throw new CommonException(String.format("RedisClusterCommands, fail to hgetAll, key：%s", key), e);
        }
    }
}