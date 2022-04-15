package com.cracker.api.mc.common.media;

import com.cracker.api.mc.common.validate.Assert;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Media媒介资源调用接口: HttpHeaderName
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-10
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface HttpHeaderName {


    public static final MediaType ALL = new MediaType("*", "*");
    public static final String ALL_VALUE = "*/*";

    public static final MediaType APPLICATION_ATOM_XML = new MediaType("application", "atom+xml");
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";

    public static final MediaType APPLICATION_CBOR = new MediaType("application", "cbor");
    public static final String APPLICATION_CBOR_VALUE = "application/cbor";

    public static final MediaType APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");
    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";

    static final public MediaType APPLICATION_JSON = new MediaType("application", "json");
    static final public String APPLICATION_JSON_VALUE = "application/json";

    public static final MediaType APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";

    static final public MediaType APPLICATION_PDF = new MediaType("application", "pdf");
    static final public String APPLICATION_PDF_VALUE = "application/pdf";

    static public final MediaType APPLICATION_PROBLEM_XML = new MediaType("application", "problem+xml");
    static public final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";

    public static final MediaType APPLICATION_XML = new MediaType("application", "xml");
    public static final String APPLICATION_XML_VALUE = "application/xml";

    public static final MediaType IMAGE_GIF = new MediaType("image", "gif");
    public static final String IMAGE_GIF_VALUE = "image/gif";

    public static final MediaType IMAGE_JPEG = new MediaType("image", "jpeg");
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";

    final public static MediaType IMAGE_PNG = new MediaType("image", "png");
    final public static String IMAGE_PNG_VALUE = "image/png";

    public static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

    public static final MediaType TEXT_HTML = new MediaType("text", "html");
    public static final String TEXT_HTML_VALUE = "text/html";


    public static final MediaType TEXT_PLAIN = new MediaType("text", "plain");
    public static final String TEXT_PLAIN_VALUE = "text/plain";


    public static final MediaType TEXT_XML = new MediaType("text", "xml");
    public static final String TEXT_XML_VALUE = "text/xml";


    /**
     * 不建议的原因是写死了一种编码方式，不灵活。有可能是application/json;charset=xxx，而这个xxx可表示的值非常多
     */
    @Deprecated
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);
    @Deprecated
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    @Deprecated
    public static final MediaType APPLICATION_FORM_URLENCODED_UTF8 = new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8);
    @Deprecated
    public static final String APPLICATION_FORM_URLENCODED_UTF8_VALUE = "application/x-www-form-urlencoded;charset=UTF-8";


    /**
     * 灵活使用内部类
     */
    public static class MediaType extends MimeType implements Serializable {

        private static final long serialVersionUID = 2069937152339670231L;

        public MediaType(String type) {
            super(type);
        }

        public MediaType(String type, String subType) {
            super(type, subType);
        }

        public MediaType(String type, String subType, Charset charset) {
            super(type, subType, charset);
        }

        /**
         * 根据类似"application/json"获取到MimeType
         * @param resource 待解析的resource uri
         * @return MimeType
         */
        public static MimeType parseMimeType(String resource) {
            Assert.notEmpty(resource, "resource must not be null");
            if (!resource.contains(SEPARATORS)) {
                throw new IllegalArgumentException("parseMimeType, invalid parameter, what you provide must has " + "/");
            }
            String[] parts = resource.split(SEPARATORS);
            if (parts.length != INCISION_NUMBER) {
                throw new IllegalArgumentException("parseMimeType, invalid parameter, what you provide must just has one " + "/");
            }
            return new MimeType(parts[0], parts[1]);
        }
    }
}
