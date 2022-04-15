package com.cracker.api.mc.common.util;

import com.cracker.api.mc.common.validate.Assert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String操作类
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-15
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public final class StringUtil {


    /**
     * 简单的正则替换
     * @param source 将要替换的String
     * @param regex 替换的正则法则
     * @param replaceWhat 替换为什么String
     * @return 替换后的result
     */
    public static String regex(String source, String regex, String replaceWhat) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        return matcher.replaceAll(replaceWhat);
    }


    /**
     * 请求格式化，最后统一转换为""或者/xxx[/yyy/zzz/...]
     * @param request uri请求
     * @return 格式化后的请求
     */
    public static String formatToRequest(String request) {
        if (!Assert.isEmpty(request)) {
            // 把全部乱七八糟的空格换行符之类的替换掉，再把双正斜替换为单正斜(如果存在)
            if ((request = regex(request, Symbol.SYMBOL_REGEX, Symbol.EMPTY).replaceAll(Symbol.D_SEPARATORS, Symbol.SEPARATORS))
                    .length() <= 1) {
                return Symbol.EMPTY;
            }
            // 最后一个字符为"/",长度大于1的字符串，先去掉最后的字符
            if (request.lastIndexOf(Symbol.SEPARATORS) == request.length() - 1) {
                request = request.substring(0, request.length() - 1);
            }
            // 假如第一个字符不为"/"，添加上去
            if (request.charAt(0) != Symbol.POSITIVELY_CHAR) {
                request = String.format("%s%s", Symbol.SEPARATORS, request);
            }
        }
        return request;
    }

    /**
     * 判断字符串是否为空或者空白
     * @param content 待判断字符串
     * @return boolean
     */
    public static boolean isNullOrBlank(String content) {
        return content == null || content.trim().isEmpty();
    }

}
