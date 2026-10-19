package com.scaffold.common.web.config;

import com.scaffold.common.web.xss.XssFilter;
import com.scaffold.common.web.xss.XssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Web 公共配置。
 *
 * <p>负责加载公共 Web 组件：</p>
 * <ul>
 *   <li>全局异常处理器 {@link GlobalExceptionHandler}</li>
 *   <li>XSS 攻击防护过滤器 {@link XssFilter}</li>
 * </ul>
 *
 * @author scaffold
 */
@Configuration
@ComponentScan("com.scaffold.common.web")
@EnableConfigurationProperties(XssProperties.class)
@RequiredArgsConstructor
public class WebConfiguration {

    private final XssProperties xssProperties;

    /**
     * 注册 XSS 过滤器。
     *
     * <p>Filter 顺序：XssFilter → CharacterEncodingFilter → DispatcherServlet</p>
     * <p>排除路径由 {@link XssProperties#excludeUrls} 配置，如 /auth/**、/login</p>
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter(xssProperties));
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        // 尽量靠前，让 XSS 过滤最早执行
        registration.setOrder(1);
        return registration;
    }
}
