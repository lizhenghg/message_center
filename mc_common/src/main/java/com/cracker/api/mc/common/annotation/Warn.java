package com.cracker.api.mc.common.annotation;


import java.lang.annotation.*;

/**
 * 警告注解，避免误用滥用。开发必看
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Warn {

    String singletonWarn() default "该类为单例模式，请不要也不能自行创建";

    String methodWarn() default "该方法全局只能运行一次，项目启动时已经运行，请不要也不能重复运行";

    String initFirstWarn() default "对象调用之前必须先进行一次方法初始化";

}
