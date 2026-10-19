package com.scaffold.common.redis.manager;

import com.scaffold.common.redis.utils.RedisUtils;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于 Redisson 的 CacheManager 实现。
 *
 * <p>重写 RedissonSpringCacheManager，支持多参数配置：
 * cacheName#ttl#maxIdleTime#maxSize#local
 * <ul>
 *   <li>ttl：过期时间</li>
 *   <li>maxIdleTime：最大空闲时间（LRU 淘汰）</li>
 *   <li>maxSize：最大条数（LRU 淘汰）</li>
 *   <li>local：启用本地二级缓存（1=启用，0=关闭）</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class PlusSpringCacheManager implements CacheManager {

    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean transactionAware = true;

    private final Map<String, CacheConfig> configMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Cache> instanceMap = new ConcurrentHashMap<>();

    public PlusSpringCacheManager() {}

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

    /** 设置固定缓存名，null 表示动态模式 */
    public void setCacheNames(Collection<String> names) {
        if (names != null) {
            for (String name : names) {
                getCache(name);
            }
            dynamic = false;
        } else {
            dynamic = true;
        }
    }

    public void setConfig(Map<String, ? extends CacheConfig> config) {
        this.configMap.putAll((Map<String, CacheConfig>) config);
    }

    protected CacheConfig createDefaultConfig() {
        return new CacheConfig();
    }

    @Override
    public Cache getCache(String name) {
        // 支持 cacheName#ttl#maxIdleTime#maxSize#local 格式
        String[] array = StringUtils.delimitedListToStringArray(name, "#");
        name = array[0];

        Cache cache = instanceMap.get(name);
        if (cache != null) {
            return cache;
        }
        if (!dynamic) {
            return cache;
        }

        CacheConfig config = configMap.get(name);
        if (config == null) {
            config = createDefaultConfig();
            configMap.put(name, config);
        }

        if (array.length > 1) {
            config.setTTL(DurationStyle.detectAndParse(array[1]).toMillis());
        }
        if (array.length > 2) {
            config.setMaxIdleTime(DurationStyle.detectAndParse(array[2]).toMillis());
        }
        if (array.length > 3) {
            config.setMaxSize(Integer.parseInt(array[3]));
        }
        int local = 1;
        if (array.length > 4) {
            local = Integer.parseInt(array[4]);
        }

        if (config.getMaxIdleTime() == 0 && config.getTTL() == 0 && config.getMaxSize() == 0) {
            return createMap(name, config, local);
        }
        return createMapCache(name, config, local);
    }

    private Cache createMap(String name, CacheConfig config, int local) {
        RMap<Object, Object> map = RedisUtils.getClient().getMap(name);
        Cache cache = new RedissonCache(map, allowNullValues);
        if (local == 1) {
            cache = new CaffeineCacheDecorator(name, cache);
        }
        if (transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }
        Cache old = instanceMap.putIfAbsent(name, cache);
        return old != null ? old : cache;
    }

    private Cache createMapCache(String name, CacheConfig config, int local) {
        RMapCache<Object, Object> map = RedisUtils.getClient().getMapCache(name);
        Cache cache = new RedissonCache(map, config, allowNullValues);
        if (local == 1) {
            cache = new CaffeineCacheDecorator(name, cache);
        }
        if (transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }
        Cache old = instanceMap.putIfAbsent(name, cache);
        if (old != null) {
            return old;
        }
        map.setMaxSize(config.getMaxSize());
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(configMap.keySet());
    }
}
