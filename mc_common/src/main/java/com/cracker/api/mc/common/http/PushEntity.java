package com.cracker.api.mc.common.http;

import java.io.Serializable;

/**
 * http消息推送类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-11
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class PushEntity implements Serializable {

    private String topic;
    private String producerName;
    private String message;
    private long produceTime;

    public PushEntity(String topic, String producerName, String message, long produceTime) {
        this.topic = topic;
        this.producerName = producerName;
        this.message = message;
        this.produceTime = produceTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(long produceTime) {
        this.produceTime = produceTime;
    }

    @Override
    public String toString() {
        return "PushEntity[topic=" + this.getTopic() + ", producerName=" + this.getProducerName() + ", message=" + this.getMessage()
                + ", produceTime=" + this.getProduceTime() + "]";
    }
}