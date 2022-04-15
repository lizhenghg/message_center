package com.cracker.api.mc.cache.redis.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Set;

/**
 * 集群模式下的redis连接池类: RedisClusterPool
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-22
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisClusterPool implements IRedisPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterPool.class);

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
     * redis集群重试次数：4次。默认2次
     * JedisCluster在连接的时候，如果出现连接错误，则会尝试随机连接一个节点，如果当期尝试的节点返回Moved重定向，
     * jedis cluster会重新更新clots缓存。如果重试依然返回连接错误，会接着再次重试，当重试次数大于maxAttempts
     * 会报出Jedis ClusterMaxRedirectionsException("to many Cluster redireciotns?")异常
     */
    private static final int MAX_ATTEMPTS = 1 << 2;

    /**
     * Jedis读超时：2000ms，2s。默认：2s
     */
    private final static int SO_TIME_OUT = 2000;

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


    private JedisCluster jedisCluster;


    public RedisClusterPool(final Set<HostAndPort> nodes, final String password) {
        this(nodes, password, MAX_TIME_OUT, SO_TIME_OUT, MAX_ATTEMPTS);
    }

    public RedisClusterPool(final Set<HostAndPort> nodes, final String password, final int connectionTimeout,
                            final int soTimeout, final int maxAttempts) {
        this(nodes, password, connectionTimeout, soTimeout, maxAttempts, null);
    }

    public RedisClusterPool(final Set<HostAndPort> nodes, final String password, final int connectionTimeout,
                            final int soTimeout, final int maxAttempts, final GenericObjectPoolConfig poolConfig) {
        init(nodes, password, connectionTimeout, soTimeout, maxAttempts, poolConfig);
    }

    /**
     * 初始化RedisClusterPool
     * 为什么方法形参前面需要加上final修饰符？因为凡是配置操作类，里面的参数都应该是常量，比如username、password，不应该被修改
     * 所以，加上final。凡是加上final的变量就变成了常量，而且不能再被修改
     * @param nodes redis集群
     * @param password 登录密码
     * @param connectionTimeout 最大连接超时
     * @param soTimeout 读超时
     * @param maxAttempts 最大重试次数
     * @param poolConfig 类对象池配置类
     */
    public void init(final Set<HostAndPort> nodes, final String password, final int connectionTimeout,
                            final int soTimeout, final int maxAttempts, GenericObjectPoolConfig poolConfig) {
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
            LOGGER.info("start to init RedisClusterPool ... ");
        }
        this.jedisCluster = new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
    }


    public JedisCluster getJedisClusterClient() {
        return this.jedisCluster;
    }


    @Override
    public Jedis getResource() {
        return null;
    }


    @Override
    public void close(Jedis jedis) {
    }


    @Override
    public void destroyResource(Jedis jedis) {
    }
}
