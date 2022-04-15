package com.cracker.api.mc.common.config;

import com.cracker.api.mc.common.exception.CommonException;
import com.cracker.api.mc.common.validate.Assert;

import java.io.UnsupportedEncodingException;

/**
 * 简单的config适配器类，适配器模式
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ConfigAdapter extends AbstractConfig {


    public ConfigAdapter(String filePath) {
        super(filePath);
    }

    @Override
    public long getLongSetting(String key) {
        String value;
        if (Assert.isEmpty(value = super.setting.get(key))) {
            return super.iDefaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (Exception ex) {
            throw new CommonException("value is not long", ex);
        }
    }


    @Override
    public byte[] getBytesSetting(String key, String charset) {
        String value;
        if (Assert.isEmpty(value = super.setting.get(key))) {
            return null;
        }
        try {
            return value.getBytes(charset);
        } catch (UnsupportedEncodingException ex) {
            throw new CommonException("UnsupportedEncodingException", ex);
        } catch (Exception ex) {
            throw new CommonException("Exception", ex);
        }
    }
}