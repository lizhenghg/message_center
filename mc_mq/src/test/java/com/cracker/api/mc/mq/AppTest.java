package com.cracker.api.mc.mq;


import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * Unit test for simple App
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-23
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class AppTest /*extends TestCase*/ {

//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//        System.out.println("setUp , hashCode = " + hashCode());
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//        System.out.println("tearDown , hashCode = " + hashCode());
//    }


    public void testActiveMqReceive() {

        ConnectionFactory connectionFactory;
        Connection connection = null;

        Session session;

        Destination destination;

        MessageConsumer consumer;

        connectionFactory = new ActiveMQConnectionFactory(
                "ucap",
                "update1qa0ok",
                //"failover:(tcp://192.168.1.237:61616,tcp://192.168.1.238:61616,tcp://192.168.1.239:61616)?timeout=10000&randomize=false&priorityBackup=true"
                "failover:(tcp://192.168.1.237:61616,tcp://192.168.1.237:61617,tcp://192.168.1.237:61618," +
                        "tcp://192.168.1.238:61616,tcp://192.168.1.238:61617,tcp://192.168.1.238:61618," +
                        "tcp://192.168.1.239:61616,tcp://192.168.1.239:61617,tcp://192.168.1.239:61618)?timeout=60000"
        );

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            destination = session.createQueue("activeMq_queue");

            consumer = session.createConsumer(destination);

            int maxCount = 10000000;
            int defaultCount = 1;

            do {

                TextMessage message = (TextMessage) consumer.receive();

                if (null != message) {
                    System.out.println("收到消息: " + message.getText());
                }
            } while (++defaultCount < maxCount);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    public void testActiveMqReceive237() {

        ConnectionFactory connectionFactory;
        Connection connection = null;

        Session session;

        Destination destination;

        MessageConsumer consumer;

        connectionFactory = new ActiveMQConnectionFactory(
                "ucap",
                "update1qa0ok",
                //"failover:(tcp://192.168.1.237:61616,tcp://192.168.1.238:61616,tcp://192.168.1.239:61616)?timeout=10000&randomize=false&priorityBackup=true"
                "failover:(tcp://192.168.1.237:61616,tcp://192.168.1.238:61616,tcp://192.168.1.239:61616)?timeout=10000&randomize=false&priorityBackup=true"
        );

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            destination = session.createQueue("activeMq_queue");

            consumer = session.createConsumer(destination);

            int maxCount = 10000000;
            int defaultCount = 1;

            do {

                TextMessage message = (TextMessage) consumer.receive();

                if (null != message) {
                    System.out.println("237 收到消息: " + message.getText());
                }
            } while (++defaultCount < maxCount);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    public void testActiveMqReceive238() {

        ConnectionFactory connectionFactory;
        Connection connection = null;

        Session session;

        Destination destination;

        MessageConsumer consumer;

        connectionFactory = new ActiveMQConnectionFactory(
                "ucap",
                "update1qa0ok",
                //"failover:(tcp://192.168.1.237:61616,tcp://192.168.1.238:61616,tcp://192.168.1.239:61616)?timeout=10000&randomize=false&priorityBackup=true"
                "failover:(tcp://192.168.1.238:61616,tcp://192.168.1.238:61617,tcp://192.168.1.238:61618)?timeout=60000"
        );

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            destination = session.createQueue("activeMq_queue");

            consumer = session.createConsumer(destination);

            int maxCount = 10000000;
            int defaultCount = 1;

            do {

                TextMessage message = (TextMessage) consumer.receive();

                if (null != message) {
                    System.out.println("238 收到消息: " + message.getText());
                }
            } while (++defaultCount < maxCount);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    public void testActiveMqReceive239() {

        ConnectionFactory connectionFactory;
        Connection connection = null;

        Session session;

        Destination destination;

        MessageConsumer consumer;

        connectionFactory = new ActiveMQConnectionFactory(
                "ucap",
                "update1qa0ok",
                //"failover:(tcp://192.168.1.237:61616,tcp://192.168.1.238:61616,tcp://192.168.1.239:61616)?timeout=10000&randomize=false&priorityBackup=true"
                "failover:(tcp://192.168.1.239:61616,tcp://192.168.1.239:61617,tcp://192.168.1.239:61618)?timeout=60000"
        );

        try {
            connection = connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            destination = session.createQueue("activeMq_queue");

            consumer = session.createConsumer(destination);

            int maxCount = 10000000;
            int defaultCount = 1;

            do {

                TextMessage message = (TextMessage) consumer.receive();

                if (null != message) {
                    System.out.println("239 收到消息: " + message.getText());
                }
            } while (++defaultCount < maxCount);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }


    public void testActiveMqSend() {

        ConnectionFactory connectionFactory;
        Connection connection = null;
        Session session;

        Destination destination;

        MessageProducer producer;

        connectionFactory = new ActiveMQConnectionFactory(
                "ucap",
                "update1qa0ok",
                "failover:(tcp://192.168.1.237:61616,tcp://192.168.1.238:61616,tcp://192.168.1.239:61616)?timeout=10000&randomize=false&priorityBackup=true");
//                "failover:(tcp://192.168.1.237:61616,tcp://192.168.1.237:61617,tcp://192.168.1.237:61618," +
//                        "tcp://192.168.1.238:61616,tcp://192.168.1.238:61617,tcp://192.168.1.238:61618," +
//                        "tcp://192.168.1.239:61616,tcp://192.168.1.239:61617,tcp://192.168.1.239:61618)?timeout=10000");
        try {


            connection =connectionFactory.createConnection();

            connection.start();

            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);

            destination = session.createQueue("activeMq_queue");


            producer =session.createProducer(destination);


            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            sendMessage(session, producer);

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (null != connection)

                    connection.close();

            } catch (Throwable ignore) {

            }

        }

    }



    public static void sendMessage(Session session,MessageProducer producer)

            throws Exception {
        // 发100分钟
        for (int i = 1; i <= 6000; i++) {

            TextMessage message = session

                    .createTextMessage("ActiveMq发送的消息" + i);

            System.out.println("发送消息：" + "ActiveMq 发送的消息" + i);

            producer.send(message);

            Thread.sleep(1000L);

        }

    }
}
