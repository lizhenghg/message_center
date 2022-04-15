package com.cracker.api.mc.common.http;

import com.ning.http.client.cookie.Cookie;

/**
 * Cookie简单封装类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-14
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class Cookies {

    private final Cookie cookie;
    private boolean secure;

    public Cookie getCookie() {
        return this.cookie;
    }

    public Cookies(Cookie cookie) {
        this.cookie = cookie;
    }

    public Cookies(String domain, String name, String value,String rawValue, String path,
                  int expires,int maxAge, boolean secure,boolean httpOnly) {
        this.cookie = new Cookie(name, value, rawValue, domain, path,
                expires, maxAge, secure, httpOnly);
        this.secure = secure;
    }

    @Override
    public String toString() {
        if (this.cookie == null) {
            return "Cookie: null";
        }
        return String.format("Cookie: domain=%s, name=%s, value=%s, path=%s, maxAge=%d, secure=%s",
                this.cookie.getDomain(),
                this.cookie.getName(),
                this.cookie.getValue(),
                this.cookie.getPath(),
                this.cookie.getMaxAge(),
                this.secure);
    }
}