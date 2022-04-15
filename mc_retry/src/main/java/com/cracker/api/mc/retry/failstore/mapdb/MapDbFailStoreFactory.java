package com.cracker.api.mc.retry.failstore.mapdb;

import com.cracker.api.mc.retry.failstore.AbstractFailStoreFactory;
import com.cracker.api.mc.retry.failstore.FailStore;

import java.io.File;

/**
 * mapDb失败记录存储工厂类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-17
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MapDbFailStoreFactory extends AbstractFailStoreFactory {

    @Override
    public String getName() {
        return "mapdb";
    }

    @Override
    public FailStore createFailStore(File dbPath, boolean needLock) {
        return new MapDbFailStore(dbPath, needLock);
    }

}