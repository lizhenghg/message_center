package com.cracker.api.mc.retry;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

/**
 * 重试任务队列
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RetryDelayQueue<T extends Delayed> {

    private final DelayQueue<T> queue = new DelayQueue<>();

    RetryDelayQueue() {}

    public int size() {
        return this.queue.size();
    }

    public boolean offer(T e) {
        return this.queue.add(e);
    }

    public T poll() {
        return this.queue.poll();
    }

    public T take() {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public T peek() {
        throw new UnsupportedOperationException("peek Unsupported now");
    }
}
