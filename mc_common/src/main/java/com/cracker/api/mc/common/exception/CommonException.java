package com.cracker.api.mc.common.exception;


/**
 * 公共异常处理类
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class CommonException extends RuntimeException {

    private String message;
    private Throwable cause;

    public CommonException() {
        super();
    }

    public CommonException(String message) {
        this(message, null);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * 打印详细的自定义的异常堆栈信息
     * @return 异常堆栈信息
     */
    public String getStackTraceMessage() {
        if (this.cause == null) {
            throw new IllegalArgumentException("Throwable must not be null");
        }
        final StringBuilder builder = new StringBuilder(128);
        for (StackTraceElement traceElement : cause.getStackTrace()) {
            builder
                    .append("at ")
                    .append(traceElement.getClassName())
                    .append(".")
                    .append(traceElement.getMethodName())
                    .append("(")
                    .append(traceElement.getFileName())
                    .append(":")
                    .append(traceElement.getLineNumber()).append(")\n");
        }
        return builder.toString();
    }
}
