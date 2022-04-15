package com.cracker.api.mc.cache;


import com.cracker.api.mc.cache.config.CacheConfig;
import com.cracker.api.mc.common.classloader.ClassClient;
import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.lock.NonDistributedLockClient;
import com.cracker.api.mc.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.Set;

/**
 * Cache对外服务类，提供Cache各种服务
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-04
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class CacheServerBootstrap {


    private static final Logger LOGGER = LoggerFactory.getLogger(CacheServerBootstrap.class);
    /**
     * 存放缓存对象的容器
     */
    private static final Map<String, AbstractCache<?>> HS_CACHE = new ConcurrentHashMap<>();

    /**
     * 使用ConcurrentHashMap实现简单的锁机制，处理多线程场景
     */
    private static NonDistributedLockClient lockClient = NonDistributedLockClient.getInstance();

    private static volatile boolean bInit = false;

    private CacheServerBootstrap() {}

    public static void init(String basePath) {
        if (bInit) {
            return;
        }
        if (lockClient.lock(basePath)) {
            try {
                LOGGER.info("CacheServerBootstrap init start ... ");
                // 加载cache配置文件
                CacheConfig.init(basePath);
                // 初始化并存储AbstractCache对象
                initAndPutClass();
                bInit = true;
            } finally {
                lockClient.unlock(basePath);
            }
        } else {
            LOGGER.error("fail to lock the entity, basePath: {}, perhaps someone is locking it ", basePath);
            throw new CommonException("fail to lock the entity, basePath: " + basePath);
        }
    }

    /**
     * 初始化并存储AbstractCache对象
     */
    private static void initAndPutClass() {

        String cachePackage = CacheConfig.getInstance().getCachePackage();

        Set<Class<?>> classSet = ClassClient
                .forClient()
                .build()
                .scanClasses(cachePackage);
        if (Assert.isNotNull(classSet)) {

            for (Class<?> clazz : classSet) {
                if (clazz.isAnnotation()
                        || clazz.isAnonymousClass()
                        || clazz.isPrimitive()
                        || clazz.isEnum()
                        || clazz.isInterface()) {
                    continue;
                }
                String superClassName = clazz.getSuperclass().getName();
                if (AbstractCache.class.getName().equals(superClassName)) {

                    try {
                        AbstractCache<?> cacheObj = (AbstractCache<?>) clazz.newInstance();
                        HS_CACHE.put(clazz.getName(), cacheObj);
                    } catch (InstantiationException | IllegalAccessException e) {
                        LOGGER.warn("WARN: something wrong has occur, could not init the Class: {}, it may cause unExcepted exception",
                                clazz.getName(), e);
                    }
                }
            }
        }
    }

    /**
     * 对应用层提供的缓存服务
     * @param clazz 获取缓存对象的key
     * @return AbstractCache<?>
     */
    public static AbstractCache<?> getCacheServer(Class<?> clazz) {
        return HS_CACHE.get(clazz.getName());
    }
}