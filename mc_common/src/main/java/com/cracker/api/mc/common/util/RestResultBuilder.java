package com.cracker.api.mc.common.util;

/**
 * 封装数据返回对象操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2021-01-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class RestResultBuilder {

    private int retCode;
    private String msg;
    private Object data;


    public static RestResultBuilder builder() {
        return new RestResultBuilder();
    }

    public RestResultBuilder code(int retCode) {
        this.retCode = retCode;
        return this;
    }

    public RestResultBuilder msg(String msg) {
        this.msg = msg;
        return this;
    }

    public RestResultBuilder data(Object data) {
        this.data = data;
        return this;
    }

    public ResultModel build() {
        return new ResultModel(this.retCode, this.msg, this.data);
    }
}
