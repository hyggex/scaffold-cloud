package com.scaffold.common.redis.utils;

import cn.hutool.core.convert.Convert;
import com.scaffold.common.core.utils.SpringUtils;
import com.scaffold.common.core.utils.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 发号器工具类。
 *
 * <p>基于 Redisson ID 生成器，提供带日期前缀的唯一 ID 生成能力。</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SequenceUtils {

    /** 默认初始值 */
    public static final long DEFAULT_INIT_VALUE = 1L;
    /** 默认步长 */
    public static final long DEFAULT_STEP_VALUE = 1L;
    /** 默认过期时间-天 */
    public static final Duration DEFAULT_EXPIRE_TIME_DAY = Duration.ofDays(1);
    /** 默认过期时间-分钟 */
    public static final Duration DEFAULT_EXPIRE_TIME_MINUTE = Duration.ofMinutes(1);
    /** 默认最小 ID 容量位数（6 位，即至少可生成 999999 个 ID） */
    public static final int DEFAULT_MIN_ID_CAPACITY_BITS = 6;

    private static final RedissonClient REDISSON_CLIENT = SpringUtils.getBean(RedissonClient.class);

    /**
     * 获取 ID 生成器。
     *
     * @param key        业务 key
     * @param expireTime 过期时间
     * @param initValue  ID 初始值
     * @param stepValue  ID 步长
     */
    public static RIdGenerator getIdGenerator(String key, Duration expireTime, long initValue, long stepValue) {
        RIdGenerator idGenerator = REDISSON_CLIENT.getIdGenerator(key);
        initValue = initValue <= 0 ? DEFAULT_INIT_VALUE : initValue;
        stepValue = stepValue <= 0 ? DEFAULT_STEP_VALUE : stepValue;
        idGenerator.tryInit(initValue, stepValue);
        idGenerator.expire(expireTime);
        return idGenerator;
    }

    /**
     * 获取 ID 生成器（默认初始值=1，步长=1）。
     */
    public static RIdGenerator getIdGenerator(String key, Duration expireTime) {
        return getIdGenerator(key, expireTime, DEFAULT_INIT_VALUE, DEFAULT_STEP_VALUE);
    }

    /**
     * 获取指定业务 key 的下一个唯一 ID。
     */
    public static long getNextId(String key, Duration expireTime, long initValue, long stepValue) {
        return getIdGenerator(key, expireTime, initValue, stepValue).nextId();
    }

    /**
     * 获取指定业务 key 的下一个唯一 ID（初始值=1，步长=1）。
     */
    public static long getNextId(String key, Duration expireTime) {
        return getIdGenerator(key, expireTime).nextId();
    }

    /**
     * 获取指定业务 key 的下一个唯一 ID 字符串。
     */
    public static String getNextIdString(String key, Duration expireTime, long initValue, long stepValue) {
        return Convert.toStr(getNextId(key, expireTime, initValue, stepValue));
    }

    /**
     * 获取指定业务 key 的下一个唯一 ID 字符串（初始值=1，步长=1）。
     */
    public static String getNextIdString(String key, Duration expireTime) {
        return Convert.toStr(getNextId(key, expireTime));
    }

    /**
     * 获取指定业务 key 的下一个唯一 ID 字符串，不足位数左补零。
     *
     * @param key        业务 key
     * @param expireTime 过期时间
     * @param width      位数，不足左补零
     */
    public static String getPaddedNextIdString(String key, Duration expireTime, Integer width) {
        return StringUtils.leftPad(getNextIdString(key, expireTime), width, '0');
    }

    /**
     * 获取 prefix + yyyyMMdd 格式的唯一 ID。
     */
    public static String getDateId(String prefix) {
        return getDateId(prefix, true);
    }

    /**
     * 获取 prefix + yyyyMMdd 格式的唯一 ID。
     *
     * @param prefix       业务前缀
     * @param isWithPrefix ID 是否携带业务前缀
     */
    public static String getDateId(String prefix, boolean isWithPrefix) {
        return getDateId(prefix, isWithPrefix, -1);
    }

    /**
     * 获取 prefix + yyyyMMdd 格式的唯一 ID（自动补零，补零长度 = DEFAULT_MIN_ID_CAPACITY_BITS）。
     */
    public static String getPaddedDateId(String prefix, boolean isWithPrefix) {
        return getDateId(prefix, isWithPrefix, DEFAULT_MIN_ID_CAPACITY_BITS);
    }

    /**
     * 获取 prefix + yyyyMMdd 格式的唯一 ID。
     *
     * @param prefix            业务前缀
     * @param isWithPrefix      ID 是否携带前缀
     * @param minIdCapacityBits 最小 ID 容量位数（小于等于 0 表示不补零）
     */
    public static String getDateId(String prefix, boolean isWithPrefix, int minIdCapacityBits) {
        return getDateId(prefix, isWithPrefix, minIdCapacityBits, LocalDate.now());
    }

    /**
     * 获取 prefix + yyyyMMdd 格式的唯一 ID。
     */
    public static String getDateId(String prefix, boolean isWithPrefix, int minIdCapacityBits, LocalDate time) {
        return getDateId(prefix, isWithPrefix, minIdCapacityBits, time, DEFAULT_INIT_VALUE, DEFAULT_STEP_VALUE);
    }

    /**
     * 获取 prefix + yyyyMMdd 格式的唯一 ID。
     */
    public static String getDateId(String prefix, boolean isWithPrefix, int minIdCapacityBits,
                                   LocalDate time, long initValue, long stepValue) {
        String datePart = DateTimeFormatter.ofPattern("yyyyMMdd").format(time);
        long id = getNextId(prefix + datePart, DEFAULT_EXPIRE_TIME_DAY, initValue, stepValue);
        String idStr = Convert.toStr(id);
        if (minIdCapacityBits > 0 && idStr.length() < minIdCapacityBits) {
            idStr = StringUtils.leftPad(idStr, minIdCapacityBits, '0');
        }
        return isWithPrefix ? prefix + datePart + idStr : datePart + idStr;
    }

    /**
     * 获取 prefix + yyyyMMddHHmmss 格式的唯一 ID。
     */
    public static String getDateTimeId(String prefix) {
        return getDateTimeId(prefix, true);
    }

    /**
     * 获取 prefix + yyyyMMddHHmmss 格式的唯一 ID。
     *
     * @param prefix       业务前缀
     * @param isWithPrefix ID 是否携带前缀
     */
    public static String getDateTimeId(String prefix, boolean isWithPrefix) {
        return getDateTimeId(prefix, isWithPrefix, -1);
    }

    /**
     * 获取 prefix + yyyyMMddHHmmss 格式的唯一 ID（自动补零）。
     */
    public static String getPaddedDateTimeId(String prefix, boolean isWithPrefix) {
        return getDateTimeId(prefix, isWithPrefix, DEFAULT_MIN_ID_CAPACITY_BITS);
    }

    /**
     * 获取 prefix + yyyyMMddHHmmss 格式的唯一 ID。
     *
     * @param prefix            业务前缀
     * @param isWithPrefix      ID 是否携带前缀
     * @param minIdCapacityBits 最小 ID 容量位数
     */
    public static String getDateTimeId(String prefix, boolean isWithPrefix, int minIdCapacityBits) {
        return getDateTimeId(prefix, isWithPrefix, minIdCapacityBits, java.time.LocalDateTime.now());
    }

    /**
     * 获取 prefix + yyyyMMddHHmmss 格式的唯一 ID。
     */
    public static String getDateTimeId(String prefix, boolean isWithPrefix, int minIdCapacityBits,
                                       java.time.LocalDateTime dateTime) {
        String datePart = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(dateTime);
        long id = getNextId(prefix + datePart, DEFAULT_EXPIRE_TIME_DAY);
        String idStr = Convert.toStr(id);
        if (minIdCapacityBits > 0 && idStr.length() < minIdCapacityBits) {
            idStr = StringUtils.leftPad(idStr, minIdCapacityBits, '0');
        }
        return isWithPrefix ? prefix + datePart + idStr : datePart + idStr;
    }
}
