package com.scaffold.common.core.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream 流处理工具类。
 *
 * @author Lion Li
 */
public class StreamUtils {

    private StreamUtils() {}

    /**
     * 将 Stream 转换为 List。
     */
    public static <T> List<T> toList(Stream<T> s) {
        return s.collect(Collectors.toList());
    }

    /**
     * 将 Stream 转换为 Set。
     */
    public static <T> Set<T> toSet(Stream<T> s) {
        return s.collect(Collectors.toSet());
    }

    /**
     * 将 Stream 元素通过 mapper 转换后收集为 Set。
     */
    public static <T, R> Set<R> toSet(Stream<T> s, Function<T, R> mapper) {
        return s.map(mapper).collect(Collectors.toSet());
    }

    /**
     * 将逗号分隔的字符串转换为 Long 类型的 List。
     */
    public static List<Long> splitToLong(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(StringUtils.SEP))
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }

    /**
     * 将逗号分隔的字符串转换为 String 类型的 List。
     */
    public static List<String> splitToString(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(StringUtils.SEP))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
    }

    /**
     * 根据条件查找第一个匹配的元素。
     */
    public static <T> T findFirst(Stream<T> s, Predicate<T> predicate) {
        return s.filter(predicate).findFirst().orElse(null);
    }

    /**
     * 根据条件查找任意一个匹配的元素。
     */
    public static <T> T findAny(Stream<T> s, Predicate<T> predicate) {
        return s.filter(predicate).findAny().orElse(null);
    }

    /**
     * 将 Stream 转换为指定分隔符分隔的字符串。
     */
    public static <T> String joining(Stream<T> s, String separator) {
        return s.map(String::valueOf).collect(Collectors.joining(separator));
    }

    /**
     * 将 Stream 转换为逗号分隔的字符串。
     */
    public static <T> String joiningWithComma(Stream<T> s) {
        return joining(s, StringUtils.SEP);
    }

    /**
     * 将逗号分隔的字符串转换为 Integer 类型的 List。
     */
    public static List<Integer> splitToInt(String str) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        return Arrays.stream(str.split(StringUtils.SEP))
            .filter(StringUtils::isNotBlank)
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }

    /**
     * 将逗号分隔的字符串转换为 Long 数组。
     */
    public static Long[] toLongArray(String str) {
        if (StringUtils.isBlank(str)) {
            return new Long[0];
        }
        return Arrays.stream(str.split(StringUtils.SEP))
            .filter(StringUtils::isNotBlank)
            .map(Long::parseLong)
            .toArray(Long[]::new);
    }

    /**
     * 判断 Stream 中是否存在任意一个匹配的元素。
     */
    public static <T> boolean matches(Stream<T> s, Predicate<T> predicate) {
        return s.anyMatch(predicate);
    }

    /**
     * 将逗号分隔的字符串转换为 List<Long>。
     */
    public static List<Long> toLongList(String str) {
        return splitToLong(str);
    }
}
