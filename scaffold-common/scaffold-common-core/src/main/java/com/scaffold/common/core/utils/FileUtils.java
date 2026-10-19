package com.scaffold.common.core.utils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件处理工具类。
 *
 * <p>提供文件下载、路径安全检查等功能。</p>
 *
 * @author scaffold
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    /**
     * 下载文件到客户端。
     *
     * @param filePath  文件完整路径
     * @param fileName 下载显示的文件名（带扩展名）
     * @param response HTTP 响应
     */
    public static void download(String filePath, String fileName, HttpServletResponse response) {
        try {
            Path path = Paths.get(filePath);
            if (!Files.exists(path) || !Files.isReadable(path)) {
                log.warn("文件不存在或不可读: {}", filePath);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 安全检查：禁止路径遍历
            if (!isSafeFileName(fileName)) {
                log.warn("非法文件名: {}", fileName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

            response.setContentType("application/octet-stream");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition",
                "attachment;filename=" + encodedFileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentLengthLong(Files.size(path));

            try (InputStream in = Files.newInputStream(path);
                 OutputStream out = response.getOutputStream()) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }

            log.info("文件下载成功: {} -> {}", filePath, fileName);

        } catch (IOException e) {
            log.error("文件下载失败: {} -> {}", filePath, e.getMessage(), e);
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 将字节数组以文件形式下载到客户端。
     *
     * @param data      文件字节数据
     * @param fileName  下载显示的文件名
     * @param mimeType  MIME 类型
     * @param response  HTTP 响应
     */
    public static void download(byte[] data, String fileName, String mimeType,
                                HttpServletResponse response) {
        if (data == null || data.length == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (!isSafeFileName(fileName)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

            response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition",
                "attachment;filename=" + encodedFileName);
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentLengthLong(data.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(data);
                out.flush();
            }

        } catch (IOException e) {
            log.error("字节数组下载失败: {}", e.getMessage(), e);
            if (!response.isCommitted()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 判断文件名是否安全（防止路径遍历攻击）。
     *
     * @param fileName 文件名
     * @return true 安全，false 危险
     */
    public static boolean isSafeFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        // 禁止包含路径分隔符
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false;
        }
        // 禁止包含特殊字符
        for (char c : fileName.toCharArray()) {
            if (c <= 31 || c == '<' || c == '>' || c == ':' || c == '"'
                || c == '|' || c == '?' || c == '*') {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文件扩展名。
     *
     * @param fileName 文件名
     * @return 扩展名（不含点），如 "xlsx"、"png"
     */
    public static String getExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }

    /**
     * 根据 MIME 类型获取 Content-Type。
     *
     * @param extension 文件扩展名
     * @return MIME 类型字符串
     */
    public static String getMimeType(String extension) {
        if (StringUtils.isBlank(extension)) {
            return "application/octet-stream";
        }
        String ext = extension.toLowerCase();
        return switch (ext) {
            case "pdf"  -> "application/pdf";
            case "txt"  -> "text/plain";
            case "html", "htm" -> "text/html";
            case "css"  -> "text/css";
            case "js"   -> "application/javascript";
            case "json" -> "application/json";
            case "xml"  -> "application/xml";
            case "png"  -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif"  -> "image/gif";
            case "svg"  -> "image/svg+xml";
            case "ico"  -> "image/x-icon";
            case "zip"  -> "application/zip";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "xls"  -> "application/vnd.ms-excel";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc"  -> "application/msword";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "application/octet-stream";
        };
    }
}
