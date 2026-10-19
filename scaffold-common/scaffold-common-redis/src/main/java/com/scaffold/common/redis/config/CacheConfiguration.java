package com.scaffold.common.redis.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.scaffold.common.redis.manager.PlusSpringCacheManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置。
 *
 * <p>整合 Caffeine 本地一级缓存 + Redis 分布式二级缓存。</p>
 */
@AutoConfiguration
@EnableCaching
public class CacheConfiguration {

    /**
     * Caffeine 本地缓存处理器。
     * 用于二级缓存模式下的本地缓存层。
     */
    @Bean
    public Cache<Object, Object> caffeine() {
        return Caffeine.newBuilder()
            // 最后一次写入后经过固定时间过期
            .expireAfterWrite(30, TimeUnit.SECONDS)
            // 初始缓存空间大小
            .initialCapacity(100)
            // 缓存最大条数
            .maximumSize(1000)
            .build();
    }

    /**
     * 自定义缓存管理器，整合 Spring Cache。
     * 支持多参数配置：cacheNames#ttl#maxIdleTime#maxSize#local
     */
    @Bean
    public CacheManager cacheManager() {
        return new PlusSpringCacheManager();
    }
}
