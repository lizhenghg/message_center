package com.cracker.api.mc.retry.failstore;


/**
 * 简简单单的自定义失败记录存储异常
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class FailStoreException extends Exception {

    public FailStoreException(String message) {
        super(message);
    }

    public FailStoreException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FailStoreException(Throwable throwable) {
        super(throwable);
    }
}