package com.cracker.api.mc.mq.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.Objects;

/**
 * jms组件：消费者类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class JmsConsumer implements ExceptionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConsumer.class);

    /**
     * 队列预取值，设置消费者消息缓冲区的大小
     */
    public static final int DEFAULT_QUEUE_PREFETCH = 100;
    private int queuePrefetch = DEFAULT_QUEUE_PREFETCH;

    private final String username;

    private final String password;

    private final String queueName;

    private final String brokerUrl;

    private MessageListener messageListener;

    private Connection connection;

    private Session session;


    public JmsConsumer(final String username, final String password, final String queueName, final String brokerUrl) {
        this(username, password, queueName, brokerUrl, DEFAULT_QUEUE_PREFETCH);
    }


    public JmsConsumer(final String username, final String password, final String queueName, final String brokerUrl,
                       final int queuePrefetch) {
        this.username = Objects.requireNonNull(username);
        this.password = Objects.requireNonNull(password);
        this.queueName = Objects.requireNonNull(queueName);
        this.brokerUrl = Objects.requireNonNull(brokerUrl);
        this.queuePrefetch = queuePrefetch <= 0 ? DEFAULT_QUEUE_PREFETCH : queuePrefetch;
    }


    /**
     * 执行消息监听
     * 执行这一步之前，必须保证messageListener不为空
     * @throws Exception 直接抛异常
     */
    public void start() throws Exception {
        // ActiveMQ的连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.username, this.password, this.brokerUrl);
        this.connection = connectionFactory.createConnection();

        // activeMQ预取策略
        ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
        prefetchPolicy.setQueuePrefetch(this.queuePrefetch);
        ((ActiveMQConnection) this.connection).setPrefetchPolicy(prefetchPolicy);
        this.connection.setExceptionListener(this);
        this.connection.start();

        // 会话采用非事务级别，消息到达机制使用自动通知机制
        this.session = this.connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        Destination destination = this.session.createQueue(this.queueName);
        MessageConsumer consumer = this.session.createConsumer(destination);
        consumer.setMessageListener(Objects.requireNonNull(this.messageListener));
    }

    public void shutdown() {
        try {
            if (this.session != null) {
                this.session.close();
                this.session = null;
            }
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
        } catch (Exception e) {
            LOGGER.error(String.format("fail to shutdown queue: %s", this.queueName), e);
        }
    }


    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = Objects.requireNonNull(messageListener);
    }


    @Override
    public void onException(JMSException e) {
        LOGGER.error(String.format("exception occur!!!, queue: %s", this.queueName), e);
    }
}
