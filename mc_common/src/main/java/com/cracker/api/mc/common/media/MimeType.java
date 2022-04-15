package com.cracker.api.mc.common.media;


import com.cracker.api.mc.common.hash.HashUtil;
import com.cracker.api.mc.common.validate.Assert;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Media媒介处理父类: MimeType
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public class MimeType implements Comparable<MimeType>, Serializable {

    private static final long serialVersionUID = 4085923477777865903L;

    /**
     * public|protected|default|private
     */
    protected static final String CHARSET = "charset";

    protected static final String WILDCARD_TYPE = "*";

    protected static final String SEPARATORS = "/";

    protected static final int INCISION_NUMBER = 1 << 1;

    /**
     * Media类型, eg: application
     */
    private final String type;

    /**
     * Media子类型, eg: json
     */
    private final String subType;

    /**
     * check参数有效与否的过滤器, bit的set集合
     */
    private static final BitSet TOKEN;

    /**
     * 当前Media(媒介)全部参数容器
     */
    private final Map<String, String> parameters;

    /**
     * 保存当前Media的content-type的编码方式
     */
    private Charset resolvedCharset;

    /**
     * MimeType转化为String所保存的value
     * 由于对外使用的是非单例，而是使用new的方式，所以应该多考虑多次使用问题，缓存问题
     */
    private volatile String toStringValue;

    /**
     * 如下的构造器,
     * 1、拼凑类似: application/json;charset=UTF-8[;aaa=bbb;ccc=ddd;...]之类格式
     * 2、check参数有效与否
     * 3、赋值给相对应的属性
     * @param type media主类型
     */
    public MimeType(String type) {
        this(type, WILDCARD_TYPE);
    }

    public MimeType(String type, String subType) {
        this(type, subType, Collections.emptyMap());
    }

    public MimeType(MimeType other, Charset charset) {
        this(other.getType(), other.getSubType(), charset);
        this.resolvedCharset = charset;
    }

    public MimeType(MimeType other, Map<String, String> parameters) {
        this(other.getType(), other.getSubType(), parameters);
    }

    public MimeType(String type, String subType, Charset charset) {
        this(type, subType, Collections.singletonMap(CHARSET, charset.name()));
    }

    /**
     * 最终执行的构造方法
     * @param type Media媒介类型
     * @param subType Media媒介子类型
     * @param parameters Media全部参数
     */
    public MimeType(String type, String subType, Map<String, String> parameters) {

        Assert.notEmpty(type, "'type' must not be null");
        Assert.notEmpty(subType, "'subType' must not be null");

        checkToken(type);
        checkToken(subType);

        this.type = type.toLowerCase(Locale.ENGLISH);
        this.subType = subType.toLowerCase(Locale.ENGLISH);

        if (Assert.isNotNull(parameters)) {
            Map<String, String> map = new LinkedHashMap<>(
                    HashUtil.getHashCapacity(parameters.size()));
            parameters.forEach((attribute, value) -> {
                this.checkParameters(attribute, value);
                map.put(attribute, value);
            });
            // 赋值给parameters，表示只读权限的Map视图
            this.parameters = Collections.unmodifiableMap(map);
        } else {
            this.parameters = Collections.emptyMap();
        }
    }

    /**
     * check char is valid or Invalid
     * @param token 待check的String
     */
    private void checkToken(String token) {
        for (int i = 0,len=token.length(); i < len; i++) {
            char ch = token.charAt(i);
            if (!TOKEN.get(ch)) {
                throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
            }
        }
    }

    /**
     * check parameters
     * @param attribute 待check的key
     * @param value 待check的value
     */
    protected void checkParameters(String attribute, String value) {

        Assert.notEmpty(attribute, "'attribute' must not be null");
        Assert.notEmpty(value, "'value' must not be null");

        checkToken(attribute);

        if (CHARSET.equals(attribute)) {
            if (this.resolvedCharset == null) {
                this.resolvedCharset = Charset.forName(unQuote(value));
            }
        } else if (!isQuotedString(value)) {
            checkToken(value);
        }
    }


    /**
     * 判断String是否存在单|双引号
     * @param content 待判断的content
     * @return true means yes,false means no
     */
    private boolean isQuotedString(String content) {
        if (content == null
                || content.length() < INCISION_NUMBER) {
            return false;
        }
        return (content.startsWith("\"") && content.endsWith("\"")) || (content.startsWith("'") && content.endsWith("'"));
    }

    /**
     * 切割单|双引号
     * @param content 待切割的content
     * @return 切割后的content
     */
    protected String unQuote(String content) {
        return isQuotedString(content) ? content.substring(1, content.length() - 1) : content;
    }


    // 静态代码块，无论MimeType实例化多少次，只会执行一次该代码块
    static {
        // 变量名参考rfc2616
        BitSet ctl = new BitSet(128);
        for (int i = 0; i <= 31; i++) {
            ctl.set(i);
        }
        ctl.set(127);

        BitSet separators = new BitSet(128);
        separators.set('(');
        separators.set(')');
        separators.set('<');
        separators.set('>');
        separators.set('@');
        separators.set(',');
        separators.set(';');
        separators.set(':');
        separators.set('\\');
        separators.set('\"');
        separators.set('/');
        separators.set('[');
        separators.set(']');
        separators.set('?');
        separators.set('=');
        separators.set('{');
        separators.set('}');
        separators.set(' ');
        separators.set('\t');

        TOKEN = new BitSet(128);
        TOKEN.set(0, 128);
        // 有效范围不包括如下两个
        TOKEN.andNot(ctl);
        TOKEN.andNot(separators);
    }


    public String getType() {
        return type;
    }


    public String getSubType() {
        return subType;
    }

    /**
     * 重写toString方法
     * @return MimeType类的字符串
     */
    @Override
    public String toString() {
        // 一般不直接返回toStringValue这个原始数据
        String value = this.toStringValue;
        if (value == null) {
            // 这里预测字符串数量
            StringBuilder builder = new StringBuilder(1 << 5);
            // 这里要善于使用分解，分解的原因是：
            // 1、可以让继承该类的所有子类都可以直接调用，返回需要的String
            // 2、化繁为简
            appendTo(builder);
            value = builder.toString();
            this.toStringValue = value;
        }
        return value;
    }

    /**
     * 使用protected，表示可以让子类自己去调用
     * @param builder StringBuilder
     */
    protected void appendTo(StringBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("Invalid parameter, StringBuilder is null");
        }
        builder
                .append(this.type)
                .append('/')
                .append(this.subType);
        appendTo(this.parameters, builder);
    }

    private void appendTo(Map<String, String> parameters, StringBuilder builder) {
        if (builder == null || !Assert.isNotNull(parameters)) {
            throw new IllegalArgumentException("Invalid parameter, StringBuilder and Map parameters must not be null");
        }
        parameters.forEach((attribute, value) -> builder
                .append(';')
                .append(attribute)
                .append('=')
                .append(value));
    }


    /**
     * 有这种排序需要的可自行重写该方法
     * @param o 待比较的Object
     * @return 比较判断符
     */
    @Override
    public int compareTo(MimeType o) {
        return 0;
    }
}
