package com.cracker.api.mc.scheduler.component.validator;


import com.cracker.api.mc.common.util.StringUtil;

/**
 * ParameterValidator: ParameterValidator.java.
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ParameterValidator {

    public static boolean validateProducerName(String producerName) {
        return !StringUtil.isNullOrBlank(producerName);
    }

    public static boolean validateTopic(String topic) {
        return !StringUtil.isNullOrBlank(topic);
    }

    public static boolean validateMessage(String message) {
        return !StringUtil.isNullOrBlank(message);
    }

    public static boolean validateConsumerName(String consumerName) {
        return !StringUtil.isNullOrBlank(consumerName);
    }

    public static boolean validateCallbackUrl(String callbackUrl) {
        return !StringUtil.isNullOrBlank(callbackUrl);
    }
}