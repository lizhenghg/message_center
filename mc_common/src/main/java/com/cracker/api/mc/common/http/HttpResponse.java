package com.cracker.api.mc.common.http;

import com.ning.http.client.Response;
import com.ning.http.client.cookie.Cookie;
import com.cracker.api.mc.common.validate.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP响应简单封装类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpResponse {

    private final Response response;

    public HttpResponse(Response response) {
        this.response = response;
    }

    /**
     * 获取响应码
     * @return 响应码
     */
    public int getStatusCode() {
        return this.response.getStatusCode();
    }

    /**
     * 获取内容，默认utf-8解码
     * @return 获取的content
     * @throws IOException ioException
     */
    public String getContent() throws IOException {
        return getContent(StandardCharsets.UTF_8.name());
    }

    public String getContent(String charset) throws IOException {
        return this.response.getResponseBody(charset);
    }

    public String getHeader(String headerName) {
        return this.response.getHeader(headerName);
    }

    public List<String> getHeaders(String headerName) {
        return this.response.getHeaders(headerName);
    }

    public Cookies getCookie(String name) {
        List<Cookie> cookies = this.response.getCookies();
        if (Assert.isNotNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return new Cookies(cookie);
                }
            }
        }
        return null;
    }
}