package com.cracker.api.mc.common.codec;

/**
 * 自定义系统内部SystemCode
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-14
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class SystemCode {

    /**
     * 用户ID无效
     */
    public static final String UID_INVAILD = "11000001";

    /**
     * 无效参数
     */
    public static final String PARAMER_INVAILD = "11000002";

    /**
     * HTTP请求方法无效
     */
    public static final String HTTP_METHOD_INVALID = "11000003";

    /**
     * 内部服务异常
     */
    public static final String INTERNAL_SERVER_ERROR = "11000004";

    /**
     * 未经过授权
     */
    public static final String UNAUTHORIZED = "11000005";

    /**
     * 获取的value不能为空
     */
    public static final String VALUE_INVALID = "10000000";

    /**
     * 对象依赖注入失败
     */
    public static final String IOC_ERROR = "11000006";


}
