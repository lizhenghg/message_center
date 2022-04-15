package com.cracker.api.mc.executor.task;

import com.cracker.api.mc.executor.vo.TaskVO;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.executor.config.ExecutorConfig;
import com.cracker.api.mc.mq.IMessageHandler;
import com.cracker.api.mc.mq.MqMessage;
import com.cracker.api.mc.mq.vo.ConsumerVO;
import com.cracker.api.mc.mq.vo.MessageSendingVO;
import com.cracker.api.mc.mq.vo.ProducerVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * 任务中心处理器
 * 优化：这里应该要好好思考下，假如出现并发量过大，而任务池性能有限的情况，
 * 是否会导致源消息被破坏情况？最好的情况是：在mc_executor项目，添加fQueue队列，先把
 * 源消息入队，保存为本地文件。
 * 当然，通过设置有效的数字，有效提升任务池的并发访问量，出了问题通过重试中心去处理
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class TaskCenterService implements IMessageHandler {

    private final static Logger FAILED_LOGGER = LoggerFactory.getLogger("executorFailedRecordLogger");
    private final static Logger LOGGER = LoggerFactory.getLogger(TaskCenterService.class);


    private volatile static TaskCenterService taskCenterService;
    private static final Object SYNC_OBJ = new Object();

    private final TaskPool taskPool = TaskPoolManager.getInstance().getTaskPool();

    public static TaskCenterService getInstance() {
        if (taskCenterService == null) {
            synchronized (SYNC_OBJ) {
                if (taskCenterService == null) {
                    taskCenterService = new TaskCenterService();
                }
            }
        }
        return Objects.requireNonNull(taskCenterService);
    }

    /**
     * 初始化配置文件
     * @param configPath 配置文件路径
     */
    public static void init(String configPath) {
        LOGGER.info("init ExecutorConfig ... ");
        ExecutorConfig.init(configPath);
    }



    @Override
    public void handle(MqMessage message) {

        if (message == null) {
            LOGGER.error("fail to handle message, message is null");
            return;
        }

        MessageSendingVO messageSendingVO = (MessageSendingVO) message.getContent();

        addTasks(messageSendingVO);

    }

    /**
     * 投递消息方法，假如出现数据异常情况，不记录在投递失败日志。
     * 因为前面调用接口时，已经对数据可能出现的异常情况进行了判断，所以这里就不记录在册。
     * @param messageSendingVO 投递消息实体
     */
    private void addTasks(MessageSendingVO messageSendingVO) {

        if (messageSendingVO == null) {
            LOGGER.error("oh, fail to add message into taskPool, messageSendingVO must not be null");
            return;
        }

        String topic = messageSendingVO.getTopic();
        if (Assert.isEmpty(topic)) {
            LOGGER.error("messageSendingVO topic is null, vo: {}", messageSendingVO);
            return;
        }


        Map<String, ConsumerVO> consumerMap = messageSendingVO.getConsumerMap();

        if (!Assert.isNotNull(consumerMap)) {
            LOGGER.error("messageSendingVO consumers is null, topic: {}", messageSendingVO.getTopic());
            return;
        }

        TaskVO taskVO;


        ProducerVO producerVO = messageSendingVO.getProducer();
        String msg = messageSendingVO.getMessage();

        for (Map.Entry<String, ConsumerVO> entry : consumerMap.entrySet()) {

            taskVO = new TaskVO();

            ConsumerVO consumer = entry.getValue();

            taskVO.setTopic(topic);
            taskVO.setProducerVO(producerVO);
            taskVO.setMessage(msg);
            taskVO.setConsumerVO(consumer);
            taskVO.setCallbackUrl(consumer.getCallbackUrl());


            // 出现这种情况，都是Error异常，没法捕获那种，
            // 比如并发量超大，而线程池设置参数过低，一过来就挂了
            // 比如内存泄漏导致内存溢出
            if (!this.taskPool.addTask(taskVO)) {
                FAILED_LOGGER.error("the message is over, couldn't find any about it. Must check it: {}", taskVO);
            }
        }
    }
}