package com.scaffold.common.security.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Security + Sa-Token 认证与鉴权公共配置。
 *
 * <p>所有端点默认需要认证，以下路径除外：</p>
 * <ul>
 *   <li>/auth/login — 登录接口</li>
 *   <li>/error — 错误页</li>
 *   <li>/doc.html — Knife4j 文档</li>
 *   <li>/v3/api-docs/** — OpenAPI 文档</li>
 *   <li>/swagger-ui/** — Swagger UI</li>
 * </ul>
 *
 *  @author 8800
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration implements WebMvcConfigurer {

    /**
     * Sa-Token 拦截器，负责校验登录状态。
     */
    @Bean
    public SaInterceptor saInterceptor() {
        return new SaInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(saInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/auth/login",
                "/auth/logout",
                "/error",
                "/doc.html",
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/webjars/**"
            );
    }

    /**
     * 配置 Spring Security：禁用表单登录，启用无状态会话，
     * 所有请求都支持匿名访问（具体权限由 Sa-Token Controller 拦截器控制）。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/login",
                    "/auth/logout",
                    "/error",
                    "/doc.html",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                .anyRequest().permitAll()
            );
        return http.build();
    }

    /**
     * BCrypt 密码编码器。
     *
     * <p>用于登录时校验密码强度，以及新增/修改用户时对密码进行 BCrypt 哈希存储。</p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
