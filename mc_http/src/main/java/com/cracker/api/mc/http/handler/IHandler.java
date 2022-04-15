package com.cracker.api.mc.http.handler;

import com.cracker.api.mc.http.context.HttpContext;

/**
 * HTTP处理器顶级接口，提供子类实现的方法，可扩展
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-9-29
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface IHandler {

    /**
     * HTTP处理器执行方法
     * @param ctx 自定义Http上下文
     */
    void handle(HttpContext ctx);

}
