package com.cracker.api.mc.mq.activemq;

import com.cracker.api.mc.common.codec.SystemCode;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.mq.config.MqConfig;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.*;

/**
 * jms组件：生产者类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class JmsProducer implements ExceptionListener {


    private static final Logger LOGGER = LoggerFactory.getLogger(JmsProducer.class);

    /**
     * 设置连接的最大连接数|线程池核心线程数
     */
    public final static int DEFAULT_MAX_CONNECTIONS = 10;
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;

    /**
     * 设置线程池核心线程数
     */
    public final static int DEFAULT_MAX_CORE_POOL_SIZE = MqConfig.getInstance().getProducingSendThreadCount();
    private int corePoolSize = DEFAULT_MAX_CORE_POOL_SIZE;


    /**
     * 默认线程最大等待获取结果时间，单位：秒
     */
    public static final int DEFAULT_THREAD_WAITING_TIME = 5;
    private int threadWaitTime = DEFAULT_THREAD_WAITING_TIME;


    /**
     * 设置每个连接中使用的最大活动会话数
     */
    public final static int DEFAULT_MAX_ACTIVE_SESSION_PER_CONNECTION = 300;
    private int maxActiveSessionPerConnection = DEFAULT_MAX_ACTIVE_SESSION_PER_CONNECTION;


    /**
     * 线程池数量
     */
    public static final int DEFAULT_THREAD_POOL_SIZE = 50;
    private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;


    /**
     * 强制使用同步返回数据的格式
     */
    public final static boolean DEFAULT_USE_ASYNC_SEND_FOR_JMS = true;
    private boolean useAsyncSendForJms = DEFAULT_USE_ASYNC_SEND_FOR_JMS;

    /**
     * 是否持久化消息
     */
    public final static boolean DEFAULT_IS_PERSISTENT = true;
    private boolean isPersistent = DEFAULT_IS_PERSISTENT;


    /**
     * 连接地址
     */
    private final String brokerUrl;
    private final String username;
    private final String password;

    private PooledConnectionFactory connectionFactory;

    /**
     * 线程池
     */
    private ExecutorService threadPool = new ThreadPoolExecutor(this.corePoolSize, this.threadPoolSize, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());


    public JmsProducer(final String brokerUrl, final String username, final String password) {
        this(brokerUrl, username, password, DEFAULT_MAX_CONNECTIONS, DEFAULT_MAX_ACTIVE_SESSION_PER_CONNECTION, DEFAULT_THREAD_POOL_SIZE, DEFAULT_USE_ASYNC_SEND_FOR_JMS, DEFAULT_IS_PERSISTENT);
    }

    public JmsProducer(final String brokerUrl, final String username, final String password, final int maxConnections,
                       final int maximumActiveSessionPerConnection, final int threadPoolSize, final boolean useAsyncSendForJms, final boolean isPersistent) {
        this.useAsyncSendForJms = useAsyncSendForJms;
        this.isPersistent = isPersistent;
        this.brokerUrl = brokerUrl;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;
        this.maxActiveSessionPerConnection = maximumActiveSessionPerConnection;
        this.threadPoolSize = threadPoolSize;
        init();
    }

    private void init() {
        // ActiveMQ的连接工厂
        ActiveMQConnectionFactory actualConnectionFactory = new ActiveMQConnectionFactory(this.username, this.password, this.brokerUrl);
        actualConnectionFactory.setUseAsyncSend(this.useAsyncSendForJms);
        // Active中的连接池工厂
        this.connectionFactory = new PooledConnectionFactory(actualConnectionFactory);
        this.connectionFactory.setCreateConnectionOnStartup(true);
        this.connectionFactory.setMaxConnections(this.maxConnections);
        this.connectionFactory.setMaximumActiveSessionPerConnection(this.maxActiveSessionPerConnection);
    }


    /**
     * 经实测，threadPool在使用Thread.sleep(xxx)方法进行测试时，会出现maximumPoolSize失效情况，
     * 也就是说，只有corePoolSize有效，高并发下，最多只能创建corePoolSize条线程
     * 执行发送消息的具体方法
     * @param queueName 队列名称
     * @param object 待发送消息信息
     * @throws Exception 凡是抛异常，表示消息入mq队列失败
     */
    public void send(final String queueName, final Object object) throws Exception {

        Future<Object> future = this.threadPool.submit(new Callable<Object>() {
            @Override
            public Object call() {
                return sendMsg(queueName, object);
            }
        });

        try {
            boolean ret = (boolean) future.get(DEFAULT_THREAD_WAITING_TIME, TimeUnit.SECONDS);
            if (!ret) {
                LOGGER.error("sendMsg to mq fail");
                throw new InternalServerException(SystemCode.INTERNAL_SERVER_ERROR,
                        "sendMsg to mq fail, but it seems still has a chance");
            }
        } catch (Exception e) {
            LOGGER.error("couldn't get result within {} seconds: {}", threadWaitTime, e.getMessage(), e);
            throw e;
        }
    }


    /**
     * 真正执行发送消息的方法
     * @param queueName 队列名称
     * @param object 待发送消息信息
     */
    @SuppressWarnings("unchecked")
    private boolean sendMsg(final String queueName, final Object object) {

        Connection connection = null;
        Session session = null;

        try {
            connection = this.connectionFactory.createConnection();
            // false参数表示为非事务型消息，后面的参数表示消费端消息的确认类型为自动确认
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            // 选择了持久化消息
            producer.setDeliveryMode(DEFAULT_IS_PERSISTENT ? DeliveryMode.PERSISTENT : DeliveryMode.NON_PERSISTENT);
            Message message;

            if (object instanceof Map) {
                message = getMessage(session, (Map<String, Object>) object);
            } else {
                // 该Object一定要实现Serializable接口
                message = session.createObjectMessage((Serializable) object);
            }
            producer.send(message);
            return true;
        } catch (Exception e) {
            LOGGER.error("fail to sendMsg, object: {}, errorMsg: {}", object, e.getMessage(), e);
            return false;
        } finally {
            closeSession(session);
            closeConnection(connection);
        }
    }



    private void closeSession(Session session) {

        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            LOGGER.error("fail to close session", e);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            LOGGER.error("fail to close connection", e);
        }
    }


    private Message getMessage(Session session, Map<String, Object> objectMap) throws JMSException {
        MapMessage message = session.createMapMessage();
        if (Assert.isNotNull(objectMap)) {
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                message.setObject(entry.getKey(), entry.getValue());
            }
        }
        return message;
    }

    @Override
    public void onException(JMSException e) {
        LOGGER.error("JmsProducer, exception occur!!!", e);
    }
}