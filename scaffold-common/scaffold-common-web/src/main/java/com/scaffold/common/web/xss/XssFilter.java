package com.scaffold.common.web.xss;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * XSS 攻击过滤器。
 *
 * <p>拦截所有请求，对请求参数和请求体进行 XSS 过滤。
 * 配置项控制开关和排除路径（通过 {@link XssProperties}）。</p>
 *
 * <p>过滤范围：
 * <ul>
 *   <li>QueryString（GET 参数）</li>
 *   <li>Form 参数（POST Form）</li>
 *   <li>JSON 请求体（application/json）</li>
 *   <li>请求头（少量，如 User-Agent）</li>
 * </ul>
 * </p>
 *
 * <p>使用方法：在 {@link WebConfiguration} 中注册此 Filter，并注入 {@link XssProperties} 配置。</p>
 *
 * @author scaffold
 * @see XssRequestWrapper
 * @see XssProperties
 */
@Slf4j
public class XssFilter implements Filter {

    private XssProperties xssProperties;

    /** 无参构造器（Spring 通过后置注入 xssProperties） */
    public XssFilter() {}

    public XssFilter(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    public void setXssProperties(XssProperties xssProperties) {
        this.xssProperties = xssProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. 检查开关
        if (!isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 检查排除路径（ant 风格，如 /api/**）
        String path = httpRequest.getRequestURI();
        if (isExcluded(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. 获取过滤模式（默认 escape）
        String mode = getMode();

        // 4. 包装请求进行 XSS 过滤
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(httpRequest, mode);

        if (log.isDebugEnabled()) {
            log.debug("XSS 过滤生效 - 路径: {}，模式: {}", path, mode);
        }

        chain.doFilter(wrappedRequest, response);
    }

    private boolean isEnabled() {
        return xssProperties != null && xssProperties.getEnabled();
    }

    private boolean isExcluded(String path) {
        if (xssProperties == null || xssProperties.getExcludeUrls() == null) {
            return false;
        }
        for (String pattern : xssProperties.getExcludeUrls()) {
            if (pathMatcher(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private String getMode() {
        if (xssProperties == null) {
            return XssUtils.MODE_ESCAPE;
        }
        return xssProperties.getMode() != null ? xssProperties.getMode() : XssUtils.MODE_ESCAPE;
    }

    /**
     * 简单的 Ant 风格路径匹配（仅支持 ? 和 **）。
     *
     * @param pattern Ant 模式，如 /api/**、/login?
     * @param path    实际路径
     * @return true 匹配
     */
    private boolean pathMatcher(String pattern, String path) {
        if (pattern.equals(path)) {
            return true;
        }
        // 支持 ** 匹配任意字符（包括 /）
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix);
        }
        // 支持 ? 匹配单个字符
        if (pattern.contains("?")) {
            String regex = pattern
                .replace(".", "\\.")
                .replace("?", ".") + "$";
            return path.matches(regex);
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化逻辑可在此处扩展
    }

    @Override
    public void destroy() {
        // 销毁逻辑
    }
}
