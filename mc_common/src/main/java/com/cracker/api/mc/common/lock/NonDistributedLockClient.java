package com.cracker.api.mc.common.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 同步锁客户端,非分布式
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-04
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class NonDistributedLockClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NonDistributedLockClient.class);

    private static volatile NonDistributedLockClient instance = null;

    private final ILock lockComponent;

    public static NonDistributedLockClient getInstance() {
        if (instance == null) {
            synchronized (NonDistributedLockClient.class) {
                if (instance == null) {
                    LOGGER.info("init NonDistributedLockClient successfully");
                    instance = new NonDistributedLockClient();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    private NonDistributedLockClient() {
        this.lockComponent = new ConcurrentMapLock();
    }


    public boolean lock(String key) {
        return this.lockComponent.lock(key);
    }

    public boolean lock(String key, long expireTime) {
        return this.lockComponent.lock(key, expireTime);
    }

    public boolean lockWithoutBlock(String key) {
       return this.lockComponent.lockWithoutBlock(key);
    }

    public void unlock(String key) {
        this.lockComponent.unlock(key);
    }
}
