package com.cracker.api.mc.retry.failstore;

import com.cracker.api.mc.retry.utils.FileLock;
import com.cracker.api.mc.retry.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 失败记录存储抽象类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractFailStore implements FailStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFailStore.class);

    protected File dbPath;
    protected FileLock fileLock;
    private final String home;
    private static final String DB_LOCK_NAME = "_db.lock";


    public AbstractFailStore(File dbPath, boolean needLock) {
        // C:\Users\24864\.retry\failStorePath\mapdb
        try {
            this.dbPath = dbPath;
            this.home = this.dbPath.getPath();
            // 判断是否需要加锁读取File，这里最后需要解锁，如果不解锁，对于相同的一个文件（C:\Users\24864\.retry\failStorePath\mapdb\_db.lock），就算有new多个RandomAccessFile(file, "rw")，后来
            // 加锁一样加不了
            if (needLock) {
                this.getLock(this.home);
            }

            this.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void getLock(String fileStorePath) throws IOException {
        FileUtils.createDirIfNotExist(fileStorePath);
        this.fileLock = new FileLock(String.format("%s%s", fileStorePath, File.separator + DB_LOCK_NAME));
        boolean locked = this.fileLock.tryLock();
        if (!locked) {
            throw new IllegalStateException("can not get current file lock.");
        }
        LOGGER.info("Current failStore path is {}", fileStorePath);
    }

    @Override
    public String getPath() {
        return this.dbPath.getPath();
    }

    /**
     * FailStore初始化
     */
    public abstract void init();
}
