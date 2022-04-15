package com.cracker.api.mc.executor.listener;

import com.cracker.api.mc.common.http.PushEntity;
import com.cracker.api.mc.common.http.RequestManager;
import com.cracker.api.mc.executor.RetryClient;
import com.cracker.api.mc.executor.task.TaskProcessor;
import com.cracker.api.mc.executor.vo.TaskVO;
import com.cracker.api.mc.retry.RetryAble;
import com.cracker.api.mc.retry.RetrySchedulerService;
import com.cracker.api.mc.retry.RetryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的RetryListener监听器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2021-01-18
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class DefaultRetryListener implements RetrySchedulerService.RetryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskProcessor.class);
    private static final Logger SUCCESSFUL_LOGGER = LoggerFactory.getLogger("executorSuccessfulRecordLogger");
    private final static Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");


    @Override
    public void onRetryArrived(RetryTask retryTask) {

    }

    @Override
    public void onRetryFailed(RetryTask retryTask) {

    }

    @Override
    public void onRetrySucceed(RetryTask retryTask) {

    }

    /**
     * 异常关闭导致缓存中可能存在重试次数不足的数据，在项目重启后
     * 再次重新进入重试队列
     * @param retryTask RetryTask
     */
    @Override
    public void onRetryAbnormalShutDown(RetryTask retryTask) {

        TaskVO newTaskVO = new TaskVO();
        TaskVO oldTaskVO = (TaskVO) retryTask;

        newTaskVO.setTopic(oldTaskVO.getTopic());
        newTaskVO.setProducerVO(oldTaskVO.getProducerVO());
        newTaskVO.setMessage(oldTaskVO.getMessage());
        newTaskVO.setConsumerVO(oldTaskVO.getConsumerVO());
        newTaskVO.setCallbackUrl(oldTaskVO.getCallbackUrl());


        newTaskVO.setRetryRunnerClass(new RetryAble() {
            @Override
            public boolean retryAble() {
                boolean flag = postMessage(newTaskVO);
                if (flag) {
                    SUCCESSFUL_LOGGER.info("producer: {}, topic: {}, consumer:{}, message:{}",
                            newTaskVO.getProducerVO().getProducerName(),
                            newTaskVO.getTopic(),
                            newTaskVO.getConsumerVO().getConsumerName(),
                            newTaskVO.getMessage());
                }
                return flag;
            }
        });
        // 启动重试服务
        if (RetryClient.getInstance().submitTask(newTaskVO)) {
            LOGGER.error("onRetryAbnormalShutDown, fail to submitTask from vo: {}", newTaskVO);
            FAILED_LOGGER.error("onRetryAbnormalShutDown, the message is over, fail to submitTask from vo. Must check it: {}", newTaskVO);
        }

    }

    /**
     * post方式传递消息
     * @param newTaskVO 任务vo
     * @return 传递成功true;失败false
     */
    private boolean postMessage(TaskVO newTaskVO) {
        PushEntity entity = new PushEntity(newTaskVO.getTopic(), newTaskVO.getProducerVO().getProducerName(),
                newTaskVO.getMessage(), System.currentTimeMillis());
        return RequestManager.postMessage(newTaskVO.getCallbackUrl(), entity);
    }
}
