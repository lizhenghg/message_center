package com.cracker.api.mc.cache.redis;

import com.cracker.api.mc.cache.redis.pool.IRedisPool;
import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 *
 * 自定义非集群模式下操作指令类：RedisNonClusterCommands
 * 适用于单机模式|主从模式|哨兵模式
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-27
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisNonClusterCommands extends AbstractRedisCommands {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisNonClusterCommands.class);

    /**
     * 全局唯一的连接池资源
     */
    private final IRedisPool redisPool;

    private RedisNonClusterCommands(IRedisPool redisPool) {
        if (redisPool == null) {
            throw new NullPointerException("RedisNonClusterCommands, pool is null");
        }

        this.redisPool = redisPool;
    }

    @Override
    public String get(String key) {
        Jedis jedis = null;
        String value;
        try {
            jedis = redisPool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to get, key：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to get, key：%s", key), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }


    @Override
    public String set(String key, String value) {
        Jedis jedis = null;
        String result;
        try {
            jedis = this.redisPool.getResource();
            result = jedis.set(key, value);
        } catch (Exception ex) {
            LOGGER.error("RedisNonClusterCommands, fail to set, key: {}, value: {}", key, value, ex);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to set, key: %s, value: %s", key, value), ex);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    @Override
    public String hget(String key, String field) {
        Jedis jedis = null;
        String value;
        try {
            jedis = redisPool.getResource();
            value = jedis.hget(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hget, key：%s, field：%s", key, field), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hget, key：%s, field：%s", key, field), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = null;
        Boolean value;
        try {
            jedis = redisPool.getResource();
            value = jedis.exists(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to exists, key：%s", key), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to exists, key：%s", key), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    @Override
    public Boolean hexists(String key, String field) {
        Jedis jedis = null;
        Boolean value;
        try {
            jedis = redisPool.getResource();
            value = jedis.hexists(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hexists, key：%s，field：%s", key, field), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hexists, key：%s，field：%s", key, field), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    @Override
    public Long setnx(String key, String value) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.setnx(key, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to setnx, key：%s, value：%s", key, value), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to setnx, key：%s, value：%s", key, value), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }

    @Override
    public Long hset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        Long ret;
        try {
            jedis = redisPool.getResource();
            ret = jedis.hset(key, hash);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hset, key：%s, hash：%s", key, hash.toString()), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hset, key：%s, hash：%s", key, hash.toString()), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }


    @Override
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        Long ret;
        try {
            jedis = redisPool.getResource();
            ret = jedis.hset(key, field, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hset, key：%s, field：%s, value：%s", key, field, value), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hset, key：%s, field：%s, value：%s", key, field, value), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        Jedis jedis = null;
        Long ret;
        try {
            jedis = redisPool.getResource();
            ret = jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hsetnx, key：%s, field：%s，value：%s", key, field, value), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hsetnx, key：%s, field：%s，value：%s", key, field, value), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        String value;
        try {
            jedis = redisPool.getResource();
            value = jedis.hmset(key, hash);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hmset, key：%s, hash：%s", key, hash.toString()), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hmset, key：%s, hash：%s", key, hash.toString()), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }


    @Override
    public Long expire(String key, int seconds) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to expire，key：%s，seconds：%s", key, seconds), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to expire，key：%s，seconds：%s", key, seconds), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }


    @Override
    public Long hlen(String key) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.hlen(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hlen，key：%s", key), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hlen，key：%s", key), e);
        }finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }


    @Override
    public Long hdel(String key, String... field) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.hdel(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hdel，key：%s，value：%s", key, Arrays.toString(field)), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hdel，key：%s，value：%s", key, Arrays.toString(field)), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }


    @Override
    public Long hDelAll(String key) {
        Jedis jedis = null;
        long lng;
        try {
            jedis = redisPool.getResource();
            Set<String> allFiled = jedis.hgetAll(key).keySet();
            if (Assert.isNotNull(allFiled)) {
                for (String field : allFiled) {
                    jedis.hdel(key, field);
                }
            }
            lng = 1L;
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hDelAll，key：%s", key), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hDelAll，key：%s", key), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }


    @Override
    public Long del(String key) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.del(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to del，key：%s", key), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to del，key：%s", key), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }


    @Override
    public Long lpush(String key, String... strings) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.lpush(key, strings);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to lpush，key：%s，string：%s", key, Arrays.toString(strings)), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to lpush，key：%s，string：%s", key, Arrays.toString(strings)), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }

    @Override
    public Long llen(String key) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.llen(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to llen，key：%s", key), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to llen，key：%s", key), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }

    @Override
    public String lset(String key, long index, String value) {
        Jedis jedis = null;
        String ret;
        try {
            jedis = redisPool.getResource();
            ret = jedis.lset(key, index, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to lset, key：%s, index：%s，value：%s", key, index, value), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to lset, key：%s, index：%s，value：%s", key, index, value), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }

    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        Jedis jedis = null;
        Long lng;
        try {
            jedis = redisPool.getResource();
            lng = jedis.linsert(key, where, pivot, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to linsert，key：%s，pivot：%s，value：%s", key, pivot, value), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to linsert，key：%s，pivot：%s，value：%s", key, pivot, value), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return lng;
    }

    @Override
    public String lindex(String key, long index) {
        Jedis jedis = null;
        String ret;
        try {
            jedis = redisPool.getResource();
            ret = jedis.lindex(key, index);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to lindex, key：%s, index：%s", key, index), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to lindex, key：%s, index：%s", key, index), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        Map<String, String> ret;
        try {
            jedis = redisPool.getResource();
            ret = jedis.hgetAll(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisNonClusterCommands, fail to hgetAll, key：%s", key), e);
            throw new CommonException(String.format("RedisNonClusterCommands, fail to hgetAll, key：%s", key), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return ret;
    }
}