package com.cracker.api.mc.mq.vo;

import java.io.Serializable;

import java.util.Map;


/**
 * 消息发送封装VO：MessageSendingVO
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-12-09
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MessageSendingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String topic;
    private ProducerVO producer;
    private Map<String, ConsumerVO> consumerMap;
    private Integer retryCount;
    private String message;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ProducerVO getProducer() {
        return producer;
    }

    public void setProducer(ProducerVO producer) {
        this.producer = producer;
    }

    public Map<String, ConsumerVO> getConsumerMap() {
        return consumerMap;
    }

    public void setConsumerMap(Map<String, ConsumerVO> consumerMap) {
        this.consumerMap = consumerMap;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("MessageSendingVO[topic=%s, producer=%s, consumerMap=%s, retryCount=%s, message=%s]",
                this.getTopic(), this.getProducer(), this.getConsumerMap(), this.getRetryCount(), this.getMessage());
    }
}