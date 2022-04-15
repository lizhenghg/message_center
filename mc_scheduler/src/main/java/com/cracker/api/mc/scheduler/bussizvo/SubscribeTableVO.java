package com.cracker.api.mc.scheduler.bussizvo;

import com.cracker.api.mc.mq.vo.ConsumerVO;
import com.cracker.api.mc.mq.vo.ProducerVO;

import java.io.Serializable;

import java.util.Map;

/**
 * 订阅模式表格|类型VO: SubscribeTableVO
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class SubscribeTableVO implements Serializable {

    private String topic;
    private ProducerVO producer;
    private Map<String, ConsumerVO> consumers;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public ProducerVO getProducer() {
        return producer;
    }

    public void setProducer(ProducerVO producerVO) {
        this.producer = producerVO;
    }

    public Map<String, ConsumerVO> getConsumers() {
        return consumers;
    }

    public void setConsumers(Map<String, ConsumerVO> consumers) {
        this.consumers = consumers;
    }

    @Override
    public String toString() {
        return "SubscribeTableVO [topic=" + topic + ", producer=" + producer + ", consumers="
                + consumers + "]";
    }
}