package com.cracker.api.mc.scheduler;


import com.cracker.api.mc.cache.CacheServerBootstrap;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.executor.RetryClient;
import com.cracker.api.mc.executor.task.TaskCenterService;
import com.cracker.api.mc.http.HttpServerBootstrap;
import com.cracker.api.mc.mq.MqClient;
import com.cracker.api.mc.scheduler.queue.ProducingQueueService;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.io.File;

/**
 * mc_scheduler项目启动类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-9-29
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class SchedulerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerApplication.class);

    public static void main(String[] args) throws InternalServerException {

        String appBasePath = "";

        try {
            ProtectionDomain protectionDomain = SchedulerApplication.class.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            appBasePath = URLDecoder.decode(codeSource.getLocation().toURI().getPath(), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException | URISyntaxException ex) {
            LOGGER.warn("can't find application root path, possibility startup failed: {}", ex.getMessage(), ex);
        }

        File jarFile = new File(appBasePath);
        appBasePath = Assert.requireNonEmpty(jarFile.getParentFile().getPath(), "root path is null");

        String configPath;
        File file = new File(appBasePath);

        if (file.exists() && file.isDirectory()) {
            configPath = String.format("%s/config/", appBasePath);
        } else {
            LOGGER.error("maybe the root path is not exist, startup failed, bye bye ... ");
            return;
        }

        // 启动全局应用程序log4j，动态监控，每60000毫秒(60s)监控一次log4j.xml内容是否改动，比如是否更改日志等级
        String log4jPath = String.format("%slog4j.xml", configPath);
        // apache log4j漏洞受影响版本：2.0 <= Apache Log4j <= 2.15.0-rc1，1.x不受影响
        DOMConfigurator.configureAndWatch(log4jPath, 1875 << 5);

        // mc_cache子模块应用程序初始化
        CacheServerBootstrap.init(configPath);

        // mc_mq子模块应用程序初始化
        MqClient.init(configPath);

        // 初始化mc_executor配置
        TaskCenterService.init(configPath);

        // 设置mc_mq的消费者监听器
        MqClient.getInstance().setHandler(TaskCenterService.getInstance());

        // 初始化数据源队列(fQueue)
        ProducingQueueService.init(configPath);

        // mc_retry子模块应用程序初始化
        RetryClient.init();


        // 异常关闭导致缓存中可能存在重试次数不足的数据，在项目重启后
        // 再次重新进入重试队列
        RetryClient.getInstance().onRetryAbnormalShutDown();

        // mc_http子模块应用程序初始化
        HttpServerBootstrap server = new HttpServerBootstrap();
        server.init(configPath);
        server.run();
    }
}