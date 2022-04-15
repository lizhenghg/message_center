package com.cracker.api.mc.http.result;

import com.google.gson.Gson;
import com.cracker.api.mc.common.codec.HttpCode;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.http.context.HttpContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 返回结果之JSON提供类: RenderJson
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-19
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RenderJson extends AbstractResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenderJson.class);

    private String sendData;

    private HttpResponseStatus responseStatus = HttpResponseStatus.OK;

    public RenderJson(Object src) {
        this(src, HttpCode.OK);
    }

    public RenderJson(String jsonString) {
        this.sendData = jsonString;
    }

    public RenderJson(Object src, Type type) {
        this.sendData = new Gson().toJson(src, type);
    }

    public RenderJson(Object src, int httpCode) {
        this.sendData = new Gson().toJson(src);
        this.responseStatus = HttpResponseStatus.valueOf(httpCode);
    }


    @Override
    public void apply(HttpContext ctx) {
        setContentType(ctx.getResponse());
        ctx.getResponse().setStatus(responseStatus);
        // 支持jsonp返回
        List<String> jsonpVal = ctx.getUriAttribute("jsonp");
        String jsonp = (jsonpVal == null ? null : (jsonpVal.size() > 0 ? jsonpVal.get(0) : null));
        if (jsonp == null) {
            List<String> callbackVal = ctx.getUriAttribute("callback");
            jsonp = (callbackVal != null ? (callbackVal.size() > 0 ? callbackVal.get(0) : null) : null);
        }
        if (!Assert.isEmpty(jsonp)) {
            sendData = String.format("%s(%s)", jsonp, sendData);
        }
        LOGGER.info("http-service response info: method={}, uri={}, rtnJson={}",
                ctx.getRequest().method().toString(), ctx.getRequest().uri(), this.sendData);
        ctx.getResponse().content().writeBytes(sendData.getBytes());
        ctx.writeResponse();
    }
}