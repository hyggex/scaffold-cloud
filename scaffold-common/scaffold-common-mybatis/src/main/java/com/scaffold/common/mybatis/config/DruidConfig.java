package com.scaffold.common.mybatis.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Druid 连接池配置。
 *
 * <p>德鲁伊（Druid）是阿里巴巴开源的数据库连接池：</p>
 * <ul>
 *   <li>监控页面：访问 http://host:port/druid/index.html</li>
 *   <li>监控账号：admin / druid123（建议生产环境修改）</li>
 *   <li>特性：连接复用、慢 SQL 检测、连接泄漏检测、SQL 防火墙</li>
 * </ul>
 *
 * <p>所有连接池参数通过 {@code spring.datasource.druid.*} 配置：</p>
 * <pre>
 * spring:
 *   datasource:
 *     druid:
 *       initial-size: 5        # 初始连接数
 *       min-idle: 5            # 最小空闲
 *       max-active: 20         # 最大活跃
 *       max-wait: 60000        # 最大等待时间（毫秒）
 *       test-while-idle: true # 空闲时检测
 *       validation-query: SELECT 1  # 连接有效性检测
 *       stat-view-servlet:
 *         enabled: true
 *         login-username: admin
 *         login-password: druid123
 *       web-stat-filter:
 *         enabled: true
 * </pre>
 *
 *  @author 8800
 */
@Slf4j
@AutoConfiguration
public class DruidConfig {

    /**
     * 注册 DruidDataSource 为 Primary DataSource。
     *
     * <p>Spring Boot 自动配置会优先使用此 Bean 替代默认的 HikariCP。</p>
     * <p>配置前缀：{@code spring.datasource.druid}</p>
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DruidDataSourceWrapper druidDataSource() {
        log.info("【Druid】初始化 Druid 连接池...");
        DruidDataSourceWrapper wrapper = new DruidDataSourceWrapper();
        return wrapper;
    }
}
