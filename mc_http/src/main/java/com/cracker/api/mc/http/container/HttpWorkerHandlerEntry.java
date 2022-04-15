package com.cracker.api.mc.http.container;

import com.cracker.api.mc.common.annotation.HttpMethod;
import com.cracker.api.mc.common.codec.SystemCode;
import com.cracker.api.mc.common.exception.MethodNotAllowException;
import com.cracker.api.mc.http.handler.IHandler;

import java.util.regex.Pattern;

/**
 * HttpWorkerHandler属性所映射的对应实体: HttpWorkerHandlerEntry
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class HttpWorkerHandlerEntry {

    private final Pattern methodPattern;
    private final Pattern uriPattern;

    /**
     * HttpWorkerHandler
     */
    private final IHandler handler;

    /**
     * 有参构造
     * @param method 使用的哪种请求方式
     * @param uri 请求uri
     * @param handler IHandler
     * @throws MethodNotAllowException 业务异常，参数无效或者方法不支持异常
     */
    public HttpWorkerHandlerEntry(String method, String uri, IHandler handler)
            throws MethodNotAllowException {
        if (method == null
                || uri == null
                || handler == null) {
            throw new IllegalArgumentException("Invalid parameter, method is "
                    + (method == null ? "null" : "not null") + ", uri is "
                    + (uri == null ? "null" : "not null")
                    + ", handler is " + (handler == null ? "null" : "not null"));
        }
        if (!(method.equalsIgnoreCase(HttpMethod.Method.GET.name())
                || method.equalsIgnoreCase(HttpMethod.Method.POST.name())
                || method.equalsIgnoreCase(HttpMethod.Method.PUT.name())
                || method.equalsIgnoreCase(HttpMethod.Method.DELETE.name())
                || method.equalsIgnoreCase(HttpMethod.Method.PATCH.name()))) {
            throw new MethodNotAllowException(SystemCode.HTTP_METHOD_INVALID,
                    "method not allowed, it must one of GET| POST| PUT| DELETE| PATCH");
        }
        // 不区分大小写
        this.methodPattern = Pattern.compile(method, Pattern.CASE_INSENSITIVE);
        // 区分大小写且全路径匹配
        this.uriPattern = Pattern.compile(uri, Pattern.MULTILINE);
        this.handler = handler;
    }

    /**
     * 判断即将push进来的元素是否已经存在
     * @param method 使用的哪种请求方式
     * @param uri 请求uri
     * @return true means the element has already existed, false means not
     */
    public boolean matchEntry (String method, String uri) {
        return this.methodPattern.matcher(method).matches() &&
                this.uriPattern.matcher(uri).matches();
    }

    public Pattern getMethodPattern() {
        return this.methodPattern;
    }

    public Pattern getUriPattern() {
        return this.uriPattern;
    }

    public IHandler getHandler() {
        return this.handler;
    }
}