package com.cracker.api.mc.retry.strategy;

/**
 * 重试策略
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface RetryStrategy {

    /**
     * 计算当前梯级的等待重试时长
     * @param currentRetryLevel 当前重试梯级
     * @return 等待重试时长
     */
    public abstract long calculateRetryTime(int currentRetryLevel);

    /**
     * 计算重试梯级
     * @return 重试梯级
     */
    int maxCount();

}
