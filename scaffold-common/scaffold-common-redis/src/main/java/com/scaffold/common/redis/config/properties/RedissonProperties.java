package com.scaffold.common.redis.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redisson 配置属性。
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    /** Redis Key 前缀 */
    private String keyPrefix;

    /** 线程池数量，默认 = CPU 核心数 * 2 */
    private int threads;

    /** Netty 线程池数量，默认 = CPU 核心数 * 2 */
    private int nettyThreads;

    /** 单机服务配置 */
    private SingleServerConfig singleServerConfig;

    /** 集群服务配置 */
    private ClusterServersConfig clusterServersConfig;

    @Data
    @NoArgsConstructor
    public static class SingleServerConfig {
        /** 客户端名称 */
        private String clientName;
        /** 最小空闲连接数 */
        private int connectionMinimumIdleSize;
        /** 连接池大小 */
        private int connectionPoolSize;
        /** 连接空闲超时（毫秒） */
        private int idleConnectionTimeout;
        /** 命令等待超时（毫秒） */
        private int timeout;
        /** 发布订阅连接池大小 */
        private int subscriptionConnectionPoolSize;
    }

    @Data
    @NoArgsConstructor
    public static class ClusterServersConfig {
        /** 客户端名称 */
        private String clientName;
        /** Master 最小空闲连接数 */
        private int masterConnectionMinimumIdleSize;
        /** Master 连接池大小 */
        private int masterConnectionPoolSize;
        /** Slave 最小空闲连接数 */
        private int slaveConnectionMinimumIdleSize;
        /** Slave 连接池大小 */
        private int slaveConnectionPoolSize;
        /** 连接空闲超时（毫秒） */
        private int idleConnectionTimeout;
        /** 命令等待超时（毫秒） */
        private int timeout;
        /** 发布订阅连接池大小 */
        private int subscriptionConnectionPoolSize;
        /** 读取模式 */
        private ReadMode readMode;
        /** 订阅模式 */
        private SubscriptionMode subscriptionMode;
    }
}
