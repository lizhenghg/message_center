package com.cracker.api.mc.http.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cracker.api.mc.common.annotation.HttpMethod;
import com.cracker.api.mc.common.annotation.HttpRouter;
import com.cracker.api.mc.common.classloader.ClassClient;
import com.cracker.api.mc.common.codec.SystemCode;
import com.cracker.api.mc.common.codec.HttpCode;
import com.cracker.api.mc.common.exception.BadRequestException;
import com.cracker.api.mc.common.exception.BaseBusinessException;
import com.cracker.api.mc.common.exception.InternalServerException;
import com.cracker.api.mc.common.util.DateSymbol;
import com.cracker.api.mc.common.util.StringUtil;
import com.cracker.api.mc.common.util.Symbol;
import com.cracker.api.mc.common.validate.Assert;
import com.cracker.api.mc.http.HttpServerBootstrap;
import com.cracker.api.mc.http.context.HttpContext;
import com.cracker.api.mc.http.util.CustomParameter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * HTTP上层架构之: AbstractHttpWorkerHandler
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-15
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public abstract class AbstractHttpWorkerHandler extends AbstractHttpHandler {


    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpWorkerHandler.class);

    private final String CLIENT_VERSION = "UCAP-CLIENT-VERSION";
    private final String APPID = "UCAP-APPID";
    private final String ACCESS_TOKEN = "UCAP-ACCESS-TOKEN";

    private final Map<String, List<String>> hsMethodParams = Maps.newLinkedHashMap();

    private final Gson gson = new Gson();

    /**
     * 把当前调用该方法的HttpWorkerHandler注册到HttpServerBootstrap，实则是把对应的HttpWorkerHandler、Method、uri、parameters保存进HttpRouterContainer容器里
     * 1、使用方法传参的方式，减少HttpServerBootstrap与HTTP上层架构之间的耦合！
     * 2、借助HttpServerBootstrap，把对应的ttpWorkerHandler、Method、uri、parameters保存进HttpRouterContainer容器，实现三者之间的三角调用(详情可查阅mc_http架构图)
     * 3、灵活运用方法传参！
     * @param server Http对外服务类
     * @throws BaseBusinessException 服务器内部异常
     */
    public void registerToServer(HttpServerBootstrap server) throws BaseBusinessException {
        Method[] methods = this.getClass().getDeclaredMethods();
        if (!Assert.isNotNull(methods)) {
            LOGGER.warn("warn: the caller has no method, so we decided not to put it in the container: {}",
                    this.getClass().getName());
            return;
        }
        HttpRouter httpRouter = this.getClass().getAnnotation(HttpRouter.class);
        String baseRouter = httpRouter.route();
        // 遍历全部的Method，整理method和uri
        for (Method method : methods) {
            if (method.isAnnotationPresent(HttpMethod.class)) {
                initHttpRouterContainer(baseRouter, method, server);
                initMethod(baseRouter, method);
            }
        }
    }

    /**
     * 初始化HttpRouter容器
     * @param baseRouter 父路由地址
     * @param method Method
     * @param server HttpServerBootstrap
     * @throws BaseBusinessException 服务器内部异常，直抛
     */
    private void initHttpRouterContainer(String baseRouter, Method method, HttpServerBootstrap server)
            throws BaseBusinessException {

        HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
        baseRouter = StringUtil.formatToRequest(baseRouter);
        String subRouter = StringUtil.formatToRequest(httpMethod.uri());
        String uri = baseRouter + subRouter;
        String mt = httpMethod.method().name();
        // 把IHandler、Method、uri等元素推送到容器
        server.registerHttpWorkerHandler(mt, uri, this);
        server.registerHttpWorkerHandlerMethod(mt, uri, method, this.getClass().getName());
    }


    /**
     * 初始化Method，把Method对应的parameter名称塞进容器
     * @param baseRouter 父路由
     * @param method Method
     * @throws InternalServerException 服务器内部异常，直抛
     */
    private void initMethod(String baseRouter, Method method) throws InternalServerException {

        HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
        baseRouter = StringUtil.formatToRequest(baseRouter);
        String subRouter = StringUtil.formatToRequest(httpMethod.uri());
        String uri = baseRouter + subRouter;

        String funcKey = String.format("%s_%s_%s_%s", method.getName(),
                uri, httpMethod.method().name(), method.getParameterCount());
        try {
            this.hsMethodParams.put(funcKey, ClassClient.forClient().build().getParameterNamesByAsm(method));
        } catch (NotFoundException e) {
            LOGGER.error("initMethod parameter failed: {}", e.getMessage(), e);
            throw new InternalServerException(SystemCode.IOC_ERROR, e.getMessage());
        }
    }

    /**
     * 执行业务方法
     * @param func Method
     * @param ctx HttpContext
     * @throws InvocationTargetException 调用目标异常
     * @throws IllegalAccessException 非法访问异常
     */
    @Override
    public void callFunction(Method func, HttpContext ctx)
            throws InvocationTargetException, IllegalAccessException {

        HttpRequest request = ctx.getRequest();
        Map<String, String> postParamMap = null;

        if (request instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) request;
            ByteBuf content = httpContent.content();

            if (content.isReadable()) {
                String body = content.toString(StandardCharsets.UTF_8);
                // body不为空
                if (!StringUtil.isNullOrBlank(body)) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("get map from json: {}", body);
                    }
                    Type type = new TypeToken<Map<String, String>>() {}.getType();
                    postParamMap = gson.fromJson(body, type);
                }
            }
        }

        String clientIp = (String) request.headers().get("X-Forwarded-For");
        if (StringUtil.isNullOrBlank(clientIp)) {
            InetSocketAddress address = (InetSocketAddress) ctx.getContext().channel().remoteAddress();
            clientIp = address.getAddress().getHostAddress();
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("incoming request! process function={}, method={}, uri={}, remote Address={}, userAgent={}",
                    func.getName(), request.method().toString(), request.uri(), clientIp, request.headers().get("User-Agent"));
        }

        // 分析方法的参数
        String uri = request.uri().replaceAll(Symbol.QUESTION_AFTER_DELETE, Symbol.EMPTY);
        if (uri.contains(Symbol.SEPARATORS)
                && uri.lastIndexOf(Symbol.SEPARATORS) == uri.length() - 1) {
            uri = uri.substring(0, uri.length() - 1);
        }

        String funcKey = String.format("%s_%s_%s_%s", func.getName(), uri, request.method().toString(),
                func.getParameterCount());
        List<String> paramKeyList = this.hsMethodParams.get(funcKey);
        List<Object> paramValueList = Lists.newArrayList();


        if (Assert.isNotNull(paramKeyList)) {
            int i = 0;
            for (Class<?> paramType : func.getParameterTypes()) {
                String paramKey = paramKeyList.get(i++);
                // 先判断是否在uri上
                List<String> uriValues = ctx.getUriAttribute(paramKey);
                String value = uriValues == null ? null : (uriValues.size() > 0 ? uriValues.get(0) : null);
                if (value == null) {
                    if (postParamMap != null) {
                        value = postParamMap.get(paramKey);
                    }
                }
                if (value == null) {
                    LOGGER.warn("WARN: missing parameter: {}, it might occurs unexpected exception", paramKey);
                }
                try {
                    Object ret = parseVal(paramType.getName(), value, ctx);
                    LOGGER.info(String.format("get params: paramName=%s, paramType=%s, inputVal=%s, outVal=%s",
                            paramKey, paramType.getName(), value, ret));
                    paramValueList.add(ret);
                } catch (Exception ex) {
                    LOGGER.error("request parameter invalid! method={}, uri={}, parameter info: name={}, val={}, type={}",
                            request.method().toString(), request.uri(), paramKey, value, paramType.getName());
                    BaseBusinessException badRequest = new BadRequestException(SystemCode.PARAMER_INVAILD,
                            String.format("parameter parse abnormal, request parameter=%s, inputVal=%s, paramType=%s", paramKey, value, paramType.getName()));
                    throw new InvocationTargetException(badRequest);
                }
            }
        }
        // 调用方法
        Object result = func.invoke(this, paramValueList.toArray());
        // 调用成功时的返回值
        int httpCode;

        switch (func.getAnnotation(HttpMethod.class).status()) {
            case CREATED:
                httpCode = HttpCode.CREATED;
                break;
            case ACCEPTED:
                httpCode = HttpCode.ACCEPTED;
                break;
            case NO_CONTENT:
                httpCode = HttpCode.NO_CONTENT;
                break;
            default:
                httpCode = HttpCode.OK;
        }

        // 请求处理成功
        renderJson(ctx, result, httpCode);
    }


    /**
     * 根据key和value，解析出正确的value
     * @param paramType 属性名称，如：String、int。。。
     * @param value 属性值
     * @param ctx Http上下文
     * @return 解析后的正确的value
     * @throws ParseException 解析异常
     */
    private Object parseVal(String paramType, String value, HttpContext ctx)
            throws ParseException {
        Object ret;
        final double db;

        switch (paramType) {
            case "short":
            case "java.lang.Short":
                db = Double.parseDouble(value);
                ret = (short) db;
                break;
            case "int":
            case "java.lang.Integer":
                db = Double.parseDouble(value);
                ret = (int) db;
                break;
            case "long":
            case "java.lang.Long":
                db = Double.parseDouble(value);
                ret = (long) db;
                break;
            case "float":
            case "java.lang.Float":
                db = Double.parseDouble(value);
                ret = (float) db;
                break;
            case "double":
            case "java.lang.Double":
                ret = Double.parseDouble(value);
                break;
            case "boolean":
            case "java.lang.Boolean":
                ret = Boolean.getBoolean(value);
                break;
            case "Date":
            case "java.util.Date":
                if (value.matches(DateSymbol.DT_REGEX)) {
                    ret = DT_FORMAT.get().parse(value);
                } else {
                    ret = DT_TIME_FORMAT.get().parse(value);
                }
                break;
            case "com.cracker.api.mc.http.util.CustomParameter":
                ret = getCustomParameter(ctx);
                break;
            case "com.cracker.api.mc.http.context.HttpContext":
                ret = ctx;
                break;
            default:
                ret = value;
                break;
        }
        return ret;
    }



    /**
     *
     * 获取当前请求中的自定义头参数
     *
     * @param ctx HTTP上下文
     * @return CustomParameter
     */
    protected CustomParameter getCustomParameter(HttpContext ctx) {
        CustomParameter head = new CustomParameter();
        head.setClientVersion((String) ctx.getRequest().headers().get(CLIENT_VERSION));
        head.setAppId((String) ctx.getRequest().headers().get(APPID) );
        head.setAccessToken((String) ctx.getRequest().headers().get(ACCESS_TOKEN));
        head.setUri(ctx.getRequest().uri());
        return head;
    }


    private static final ThreadLocal<DateFormat> DT_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat(DateSymbol.DT_FORMAT));


    private static final ThreadLocal<DateFormat> DT_TIME_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DateSymbol.DT_TIME_FORMAT);
        }
    };
}