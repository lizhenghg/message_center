package com.cracker.api.mc.cache.redis;

import com.google.common.collect.Maps;
import com.cracker.api.mc.cache.ICache;
import com.cracker.api.mc.cache.config.CacheConfig;
import com.cracker.api.mc.cache.redis.pool.IRedisPool;
import com.cracker.api.mc.cache.redis.pool.RedisPoolManager;
import com.cracker.api.mc.cache.serialize.SerializeBuilder;
import com.cracker.api.mc.common.codec.Codec;
import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;

/**
 *
 * 缓存系统中层组件，作为上层与下层的耦合剂：RedisCache
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-27
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisCache<T> implements ICache<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCache.class);

    /**
     * 缓存系统上层组件
     */
    private AbstractRedisCommands redisCommands;

    private Class<T> classType = null;

    private String cacheName = null;

    public RedisCache() {
        init();
    }

    /**
     * 在这里实例化AbstractRedisCommands
     */
    private void init() {
        try {
            Constructor<?> constructor = Class.forName(CacheConfig.getInstance().getCommandClassName()).getDeclaredConstructor(IRedisPool.class);
            constructor.setAccessible(true);
            this.redisCommands = (AbstractRedisCommands) constructor.newInstance(RedisPoolManager.getInstance().getRedisPool());
        } catch (NoSuchMethodException | ClassNotFoundException | IllegalAccessException
                | InstantiationException | InvocationTargetException ex) {
            LOGGER.warn("initialize AbstractRedisCommands fail, it might cause unexpected exception, pls check errorMsg: {}",
                    ex.getMessage(), ex);
        }
    }


    @Override
    public void setClassType(Class<T> classType, String cacheName) {
        this.classType = classType;
        this.cacheName = cacheName;
    }


    public Class<T> getClassType() {
        return this.classType;
    }

    public String getCacheName() {
        return this.cacheName;
    }


    @Override
    public T get(String field) {

        String value = this.redisCommands.hget(getCacheName(), field);
        if (Assert.isEmpty(value)) {
            return null;
        }
        try {
            byte[] bytes = Codec.decodeBase64(value);
            return SerializeBuilder.builder().deSerialize(getClassType(), bytes);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(String.format("RedisCache, fail to decode value, field：%s, value：%s", field, value), ex);
            throw new CommonException(String.format("RedisCache, fail to decode value, field：%s, value：%s", field, value), ex);
        }
    }


    @Override
    public boolean put(String key, T obj) {
        return put(key, obj, 0L);
    }

    @Override
    public boolean put(String field, T obj, long expireTime) {

        byte[] bytes = SerializeBuilder.builder().serialize(obj);
        String encodeStr = Codec.encodeBase64(bytes);

        this.redisCommands.hset(getCacheName(), field, encodeStr);
        if (expireTime > 0) {
            this.redisCommands.expire(field, (int) expireTime);
        }
        return true;
    }

    @Override
    public boolean clear() {
        this.redisCommands.del(getCacheName());
        return true;
    }

    @Override
    public boolean remove(String key) {
        return this.redisCommands.del(key) > 0;
    }


    @Override
    public boolean hRemove(String key) {
        return this.redisCommands.hdel(getCacheName(), key) > 0;
    }


    @Override
    public boolean hRemoveAll() {
        return this.redisCommands.hDelAll(getCacheName()) > 0;
    }


    @Override
    public boolean containKey(String field) {
        return this.redisCommands.hexists(getCacheName(), field);
    }

    @Override
    public boolean replace(String field, T obj) {

        byte[] bytes = SerializeBuilder.builder().serialize(obj);
        String encodeStr = Codec.encodeBase64(bytes);

        this.redisCommands.hset(getCacheName(), field, encodeStr);
        return true;
    }

    @Override
    public Map<String, T> getAll() {

        Map<String, T> retMap = Maps.newHashMap();
        Map<String, String> srcMap = this.redisCommands.hgetAll(getCacheName());
        if (!Assert.isNotNull(srcMap)) {
            return Collections.emptyMap();
        }

        for (Map.Entry<String, String> entry : srcMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            try {
                byte[] bytes = Codec.decodeBase64(value);
                T obj = SerializeBuilder.builder().deSerialize(getClassType(), bytes);
                retMap.put(key, obj);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(String.format("RedisCache, getAll(), fail to decode base64, key：%s, value：%s", key, value), e);
            }
        }
        return retMap;
    }

    @Override
    public long size() {
        return this.redisCommands.hlen(getCacheName());
    }
}