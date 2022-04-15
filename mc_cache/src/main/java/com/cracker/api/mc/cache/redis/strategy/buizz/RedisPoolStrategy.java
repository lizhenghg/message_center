package com.cracker.api.mc.cache.redis.strategy.buizz;

import com.cracker.api.mc.cache.redis.pool.RedisPool;
import com.cracker.api.mc.cache.redis.pool.IRedisPool;
import com.cracker.api.mc.common.strategy.AbstractStrategy;
import com.cracker.api.mc.common.util.NumberSymbol;
import com.cracker.api.mc.common.util.Symbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 策略模式业务实现RedisPool：RedisPoolStrategy
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisPoolStrategy extends AbstractStrategy<IRedisPool> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPoolStrategy.class);


    /**
     * 单实例或者主从模式下使用策略模式创建redis连接池
     * @param objects 参数对象组
     * @return IRedisPool
     */
    @Override
    public IRedisPool exec(Object ... objects) throws Error {

        String address = (String) objects[0];
        String password = (String) objects[1];

        assert address != null;

        String[] hostAndPort = address.split(Symbol.COLON);

        String host = null;
        int port = 0;

        boolean warn = false;
        // 不抛异常，直接调用到Jedis时，让它抛。正常的springBoot连接redis也是如此，springBoot可不管你写的uri有多奇葩，俺只是送个地址过来的，谁调用谁check
        if (hostAndPort.length != NumberSymbol.SPLIT_NUMBER) {
            LOGGER.warn("RedisPool config wrong address: {}", address);
            warn = true;
        }
        if (!hostAndPort[1].matches(Symbol.REGEX_ALL_NUMBER)) {
            LOGGER.warn("RedisPool config wrong address: {}", address);
            warn = true;
        }
        if (!warn) {
            host = hostAndPort[0];
            port = Integer.parseInt(hostAndPort[1]);
        }

        return warn ? new RedisPool() : new RedisPool(host, port, password);
    }
}