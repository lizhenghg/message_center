package com.cracker.api.mc.cache.config;

import com.cracker.api.mc.common.config.AbstractConfig;
import com.cracker.api.mc.common.config.ConfigAdapter;

import java.util.Objects;

/**
 * mc_cache项目的配置文件操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-22
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class CacheConfig {


    /**
     * 如下为cache.properties配置文件的所有key
     */

    private static final String REDIS_HOST = "cache_redis_host";
    private static final String REDIS_PASSWORD = "cache_redis_password";
    private static final String REDIS_TYPE = "cache_redis_type";
    private static final String REDIS_SENTINEL_MASTER_NAME = "cache_redis_sentinel_master_name";
    private static final String COMMAND_CLASS = "cache_command_class";
    private static final String EXECUTOR_CLASS = "cache_executor_class";
    private static final String CACHE_PACKAGE = "cache_package";
    private static final String CACHE_VO_PACKAGE = "cache_vo_package";

    /**
     * 类的导航，把抽象配置处理类导航到这里使用
     */
    private final AbstractConfig configInstance;

    /**
     * 一个配置文件对应一个类，使用单例全局唯一
     */
    private static volatile CacheConfig instance;

    /**
     * 配置文件路径
     */
    private static String filePath;

    /**
     * 扔外部使用，配置文件初始化之前必须先调用这个方法，引入文件路径
     * @param configPath 文件路径，非完整
     */
    public static void init(String configPath) {
        filePath = configPath + "cache.properties";
    }

    /**
     * 防止外部调用
     */
    private CacheConfig() {
        this(filePath);
    }

    /**
     * 1、防止外部调用
     * 2、初始化配置文件处理类
     * @param filePath 配置文件完整路径
     */
    private CacheConfig(String filePath) {
        this.configInstance = new ConfigAdapter(filePath);
    }

    /**
     * 获取CacheConfig单例的方法
     * @return CacheConfig单例
     */
    public static CacheConfig getInstance() {
        if (instance == null) {
            synchronized (CacheConfig.class) {
                if (instance == null) {
                    instance = new CacheConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    /**
     * 如下为获取配置文件中指定key的value
     */

    public String getRedisHost() {
        return this.configInstance.getStringSetting(REDIS_HOST);
    }

    public String getRedisPassword() {
        return this.configInstance.getStringSetting(REDIS_PASSWORD);
    }

    public int getRedisType() {
        return this.configInstance.getIntSetting(REDIS_TYPE);
    }

    public String getRedisSentinelMasterName() {
        return this.configInstance.getStringSetting(REDIS_SENTINEL_MASTER_NAME);
    }

    public String getExecutorClassName() {
        return this.configInstance.getStringSetting(EXECUTOR_CLASS);
    }

    public String getCommandClassName() {
        return this.configInstance.getStringSetting(COMMAND_CLASS);
    }

    public String getCachePackage() {
        return this.configInstance.getStringSetting(CACHE_PACKAGE);
    }

    public String getCacheVoPackage() {
        return this.configInstance.getStringSetting(CACHE_VO_PACKAGE);
    }

    public void reload() {
        this.configInstance.reload();
    }
}