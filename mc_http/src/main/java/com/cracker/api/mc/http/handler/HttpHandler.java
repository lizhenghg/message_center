package com.cracker.api.mc.http.handler;

import com.cracker.api.mc.common.media.HttpHeaderName;
import com.cracker.api.mc.common.media.MimeType;
import com.cracker.api.mc.common.util.Symbol;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.http.HttpServerBootstrap;
import com.cracker.api.mc.http.context.HttpContext;
import com.cracker.api.mc.http.context.HttpThreadLocalContext;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.handler.codec.http.HttpHeaderNames;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.Map;



/**
 * 与Netty应用层处理Handler: HttpHandler
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class HttpHandler extends SimpleChannelInboundHandler<HttpRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpRequest request) {

        StopWatch watch = new StopWatch();
        watch.start();

        // 请求方法
        HttpMethod httpMethod = request.method();
        // 请求uri
        String uri = request.uri();
        // 自定义的请求处理器
        IHandler handler;
        // 自定义的HTTP上下文
        HttpContext httpContext;

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("http-service, messageReceived incoming: method = {}, uri = {}", httpMethod.toString(), uri);
        }

        // 删除无用请求
        if (!request.decoderResult().isSuccess()) {
            watch.stop();
            LOGGER.error("http-service, response bad request: method = {}, uri = {}, runTime = {} ms", httpMethod.toString(), uri, watch.getTime(TimeUnit.MILLISECONDS));
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        // 剔除不符合请求方式的请求,由于为cs模式，故剔除OPTIONS请求
        if (httpMethod != HttpMethod.GET
                && httpMethod != HttpMethod.POST
                && httpMethod != HttpMethod.PUT
                && httpMethod != HttpMethod.DELETE
                && httpMethod != HttpMethod.PATCH) {
            watch.stop();
            LOGGER.error("http-service, request method illegal: method = {}, uri = {}, runTime = {} ms", httpMethod.toString(), uri, watch.getTime());
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        // 剔除无效uri
        if (uri.lastIndexOf(Symbol.SEPARATORS) == -1) {
            watch.stop();
            LOGGER.error("http-service, request uri illegal: uri = {}, method = {}, runTime = {} ms", httpMethod.toString(), uri, watch.getTime());
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        // 获取指定的请求处理器
        String newUri = uri;
        if (newUri.contains(Symbol.QUESTION)) {
            newUri = newUri.replaceAll(Symbol.QUESTION_AFTER_DELETE, Symbol.EMPTY);
        }
        if (newUri.contains(Symbol.SEPARATORS)
                && newUri.lastIndexOf(Symbol.SEPARATORS) == newUri.length() - 1) {
            newUri = newUri.substring(0, newUri.length() - 1);
        }

        handler = HttpServerBootstrap.container.getHttpWorkerHandler(httpMethod.toString(), newUri);
        if (handler == null) {
            watch.stop();
            LOGGER.error("http-service response handler not found: method = {}, uri = {}, runTime = {} ms", httpMethod, uri, watch.getTime());
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }

        // Cookie解码
        Set<Cookie> cookies;
        String cookieValue = (String) request.headers().get(HttpHeaderNames.COOKIE);
        if (Assert.isEmpty(cookieValue)) {
            cookies = Collections.emptySet();
        } else {
            cookies = ServerCookieDecoder.decode(cookieValue);
        }

        QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);
        Map<String, List<String>> uriAttributes = decoderQuery.parameters();

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        httpContext = new HttpContext(ctx, request, response, cookies, uriAttributes);
        HttpThreadLocalContext.setContext(httpContext);

        // 执行IHandler方法
        handler.handle(httpContext);

        watch.stop();
        LOGGER.info("http-service end request：method = {}, uri = {}, runTime = {} ms", httpMethod, uri, watch.getTime());
    }

    /**
     * 抽象错误响应方法
     * @param ctx 频道处理器上下文
     * @param status 响应体状态类
     */
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {

        // 使用http1.1协议，UTF-8编码方式
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));

        // 能够规范的就绝不要使用局部字符串变量
        MimeType mimeType = HttpHeaderName.MediaType.parseMimeType(HttpHeaderName.TEXT_PLAIN_VALUE);
        HttpHeaderName.MediaType mediaType = new HttpHeaderName.MediaType(mimeType.getType(), mimeType.getSubType(), StandardCharsets.UTF_8);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mediaType.toString());
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        // 如下的长连接没有存在的必要，出现异常，应该及时关闭Channel.所以屏蔽
        /*boolean keepAlive = HttpHeaderUtil.isKeepAlive(request);
        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }*/
    }


    /**
     * 当捕获到指定异常时对管道监听器发送关闭信号量
     * @param ctx 频道处理器上下文
     * @param cause 通常指ReadTimeoutException
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("http-service exceptionCaught: error={}", cause.getMessage(), cause);
        if (cause instanceof ReadTimeoutException) {
            ctx.write(ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        // 统一通过channel关闭，而不是由上下文关闭
        ctx.channel().close();
    }
}