package com.cracker.api.mc.executor.vo;

import com.cracker.api.mc.mq.vo.ConsumerVO;
import com.cracker.api.mc.mq.vo.ProducerVO;
import com.cracker.api.mc.retry.RetryTask;

import java.io.Serializable;

/**
 * 任务处理的最小单元，继承RetryTask，可以实现消息重试
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class TaskVO extends RetryTask implements Serializable {

    private String topic;
    private ProducerVO producerVO;
    private ConsumerVO consumerVO;
    private String callbackUrl;
    private String message;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ProducerVO getProducerVO() {
        return producerVO;
    }

    public void setProducerVO(ProducerVO producerVO) {
        this.producerVO = producerVO;
    }

    public ConsumerVO getConsumerVO() {
        return consumerVO;
    }

    public void setConsumerVO(ConsumerVO consumerVO) {
        this.consumerVO = consumerVO;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("TaskVO[taskId=%s, topic=%s, producerVO=%s, consumerVO=%s, callbackUrl=%s, message=%s]",
                getTaskId(), getTopic(), getProducerVO(), getConsumerVO(), getCallbackUrl(), getMessage());
    }
}
