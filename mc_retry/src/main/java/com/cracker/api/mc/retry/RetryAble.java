package com.cracker.api.mc.retry;

/**
 * 重试任务接口，面向业务方法
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface RetryAble {

    /**
     * 重试任务，面向业务方法
     * @return 返回的业务方法，true表示执行成功；false表示执行失败
     * @throws Exception 业务异常
     */
    public abstract boolean retryAble() throws Exception;

}
