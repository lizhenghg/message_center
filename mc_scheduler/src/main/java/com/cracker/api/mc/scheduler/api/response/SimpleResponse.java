package com.cracker.api.mc.scheduler.api.response;

import java.io.Serializable;

/**
 * 简单响应类: SimpleResponse
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-13
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class SimpleResponse implements Serializable {

    private boolean result;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return String.format("{\"result\": %s}", this.getResult());
    }
}
