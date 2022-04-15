package com.cracker.api.mc.mq.activemq;

import javax.jms.Message;

/**
 * activeMq消息回调接口
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface MessageHandler {
    /**
     * 消息回调接口
     * @param message 待处理的Message
     */
    public abstract void handle(Message message);
}
