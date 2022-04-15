package com.cracker.api.mc.cache.redis.strategy.buizz;

import com.google.common.collect.Sets;
import com.cracker.api.mc.cache.redis.pool.IRedisPool;
import com.cracker.api.mc.cache.redis.pool.RedisClusterPool;
import com.cracker.api.mc.common.strategy.AbstractStrategy;
import com.cracker.api.mc.common.util.NumberSymbol;
import com.cracker.api.mc.common.util.Symbol;
import com.cracker.api.mc.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;

import java.util.Set;

/**
 *
 * 策略模式业务实现RedisClusterPool：RedisClusterPoolStrategy
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisClusterPoolStrategy extends AbstractStrategy<IRedisPool> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelPoolStrategy.class);

    /**
     * 集群模式下使用策略模式创建redis连接池
     * @param objects 参数对象组
     * @return IRedisPool
     */
    @Override
    public IRedisPool exec(Object... objects) throws Error {

        String hosts = (String) objects[0];
        String password = (String) objects[1];

        assert !Assert.isEmpty(hosts);

        String[] arrayAddress = hosts.split(Symbol.COMMA);
        Set<HostAndPort> sentinels = Sets.newLinkedHashSet();

        for (String address : arrayAddress) {

            if (address.isEmpty()) {
                continue;
            }

            String[] array = address.split(Symbol.COLON);

            // 不抛异常，直接调用到Jedis时，让它抛。正常的springBoot连接redis也是如此，springBoot可不管你写的uri有多奇葩，俺只是送个地址过来的，谁调用谁check
            if (array.length != NumberSymbol.SPLIT_NUMBER) {
                LOGGER.warn("RedisClusterPool config wrong address: {}", address);
                continue;
            }
            if (!array[1].matches(Symbol.REGEX_ALL_NUMBER)) {
                LOGGER.warn("RedisClusterPool config wrong address: {}", address);
                continue;
            }

            sentinels.add(new HostAndPort(array[0], Integer.parseInt(array[1])));
        }

        return new RedisClusterPool(sentinels, password);
    }
}
