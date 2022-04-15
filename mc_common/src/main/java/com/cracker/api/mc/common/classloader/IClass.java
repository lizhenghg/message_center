package com.cracker.api.mc.common.classloader;

import javassist.NotFoundException;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 类工具操作接口
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface IClass {

    /**
     * 寻找指定的Class，一般是泛型T之类的Class。通过父类Class和typeName
     * @param object 待寻找的Class的对应Object
     * @param parameterizedSuperClass 父类Class
     * @param typeParamName 待寻找的Class的typeName
     * @return what you want Class<?>
     */
    public abstract Class<?> seekClass(Object object, Class<?> parameterizedSuperClass, String typeParamName);

    /**
     * 扫描指定路径的class包，返回全部被扫描的Class，但不递归处理文件夹包含子文件夹情况。适用于对象管理
     * @param classPackage class包路径
     * @return 扫描的class集合
     */
    public abstract Set<Class<?>> scanClasses(String classPackage);

    /**
     * 扫描指定路径的class包，返回全部被扫描的Class，可选择递归调用处理文件夹包含子文件夹情况。适用于对象管理
     * @param classPackage class包路径
     * @param recursive 是否递归调用来处理文件夹嵌套文件夹情况，true为yes，false为no
     * @return 扫描的class集合
     */
    public abstract Set<Class<?>> scanClasses(String classPackage, boolean recursive);

    /**
     * 根据Method获取该Method全部所有的参数，就是方法的传参非实参
     * @param method need to parse Method
     * @return 方法传参的List<String>集合
     * @throws NotFoundException 指定not found exception
     */
    public abstract List<String> getParameterNamesByAsm(Method method) throws NotFoundException;


    /**
     * 获取Class加载器
     * @return ClassLoader
     */
    public abstract ClassLoader getDefaultClassLoader();

    /**
     * 根据dir获取resource资源
     * @param dir 资源dir
     * @return 字节输入流
     */
    public abstract InputStream getInputStream(String dir);

}
