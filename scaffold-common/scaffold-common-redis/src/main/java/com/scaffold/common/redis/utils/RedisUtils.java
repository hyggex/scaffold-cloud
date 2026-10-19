package com.scaffold.common.redis.utils;

import com.scaffold.common.core.utils.SpringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.redisson.api.*;
import org.redisson.api.options.KeysScanOptions;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Redis 工具类。
 *
 * <p>封装 Redisson 常用操作，包括：缓存、限流、发布订阅、分布式锁、原子操作、ID 生成等。</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    private static final RedissonClient CLIENT = SpringUtils.getBean(RedissonClient.class);

    // ==================== 客户端 ====================

    /** 获取 Redisson 客户端实例 */
    public static RedissonClient getClient() {
        return CLIENT;
    }

    // ==================== 限流 ====================

    /**
     * 限流。
     *
     * @param key          限流 key
     * @param rateType     限流类型（OVERALL 全局 / PER_CLIENT 单客户端）
     * @param rate         速率
     * @param rateInterval 速率间隔（秒）
     * @return 剩余许可数，-1 表示失败
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval) {
        return rateLimiter(key, rateType, rate, rateInterval, 0);
    }

    /**
     * 限流。
     *
     * @param key          限流 key
     * @param rateType     限流类型
     * @param rate         速率
     * @param rateInterval 速率间隔（秒）
     * @param timeout      超时时间（秒）
     * @return 剩余许可数，-1 表示失败
     */
    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval, int timeout) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, rate, Duration.ofSeconds(rateInterval), Duration.ofSeconds(timeout));
        if (rateLimiter.tryAcquire()) {
            return rateLimiter.availablePermits();
        }
        return -1L;
    }

    // ==================== 发布订阅 ====================

    /**
     * 发布消息并自定义处理。
     *
     * @param channelKey 通道 key
     * @param msg        消息内容
     * @param consumer   自定义处理
     */
    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
        consumer.accept(msg);
    }

    /**
     * 发布消息到指定频道。
     *
     * @param channelKey 通道 key
     * @param msg        消息内容
     */
    public static <T> void publish(String channelKey, T msg) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
    }

    /**
     * 订阅通道接收消息。
     *
     * @param channelKey 通道 key
     * @param clazz      消息类型
     * @param consumer   自定义处理
     */
    public static <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    // ==================== Object（对象缓存） ====================

    /**
     * 缓存对象。
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> void setCacheObject(final String key, final T value) {
        setCacheObject(key, value, false);
    }

    /**
     * 缓存对象。
     *
     * @param key       缓存键
     * @param value     缓存值
     * @param isSaveTtl 是否保留 TTL（set 后仍保持原有剩余 TTL）
     */
    public static <T> void setCacheObject(final String key, final T value, final boolean isSaveTtl) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        if (isSaveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception e) {
                long ttl = bucket.remainTimeToLive();
                if (ttl == -1) {
                    bucket.set(value);
                } else {
                    bucket.set(value, Duration.ofMillis(ttl));
                }
            }
        } else {
            bucket.set(value);
        }
    }

    /**
     * 缓存对象并指定过期时间。
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间
     */
    public static <T> void setCacheObject(final String key, final T value, final Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        bucket.set(value, duration);
    }

    /**
     * 不存在则设置（SETNX）。
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间
     * @return true=设置成功，false=key已存在
     */
    public static <T> boolean setObjectIfAbsent(final String key, final T value, final Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.setIfAbsent(value, duration);
    }

    /**
     * 存在则设置（SETXX）。
     *
     * @param key      缓存键
     * @param value    缓存值
     * @param duration 过期时间
     * @return true=设置成功，false=key不存在
     */
    public static <T> boolean setObjectIfExists(final String key, final T value, final Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.setIfExists(value, duration);
    }

    /**
     * 注册对象监听器（Key 空间通知）。
     *
     * @param key      缓存键
     * @param listener 监听器配置
     */
    public static <T> void addObjectListener(final String key, final ObjectListener listener) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        bucket.addListener(listener);
    }

    // ==================== TTL 与过期 ====================

    /**
     * 设置过期时间（秒）。
     *
     * @param key     Redis 键
     * @param timeout 超时时间（秒）
     * @return true=设置成功
     */
    public static boolean expire(final String key, final long timeout) {
        return expire(key, Duration.ofSeconds(timeout));
    }

    /**
     * 设置过期时间。
     *
     * @param key      Redis 键
     * @param duration 超时时间
     * @return true=设置成功
     */
    public static boolean expire(final String key, final Duration duration) {
        RBucket rBucket = CLIENT.getBucket(key);
        return rBucket.expire(duration);
    }

    /**
     * 获取 Key 剩余存活时间。
     *
     * @param key 缓存键
     * @return 剩余毫秒数，-1 表示永不过期，-2 表示 key 不存在
     */
    public static long getTimeToLive(final String key) {
        RBucket<Object> rBucket = CLIENT.getBucket(key);
        return rBucket.remainTimeToLive();
    }

    // ==================== 获取与删除 ====================

    /**
     * 获取缓存对象。
     *
     * @param key 缓存键
     * @return 缓存值，不存在返回 null
     */
    public static <T> T getCacheObject(final String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.get();
    }

    /**
     * 删除单个对象。
     *
     * @param key 缓存键
     * @return true=删除成功
     */
    public static boolean deleteObject(final String key) {
        return CLIENT.getBucket(key).delete();
    }

    /**
     * 批量删除对象。
     *
     * @param collection 多个缓存键
     */
    public static void deleteObject(final Collection collection) {
        RBatch batch = CLIENT.createBatch();
        collection.forEach(t -> batch.getBucket(t.toString()).deleteAsync());
        batch.execute();
    }

    /**
     * 检查缓存对象是否存在。
     *
     * @param key 缓存键
     */
    public static boolean isExistsObject(final String key) {
        return CLIENT.getBucket(key).isExists();
    }

    // ==================== List ====================

    /**
     * 缓存 List 数据。
     *
     * @param key      缓存键
     * @param dataList 待缓存的 List 数据
     */
    public static <T> boolean setCacheList(final String key, final List<T> dataList) {
        RList<T> rList = CLIENT.getList(key);
        return rList.addAll(dataList);
    }

    /**
     * 缓存 List 数据并设置过期时间。
     */
    public static <T> boolean setCacheList(final String key, final List<T> dataList, final Duration duration) {
        deleteObject(key);
        RList<T> rList = CLIENT.getList(key);
        boolean result = rList.addAll(dataList);
        if (result) {
            rList.expire(duration);
        }
        return result;
    }

    /**
     * 追加 List 数据。
     */
    public static <T> boolean addCacheList(final String key, final T data) {
        RList<T> rList = CLIENT.getList(key);
        return rList.add(data);
    }

    /**
     * 注册 List 监听器。
     *
     * @param key      缓存键
     * @param listener 监听器配置
     */
    public static <T> void addListListener(final String key, final ObjectListener listener) {
        RList<T> rList = CLIENT.getList(key);
        rList.addListener(listener);
    }

    /**
     * 获取缓存的 List 对象（全部）。
     */
    public static <T> List<T> getCacheList(final String key) {
        RList<T> rList = CLIENT.getList(key);
        return rList.readAll();
    }

    /**
     * 获取缓存的 List 对象（范围）。
     *
     * @param key  缓存键
     * @param from 起始下标
     * @param to   截止下标
     */
    public static <T> List<T> getCacheListRange(final String key, int from, int to) {
        RList<T> rList = CLIENT.getList(key);
        return rList.range(from, to);
    }

    // ==================== Set ====================

    /**
     * 缓存 Set 数据。
     */
    public static <T> boolean setCacheSet(final String key, final Set<T> dataSet) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.addAll(dataSet);
    }

    /**
     * 追加 Set 数据。
     */
    public static <T> boolean addCacheSet(final String key, final T data) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.add(data);
    }

    /**
     * 注册 Set 监听器。
     */
    public static <T> void addSetListener(final String key, final ObjectListener listener) {
        RSet<T> rSet = CLIENT.getSet(key);
        rSet.addListener(listener);
    }

    /**
     * 获取缓存的 Set 对象。
     */
    public static <T> Set<T> getCacheSet(final String key) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.readAll();
    }

    // ==================== Map ====================

    /**
     * 缓存 Map 数据。
     */
    public static <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> rMap = CLIENT.getMap(key);
            rMap.putAll(dataMap);
        }
    }

    /**
     * 注册 Map 监听器。
     */
    public static <T> void addMapListener(final String key, final ObjectListener listener) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.addListener(listener);
    }

    /**
     * 获取缓存的 Map。
     */
    public static <T> Map<String, T> getCacheMap(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.getAll(rMap.keySet());
    }

    /**
     * 获取缓存 Map 的所有 key。
     */
    public static <T> Set<String> getCacheMapKeySet(final String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.keySet();
    }

    // ==================== Hash ====================

    /**
     * 往 Hash 中存入数据。
     *
     * @param key   Redis 键
     * @param hKey  Hash 键
     * @param value 值
     */
    public static <T> void setCacheMapValue(final String key, final String hKey, final T value) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.put(hKey, value);
    }

    /**
     * 获取 Hash 中的数据。
     *
     * @param key  Redis 键
     * @param hKey Hash 键
     */
    public static <T> T getCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.get(hKey);
    }

    /**
     * 删除 Hash 中的数据。
     *
     * @param key  Redis 键
     * @param hKey Hash 键
     */
    public static <T> T delCacheMapValue(final String key, final String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.remove(hKey);
    }

    /**
     * 批量删除 Hash 中的数据。
     */
    public static <T> void delMultiCacheMapValue(final String key, final Set<String> hKeys) {
        RBatch batch = CLIENT.createBatch();
        RMapAsync<String, T> rMap = batch.getMap(key);
        for (String hKey : hKeys) {
            rMap.removeAsync(hKey);
        }
        batch.execute();
    }

    /**
     * 批量获取 Hash 中的数据。
     */
    public static <K, V> Map<K, V> getMultiCacheMapValue(final String key, final Set<K> hKeys) {
        RMap<K, V> rMap = CLIENT.getMap(key);
        return rMap.getAll(hKeys);
    }

    // ==================== 原子操作 ====================

    /**
     * 设置原子值（Long）。
     */
    public static void setAtomicValue(String key, long value) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        atomic.set(value);
    }

    /**
     * 获取原子值。
     */
    public static long getAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.get();
    }

    /**
     * 递增原子值。
     */
    public static long incrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.incrementAndGet();
    }

    /**
     * 递减原子值。
     */
    public static long decrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.decrementAndGet();
    }

    // ==================== Key 扫描与操作 ====================

    /**
     * 扫描所有匹配前缀的 Key。
     *
     * @param pattern 匹配模式（如 "user:*"）
     * @return Key 列表
     */
    public static Collection<String> keys(final String pattern) {
        return keys(KeysScanOptions.defaults().pattern(pattern).chunkSize(1000));
    }

    /**
     * 通过扫描参数获取 Key 列表。
     *
     * @param keysScanOptions 扫描参数
     */
    public static Collection<String> keys(final KeysScanOptions keysScanOptions) {
        Stream<String> keysStream = CLIENT.getKeys().getKeysStream(keysScanOptions);
        return keysStream.collect(Collectors.toList());
    }

    /**
     * 批量删除匹配前缀的 Key。
     *
     * @param pattern 匹配模式
     */
    public static void deleteKeys(final String pattern) {
        CLIENT.getKeys().deleteByPattern(pattern);
    }

    /**
     * 判断 Key 是否存在。
     *
     * @param key 缓存键
     */
    public static Boolean hasKey(String key) {
        RKeys rKeys = CLIENT.getKeys();
        return rKeys.countExists(key) > 0;
    }
}
