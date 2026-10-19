package com.scaffold.common.core.utils.ip;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网络工具类。
 *
 * @author Lion Li
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetUtils {

    private static final String UNKNOWN = "unknown";

    /**
     * 判断是否为 IPv4 地址。
     */
    public static boolean isIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否为内网 IP。
     */
    public static boolean isInnerIP(String ip) {
        if (ip == null || ip.isEmpty()) {
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
        if (ip.startsWith("192.168.")) {
            return true;
        }
        // 127.0.0.1 ~ 127.255.255.255
        if (ip.startsWith("127.")) {
            return true;
        }
        return false;
    }

    /**
     * 获取本机 IP 地址。
     */
    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

    /**
     * 将 IP 地址转换为整数（IPv4 专用）。
     */
    public static long ipToLong(String ip) {
        if (!isIPv4(ip)) {
            return 0;
        }
        String[] parts = ip.split("\\.");
        long result = 0;
        for (String part : parts) {
            result = (result << 8) | (Integer.parseInt(part) & 0xFF);
        }
        return result;
    }

    /**
     * 将整数转换为 IP 地址（IPv4 专用）。
     */
    public static String longToIP(long ipLong) {
        return String.format("%d.%d.%d.%d",
            (ipLong >> 24) & 0xFF,
            (ipLong >> 16) & 0xFF,
            (ipLong >> 8) & 0xFF,
            ipLong & 0xFF);
    }

    /**
     * 获取客户端真实 IP（从请求头中提取）。
     * 依次尝试 X-Forwarded-For、Proxy-Client-IP、WL-Proxy-Client-IP、HTTP_X_FORWARDED_FOR、HTTP_CLIENT_IP。
     */
    public static String getClientIP(String... headers) {
        if (headers != null && headers.length > 0) {
            for (String header : headers) {
                if (StringUtilsIp.isNotBlank(header) && !UNKNOWN.equalsIgnoreCase(header)) {
                    return header;
                }
            }
        }
        return UNKNOWN;
    }
}

/** 内部使用的字符串工具（避免循环依赖） */
class StringUtilsIp {
    static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
