package com.cracker.api.mc.http.context;

import com.cracker.api.mc.common.validate.Assert;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map;


/**
 * HTTP上下文
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-9-29
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpContext {

    /**
     * Netty处理器上下文
     */
    private ChannelHandlerContext ctx;

    /**
     * Netty Http请求对象
     */
    private HttpRequest request;

    /**
     * Netty Http响应对象
     */
    private FullHttpResponse response;

    /**
     * Cookie
     */
    private Set<Cookie> cookies;

    /**
     * uri请求参数
     */
    private Map<String, List<String>> uriAttributes;



    public HttpRequest getRequest() {
        return this.request;
    }

    public FullHttpResponse getResponse() {
        return this.response;
    }

    public ChannelHandlerContext getContext() {
        return this.ctx;
    }


    /**
     * 构造器
     * @param ctx Netty处理器上下文
     * @param request Http请求对象
     * @param response Http响应对象
     * @param cookies Cookie
     * @param uriAttributes uri请求参数
     */
    public HttpContext(ChannelHandlerContext ctx, HttpRequest request, FullHttpResponse response,
                       Set<Cookie> cookies, Map<String, List<String>> uriAttributes) {
        this.ctx = ctx;
        this.request = request;
        this.response = response;
        this.cookies = cookies;
        this.uriAttributes = uriAttributes;
    }

    /**
     * 根据key获取到指定的uri参数集合
     * @param key uri key
     * @return uri参数集合
     */
    public List<String> getUriAttribute(String key) {
        if (Assert.isNotNull(this.uriAttributes)) {
            for (Map.Entry<String, List<String>> entry : this.uriAttributes.entrySet()) {
                if (entry.getKey().toLowerCase().equals(key.toLowerCase())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 组装Cookie
     */
    private void buildCookie() {
        if (Assert.isNotNull(this.cookies)) {
            Iterator<Cookie> iterator = this.cookies.iterator();
            while (iterator.hasNext()) {
                Cookie cookie = iterator.next();
                this.response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.encode(cookie));
            }
        }
    }


    /**
     * 统一响应response
     */
    public void writeResponse() {

        boolean close = (HttpHeaderValues.CLOSE.toString().contentEquals(request.headers().get(HttpHeaderNames.CONNECTION.toString())) || request.protocolVersion() == HttpVersion.HTTP_1_0)
                && !(HttpHeaderValues.KEEP_ALIVE.toString().contentEquals(request.headers().get(HttpHeaderNames.CONNECTION.toString())));

        this.response.headers().set(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
        buildCookie();
        ChannelFuture future = this.ctx.channel().writeAndFlush(response);

        HttpThreadLocalContext.remove();

        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
