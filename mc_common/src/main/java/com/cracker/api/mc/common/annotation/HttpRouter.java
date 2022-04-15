package com.cracker.api.mc.common.annotation;

import java.lang.annotation.*;

/**
 * HTTP处理类路由信息，将指定URI的请求路由到符合匹配条件的处理类中
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-9-29
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpRouter {


    /**
     * 路由名称，可以作为JS代理插件KEY使用
     * @return 路由名称
     */
    String name() default "";

    /**
     * 路由信息
     * @return 路由信息
     */
    String route() default "";

    /**
     * 是否生成JS代理类，默认为false。设置为true则通过基于接口代理的jdk动态代理生成各个http接口的代理类，可通过JS接口获取
     * @return To or not to generate proxy
     */
    boolean proxy() default false;

}
