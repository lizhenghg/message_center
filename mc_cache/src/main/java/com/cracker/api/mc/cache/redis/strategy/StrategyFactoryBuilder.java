package com.cracker.api.mc.cache.redis.strategy;

import com.cracker.api.mc.cache.redis.strategy.buizz.RedisClusterPoolStrategy;
import com.cracker.api.mc.cache.redis.strategy.buizz.RedisPoolStrategy;
import com.cracker.api.mc.cache.redis.strategy.buizz.RedisSentinelPoolStrategy;
import com.cracker.api.mc.common.codec.BuzzCode;
import com.cracker.api.mc.common.strategy.DefaultStrategyFactory;
import com.cracker.api.mc.common.strategy.StrategyFactory;

/**
 *
 * 策略模式工厂创建类：StrategyFactoryBuilder
 * 策略模式把具体的算法封装到了具体策略角色内部，增强了可扩展性，隐蔽了实现细节；它替代继承来实现，避免了if-else这种不易维护的条件语句
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class StrategyFactoryBuilder {


    private static final StrategyFactory STRATEGY_FACTORY = DefaultStrategyFactory.getStrategyFactory();

    public StrategyFactory build() {
        return STRATEGY_FACTORY;
    }

    static {
        // 1表示集群;2表示哨兵;3表示单实例
        STRATEGY_FACTORY.pushStrategy(String.valueOf(BuzzCode.STRATEGY_REDIS_POOL.getCode()),
                BuzzCode.TYPE_CLUSTER_REDIS_POOL.getCode(), RedisClusterPoolStrategy.class.getName());
        STRATEGY_FACTORY.pushStrategy(String.valueOf(BuzzCode.STRATEGY_REDIS_POOL.getCode()),
                BuzzCode.TYPE_GUARD_REDIS_POOL.getCode(), RedisSentinelPoolStrategy.class.getName());
        STRATEGY_FACTORY.pushStrategy(String.valueOf(BuzzCode.STRATEGY_REDIS_POOL.getCode()),
                BuzzCode.TYPE_SIMPLE_REDIS_POOL.getCode(), RedisPoolStrategy.class.getName());
    }
}