package com.cracker.api.mc.mq.vo;

import java.io.Serializable;
/**
 * 消息队列-生产者VO: ProducerVO
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ProducerVO implements Serializable {

    private String producerName;

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    @Override
    public String toString() {
        return String.format("ProducerVO[producerName=%s]", this.getProducerName());
    }
}