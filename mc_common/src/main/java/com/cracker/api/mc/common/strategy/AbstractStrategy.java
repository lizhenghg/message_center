package com.cracker.api.mc.common.strategy;


/**
 *
 * 策略抽象类：AbstractStrategy
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractStrategy<T> implements Strategy<T> {

    @Override
    public T exec(Object... objects) {
        return null;
    }

    @Override
    public <T> T exec() {
        return null;
    }
}