package com.scaffold.common.oss.core;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.scaffold.common.oss.config.OssProperties;
import com.scaffold.common.oss.entity.UploadResult;
import com.scaffold.common.oss.exception.OssException;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * MinIO 对象存储客户端（基于 S3 协议，兼容 MinIO）
 * <p>
 * 支持：MinIO、阿里云 OSS（需调整 endpoint 样式）
 * </p>
 *
 * @author 8800
 */
@Slf4j
public class MinioOssClient {

    private static final String PATH_SEPARATOR = "/";

    /**
     * MinIO 域名后缀（用于判断是否为 MinIO）
     */
    private static final String MINIO_DOMAIN_SUFFIX = ".aliyuncs.com";

    private final OssProperties properties;
    private final S3AsyncClient s3AsyncClient;
    private final S3Client s3Client;
    private final S3TransferManager transferManager;
    private final S3Presigner presigner;

    public MinioOssClient(OssProperties properties) {
        this.properties = properties;
        try {
            // 构建 S3 客户端
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                properties.getAccessKey(),
                properties.getSecretKey()
            );

            // MinIO 使用路径样式访问
            boolean forcePathStyle = true;
            // 如果 endpoint 包含阿里云域名后缀，则使用虚拟主机样式
            if (properties.getEndpoint().contains(MINIO_DOMAIN_SUFFIX)) {
                forcePathStyle = false;
            }

            S3Configuration s3Config = S3Configuration.builder()
                .pathStyleAccessEnabled(forcePathStyle)
                .chunkedEncodingEnabled(false)
                .build();

            this.s3AsyncClient = S3AsyncClient.builder()
                .endpointOverride(URI.create(getEndpoint()))
                .region(of())
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(s3Config)
                .httpClient(NettyNioAsyncHttpClient.builder()
                    .connectionTimeout(java.time.Duration.ofSeconds(60))
                    .maxConcurrency(100)
                    .build())
                .build();

            // 同步 S3 Client（用于 getObject、headObject 等简单操作）
            this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(getEndpoint()))
                .region(of())
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(s3Config)
                .build();

            this.transferManager = S3TransferManager.builder()
                .s3Client(this.s3AsyncClient)
                .build();

