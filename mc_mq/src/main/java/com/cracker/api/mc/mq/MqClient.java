package com.cracker.api.mc.mq;

import com.cracker.api.mc.mq.activemq.MessageHandler;
import com.cracker.api.mc.mq.activemq.MultiThreadMessageListener;
import com.cracker.api.mc.mq.config.MqConfig;
import com.cracker.api.mc.mq.factory.PoolConsumer;
import com.cracker.api.mc.mq.factory.PoolProducer;
import com.cracker.api.mc.mq.factory.SimpleConsumer;
import com.cracker.api.mc.mq.factory.SimpleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.Objects;

/**
 * MqClient，获取内置MQ操作方法的唯一入口
 * 单例，需传入配置文件地址主动初始化
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-08
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MqClient {


    private static final Logger LOGGER = LoggerFactory.getLogger(MqClient.class);

    private static MqClient instance = null;

    private final ImqProducer producer;
    private final ImqConsumer consumer;

    private IMessageHandler messageHandler;


    public MqClient() {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init MqClient");
        }

        String queueName = Objects.requireNonNull(MqConfig.getInstance().getMqQueueName(), "value of queue name must not be null");
        int queueCount = MqConfig.getInstance().getMqQueueCount();

        if (queueCount > 1) {

            String[] queueNames = getQueueNameArray(queueName, queueCount);

            LOGGER.info("init MqClient with multi queue, queueName: {}, count: {}", queueNames, queueCount);

            this.producer = new PoolProducer(MqConfig.getInstance().getMqBrokerUrl(),
                    MqConfig.getInstance().getMqUsername(),
                    MqConfig.getInstance().getMqPassword(),
                    queueNames);

            this.consumer = new PoolConsumer(MqConfig.getInstance().getMqUsername(),
                    MqConfig.getInstance().getMqPassword(),
                    queueNames,
                    MqConfig.getInstance().getMqBrokerUrl());

        } else {

            LOGGER.info("init MqClient with single queue, queueName: {}", queueName);

            this.producer = new SimpleProducer(MqConfig.getInstance().getMqBrokerUrl(),
                    MqConfig.getInstance().getMqUsername(),
                    MqConfig.getInstance().getMqPassword(),
                    queueName);

            this.consumer = new SimpleConsumer(MqConfig.getInstance().getMqUsername(),
                    MqConfig.getInstance().getMqPassword(),
                    queueName,
                    MqConfig.getInstance().getMqBrokerUrl());

        }
    }

    /**
     * 不作多线程处理
     * @param basePath 传参base path
     */
    public static void init(String basePath) {
        MqConfig.init(basePath);
        instance = new MqClient();
    }

    public static MqClient getInstance() {
        Objects.requireNonNull(instance);
        return instance;
    }

    /**
     * 发送消息到mq队列
     * @param mqMessage 发送的消息实体
     * @return true means successful, otherwise fail
     */
    public boolean send(MqMessage mqMessage) throws Exception {
        Objects.requireNonNull(mqMessage);
        this.producer.send(mqMessage);
        return true;
    }

    /**
     * 设置JmsConsumer消费者监听器
     * 1、把监听器组件拆分，自由组合，可以起到灵活接驳在不同组件的神奇功效
     * 2、把监听器组件拆分，自由组合，可以起到把消费消息的功能迁移到不同模块的神奇功效
     * @param messageHandler 自定义的消息回调顶级父类接口
     */
    public void setHandler(IMessageHandler messageHandler) {

        this.messageHandler = Objects.requireNonNull(messageHandler);

        this.consumer.setMessageListener(new MultiThreadMessageListener(new MessageHandler() {
            @Override
            public void handle(Message message) {
                invokeListener(message);
            }
        }, MqConfig.getInstance().getConsumerQueueThreadCount()));
        this.consumer.start();
    }


    /**
     * 被抽离出来的执行监听器方法
     * @param message 消息实体
     */
    private void invokeListener(Message message) {
        if (this.messageHandler != null) {
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                MqMessage mqMessage = (MqMessage) objectMessage.getObject();
                this.messageHandler.handle(mqMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.error("messageHandler is null");
        }
    }



    private String[] getQueueNameArray(String queueName, int queueCount) {
        String[] queueNames = new String[queueCount];
        for (int i = 0; i < queueCount; i++) {
            queueNames[i] = queueName + "_" + i;
        }
        return queueNames;
    }
}
