package com.cracker.api.mc.retry;

/**
 * 调度状态
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public enum RetrySchedulerState {

    /**
     * 调度状态：加载
     */
    LOAD,
    /**
     * 调度状态：已启动
     */
    STARTED,
    /**
     * 调度状态：已关闭
     */
    STOPPED;

    private RetrySchedulerState() {}

}
