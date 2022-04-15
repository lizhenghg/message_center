package com.cracker.api.mc.scheduler.component;

import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.scheduler.bussizvo.SubscribeTableVO;

/**
 * 消息中心消费者业务层业务处理类父接口，类似于Spring的IConsumerService
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IConsumerComponent {

    /**
     * 订阅主题
     * @param topic 主题名称
     * @param consumerName 消费者名称
     * @param callbackUrl 消费者通知url
     * @return boolean，true means succeed, false means failed
     * @throws BaseBusinessException 业务异常
     */
    public abstract boolean subscribeTopic(String topic, String consumerName, String callbackUrl) throws BaseBusinessException;

    /**
     * 撤销指定主题下的指定消费者
     * @param topic 主题名称
     * @param consumerName 消费者名称
     * @return SubscribeTableVO，撤销主题的tableVO
     * @throws BaseBusinessException 业务异常
     */
    public abstract SubscribeTableVO unSubscribeWithConsName(String topic, String consumerName) throws BaseBusinessException;

    /**
     * 撤销指定主题(该主题及其包含的所有消费者全部被删除)
     * @param topic 主题名称
     * @return boolean，撤销主题的boolean
     * @throws BaseBusinessException 业务异常
     */
    public abstract boolean unSubscribeTopic(String topic) throws BaseBusinessException;
}
