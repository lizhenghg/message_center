package com.cracker.api.mc.common.hash;


/**
 * 针对hash散列表的操作工具类：HashUtil，适合使用数组+链表的hash底层数据结构，比如：HashMap、LinkedHashMap、HashSet.针对jdk1.8有效
 * 很适合对java哈希集合进行操作时对初始化容器值，适合的初始化容器值，可以大大减少Hash表的扩容次数，大大优化内存和性能
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class HashUtil {

    /**
     * 最大Hash桶容量，当隐式指定较高的值时使用
     * 通过任何一个带参数的构造函数
     * 必须是2的幂<= 1<<30
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * default最小的判断启动值
     */
    static final int DEFAULT_JUDGE_VALUE = 1 << 2;


    /**
     * 默认初始容量-必须是2的幂
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;


    /**
     * 最小Hash桶容量
     */
    static final int MINIMUM_CAPACITY = 10;

    /**
     * 负载因子
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 对容量进行hash高位运算
     * @param cap cap
     * @return int
     */
    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * 针对将put进哈希表的数据的size，进行最精确的阈值判断，确保返回值为当前条件下最适合的
     * @param capacity capacity
     * @return int
     */
    private static int getThreshold(int capacity) {

        for (int n = DEFAULT_JUDGE_VALUE, compareThreshold = tableSizeFor(capacity);;) {
            // 2^n之内，则(2^n * 0.75 + 1)有效
            if (capacity <= compareThreshold) {
                // 判断有效范围
                if (capacity < (int)(compareThreshold * DEFAULT_LOAD_FACTOR) + 1) {
                    return capacity;
                }
                // 不在有效范围，获取下一个阈值中有效范围最小值，即当前阈值 + 1
                return compareThreshold + 1;
            }
            compareThreshold = (1 << ++n);
        }
    }

    /**
     * 对外提供方法，获取最适合的初始化容器值。适合使用HashMap数据结构的java集合：HashMap、LinkedHashMap、HashSet。。。只针对jdk1.8
     * @param initialCapacity 初始化容器size
     * @return int
     */
    public static int getHashCapacity(int initialCapacity) {

        final int defaultInitialCapacity = DEFAULT_INITIAL_CAPACITY;

        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY
                || initialCapacity <= MINIMUM_CAPACITY) {
            return defaultInitialCapacity;
        }
        // 获取阈值
        return getThreshold(initialCapacity);
    }
}
