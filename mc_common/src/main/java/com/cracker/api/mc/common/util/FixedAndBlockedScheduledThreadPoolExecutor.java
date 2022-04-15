package com.cracker.api.mc.common.util;


import java.util.concurrent.*;

/**
 * 支持阻塞的、固定大小、可延迟或者定时执行的线程池
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-07
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class FixedAndBlockedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    private final ExecutorService taskExecutor;

    /**
     * 每initialDelay(一般为1单位秒)允许最大maximumPoolSize个(这里默认40个)线程同时并发执行
     * @param corePoolSize 核心线程数
     */
    public FixedAndBlockedScheduledThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        super(corePoolSize);
        // 饱和策略为: 直接抛异常
        this.taskExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return super.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        taskExecutor.execute(command);
                    }
                }, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        return super.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                taskExecutor.execute(command);
            }
        }, initialDelay, delay, unit);
    }

    /**
     * 测试，比较FixedAndBlockedScheduledThreadPoolExecutor与ScheduledExecutorService的区别
     * 区别：FixedAndBlockedScheduledThreadPoolExecutor：每initialDelay允许最大的线程数一起并发执行
     *      ScheduledExecutorService：每initialDelay只允许单个线程执行
     * @param args args
     */
    public static void main(String[] args) {

        FixedAndBlockedScheduledThreadPoolExecutor fixedAndBlockedScheduledThreadPoolExecutor = new FixedAndBlockedScheduledThreadPoolExecutor(5, 50);
        ScheduledExecutorService scheduledThreadPoolExecutor = Executors.newScheduledThreadPool(5);

        final long startMs = System.currentTimeMillis();

        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
               String threadName = Thread.currentThread().getName();
               System.out.println("scheduledThreadPoolExecutor " + threadName + " start at: " + (System.currentTimeMillis() - startMs));
               try {
                 Thread.sleep(3000L);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
            }
        }, 0L, 2000L, TimeUnit.MILLISECONDS);


        fixedAndBlockedScheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                String threadName = Thread.currentThread().getName();
                System.out.println("fixedAndBlockedScheduledThreadPoolExecutor " + threadName + " start at: " + (System.currentTimeMillis() - startMs));
                try {
                    Thread.sleep(10000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0L, 1000L, TimeUnit.MICROSECONDS);
    }
}
