package com.scaffold.common.core.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类。
 *
 * @author Lion Li
 */
public class DateUtils {

    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String HH_MM_SS = "HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS);

    private DateUtils() {}

    /**
     * 获取当前日期时间字符串（yyyy-MM-dd HH:mm:ss）。
     */
    public static String now() {
        return format(new Date());
    }

    /**
     * 格式化日期为字符串。
     */
    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * 格式化日期为字符串。
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern)
            .format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    /**
     * 格式化 LocalDateTime 为字符串。
     */
    public static String format(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return DATE_TIME_FORMATTER.format(localDateTime);
    }

    /**
     * 格式化 LocalDateTime 为字符串。
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    /**
     * 格式化 LocalDate 为字符串。
     */
    public static String format(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return DATE_FORMATTER.format(localDate);
    }

    /**
     * 解析字符串为 Date。
     */
    public static Date parse(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return Date.from(LocalDateTime.parse(str, DATE_TIME_FORMATTER)
            .atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 解析字符串为 Date。
     */
    public static Date parse(String str, String pattern) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return Date.from(LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern))
            .atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 解析字符串为 LocalDateTime。
     */
    public static LocalDateTime parseLocalDateTime(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return LocalDateTime.parse(str, DATE_TIME_FORMATTER);
    }

    /**
     * 解析字符串为 LocalDateTime。
     */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Date 转 LocalDateTime。
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime 转 Date。
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转 LocalDate。
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * LocalDate 转 Date。
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 判断两个日期是否为同一天。
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        LocalDate d1 = toLocalDate(date1);
        LocalDate d2 = toLocalDate(date2);
        return d1.equals(d2);
    }

    /**
     * 判断两个日期是否为同一时刻。
     */
    public static boolean isSameInstant(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getTime() == date2.getTime();
    }

    /**
     * 增加秒数。
     */
    public static Date addSeconds(Date date, int seconds) {
        if (date == null) {
            return null;
        }
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault())
            .plusSeconds(seconds).toInstant());
    }

    /**
     * 增加天数。
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault())
            .plusDays(days).toInstant());
    }

    /**
     * 获取当天开始时间（00:00:00）。
     */
    public static Date getDayStart(Date date) {
        if (date == null) {
            return null;
        }
        LocalDate localDate = toLocalDate(date);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取当天结束时间（23:59:59）。
     */
    public static Date getDayEnd(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime end = toLocalDate(date).atTime(23, 59, 59);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 判断日期是否在区间内（含边界）。
     */
    public static boolean isBetween(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        if (target == null || start == null || end == null) {
            return false;
        }
        return !target.isBefore(start) && !target.isAfter(end);
    }
}
