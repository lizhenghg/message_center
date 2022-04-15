package com.cracker.api.mc.scheduler.queue;


import com.cracker.api.mc.cache.CacheServerBootstrap;
import com.cracker.api.mc.common.codec.BuzzCode;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.mq.MqClient;
import com.cracker.api.mc.mq.MqMessage;
import com.cracker.api.mc.mq.config.MqConfig;
import com.cracker.api.mc.mq.vo.ConsumerVO;
import com.cracker.api.mc.mq.vo.MessageSendingVO;
import com.cracker.api.mc.mq.vo.ProducerVO;
import com.cracker.api.mc.scheduler.bussizcache.SubscriberTableCache;
import com.cracker.api.mc.scheduler.bussizvo.MessageProducingVO;
import com.cracker.api.mc.scheduler.bussizvo.SubscribeTableVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.Map;


/**
 * 生产者生产消息处理器，也就是处理进FQueue队列的原始数据，塞进activeMQ和kafka
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ProducingProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducingProcessor.class);
    private static final Logger SUCCESSFUL_LOGGER = LoggerFactory.getLogger("schedulerSuccessfulRecordLogger");
    private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("schedulerFailedRecordLogger");


    /**
     * 基于文件系统的FQueue队列，用于存放消息
     */
    private Queue<MessageProducingVO> messageProducingQueue;

    /**
     * 缓存组件全家桶
     */
    private final SubscriberTableCache subscriberTableCache =
            (SubscriberTableCache) CacheServerBootstrap.getCacheServer(SubscriberTableCache.class);

    /**
     * 进队最大重试次数
     */
    private final int maxRetryCount = MqConfig.getInstance().getProducingQueueRetryCount();

    @SuppressWarnings("unused")
    private ProducingProcessor() {}

    public ProducingProcessor(Queue<MessageProducingVO> messageProducingQueue) {

        this.messageProducingQueue = Objects.requireNonNull(messageProducingQueue);

    }

    @Override
    public void run() {
        MessageProducingVO messageProducingVO = this.messageProducingQueue.poll();
        if (messageProducingVO == null) {
            return;
        }
        LOGGER.debug("get a messageProducingVO from queue, vo: {}", messageProducingVO);

        trySendingMessage(messageProducingVO);
    }

    /**
     * 异步发送消息入队
     * @param messageProducingVO 消息实体
     */
    private void trySendingMessage(MessageProducingVO messageProducingVO) {

        boolean ret = sendMessage(messageProducingVO);

        // 假如发送失败，重新入队
        if (!ret) {
            if (messageProducingVO.getRetryingCount() >= maxRetryCount) {
                // 记录错误日志，连续5次重试发送到mq均失败，放弃治疗
                FAILED_LOGGER.error("retryingCount is more than {}, producer: {}, topic: {}, message: {}",
                        maxRetryCount,
                        messageProducingVO.getProducerName(),
                        messageProducingVO.getTopic(),
                        messageProducingVO.getMessage());
                LOGGER.error("fail to send message, retryingCount is more than {}", maxRetryCount);
            } else {
                messageProducingVO.incRetryingCount();
                // 进不了fQueue，放弃治疗
                if (!this.messageProducingQueue.offer(messageProducingVO)) {
                    // 记录错误日志
                    FAILED_LOGGER.error("fail to put vo in fQueue, producer: {}, topic: {}, message: {}",
                            messageProducingVO.getProducerName(),
                            messageProducingVO.getTopic(),
                            messageProducingVO.getMessage());
                    LOGGER.error("fail to put vo in fQueue, vo: {}", messageProducingVO.toString());
                }
            }
        }
    }


    private boolean sendMessage(MessageProducingVO messageProducingVO) {

        boolean ret = false;

        String topic = messageProducingVO.getTopic();
        String producerName = messageProducingVO.getProducerName();
        String message = messageProducingVO.getMessage();

        SubscribeTableVO subscribeTableVO = this.subscriberTableCache.get(topic);

        if (subscribeTableVO == null) {
            // 记录入队失败的错误日志
            FAILED_LOGGER.info("errorCode: {}, errorMessage: {}, fail to put vo in mq. producer: {}, topic: {}, message: {}",
                    BuzzCode.NO_SUCH_TOPIC.getCode(),
                    BuzzCode.NO_SUCH_TOPIC.getDesc(),
                    producerName,
                    topic,
                    message);
            LOGGER.warn("ProducingProcessor, sendMessage, this topic is null, vo: {}", messageProducingVO);
            ret = true;

        } else {

            Map<String, ConsumerVO> consumers = subscribeTableVO.getConsumers();
            ProducerVO producerVO = subscribeTableVO.getProducer();

            if (!Assert.isNotNull(consumers)) {
                // 记录入队失败的错误日志
                FAILED_LOGGER.info("errorCode: {}, errorMessage: {}, fail to put vo in mq. producer: {}, topic: {}, message: {}",
                        BuzzCode.NO_CONSUMER.getCode(),
                        BuzzCode.NO_CONSUMER.getDesc(),
                        producerName,
                        topic,
                        message);
                LOGGER.warn("ProducingProcessor, sendMessage, this topic has no consumers, vo: {}", messageProducingVO);
                ret = true;
            } else {

                if (producerVO == null) {
                    // 记录入队失败的错误日志
                    FAILED_LOGGER.info("errorCode: {}, errorMessage: {}, fail to put vo in mq. producer: {}, topic: {}, message: {}",
                            BuzzCode.NO_PRODUCER.getCode(),
                            BuzzCode.NO_PRODUCER.getDesc(),
                            producerName,
                            topic,
                            message);
                    LOGGER.warn("ProducingProcessor, sendMessage, this topic has no producer, vo: {}", messageProducingVO);
                    ret = true;
                } else {

                    if (!producerVO.getProducerName().equals(producerName)
                            && !Assert.isEmpty(producerName)) {
                        LOGGER.warn("this topic's producer_name is not equal with parameter, topic's producer_name: {}, parameter's: {}",
                                producerVO.getProducerName(), producerName);
                        producerVO = new ProducerVO();
                        producerVO.setProducerName(producerName);
                    }
                    // 消息发送VO
                    MessageSendingVO messageSendingVO = new MessageSendingVO();
                    messageSendingVO.setTopic(topic);
                    messageSendingVO.setConsumerMap(consumers);
                    messageSendingVO.setProducer(producerVO);
                    messageSendingVO.setMessage(message);
                    // 初始化重试次数为0，处理失败时增加
                    messageSendingVO.setRetryCount(0);

                    MqMessage mqMessage = new MqMessage();
                    mqMessage.setTitle(topic);
                    mqMessage.setContent(messageSendingVO);

                    try {
                        ret = MqClient.getInstance().send(mqMessage);
                        // 没报异常，表示发送消息到mq队列成功
                        // 用专门的logger记录成专门的日志文件
                        SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, message: {}",
                                producerVO.getProducerName(),
                                topic,
                                message);
                    } catch (Exception ex) {
                        LOGGER.error(String.format("send message fail, vo: %s", messageProducingVO), ex);
                    }
                }
            }
        }
        return ret;
    }
}
