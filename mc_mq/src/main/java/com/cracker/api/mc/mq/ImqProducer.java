package com.cracker.api.mc.mq;

import java.io.Serializable;

import java.util.Map;

/**
 * 消息中心顶级父类消费者接口
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface ImqProducer {

    /**
     * 发送Map消息
     * @param messageMap map消息
     * @throws Exception 不可思议的exception
     */
    void send(Map<String, Object> messageMap) throws Exception;

    /**
     * 发送obj消息
     * @param messageObj 序列化对象
     * @throws Exception 不可思议的exception
     */
    void send(Serializable messageObj) throws Exception;

}