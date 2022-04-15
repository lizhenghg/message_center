package com.cracker.api.mc.common.exception;

/**
 * 业务异常处理类: BusinessException
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */

public abstract class BaseBusinessException extends Exception {

    private static final long serialVersionUID = -4632539867780934306L;

    private String errCode;
    private int httpCode;
    public BaseBusinessException (int httpCode, String errCode, String errMsg) {
        super(errMsg);
        setErrCode(errCode);
        setHttpCode(httpCode);
    }
    public String getErrCode() {
        return errCode;
    }
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    public int getHttpCode() {
        return httpCode;
    }
    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }
}
