package com.scaffold.common.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.scaffold.common.core.factory.YmlPropertySourceFactory;
import com.scaffold.common.mybatis.handler.InjectionMetaObjectHandler;
import com.scaffold.common.mybatis.handler.MybatisExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * MyBatis Plus 配置。
 *
 * <p>包含：分页插件、乐观锁、字段自动填充、雪花ID生成、异常处理。</p>
 */
@Slf4j
@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("${mybatis-plus.mapperPackage}")
@PropertySource(value = "classpath:common-mybatis.yml", factory = YmlPropertySourceFactory.class)
public class MybatisPlusConfiguration {

    /**
     * MyBatis Plus 插件拦截器。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        return interceptor;
    }

    /**
     * 分页插件。
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 溢出总页数时自动回拨第一页
        paginationInnerInterceptor.setOverflow(true);
        return paginationInnerInterceptor;
    }

    /**
     * 乐观锁插件。
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 字段自动填充处理器（插入/更新时自动填充公共字段）。
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new InjectionMetaObjectHandler();
    }

    /**
     * 异常处理器。
     */
    @Bean
    public MybatisExceptionHandler mybatisExceptionHandler() {
        return new MybatisExceptionHandler();
    }

    // ========== 雪花ID生成器（基于IP+端口）==========

    @Value("${server.port:8080}")
    private Integer serverPort;

    @Bean
    public DefaultIdentifierGenerator idGenerator() {
        String ip = getRealIp();
        int lastIpSegment = getLastIpSegment(ip);
        int workerId = (lastIpSegment + serverPort) % 32;
        if (workerId < 0) {
            workerId = Math.abs(workerId) % 32;
        }
        log.info("当前节点 IP: {}, 端口: {}, 雪花WorkerID: {}", ip, serverPort, workerId);
        return new DefaultIdentifierGenerator(workerId, 1);
    }

    private String getRealIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取本机IP失败，使用本机地址: {}", e.getMessage());
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    private int getLastIpSegment(String ip) {
        try {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return Integer.parseInt(parts[3]);
            }
        } catch (Exception e) {
            log.warn("解析IP失败: {}", ip, e);
        }
        return serverPort % 32;
    }
}
