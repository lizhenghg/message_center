package com.cracker.api.mc.retry.failstore;

import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.retry.config.RetryConfig;
import com.cracker.api.mc.retry.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 失败记录存储工厂抽象类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractFailStoreFactory implements FailStoreFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFailStoreFactory.class);

    public AbstractFailStoreFactory() {}

    @Override
    public FailStore getFailStore(RetryConfig config, String storePath) {
        if (Assert.isEmpty(storePath)) {
            storePath = config.getFailStorePath();
        }

        File storeFile = new File(storePath.concat(this.getName()));

        try {
            FileUtils.createDirIfNotExist(storeFile);
        } catch (IOException ioException) {
            LOGGER.error("unexpected FailStore exception: {}", ioException.getMessage(), ioException);
        }

        return this.createFailStore(storeFile, true);
    }

    /**
     * 获取FailStore存储器命名，比如mapdb、fQueue...
     * @return FailStore存储器命名
     */
    public abstract String getName();

    /**
     * 创建FailStore
     * @param dbPath 存储db文件路径
     * @param needLock 是否需要独占锁
     * @return FailStore
     */
    public abstract FailStore createFailStore(File dbPath, boolean needLock);
}