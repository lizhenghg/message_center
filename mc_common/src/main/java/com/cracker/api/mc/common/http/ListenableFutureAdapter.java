package com.cracker.api.mc.common.http;

import com.google.common.util.concurrent.ExecutionList;
import com.google.common.util.concurrent.ForwardingFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 线程异步执行监听器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ListenableFutureAdapter<V> extends ForwardingFuture<V> implements ListenableFuture<V> {

    private static final ThreadFactory THREAD_FACTORY =
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("ListenableFutureAdapter-thread-%d")
                    .build();

    private static final Executor DEFAULT_ADAPTER_EXECUTOR = new ThreadPoolExecutor(15, 50, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    private final Executor adapterExecutor;

    private final ExecutionList executionList = new ExecutionList();

    private final AtomicBoolean hasListeners = new AtomicBoolean(false);

    private final Future<V> delegate;

    ListenableFutureAdapter(Future<V> delegate) {
        this(delegate, DEFAULT_ADAPTER_EXECUTOR);
    }

    ListenableFutureAdapter(Future<V> delegate, Executor adapterExecutor) {
        this.delegate = delegate;
        this.adapterExecutor = checkNotNull(adapterExecutor);
    }


    @Override
    protected Future<V> delegate() {
        return this.delegate;
    }


    @Override
    public void addListener(Runnable listener, Executor executor) {
        this.executionList.add(listener, executor);

        if (hasListeners.compareAndSet(false, true)) {
            if (this.delegate.isDone()) {
                this.executionList.execute();
                return;
            }

            this.adapterExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        delegate.get();
                    } catch (Error e) {
                        throw e;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new AssertionError(e);
                    } catch (Throwable e) {
                        e.getStackTrace();
                    }
                    executionList.execute();
                }
            });
        }
    }
}