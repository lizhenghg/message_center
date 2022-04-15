package com.cracker.api.mc.executor.config;

import com.cracker.api.mc.common.config.AbstractConfig;
import com.cracker.api.mc.common.config.ConfigAdapter;

import java.util.Objects;

/**
 * mc_executor项目的配置文件操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-10
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class ExecutorConfig {


    /**
     * 如下为executor.properties配置文件的所有key
     */
    private static final String TASK_THREAD_COUNT = "executor_task_thread_count";


    /**
     * 类的导航，把抽象配置处理类导航到这里使用
     */
    private final AbstractConfig configInstance;

    /**
     * 一个配置文件对应一个类，使用单例全局唯一
     */
    private static volatile ExecutorConfig instance;

    /**
     * 配置文件路径
     */
    private static String filePath;

    /**
     * 扔外部使用，配置文件初始化之前必须先调用这个方法，引入文件路径
     * @param configPath 文件路径，非完整
     */
    public static void init(String configPath) {
        filePath = configPath + "executor.properties";
    }

    /**
     * 防止外部调用
     */
    private ExecutorConfig() {
        this(filePath);
    }

    /**
     * 1、防止外部调用
     * 2、初始化配置文件处理类
     * @param filePath 配置文件完整路径
     */
    private ExecutorConfig(String filePath) {
        this.configInstance = new ConfigAdapter(filePath);
    }

    /**
     * 获取ExecutorConfig单例的方法
     * @return ExecutorConfig单例
     */
    public static ExecutorConfig getInstance() {
        if (instance == null) {
            synchronized (ExecutorConfig.class) {
                if (instance == null) {
                    instance = new ExecutorConfig();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    /**
     * 如下为获取配置文件中指定key的value
     */

    public int getTaskThreadCount() {
        return this.configInstance.getIntSetting(TASK_THREAD_COUNT);
    }



    public void reload() {
        this.configInstance.reload();
    }
}