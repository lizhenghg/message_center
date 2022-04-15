package com.cracker.api.mc.retry.failstore;

import com.cracker.api.mc.retry.RetryTask;
import com.cracker.api.mc.retry.utils.Pair;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 失败记录存储接口
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-16
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface FailStore {

    /**
     * 获取mapDb文件路径
     * @return mapDb文件路径
     */
    String getPath();

    /**
     * 打开mapDb文件
     * @throws FailStoreException 业务异常
     */
    void open() throws FailStoreException;

    /**
     * 添加元素到mapDb
     * @param key key
     * @param value value
     * @throws FailStoreException 业务异常
     */
    void put(String key, String value) throws FailStoreException;

    /**
     * 添加元素到mapDb
     * @param key key
     * @param value value
     * @throws FailStoreException 业务异常
     */
    void put(String key, RetryTask value) throws FailStoreException;

    /**
     * 根据业务类型和数据类型查询数据聚合
     * @param size 返回数据数量
     * @param type 数据类型
     * @param <T> T
     * @return 返回数据集合
     */
    <T> List<Pair<String, String>> query(int size, Type type);

    /**
     * 查询全部mapDb数据集合
     * @param <T> T
     * @return 全部mapDb数据集合
     */
    <T> List<Pair<String, String>> queryAll();

    /**
     * 查询全部mapDb数据RetryTask集合
     * @param <T> T
     * @return 全部mapDb数据集合
     */
    <T> List<Pair<String, RetryTask>> queryAllRetryTask();


    /**
     * 获取指定key的value
     * @param key 关键词
     * @return key对应的value
     * @throws FailStoreException 业务异常
     */
    String get(String key) throws FailStoreException;

    /**
     * 删除指定key的value
     * @param key 关键词
     * @throws FailStoreException 业务异常
     */
    void delete(String key) throws FailStoreException;

    /**
     * 关闭mapDb
     * @throws FailStoreException 业务异常
     */
    void close() throws FailStoreException;

    /**
     * 销毁mapDb
     * @throws FailStoreException 业务异常
     */
    void destroy() throws FailStoreException;

    /**
     * 获取集合中元素个数
     * @return 元素个数
     * @throws FailStoreException 业务异常
     */
    int size() throws FailStoreException;
}
