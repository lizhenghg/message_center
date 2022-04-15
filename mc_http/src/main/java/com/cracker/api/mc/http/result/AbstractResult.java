package com.cracker.api.mc.http.result;

import com.cracker.api.mc.common.media.HttpHeaderName;
import com.cracker.api.mc.common.media.MimeType;
import com.cracker.api.mc.http.context.HttpContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;


/**
 * 返回结果抽象类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-19
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractResult {

    /**
     * 自己去扩展，想怎么diy返回就怎么diy。任性
     * @param ctx Http上下文
     */
    public abstract void apply(HttpContext ctx);

    protected void setContentType(FullHttpResponse response) {
        MimeType mimeType = HttpHeaderName.MediaType.parseMimeType(HttpHeaderName.APPLICATION_JSON_VALUE);
        HttpHeaderName.MediaType mediaType = new HttpHeaderName.MediaType(mimeType.getType(), mimeType.getSubType(), StandardCharsets.UTF_8);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mediaType.toString());
    }

    protected String getEncoding() {
        return CharsetUtil.UTF_8.name();
    }
}
