package com.cracker.api.mc.retry;

import com.cracker.api.mc.retry.config.RetryConfig;
import com.cracker.api.mc.retry.failstore.FailStore;
import com.cracker.api.mc.retry.strategy.RetryStrategy;

/**
 * 重试中心调度factory
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RetrySchedulerFactory {


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Builder() {}

        private RetryStrategy retryStrategy;
        private FailStore failStore;
        private RetryConfig retryConfig;
        private RetrySchedulerService.RetryListener retryListener;
        private String name;

        public RetrySchedulerService build() {
            return new RetrySchedulerService(this);
        }

        public Builder withFailStore(FailStore failStore) {
            this.failStore = failStore;
            return this;
        }

        public Builder withRetryConfig(RetryConfig retryConfig) {
            this.retryConfig = retryConfig;
            return this;
        }

        public Builder withRetryStrategy(RetryStrategy retryStrategy) {
            this.retryStrategy = retryStrategy;
            return this;
        }

        public Builder withRetryListener(RetrySchedulerService.RetryListener retryListener) {
            this.retryListener = retryListener;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public RetryStrategy getRetryStrategy() {
            return retryStrategy;
        }

        public FailStore getFailStore() {
            return failStore;
        }

        public RetryConfig getRetryConfig() {
            return retryConfig;
        }

        public RetrySchedulerService.RetryListener getRetryListener() {
            return retryListener;
        }

        public String getName() {
            return name;
        }
    }
}
