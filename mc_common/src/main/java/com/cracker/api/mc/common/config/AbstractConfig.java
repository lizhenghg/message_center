package com.cracker.api.mc.common.config;

import com.cracker.api.mc.common.classloader.ClassClient;
import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.hash.HashUtil;
import com.cracker.api.mc.common.validate.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 配置文件操作抽象类
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public abstract class AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConfig.class);

    /**
     * 存储配置文件key-value的hash容器。预测hash容器值上限为10。在这里加final，表示该容器只能new一次，不能再进行重新赋值
     */
    protected final Map<String, String> setting = new ConcurrentHashMap<>(HashUtil.getHashCapacity(10));

    protected final int iDefaultValue = -100;

    protected final boolean bDefaultValue = false;

    private String filePath;

    /**
     * 防止外部调用无参构造器
     */
    private AbstractConfig() {
    }

    /**
     * 只让子类实例化时才先对自己进行实例化
     * @param filePath 配置文件完整路径
     */
    AbstractConfig(String filePath) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("init config with filePath: {}", filePath);
        }
        this.filePath = filePath;
        init(filePath);
    }

    /**
     * 加锁进行读取，原则上读操作，即便在多线程场景也完全不用加锁，这里可加可不加。看使用频繁度决定是否加。这里使用频率极低
     * @param filePath 资源dir
     */
    private synchronized void init(String filePath) {

        if (Assert.isEmpty(filePath)) {
            return;
        }

        try (InputStream input = ClassClient.forClient().build().getInputStream(filePath)) {
            if (input == null) {
                return;
            }
            Properties properties = new Properties();
            properties.load(input);
            //遍历配置文件加入到Map中进行缓存
            Enumeration<?> propertyNames = properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String key = (String) propertyNames.nextElement();
                String value = properties.getProperty(key);
                setting.put(key, value);
                LOGGER.info("load config: {}, key: {}, value: {}", filePath, key, value);
            }
        } catch (Exception ex) {
            if (ex instanceof FileNotFoundException) {
                throw new CommonException("FileNotFoundException", ex);
            } else {
                throw new CommonException("IOException", ex);
            }
        }
    }

    /**
     * 重新加载配置文件，可动态通知
     */
    public void reload() {
        init(filePath);
    }

    /**
     * 获取String类型的value
     * @param key 待转换的key
     * @return String类型的value
     */
    public String getStringSetting(String key) {
        return this.setting.get(key);
    }

    /**
     * 获取int类型的value，如果该元素不存在，返回-100
     * @param key 待转换的key
     * @return int类型的value
     */
    public int getIntSetting(String key) {
        String value;
        if (Assert.isEmpty(value = this.setting.get(key))) {
            return this.iDefaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new CommonException("value is not int", ex);
        }
    }

    /**
     * 获取Boolean类型的value
     * @param key 待转换的key
     * @return Boolean类型的value
     */
    public boolean getBooleanSetting(String key) {
        String value;
        if (Assert.isEmpty(value = this.setting.get(key))) {
            return this.bDefaultValue;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception exception) {
            throw new CommonException("value is not boolean", exception);
        }
    }

    /**
     * 重置配置参数
     * @param key key
     * @param value value
     */
    public void setSetting(String key, String value) {
        this.setting.put(key, value);
    }

    /**
     * 这个给后代自己实现，父类一调用就抛异常
     * @param key 待转换的key
     * @return long
     */
    public long getLongSetting(String key) {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    /**
     * 这个给后代自己实现
     * @param key 待转换的key
     * @param charset 将要转换的编码方式
     * @return byte[]
     */
    public abstract byte[] getBytesSetting(String key, String charset);
}
