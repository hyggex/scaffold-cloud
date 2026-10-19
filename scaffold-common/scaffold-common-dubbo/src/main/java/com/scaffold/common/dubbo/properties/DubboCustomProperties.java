package com.scaffold.common.dubbo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dubbo 自定义配置属性。
 */
@Data
@ConfigurationProperties(prefix = "dubbo.custom")
public class DubboCustomProperties {

    /**
     * 是否开启请求日志记录
     */
    private Boolean requestLog;

    /**
     * 日志级别
     */
    private String logLevel;
}
