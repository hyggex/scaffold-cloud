package com.scaffold.common.web.xss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * XSS 防护配置属性。
 *
 * <p>通过 application.yml 配置：</p>
 * <pre>
 * scaffold:
 *   security:
 *     xss:
 *       enabled: true
 *       mode: escape          # escape=转义（默认），filter=过滤
 *       exclude-urls:         # 排除路径（ant 风格）
 *         - /auth/**
 *         - /login
 * </pre>
 *
 *  @author 8800
 */
@Data
@ConfigurationProperties(prefix = "scaffold.security.xss")
public class XssProperties {

    /** 是否开启 XSS 防护，默认 true */
    private Boolean enabled = true;

    /**
     * XSS 过滤模式：
     * <ul>
     *   <li>{@code escape}（默认）- HTML 特殊字符转义，适用于普通文本输入</li>
     *   <li>{@code filter} - 移除危险标签和事件属性，适用于富文本输入</li>
     * </ul>
     */
    private String mode = XssUtils.MODE_ESCAPE;

    /** 排除路径列表（ant 风格），命中的路径不进行 XSS 过滤 */
    private List<String> excludeUrls = new ArrayList<>();
}
