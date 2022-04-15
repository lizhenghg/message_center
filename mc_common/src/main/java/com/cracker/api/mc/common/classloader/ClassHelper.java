package com.cracker.api.mc.common.classloader;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.util.Symbol;
import com.cracker.api.mc.common.validate.Assert;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 操作Class助手类，类似这种工具类，一般应该使用非单例
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ClassHelper implements IClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassHelper.class);

    private final String[] converter = new String[0];

    /**
     * 由于ClassHelper非单例，既可以使用ThreadLocal线程本地副本来保存多次操作的结果，当然也可以使用Set<String>和Map<String, String>成员变量
     */
    private static final ThreadLocal<Set<String>> FOLDER_RECORDER = new ThreadLocal<>();

    private static final ThreadLocal<Map<String, String>> INITIAL_MAP = new ThreadLocal<>();

    private static Set<String> getSet() {
        return FOLDER_RECORDER.get() == null ? buildSet() : FOLDER_RECORDER.get();
    }

    private static Set<String> buildSet() {
        Set<String> set = Sets.newLinkedHashSet();
        FOLDER_RECORDER.set(set);
        return set;
    }

    private static Map<String, String> getMap() {
        return INITIAL_MAP.get() == null ? buildMap() : INITIAL_MAP.get();
    }

    private static Map<String, String> buildMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        INITIAL_MAP.set(map);
        return map;
    }


    /**
     * 同一类|同一包中的类|不同包的子类才可以实例化
     */
    ClassHelper() {}


    /**
     * 根据指定对象、Class、泛型字义，获取该Class对象的泛型参数对应的实体，返回该实体Class
     * sample：
     * public class EstablishPointTableCache extends AbstractCache<EstablishPointTableVO> {}
     * public abstract class AbstractCache<T>{}
     * Object object对应：EstablishPointTableCache
     * parameterizedSuperClass对应：AbstractCache的Class
     * typeParamName对应："T"
     *
     * @param object object
     * @param parameterizedSuperClass parameterizedSuperClass
     * @param typeParamName typeParamName
     * @return Class<?>
     */
    @Override
    public Class<?> seekClass(final Object object, Class<?> parameterizedSuperClass,
                              String typeParamName) {
        final Class<?> thisClass = object.getClass();
        Class<?> superClass = thisClass.getSuperclass();
        // 判断传进来的前面两个参数是否继承关系
        if (superClass != parameterizedSuperClass) {
            return Object.class;
        }
        Type genericSuperType;
        // 判断parameterizedSuperClass是否泛型，不是的话直接抛异常
        if (!((genericSuperType = thisClass.getGenericSuperclass())
                instanceof ParameterizedType)) {
            LOGGER.error(parameterizedSuperClass + " must be ParameterizedType");
            throw new IllegalArgumentException(parameterizedSuperClass + " must be ParameterizedType");
        }

        // 判断parameterizedSuperClass泛型对应的泛型字义(一般是T、K、V、E...)
        TypeVariable<?>[] typeParams = superClass.getTypeParameters();
        int typeParamIndex = -1;
        for (int i = 0, m = typeParams.length; i < m; i++) {
            if (typeParamName.equals(typeParams[i].getName())) {
                typeParamIndex = i;
                break;
            }
        }
        if (typeParamIndex == -1) {
            LOGGER.error("invalid param：{}, it must be the same as：{}", typeParamName, parameterizedSuperClass + "'TypeParameters");
            throw new IllegalArgumentException("invalid param：" + typeParamName);
        }
        // 这里开始解析获取EstablishPointTableVO，得到的是EstablishPointTableVO的Class
        // 正常情况是这样的：public class EstablishPointTableCache extends AbstractCache<EstablishPointTableVO>
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperType).getActualTypeArguments();
        Type actualTypeArgument = actualTypeArguments[typeParamIndex];

        // 二次判断还属于泛型的话，属于这种情况：
        // public class EstablishPointTableCache<T> extends AbstractCache<EstablishPointTableVO<T>>
        // 此时解析获取到的是EstablishPointTableVO<T>
        if (actualTypeArgument instanceof ParameterizedType) {
            // 这里获取EstablishPointTableVO
            actualTypeArgument = ((ParameterizedType) actualTypeArgument).getRawType();
        }
        // 找到了EstablishPointTableVO的Class
        if (actualTypeArgument instanceof Class) {
            return (Class<?>) actualTypeArgument;
        }
        // 判断泛型数组，属于下面情况之一(不唯一)：
        // public class EstablishPointTableCache<T> extends AbstractCache<EstablishPointTableVO<T>[]>
        // public class EstablishPointTableCache<T> extends AbstractCache<EstablishPointTableVO<String>[]>
        if (actualTypeArgument instanceof GenericArrayType) {
            // 这里获取到EstablishPointTableVO<T>或者EstablishPointTableVO<String>
            Type genericComponentType = ((GenericArrayType) actualTypeArgument).getGenericComponentType();
            if (genericComponentType instanceof ParameterizedType) {
                // 获取了T或者String的Class
                genericComponentType = ((ParameterizedType) genericComponentType).getRawType();
            }
            if (genericComponentType instanceof Class) {
                return (Class<?>) genericComponentType;
            }
        }
        // 其他情况<K,V>暂时不作处理(跟业务有悖)
        return fail(parameterizedSuperClass, typeParamName);
    }


    private Class<?> fail(Class<?> type, String typeParamName) {
        throw new IllegalStateException("cannot determine the type of the parameter '" + typeParamName + "'：" + type);
    }


    /**
     * 扫描指定路径的class包，返回全部被扫描的Class，但不递归处理文件夹包含子文件夹情况。适用于对象管理
     * @param classPackage class包路径
     * @return 扫描的class集合
     */
    @Override
    public Set<Class<?>> scanClasses(String classPackage) {
        return this.scanClasses(classPackage, true);
    }

    /**
     * 扫描指定路径的class包，返回全部被扫描的Class，可选择递归调用处理文件夹包含子文件夹情况。适用于对象管理。
     * 加锁，仅仅是为了处理可能存在的主线程在运行中多次调用该方法，由于该方法存在数据重复操作情况，极容易导致数据被覆盖。
     * @param classPackage class包路径
     * @param recursive 是否递归调用来处理文件夹嵌套文件夹情况，true为yes，false为no
     * @return 扫描的class集合
     */
    @Override
    public synchronized Set<Class<?>> scanClasses(String classPackage, boolean recursive) {
        if (!Assert.isNotNull(classPackage)) {
            return Collections.emptySet();
        }
        Set<Class<?>> classes = Sets.newLinkedHashSet();
        String packageName = classPackage;
        String packageDirName = packageName.replace(Symbol.PERIOD, Symbol.SEPARATORS);

        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().
                    getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8.name());
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        //获取jar里的一个实体 可以是目录和一些jar包里的其他文件 如META-INF等文件
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.charAt(0) == '/') {
                            name = name.substring(1);
                        }
                        if (name.startsWith(packageDirName)) {
                            int idx = name.lastIndexOf('/');
                            if (idx != -1) {
                                packageName = name.substring(0, idx).replace('/', '.');
                            }
                            if ((idx != -1) || recursive) {
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        classes.add(Class.forName(packageName + '.' + className));
                                        if (LOGGER.isInfoEnabled()) {
                                            LOGGER.info("scanClasses, load: {} succeed", packageName + "." + className + ".Class");
                                        }
                                    } catch (ClassNotFoundException e) {
                                        LOGGER.warn("ClassNotFoundException, localClass fail：{}", className, e);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error(String.format("IOException, fail to scan class, please check the package：%s", classPackage), e);
            throw new CommonException("IOException, fail to scan class, please check the package：" + classPackage, e);
        } finally {
            // 至少执行一次remove()，避免线程是从线程池启动的，最后线程池不对该线程进行回收的情况出现，这样的话极容易出现内存泄漏
            FOLDER_RECORDER.remove();
            INITIAL_MAP.remove();
        }
        return classes;
    }


    /**
     * 根据Method获取该Method全部所有的参数名称，就是方法的传参非实参
     * @param method need to parse Method
     * @return 方法传参的List<String>集合
     * @throws NotFoundException 指定元素找不到异常
     */
    @Override
    public List<String> getParameterNamesByAsm(Method method) throws NotFoundException {
        if (method.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
        CtMethod[] arrayM = ctClass.getDeclaredMethods(method.getName());
        CtMethod ctMethod = arrayM[0];

        boolean flag = true;
        if (arrayM.length > 1) {
            for (CtMethod cm : arrayM) {
                if (cm.getName().equals(method.getName())
                        && cm.getParameterTypes().length == method.getParameterCount()) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (Assert.isNotNull(paramTypes)) {
                        CtClass[] ctParamTypes = cm.getParameterTypes();
                        for (int i = 0, len = paramTypes.length; i < len; i++) {
                            if (!paramTypes[i].getName().equalsIgnoreCase(ctParamTypes[i].getName())) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            ctMethod = cm;
                            break;
                        }
                    }
                }
            }
        }
        if (!flag) {
            throw new NotFoundException("couldn't find the method:" + method.getName());
        }
        //通过javassist的反射方法获取方法的参数名
        MethodInfo methodInfo = ctMethod.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

        List<String> parameterNames = Lists.newArrayList();
        int len = ctMethod.getParameterTypes().length;
        int pos = 0;

        //非static方法要这样子处理，找到table中this之后的参数位置，从那里开始读取
        if (!Modifier.isStatic(ctMethod.getModifiers())) {
            for (int i = 0, count = attribute.tableLength(); i < count; i++) {
                if ("this".equalsIgnoreCase(attribute.variableName(i))) {
                    pos = i + 1;
                    break;
                }
            }
        }
        for (int i = 0; i < len; i++) {
            parameterNames.add(attribute.variableName(pos + i));
        }
        return parameterNames;

    }

    /**
     * 寻找类加载器
     * @return ClassLoader
     */
    @Override
    public ClassLoader getDefaultClassLoader() {

        ClassLoader classLoader = null;

        try {
            //启动类加载器(最顶级)
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            LOGGER.warn("getDefaultClassLoader, Cannot access thread context ClassLoader");
        }

        if (classLoader == null) {
            //扩展类加载器(次顶级)
            classLoader = ClassHelper.class.getClassLoader();
            if (classLoader == null) {
                try {
                    //应用程序类加载器
                    classLoader = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    LOGGER.error("getDefaultClassLoader, Cannot access system ClassLoader, exceptionCaught: {}",
                            ex.getMessage(), ex);
                    throw new ClassCastException("getDefaultClassLoader, Cannot access system ClassLoader, exceptionCaught: {}" +
                            ex.getMessage());
                }
            }
            // 不再寻找，直抛异常
            if (classLoader == null) {
                LOGGER.error("getDefaultClassLoader, Cannot access system ClassLoader - maybe the caller can live with null...");
                throw new ClassCastException("getDefaultClassLoader, Cannot access system ClassLoader - maybe the caller can live with null...");
            }
        }
        return classLoader;
    }

    /**
     * 根据dir获取resource资源
     * @param dir 资源dir
     * @return 字节输入流
     */
    @Override
    public InputStream getInputStream(String dir) {

        if (Assert.isEmpty(dir)) {
            return null;
        }
        InputStream inputStream;
        File file = new File(dir);

        // 直接从文件系统路径加载
        if (file.exists() && file.isFile()) {
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            // 从类路径加载
            inputStream = getDefaultClassLoader().getResourceAsStream(dir);
        }
        return inputStream;
    }


    /**
     * 本方法适用单文件夹获取class以及父文件夹中文件夹与class文件共存场景
     *
     * @param packageName = "xx.xx.xx"
     * @param packagePath = "/xx/xx/xx"
     * @param recursive 递归标识
     * @param classes 待返回class集合
     */
    private void findAndAddClassesInPackageByFile(String packageName, String packagePath,
                                                  final boolean recursive, Set<Class<?>> classes) {
        if (!Assert.isNotNull(getMap())) {
            INITIAL_MAP.get().put(packageName, packagePath);
        }
        File dir = new File(packagePath);
        if (!(dir.exists() && dir.isDirectory())) {
            return;
        }
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (recursive && pathname.isDirectory()) || (pathname.getName().endsWith(".class"));
            }
        });

        Map<String, String> paramMap = INITIAL_MAP.get();
        String key = paramMap.values().toArray(converter)[0];
        String value = paramMap.get(key);

        if (Assert.isNotNull(dirFiles)) {

            String finalPath;

            for (File file : dirFiles) {
                if (file.isDirectory()) {
                    finalPath = file.getPath().replace("\\", "/");
                    String suffixStr = value;
                    if (value.charAt(0) == '/') {
                        suffixStr = value.substring(1);
                    }
                    getSet().add(finalPath.replace(suffixStr, ""));
                    continue;
                }
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + "." + className));
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("findAndAddClassesInPackageByFile, load: {} succeed", packageName + "." + className + ".class");
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.warn("ClassNotFoundException, localClass fail：{}", className, e);
                }
            }
            //在一个父文件夹里，先处理完该层全部的class文件，然后再处理子文件夹
            Set<String> classSet;
            if (Assert.isNotNull(classSet = FOLDER_RECORDER.get())) {
                String relativePath = classSet.toArray(converter)[0];
                classSet.remove(relativePath);
                findAndAddClassesInPackageByFile(key + relativePath.replace("/", "."),
                        value + relativePath, recursive, classes);
            }
        } else {
            //防止出现空文件夹情况
            Set<String> iSet = FOLDER_RECORDER.get();
            if (Assert.isNotNull(iSet)) {
                String relativePath = iSet.toArray(converter)[0];
                iSet.remove(relativePath);
                findAndAddClassesInPackageByFile(key + relativePath.replace("/", "."),
                        value + relativePath, recursive, classes);
            }
        }
    }
}
