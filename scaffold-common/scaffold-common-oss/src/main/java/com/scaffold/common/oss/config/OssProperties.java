package com.scaffold.common.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO 对象存储配置属性
 *
 * @author 8800
 */
@Data
@ConfigurationProperties(prefix = "oss.minio")
public class OssProperties {

    /**
     * 访问站点
     * 例如：127.0.0.1:9000
     */
    private String endpoint = "127.0.0.1:9000";

    /**
     * 自定义域名（可选）
     * 例如：oss.example.com
     */
    private String domain;

    /**
     * 存储桶名称
     */
    private String bucketName = "scaffold";

    /**
     * 前缀（上传路径前缀）
     */
    private String prefix = "uploads";

    /**
     * Access Key
     */
    private String accessKey;

    /**
     * Secret Key
     */
    private String secretKey;

    /**
     * 是否 HTTPS（Y/N）
     */
    private String isHttps = "N";

    /**
     * 桶访问策略（0=private, 1=public, 2=custom）
     */
    private String accessPolicy = "1";

    /**
     * 域（区域，MinIO 可不填）
     */
    private String region;
}
