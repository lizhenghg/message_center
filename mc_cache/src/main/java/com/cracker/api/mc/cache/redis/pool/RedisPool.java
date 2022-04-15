package com.cracker.api.mc.cache.redis.pool;

import com.cracker.api.mc.common.exception.CommonException;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolAbstract;

/**
 * 单实例或者主从模式下的redis连接池类: RedisPool
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-22
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisPool implements IRedisPool {


    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPool.class);

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


    public RedisPool() {
        GenericObjectPoolConfig poolConfig = buildGenericObjectPool();
        this.poolAbstract = new JedisPool(poolConfig);
    }

    public RedisPool(final String host, final int port) {
        this(host, port, null);
    }

    public RedisPool(final String host, final int port, final String password) {
        this(host, port, password, MAX_TIME_OUT);
    }

    public RedisPool(final String host, final int port, final String password, final int maxTimeOut) {
        this(host, port, password, maxTimeOut, null);
    }

    public RedisPool(final String host, final int port, final String password, final int maxTimeOut, final GenericObjectPoolConfig config) {
        init(host, port, password, maxTimeOut, config);
    }

    /**
     * 初始化RedisPool
     * 为什么方法形参前面需要加上final修饰符？因为凡是配置操作类，里面的参数都应该是常量，比如username、password，不应该被修改
     * 所以，加上final。凡是加上final的变量就变成了常量，而且不能再被修改
     * @param host ip
     * @param port 端口
     * @param password 密码
     * @param maxTimeOut 最大连接时长
     * @param poolConfig 类对象池配置文件类
     */
    private void init(final String host, final int port, final String password, final int maxTimeOut,
                      GenericObjectPoolConfig poolConfig) {

        if (poolConfig == null) {
            poolConfig = buildGenericObjectPool();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("start to init RedisPool ... ");
        }
        this.poolAbstract = new JedisPool(poolConfig, host, port, maxTimeOut, password);
    }


    /**
     * 构建类对象池，一般大凡涉及到对象，都应该考虑类对象池技术
     * @return GenericObjectPoolConfig
     */
    private GenericObjectPoolConfig buildGenericObjectPool() {

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

        // 设置等待最长时间，单位：毫秒
        poolConfig.setMaxWaitMillis(maxWaitMills);
        // 设置最大空闲等待
        poolConfig.setMaxIdle(maxIdle);
        // 设置最大连接数
        poolConfig.setMaxTotal(maxTotal);
        // 在空闲时检查有效性
        poolConfig.setTestWhileIdle(testWhileIdle);
        // 获取连接时是否测试连接可行性
        poolConfig.setTestOnBorrow(testOnBorrow);

        return poolConfig;
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
            LOGGER.error("RedisPool, fail to get resource from pool, jedis is null");
            throw new CommonException("RedisPool, fail to get resource from pool, jedis is null");
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
                LOGGER.error(String.format("RedisPool, destroy the jedis, fail to quit jedis. errorMsg: %s", ex.getMessage()), ex);
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
            LOGGER.error("RedisPool, fail to get Resource", ex);
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
        } catch (InterruptedException ex) {
            LOGGER.error(String.format("RedisPool, fail to sleep, errorMsg: %s", ex.getMessage()), ex);
        }
    }
}
