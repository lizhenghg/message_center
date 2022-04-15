package com.cracker.api.mc.common.annotation;

import java.lang.annotation.*;

/**
 * HTTP方法注解，在运行时解析各种HTTP Handle类的方法
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-9-29
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {

    /**
     * 请求方法枚举类
     */
    enum Method {
        GET, POST, OPTIONS, DELETE, PUT, HEAD, TRACE, CONNECT, PATCH
    }

    /**
     * http请求状态
     */
    enum Status {
        OK, CREATED, ACCEPTED, NO_CONTENT
    }

    /**
     * http返回数据格式
     */
    enum Return {
        JSON, XML, TEXT, TEMPLATE
    }

    /**
     * 每个处理方法可匹配的uri路径，使用全局匹配操作
     * @return uri
     */
    String uri() default "";

    /**
     * 方法排序优先级，数值越小优先级越高
     * @return 优先等级
     */
    int priority() default 10;

    /**
     * uri是否使用正则表达式，默认全局路径匹配
     * @return false
     */
    boolean isRegex() default false;

    /**
     * 允许使用的HTTP方法，目前暂时支持HTTP操作
     * @return http方法
     */
    Method method() default Method.GET;

    /**
     * 正常操作后返回的HTTP状态码
     * @return http状态码
     */
    Status status() default Status.OK;

    /**
     * 返回的数据格式
     * @return dataType
     */
    Return returnType() default Return.JSON;

    /**
     * 是否检测请求的会话信息
     * @return false
     */
    boolean isCheckSession() default false;

    /**
     * 模板页面路径，针对使用MVC设计模式
     * @return template path
     */
    String template() default "";
}
