package com.cracker.api.mc.scheduler.queue;

import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.util.FixedAndBlockedScheduledThreadPoolExecutor;
import com.cracker.api.mc.mq.config.MqConfig;
import com.cracker.api.mc.mq.queue.FQueue;
import com.cracker.api.mc.scheduler.bussizvo.MessageProducingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * 原始数据进出队列服务器，这里使用的容器为FQueue
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ProducingQueueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducingQueueService.class);

    /**
     * 类似这种全局可以唯一的资源，一般都要单例
     */
    private volatile static ProducingQueueService instance;
    private static final Object SYNC_OBJ = new Object();

    /**
     * 定时任务处理器
     */
    private final FixedAndBlockedScheduledThreadPoolExecutor fixedAndBlockedScheduledThreadPoolExecutor;

    /**
     * 生产者生产消息处理器，也就是处理进FQueue队列的原始数据，塞进activeMQ和kafka
     */
    private final ProducingProcessor producingProcessor;


    /**
     * 生产者队列，默认使用FQueue队列
     */
    private final Queue<MessageProducingVO> messageProducingQueue;

    /**
     * 定时任务处理器核心线程数
     */
    private final int producingQueueThreadCount = MqConfig.getInstance().getProducingQueueThreadCount();

    /**
     * 定时任务处理器支持的最大线程数
     */
    private final int producingQueueMaxPoolSize = MqConfig.getInstance().getProducingQueueMaximumPoolSize();


    /**
     * 定时任务处理器执行任务的线程间隔时长
     */
    private final long producingQueueThreadPeriod = MqConfig.getInstance().getProducingQueueThreadPeriod();

    private ProducingQueueService(String basePath) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init producing queue service start ... ");
        }
        // 实例化定时任务器
        this.fixedAndBlockedScheduledThreadPoolExecutor = new FixedAndBlockedScheduledThreadPoolExecutor(producingQueueThreadCount,
                producingQueueMaxPoolSize);
        String queueFilePath = String.format("%s/tmp/fqueue", basePath);

        try {
            this.messageProducingQueue = new FQueue<>(queueFilePath, MessageProducingVO.class);
        } catch (Exception ex) {
            LOGGER.error(String.format("fail to init fQueue, path: %s", queueFilePath), ex);
            throw new CommonException(String.format("fail to init fQueue, path: %s, errorMsg: %s", queueFilePath, ex.getMessage()), ex);
        }

        // 实例化生产任务处理器
        this.producingProcessor = new ProducingProcessor(this.messageProducingQueue);

        // 启动定时器
        this.fixedAndBlockedScheduledThreadPoolExecutor.scheduleAtFixedRate(this.producingProcessor, 1,
                this.producingQueueThreadPeriod, TimeUnit.MILLISECONDS);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init producing queue service end ... ");
        }
    }

    /**
     * 初始化ProducingQueueService
     */
    public static void init(String basePath) {
        if (instance == null) {
            synchronized (SYNC_OBJ) {
                if (instance == null) {
                    instance = new ProducingQueueService(basePath);
                }
            }
        }
    }

    public static ProducingQueueService getInstance() {
        return instance;
    }

    /**
     * 直接入队，发送消息直接使用异步方式。把接口获取到的message直接offer到fQueue队列即返回
     * 进不了fQueue，接口调用处直接返回false，不作任何处理
     * @param messageProducingVO 发送消息实体
     * @return 入队成功与否
     */
    public boolean offer(MessageProducingVO messageProducingVO) {
        return this.messageProducingQueue.offer(messageProducingVO);
    }
}