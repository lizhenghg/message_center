package com.cracker.api.mc.http.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 基于netty的服务器管道初始化类: HttpChannelInitializer
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * Http请求任务处理线程组，用于处理http请求任务。
     */
    private final EventExecutorGroup requestGroup;
    private final int channelReadTimeout;
    private final int channelWriteTimeout;
    private final int channelDefaultAggregator;

    public HttpChannelInitializer(EventExecutorGroup requestGroup, int channelReadTimeout,
                                  int channelWriteTimeout, int channelDefaultAggregator) {
        this.requestGroup = requestGroup;
        this.channelReadTimeout = channelReadTimeout;
        this.channelWriteTimeout = channelWriteTimeout;
        this.channelDefaultAggregator = channelDefaultAggregator;
    }

    /**
     * 初始化管道(频道)
     * @param socketChannel 服务器管道
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ReadTimeoutHandler(this.channelReadTimeout));
        pipeline.addLast(new WriteTimeoutHandler(this.channelWriteTimeout));
        // 请求解码器
        pipeline.addLast("decoder", new HttpRequestDecoder());
        // 规定每次请求最大分配内存
        pipeline.addLast("aggregator", new HttpObjectAggregator(this.channelDefaultAggregator));
        // 响应编码器
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // 如果不需要自动对内容进行压缩，将下面这一行注释
        pipeline.addLast("deflater", new HttpContentCompressor());

        // 把自定义的ChannelHandler塞进去即可
        if (this.requestGroup == null) {
            pipeline.addLast("handler", new HttpHandler());
        } else {
            pipeline.addLast(this.requestGroup, "handler", new HttpHandler());
        }
    }
}
