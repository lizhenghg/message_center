package com.cracker.api.mc.scheduler.component.impl;

import com.cracker.api.mc.cache.CacheServerBootstrap;
import com.cracker.api.mc.common.exception.BadRequestException;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.lock.NonDistributedLockClient;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.mq.vo.ConsumerVO;
import com.cracker.api.mc.mq.vo.ProducerVO;
import com.cracker.api.mc.scheduler.api.constants.BusinessCode;
import com.cracker.api.mc.scheduler.bussizcache.SubscriberTableCache;
import com.cracker.api.mc.scheduler.bussizvo.SubscribeTableVO;
import com.cracker.api.mc.scheduler.component.IConsumerComponent;
import com.cracker.api.mc.scheduler.component.validator.ParameterValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息中心消费者业务层业务处理类，类似于Spring的ConsumerServiceImpl
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ConsumerComponent implements IConsumerComponent {


    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerComponent.class);

    private static final String DEFAULT_PRODUCER = "mc_default_producer";
    private static String mc_default_producer = DEFAULT_PRODUCER;

    private final SubscriberTableCache subscriberTableCache = (SubscriberTableCache) CacheServerBootstrap.getCacheServer(SubscriberTableCache.class);

    private final NonDistributedLockClient lockClient = NonDistributedLockClient.getInstance();

    /**
     * 订阅主题
     * @param topic 主题名称
     * @param consumerName 消费者名称
     * @param callbackUrl 消费者通知url
     * @return boolean，true means succeed, false means failed
     * @throws BaseBusinessException 业务异常
     */
    @Override
    public boolean subscribeTopic(String topic, String consumerName, String callbackUrl) throws BaseBusinessException {

        LOGGER.info("ConsumerComponent, subscribe, topic: {}, consumerName: {}, callbackUrl: {}", topic, consumerName, callbackUrl);

        // topic有效性验证
        if (!ParameterValidator.validateTopic(topic)) {
            LOGGER.debug("invalid topic, callbackUrl:{}, consumerName:{}, topic:{}", callbackUrl, consumerName, topic);
            throw new BadRequestException(BusinessCode.SUBSCRIBE_TOPIC_INVALID_TOPIC, BusinessCode.SUBSCRIBE_TOPIC_INVALID_TOPIC_MSG);
        }

        // consumerName有效性验证
        if (!ParameterValidator.validateConsumerName(consumerName)) {
            LOGGER.debug("invalid topic, callbackUrl:{}, consumerName:{}, topic:{}", callbackUrl, consumerName, topic);
            throw new BadRequestException(BusinessCode.SUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME, BusinessCode.SUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME_MSG);
        }

        // callbackUrl有效性验证
        if (!ParameterValidator.validateCallbackUrl(callbackUrl)) {
            LOGGER.debug("invalid topic, callbackUrl:{}, consumerName:{}, topic:{}", callbackUrl, consumerName, topic);
            throw new BadRequestException(BusinessCode.SUBSCRIBE_TOPIC_INVALID_CALLBACK_URL, BusinessCode.SUBSCRIBE_TOPIC_INVALID_CALLBACK_URL_MSG);
        }

        String lockKey = topic + "_" + consumerName;

        try {
            if (this.lockClient.lock(lockKey)) {
                return processSubscribing(topic, consumerName, callbackUrl);
            } else {
                LOGGER.error("fail to lock entity, topic: {}, consumerName: {}, callbackUrl: {}", topic, consumerName, callbackUrl);
                throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
            }
        } finally {
            this.lockClient.unlock(lockKey);
        }
    }



    private boolean processSubscribing(String topic, String consumerName, String callbackUrl) throws BaseBusinessException {

        SubscribeTableVO subscriberTableVO = subscriberTableCache.get(topic);

        if (subscriberTableVO == null) {
            LOGGER.debug("topic is null, topic: {}", topic);

            subscriberTableVO = new SubscribeTableVO();
            subscriberTableVO.setTopic(topic);

            // 生成一个默认生产者，后续改掉这块逻辑
            // 理论上应该先有主题和生产者，再有消费者
            ProducerVO producerVO = new ProducerVO();
            producerVO.setProducerName(mc_default_producer);
            subscriberTableVO.setProducer(producerVO);

            // 初始化消费者队列
            subscriberTableVO.setConsumers(new HashMap<>(16));
        }

        // 记录consumer
        Map<String, ConsumerVO> consumerMap = subscriberTableVO.getConsumers();
        if (!consumerMap.containsKey(consumerName)) {
            // 不存在，则新建
            LOGGER.info("add consumer, topic: {}, consumer: {}", topic, consumerName);
            ConsumerVO consumerVO = new ConsumerVO();
            consumerVO.setTopic(topic);
            consumerVO.setConsumerName(consumerName);
            consumerVO.setCallbackUrl(callbackUrl);
            consumerVO.setDescription(String.format("%s##%s##%s", topic, consumerName, callbackUrl));
            consumerMap.put(consumerName, consumerVO);
        } else {
            // 存在，则更新
            // 理论上不应该这么容易更新
            // 后续应考虑加上权限控制
            ConsumerVO consumerVO = consumerMap.get(consumerName);
            consumerVO.setCallbackUrl(callbackUrl);
            consumerMap.put(consumerName, consumerVO);
        }

        // 存入缓存
        if (subscriberTableCache.put(topic, subscriberTableVO)) {
            LOGGER.info("add consumer successfully!!");
        } else {
            LOGGER.error("fail to add consumer");
            throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
        }
        return true;
    }



    /**
     * 撤销指定主题下的指定消费者
     * @param topic 主题名称
     * @param consumerName 消费者名称
     * @return SubscribeTableVO，撤销主题的tableVO
     * @throws BaseBusinessException 业务异常
     */
    @Override
    public SubscribeTableVO unSubscribeWithConsName(String topic, String consumerName) throws BaseBusinessException {

        LOGGER.info("subscribe, topic: {}, consumerName: {}", topic, consumerName);

        // topic有效性验证
        if (!ParameterValidator.validateTopic(topic)) {
            LOGGER.debug("invalid topic, consumerName:{}, topic:{}", consumerName, topic);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_TOPIC, BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_TOPIC_MSG);
        }

        // consumerName有效性验证
        if (!ParameterValidator.validateConsumerName(consumerName)) {
            LOGGER.debug("invalid topic, consumerName:{}, topic:{}", consumerName, topic);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME, BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME_MSG);
        }

        String lockKey = topic + "_" + consumerName;

        try {
            if (this.lockClient.lock(lockKey)) {
                return processingUnSubscribeWithConsName(topic, consumerName);
            } else {
                LOGGER.error("unsubscribe, fail to lock entity, topic: {}, consumerName: {}", topic, consumerName);
                throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
            }
        } finally {
            this.lockClient.unlock(lockKey);
        }
    }


    private SubscribeTableVO processingUnSubscribeWithConsName(String topic, String consumerName) throws BaseBusinessException {

        SubscribeTableVO subscriberTableVO = subscriberTableCache.get(topic);

        if (subscriberTableVO == null) {
            LOGGER.debug("topic is null, topic: {}", topic);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC_MSG);
        }

        Map<String, ConsumerVO> consumerMap = subscriberTableVO.getConsumers();
        if (!Assert.isNotNull(consumerMap)) {
            LOGGER.error("cache error! consumerMap is null, topic: {}", topic);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER_MSG);
        }

        if (consumerMap.containsKey(consumerName)) {
            ConsumerVO consumerVO = consumerMap.remove(consumerName);
            if (consumerVO != null) {
                // 更新缓存
                if (subscriberTableCache.put(topic, subscriberTableVO)) {
                    LOGGER.info("unsubscribe successfully!!, consumer: {}", consumerVO.toString());
                    // 返回更新后的实体
                    return subscriberTableVO;
                } else {
                    LOGGER.error("fail to update cache, topic: {}", topic);
                    throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
                }
            } else {
                LOGGER.error("error occur! consumerVO is null, topic: {}", topic);
                throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
            }
        } else {
            LOGGER.debug("no such consumer, topic: {}, consumer: {}", topic, consumerName);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER_MSG);
        }
    }


    /**
     * 撤销指定主题(该主题及其包含的所有消费者全部被删除)
     * @param topic 主题名称
     * @return SubscribeTableVO，撤销主题的tableVO
     * @throws BaseBusinessException 业务异常
     */
    @Override
    public boolean unSubscribeTopic(String topic) throws BaseBusinessException {

        LOGGER.info("unSubscribeTopic, topic: {}", topic);

        // topic有效性验证
        if (!ParameterValidator.validateTopic(topic)) {
            LOGGER.debug("invalid topic, topic:{}", topic);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_TOPIC, BusinessCode.UNSUBSCRIBE_TOPIC_INVALID_TOPIC_MSG);
        }

        String lockKey = topic + "_";

        try {
            if (this.lockClient.lock(lockKey)) {
                return processingUnsubscribing(topic);
            } else {
                LOGGER.error("unsubscribe, fail to lock entity, topic: {}", topic);
                throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
            }
        } finally {
            this.lockClient.unlock(lockKey);
        }
    }


    private boolean processingUnsubscribing(String topic) throws BaseBusinessException {

        SubscribeTableVO subscriberTableVO = subscriberTableCache.get(topic);

        if (subscriberTableVO == null) {
            LOGGER.debug("topic is null, topic: {}", topic);
            throw new BadRequestException(BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC, BusinessCode.UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC_MSG);
        }
        subscriberTableCache.hRemove(topic);
        return true;
    }
}