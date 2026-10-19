package com.scaffold.common.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * OpenAPI 3 (SpringDoc) 全局配置。
 *
 * <p>访问地址：</p>
 * <ul>
 *   <li>Knife4j UI: http://host:port/doc.html</li>
 *   <li>Swagger UI: http://host:port/swagger-ui/index.html</li>
 *   <li>OpenAPI JSON: http://host:port/v3/api-docs</li>
 * </ul>
 *
 * @author scaffold
 */
@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
public class OpenApi3Configuration {

    /**
     * 默认 API 文档分组。
     */
    @Bean
    public GroupedOpenApi defaultApi() {
        return GroupedOpenApi.builder()
            .group("Default API")
            .pathsToMatch("/**")
            .build();
    }

    /**
     * 全局 OpenAPI 配置。
     */
    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();

        // API 信息
        openAPI.info(new Info()
            .title("Scaffold API Document")
            .description("Scaffold Microservice Framework API Documentation, Based on OpenAPI 3.0")
            .version("1.0.0")
            .contact(new Contact()
                .name("scaffold")
                .url("https://github.com/example/scaffold"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")));

        // 全局安全方案（Authorization Token）
        openAPI.addSecurityItem(new SecurityRequirement()
            .addList("Authorization"));

        openAPI.components(new Components()
            .addSecuritySchemes("Authorization", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Please input Token, format: Bearer {token}")));

        return openAPI;
    }
}
