package com.cracker.api.mc.http.config;


import com.cracker.api.mc.common.config.AbstractConfig;
import com.cracker.api.mc.common.config.ConfigAdapter;

import java.util.Objects;

/**
 * mc_http项目的配置文件操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-12
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpConfig {

    /**
     * 如下为配置文件中的全部key
     */

    private static final String CHANNEL_READ_TIMEOUT = "http_channel_read_timeout";
    private static final String CHANNEL_WRITE_TIMEOUT = "http_channel_write_timeout";
    private static final String CHANNEL_DEFAULT_AGGREGATOR = "http_channel_default_aggregator";
    private static final String DEFAULT_PORT = "http_default_port";
    private static final String HANDLER_PACKAGE = "http_handler_package";

    /**
     * 类的导航，把抽象配置处理类导航到这里使用
     */
    private final AbstractConfig configInstance;

    /**
     * 一个配置文件对应一个类，使用单例全局唯一
     */
    private static volatile HttpConfig instance;

    /**
     * 配置文件路径
     */
    private static String filePath;

    /**
     * 扔外部使用，配置文件初始化之前必须先调用这个方法，引入文件路径
     * @param configPath 文件路径，非完整
     */
    public static void init(String configPath) {
        filePath = configPath + "http.properties";
    }

    /**
     * 防止外部调用
     */
    private HttpConfig() {
        this(filePath);
    }

    /**
     * 1、防止外部调用
     * 2、初始化配置文件处理类
     * @param filePath 配置文件完整路径
     */
    private HttpConfig(String filePath) {
        this.configInstance = new ConfigAdapter(filePath);
    }

    /**
     * 获取HttpConfig单例的方法
     * @return HttpConfig单例
     */
    public static HttpConfig getInstance() {
        if (instance == null) {
            synchronized (HttpConfig.class) {
                if (instance == null) {
                    instance = new HttpConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    /**
     * 如下为获取配置文件中指定key的value
     */


    public int getChannelReadTimeout() {
        return this.configInstance.getIntSetting(CHANNEL_READ_TIMEOUT);
    }

    public int getChannelWriteTimeout() {
        return this.configInstance.getIntSetting(CHANNEL_WRITE_TIMEOUT);
    }

    public long getChannelDefaultAggregator() {
        return this.configInstance.getLongSetting(CHANNEL_DEFAULT_AGGREGATOR);
    }

    public int getDefaultPort() {
        return this.configInstance.getIntSetting(DEFAULT_PORT);
    }

    public String getHandlerPackage() {
        return this.configInstance.getStringSetting(HANDLER_PACKAGE);
    }

    public void reload() {
        this.configInstance.reload();
    }
}
