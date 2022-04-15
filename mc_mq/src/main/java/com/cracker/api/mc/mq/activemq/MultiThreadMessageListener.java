package com.cracker.api.mc.mq.activemq;


import com.cracker.api.mc.mq.FixedAndBlockedThreadPoolExecutor;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.concurrent.ExecutorService;

/**
 * 消息消费者中使用的多线程消息监听服务
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MultiThreadMessageListener implements MessageListener {

    /**
     * 提供消息回调调用接口
     */
    private final MessageHandler messageHandler;

    /**
     * 默认线程池数量
     */
    private final static int DEFAULT_HANDLE_THREAD_POOL = 30;

    private final int maxHandleThreads;

    /**
     * 自定义线程池
     */
    private final ExecutorService handleThreadPool;

    public MultiThreadMessageListener(MessageHandler messageHandler) {
        this(messageHandler, DEFAULT_HANDLE_THREAD_POOL);
    }

    public MultiThreadMessageListener(MessageHandler messageHandler, int maxHandleThreads) {
        this.maxHandleThreads = maxHandleThreads;
        this.messageHandler = messageHandler;
        this.handleThreadPool = new FixedAndBlockedThreadPoolExecutor(this.maxHandleThreads);
    }

    /**
     * 比较经典的构思，B把A需要的对象全部处理好了再送进来A处，然后A再调B写好的方法
     * @param message 待消费消息
     */
    @Override
    public void onMessage(Message message) {
        this.handleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                MultiThreadMessageListener.this.messageHandler.handle(message);
            }
        });
    }
}