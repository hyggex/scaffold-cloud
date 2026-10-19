package com.scaffold.common.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Web 公共配置。
 *
 * <p>负责加载公共 Web 组件，例如全局异常处理器。</p>
 */
@Configuration
@ComponentScan("com.scaffold.common.web")
public class WebConfiguration {
}
