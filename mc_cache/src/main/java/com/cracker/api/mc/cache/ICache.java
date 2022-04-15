package com.cracker.api.mc.cache;

import java.util.Map;

/**
 *
 * 缓存系统中层组件，作为上层与下层的耦合剂：ICache
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-27
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface ICache<T> {

    /**
     * 设置当前缓存存储的类对象
     *
     * @param classType 类Class
     * @param cacheName Class名
     */
    public abstract void setClassType(Class<T> classType, String cacheName);

    /**
     * 根据指定的key获取缓存值，如果不存在，则返回null
     *
     * @param key 获取缓存值的key
     * @return T
     */
    public abstract T get(String key);

    /**
     * 往缓存中推送要存储的数据，如果存在，则更新
     *
     * @param key 要推送值的key
     * @param obj 要推送的对象
     * @return boolean 推送成功与否
     */
    public abstract boolean put(String key, T obj);

    /**
     * 往缓存中推送要存储的数据，如果存在，则更新.同时设置失效期
     *
     * @param key 要推送值的key
     * @param obj 要推送的对象
     * @param expireTime 失效时长
     * @return boolean 推送成功与否
     */
    public abstract boolean put(String key, T obj, long expireTime);

    /**
     * 清空缓存对象
     * @return boolean 清空成功与否标识；true表示成功；false表示失败
     */
    public abstract boolean clear();


    /**
     * 通过指定的key删除指定的缓存
     *
     * @param key 要删除缓存的key
     * @return boolean 删除成功与否
     */
    abstract boolean remove(String key);


    /**
     * hash类型操作
     * 通过指定的key删除指定的缓存
     *
     * @param key 要删除缓存的key
     * @return boolean 删除成功与否标识
     */
    abstract boolean hRemove(String key);


    /**
     * hash类型操作
     * 通过指定的key删除指定的缓存
     *
     * @return boolean 删除成功与否标识
     */
    abstract boolean hRemoveAll();

    /**
     * 从缓存中寻找是否给定key值的缓存
     *
     * @param key 将要寻找的缓存的key
     * @return 是否存在，true表示存在；false表示不存在
     */
    abstract boolean containKey(String key);

    /**
     * 替换掉缓存中存在的key-value
     *
     * @param key 将要替换的缓存的key
     * @param obj 将要替换的缓存
     * @return 替换成功与否
     */
    abstract boolean replace(String key, T obj);

    /**
     * 获取全部键值对
     *
     * @return 全部键值对
     */
    Map<String, T> getAll();

    /**
     * 获取缓存存储数据总数
     *
     * @return 缓存数据总数
     */
    long size();

}
