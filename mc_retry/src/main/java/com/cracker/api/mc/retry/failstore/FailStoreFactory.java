package com.cracker.api.mc.retry.failstore;

import com.cracker.api.mc.retry.config.RetryConfig;

/**
 * 失败记录存储工厂接口
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface FailStoreFactory {

    /**
     * 获取失败记录存储
     * @param retryConfig 重试配置文件
     * @param storePath 失败记录存储路径
     * @return FailStore
     */
    FailStore getFailStore(RetryConfig retryConfig, String storePath);

}
