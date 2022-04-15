package com.cracker.api.mc.retry.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import java.io.File;

/**
 * 文件锁操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-18
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class FileLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileLock.class);
    private FileChannel channel;
    private java.nio.channels.FileLock lock;
    private final RandomAccessFile raf;

    public FileLock(String fileName) {
        this(new File(fileName));
    }

    public FileLock(File file) {
        this.lock = null;
        FileUtils.createFileIfNotExist(file);

        try {
            this.raf = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException();
        }
    }

    public boolean tryLock() {
        boolean success = false;

        try {
            // 被其他线程占领了
            if (this.channel != null && this.channel.isOpen()) {
                return false;
            }

            this.channel = this.raf.getChannel();
            this.lock = this.channel.tryLock();
            if (this.lock == null) {
                return false;
            }

            success = true;
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return false;
        } finally {
            if (!success && this.channel != null) {
                try {
                    // channel关闭也不能释放锁，必须显式释放锁
                    this.channel.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        return true;
    }

    public void release() {

        try {
            if (this.lock != null) {
                this.lock.release();
            }
        } catch (Exception exception) {
            throw new RuntimeException();
        } finally {
            if (this.channel != null) {
                try {
                    this.channel.close();
                } catch (IOException var8) {
                    LOGGER.error("file channel close failed", var8);
                }
            }
        }
    }
}