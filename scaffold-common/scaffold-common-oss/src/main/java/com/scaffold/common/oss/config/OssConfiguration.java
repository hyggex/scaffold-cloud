package com.scaffold.common.oss.config;

import com.scaffold.common.oss.core.MinioOssClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 对象存储配置
 *
 * @author Lion Li
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "oss.minio", name = "enabled", havingValue = "true", matchIfMissing = false)
public class OssConfiguration {

    private final OssProperties ossProperties;

    /**
     * 创建 MinIO 客户端 Bean
     */
    @Bean
    public MinioOssClient minioOssClient() {
        log.info("初始化 MinIO 对象存储客户端，endpoint={}, bucket={}",
            ossProperties.getEndpoint(), ossProperties.getBucketName());
        return new MinioOssClient(ossProperties);
    }
}
