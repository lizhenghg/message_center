package com.cracker.api.mc.common.http;

import com.google.common.util.concurrent.CheckedFuture;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 对外调用HTTP
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    /**
     * get请求，在这里同步处理
     * @param request Http请求简单封装类
     * @param seconds 最大等待时长
     * @return Http响应简单封装类
     */
    public static HttpResponse get(HttpRequest request, int seconds) {

        CheckedFuture<Response, HttpIoException> future = CustomAsyncHttpClient.getInstance()
                .doGet(request.getUrl(), request.getParams(), request.getHeaders(), request.getCookieList(), null);

        try {
            Response response = future.checkedGet(seconds, TimeUnit.SECONDS);
            if (response == null) {
                LOGGER.error("HTTP GET, cannot connect to server, timeout, seconds: {}", seconds);
                throw new RuntimeException("HTTP GET, cannot connect to server, timeout, seconds: " + seconds);
            }
            return new HttpResponse(response);
        } catch (Exception ex) {
            LOGGER.error("HTTP GET, runtime error", ex);
            throw new RuntimeException(ex);
        }

    }


    /**
     * post请求，在这里同步处理
     * @param request Http请求简单封装类
     * @param seconds 最大等待时长
     * @return Http响应简单封装类
     */
    public static HttpResponse post(HttpRequest request, int seconds) {

        CheckedFuture<Response, HttpIoException> future = CustomAsyncHttpClient.getInstance()
                .doPost(request.getUrl(), request.getParams(), request.getBody(), request.getHeaders(), request.getCookieList(), null);

        try {
            Response response = future.checkedGet(seconds, TimeUnit.SECONDS);
            if (response == null) {
                LOGGER.error("HTTP POST, cannot connect to server, timeout, seconds: {}", seconds);
                throw new RuntimeException("HTTP POST, cannot connect to server, timeout, seconds: " + seconds);
            }
            return new HttpResponse(response);
        } catch (Exception ex) {
            LOGGER.error("HTTP POST, runtime error", ex);
            throw new RuntimeException(ex);
        }

    }


    /**
     * put请求，在这里同步处理
     * @param request Http请求简单封装类
     * @param seconds 最大等待时长
     * @return Http响应简单封装类
     */
    public static HttpResponse put(HttpRequest request, int seconds) {

        CheckedFuture<Response, HttpIoException> future = CustomAsyncHttpClient.getInstance()
                .doPut(request.getUrl(), request.getParams(), request.getHeaders(), request.getCookieList(), null);

        try {
            Response response = future.checkedGet(seconds, TimeUnit.SECONDS);
            if (response == null) {
                LOGGER.error("HTTP PUT, cannot connect to server, timeout, seconds: {}", seconds);
                throw new RuntimeException("HTTP PUT, cannot connect to server, timeout, seconds: " + seconds);
            }
            return new HttpResponse(response);
        } catch (Exception ex) {
            LOGGER.error("HTTP PUT, runtime error", ex);
            throw new RuntimeException(ex);
        }
    }


    /**
     * delete请求，在这里同步处理
     * @param request Http请求简单封装类
     * @param seconds 最大等待时长
     * @return Http响应简单封装类
     */
    public static HttpResponse delete(HttpRequest request, int seconds) {

        CheckedFuture<Response, HttpIoException> future = CustomAsyncHttpClient.getInstance()
                .doDelete(request.getUrl(), request.getParams(), request.getHeaders(), request.getCookieList(), null);

        try {
            Response response = future.checkedGet(seconds, TimeUnit.SECONDS);
            if (response == null) {
                LOGGER.error("HTTP DELETE, cannot connect to server, timeout, seconds: {}", seconds);
                throw new RuntimeException("HTTP DELETE, cannot connect to server, timeout, seconds: " + seconds);
            }
            return new HttpResponse(response);
        } catch (Exception ex) {
            LOGGER.error("HTTP DELETE, runtime error", ex);
            throw new RuntimeException(ex);
        }
    }


    public static void shutdown() {
        CustomAsyncHttpClient.getInstance().shutdown();
    }
}
