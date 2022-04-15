package com.cracker.api.mc.common.http;

import com.google.common.base.Function;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.ning.http.client.*;
import com.ning.http.client.cookie.Cookie;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import com.cracker.api.mc.common.validate.Assert;

import java.util.List;
import java.util.Map;

/**
 * 客户端异步http调用
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class CustomAsyncHttpClient {

    private static int http_timeout;
    private static int http_connect_timeout;
    private static boolean http_tcpNoDelay;
    private static boolean http_keepAlive;

    static {
        http_timeout = Integer.parseInt("60000");
        http_connect_timeout = Integer.parseInt("30000");
        http_tcpNoDelay = "true".equals("true");
        http_keepAlive = (1 == 1);
    }

    private AsyncHttpClient client;

    private static CustomAsyncHttpClient instance = new Builder()
            .setCompress(true)
            .setReadTimeout(http_timeout)
            .setConnectTimeout(http_connect_timeout)
            .build();

    private CustomAsyncHttpClient() {}


    /**
     * 获取默认的http客户端
     * @return http客户端
     */
    public static CustomAsyncHttpClient getInstance() {
        return instance;
    }


    /**
     * 一般在容器生命周期结束时调用
     */
    public void shutdown() {
        this.client.close();
    }


    /**
     * 比较经典的构造法
     * http客户端构造类
     */
    static public class Builder {

        int connectTimeout = 5000;
        int readTimeout = 10000;
        int maxConnections = -1;
        int maxConnectionsPerHost = -1;
        boolean compress;
        boolean redirectEnabled;

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public Builder setMaxConnectionsPerHost(int maxConnectionsPerHost) {
            this.maxConnectionsPerHost = maxConnectionsPerHost;
            return this;
        }

        public Builder setCompress(boolean compress) {
            this.compress = compress;
            return this;
        }

        public Builder setRedirectEnabled(boolean redirectEnabled) {
            this.redirectEnabled = redirectEnabled;
            return this;
        }

        /**
         * 通过手工构造AsyncHttpClientConfig，来创建http客户端
         * @param config async http client里面的配置
         * @return http客户端
         */
        public CustomAsyncHttpClient build(AsyncHttpClientConfig config) {
            CustomAsyncHttpClient httpClient = new CustomAsyncHttpClient();
            httpClient.client = new AsyncHttpClient(config);
            return httpClient;
        }

        /**
         * 通过简单的配置来构造http客户端
         * @return http客户端
         */
        public CustomAsyncHttpClient build() {
            CustomAsyncHttpClient httpClient = new CustomAsyncHttpClient();
            AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();

            if (this.connectTimeout > 0) {
                builder = builder.setConnectTimeout(this.connectTimeout);
            }
            if (this.readTimeout > 0) {
                builder = builder.setReadTimeout(this.readTimeout);
            }
            if (this.maxConnectionsPerHost > 0) {
                builder = builder.setMaxConnectionsPerHost(this.maxConnectionsPerHost);
            }
            if (this.maxConnections > 0) {
                builder = builder.setMaxConnections(this.maxConnections);
            }
            builder = builder.setFollowRedirect(this.redirectEnabled).setCompressionEnforced(this.compress);

            if (http_tcpNoDelay) {
                // 开启tcpNoDelay
                NettyAsyncHttpProviderConfig providerConfig = new NettyAsyncHttpProviderConfig();
                providerConfig.addProperty("tcpNoDelay", true);
                builder.setAsyncHttpClientProviderConfig(providerConfig);
            }
            // http_keepAlive: 开启http长连接
            builder.setAllowPoolingConnections(http_keepAlive);

            httpClient.client = new AsyncHttpClient(builder.build());
            return httpClient;
        }
    }

    /**
     * 执行一个异步的get方法
     * @param url 访问url
     * @param params 参数key-value集合
     * @param headers 对象头
     * @param cookies cookie集合
     * @param calculator 签名计算器
     * @return CheckedFuture<Response, HttpIoException>
     */
    public CheckedFuture<Response, HttpIoException> doGet(String url, Map<String, String> params,
                                                          Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator) {
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = this.client.prepareGet(url);
        if (calculator != null) {
            boundRequestBuilder = boundRequestBuilder.setSignatureCalculator(calculator);
        }
        if (Assert.isNotNull(headers)) {
            FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerMap.add(entry.getKey(), entry.getValue());
            }
            boundRequestBuilder = boundRequestBuilder.setHeaders(headerMap);
        }
        if (Assert.isNotNull(cookies)) {
            for (Cookie cookie : cookies) {
                boundRequestBuilder = boundRequestBuilder.addCookie(cookie);
            }
        }
        if (Assert.isNotNull(params)) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                boundRequestBuilder = boundRequestBuilder.addQueryParam(param.getKey(), param.getValue());
            }
        }

        ListenableFuture<Response> future = new ListenableFutureAdapter<>(boundRequestBuilder.execute());

        return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
            @Override
            public HttpIoException apply(Exception input) {
                return new HttpIoException(input);
            }
        });
    }

    /**
     * 执行一个异步的post方法
     * @param url 访问url
     * @param params 参数key-value集合
     * @param body 传递content
     * @param headers 对象头
     * @param cookies cookie集合
     * @param calculator 签名计算器
     * @return CheckedFuture<Response, HttpIoException>
     */
    public CheckedFuture<Response, HttpIoException> doPost(String url, Map<String, String> params, byte[] body,
                                                           Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator) {
        AsyncHttpClient.BoundRequestBuilder boundRequestBuilder = this.client.preparePost(url);
        if (calculator != null) {
            boundRequestBuilder = boundRequestBuilder.setSignatureCalculator(calculator);
        }
        if (Assert.isNotNull(headers)) {
            FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerMap.add(entry.getKey(), entry.getValue());
            }
            boundRequestBuilder = boundRequestBuilder.setHeaders(headerMap);
        }
        if (Assert.isNotNull(body)) {
            boundRequestBuilder = boundRequestBuilder.setBody(body);
        } else if (Assert.isNotNull(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                boundRequestBuilder = boundRequestBuilder.addFormParam(entry.getKey(), entry.getValue());
            }
        }

        if (Assert.isNotNull(cookies)) {
            for (Cookie cookie : cookies) {
                boundRequestBuilder = boundRequestBuilder.addCookie(cookie);
            }
        }

        ListenableFuture<Response> future = new ListenableFutureAdapter<>(boundRequestBuilder.execute());

        return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
            @Override
            public HttpIoException apply(Exception input) {
                return new HttpIoException(input);
            }
        });
    }


    /**
     * 执行一个异步的put方法
     * @param url 访问url
     * @param params 参数key-value集合
     * @param headers 对象头
     * @param cookies cookie集合
     * @param calculator 签名计算器
     * @return CheckedFuture<Response, HttpIoException>
     */
    public CheckedFuture<Response, HttpIoException> doPut(String url, Map<String, String> params,
                                                          Map<String, String> headers, List<Cookie> cookies, SignatureCalculator calculator) {

        AsyncHttpClient.BoundRequestBuilder builder = this.client.preparePut(url);
        if (calculator != null) {
            builder.setSignatureCalculator(calculator);
        }
        if (headers != null) {
            FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerMap.add(entry.getKey(), entry.getValue());
            }
            builder = builder.setHeaders(headerMap);
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                builder.addCookie(cookie);
            }
        }
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder = builder.addQueryParam(entry.getKey(), entry.getValue());
            }
        }

        ListenableFuture<Response> future = new ListenableFutureAdapter<>(builder.execute());

        return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
            @Override
            public HttpIoException apply(Exception input) {
                return new HttpIoException(input);
            }
        });
    }

    public CheckedFuture<Response, HttpIoException> doDelete(String url, Map<String, String> params, Map<String, String> headers,
                                                             List<Cookie> cookies, SignatureCalculator calculator) {
        AsyncHttpClient.BoundRequestBuilder builder = this.client.prepareDelete(url);
        if (calculator != null) {
            builder.setSignatureCalculator(calculator);
        }
        if (headers != null) {
            FluentCaseInsensitiveStringsMap headerMap = new FluentCaseInsensitiveStringsMap();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerMap.add(entry.getKey(), entry.getValue());
            }
            builder = builder.setHeaders(headerMap);
        }
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                builder.addCookie(cookie);
            }
        }
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder = builder.addQueryParam(entry.getKey(), entry.getValue());
            }
        }

        ListenableFuture<Response> future = new ListenableFutureAdapter<>(builder.execute());

        return Futures.makeChecked(future, new Function<Exception, HttpIoException>() {
            @Override
            public HttpIoException apply(Exception input) {
                return new HttpIoException(input);
            }
        });
    }
}
