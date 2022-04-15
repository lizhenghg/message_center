package com.cracker.api.mc.retry.config;

import java.io.File;

/**
 * 重试中心配置类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RetryConfig {

    private long retryTimePeriod = 5000L;
    private int threadPoolSize = 5;
    private int maxThreadPoolSize = 15;
    private String dataPath = System.getProperty("user.home");

    public RetryConfig() {}

    public String getFailStorePath() {
        return String.format("%s%s%s", this.dataPath, File.separator + ".retry",
                File.separator + "failStorePath" + File.separator);
    }

    public long getRetryTimePeriod() {
        return retryTimePeriod;
    }

    public void setRetryTimePeriod(long retryTimePeriod) {
        this.retryTimePeriod = retryTimePeriod;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public int getMaxThreadPoolSize() {
        return maxThreadPoolSize;
    }

    public void setMaxThreadPoolSize(int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}