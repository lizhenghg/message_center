package com.cracker.api.mc.cache.serialize;


/**
 * 序列化构造器
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-03
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class SerializeBuilder {

    private final Serialize serialize = new Serialize();

    public static SerializeBuilder builder() {
        return new SerializeBuilder();
    }

    public byte[] serialize(Object object) {
        return this.serialize.serialize(object);
    }

    public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
        return this.serialize.deSerialize(clazz, bytes);
    }
}
