package com.cracker.api.mc.scheduler.bussizvo;

/**
 * 消息生产VO
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MessageProducingVO {

    private String topic;
    private String producerName;
    private String message;
    private Integer retryingCount;

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

    public int getRetryingCount() {
        return retryingCount;
    }

    public void setRetryingCount(int retryingCount) {
        this.retryingCount = retryingCount;
    }

    public void incRetryingCount() {
        this.retryingCount++;
    }

    @Override
    public String toString() {
        return "MessageProducingVO[topic=" + this.getTopic() + ", producerName=" + this.getProducerName() +
                ", message=" + this.getMessage() + ", retryingCount=" + this.getRetryingCount() + "]";
    }
}