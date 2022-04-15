package com.cracker.api.mc.scheduler.api.handler;

import com.cracker.api.mc.common.annotation.HttpMethod;
import com.cracker.api.mc.common.annotation.HttpRouter;
import com.cracker.api.mc.common.codec.SystemCode;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.util.RestResultBuilder;
import com.cracker.api.mc.common.util.ResultModel;
import com.cracker.api.mc.http.handler.AbstractHttpWorkerHandler;
import com.cracker.api.mc.scheduler.api.constants.BusinessCode;
import com.cracker.api.mc.scheduler.api.response.SimpleResponse;
import com.cracker.api.mc.scheduler.api.response.UnSubscribeResponse;
import com.cracker.api.mc.scheduler.bussizvo.SubscribeTableVO;
import com.cracker.api.mc.scheduler.component.IConsumerComponent;
import com.cracker.api.mc.scheduler.component.impl.ConsumerComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 消息中心消费者接口操作类，类似于Spring的ConsumerController。使用了基于继承的方式对对象进行生命周期管理
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
@HttpRouter(route = "/uam/mc/v1/consume")
public class ConsumerHandler extends AbstractHttpWorkerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHandler.class);

    /**
     * 业务处理类
     */
    private final IConsumerComponent consumerComponent = new ConsumerComponent();


    /**
     * 订阅主题
     * @param topic 主题名称
     * @param consumerName 消费者名称
     * @param callbackUrl 消费者通知url
     * @return ResultModel，封装数据返回类
     */
    @HttpMethod(uri = "", method = HttpMethod.Method.POST, status = HttpMethod.Status.OK)
    public ResultModel subscribeTopic(String topic, String consumerName, String callbackUrl) {

        boolean result;

        try {
            result = this.consumerComponent.subscribeTopic(topic, consumerName, callbackUrl);
        } catch (Exception ex) {
            if (ex instanceof BaseBusinessException) {
                return RestResultBuilder.builder().code(Integer.parseInt(((BaseBusinessException)ex).getErrCode()))
                        .msg(ex.getMessage()).build();
            }
            return RestResultBuilder.builder().code(Integer.parseInt(BusinessCode.INTERNAL_SERVER_EXCEPTION))
                    .msg(BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG).build();
        }

        return RestResultBuilder.builder()
                .code(Integer.parseInt(BusinessCode.SUCCESSFUL_REQUEST))
                .msg(BusinessCode.SUCCESSFUL_REQUEST_MSG).data(buildSimpleResponse(result)).build();
    }

    /**
     * 撤销指定主题下的指定消费者
     * @param topic 主题名称
     * @param consumerName 消费者名称
     * @return ResultModel，封装数据返回类
     */
    @HttpMethod(uri = "/deleteByConsName", method = HttpMethod.Method.DELETE, status = HttpMethod.Status.OK)
    public ResultModel unSubscribeWithConsName(String topic, String consumerName) {

        SubscribeTableVO subscribeTableVO;

        try {
            subscribeTableVO = this.consumerComponent.unSubscribeWithConsName(topic, consumerName);
            if (subscribeTableVO == null) {
                LOGGER.error("deleteByConsName, subscribeTableVO is null, topic: {}, consumerName: {}", topic, consumerName);
                throw new InternalServerException(SystemCode.VALUE_INVALID, "subscribeTableVO is null");
            }
        } catch (Exception ex) {
            if (ex instanceof BaseBusinessException) {
                return RestResultBuilder.builder().code(Integer.parseInt(((BaseBusinessException)ex).getErrCode()))
                        .msg(ex.getMessage()).build();
            }
            return RestResultBuilder.builder().code(Integer.parseInt(BusinessCode.INTERNAL_SERVER_EXCEPTION))
                    .msg(BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG).build();
        }

        return RestResultBuilder.builder()
                .code(Integer.parseInt(BusinessCode.SUCCESSFUL_REQUEST))
                .msg(BusinessCode.SUCCESSFUL_REQUEST_MSG).data(buildUnSubscribeResponse(subscribeTableVO)).build();
    }

    /**
     * 撤销指定主题(该主题及其包含的所有消费者全部被删除)
     * @param topic 主题名称
     * @return ResultModel，封装数据返回类
     */
    @HttpMethod(uri = "", method = HttpMethod.Method.DELETE, status = HttpMethod.Status.OK)
    public ResultModel unSubscribeTopic(String topic) {

        boolean ret;

        try {
            ret = this.consumerComponent.unSubscribeTopic(topic);
        } catch (Exception ex) {
            if (ex instanceof BaseBusinessException) {
                return RestResultBuilder.builder().code(Integer.parseInt(((BaseBusinessException)ex).getErrCode()))
                        .msg(ex.getMessage()).build();
            }
            return RestResultBuilder.builder().code(Integer.parseInt(BusinessCode.INTERNAL_SERVER_EXCEPTION))
                    .msg(BusinessCode.INTERNAL_SERVER_EXCEPTION_MSG).build();
        }

        return RestResultBuilder.builder()
                .code(Integer.parseInt(BusinessCode.SUCCESSFUL_REQUEST))
                .msg(BusinessCode.SUCCESSFUL_REQUEST_MSG).data(buildSimpleResponse(ret)).build();
    }


    /**
     * 封装SimpleResponse
     * @param result 执行的结果
     * @return SimpleResponse
     */
    public SimpleResponse buildSimpleResponse(boolean result) {
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setResult(result);
        return simpleResponse;
    }

    /**
     * 封装UnSubscribeResponse
     * @param subscribeTableVO 执行的结果
     * @return UnSubscribeResponse
     */
    public UnSubscribeResponse buildUnSubscribeResponse(SubscribeTableVO subscribeTableVO) {
        UnSubscribeResponse unSubscribeResponse = new UnSubscribeResponse();
        unSubscribeResponse.setTopic(subscribeTableVO.getTopic());
        unSubscribeResponse.setProducer(subscribeTableVO.getProducer());
        unSubscribeResponse.setConsumers(subscribeTableVO.getConsumers());
        return unSubscribeResponse;
    }
}