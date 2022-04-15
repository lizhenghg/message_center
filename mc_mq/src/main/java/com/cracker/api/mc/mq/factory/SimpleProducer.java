package com.cracker.api.mc.mq.factory;

import com.cracker.api.mc.mq.ImqProducer;
import com.cracker.api.mc.mq.activemq.JmsProducer;

import java.io.Serializable;
import java.util.Map;

/**
 * 孤独的生产者管理类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-08
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class SimpleProducer implements ImqProducer {


    private JmsProducer jmsProducer;
    private String queueName;

    @SuppressWarnings("unused")
    private SimpleProducer() {}

    public SimpleProducer(final String brokerUrl, final String username, final String password,
                          final String queueName) {
        this.jmsProducer = new JmsProducer(brokerUrl, username, password);
        this.queueName = queueName;
    }

    @Override
    public void send(Map<String, Object> messageMap) throws Exception {
        this.jmsProducer.send(queueName, messageMap);
    }

    @Override
    public void send(Serializable messageObj) throws Exception {
        this.jmsProducer.send(queueName, messageObj);
    }
}