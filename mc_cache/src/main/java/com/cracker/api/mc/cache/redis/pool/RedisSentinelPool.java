package com.cracker.api.mc.cache.redis.pool;

import com.cracker.api.mc.common.exception.CommonException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolAbstract;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Set;

/**
 * 哨兵模式下的redis连接池类: RedisSentinelPool
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-22
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisSentinelPool implements IRedisPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelPool.class);

    private JedisPoolAbstract poolAbstract;

    /**
     * 最大连接超时时间：8000ms，8s
     */
    private static final int MAX_TIME_OUT = 125 << 6;

    /**
     * 设置最大空闲等待：10
     */
    private final int maxIdle = 5 << 1;

    /**
     * 设置最小空闲等待：0
     */
    private final int minIdle = 0;

    /**
     * 设置最大连接数：100
     */
    private final int maxTotal = 25 << 2;

    /**
     * 最大等待时长：6000ms，6s
     */
    private final long maxWaitMills = 375L << 4;

    /**
     * 获取连接时是否测试连接可行性
     */
    private final boolean testOnBorrow = true;

    /**
     * 在空闲时检查有效性
     */
    private final boolean testWhileIdle = true;

    /**
     * 获取Jedis的最大次数
     */
    private final int retryCount = 5;

    /**
     * 每次获取Jedis资源失败时等待的时长，2000ms，2s
     */
    private final int retryingWaitingMillis = 125 << 4;


    public RedisSentinelPool(final String masterName, final Set<String> sentinels) {
        this(masterName, sentinels, null);
    }

    public RedisSentinelPool(final String masterName, final Set<String> sentinels, final String password) {
        this(masterName, sentinels, password, MAX_TIME_OUT);
    }

    public RedisSentinelPool(final String masterName, final Set<String> sentinels, final String password, final int maxTimeOut) {
        this(masterName, sentinels, password, maxTimeOut, null);
    }

    public RedisSentinelPool(final String masterName, final Set<String> sentinels, final String password,
                             final int maxTimeOut, final GenericObjectPoolConfig poolConfig) {
        init(masterName, sentinels, password, maxTimeOut, poolConfig);
    }


    /**
     * 初始化RedisSentinelPool
     * 为什么方法形参前面需要加上final修饰符？因为凡是配置操作类，里面的参数都应该是常量，比如username、password，不应该被修改
     * 所以，加上final。凡是加上final的变量就变成了常量，而且不能再被修改
     * @param masterName 主节点名称
     * @param sentinels 哨兵节点集
     * @param password 登录密码
     * @param maxTimeOut 最长连接时长
     * @param poolConfig 类对象池配置类
     */
    public void init(final String masterName, final Set<String> sentinels, final String password,
                     final int maxTimeOut, GenericObjectPoolConfig poolConfig) {
        if (poolConfig == null) {
            poolConfig = new GenericObjectPoolConfig();
            // 设置等待最长时间，单位：毫秒
            poolConfig.setMaxWaitMillis(maxWaitMills);
            // 设置最大空闲等待
            poolConfig.setMaxIdle(maxIdle);
            // 设置最小空闲等待
            poolConfig.setMinIdle(minIdle);
            // 设置最大连接数
            poolConfig.setMaxTotal(maxTotal);
            // 在空闲时检查有效性
            poolConfig.setTestWhileIdle(testWhileIdle);
            // 获取连接时是否测试连接可行性
            poolConfig.setTestOnBorrow(testOnBorrow);
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("start to init RedisSentinelPool ... ");
        }
        this.poolAbstract = new JedisSentinelPool(masterName, sentinels, poolConfig, maxTimeOut, password);
    }


    /**
     * 获取Jedis连接资源
     * @return Jedis
     */
    @Override
    public Jedis getResource() {
        Jedis jedis;
        jedis = this.getResourceFromPool();

        int count = 0;
        while (jedis == null && count < retryCount) {
            jedis = this.getResourceFromPool();
            wait(retryingWaitingMillis);
            count++;
        }
        if (jedis == null) {
            LOGGER.error("RedisSentinelPool, fail to get resource from pool, jedis is null");
            throw new CommonException("RedisSentinelPool, fail to get resource from pool, jedis is null");
        }
        return jedis;
    }


    /**
     * 关闭Jedis连接资源
     * @param jedis 被封装了资源的Jedis
     */
    @Override
    public void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


    /**
     * 强制销毁Jedis
     * @param jedis 被封装了资源的Jedis
     */
    @Override
    public void destroyResource(Jedis jedis) {
        if (jedis != null && jedis.isConnected()) {
            try {
                jedis.quit();
            } catch (Exception ex) {
                LOGGER.error(String.format("RedisSentinelPool, destroy the jedis, fail to quit jedis. errorMsg: %s", ex.getMessage()), ex);
            }
        }
    }


    /**
     * 从池中安全地获取单个连接
     *
     * @return Jedis
     */
    private synchronized Jedis getResourceFromPool() {
        Jedis jedis = null;
        try {
            jedis = this.poolAbstract.getResource();
        } catch (Exception ex) {
            LOGGER.error(String.format("RedisSentinelPool, fail to get resource, errorMsg: %s", ex.getMessage()), ex);
        }
        return jedis;
    }


    /**
     * 线程等待指定时长
     * @param mills 线程等待的指定时长
     */
    public void wait(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            LOGGER.error("RedisSentinelPool, fail to sleep: {}", e.getMessage(), e);
        }
    }
}