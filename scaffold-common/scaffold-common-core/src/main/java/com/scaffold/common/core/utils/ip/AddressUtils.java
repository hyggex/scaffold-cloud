package com.scaffold.common.core.utils.ip;

import cn.hutool.http.HtmlUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.scaffold.common.core.utils.StringUtils;

/**
 * 地址工具类 — 根据 IP 获取地理位置。
 *
 * <p>依赖第三方 IP 库（如 ip2region），若未接入则返回"内网IP"或"未知"。</p>
 *
 * @author Lion Li
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressUtils {

    /** 未知地址标记 */
    public static final String UNKNOWN = "XX XX";
    /** 内网地址标记 */
    public static final String LOCAL_ADDRESS = "内网IP";

    /**
     * 根据 IP 获取真实物理地址。
     *
     * @param ip IP 地址
     * @return 物理地址，查询失败或内网 IP 时返回默认值
     */
    public static String getRealAddressByIP(String ip) {
        // 清洗 HTML 标签
        ip = HtmlUtil.cleanHtmlTag(org.apache.commons.lang3.StringUtils.defaultIfBlank(ip, ""));
        // 内网 IP 不查询
        if (isInnerIP(ip)) {
            return LOCAL_ADDRESS;
        }
        // 未知 IP
        if (StringUtils.isBlank(ip) || UNKNOWN.equals(ip)) {
            return UNKNOWN;
        }
        // 此处依赖第三方 IP 库（如 ip2region），项目接入后替换以下实现
        try {
            // return RegionUtils.getRegion(ip);
            return ip;
        } catch (Exception e) {
            log.warn("IP 地址查询失败: {}, 错误: {}", ip, e.getMessage());
            return UNKNOWN;
        }
    }

    private static boolean isInnerIP(String ip) {
        if (StringUtils.isBlank(ip)) {
            return true;
        }
        // 10.0.0.0 ~ 10.255.255.255
        if (ip.startsWith("10.")) {
            return true;
        }
        // 172.16.0.0 ~ 172.31.255.255
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                try {
                    int second = Integer.parseInt(parts[1]);
                    return second >= 16 && second <= 31;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            }
        }
        // 192.168.0.0 ~ 192.168.255.255
        return ip.startsWith("192.168.");
    }
}
