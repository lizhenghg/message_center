package com.cracker.api.mc.retry;

import com.cracker.api.mc.retry.RetrySchedulerService.RetryListener;


/**
 * 重试调度器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface RetryScheduler {

    /**
     * 启动重试调度
     */
    void start();

    /**
     * 判断启动状态
     * @return true means has started, otherwise not started
     */
    boolean isStarted();

    /**
     * 添加重试监听器
     * @param listener 重试监听器
     */
    void addListener(RetryListener listener);

    /**
     * 提交任务到重试队列
     * @param retryTask 重试任务
     * @return 提交成功与否
     */
    boolean submitTask(RetryTask retryTask);

    /**
     * 停止重试调度
     */
    void stop();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 异常关闭导致缓存中可能存在重试次数不足的数据，在项目重启后
     * 再次重新进入重试队列
     */
    void dealRetryAbnormalShutDown();

}
