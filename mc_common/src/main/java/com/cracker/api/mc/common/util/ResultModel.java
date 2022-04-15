package com.cracker.api.mc.common.util;

import com.google.gson.Gson;

import java.io.Serializable;


/**
 * 封装数据返回类
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2021-01-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class ResultModel implements Serializable {

    private int retCode;
    private String msg;
    private Object data;

    public ResultModel() {}

    public ResultModel(int retCode, String msg, Object data) {
        this.retCode = retCode;
        this.msg = msg;
        this.data = data;
    }


    @Override
    public String toString() {
        return "{\"retCode\":\"" + this.retCode + "\", \"msg\":\"" + this.msg + "\", \"data\":" + new Gson().toJson(data) + "}";
    }

}
