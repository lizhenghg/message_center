package com.cracker.api.mc.http.handler;

import com.cracker.api.mc.common.util.Symbol;
import com.cracker.api.mc.http.HttpServerBootstrap;
import com.cracker.api.mc.http.context.HttpContext;
import com.cracker.api.mc.http.result.AbstractResult;
import com.cracker.api.mc.http.result.RenderJson;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

/**
 * HTTP上层架构之: AbstractHttpHandler
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractHttpHandler implements IHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpHandler.class);

    /**
     * 实现handle方法
     * @param ctx 自定义Http上下文
     */
    @Override
    public void handle(HttpContext ctx) {

        HttpRequest request = ctx.getRequest();
        String method = request.method().toString();
        String uri = request.uri();

        uri = uri.replaceAll(Symbol.QUESTION_AFTER_DELETE, Symbol.EMPTY);
        if (uri.contains(Symbol.SEPARATORS) &&
                uri.lastIndexOf(Symbol.SEPARATORS) == uri.length() - 1) {
            uri = uri.substring(0, uri.length() - 1);
        }

        Method func = HttpServerBootstrap.container.getHttpWorkerHandlerMethod(method, uri,
                this.getClass().getName());

        if (func == null) {
            LOGGER.error("Not found request method. method:{}, uri: {}", method, uri);
            ctx.getResponse().setStatus(HttpResponseStatus.NOT_FOUND);
            ctx.writeResponse();
            return;
        }

        try {
            this.callFunction(func, ctx);
        } catch (Exception ex) {
            LOGGER.error("AbstractHttpHandler fail to handle: {}", ex.getMessage(), ex);
            ctx.getResponse().setStatus(INTERNAL_SERVER_ERROR);
            ctx.writeResponse();
        }
    }

    /**
     * 抽出来的方法，让子类自己去happy
     * @param func Method
     * @param ctx HttpContext
     * @throws InvocationTargetException 调用目标异常
     * @throws IllegalAccessException 非法通行异常
     */
    protected void callFunction(Method func, HttpContext ctx)
            throws InvocationTargetException, IllegalAccessException {
        func.invoke(this, ctx);
    }

    /**
     * 响应请求，返回结果
     * @param ctx Http上下文
     * @param src 待返回结果对象
     * @param httpCode 返回code
     */
    protected void renderJson(HttpContext ctx, Object src, int httpCode) {
        AbstractResult result = new RenderJson(src, httpCode);
        result.apply(ctx);
    }
}