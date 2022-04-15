package com.cracker.api.mc.common.classloader;


import javassist.NotFoundException;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 操作Class客户端。
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class ClassClient {


    /**
     * 饿汉模式
     */
    private final static ClassClient CLIENT = new ClassClient();
    private IClass iClass = null;

    /**
     * 防止单例被外部引用
     */
    private ClassClient() {}

    /**
     * 获取饿汉模式下的单例
     * @return Class操作客户端
     */
    public static ClassClient forClient() {
        return CLIENT;
    }

    /**
     * 创建IClass对象，返回调用对象ClassClient
     * @return Class操作客户端
     */
    public ClassClient build() {
        this.iClass = new ClassHelper();
        return this;
    }

    public Class<?> seekClass(final Object object, Class<?> parameterizedSuperClass, String typeParamName) {
        return this.iClass.seekClass(object, parameterizedSuperClass, typeParamName);
    }

    public Set<Class<?>> scanClasses(String classPackage) {
        return this.scanClasses(classPackage, true);
    }

    public Set<Class<?>> scanClasses(String classPackage, boolean recursive) {
        return this.iClass.scanClasses(classPackage, recursive);
    }

    public List<String> getParameterNamesByAsm(Method method)
            throws NotFoundException {
        return this.iClass.getParameterNamesByAsm(method);
    }

    public ClassLoader getDefaultClassLoader() {
        return this.iClass.getDefaultClassLoader();
    }

    public InputStream getInputStream(String dir) {
        return this.iClass.getInputStream(dir);
    }
}
