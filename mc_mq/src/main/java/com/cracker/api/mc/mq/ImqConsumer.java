package com.cracker.api.mc.mq;

import javax.jms.MessageListener;

/**
 * 消息中心顶级父类生产者接口
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface ImqConsumer {

    /**
     * 设置消息监听
     * @param messageListener 消息监听器
     */
    void setMessageListener(MessageListener messageListener);

    /**
     * 启动监听
     */
    void start();

    /**
     * 关闭监听
     */
    void shutdown();
}