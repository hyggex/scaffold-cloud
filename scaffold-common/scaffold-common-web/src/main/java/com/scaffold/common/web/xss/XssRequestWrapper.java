package com.scaffold.common.web.xss;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * XSS 过滤请求包装器。
 *
 * <p>对请求参数、请求体中的 XSS 攻击脚本进行转义或过滤：</p>
 * <ul>
 *   <li>GET 参数（queryString）</li>
 *   <li>POST Form 参数</li>
 *   <li>JSON 请求体（Content-Type 为 application/json）</li>
 * </ul>
 *
 *  @author 8800
 */
@Slf4j
public class XssRequestWrapper extends HttpServletRequestWrapper {

    /** XSS 过滤模式：转义（默认）或 过滤 */
    private final String xssMode;

    /** 缓存清理后的请求体字节数组 */
    private final byte[] cachedBody;

    public XssRequestWrapper(HttpServletRequest request, String xssMode) {
        super(request);
        this.xssMode = xssMode != null ? xssMode : XssUtils.MODE_ESCAPE;
        // 读取并清理原始请求体，缓存字节数组供后续多次读取
        this.cachedBody = readAndCleanBody(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return clean(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = clean(values[i]);
        }
        return values;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return clean(value);
    }

    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        return clean(queryString);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(
            new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ByteArrayServletInputStream(this.cachedBody);
    }

    /**
     * 获取清理后的请求体内容字符串。
     */
    public String getBody() {
        return new String(this.cachedBody, StandardCharsets.UTF_8);
    }

    /**
     * 读取原始请求体并进行 XSS 清理。
     */
    private byte[] readAndCleanBody(HttpServletRequest request) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();
            String cleaned = clean(body);
            return cleaned.getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("XSS 过滤读取请求体失败: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * 根据过滤模式清理字符串。
     */
    private String clean(String text) {
        if (text == null) {
            return null;
        }
        if (XssUtils.MODE_FILTER.equals(xssMode)) {
            return XssUtils.filter(text);
        }
        // 默认使用转义模式
        return XssUtils.escape(text);
    }

    /**
     * 将 byte[] 包装为 ServletInputStream，供 getInputStream() 返回。
     */
    private static class ByteArrayServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream;

        public ByteArrayServletInputStream(byte[] data) {
            this.inputStream = new ByteArrayInputStream(data);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            throw new UnsupportedOperationException("setReadListener not implemented");
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
