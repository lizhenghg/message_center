package com.cracker.api.mc.mq.vo;

import java.io.Serializable;

/**
 * 消息队列-消费者VO: ConsumerVO
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ConsumerVO implements Serializable {

    private String topic;
    private String consumerName;
    private String callbackUrl;
    private String description;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("ConsumerVO[topic=%s, consumerName=%s, callbackUrl=%s, description=%s]",
                this.getTopic(), this.getConsumerName(), this.getCallbackUrl(), this.getDescription());
    }
}