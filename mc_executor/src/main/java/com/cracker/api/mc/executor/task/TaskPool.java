package com.cracker.api.mc.executor.task;

import com.cracker.api.mc.executor.config.ExecutorConfig;
import com.cracker.api.mc.executor.vo.TaskVO;
import com.cracker.api.mc.mq.FixedAndBlockedThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * 任务中心资源池
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class TaskPool {

    private static final Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");

    private final ExecutorService taskThreadPool;

    /**
     * 确保只能同一包中的类可以初始化
     */
    TaskPool() {
        this.taskThreadPool = new FixedAndBlockedThreadPoolExecutor(ExecutorConfig.getInstance().getTaskThreadCount());
    }


    /**
     * @param taskVO 任务vo
     * @return 执行成功与否
     */
    public boolean addTask(TaskVO taskVO) {
        try {
            this.taskThreadPool.execute(new TaskProcessor(taskVO));
            return true;
        } catch (Exception ex) {
            FAILED_LOGGER.error("addTask, fail to addTask, pls must check the vo: {}, the error is: {}", taskVO, ex.getMessage(), ex);
            return false;
        }
    }
}
