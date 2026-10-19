package com.scaffold.common.core.utils;

/**
 * 字符串工具类。
 *
 * <p>封装常用字符串判断与处理方法，基于 Apache Commons Lang3。</p>
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    /** 分隔符常量 */
    public static final String SEP = ",";

    /**
     * 判断字符串是否为空白（null、空字符串或仅含空格）。
     */
    public static boolean isBlank(CharSequence cs) {
        return cs == null || isBlank(cs.toString());
    }

    /**
     * 判断字符串是否不为空白。
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * 判断字符串是否为空（null 或空字符串）。
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || isEmpty(cs.toString());
    }

    /**
     * 判断字符串是否不为空。
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * 驼峰转下划线（camelCase -> camel_case）。
     */
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 替换字符串中的每一个匹配项。
     */
    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        return org.apache.commons.lang3.StringUtils.replaceEach(text, searchList, replacementList);
    }

    /**
     * 左补字符至指定长度。
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            str = "";
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }
}
