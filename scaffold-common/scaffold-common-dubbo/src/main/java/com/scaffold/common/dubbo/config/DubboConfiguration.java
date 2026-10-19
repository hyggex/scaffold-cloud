package com.scaffold.common.dubbo.config;

import com.scaffold.common.core.factory.YmlPropertySourceFactory;
import com.scaffold.common.dubbo.handler.DubboExceptionHandler;
import com.scaffold.common.dubbo.properties.DubboCustomProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Dubbo 配置类。
 */
@AutoConfiguration
@EnableConfigurationProperties(DubboCustomProperties.class)
@PropertySource(value = "classpath:common-dubbo.yml", factory = YmlPropertySourceFactory.class)
public class DubboConfiguration {

    /**
     * Dubbo 自定义 IP 注入（避免 IP 不正确问题）。
     * <p>通过 Spring Cloud InetUtils 找到正确的网卡 IP，设置为 dubbo.ip.to.registry 系统属性，
     * 使 Dubbo 使用正确的 IP 注册到 Nacos。</p>
     */
    @Bean
    public CustomBeanFactoryPostProcessor customBeanFactoryPostProcessor() {
        return new CustomBeanFactoryPostProcessor();
    }

    /**
     * Dubbo 异常处理器。
     */
    @Bean
    public DubboExceptionHandler dubboExceptionHandler() {
        return new DubboExceptionHandler();
    }
}
