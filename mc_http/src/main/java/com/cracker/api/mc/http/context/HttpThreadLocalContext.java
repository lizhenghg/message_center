package com.cracker.api.mc.http.context;

/**
 * HTTP线程本地变量上下文
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-9-29
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpThreadLocalContext {

    public static ThreadLocal<HttpContext> localContext = new ThreadLocal<>();

    public static HttpContext getContext() {
        return localContext.get();
    }

    public static void setContext(HttpContext context) {
        localContext.set(context);
    }

    public static void remove() {
        localContext.remove();
    }
}
