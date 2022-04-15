package com.cracker.api.mc.scheduler.api.constants;

/**
 * 业务码
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class BusinessCode {

    public static final String SUCCESSFUL_REQUEST = "22100000";
    public static final String SUCCESSFUL_REQUEST_MSG = "ok";

    public static final String INTERNAL_SERVER_EXCEPTION = "52100000";
    public static final String INTERNAL_SERVER_EXCEPTION_MSG = "internal server exception";

    public static final String SUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME = "42100100";
    public static final String SUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME_MSG = "invalid consumer name";
    public static final String SUBSCRIBE_TOPIC_INVALID_CALLBACK_URL = "42100101";
    public static final String SUBSCRIBE_TOPIC_INVALID_CALLBACK_URL_MSG = "invalid callback url";
    public static final String SUBSCRIBE_TOPIC_INVALID_TOPIC = "42100102";
    public static final String SUBSCRIBE_TOPIC_INVALID_TOPIC_MSG = "invalid topic";

    public static final String UNSUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME = "42100200";
    public static final String UNSUBSCRIBE_TOPIC_INVALID_CONSUMER_NAME_MSG = "invalid consumer name";
    public static final String UNSUBSCRIBE_TOPIC_INVALID_TOPIC = "42100201";
    public static final String UNSUBSCRIBE_TOPIC_INVALID_TOPIC_MSG = "invalid topic";
    public static final String UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC = "42100202";
    public static final String UNSUBSCRIBE_TOPIC_NONEXISTENT_TOPIC_MSG = "nonexistent topic";
    public static final String UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER = "42100203";
    public static final String UNSUBSCRIBE_TOPIC_NONEXISTENT_CONSUMER_MSG = "nonexistent consumer";

    public static final String PRODUCE_MESSAGE_INVALID_PRODUCER_NAME = "42100300";
    public static final String PRODUCE_MESSAGE_INVALID_PRODUCER_NAME_MSG = "invalid producer name";
    public static final String PRODUCE_MESSAGE_INVALID_TOPIC = "42100301";
    public static final String PRODUCE_MESSAGE_INVALID_TOPIC_MSG = "invalid topic";
    public static final String PRODUCE_MESSAGE_INVALID_MESSAGE = "42100302";
    public static final String PRODUCE_MESSAGE_INVALID_MESSAGE_MSG = "invalid message content";
}