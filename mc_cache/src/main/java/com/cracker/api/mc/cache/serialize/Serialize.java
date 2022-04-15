package com.cracker.api.mc.cache.serialize;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.Sets;
import com.cracker.api.mc.cache.config.CacheConfig;
import com.cracker.api.mc.common.classloader.ClassClient;
import com.cracker.api.mc.common.validate.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * 序列化实现类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-03
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class Serialize implements ISerialize {

    private static final ThreadLocal<Kryo> THREAD_LOCAL_KRYO = new ThreadLocal<>();

    private static final Set<Class<?>> HS_CLASS = Sets.newLinkedHashSet();

    /**
     * 给Kryo划分的保存对象序列化的内存空间
     * 这里随着业务深入需要划分更多的内存空间，因为每次的主题Object的大小只会变大不会变小。默认给1k * 1024 = 1M
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
    private final int bufferSize = DEFAULT_BUFFER_SIZE;


    public static void init() {
        if (Assert.isNotNull(HS_CLASS)) {
            return;
        }
        String cacheVoPackage = CacheConfig.getInstance().getCacheVoPackage();
        Set<Class<?>> set = ClassClient
                .forClient()
                .build()
                .scanClasses(cacheVoPackage);
        if (Assert.isNotNull(set)) {
            for (Class<?> clazz : set) {
                if (clazz.isAnnotation()
                        || clazz.isAnonymousClass()
                        || clazz.isPrimitive()
                        || clazz.isEnum()
                        || clazz.isInterface()) {
                    continue;
                }
                addClass(clazz);
            }
        }
    }

    /**
     * public protected default private
     * 只允许：同一个类、同一个包中的类、不同包的子类这三个范围的类去new
     */
    protected Serialize() {
    }

    private static void addClass(Class<?> clazz) {
        HS_CLASS.add(clazz);
    }


    private Kryo getKryo() {
        Kryo kryo = THREAD_LOCAL_KRYO.get();
        return kryo == null ? buildKryo() : kryo;
    }

    private Kryo buildKryo() {

        Kryo kryo = new Kryo();
        int index = 1;

        for (Iterator<Class<?>> iterator = HS_CLASS.iterator(); iterator.hasNext();) {
            Class<?> loadClass = iterator.next();
            kryo.register(loadClass, index++);
        }
        THREAD_LOCAL_KRYO.set(kryo);
        return kryo;
    }


    @Override
    public byte[] serialize(Object object) {
        // ByteArrayOutputStream可以不用关闭资源
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 这里随着业务深入需要划分更多的内存空间，因为每次的主题Object的大小只会变大不会变小。默认给1024 * 1024 = 1M
            Output output = new Output(bos, bufferSize);
            getKryo().writeObject(output, object);

            output.flush();
            return bos.toByteArray();
        } finally {
            THREAD_LOCAL_KRYO.remove();
        }
    }


    @Override
    public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
        // ByteArrayInputStream可以不用关闭资源
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Input input = new Input(bis);

            return getKryo().readObject(input, clazz);
        } finally {
            THREAD_LOCAL_KRYO.remove();
        }
    }
}