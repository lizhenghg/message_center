package com.cracker.api.mc.scheduler.component;


import com.cracker.api.mc.common.exception.BaseBusinessException;

/**
 * 消息中心生产者业务层业务处理类父接口，类似于Spring的IProducerService
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IProducerComponent {

    /**
     * 生产者生产消息
     * @param topic 消息所属主题
     * @param message 消息content
     * @param producerName 生产者名称
     * @return true is succeed, false is failed
     * @throws BaseBusinessException 业务异常
     */
    public abstract boolean produceMessage(String topic, String message, String producerName) throws BaseBusinessException;

}
