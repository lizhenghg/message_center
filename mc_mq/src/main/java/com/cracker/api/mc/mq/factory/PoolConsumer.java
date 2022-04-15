package com.cracker.api.mc.mq.factory;

import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.mq.ImqConsumer;
import com.cracker.api.mc.mq.activemq.JmsConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.MessageListener;

/**
 * 消费者组管理类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-08
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class PoolConsumer implements ImqConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolConsumer.class);

    /**
     * 类的导航，把jms组件引用到这里
     */
    private JmsConsumer[] jmsConsumers;


    private String[] queueNames;


    private int queueSize;

    @SuppressWarnings("unused")
    private PoolConsumer() {}

    /**
     * 通过有参构造，把需要用到的属性全部实例化
     * @param username 用户名
     * @param password 密码
     * @param brokerUrl mq url
     * @param queueNames 队列名称数组
     */
    public PoolConsumer(final String username, final String password, final String[] queueNames, final String brokerUrl) {
        if (!Assert.isNotNull(queueNames)) {
            LOGGER.error("fail to construct PoolConsumer, queueName array is null");
            throw new IllegalArgumentException("queueName array is null");
        }
        this.queueNames = queueNames;
        this.queueSize = queueNames.length;

        // 新建消费组
        this.jmsConsumers = new JmsConsumer[this.queueSize];

        for (int i = 0; i < this.queueSize; i++) {

            if (Assert.isEmpty(this.queueNames[i])) {
                LOGGER.error("fail to construct PoolConsumer, queueName is null");
                throw new IllegalArgumentException("queueName is null");
            }

            this.jmsConsumers[i] = new JmsConsumer(username, password, this.queueNames[i], brokerUrl);
        }
    }

    @Override
    public void setMessageListener(MessageListener messageListener) {

        if (messageListener == null) {
            throw new NullPointerException("messageListener is null");
        }

        if (jmsConsumers != null && jmsConsumers.length > 0) {
            for (JmsConsumer jmsConsumer : jmsConsumers) {
                jmsConsumer.setMessageListener(messageListener);
            }
        }
    }


    @Override
    public void start() {
        if (jmsConsumers != null && jmsConsumers.length > 0) {
            try {
                for (JmsConsumer jmsConsumer : jmsConsumers) {
                    jmsConsumer.start();
                }
            } catch (Exception ex) {
                LOGGER.error("PoolConsumer, fail to start JmsConsumer: {}", ex.getMessage(), ex);
            }
        }
    }


    @Override
    public void shutdown() {
        if (jmsConsumers != null && jmsConsumers.length > 0) {
            for (JmsConsumer jmsConsumer : jmsConsumers) {
                jmsConsumer.shutdown();
            }
        }
    }
}