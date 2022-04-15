package com.cracker.api.mc.mq.factory;

import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.mq.ImqProducer;
import com.cracker.api.mc.mq.activemq.JmsProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * 生产者组管理类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-08
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class PoolProducer implements ImqProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolProducer.class);

    private JmsProducer[] jmsProducers;

    private String[] queueNames;

    private int queueSize;

    private int currentIndex = 0;

    @SuppressWarnings("unused")
    private PoolProducer() {}

    public PoolProducer(final String brokerUrl, final String username, final String password, String[] queueNames) {
        if (queueNames == null || queueNames.length == 0) {
            LOGGER.error("fail to construct PoolProducer, queueName array is null");
            throw new IllegalArgumentException("PoolProducer, queueName array is null");
        }
        this.queueNames = queueNames;
        this.queueSize = queueNames.length;

        // 新建生产者数组
        this.jmsProducers = new JmsProducer[this.queueSize];
        for (int i = 0; i < this.queueSize; i++) {
            if (Assert.isEmpty(this.queueNames[i])) {
                LOGGER.error("fail to construct PoolProducer, queueName is null");
                throw new IllegalArgumentException("queueName is null");
            }
            this.jmsProducers[i] = new JmsProducer(brokerUrl, username, password);
        }
    }

    @Override
    public void send(Map<String, Object> messageMap) throws Exception {
        int index = selectIndex();
        this.jmsProducers[index].send(queueNames[index], messageMap);
    }

    @Override
    public void send(Serializable messageObj) throws Exception {
        int index = selectIndex();
        this.jmsProducers[index].send(queueNames[index], messageObj);
    }

    /**
     * 选择一个范围内的位置
     * 不要求绝对的平均分布，不做去重处理，不做分布式同步处理
     * 简单的轮询
     * @return 队列数组下标
     */
    private int selectIndex() {
        // 先取一个副本
        // 直接操作this.currentIndex有范围溢出风险
        int currentIndex = this.currentIndex;

        if (currentIndex >= this.queueSize) {
            currentIndex = 0;
            this.currentIndex = 0;
        }
        this.currentIndex++;
        return currentIndex;
    }
}
