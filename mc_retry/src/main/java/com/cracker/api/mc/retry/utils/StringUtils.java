package com.cracker.api.mc.retry.utils;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-18
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public final class StringUtils {

    private static final Pattern INT_PATTERN = Pattern.compile("^\\d+$");

    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String... strings) {
        if (strings == null) {
            return false;
        } else {

            for (String s : strings) {
                if (isEmpty(s)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String generateUUID() {
        return replace(UUID.randomUUID().toString(), "-", "").toUpperCase();
    }

    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    public static String replace(String text, String repl, String with, int max) {
        if (!isEmpty(text) && !isEmpty(repl) && with != null && max != 0) {
            int start = 0;
            int end = text.indexOf(repl, start);
            if (end == -1) {
                return text;
            } else {
                int replLength = repl.length();
                int increase = with.length() - replLength;
                increase = Math.max(increase, 0);
                increase *= max < 0 ? 16 : (Math.min(max, 64));

                StringBuilder buf;
                for(buf = new StringBuilder(text.length() + increase); end != -1; end = text.indexOf(repl, start)) {
                    buf.append(text, start, end).append(with);
                    start = end + replLength;
                    --max;
                    if (max == 0) {
                        break;
                    }
                }

                buf.append(text.substring(start));
                return buf.toString();
            }
        } else {
            return text;
        }
    }

    public static boolean hasLength(String str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasText(String str) {
        if (hasLength(str)) {
            int strLen = str.length();

            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

        }
        return false;
    }

    public static String format(String format, Object... args) {
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
        return ft.getMessage();
    }

    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        p.print(e.getClass().getName());
        if (e.getMessage() != null) {
            p.print(": " + e.getMessage());
        }

        p.println();

        String ret;
        try {
            e.printStackTrace(p);
            ret = w.toString();
        } finally {
            p.close();
        }

        return ret;
    }

    public static String toString(String msg, Throwable e) {
        StringWriter w = new StringWriter();
        w.write(msg + "\n");
        PrintWriter p = new PrintWriter(w);

        String ret;
        try {
            e.printStackTrace(p);
            ret = w.toString();
        } finally {
            p.close();
        }

        return ret;
    }

    public static String concat(String... strings) {
        if (strings == null) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();

            for (String str : strings) {
                if (str != null) {
                    sb.append(str);
                }
            }

            return sb.toString();
        }
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static boolean isInteger(String str) {
        return str != null && str.length() != 0 && INT_PATTERN.matcher(str).matches();
    }

    public static String toString(Object value) {
        return value == null ? null : value.toString();
    }

    public static String[] splitWithTrim(String spilt, String sequence) {
        if (isEmpty(sequence)) {
            return null;
        } else {
            String[] values = sequence.split(spilt);
            if (values.length != 0) {
                for (int i = 0; i < values.length; ++i) {
                    values[i] = values[i].trim();
                }

            }
            return values;
        }
    }
}
