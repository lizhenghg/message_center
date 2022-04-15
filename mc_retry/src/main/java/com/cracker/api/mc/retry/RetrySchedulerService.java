package com.cracker.api.mc.retry;

import com.cracker.api.mc.retry.config.RetryConfig;
import com.cracker.api.mc.retry.failstore.FailStore;
import com.cracker.api.mc.retry.failstore.FailStoreException;
import com.cracker.api.mc.retry.strategy.RetryStrategy;
import com.cracker.api.mc.retry.utils.Pair;
import com.cracker.api.mc.retry.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重试调度实现类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RetrySchedulerService implements RetryScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrySchedulerService.class);
    private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("retryFailedRecordAppender");
    private static final Logger SUCCESSFUL_LOGGER = LoggerFactory.getLogger("retrySuccessfulRecordAppender");

    private final RetryConfig retryConfig;
    private final RetryStrategy retryStrategy;
    private final RetryDelayQueue<RetryTask> retryTaskQueue;
    private final FailStore failStore;
    private final AtomicReference<RetrySchedulerState> state;
    private final ReentrantLock lock = new ReentrantLock();
    private final String retrySchedulerName;
    private ScheduledFuture<?> retryScheduledFuture;
    private final ScheduledExecutorService retryScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService threadPool;
    private RetryListener retryListener;



    public RetrySchedulerService(RetrySchedulerFactory.Builder builder) {
        this.retryConfig = builder.getRetryConfig();
        this.retryStrategy = builder.getRetryStrategy();
        this.retryListener = builder.getRetryListener();
        this.retrySchedulerName = builder.getName();
        this.failStore = builder.getFailStore();

        try {
            this.failStore.open();
            this.retryTaskQueue = new RetryDelayQueue<>();
            this.threadPool = new ThreadPoolExecutor(this.retryConfig.getThreadPoolSize(),
                    this.retryConfig.getMaxThreadPoolSize(), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        } catch (Exception ex) {
            throw new RuntimeException("RetrySchedulerService init Failed", ex);
        }

        this.state = new AtomicReference<>(RetrySchedulerState.LOAD);
    }

    @Override
    public void start() {
        // 灵活使用原子操作类来执行基于多线程场景下的状态判断
        if (!this.state.compareAndSet(RetrySchedulerState.LOAD, RetrySchedulerState.STARTED)) {
            LOGGER.error("couldn't started more than once");
            throw new IllegalStateException("couldn't started RetryScheduler more than once");
        }
        this.retryScheduledFuture = this.retryScheduledExecutorService.scheduleAtFixedRate(new RetryTaskProcessor(), 0L,
                this.retryConfig.getRetryTimePeriod(), TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isStarted() {
        return this.state.get() == RetrySchedulerState.STARTED;
    }

    @Override
    public void addListener(RetryListener listener) {
        this.retryListener = listener;
    }

    /**
     * 提交任务到延迟队列
     * @param retryTask 重试任务
     * @return 提交成功与否
     */
    @Override
    public boolean submitTask(RetryTask retryTask) {
        boolean success = false;

        try {
            this.lock.lock();
            retryTask.setMaxCount(this.retryStrategy.maxCount());
            retryTask.incRetryingCount();
            retryTask.setTaskId(StringUtils.generateUUID());
            this.failStore.put(retryTask.getTaskId(), retryTask);
            // 进入延迟队列
            success = this.offerRetryQueue(retryTask);
        } catch (Exception ex) {
            LOGGER.error("retry submitTask Failed, RetryTask: {}, error: {}", retryTask, ex.getMessage(), ex);
        } finally {
            this.lock.unlock();
        }
        return success;
    }

    /**
     * 把重试任务塞进延迟队列
     * @param retryTask 重试任务
     * @return 成功入队与否
     */
    private boolean offerRetryQueue(final RetryTask retryTask) {
        this.threadPool.execute(new Runnable() {
            @Override
            public void run() {
                long nextDelayTime = RetrySchedulerService.this.retryStrategy.calculateRetryTime(retryTask.getCurrentRetryCount());
                retryTask.computeRetryTask(nextDelayTime);
                RetrySchedulerService.this.retryTaskQueue.offer(retryTask);
                RetrySchedulerService.LOGGER.info("offer retry queue success, retryTask = {}", retryTask);
            }
        });
        return true;
    }

    @Override
    public void stop() {
        try {
            if (this.state.compareAndSet(RetrySchedulerState.STARTED, RetrySchedulerState.STOPPED)) {
                // 关闭定时任务
                if (this.retryScheduledFuture != null) {
                    this.retryScheduledFuture.cancel(true);
                }
                // 关闭mapDb
                this.failStore.close();
                // 优雅关闭线程池
                this.retryScheduledExecutorService.shutdown();

                LOGGER.info("Stop {} RetrySchedulerService success", this.retrySchedulerName);
            }
        } catch (Exception exception) {
            LOGGER.error("Stop {} RetrySchedulerService failed", this.retrySchedulerName);
        }
    }

    @Override
    public void destroy() {
        try {
            this.stop();
            // 再把mapDb所在的文件夹的全部文件删除
            this.failStore.destroy();
        } catch (Exception exception) {
            LOGGER.error("destroy {} RetrySchedulerService failed", this.retrySchedulerName);
        }
    }



    private boolean retry(RetryTask retryTask) {
        LOGGER.info("The Retry Task Start, TaskID = {}, currentRetryCount = {}, currentRetryTimeMS = {}",
                retryTask.getTaskId(), retryTask.getCurrentRetryCount(), retryTask.getCurrentRetryTimeMs());
        boolean success = false;

        try {
            RetryAble retryAble = retryTask.getRetryRunnerClass();
            success = retryAble.retryAble();
        } catch (Exception ex) {
            LOGGER.error("The Retry Task business Run Failed", ex);
            // 错误日志记录一次重试失败的记录
            FAILED_LOGGER.info("The Scheduler Retry task is failed, retryCount: {}, vo: {}, errorMsg: {}",
                    retryTask.getCurrentRetryCount(), retryTask, ex.getMessage(), ex);
        }

        if (!success) {

            LOGGER.info("The retry task did not succeed, Try again");
            FAILED_LOGGER.info("The Scheduler Retry task is failed, retryCount: {}, vo: {}", retryTask.getCurrentRetryCount(), retryTask);
            retryTask.incRetryingCount();
            if (retryTask.hasRetry()) {
                // 再次入队
                this.offerRetryQueue(retryTask);
                if (this.retryListener != null) {
                    this.retryListener.onRetryArrived(retryTask);
                }
            } else {
                LOGGER.error("The {} retry task is not the number of retries that will be discarded", retryTask.getTaskId());
                // 重试指定次数都没法挽救的消息
                FAILED_LOGGER.error("The retry task last retry failed, retryTask: {}", retryTask);
                if (this.retryListener != null) {
                    this.retryListener.onRetryFailed(retryTask);
                }
                try {
                    this.failStore.delete(retryTask.getTaskId());
                } catch (FailStoreException ex) {
                    LOGGER.warn("the unValued task couldn't remove from mapDb, the key is: {}", retryTask.getTaskId(), ex);
                }
            }
        } else {
            if (this.retryListener != null) {
                this.retryListener.onRetrySucceed(retryTask);
            }
            success = true;
            SUCCESSFUL_LOGGER.info("congratulation, the retry task: {}, has successfully consumed", retryTask);
        }
        return success;
    }


    /**
     * 偶尔用下内部类也是挺ok的
     * 重试任务执行器
     */
    private class RetryTaskProcessor implements Runnable {

        private RetryTaskProcessor() {}

        @Override
        public void run() {
            try {
                while (true) {
                    RetryTask task = RetrySchedulerService.this.retryTaskQueue.take();
                    if (task == null) {
                        return;
                    }
                    if (RetrySchedulerService.this.retry(task)) {
                        RetrySchedulerService.this.failStore.delete(task.getTaskId());
                        RetrySchedulerService.LOGGER.info("The {} retry task retry succeed, Delete FailStore success", task.getTaskId());
                    }
                }
            } catch (Throwable throwable) {
                RetrySchedulerService.LOGGER.error("Run {} RetryScheduler error", RetrySchedulerService.this.retrySchedulerName, throwable);
            }
        }
    }


    /**
     * 异常关闭导致缓存中可能存在重试次数不足的数据，在项目重启后
     * 再次重新进入重试队列
     */
    @Override
    public void dealRetryAbnormalShutDown() {

        if (this.retryListener == null) {
            return;
        }

        try {

            if (this.failStore.size() == 0) {
                return;
            }

            List<Pair<String, RetryTask>> retryTaskPairs = this.failStore.queryAllRetryTask();

            for (Pair<String, RetryTask> pair : retryTaskPairs) {
                this.retryListener.onRetryAbnormalShutDown(pair.getValue());
                this.failStore.delete(pair.getKey());
            }
        } catch (Exception exception) {
            LOGGER.error("deal retryTask for abnormal shutdown fail: {}", exception.getMessage(), exception);
        }
    }


    /**
     * 重试消息监听器组件，这里可大有扩展的自由
     * 这里留给其他大神想扩展的就自由扩展
     * just do it
     */
    public interface RetryListener {

        /**
         * 消息到达后执行的方法
         * @param retryTask RetryTask
         */
        void onRetryArrived(RetryTask retryTask);

        /**
         * 消息重试失败后执行的方法
         * @param retryTask RetryTask
         */
        void onRetryFailed(RetryTask retryTask);

        /**
         * 消息重试成功后执行的方法
         * @param retryTask RetryTask
         */
        void onRetrySucceed(RetryTask retryTask);

        /**
         * 异常关闭导致缓存中可能存在重试次数不足的数据，在项目重启后
         * 再次重新进入重试队列
         * @param retryTask RetryTask
         */
        void onRetryAbnormalShutDown(RetryTask retryTask);
    }
}
