package com.cracker.api.mc.cache.jvm;

import com.google.common.collect.Maps;
import com.cracker.api.mc.cache.ICache;
import com.cracker.api.mc.common.validate.Assert;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 缓存系统中层组件，作为上层与下层的耦合剂：MemoryCache
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-27
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MemoryCache<T> implements ICache<T> {


    private Map<Object, T> hsMap = null;
    private Lock reentrantLock = null;

    public MemoryCache() {
        init();
    }

    private void init() {
        this.hsMap = Maps.newHashMap();
        // 使用公平锁
        reentrantLock = new ReentrantLock(true);
    }

    @Override
    public void setClassType(Class<T> classType, String cacheName) {
    }


    @Override
    public T get(String key) {
        return this.hsMap.get(key);
    }

    @Override
    public boolean put(String key, T obj) {
        try {
            this.reentrantLock.lock();
            this.hsMap.put(key, obj);
            return true;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public boolean put(String key, T obj, long expireTime) {
        try {
            this.reentrantLock.lock();
            this.hsMap.put(key, obj);
            return true;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public boolean clear() {
        this.hsMap.clear();
        return true;
    }

    @Override
    public boolean remove(String key) {
        try {
            this.reentrantLock.lock();
            return this.hsMap.remove(key) != null;
        } finally {
            this.reentrantLock.unlock();
        }
    }


    @Override
    public boolean hRemove(String key) {
        try {
            this.reentrantLock.lock();
            return this.hsMap.remove(key) != null;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public boolean hRemoveAll() {
        return false;
    }

    @Override
    public boolean containKey(String key) {
        return this.hsMap.containsKey(key);
    }

    @Override
    public boolean replace(String key, T obj) {
        try {
            this.reentrantLock.lock();
            return this.hsMap.replace(key, obj) != null;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public Map<String, T> getAll() {
        if (!Assert.isNotNull(hsMap)) {
            return null;
        }
        try {
            this.reentrantLock.lock();
            Map<String, T> result = Maps.newHashMap();
            for (Map.Entry<String, T> entry : result.entrySet()) {
                String key = entry.getKey();
                T value = entry.getValue();
                result.put(key, value);
            }
            return result;
        } finally {
            this.reentrantLock.unlock();
        }
    }

    @Override
    public long size() {
        return this.hsMap.size();
    }
}