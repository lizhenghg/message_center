package com.cracker.api.mc.executor;

import com.cracker.api.mc.executor.listener.DefaultRetryListener;
import com.cracker.api.mc.retry.RetryScheduler;
import com.cracker.api.mc.retry.RetrySchedulerFactory;
import com.cracker.api.mc.retry.RetryTask;
import com.cracker.api.mc.retry.config.RetryConfig;
import com.cracker.api.mc.retry.failstore.mapdb.MapDbFailStoreFactory;
import com.cracker.api.mc.retry.strategy.LadderRetryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 重试中心对外调度客户端
 * 1、使用阶梯重试策略
 * 2、使用mapDB策略持久化
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class RetryClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryClient.class);

    private volatile static RetryClient retryClient;

    private static final Object SYNC_OBJ = new Object();

    /**
     * 重试任务调度器
     */
    private final RetryScheduler retryScheduler;

    public static RetryClient getInstance() {
        return Objects.requireNonNull(retryClient);
    }

    /**
     * 确保只实例化一次
     */
    public static void init() {
        if (retryClient == null) {
            synchronized (SYNC_OBJ) {
                if (retryClient == null) {
                    retryClient = new RetryClient();
                }
            }
        }
    }

    private RetryClient() {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init RetryCenter ... ");
        }

        RetryConfig retryConfig = new RetryConfig();
        RetryScheduler retryService = RetrySchedulerFactory.builder()
                .withRetryConfig(retryConfig)
                .withRetryStrategy(new LadderRetryStrategy())
                .withFailStore(new MapDbFailStoreFactory().getFailStore(retryConfig, retryConfig.getFailStorePath()))
                .withRetryListener(new DefaultRetryListener())
                .name("retryCenter")
                .build();
        retryService.start();
        this.retryScheduler = retryService;
        /*
         * 注册一个新的虚拟机关机挂钩，等价于前端的当浏览器被动|主动关闭时，会自动执行某些指定的函数类似。当jvm异常时进行资源清理。详情请参考JDK
         * java虚拟机关闭可以归纳为两种事件
         * 1、在程序正常退出，当最后一个非守护线程退出时或者当(等价地，exit Syetem.exit)方法被调用
         * 2、虚拟机被终止在响应于用户中断，如键入^c，或一个全系统的事件，如用户注销或系统关闭
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                LOGGER.error("Exception quit, RetryScheduler stop");
                retryScheduler.stop();
            }
        }));
    }

    public void onRetryAbnormalShutDown() {
        this.retryScheduler.dealRetryAbnormalShutDown();
    }


    public boolean submitTask(RetryTask retryTask) {
        boolean success = false;
        if (this.retryScheduler.isStarted()) {
            success = this.retryScheduler.submitTask(retryTask);
        }
        return !success;
    }

}