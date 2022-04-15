package com.cracker.api.mc.retry.utils;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作类
 *
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class FileUtils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static File createFileIfNotExist(File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                if (!file.createNewFile()) {
                    throw new IOException("create file[" + file.getAbsolutePath() + "] failed!");
                }
            } catch (IOException ioException) {
                throw new RuntimeException("create file[" + file.getAbsolutePath() + "] failed!", ioException);
            }
        }
        return file;
    }

    public static File createFileIfNotExist(String filePath) {
        return createFileIfNotExist(new File(filePath));
    }

    public static File createDirIfNotExist(File dir) throws IOException {
        if (dir == null) {
            return null;
        }
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("couldn't mkdirs for the dir: " + dir.getAbsolutePath());
            }
        }
        if (!dir.isDirectory()) {
            throw new IOException("Not a directory [" + dir.getAbsolutePath() + "]");
        }
        if (!dir.canWrite()) {
            throw new IOException("Not writable [" + dir.getAbsolutePath() + "]");
        }
        return dir.getAbsoluteFile();
    }

    public static File createDirIfNotExist(String filePath) throws IOException {
        return createDirIfNotExist(new File(filePath));
    }

    public static void delete(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] delFiles = file.listFiles();
                if (delFiles != null && delFiles.length != 0) {
                    for (File delFile : delFiles) {
                        if (delFile.isDirectory()) {
                            delete(delFile);
                        }
                        delFile.delete();
                    }
                }
            } else {
                file.delete();
            }
        }
    }
}