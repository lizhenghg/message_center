package com.cracker.api.mc.scheduler.component.impl;

import com.cracker.api.mc.common.exception.BadRequestException;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.scheduler.api.constants.BusinessCode;
import com.cracker.api.mc.scheduler.bussizvo.MessageProducingVO;
import com.cracker.api.mc.scheduler.component.IProducerComponent;
import com.cracker.api.mc.scheduler.component.validator.ParameterValidator;
import com.cracker.api.mc.scheduler.queue.ProducingQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息中心生产者业务层业务处理类，类似于Spring的ProducerServiceImpl
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ProducerComponent implements IProducerComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerComponent.class);
    private final ProducingQueueService producingQueueService = ProducingQueueService.getInstance();

    /**
     * 生产者生产消息
     * @param topic 消息所属主题
     * @param message 消息content
     * @param producerName 生产者名称
     * @return true is succeed, false is failed
     * @throws BaseBusinessException 业务异常
     */
    @Override
    public boolean produceMessage(String topic, String message, String producerName)
            throws BaseBusinessException {

        LOGGER.info("ProducerComponent, produceMessage, message: {}, producerCode: {}, topic: {}", message, producerName, topic);
        // 消息有效性验证
        if (!ParameterValidator.validateMessage(message)) {
            LOGGER.debug("invalid message, message:{}, producerName:{}, topic:{}", message, producerName, topic);
            throw new BadRequestException(BusinessCode.PRODUCE_MESSAGE_INVALID_MESSAGE, BusinessCode.PRODUCE_MESSAGE_INVALID_MESSAGE_MSG);
        }

        // 生产者有效性验证
        if (!ParameterValidator.validateProducerName(producerName)) {
            LOGGER.debug("invalid producerName, message:{}, producerName:{}, topic:{}", message, producerName, topic);
            throw new BadRequestException(BusinessCode.PRODUCE_MESSAGE_INVALID_PRODUCER_NAME, BusinessCode.PRODUCE_MESSAGE_INVALID_PRODUCER_NAME_MSG);
        }

        // topic有效性验证
        if (!ParameterValidator.validateTopic(topic)) {
            LOGGER.debug("invalid topic, message:{}, producerName:{}, topic:{}", message, producerName, topic);
            throw new BadRequestException(BusinessCode.PRODUCE_MESSAGE_INVALID_TOPIC, BusinessCode.PRODUCE_MESSAGE_INVALID_TOPIC_MSG);
        }

        MessageProducingVO messageProducingVO = new MessageProducingVO();
        messageProducingVO.setMessage(message);
        messageProducingVO.setProducerName(producerName);
        messageProducingVO.setTopic(topic);
        boolean ret = false;

        try {
            ret = producingQueueService.offer(messageProducingVO);
            if (!ret) {
                // 入队失败
                LOGGER.error("fail to put vo in queue, vo: {}", messageProducingVO.toString());
            }
        } catch (Exception e) {
            //未知内部错误
            LOGGER.error(String.format("fail to put vo in queue, vo: %s, msg: %s", messageProducingVO.toString(), e.getMessage()), e);
        }

        if (!ret) {
            throw new InternalServerException(BusinessCode.INTERNAL_SERVER_EXCEPTION, BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG);
        }

        LOGGER.info("ProducerComponent offer Successful");
        return true;
    }
}