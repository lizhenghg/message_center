package com.cracker.api.mc.common.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ning.http.client.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * HTTP请求简单封装类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-11
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class HttpRequest {


    private String url;

    private Map<String, String> headers = Maps.newHashMap();

    private List<Cookie> cookieList = Lists.newArrayList();

    private byte[] body;

    private Map<String, String> params = Maps.newHashMap();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public List<Cookie> getCookieList() {
        return cookieList;
    }

    public void setCookieList(List<Cookie> cookieList) {
        this.cookieList = cookieList;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * 把部分简单的操作放在pojo类中
     * @param key key
     * @param value value
     */
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addCookie(Cookie cookie) {
        this.cookieList.add(cookie);
    }

    public void addParams(String key, String value) {
        this.params.put(key, value);
    }
}