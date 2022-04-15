package com.cracker.api.mc.scheduler.api.handler;

import com.cracker.api.mc.common.annotation.HttpMethod;
import com.cracker.api.mc.common.annotation.HttpRouter;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.util.RestResultBuilder;
import com.cracker.api.mc.common.util.ResultModel;
import com.cracker.api.mc.http.handler.AbstractHttpWorkerHandler;
import com.cracker.api.mc.scheduler.api.constants.BusinessCode;
import com.cracker.api.mc.scheduler.api.response.SimpleResponse;
import com.cracker.api.mc.scheduler.component.IProducerComponent;
import com.cracker.api.mc.scheduler.component.impl.ProducerComponent;


/**
 * 消息中心生产者接口操作类，类似于Spring的ProducerController。使用了基于继承的方式对对象进行生命周期管理
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
@HttpRouter(route = "/uam/mc/v1/produce")
public class ProducerHandler extends AbstractHttpWorkerHandler {

    /**
     * 业务处理类
     */
    private final IProducerComponent producerComponent = new ProducerComponent();

    /**
     * 生产者生产消息接口
     * @param topic 消息所属主题
     * @param message 消息content
     * @param producerName 生产者名称
     * @return ResultModel，封装数据返回类
     */
    @HttpMethod(uri = "/message", method = HttpMethod.Method.POST, status = HttpMethod.Status.OK)
    public ResultModel produceMessage(String topic, String message, String producerName) {

        boolean result;

        try {
            result = this.producerComponent.produceMessage(topic, message, producerName);
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
     * 封装SimpleResponse
     * @param result 执行的结果
     * @return SimpleResponse
     */
    public SimpleResponse buildSimpleResponse(boolean result) {
        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setResult(result);
        return simpleResponse;
    }
}