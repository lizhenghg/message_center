package com.cracker.api.mc.http.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.cracker.api.mc.common.codec.SystemCode;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.exception.MethodNotAllowException;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.http.handler.IHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;

/**
 * Http路由容器池，负责管理所有的HttpWorkerHandler和对应的Method的生命周期运动
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpRouterContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRouterContainer.class);

    private static volatile HttpRouterContainer instance;
    private static final Object SYN_OBJECT = new Object();

    /**
     * 一般像容器管理，都应该单例
     * @return HttpRouterContainer
     */
    public static HttpRouterContainer getInstance() {
        if (instance == null) {
            synchronized (SYN_OBJECT) {
                if (instance == null) {
                    instance = new HttpRouterContainer();
                }
            }
        }
        return Objects.requireNonNull(instance);
    }

    private HttpRouterContainer() {}

    /**
     * HttpWorkerHandler对象容器
     */
    private final List<HttpWorkerHandlerEntry> handlerEntries = Lists.newArrayList();

    /**
     * HttpWorkerHandlerMethod对象容器
     */
    private final List<Map<String, List<HttpWorkerHandlerMethodEntry>>> handlerMethodEntries = Lists.newArrayList();

    /**
     * 把请求方式、请求uri和对应的HttpWorkerHandler关联在一起，塞进容器
     * @param method 请求方式
     * @param uri 请求uri
     * @param handler HttpWorkerHandler
     * @throws BaseBusinessException 参照Spring等ioc框架，一旦ioc失败直接抛异常
     */
    public void registerHttpWorkerHandler(String method, String uri, IHandler handler)
            throws BaseBusinessException {
        if (Assert.isNotNull(handlerEntries)) {
            for (HttpWorkerHandlerEntry entry : handlerEntries) {
                if (entry.matchEntry(method, uri)) {
                    LOGGER.error("can not create the HttpWorkerHandler, it has the same method: {} and uri: {}", method, uri);
                    throw new InternalServerException(SystemCode.IOC_ERROR,
                            "can not create the HttpWorkerHandler, it has the same method: " + method + " and uri: " + uri);
                }
            }
        }
        final HttpWorkerHandlerEntry httpWorkerHandlerEntry = new HttpWorkerHandlerEntry(method, uri, handler);
        handlerEntries.add(httpWorkerHandlerEntry);
        LOGGER.info("registerHttpWorkerHandler, succeed register HttpWorkerHandler into HttpRouterContainer, method={}, uri={}, handler={}",
                method, uri, handler.getClass().getName());
    }


    /**
     * 把请求方式、请求uri和对应的HttpWorkerHandlerMethod关联在一起，塞进容器
     * @param method 请求方式
     * @param uri 请求uri
     * @param methodHandler Method
     * @param key 容器寻找HttpWorkerHandlerMethod的key
     * @throws InternalServerException 参照Spring等ioc框架，一旦ioc失败直接抛异常
     * @throws MethodNotAllowException 参照Spring等ioc框架，一旦ioc失败直接抛异常
     */
    public void registerHttpWorkerHandlerMethod(String method, String uri, Method methodHandler, String key)
            throws InternalServerException, MethodNotAllowException {

        Map<String, List<HttpWorkerHandlerMethodEntry>> methodEntryMap = null;

        if (Assert.isNotNull(handlerMethodEntries)) {

            for (Map<String, List<HttpWorkerHandlerMethodEntry>> methodEntry : handlerMethodEntries) {
                if (methodEntry.containsKey(key)) {
                    methodEntryMap = methodEntry;
                    break;
                }
            }
            if (methodEntryMap != null) {
                List<HttpWorkerHandlerMethodEntry> methodEntryList = methodEntryMap.get(key);
                if (Assert.isNotNull(methodEntryList)) {
                    for (HttpWorkerHandlerMethodEntry httpWorkerHandlerMethodEntry : methodEntryList) {
                        if (httpWorkerHandlerMethodEntry.matchEntry(method, uri)) {
                            LOGGER.error("can not create the HttpWorkerHandlerMethod, it has the same method: {} and uri: {}", method, uri);
                            throw new InternalServerException(SystemCode.IOC_ERROR,
                                    "can not create the HttpWorkerHandlerMethod, it has the same method: " + method + " and uri: " + uri);
                        }
                    }
                }
            }
        }
        final HttpWorkerHandlerMethodEntry httpWorkerHandlerMethodEntry = new HttpWorkerHandlerMethodEntry(method, uri, methodHandler);
        Map<String, List<HttpWorkerHandlerMethodEntry>> newMethodEntryMap = (methodEntryMap == null ? Maps.newHashMap() : methodEntryMap);

        if (newMethodEntryMap.containsKey(key)) {
            newMethodEntryMap.get(key).add(httpWorkerHandlerMethodEntry);
        } else {
            newMethodEntryMap.put(key, new ArrayList<HttpWorkerHandlerMethodEntry>() {
                {
                    add(httpWorkerHandlerMethodEntry);
                }
            });
            handlerMethodEntries.add(newMethodEntryMap);
        }

        LOGGER.info("registerHttpWorkerHandlerMethod, succeed register HttpWorkerHandlerMethod into HttpRouterContainer, " +
                        "method={}, uri={}, methodHandler={}, key={}",
                method, uri, methodHandler.getName(), key);
    }

    /**
     * 获取指定的IHandler
     * @param method 请求方式
     * @param uri 请求路径
     * @return IHandler
     */
    public IHandler getHttpWorkerHandler(String method, String uri) {

        if (Assert.isNotNull(handlerEntries)) {
            for (HttpWorkerHandlerEntry entry : handlerEntries) {
                if (entry.matchEntry(method, uri)) {
                    return entry.getHandler();
                }
            }
        }
        return null;
    }

    /**
     * 获取指定的Method
     * @param method 请求方式
     * @param uri 请求路径
     * @param key 容器寻找HttpWorkerHandlerMethod的key
     * @return Method
     */
    public Method getHttpWorkerHandlerMethod(String method, String uri, String key) {

        if (Assert.isNotNull(handlerMethodEntries)) {
            for (Map<String, List<HttpWorkerHandlerMethodEntry>> listMap : handlerMethodEntries) {
                if (listMap.containsKey(key)) {
                    for (HttpWorkerHandlerMethodEntry httpWorkerHandlerMethodEntry : listMap.get(key)) {
                        if (httpWorkerHandlerMethodEntry.matchEntry(method, uri)) {
                            return httpWorkerHandlerMethodEntry.getMethodHandler();
                        }
                    }
                }
            }
        }
        return null;
    }
}
