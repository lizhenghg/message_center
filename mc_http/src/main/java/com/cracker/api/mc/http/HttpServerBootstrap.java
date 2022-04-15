package com.cracker.api.mc.http;


import com.google.common.collect.Lists;
import com.cracker.api.mc.common.annotation.HttpRouter;
import com.cracker.api.mc.common.classloader.ClassClient;
import com.cracker.api.mc.common.codec.SystemCode;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.http.config.HttpConfig;
import com.cracker.api.mc.http.container.HttpRouterContainer;
import com.cracker.api.mc.http.handler.AbstractHttpWorkerHandler;
import com.cracker.api.mc.http.handler.HttpChannelInitializer;
import com.cracker.api.mc.http.handler.IHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * Http对外服务类，提供http各种服务
 * 1、Http对外暴露服务类，使用方式：new
 * 2、Http对象容器引用
 * 3、netty引用
 * 4、配置文件引用
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpServerBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerBootstrap.class);

    private static volatile boolean initialized = false;

    /**
     * Http对象容器池
     */
    public static HttpRouterContainer container = HttpRouterContainer.getInstance();


    /**
     * 接收网络连接事件的工作线程
     */
    private EventLoopGroup bossGroup;

    /**
     * 网络建立连接后，处理网络数据读写的工作线程
     */
    private EventLoopGroup workerGroup;

    /**
     * 执行请求处理任务线程组
     */
    private EventExecutorGroup requestGroup;

    /**
     * 服务启动器
     */
    private ServerBootstrap bootstrap;

    /**
     * 监听网络信号通道流
     */
    private List<Channel> listenChannels;



    /**
     * 项目启动时只会也只能执行一次，故这里不处理多线程场景。不能滥用误用
     *
     * @param configPath 配置文件路径
     */
    public void init(String configPath) throws InternalServerException {

        if (initialized) {
            return;
        }

        Assert.notEmpty(configPath, "configPath must not null");

        // 初始化HttpConfig
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("start to init HttpConfig: {}", configPath);
        }
        HttpConfig.init(configPath);

        // 启动http服务
        LOGGER.info("start to startup http-service ... ");
        // cpu核数
        int processNum = Runtime.getRuntime().availableProcessors();
        this.startUpHttpService(HttpConfig.getInstance().getChannelReadTimeout(), HttpConfig.getInstance().getChannelWriteTimeout(),
                (int) HttpConfig.getInstance().getChannelDefaultAggregator(), processNum << 1, processNum << 2, processNum << 2, null);

        LOGGER.info("startup http-service succeed ... ");

        // 添加http监听，出现异常直接抛
        if (this.addListen(new InetSocketAddress(HttpConfig.getInstance().getDefaultPort())) != 0) {
            LOGGER.error("http-service add listen fail, can't bind the specified port: " + HttpConfig.getInstance().getDefaultPort());
            throw new IllegalStateException("http-service add listen fail, can't bind the specified port: "
                    + HttpConfig.getInstance().getDefaultPort());
        }

        LOGGER.info("binding port: {} succeed ... ", HttpConfig.getInstance().getDefaultPort());

        // 对HttpWorkerHandler对象进行生命周期管理
        // 扫描并加载指定路径的HttpWorkerHandler
        this.scanAndLoadHandler(HttpConfig.getInstance().getHandlerPackage());

        initialized = true;
    }

    /**
     * 启动基于netty的http服务
     *
     * @param channelReadTime          通道读取超时时长
     * @param channelWriteTime         通道写入超时时长
     * @param channelDefaultAggregator 每次request最大允许分配内存大小
     * @param listeners                网络连接监听线程数
     * @param workers                  网络读写线程数
     * @param requests                 请求数
     * @param fileDataDir              文件数据存储目录
     */
    private void startUpHttpService(int channelReadTime, int channelWriteTime, int channelDefaultAggregator,
                                    int listeners, int workers, int requests, String fileDataDir) {

        this.bossGroup = new NioEventLoopGroup(listeners);
        this.workerGroup = new NioEventLoopGroup(workers);
        this.listenChannels = Lists.newArrayList();

        if (requests > 0) {
            this.requestGroup = new DefaultEventExecutorGroup(requests);
        }

        this.bootstrap = new ServerBootstrap();
        this.bootstrap.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpChannelInitializer(this.requestGroup, channelReadTime, channelWriteTime, channelDefaultAggregator))
                .option(ChannelOption.SO_BACKLOG, 2 << 6)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        //服务启动后删除已经退出的临时文件
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = fileDataDir;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = fileDataDir;
    }

    /**
     * 添加网络地址监听
     *
     * @param address 网络地址
     * @return 0表示监听成功;1表示监听失败
     */
    private int addListen(InetSocketAddress address) {
        try {
            Channel channel = this.bootstrap.bind(address).sync().channel();
            this.listenChannels.add(channel);
            return 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 执行http服务
     */
    public void run() {
        try {
            for (Channel channel : this.listenChannels) {
                // 等待直到该connection关闭
                // 在这里不会关闭，除非手动关闭服务器或者服务器崩了
                channel.closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.workerGroup.shutdownGracefully();
            this.bossGroup.shutdownGracefully();
        }
    }


    /**
     * 扫描并加载指定路径的HttpWorkerHandler
     * @param classPackage HttpWorkerHandler路径
     */
    private void scanAndLoadHandler(String classPackage) throws InternalServerException {

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("scan and load handler from path: {}", classPackage);
        }

        Set<Class<?>> classSet = ClassClient
                .forClient()
                .build()
                .scanClasses(classPackage);

        if (Assert.isNotNull(classSet)) {

            for (Class<?> clazz : classSet) {
                if (clazz.isAnnotation()
                        || clazz.isAnonymousClass()
                        || clazz.isPrimitive()
                        || clazz.isEnum()
                        || clazz.isInterface()) {
                    continue;
                }

                if (clazz.isAnnotationPresent(HttpRouter.class)) {
                    try {
                        // 通过反射直接获取到指定的对象，当然也可以考虑使用动态代理
                        AbstractHttpWorkerHandler httpWorkerHandler = (AbstractHttpWorkerHandler) clazz.newInstance();
                        // 把当前的HttpWorkerHandler注册到HttpServerBootstrap
                        httpWorkerHandler.registerToServer(this);
                    } catch (Exception e) {
                        LOGGER.error(clazz.getName() + " initialize fail: {}", e.getMessage(), e);
                        throw new InternalServerException(SystemCode.IOC_ERROR, e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * 把请求方式、请求uri和对应的HttpWorkerHandler关联在一起，塞进容器
     * @param method 请求方式
     * @param uri 请求uri
     * @param handler HttpWorkerHandler
     * @throws BaseBusinessException 参照Spring等ioc框架，一旦ioc失败直接抛异常
     */
    public void registerHttpWorkerHandler(String method, String uri, IHandler handler)
            throws BaseBusinessException {
        container.registerHttpWorkerHandler(method, uri, handler);
    }

    /**
     * 把请求方式、请求uri和对应的Method关联在一起，塞进容器
     * @param method 请求方式
     * @param uri 请求uri
     * @param methodHandler Method
     * @param key 容器寻找HttpWorkerHandlerMethod的key
     * @throws BaseBusinessException 参照Spring等ioc框架，一旦ioc失败直接抛异常
     */
    public void registerHttpWorkerHandlerMethod(String method, String uri, Method methodHandler, String key)
            throws BaseBusinessException {
        container.registerHttpWorkerHandlerMethod(method, uri, methodHandler, key);
    }
}