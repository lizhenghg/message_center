package com.cracker.api.mc.retry.failstore.mapdb;

import com.cracker.api.mc.retry.RetryTask;
import com.cracker.api.mc.retry.failstore.AbstractFailStore;
import com.cracker.api.mc.retry.failstore.FailStoreException;
import com.cracker.api.mc.retry.utils.FileUtils;
import com.cracker.api.mc.retry.utils.Pair;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 * 基于mapDb的失败记录存储类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MapDbFailStore extends AbstractFailStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapDbFailStore.class);

    private DB db;
    private ConcurrentNavigableMap<String, Object> map;

    public MapDbFailStore(File dbPath, boolean needLock) {
        super(dbPath, needLock);
    }


    @Override
    public void init() {
        try {
            String dbName = super.getPath() + File.separator + "retry.db";
            this.db = DBMaker.fileDB(new File(dbName)).closeOnJvmShutdown().encryptionEnable("retry").make();
        } catch (Exception ex) {
            LOGGER.error("init mapDB fail: {}", ex.getMessage(), ex);
            throw ex;
        }
    }


    @Override
    public void open() throws FailStoreException {
        try {
            this.map = this.db.treeMap("retry");
        } catch (Exception ex) {
            LOGGER.error("open mapDB fail: {}", ex.getMessage(), ex);
            throw new FailStoreException(ex);
        }
    }


    @Override
    public void put(String key, String value) throws FailStoreException {
        try {
            this.map.put(key, value);
            this.db.commit();
        } catch (Exception ex) {
            this.db.rollback();
            LOGGER.error("put key-value = {}-{} into mapDB fail: {}",
                    key, value, ex.getMessage(), ex);
            throw new FailStoreException(ex);
        }
    }


    @Override
    public void put(String key, RetryTask value) throws FailStoreException {
        try {
            this.map.put(key, value);
            this.db.commit();
        } catch (Exception ex) {
            this.db.rollback();
            LOGGER.error("put key-value = {}-{} into mapDB fail: {}",
                    key, value, ex.getMessage(), ex);
            throw new FailStoreException(ex);
        }
    }

    @Override
    public String get(String key) {
        String value = "";
        if (this.map.containsKey(key)) {
            value = (String) this.map.get(key);
        }
        return value;
    }


    @Override
    public List<Pair<String, String>> query(int size, Type type) {

        if (this.map.size() == 0) {
            return Collections.emptyList();
        }

        List<Pair<String, String>> list = new ArrayList<>(size);

        Iterator<Map.Entry<String, Object>> iterator = this.map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            String value = (String) entry.getValue();
            Pair<String, String> pair = new Pair<>(key, value);

            list.add(pair);
            if (list.size() >= size) {
                break;
            }
        }

        return list;
    }


    @Override
    public <T> List<Pair<String, String>> queryAll() {

        if (this.map.size() == 0) {
            return Collections.emptyList();
        }

        List<Pair<String, String>> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : this.map.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();
            Pair<String, String> pair = new Pair<>(key, value);

            list.add(pair);
        }

        return list;
    }

    @Override
    public <T> List<Pair<String, RetryTask>> queryAllRetryTask() {
        if (this.map.size() == 0) {
            return Collections.emptyList();
        }

        List<Pair<String, RetryTask>> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : this.map.entrySet()) {
            String key = entry.getKey();
            RetryTask value = (RetryTask) entry.getValue();
            Pair<String, RetryTask> pair = new Pair<>(key, value);

            list.add(pair);
        }

        return list;
    }


    @Override
    public void delete(String key) throws FailStoreException {
        try {
            this.map.remove(key);
            this.db.commit();
        } catch (Exception ex) {
            this.db.rollback();
            LOGGER.error("delete key = {} from mapDB fail: {}", key, ex.getMessage(), ex);
            throw new FailStoreException(ex);
        }
    }


    @Override
    public void close() throws FailStoreException {
        try {
            this.db.close();
        } catch (Exception ex) {
            LOGGER.error("close mapDB fail: {}", ex.getMessage(), ex);
            throw new FailStoreException(ex);
        }
    }


    @Override
    public void destroy() throws FailStoreException {
        try {
            this.close();
        } catch (Exception ex) {
            LOGGER.error("destroy mapDB fail: {}", ex.getMessage(), ex);
            throw new FailStoreException(ex);
        } finally {
            FileUtils.delete(super.dbPath);
        }
    }

    @Override
    public int size() {
        return this.map.size();
    }
}