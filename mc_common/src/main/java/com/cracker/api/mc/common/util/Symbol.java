package com.cracker.api.mc.common.util;

/**
 * 逗号句号...等常用符号接口
 * @author lizhg<2486479615@qq.com>
 * <br/>========================================
 * <br/>公司：myself
 * <br/>开发时间：2020-10-12
 * <br/>版本：1.1.0
 * <br/>JDK版本：1.8
 * <br/>========================================
 */
public interface Symbol {

    /**
     * 逗号
     */
    String COMMA = ",";

    /**
     * 句号
     */
    String PERIOD = ".";

    /**
     * 问号
     */
    String QUESTION = "?";

    /**
     * 冒号
     */
    String COLON = ":";

    /**
     * 正斜
     */
    String SEPARATORS = "/";

    /**
     * 空格|换行符之类的正则
     */
    String SYMBOL_REGEX = "\\s*|\t|\r|\n";

    /**
     * 把问号以及后面的所有字符全部删除
     */
    String QUESTION_AFTER_DELETE = "(\\?{1}$)|(\\?.+)";

    /**
     * 长度为0的字符串
     */
    String EMPTY = "";

    /**
     * 双正斜
     */
    String D_SEPARATORS = "//";


    /**
     * 字符'/'所对应的int值
     */
    int POSITIVELY_CHAR = 47;

    /**
     * 正则表达式全数字匹配
     */
    String REGEX_ALL_NUMBER = "\\d+";

    /**
     * 空格字符串
     */
    String BLANK_STRING = " ";
}
