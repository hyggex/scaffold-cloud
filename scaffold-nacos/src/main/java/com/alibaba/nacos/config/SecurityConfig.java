package com.alibaba.nacos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 安全配置。
 *
 * <p>为 /actuator 端点添加 Basic Auth 认证。</p>
 */
@Configuration
public class SecurityConfig {

    @Value("${spring.boot.admin.client.username}")
    private String username;

    @Value("${spring.boot.admin.client.password}")
    private String password;

    @Bean
    public FilterRegistrationBean<ActuatorAuthFilter> actuatorFilterRegistrationBean() {
        FilterRegistrationBean<ActuatorAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ActuatorAuthFilter(username, password));
        registrationBean.addUrlPatterns("/actuator", "/actuator/*");
        return registrationBean;
    }
}
