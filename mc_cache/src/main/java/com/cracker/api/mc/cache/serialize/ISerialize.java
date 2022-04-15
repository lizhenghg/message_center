package com.cracker.api.mc.cache.serialize;

/**
 * 序列化顶级接口
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-03
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public interface ISerialize {

    /**
     * 序列化Object
     * @param object 待序列化Object
     * @return byte[]
     */
    public abstract byte[] serialize(Object object);

    /**
     * 反序列化Object
     * @param clazz 反序列化key
     * @param bytes 反序列化content
     * @return <T> 返回的Object
     */
    public abstract <T> T deSerialize(Class<T> clazz, byte[] bytes);

}
