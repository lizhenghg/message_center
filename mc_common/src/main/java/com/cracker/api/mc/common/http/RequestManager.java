package com.cracker.api.mc.common.http;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.cracker.api.mc.common.media.HttpHeaderName;
import com.cracker.api.mc.common.media.MimeType;
import com.cracker.api.mc.common.media.MimeTypeSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import java.util.Map;

/**
 * HTTP对外提供类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-11
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RequestManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestManager.class);

    public static final int DEFAULT_REQUEST_TIMEOUT_SECOND = 30;
    private static int requestTimeoutSecond = DEFAULT_REQUEST_TIMEOUT_SECOND;

    private RequestManager() {}
    private static final Gson GSON = new Gson();


    private static final int HTTP_CODE_OFFSET = 300;
    /**
     * post请求
     * @param url 请求url
     * @param entity 请求实体
     * @return 请求成功与否
     */
    public static boolean postMessage(String url, PushEntity entity) {
        LOGGER.info("prepare to post message, url: {}, message: {}", url, entity);
        HttpRequest request = new HttpRequest();

        String bodyStr = null;
        byte[] body;

        try {
            bodyStr = GSON.toJson(entity);
            body = bodyStr.getBytes(StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("RequestManager postMessage, UnsupportedEncodingException", e);
            return false;
        } catch (Exception ex) {
            LOGGER.error("RequestManager postMessage, fail to get json from PushEntity: {}", bodyStr);
            return false;
        }

        request.setUrl(url);
        request.setBody(body);

        Map<String, String> headers = Maps.newHashMap();

        MimeType mimeType = HttpHeaderName.MediaType.parseMimeType(HttpHeaderName.APPLICATION_JSON_VALUE);
        HttpHeaderName.MediaType mediaType = new HttpHeaderName.MediaType(mimeType.getType(), mimeType.getSubType(), StandardCharsets.UTF_8);

        headers.put(MimeTypeSymbol.CONTENT_TYPE, mediaType.toString());

        request.setHeaders(headers);

        boolean ret =  false;
        try {
            HttpResponse response = HttpClient.post(request, requestTimeoutSecond);
            String content = response.getContent();
            int statusCode = response.getStatusCode();
            LOGGER.info("get response successfully! url: {}, sendMessage: {}, receiveContent: {}, receiveStatusCode: {}", url, bodyStr, content, statusCode);

            if (statusCode > HTTP_CODE_OFFSET) {
                LOGGER.error("httpCode is wrong, url: {}, sendMessage: {}, receiveStatusCode: {}", url, bodyStr, statusCode);
            } else {
                LOGGER.info("post message successfully! url: {}, receiveContent: {}", url, content);
                ret = true;
            }
            return ret;
        } catch (Exception ex) {
            // 内部服务异常
            LOGGER.error(String.format("fail to request, url: %s, message: %s", url, bodyStr), ex);
        }
        return false;
    }
}
