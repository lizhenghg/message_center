package com.cracker.api.mc.common.http;


/**
 * 自定义HttpIoException
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpIoException extends RuntimeException {

    private static final long serialVersionUID = 5456613924966363824L;

    public HttpIoException(Exception ex) {
        super(ex);
    }
}