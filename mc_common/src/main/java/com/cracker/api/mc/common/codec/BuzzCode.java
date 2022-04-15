package com.cracker.api.mc.common.codec;


/**
 * 系统业务code：BuzzCode
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-14
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public enum BuzzCode {

    /**
     * 策略模式下，redis连接池业务获取保管对象的key
     */
    STRATEGY_REDIS_POOL(10001, "策略模式下，redis连接池业务获取保管对象的key"),

    /**
     * 集群模式
     */
    TYPE_CLUSTER_REDIS_POOL(1, "集群模式"),

    /**
     * 哨兵模式
     */
    TYPE_GUARD_REDIS_POOL(2, "哨兵模式"),

    /**
     * 单例模式
     */
    TYPE_SIMPLE_REDIS_POOL(3, "单例模式"),


    // 如下为主题业务code

    /**
     * 主题不存在
     */
    NO_SUCH_TOPIC(30001, "不存在该Topic"),


    /**
     * 指定主题不存在consumer
     */
    NO_CONSUMER(30002, "指定主题不存在消费者"),

    /**
     * 指定主题不存在producer
     */
    NO_PRODUCER(30003, "指定主题不存在生产者");


    private final int code;
    private final String desc;

    BuzzCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}