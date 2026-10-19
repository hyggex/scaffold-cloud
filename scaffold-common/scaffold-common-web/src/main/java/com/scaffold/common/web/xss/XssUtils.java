package com.scaffold.common.web.xss;

import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * XSS 攻击防护工具类。
 *
 * <p>提供 HTML 特殊字符转义和 XSS 脚本过滤两种模式：</p>
 * <ul>
 *   <li>{@link #escape(String)} - 转义 &lt; &gt; &amp; &quot; 等危险字符，适用于 JSON/XML 中的文本输出</li>
 *   <li>{@link #filter(String)} - 彻底移除 script、onerror 等危险标签和属性，适用于富文本场景</li>
 * </ul>
 *
 * @author scaffold
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XssUtils {

    /** XSS 模式：转义模式（默认，用于普通文本） */
    public static final String MODE_ESCAPE = "escape";

    /** XSS 模式：过滤模式（用于可能包含 HTML 的富文本） */
    public static final String MODE_FILTER = "filter";

    /**
     * 对字符串进行 HTML 转义，防止 XSS 攻击。
     *
     * <p>转义规则：
     * <ul>
     *   <li>&lt; → &amp;lt;</li>
     *   <li>&gt; → &amp;gt;</li>
     *   <li>&amp; → &amp;amp;（非标签场景）</li>
     *   <li>&quot; → &amp;quot;</li>
     *   <li>&#39; → &amp;#39;</li>
     * </ul>
     * </p>
     *
     * @param text 原始文本
     * @return 转义后的安全文本，null 或空串原样返回
     */
    public static String escape(String text) {
        if (StrUtil.isBlank(text)) {
            return text;
        }
        return EscapeUtil.escape(text);
    }

    /**
     * 对字符串进行 XSS 脚本过滤，移除危险标签和事件属性。
     *
     * <p>过滤规则：
     * <ul>
     *   <li>移除所有 &lt;script&gt; 标签及其内容</li>
     *   <li>移除所有 HTML 事件属性（如 onerror、onload、onclick）</li>
     *   <li>移除 javascript:、data: 等危险协议</li>
     *   <li>移除 style 中的 expression、javascript 等</li>
     * </ul>
     * </p>
     *
     * <p>适用于用户输入的富文本（如评论、内容），普通文本建议使用 {@link #escape(String)}。</p>
     *
     * @param text 原始文本
     * @return 过滤后的安全文本，null 或空串原样返回
     */
    public static String filter(String text) {
        if (StrUtil.isBlank(text)) {
            return text;
        }
        String filtered = text;

        // 移除 <script>...</script> 及其中内容（不区分大小写）
        filtered = filtered.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        filtered = filtered.replaceAll("(?i)<script[^>]*>", "");
        filtered = filtered.replaceAll("(?i)</script>", "");

        // 移除 <iframe>...</iframe>
        filtered = filtered.replaceAll("(?i)<iframe[^>]*>.*?</iframe>", "");
        filtered = filtered.replaceAll("(?i)<iframe[^>]*>", "");
        filtered = filtered.replaceAll("(?i)</iframe>", "");

        // 移除 <object>、<embed>（Flash 嵌入）
        filtered = filtered.replaceAll("(?i)<object[^>]*>.*?</object>", "");
        filtered = filtered.replaceAll("(?i)<embed[^>]*>", "");
        filtered = filtered.replaceAll("(?i)</object>", "");

        // 移除所有 on* 事件属性（不区分大小写）
        filtered = filtered.replaceAll("(?i)\\s+on\\w+\\s*=\\s*[\"'][^\"']*[\"']", "");
        filtered = filtered.replaceAll("(?i)\\s+on\\w+\\s*=\\s*[^\\s>]+", "");

        // 移除 javascript:、vbscript:、data: 等危险协议
        filtered = filtered.replaceAll("(?i)javascript\\s*:", "");
        filtered = filtered.replaceAll("(?i)vbscript\\s*:", "");
        filtered = filtered.replaceAll("(?i)data\\s*:", "");

        // 移除 expression(...)
        filtered = filtered.replaceAll("(?i)expression\\s*\\(", "");

        // 移除 <style> 中的 expression、javascript
        filtered = filtered.replaceAll("(?i)<style[^>]*>.*?</style>", "");

        // 移除 SVG/XML 中的危险标签
        filtered = filtered.replaceAll("(?i)<svg[^>]*>.*?</svg>", "");
        filtered = filtered.replaceAll("(?i)<xml[^>]*>.*?</xml>", "");

        return filtered;
    }

    /**
     * 判断字符串是否包含潜在的 XSS 风险（仅用于检测，不做处理）。
     *
     * @param text 原始文本
     * @return true 表示存在风险，false 表示安全
     */
    public static boolean containsXssRisk(String text) {
        if (StrUtil.isBlank(text)) {
            return false;
        }
        String lower = text.toLowerCase();
        return lower.contains("<script") ||
               lower.contains("onerror") ||
               lower.contains("onload") ||
               lower.contains("onclick") ||
               lower.contains("javascript:") ||
               lower.contains("expression(");
    }
}
