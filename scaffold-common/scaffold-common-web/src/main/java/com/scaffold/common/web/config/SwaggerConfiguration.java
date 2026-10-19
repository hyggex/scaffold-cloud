package com.scaffold.common.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * Swagger/Knife4j API 文档配置占位类。
 *
 * <p>实际的 Knife4j OpenAPI 配置在各个服务模块的 application.yml 中通过 springdoc.knife4j.* 配置。
 * Knife4j 依赖已添加到 scaffold-system 和 scaffold-resource 模块。</p>
 *
 * <p>访问地址：</p>
 * <ul>
 *   <li>Knife4j 增强版 UI：http://host:port/doc.html</li>
 *   <li>原生 Swagger UI：http://host:port/swagger-ui/index.html</li>
 *   <li>OpenAPI JSON：http://host:port/v3/api-docs</li>
 * </ul>
 *
 *  @author 8800
 */
@Slf4j
@AutoConfiguration
public class SwaggerConfiguration {

    public SwaggerConfiguration() {
        log.info("【Knife4j】API 文档配置已就绪，请访问 /doc.html");
    }
}
