package com.cracker.api.mc.mq.config;

import com.cracker.api.mc.common.config.AbstractConfig;
import com.cracker.api.mc.common.config.ConfigAdapter;

import java.util.Objects;

/**
 * mc_mq项目的配置文件操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MqConfig {


    /**
     * 如下为mq.properties配置文件的所有key
     */
    private static final String PRODUCING_QUEUE_THREAD_COUNT = "mq_producing_queue_thread_count";
    private static final String PRODUCING_QUEUE_THREAD_PERIOD = "mq_producing_queue_thread_period";
    private static final String PRODUCING_QUEUE_RETRY_COUNT = "mq_producing_queue_retryCount";
    private static final String PRODUCING_QUEUE_MAXIMUM_POOL_SIZE = "mq_producing_queue_maximumPoolSize";
    private static final String PRODUCING_SEND_THREAD_COUNT = "mq_producing_send_thread_count";
    private static final String CONSUMER_QUEUE_THREAD_COUNT = "mq_consumer_queue_thread_count";
    private final static String MQ_QUEUE_NAME = "mq_queue_name";
    private final static String MQ_QUEUE_COUNT = "mq_queue_count";
    private final static String MQ_BROKER_URL = "mq_broker_url";
    private final static String MQ_USERNAME = "mq_username";
    private static final String MQ_PASSWORD = "mq_password";


    /**
     * 类的导航，把抽象配置处理类导航到这里使用
     */
    private final AbstractConfig configInstance;

    /**
     * 一个配置文件对应一个类，使用单例全局唯一
     */
    private static volatile MqConfig instance;

    /**
     * 配置文件路径
     */
    private static String filePath;

    /**
     * 扔外部使用，配置文件初始化之前必须先调用这个方法，引入文件路径
     * @param configPath 文件路径，非完整
     */
    public static void init(String configPath) {
        filePath = configPath + "mq.properties";
    }

    /**
     * 防止外部调用
     */
    private MqConfig() {
        this(filePath);
    }

    /**
     * 1、防止外部调用
     * 2、初始化配置文件处理类
     * @param filePath 配置文件完整路径
     */
    private MqConfig(String filePath) {
        this.configInstance = new ConfigAdapter(filePath);
    }

    /**
     * 获取MqConfig单例的方法
     * @return MqConfig单例
     */
    public static MqConfig getInstance() {
        if (instance == null) {
            synchronized (MqConfig.class) {
                if (instance == null) {
                    instance = new MqConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    /**
     * 如下为获取配置文件中指定key的value
     */

    public int getProducingQueueThreadCount() {
        return this.configInstance.getIntSetting(PRODUCING_QUEUE_THREAD_COUNT);
    }

    public long getProducingQueueThreadPeriod() {
        return this.configInstance.getLongSetting(PRODUCING_QUEUE_THREAD_PERIOD);
    }

    public int getProducingQueueRetryCount() {
        return this.configInstance.getIntSetting(PRODUCING_QUEUE_RETRY_COUNT);
    }

    public int getProducingQueueMaximumPoolSize() {
        return this.configInstance.getIntSetting(PRODUCING_QUEUE_MAXIMUM_POOL_SIZE);
    }

    public int getProducingSendThreadCount() {
        return this.configInstance.getIntSetting(PRODUCING_SEND_THREAD_COUNT);
    }

    public int getConsumerQueueThreadCount() {
        return this.configInstance.getIntSetting(CONSUMER_QUEUE_THREAD_COUNT);
    }

    public String getMqQueueName() {
        return this.configInstance.getStringSetting(MQ_QUEUE_NAME);
    }

    public String getMqBrokerUrl() {
        return this.configInstance.getStringSetting(MQ_BROKER_URL);
    }

    public String getMqUsername() {
        return this.configInstance.getStringSetting(MQ_USERNAME);
    }

    public String getMqPassword() {
        return this.configInstance.getStringSetting(MQ_PASSWORD);
    }

    public int getMqQueueCount() {
        return this.configInstance.getIntSetting(MQ_QUEUE_COUNT);
    }

    public void reload() {
        this.configInstance.reload();
    }
}