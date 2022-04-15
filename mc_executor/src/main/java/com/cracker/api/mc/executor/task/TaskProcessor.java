package com.cracker.api.mc.executor.task;

import com.cracker.api.mc.common.http.PushEntity;
import com.cracker.api.mc.common.http.RequestManager;
import com.cracker.api.mc.executor.RetryClient;
import com.cracker.api.mc.executor.vo.TaskVO;
import com.cracker.api.mc.retry.RetryAble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 多线程任务处理器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-11
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class TaskProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProcessor.class);
    private static final Logger SUCCESSFUL_LOGGER = LoggerFactory.getLogger("executorSuccessfulRecordLogger");
    private final static Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");

    private final TaskVO taskVO;

    public TaskProcessor(TaskVO taskVO) {
        this.taskVO = Objects.requireNonNull(taskVO);
    }

    @Override
    public void run() {
        LOGGER.debug("process task, task: {}", taskVO);
        tryPostingMessage(taskVO);
    }

    private void tryPostingMessage(TaskVO taskVO) {

        boolean ret = false;

        try {
            ret = postMessage(taskVO);
        } catch (Exception ex) {
            LOGGER.error(String.format("fail to postMessage, vo: %s", taskVO.toString()), ex);
        }
        // 假如投递消息失败，提交到重试任务队列中执行
        if (!ret) {
            taskVO.setRetryRunnerClass(new RetryAble() {
                @Override
                public boolean retryAble() {
                    boolean flag = postMessage(taskVO);
                    if (flag) {
                        SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, consumer:{}, message:{}",
                                taskVO.getProducerVO().getProducerName(),
                                taskVO.getTopic(),
                                taskVO.getConsumerVO().getConsumerName(),
                                taskVO.getMessage());
                    }
                    return flag;
                }
            });
            // 启动重试服务
            if (RetryClient.getInstance().submitTask(taskVO)) {
                LOGGER.error("fail to submitTask from vo: {}", taskVO);
                FAILED_LOGGER.error("the message is over, fail to submitTask from vo. Must check it: {}", taskVO);
            }
        } else {
            // 投递成功，记录成功日志
            SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, consumer:{}, message:{}",
                    taskVO.getProducerVO().getProducerName(),
                    taskVO.getTopic(),
                    taskVO.getConsumerVO().getConsumerName(),
                    taskVO.getMessage());
        }
    }

    /**
     * post方式传递消息
     * @param taskVO 任务vo
     * @return 传递成功true;失败false
     */
    private boolean postMessage(TaskVO taskVO) {
        PushEntity entity = new PushEntity(taskVO.getTopic(), taskVO.getProducerVO().getProducerName(),
                taskVO.getMessage(), System.currentTimeMillis());
        return RequestManager.postMessage(taskVO.getCallbackUrl(), entity);
    }
}
