package com.cracker.api.mc.executor.task;


import java.util.Objects;

/**
 * 任务中心资源池管理器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class TaskPoolManager {

    private static volatile TaskPoolManager instance;

    private TaskPool taskPool;

    public static TaskPoolManager getInstance() {
        if (instance == null) {
            synchronized (TaskPoolManager.class) {
                if (instance == null) {
                    instance = new TaskPoolManager();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }


    private TaskPoolManager() {
        this.taskPool = new TaskPool();
    }


    private void initTaskManager() {
        this.taskPool = new TaskPool();
    }


    public TaskPool getTaskPool() {
        return this.taskPool;
    }
}