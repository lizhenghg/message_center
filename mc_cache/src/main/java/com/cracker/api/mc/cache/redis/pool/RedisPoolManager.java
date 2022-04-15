package com.cracker.api.mc.cache.redis.pool;

import com.cracker.api.mc.cache.config.CacheConfig;
import com.cracker.api.mc.cache.redis.strategy.StrategyFactoryBuilder;
import com.cracker.api.mc.common.codec.BuzzCode;
import com.cracker.api.mc.common.strategy.Strategy;
import com.cracker.api.mc.common.strategy.StrategyFactory;

import java.util.Objects;

/**
 *
 * IRedisPool连接池管理类：RedisPoolManager
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-22
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class RedisPoolManager {

    /**
     * 管理连接池，全局单例
     */
    private IRedisPool redisPool;

    private static volatile RedisPoolManager instance;

    public static RedisPoolManager getInstance() {
        if (instance == null) {
            synchronized (RedisPoolManager.class) {
                if (instance == null) {
                    instance = new RedisPoolManager();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    private RedisPoolManager() {
        initRedisPool();
    }

    /**
     * 通过策略模式初始化IRedisPool，可能会抛断言AssertionError，该异常只instanceof Error
     */
    private void initRedisPool() throws Error {

        int choice = CacheConfig.getInstance().getRedisType();
        String hosts = CacheConfig.getInstance().getRedisHost();
        String password = CacheConfig.getInstance().getRedisPassword();
        String sentinelMasterName = CacheConfig.getInstance().getRedisSentinelMasterName();

        // 通过策略模式初始化IRedisPool
        StrategyFactory strategyFactory = new StrategyFactoryBuilder().build();
        @SuppressWarnings("unchecked")
        Strategy<IRedisPool> strategy = strategyFactory.buildStrategy(String.valueOf(BuzzCode.STRATEGY_REDIS_POOL.getCode()),
                choice);

        this.redisPool = strategy.exec(hosts, password, sentinelMasterName);

        // 销毁策略模式容器对象,下次再使用，记得先pushStrategy
        strategyFactory.destroyStrategy(String.valueOf(BuzzCode.STRATEGY_REDIS_POOL.getCode()));
    }

    /**
     * 获取RedisPool
     * @return IRedisPool
     */
    public IRedisPool getRedisPool() {
        return this.redisPool;
    }
}
