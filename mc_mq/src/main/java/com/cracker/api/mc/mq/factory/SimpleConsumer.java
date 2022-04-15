package com.cracker.api.mc.mq.factory;

import com.cracker.api.mc.mq.ImqConsumer;
import com.cracker.api.mc.mq.activemq.JmsConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MessageListener;

/**
 * 孤独的消费者管理类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-08
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class SimpleConsumer implements ImqConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConsumer.class);

    private JmsConsumer jmsConsumer;


    @SuppressWarnings("unused")
    private SimpleConsumer() {}

    public SimpleConsumer(final String username, final String password, final String queueName, final String brokerUrl) {
        this.jmsConsumer = new JmsConsumer(username, password, queueName, brokerUrl);
    }


    @Override
    public void setMessageListener(MessageListener messageListener) {

        if (messageListener == null) {
            throw new NullPointerException("messageListener is null");
        }

        if (this.jmsConsumer != null) {
            this.jmsConsumer.setMessageListener(messageListener);
        }
    }


    @Override
    public void start() {
        if (this.jmsConsumer != null) {
            try {
                this.jmsConsumer.start();
            } catch (Exception ex) {
                LOGGER.error("SimpleConsumer, fail to start JmsConsumer: {}", ex.getMessage(), ex);
            }
        }
    }


    @Override
    public void shutdown() {
        if (this.jmsConsumer != null) {
            this.jmsConsumer.shutdown();
        }
    }
}