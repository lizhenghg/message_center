package com.cracker.api.mc.common.exception;

import com.cracker.api.mc.common.codec.HttpCode;

/**
 * 业务异常处理类之: BadRequestException
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class BadRequestException extends BaseBusinessException {

    public BadRequestException(String errCode, String errMsg) {
        super(HttpCode.BAD_REQUEST, errCode, errMsg);
    }
}