            this.presigner = S3Presigner.builder()
                .endpointOverride(URI.create(getEndpoint()))
                .region(of())
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(s3Config)
                .build();

        } catch (Exception e) {
            throw new OssException("初始化 MinIO 客户端失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传文件
     *
     * @param file        本地文件
     * @param key         对象键（路径）
     * @param contentType 内容类型
     * @return 上传结果
     */
    public UploadResult upload(File file, String key, String contentType) {
        try {
            FileUpload fileUpload = transferManager.uploadFile(
                x -> x.source(file.toPath())
                    .putObjectRequest(
                        y -> y.bucket(properties.getBucketName())
                            .key(key)
                            .contentType(contentType)
                            .build()
                    )
            );
            CompletedFileUpload result = fileUpload.completionFuture().join();
            return UploadResult.builder()
                .url(getUrl() + PATH_SEPARATOR + key)
                .fileUrl(properties.getBucketName() + PATH_SEPARATOR + key)
                .filename(key)
                .eTag(result.response().eTag())
                .build();
        } catch (Exception e) {
            throw new OssException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传 byte[]
     *
     * @param data        数据
     * @param key         对象键
     * @param contentType 内容类型
     * @return 上传结果
     */
    public UploadResult upload(byte[] data, String key, String contentType) {
        return upload(new ByteArrayInputStream(data), key, data.length, contentType);
    }

    /**
     * 上传 InputStream
     *
     * @param inputStream 输入流
     * @param key         对象键
     * @param length      长度
     * @param contentType 内容类型
     * @return 上传结果
     */
    public UploadResult upload(InputStream inputStream, String key, long length, String contentType) {
        try {
            if (!(inputStream instanceof ByteArrayInputStream)) {
                inputStream = new ByteArrayInputStream(IoUtil.readBytes(inputStream));
            }
            BlockingInputStreamAsyncRequestBody body = BlockingInputStreamAsyncRequestBody.builder()
                .contentLength(length)
                .subscribeTimeout(Duration.ofSeconds(120))
                .build();

            Upload upload = transferManager.upload(
                x -> x.requestBody(body)
                    .putObjectRequest(
                        y -> y.bucket(properties.getBucketName())
                            .key(key)
                            .contentType(contentType)
                            .build()
                    )
            );

            body.writeInputStream(inputStream);
            CompletedUpload result = upload.completionFuture().join();

            return UploadResult.builder()
                .url(getUrl() + PATH_SEPARATOR + key)
                .fileUrl(properties.getBucketName() + PATH_SEPARATOR + key)
                .filename(key)
                .eTag(result.response().eTag())
                .build();
        } catch (OssException e) {
            throw e;
        } catch (Exception e) {
            throw new OssException("文件上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 上传（使用后缀自动生成路径）
     *
     * @param data        数据
     * @param suffix      文件后缀（如 .jpg）
     * @param contentType 内容类型
     * @return 上传结果
     */
    public UploadResult uploadSuffix(byte[] data, String suffix, String contentType) {
        String key = getPath(suffix);
        return upload(data, key, contentType);
    }

    /**
     * 下载文件到本地临时路径
     *
     * @param key 文件对象键
     * @return 临时文件路径
     */
    public Path download(String key) {
        try {
            Path tempFile = Files.createTempFile("oss_download_", "");
            FileDownload downloadFile = transferManager.downloadFile(
                x -> x.destination(tempFile)
                    .getObjectRequest(y -> y.bucket(properties.getBucketName()).key(key).build())
            );
            downloadFile.completionFuture().join();
            return tempFile;
        } catch (Exception e) {
            throw new OssException("文件下载失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文件
     *
     * @param key 文件对象键
     */
    public void delete(String key) {
        try {
            s3AsyncClient.deleteObject(
                DeleteObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build()
            );
        } catch (Exception e) {
            throw new OssException("删除文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成预签名下载 URL（私有桶访问）
     *
     * @param key        对象键
     * @param expireTime 过期时间
     * @return 预签名 URL
     */
    public String createPresignedGetUrl(String key, Duration expireTime) {
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expireTime)
                .getObjectRequest(
                    GetObjectRequest.builder()
                        .bucket(properties.getBucketName())
                        .key(key)
                        .build()
                )
                .build();
            PresignedGetObjectRequest request = presigner.presignGetObject(presignRequest);
            return request.url().toExternalForm();
        } catch (Exception e) {
            throw new OssException("生成预签名 URL 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取对象内容（InputStream）
     *
     * @param key 对象键
     * @return 输入流（使用后需自行关闭）
     */
    public InputStream getObjectContent(String key) {
        try {
            return s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build()
            );
        } catch (Exception e) {
            throw new OssException("获取文件内容失败: " + e.getMessage(), e);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param key 对象键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(key)
                    .build()
            );
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取访问端点
     */
    public String getEndpoint() {
        String protocol = "N".equalsIgnoreCase(properties.getIsHttps()) ? "http://" : "https://";
        return protocol + properties.getEndpoint();
    }

    /**
     * 获取文件访问 URL
     */
    public String getUrl() {
        String domain = properties.getDomain();
        if (domain != null && !domain.isEmpty()) {
            String protocol = "N".equalsIgnoreCase(properties.getIsHttps()) ? "http://" : "https://";
            if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
                domain = protocol + domain;
            }
            return domain + PATH_SEPARATOR + properties.getBucketName();
        }
        return getEndpoint() + PATH_SEPARATOR + properties.getBucketName();
    }

    /**
     * 生成带日期路径的对象键
     *
     * @param suffix 文件后缀（如 .jpg）
     * @return 对象键
     */
    public String getPath(String suffix) {
        String uuid = IdUtil.fastSimpleUUID();
        String datePath = java.time.LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String prefix = properties.getPrefix();
        String path = (prefix != null && !prefix.isEmpty())
            ? prefix + PATH_SEPARATOR + datePath + PATH_SEPARATOR + uuid
            : datePath + PATH_SEPARATOR + uuid;
        return path + suffix;
    }

    /**
     * 获取 Region
     */
    private Region of() {
        String region = properties.getRegion();
        return (region != null && !region.isEmpty())
            ? Region.of(region)
            : Region.US_EAST_1;
    }

    /**
     * 获取桶名称
     */
    public String getBucketName() {
        return properties.getBucketName();
    }

    /**
     * 获取配置前缀
     */
    public String getPrefix() {
        return properties.getPrefix();
    }
}
