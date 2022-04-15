package com.cracker.api.mc.retry;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 重试中心重试任务类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RetryTask implements Delayed {

    private RetryAble retryRunnerClass;

    private final AtomicInteger currentRetryCount = new AtomicInteger(0);
    private final AtomicLong currentRetryTimeMs = new AtomicLong(0L);

    private int maxCount;
    private String taskId;

    public RetryTask() {}


    @Override
    public long getDelay(TimeUnit unit) {
        long currentTimeMillis = System.currentTimeMillis();
        // 等价于currentRetryTimeMs.get() - currentTimeMillis
        return unit.convert(this.currentRetryTimeMs.get() - currentTimeMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        if (other == this) {
            return 0;
        }
        long delay = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        return Long.compare(delay, 0L);
    }


    public void computeRetryTask(long delayTime) {
        if (this.hasRetry()) {
            long now = System.currentTimeMillis();
            this.currentRetryTimeMs.set(now + delayTime);
        }
    }

    /**
     * 判断是否放弃治疗
     * @return true不放弃;false放弃
     */
    public boolean hasRetry() {
        return this.currentRetryCount.get() <= this.maxCount;
    }


    public void incRetryingCount() {
        this.currentRetryCount.incrementAndGet();
    }


    public RetryAble getRetryRunnerClass() {
        return this.retryRunnerClass;
    }

    public void setRetryRunnerClass(RetryAble retryRunnerClass) {
        this.retryRunnerClass = retryRunnerClass;
    }

    public int getCurrentRetryCount() {
        return this.currentRetryCount.get();
    }

    public long getCurrentRetryTimeMs() {
        return this.currentRetryTimeMs.get();
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "RetryTask [taskId=" + this.taskId + ",currentRetryCount=" + this.getCurrentRetryCount() + ",currentRetryTimeMs=" + this.getCurrentRetryTimeMs() + "]";
    }
}