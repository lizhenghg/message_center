package com.cracker.api.mc.cache.redis.pool;

import redis.clients.jedis.Jedis;

/**
 * redis连接池管理接口: IRedisPool
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-22
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface IRedisPool {

    /**
     *  从连接池中获取Jedis实例
     * @return 被封装了资源的Jedis
     */
    public abstract Jedis getResource();

    /**
     * 关闭资源
     * @param jedis 被封装了资源的Jedis
     */
    public abstract void close(Jedis jedis);

    /**
     * 强制销毁连接
     * @param jedis 被封装了资源的Jedis
     */
    void destroyResource(Jedis jedis);

}
