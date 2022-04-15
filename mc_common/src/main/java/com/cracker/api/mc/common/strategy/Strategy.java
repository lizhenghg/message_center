package com.cracker.api.mc.common.strategy;

/**
 *
 * 策略模式顶级父接口：Strategy
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface Strategy<T> {

    /**
     * 有参策略方法
     * @param objects 待传递params
     * @return 将要返回的泛型对象
     */
    T exec(Object ... objects);

    /**
     * 无参策略方法
     * @return 泛型对象
     */
    <T> T exec();
}