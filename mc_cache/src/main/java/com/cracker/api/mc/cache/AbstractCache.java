package com.cracker.api.mc.cache;

import com.cracker.api.mc.cache.config.CacheConfig;
import com.cracker.api.mc.common.classloader.ClassClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 *
 * 缓存对外应用层，也即是缓存最下层组件。缓存基础类，实现基本的缓存逻辑
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-03
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractCache<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCache.class);

    private ICache<T> iCache;

    private final String classTypeName;

    /**
     * 初始化中层--高层缓存组件
     */
    public AbstractCache() {
        String cacheClassName = CacheConfig.getInstance().getExecutorClassName();
        this.classTypeName = this.getClass().getName();

        try {
            @SuppressWarnings("unchecked")
            Class<ICache<T>> cacheClass = (Class<ICache<T>>) Class.forName(cacheClassName);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("init cache: {}", this.classTypeName);
            }
            this.iCache = cacheClass.newInstance();
            this.iCache.setClassType(seekClass(), this.classTypeName);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            LOGGER.warn("AbstractCache, fail to constructor: {}", ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> seekClass() {
        Class<?> clazz = ClassClient.forClient().build().seekClass(this, AbstractCache.class, "T");
        return (Class<T>) clazz;
    }


    public T get(String key) {
        return this.iCache.get(key);
    }

    public boolean put(String key, T obj) {
        return this.iCache.put(key, obj);
    }

    public boolean put(String key, T obj, long expireTime) {
        return this.iCache.put(key, obj, expireTime);
    }

    public void clear() {
        this.iCache.clear();
    }

    public void remove(String key) {
        this.iCache.remove(key);
    }

    public void hRemove(String key) {
        this.iCache.hRemove(key);
    }

    public void hRemoveAll() {
        this.iCache.hRemoveAll();
    }

    public boolean containKey(String key) {
        return this.iCache.containKey(key);
    }

    public boolean replace(String key, T obj) {
        return this.iCache.replace(key, obj);
    }

    public Map<String, T> getAll() {
        return this.iCache.getAll();
    }

    public long size() {
        return this.iCache.size();
    }

}